package skywolf46.microgamelib.data

import skywolf46.microgamelib.storage.InjectReference

class GameInstanceObject(val gameData: GameInstanceData, val gameConfig: ConfigurationStructure, doSave: Boolean = false) {

    private val arguments = InjectReference()
    var gameStagePointer = 0


    init {
        arguments.addArgument(gameConfig)
        arguments.addArgument(gameData)
        arguments.addArgument(this)
    }

    fun toStage(name: String) {
        gameStagePointer = gameData.findStageIndex(name)
    }

    fun cleanUpStage() {

    }

}