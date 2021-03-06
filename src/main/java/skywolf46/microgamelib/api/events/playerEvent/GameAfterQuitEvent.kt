package skywolf46.microgamelib.api.events.playerEvent

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import skywolf46.microgamelib.api.abstraction.event.AbstractPlayerGameEvent
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.enums.QuitCause

class GameAfterQuitEvent(val cause: QuitCause, instance: GameInstanceObject, player: Player) : AbstractPlayerGameEvent(instance, player) {
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