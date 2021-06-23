package skywolf46.microgamelib.commands

import org.bukkit.command.CommandSender
import skywolf46.commandannotation.kotlin.annotation.Force
import skywolf46.commandannotation.kotlin.annotation.Mark
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.commandannotationmc.minecraft.annotations.MinecraftCommand
import skywolf46.extrautility.util.sendMessage
import skywolf46.microgamelib.MicroGameLib
import skywolf46.microgamelib.storage.GameInstanceStorage

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

    const val GLOBAL_HELP = "/mglib config"
    const val GLOBAL_GAME_PREFIX = "/mglib config <string>"

    @MinecraftCommand("/mglib config", "/mglib config <string>")
    fun Arguments.onBaseCommand(sender: CommandSender) {
        sender.sendMessage(
            "§bMicroGameLib v${MicroGameLib.inst.description.version}",
            "§e/mglib config <GameName> set <Name> <Value(Any..)>",
            "§7Set target game configuration variable.",
            "§6set §7command not work for Map / List.",

            "§e/mglib config <GameName> add <Name> <Value(Any..)>",
            "§7Add variable to target game configuration.",
            "§6config§7 command will work only for List.",


            "§e/mglib config <GameName> insert <Name> <Key(String)> <Value(Any..)>",
            "§7Add variable to target game configuration.",
            "§6insert§7 command will work only for Map.",

            "§e/mglib config <GameName> remove <Name> <Key> <Value(Any..)>",
            "§7Remove target from game configuration variable.",
            "§6remove§7 command will work only for Map."
        )

    }

    @MinecraftCommand(GLOBAL_HELP, GLOBAL_GAME_PREFIX, "$GLOBAL_GAME_PREFIX set <string>")
    fun Arguments.onSetValueHelp(sender: CommandSender) {
        sender.sendMessage(
            "§bMicroGameLib v${MicroGameLib.inst.description.version}",
            "§e/mglib config <GameName> set <Name> <Value(Any..)>",
            "§7Set target game configuration variable.",
            "§6set §7command not work for Map / List.")
    }

    // Min - 2 arguments
    @MinecraftCommand("$GLOBAL_GAME_PREFIX set <string> <string>")
    fun Arguments.onSetValue(sender: CommandSender) {
        args<String>(false) { stageName ->
            GameInstanceStorage.getGameInstance(stageName)?.let { stage ->
                args<String>(false) { fieldName ->
                    if (fieldName !in stage.config.fields) {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Configuration instance \"$fieldName\" is not exists")
                        return
                    }
                    val configField = stage.config.fields[fieldName]!!
                    if (List::class.java.isAssignableFrom(configField.type)) {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Configuration instance \"$fieldName\" is List; Use $GLOBAL_GAME_PREFIX <add / remove> <FieldName> <Value(Any)> instead.")
                        return
                    }

                    if (Map::class.java.isAssignableFrom(configField.type)) {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Configuration instance \"$fieldName\" is Map; Use $GLOBAL_GAME_PREFIX <insert / remove> <FieldName> <Value(Any)> instead.")
                        return
                    }
                    args(configField.type.kotlin, false) { argument ->
                        stage.config.declaredVariables[fieldName] = argument
                        stage.reserveSave()
                        sender.sendMessage("§aConfiguration changed for field \"${fieldName}\" in game instance \"$stageName\"")
                    } illegalNumber {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Number field wrong (${this.message})")
                    } noArgs {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Type of configuration field $fieldName (${configField.type.name}) is not registered to CommandAnnotation processors.")
                    } throws {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Error occurred (${this.javaClass.name} : ${this.message})")
                    }
                }
            }
                ?: sender.sendMessage("§cCannot modify game instance configuration : Game instance \"$stageName\" is not registered")
        }
    }

    @MinecraftCommand(GLOBAL_HELP, GLOBAL_GAME_PREFIX, "$GLOBAL_GAME_PREFIX add")
    fun Arguments.onAddValueHelp(sender: CommandSender) {
        sender.sendMessage(
            "§e/mglib config <GameName> add <Name> <Value(Any..)>",
            "§7Add variable to target game configuration.",
            "§6config§7 command will work only for List.")
    }

    @MinecraftCommand(GLOBAL_HELP, GLOBAL_GAME_PREFIX, "$GLOBAL_GAME_PREFIX insert")
    fun Arguments.onInsertValueHelp(sender: CommandSender) {
        sender.sendMessage(
            "§e/mglib config <GameName> insert <Name> <Key(String)> <Value(Any..)>",
            "§7Add variable to target game configuration.",
            "§6insert§7 command will work only for Map.")
    }

    // Min - 2 arguments
    @MinecraftCommand("$GLOBAL_GAME_PREFIX insert <string> <string> <string>")
    fun Arguments.onInsertValue(sender: CommandSender) {
        println("Inserting")
        args<String>(false) { stageName ->
            GameInstanceStorage.getGameInstance(stageName)?.let { stage ->
                args<String>(false) { fieldName ->
                    if (fieldName !in stage.config.fields) {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Configuration instance \"$fieldName\" is not exists")
                        return
                    }
                    val configField = stage.config.fields[fieldName]!!

                    if (List::class.java.isAssignableFrom(configField.type)) {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Configuration instance \"$fieldName\" is List; use $GLOBAL_GAME_PREFIX add <FieldName> <Value(Any)> instead.")
                        return
                    }
                    if (!Map::class.java.isAssignableFrom(configField.type)) {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Configuration instance \"$fieldName\" is not Map; use $GLOBAL_GAME_PREFIX set <FieldName> <Value(Any)> instead.")
                        return
                    }
                    println(configField.argumentTypes.contentToString())
                    val mapKey = args<String>()!!
                    args(configField.argumentTypes[1].kotlin, false) { argument ->
                        val next = stage.config.declaredVariables[fieldName] as MutableMap<String, Any>?
                            ?: mutableMapOf<String, Any>().apply {
                                stage.config.declaredVariables[fieldName] = this
                            }
                        next[mapKey] = argument
                        stage.reserveSave()
                        sender.sendMessage("§aConfiguration updated for field \"${fieldName}\"(Key = \"${mapKey}\") in game instance \"$stageName\"")
                    } illegalNumber {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Number field wrong (${this.message})")
                    } noArgs {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Type of configuration field $fieldName (${configField.argumentTypes[1]}) is not registered to CommandAnnotation processors.")
                    } throws {
                        sender.sendMessage("§cCannot modify game instance configuration for game instance \"$stageName\" : Error occurred (${this.javaClass.name} : ${this.message})")
                    }
                }
            }
                ?: sender.sendMessage("§cCannot modify game instance configuration : Game instance \"$stageName\" is not registered")
        }
    }


    @MinecraftCommand("/mglib config <string> remove")
    fun Arguments.onRemoveValue(sender: CommandSender) {
        if (size() < 3) {
            sender.sendMessage(
                "§bMicroGameLib v${MicroGameLib.inst.description.version}",
                "§e/mglib config <GameName> remove <Name> <Key> <Value(Any..)>",
                "§7Remove target from game configuration variable.",
                "§6remove§7 command will work only for Map.")
            return
        }
    }

}