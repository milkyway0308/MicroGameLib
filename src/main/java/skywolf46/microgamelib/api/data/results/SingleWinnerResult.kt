package skywolf46.microgamelib.api.data.results

import org.bukkit.entity.Player
import skywolf46.microgamelib.api.abstraction.IGameResult

class SingleWinnerResult(var winner: Player?) : IGameResult {
    override fun getWinner(): List<Player> {
        return mutableListOf(winner!!)
    }

    override fun isWinnerExists(): Boolean {
        return winner != null
    }


}