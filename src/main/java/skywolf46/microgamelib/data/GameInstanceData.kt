package skywolf46.microgamelib.data

import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.PriorityReference

class GameInstanceData(val gameName: String) {

    val gameStages = object : ArrayList<PriorityReference<GameStageData>>() {
        override fun add(element: PriorityReference<GameStageData>): Boolean {
            val ret = super.add(element)
            sortWith(Comparator.reverseOrder())
            return ret
        }
    }
    val gameStageSorted
        get() = gameStages.map { x -> x.data }
    val gameStageMap = mutableMapOf<String, GameStageData>()
    var gameConfiguration: ConfigurationStructure? = null
    var isMultiStaged = false
    var alwaysStarted = false


    fun getStage(index: Int): GameStageData {
        return gameStages[index].data
    }

    fun findStage(name: String): GameStageData {
        return gameStageMap[name]!!
    }

    fun findStageIndex(name: String): Int {
        return gameStageSorted.indexOf(gameStageMap[name]!!)
    }


}