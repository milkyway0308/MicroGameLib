package skywolf46.microgamelib.test

import skywolf46.microgamelib.annotations.GameConfiguration

@GameConfiguration(TEST_GAME_NAME)
class TestConfiguration {
    val name = "Test"
    var data = "Test4"
    var systemVersion: Double = 25.0

    var map = mutableMapOf<String, String>()
}