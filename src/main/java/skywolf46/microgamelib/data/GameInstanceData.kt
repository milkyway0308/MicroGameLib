package skywolf46.microgamelib.data

import skywolf46.extrautility.data.ArgumentStorage

class GameInstanceData(val gameName: String) {

    val gameStages = arrayListOf<GameStageData>()
    val gameStageMap = mutableMapOf<String, GameStageData>()
    var gameConfiguration : ConfigurationStructure? = null
    var isMultiStaged = false
    var alwaysStarted = false


    fun getStage(index: Int): GameStageData {
        return gameStages[index]
    }

    fun findStage(name: String): GameStageData {
        return gameStageMap[name]!!
    }

    fun findStageIndex(name: String): Int {
        return gameStages.indexOf(gameStageMap[name]!!)
    }


}