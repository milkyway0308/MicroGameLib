package skywolf46.microgamelib.api.stages

import org.bukkit.event.EventPriority
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.events.GameAfterJoinEvent
import skywolf46.microgamelib.events.GameJoinEvent

abstract class MicroGamePrepare(val requiredPlayers: Int = 10) : AbstractGameBase() {

    @InGameListener(priority = EventPriority.LOWEST)
    fun GameJoinEvent.onEvent() {
        if (isCancelledFromFramework()) {
            isCancelled = false
            println("Cancelled! Ignoring.")
        }
    }

    @InGameListener()
    fun GameAfterJoinEvent.onPrepare() {
        if (party.players.size >= requiredPlayers) {
            instance.nextStage()
        }
    }
}