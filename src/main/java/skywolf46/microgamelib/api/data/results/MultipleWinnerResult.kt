package skywolf46.microgamelib.api.data.results

import org.bukkit.entity.Player
import skywolf46.microgamelib.api.abstraction.IGameResult

class MultipleWinnerResult(val players: MutableList<Player>) : IGameResult {
    override fun getWinner(): List<Player> {
        return ArrayList(players)
    }

    override fun isWinnerExists(): Boolean {
        return players.size > 0
    }
}