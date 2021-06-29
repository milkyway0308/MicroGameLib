package skywolf46.microgamelib

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import skywolf46.commandannotation.kotlin.CommandAnnotationCore
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.extrautility.annotations.AllowScanning
import skywolf46.extrautility.util.MinecraftLoader
import skywolf46.extrautility.util.get
import skywolf46.extrautility.util.log
import skywolf46.extrautility.util.removeValue
import skywolf46.microgamelib.annotations.GameConfiguration
import skywolf46.microgamelib.annotations.GameInstance
import skywolf46.microgamelib.annotations.GameStage
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.data.*
import skywolf46.microgamelib.storage.GameInstanceStorage
import skywolf46.microgamelib.storage.InjectorClassManagerStorage
import java.io.File
import java.lang.IllegalStateException

@AllowScanning
class MicroGameLib : JavaPlugin() {

    companion object {
        lateinit var inst: MicroGameLib
            private set
    }

    override fun onEnable() {
        inst = this
        log("§bMicroGameLib - Super lightweight minigame library")
        log("§e-- Starting up..")
        log("§e-- Scanning classes")
        val gameClasses = scanClasses(MinecraftLoader.loadAllClass())
        finalizeInjection()
        prepareConfiguration(gameClasses)
        registerSerializers()
        loadGameConfiguration()
        validateGames()
        checkSingleInstanceGames()
        finalizeAlwaysEnabledGames()
        prepareCommandAnnotationApi()
    }


    private fun scanClasses(classes: List<Class<*>>): List<Class<*>> {
        val gameClasses = mutableListOf<Class<*>>()
        for (x in classes) {
            when {
                x.getAnnotation(InjectTarget::class.java) != null -> {
                    InjectorClassManagerStorage.addInject(x)
                }
                x.getAnnotation(GameInstance::class.java) != null -> {
                    registerGameInstance(x)
                }
                x.getAnnotation(GameStage::class.java) != null -> {
                    registerGameStage(x)
                }
                x.getAnnotation(GameConfiguration::class.java) != null -> {
                    gameClasses.add(x)
                }
            }
        }
        return gameClasses
    }

    private fun registerGameInstance(cls: Class<*>) {
        if (cls.getAnnotation(GameStage::class.java) != null) {
            log("§c--- Inapplicable annotation \"@GameInstance\" and \"@GameStage\" detected in class ${cls.name}. To specify usage, remove collapsing annotation.")
        }
        val gameInstance = cls.getAnnotation(GameInstance::class.java)
        GameInstanceStorage.gameMapData.computeIfAbsent(gameInstance.value) { GameInstanceData(gameInstance.value) }
            .apply {
                isMultiStaged = gameInstance.allowMultiInstance
                alwaysStarted = gameInstance.alwaysStarted
                val stage = GameStageData(this, gameInstance.value, cls)
                gameStageMap[gameInstance.value] = stage
                gameStages.add(stage)
            }
        log("§e--- Game instance §f\"${gameInstance.value}\" §e(${cls.name}, ${if (gameInstance.allowMultiInstance) "Multi-Staged" else "Single"}) registered")
    }

    private fun registerGameStage(cls: Class<*>) {
        val gameInstance = cls.getAnnotation(GameStage::class.java)
        if (gameInstance.gameName == gameInstance.value) {
            log("§c--- Cannot register Game stage §f\"${gameInstance.value}\" (${cls.name}) : Game stage cannot have same name with game")
            return
        }

        GameInstanceStorage.gameMapData.computeIfAbsent(gameInstance.gameName) { GameInstanceData(gameInstance.value) }
            .apply {
                if (gameStageMap.containsKey(gameInstance.value)) {
                    log("§c--- Cannot register Game stage §f\"${gameInstance.value}\" (${cls.name}) : Game stage name duplicated")
                    return
                }
                val stage = GameStageData(this, gameInstance.value, cls)
                gameStageMap[gameInstance.value] = stage
                gameStages.add(stage)
            }
        log("§e--- Game stage §f\"${gameInstance.value}\" §e(Priority ${gameInstance.stagePriority}) for ${gameInstance.gameName} registered")
    }

    private fun finalizeInjection() {
        log("§e-- Finalizing global injection")
        InjectorClassManagerStorage.finalizeGlobalInject()
    }

    private fun prepareConfiguration(gameClasses: List<Class<*>>) {
        log("§e-- Preparing minigame configurations")
        for (x in gameClasses) {
            val config = x.getAnnotation(GameConfiguration::class.java)
            if (GameInstanceStorage.getGameInstanceData(config.value) == null) {
                log("§c--- Cannot register game configuration for \"${config.value}\" : Game not registered")
            } else {
                log("§e--- Registering game configuration for \"${config.value}\"")
                GameInstanceStorage.getGameInstanceData(config.value)!!.gameConfiguration =
                    ConfigurationStructure(x.kotlin)
            }
        }
    }

    private fun loadGameConfiguration() {
        log("§e-- Loading minigame configurations")
        with(File(dataFolder, "Stages")) {
            if (exists()) {
                if (!isDirectory) {
                    println("§c--- Cannot load minigame configurations from directory : Target directory is file")
                    println("§c--- CRITICAL ERROR! Shutdown plugin.")
                    Bukkit.getPluginManager().disablePlugin(this@MicroGameLib)
                    return
                }
                for (x in listFiles()!!) {
                    loadGameFromFile(x)
                }
            } else {
                log("§e-- No minigame configuration detected.")
            }
        }
    }

    private fun loadGameFromFile(file: File) {
        val loaded = YamlConfiguration.loadConfiguration(file)
        val gameInstance = GameInstanceStorage.getGameInstanceData(loaded.getString("Game"))
        gameInstance?.apply {
            if (this.gameConfiguration == null) {
                log("§c-- Cannot load minigame configuration \"${file.name}\" : Configuration class not exists for game ${
                    loaded.getString("Game")
                } ")
                return@apply
            }
            val sector = loaded.getConfigurationSection("Data")
            if (sector == null) {
                log("§c-- Cannot load minigame configuration \"${file.name}\" : \"Data\" Configuration sector not exists in file ")
                return@apply
            }
            log("§e--- Loading minigame stage \"${loaded.getString("Stage")}\" (Game instance: ${loaded.getString("Game")})")
            GameInstanceStorage.gameMap[loaded.getString("Stage")] =
                GameInstanceObject(loaded.getString("Stage"), this, gameConfiguration!!.loadFromSection(sector))
        }
            ?: log("§c-- Cannot load minigame configuration \"${file.name}\" : Minigame ${loaded.getString("Game")} is not registered")
    }

    private fun validateGames() {
        log("§e-- Validating minigame configurations.")
        for ((x, y) in HashMap(GameInstanceStorage.gameMapData)) {
            if (y.gameConfiguration == null) {
                GameInstanceStorage.gameMapData.remove(x)
                log("§c--- Minigame validation failed; No @GameConfiguration class provided to game $x. Disabling game..")
            }
        }
    }

    private fun checkSingleInstanceGames() {
        log("§e-- Adding single instanced minigames.")
        for ((x, y) in GameInstanceStorage.gameMapData) {
            if (!y.isMultiStaged && GameInstanceStorage.getGameInstance(x) == null) {
                GameInstanceStorage.gameMap[x] = GameInstanceObject(x, y, y.gameConfiguration!!)
                log("§e--- Registered minigame instance \"$x\".")
            }
        }
    }

    private fun finalizeAlwaysEnabledGames() {
        log("§e-- Enabling always-enabled game instances..")
        for ((_, y) in GameInstanceStorage.gameMap) {
            log("§e--- Starting game instance ${y.instanceName} (Game \"${y.gameData.gameName}\")")
            y.start()
        }
    }

    private fun registerSerializers() {
        log("§e-- Registering serializers")
        ConfigurationSerialization.registerClass(SelectedLocation::class.java)
    }

    private fun prepareCommandAnnotationApi() {
        Arguments.register(Location::class) {
            if (size() != 4 && params<Player>() != null) {
                return@register params<Player>()!!.location
            }
            val world = Bukkit.getWorld(args<String>())!!
            return@register Location(world, args()!!, args()!!, args()!!)
        }

        Arguments.register(SelectedLocation::class) {
            if (params<Player>() == null) {
                throw IllegalStateException()
            }
            val player = params<Player>()!!
            return@register player.get<Array<Location>>("[MGLib] Locs")!!.run {
                player.removeValue("[MGLib] Locs")
                return@run SelectedLocation(this[0], this[1])
            }
        }
    }
}