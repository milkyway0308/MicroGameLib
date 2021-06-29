package skywolf46.microgamelib.api.stages

import org.bukkit.event.EventPriority
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.api.events.gameEvent.StageAfterChangedEvent
import skywolf46.microgamelib.api.events.playerEvent.GameAfterJoinEvent
import skywolf46.microgamelib.api.events.playerEvent.GameJoinEvent

abstract class MicroGamePrepare(val requiredPlayers: Int = 10) : MicroGame() {


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

    private fun checkPlayers() {
        if (party.size() >= requiredPlayers) {
            instance.nextStage()
        }
    }
}