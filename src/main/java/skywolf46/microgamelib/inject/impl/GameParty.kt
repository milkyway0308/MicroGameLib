package skywolf46.microgamelib.inject.impl

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import skywolf46.extrautility.util.callEvent
import skywolf46.extrautility.util.get
import skywolf46.extrautility.util.removeValue
import skywolf46.extrautility.util.set
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.api.abstraction.ISpectate
import skywolf46.microgamelib.api.events.playerEvent.*
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.enums.QuitCause

@InjectTarget(scope = InjectScope.GAME)
open class GameParty(private val parent: GameParty?) : ISpectate {
    init {
        println("Party created")
    }

    @Inject
    private lateinit var gameInstance: GameInstanceObject

    @Suppress("MemberVisibilityCanBePrivate")
    protected val playerList = mutableListOf<Player>()

    private val realSpectator = mutableListOf<Player>()

    val spectators
        get() = ArrayList(realSpectator)

    open fun getAllPlayers() = playerList.toList()

    open fun size() = playerList.size

    open fun removePlayer(player: Player, cause: QuitCause) {
        parent?.removePlayer(player, cause) ?: run {
            if (playerList.contains(player)) {
                Bukkit.getPluginManager().callEvent(GameQuitEvent(cause, gameInstance, player))
                playerList.remove(player)
                player.removeValue("[MGLib] Game")
                Bukkit.getPluginManager().callEvent(GameAfterQuitEvent(cause, gameInstance, player))
            }
        }
    }

    open fun addPlayer(player: Player) {
        val before = player.get<String>("[MGLib] Game")
        if (before != null) {
            return
        }
        player["[MGLib] Game"] = gameInstance.instanceName
        GameJoinEvent(gameInstance, player).callEvent().apply {
            if (isCancelled) {
                player.removeValue("[MGLib] Game")
                return
            }
        }
        playerList.add(player)
        Bukkit.getPluginManager().callEvent(GameAfterJoinEvent(gameInstance, player))
    }


    override fun isSpectated(player: Player): Boolean {
        return player in realSpectator
    }

    override fun getSpectators(): List<Player> {
        val players = mutableListOf<Player>()
        for (x in playerList)
            if (isSpectated(x))
                players += x
        return players
    }

    override fun getPlayers(includeSpectator: Boolean): List<Player> {
        return if (includeSpectator) getAllPlayers() else kotlin.run {
            val players = mutableListOf<Player>()
            for (x in playerList)
                if (!isSpectated(x))
                    players += x
            return@run players
        }
    }

    override fun setSpectate(player: Player, isSpectate: Boolean) {
        if (isSpectate == isSpectated(player))
            return
        PlayerSpectateEvent(gameInstance, player, isSpectate).callEvent().apply {
            if (isCancelled)
                return
            if (isSpectating)
                realSpectator += player
            else
                realSpectator -= player
            PlayerAfterSpectateEvent(gameInstance, player, isSpectating).callEvent()
        }
    }
}