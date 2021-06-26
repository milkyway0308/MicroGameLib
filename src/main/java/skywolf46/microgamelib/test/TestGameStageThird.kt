package skywolf46.microgamelib.test

import org.bukkit.event.block.BlockBreakEvent
import skywolf46.extrautility.util.schedule
import skywolf46.microgamelib.annotations.GameStage
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.data.GameInstanceObject

@GameStage("Test3", gameName = TEST_GAME_NAME, 2)
class TestGameStageThird(
    val configuration: TestConfiguration, val stage: GameInstanceObject,
) {
    init {
        println("Game third! ${configuration.systemVersion}")
        configuration.systemVersion += 3
        schedule(200L) {
            stage.nextStage()
        }
    }

    @InGameListener
    fun BlockBreakEvent.hello() {
        println("Hello, sub stage!")
    }
}