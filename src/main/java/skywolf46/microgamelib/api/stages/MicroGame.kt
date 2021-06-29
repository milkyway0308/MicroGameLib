package skywolf46.microgamelib.api.stages

import org.bukkit.event.EventPriority
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.api.events.playerEvent.GameJoinEvent
import skywolf46.microgamelib.inject.impl.GameParty

abstract class MicroGame {
    @Inject
    lateinit var party: GameParty

    @Inject
    lateinit var instance: GameInstanceObject

    @InGameListener(priority = EventPriority.LOWEST)
    fun GameJoinEvent.filterJoin() {
        cancelFramework()
    }


}