package skywolf46.microgamelib.events.playerEvent

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class GameAfterQuitEvent(player: Player) : PlayerEvent(player) {
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