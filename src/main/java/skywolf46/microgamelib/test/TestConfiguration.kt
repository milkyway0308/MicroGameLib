package skywolf46.microgamelib.test

import org.bukkit.Location
import org.jetbrains.annotations.PropertyKey
import skywolf46.microgamelib.annotations.GameConfiguration
import skywolf46.microgamelib.annotations.TargetType

@GameConfiguration(TEST_GAME_NAME)
class TestConfiguration {
    val name = "Test"
    var data = "Test4"
    var task: Location? = null
    var systemVersion: String? = "40.0"

    var map = mutableMapOf<String, Location>()
}