package skywolf46.microgamelib.commands

import org.bukkit.command.CommandSender
import skywolf46.commandannotation.kotlin.annotation.Force
import skywolf46.commandannotation.kotlin.annotation.Mark
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.commandannotationmc.minecraft.annotations.MinecraftCommand
import skywolf46.extrautility.util.sendMessage
import skywolf46.microgamelib.MicroGameLib
import skywolf46.microgamelib.data.GameInstanceData
import skywolf46.microgamelib.data.GameInstanceObject
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

    @MinecraftCommand("/mglib game <string> delete")
    fun Arguments.onDelete(sender: CommandSender) {

    }

    @MinecraftCommand("/mglib game <string> start")
    fun Arguments.onStart(sender: CommandSender) {

    }
}