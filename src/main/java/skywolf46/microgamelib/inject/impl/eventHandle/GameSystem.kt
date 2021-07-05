package skywolf46.microgamelib.inject.impl.eventHandle

import org.bukkit.event.EventPriority
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.api.events.gameEvent.GameEndedEvent
import skywolf46.microgamelib.enums.QuitCause
import skywolf46.microgamelib.inject.impl.GameParty

@InjectTarget(InjectScope.GAME)
class GameSystem {
    @Inject
    private lateinit var party: GameParty

    var doCleanUp = true


    @InGameListener(priority = EventPriority.HIGHEST)
    fun GameEndedEvent.onEvent() {
        if (!doCleanUp)
            return
        for (player in party.getPlayers())
            party.removePlayer(player, QuitCause.GAME_ENDED)
    }
}