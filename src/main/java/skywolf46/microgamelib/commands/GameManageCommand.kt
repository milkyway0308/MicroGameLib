package skywolf46.microgamelib.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skywolf46.commandannotation.kotlin.annotation.Force
import skywolf46.commandannotation.kotlin.annotation.Mark
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.commandannotationmc.minecraft.annotations.MinecraftCommand
import skywolf46.commandannotationmc.minecraft.annotations.preprocessor.PlayerOnly
import skywolf46.extrautility.util.callEvent
import skywolf46.extrautility.util.get
import skywolf46.extrautility.util.log
import skywolf46.extrautility.util.sendMessage
import skywolf46.microgamelib.MicroGameLib
import skywolf46.microgamelib.api.events.gameEvent.GameSuggestNextStageEvent
import skywolf46.microgamelib.data.GameInstanceData
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.inject.impl.GameParty
import skywolf46.microgamelib.storage.GameInstanceStorage

object GameManageCommand {
    @Mark("Permissions")
    @Force
    fun checkPermissions(sender: CommandSender): Boolean {
        if (!sender.hasPermission("mglib.admin") && !sender.isOp) {
            sender.sendMessage("§cPermission denied.")
            return false
        }
        return true
    }

    @MinecraftCommand("/mglib game <string> create")
    fun Arguments.onCreateHelp(sender: CommandSender) {
        sender.sendMessage(
            "§bMicroGameLib v${MicroGameLib.inst.description.version}",
            "§e/mglib game <gameName> create <instanceName>",
            ""
        )
    }

    @MinecraftCommand("/mglib game <string> create <string>")
    fun Arguments.onCreate(sender: CommandSender) {
        args<String>(false) { stageName ->
            GameInstanceStorage.getGameInstance(stageName)?.apply {
                sender.sendMessage("§cCannot register game instance \"$stageName\" : Instance already registered")
                return
            }
            args<String>(false) { gameName ->
                GameInstanceStorage.gameMap[stageName] =
                    GameInstanceStorage.getGameInstanceData(gameName)?.run {
                        GameInstanceObject(stageName, this, this.gameConfiguration!!)
                    } ?: kotlin.run {
                        sender.sendMessage("§cCannot register game instance \"$stageName\" : Target game \"$gameName\" not registered")
                        return
                    }
            }
        }
    }

    @MinecraftCommand("/mglib game <string> join")
    @PlayerOnly
    fun Arguments.onJoin(player: Player) {
        args<String>(false) { stageName ->
            GameInstanceStorage.getGameInstance(stageName)?.let { stage ->
                if (!stage.gameData.alwaysStarted) {
                    player.sendMessage("§cOperation not supported : Join feature only supports for always-enabled games")
                    return
                }
                if (player.hasMetadata("[MGLib] Game")) {
                    player.sendMessage("§cYou have to quit current game to join another game.")
                    return
                }
                stage.stageArgument!![GameParty::class.java][0].addPlayer(player)
            }
        }
    }

    @MinecraftCommand("/mglib game <string> delete")
    fun Arguments.onDelete(sender: CommandSender) {

    }

    @MinecraftCommand("/mglib game <string> start")
    fun Arguments.onStart(sender: CommandSender) {
        args<String>(false) { stageName ->
            GameInstanceStorage.getGameInstance(stageName)?.apply {
                if (gameData.alwaysStarted) {
                    GameSuggestNextStageEvent(this).callEvent().apply {
                        if (!isAccepted()) {
                            sender.sendMessage("§cFailed to suggest starting game to stage : No listener bounded on current stage")
                            return
                        }
                        sender.sendMessage("§astage skipped.")
                    }
                    return
                }
                start()
            } ?: sender.sendMessage("§cCannot start game instance \"$stageName\" : Instance not registered")
        }
    }

    @MinecraftCommand("/mglib game <string> restart")
    fun Arguments.onRestart(sender: CommandSender) {
        args<String>(false) { stageName ->
            GameInstanceStorage.getGameInstance(stageName)?.apply {
                if (this.stageArgument == null) {
                    sender.sendMessage("§cOperation not supported : Game not started")
                    return
                }
                log("§eMicroGameLib / Log §f| §7Game restarted from ${sender.name}")
                reset()
                if (!gameData.alwaysStarted)
                    start()
            } ?: sender.sendMessage("§cCannot start game instance \"$stageName\" : Instance not registered")
        }
    }
}