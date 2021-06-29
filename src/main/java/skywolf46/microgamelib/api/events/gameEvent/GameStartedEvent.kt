package skywolf46.microgamelib.api.events.gameEvent

import org.bukkit.event.HandlerList
import org.bukkit.event.server.ServerEvent
import skywolf46.microgamelib.data.GameInstanceObject

class GameStartedEvent(val game: GameInstanceObject) : ServerEvent() {
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