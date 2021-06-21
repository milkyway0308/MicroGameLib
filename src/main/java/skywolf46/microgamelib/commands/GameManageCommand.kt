package skywolf46.microgamelib.commands

import org.bukkit.command.CommandSender
import skywolf46.commandannotation.kotlin.annotation.Force
import skywolf46.commandannotation.kotlin.annotation.Mark
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.commandannotationmc.minecraft.annotations.MinecraftCommand

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
    fun Arguments.onCreate(sender: CommandSender) {

    }

    @MinecraftCommand("/mglib game <string> delete")
    fun Arguments.onDelete(sender: CommandSender) {

    }

    @MinecraftCommand("/mglib game <string> start")
    fun Arguments.onStart(sender: CommandSender) {

    }
}