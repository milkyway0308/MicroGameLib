package skywolf46.microgamelib.api.events.gameEvent

import org.bukkit.event.HandlerList
import org.bukkit.event.server.ServerEvent
import skywolf46.microgamelib.data.GameInstanceObject

class GameSuggestNextStageEvent(val game: GameInstanceObject) : ServerEvent() {
    private var isAccepted = false
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

    fun nextStage() {
        if(isAccepted)
            return
        isAccepted = true
        game.nextStage()
    }

    fun isAccepted(): Boolean {
        return isAccepted
    }
}