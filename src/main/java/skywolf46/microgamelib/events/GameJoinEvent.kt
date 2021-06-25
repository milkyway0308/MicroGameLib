package skywolf46.microgamelib.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class GameJoinEvent(player: Player) : PlayerEvent(player), Cancellable {

    var cancel = false
    override fun isCancelled(): Boolean {
        return cancel
    }

    override fun setCancelled(p0: Boolean) {
        cancel = p0
    }

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