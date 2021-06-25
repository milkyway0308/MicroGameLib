package skywolf46.microgamelib.inject.impl

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import skywolf46.extrautility.util.callEvent
import skywolf46.extrautility.util.removeValue
import skywolf46.extrautility.util.set
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.events.GameAfterJoinEvent
import skywolf46.microgamelib.events.GameAfterQuitEvent
import skywolf46.microgamelib.events.GameJoinEvent
import skywolf46.microgamelib.events.GameQuitEvent

@InjectTarget(scope = InjectScope.GAME)
open class GameParty(private val parent: GameParty?) {
    lateinit var gameInstance: GameInstanceObject
    val players = mutableListOf<Player>()

    open fun removePlayer(player: Player) {
        parent?.removePlayer(player) ?: kotlin.run {
            if (players.contains(player)) {
                Bukkit.getPluginManager().callEvent(GameQuitEvent(player))
                players.remove(player)
                player.removeValue("[MGLib] Game")
                Bukkit.getPluginManager().callEvent(GameAfterQuitEvent(player))
            }
        }
    }

    open fun addPlayer(player: Player) {
        GameJoinEvent(player).callEvent().apply {
            if (isCancelled)
                return
        }
        player["[MGLib] Game"] = gameInstance.instanceName
        players.add(player)
        Bukkit.getPluginManager().callEvent(GameAfterJoinEvent(player))

    }

    @InGameListener
    fun onListen() {

    }
}