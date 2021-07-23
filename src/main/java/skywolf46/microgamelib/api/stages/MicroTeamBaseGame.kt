package skywolf46.microgamelib.api.stages

import skywolf46.microgamelib.api.data.GameTeamManager

abstract class MicroTeamBaseGame : MicroGame() {

    var teamManager: GameTeamManager? = null

    fun getTeamManagerImpl(): GameTeamManager {
        return (teamManager ?: run {
            teamManager = GameTeamManager(party)
            return@run teamManager
        })!!
    }


}