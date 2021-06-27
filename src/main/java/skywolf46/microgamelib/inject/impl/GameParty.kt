package skywolf46.microgamelib.inject.impl

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import skywolf46.extrautility.util.callEvent
import skywolf46.extrautility.util.get
import skywolf46.extrautility.util.removeValue
import skywolf46.extrautility.util.set
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.events.playerEvent.GameAfterJoinEvent
import skywolf46.microgamelib.events.playerEvent.GameAfterQuitEvent
import skywolf46.microgamelib.events.playerEvent.GameJoinEvent
import skywolf46.microgamelib.events.playerEvent.GameQuitEvent

@InjectTarget(scope = InjectScope.GAME)
open class GameParty(private val parent: GameParty?) {
    @Inject
    private lateinit var gameInstance: GameInstanceObject

    @Suppress("MemberVisibilityCanBePrivate")
    protected val playerList = mutableListOf<Player>()

    open fun getPlayers() = playerList.toList()

    open fun size() = playerList.size

    open fun removePlayer(player: Player) {
        parent?.removePlayer(player) ?: kotlin.run {
            if (playerList.contains(player)) {
                Bukkit.getPluginManager().callEvent(GameQuitEvent(player))
                playerList.remove(player)
                player.removeValue("[MGLib] Game")
                Bukkit.getPluginManager().callEvent(GameAfterQuitEvent(player))
            }
        }
    }

    open fun addPlayer(player: Player) {
        val before = player.get<String>("[MGLib] Game")
        if (before != null) {
            return
        }
        player["[MGLib] Game"] = gameInstance.instanceName
        println("Called event")
        GameJoinEvent(player).callEvent().apply {
            if (isCancelled) {
                println("Cancelled! Prevent event.")
                player.removeValue("[MGLib] Game")
                return
            }
        }
        playerList.add(player)
        Bukkit.getPluginManager().callEvent(GameAfterJoinEvent(player))
    }


}