package skywolf46.microgamelib.api.stages

import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.events.GameAfterJoinEvent

class MicroGamePrepare : AbstractGameBase() {
    var requiredPlayers = 10


    @InGameListener
    fun GameAfterJoinEvent.onPrepare() {
        if (party.players.size >= requiredPlayers) {
            instance.nextStage()
        }
    }
}