package skywolf46.microgamelib.api.data.results

import org.bukkit.entity.Player
import skywolf46.microgamelib.api.abstraction.IGameResult
import skywolf46.microgamelib.api.data.GameTeam

class TeamWinnerResult(var winner: GameTeam?) : IGameResult {
    override fun getWinner(): List<Player> {
        return ArrayList(winner!!.players)
    }

    override fun isWinnerExists(): Boolean {
        return winner != null
    }

}