package skywolf46.microgamelib.commands

import org.bukkit.command.CommandSender
import skywolf46.commandannotation.kotlin.annotation.Force
import skywolf46.commandannotation.kotlin.annotation.Mark
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.commandannotation.kotlin.enums.VisibilityScope
import skywolf46.commandannotationmc.minecraft.annotations.MinecraftCommand

object ConfigurationCommand {
    @Mark("Permissions")
    @Force
    fun checkPermissions(sender: CommandSender): Boolean {
        if (!sender.hasPermission("mglib.admin") && !sender.isOp) {
            sender.sendMessage("§cPermission denied.")
            return false
        }
        return true
    }

    @MinecraftCommand("/mglib game")
    fun Arguments.onBaseCommand(sender: CommandSender) {
        sender.sendMessage("§eGame help")
    }

    @MinecraftCommand("/mglib game <string>")
    fun Arguments.onGameHelp(sender: CommandSender) {
        sender.sendMessage("§eGame help - ${args<String>()}")
    }

    @MinecraftCommand("/mglib game <string> set")
    fun Arguments.onAdd(sender: CommandSender) {
        sender.sendMessage("§eGame set - ${args<String>()} / ${args<String>()}")
    }


}