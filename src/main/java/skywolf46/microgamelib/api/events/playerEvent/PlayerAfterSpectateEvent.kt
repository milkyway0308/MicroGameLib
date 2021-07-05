package skywolf46.microgamelib.api.events.playerEvent

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import skywolf46.microgamelib.api.abstraction.event.AbstractPlayerGameEvent
import skywolf46.microgamelib.data.GameInstanceObject

class PlayerAfterSpectateEvent(game: GameInstanceObject, player: Player, val isSpectating: Boolean) :
    AbstractPlayerGameEvent(game, player) {

    override fun getHandlers(): HandlerList {
        return _handle
    }

    companion object {
        internal val _handle = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return _handle
        }
    }
}