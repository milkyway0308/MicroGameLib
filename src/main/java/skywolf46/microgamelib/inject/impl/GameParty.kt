package skywolf46.microgamelib.inject.impl

import org.bukkit.entity.Player
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.InjectTarget

@InjectTarget(injectPriority = 0)
class GameParty(protected val parent: GameParty?) {
    val players = mutableListOf<Player>()

    open fun removePlayer(player: Player) {
        parent?.removePlayer(player) ?: players.remove(player)
    }

    open fun addPlayer(player: Player) {

    }

    @InGameListener
    fun onListen() {

    }
}