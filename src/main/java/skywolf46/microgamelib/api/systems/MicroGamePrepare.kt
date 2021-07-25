package skywolf46.microgamelib.api.systems

import org.bukkit.event.EventPriority
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.api.abstraction.AbstractAttachableSystem
import skywolf46.microgamelib.api.abstraction.IAttachableSystem
import skywolf46.microgamelib.api.events.gameEvent.GameSuggestNextStageEvent
import skywolf46.microgamelib.api.events.gameEvent.StageAfterChangedEvent
import skywolf46.microgamelib.api.events.playerEvent.GameAfterJoinEvent
import skywolf46.microgamelib.api.events.playerEvent.GameJoinEvent

class MicroGamePrepare(val requiredPlayers: Int = 10) : AbstractAttachableSystem() {
    @InGameListener(priority = EventPriority.LOWEST)
    fun GameJoinEvent.onEvent() {
        if (isCancelledFromFramework()) {
            isCancelled = false
        }
    }

    @InGameListener
    fun StageAfterChangedEvent.onEvent() {
        checkPlayers()
    }

    @InGameListener
    fun GameAfterJoinEvent.onPrepare() {
        checkPlayers()
    }


    @InGameListener
    fun GameSuggestNextStageEvent.onEvent() {
        nextStage()
    }

    private fun checkPlayers() {
        if (party.size() >= requiredPlayers) {
            instance.nextStage()
        }
    }

    override fun hasWinner(): Boolean {
        return false
    }
}