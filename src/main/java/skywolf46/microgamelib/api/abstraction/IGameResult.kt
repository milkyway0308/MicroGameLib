package skywolf46.microgamelib.api.abstraction

import org.bukkit.entity.Player
import skywolf46.microgamelib.api.data.results.MultipleWinnerResult
import skywolf46.microgamelib.api.data.results.ScoredSingleResult
import skywolf46.microgamelib.api.data.results.SingleWinnerResult
import skywolf46.microgamelib.api.data.results.TeamWinnerResult

interface IGameResult {

    fun getWinner(): List<Player>

    fun isWinnerExists() : Boolean

    fun asScoredResult(): ScoredSingleResult {
        return this as ScoredSingleResult
    }

    fun asSingleWinnerResult(): SingleWinnerResult {
        return this as SingleWinnerResult
    }

    fun asMultipleWinnerResult(): MultipleWinnerResult {
        return this as MultipleWinnerResult
    }

    fun asTeamWinnerResult(): TeamWinnerResult {
        return this as TeamWinnerResult
    }
}