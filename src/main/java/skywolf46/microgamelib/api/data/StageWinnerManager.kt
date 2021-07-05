package skywolf46.microgamelib.api.data

import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.api.abstraction.IGameResult
import skywolf46.microgamelib.enums.InjectScope

@InjectTarget(InjectScope.GAME)
class StageWinnerManager {
    private val realWinners = mutableListOf<IGameResult>()

    val winners
        get() = ArrayList(realWinners)

    fun getPreviousWinners(): IGameResult? {
        return if (realWinners.size <= 0) null else realWinners[realWinners.size - 1]
    }

    fun addResult(result: IGameResult) {
        realWinners += result
    }
}