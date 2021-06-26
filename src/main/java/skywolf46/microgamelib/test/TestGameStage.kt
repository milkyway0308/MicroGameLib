package skywolf46.microgamelib.test

import skywolf46.extrautility.util.schedule
import skywolf46.microgamelib.annotations.Extract
import skywolf46.microgamelib.annotations.GameStage
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.data.GameInstanceObject
import java.util.concurrent.atomic.AtomicInteger

@GameStage("Test1", gameName = TEST_GAME_NAME, 0)
class TestGameStage(
    val configuration: TestConfiguration, val stage: GameInstanceObject,
) {
    init {
        println("Game stage started! ${configuration.systemVersion}")
        configuration.systemVersion += 3
        schedule(70L) {
            stage.nextStage()
        }
    }

    @Extract
    var test: TestListenerObject = TestListenerObject()


}