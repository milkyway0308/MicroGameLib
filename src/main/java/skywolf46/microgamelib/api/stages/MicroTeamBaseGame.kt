package skywolf46.microgamelib.api.stages

import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.api.data.GameTeamManager
import skywolf46.microgamelib.inject.impl.GameParty

abstract class MicroTeamBaseGame : MicroGame() {

    var teamManager: GameTeamManager? = null

    fun getTeamManagerImpl(): GameTeamManager {
        return (teamManager ?: run {
            teamManager = GameTeamManager(party)
            return@run teamManager
        })!!
    }


}