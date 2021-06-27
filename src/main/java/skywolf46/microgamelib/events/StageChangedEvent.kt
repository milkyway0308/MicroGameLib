package skywolf46.microgamelib.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.data.GameStageData

class StageChangedEvent(val stage: GameInstanceObject) : Event() {
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