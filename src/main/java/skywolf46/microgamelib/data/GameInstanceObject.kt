package skywolf46.microgamelib.data

import skywolf46.asyncdataloader.core.abstraction.enums.LoadState
import skywolf46.asyncdataloader.core.abstraction.loader.AbstractDataLoader
import skywolf46.asyncdataloader.core.abstraction.loader.AbstractDataSnapshot
import skywolf46.asyncdataloader.file.impl.BukkitYamlBasedSnapshot
import skywolf46.asyncdataloader.file.impl.loadBukkitYaml
import skywolf46.microgamelib.MicroGameLib
import skywolf46.microgamelib.storage.InjectReference
import java.io.File

class GameInstanceObject : AbstractDataLoader<GameInstanceObject> {

    private val arguments = InjectReference()
    var gameStagePointer = 0
    lateinit var config: ConfigurationStructure

    val stageName: String
    val gameData: GameInstanceData

    constructor(stageName: String, gameData: GameInstanceData) {
        this.stageName = stageName
        this.gameData = gameData
        init()
    }

    constructor(stageName: String, gameData: GameInstanceData, config: ConfigurationStructure)  {
        this.stageName = stageName
        this.gameData = gameData
        this.config = config
        loaded.set(LoadState.EMPTY_LOAD)
        init()
    }

    private fun init() {
        arguments.addArgument(gameData)
        arguments.addArgument(this)
        if (!loaded.get().isLoadedState) {
            loadBukkitYaml(File(MicroGameLib.inst.dataFolder, "Stages/$stageName")) {
                config = gameData.gameConfiguration!!.loadFromSection(this)
                arguments.addArgument(config)
            }
        } else
            arguments.addArgument(config)
    }


    fun toStage(name: String) {
        gameStagePointer = gameData.findStageIndex(name)
    }


    override fun snapshot(): AbstractDataSnapshot {
        return object : BukkitYamlBasedSnapshot(File(MicroGameLib.inst.dataFolder, "Stages/$stageName")) {
            override fun trigger(isFinalizing: Boolean) {
                yaml {
                    this["Game"] = gameData.gameName
                    this["Stage"] = stageName
                    config.saveToSection(this.createSection("Data"))
                }
            }

        }
    }

}