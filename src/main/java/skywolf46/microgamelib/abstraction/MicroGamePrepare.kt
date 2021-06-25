package skywolf46.microgamelib.abstraction

import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.events.GameAfterJoinEvent
import skywolf46.microgamelib.events.GameJoinEvent

class MicroGamePrepare : AbstractGameBase() {
    var requiredPlayers = 10


    @InGameListener
    fun GameAfterJoinEvent.onPrepare() {
        if (party.players.size >= requiredPlayers) {
            instance.nextStage()
        }
    }
}