package skywolf46.microgamelib.test

import skywolf46.extrautility.util.schedule
import skywolf46.microgamelib.annotations.GameStage
import skywolf46.microgamelib.data.GameInstanceObject

@GameStage("Test3", gameName = TEST_GAME_NAME, 2)
class TestGameStageThird(
    val configuration: TestConfiguration, val stage: GameInstanceObject,
) {
    init {
        println("Game third! ${configuration.systemVersion}")
        configuration.systemVersion += 3
        schedule(20L) {
            stage.nextStage()
        }
    }
}