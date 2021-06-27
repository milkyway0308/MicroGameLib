package skywolf46.microgamelib.test

import skywolf46.extrautility.util.schedule
import skywolf46.microgamelib.annotations.Extract
import skywolf46.microgamelib.annotations.GameInstance
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.api.stages.MicroGamePrepare
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.inject.impl.GameParty
import java.util.concurrent.atomic.AtomicInteger

internal const val TEST_GAME_NAME = "TestGame"

@GameInstance(TEST_GAME_NAME, alwaysStarted = true)
class TestGameInstance(
    val configuration: TestConfiguration, val stage: GameInstanceObject,
) : MicroGamePrepare(1) {

    @Extract(scope = InjectScope.GAME)
    val test = AtomicInteger(0)
}