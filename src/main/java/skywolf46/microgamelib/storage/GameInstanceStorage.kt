package skywolf46.microgamelib.storage

import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.data.GameInstanceData

object GameInstanceStorage {
    val gameMap = mutableMapOf<String, GameInstanceObject>()
    val gameMapData = mutableMapOf<String, GameInstanceData>()


    fun getGameInstance(name: String): GameInstanceObject? {
        return gameMap[name]
    }

    fun getGameInstanceData(name: String): GameInstanceData? {
        return gameMapData[name]
    }


}