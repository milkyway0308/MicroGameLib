package skywolf46.microgamelib.data

import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable

class SelectedLocation(val loc01: Location, val loc02: Location) : ConfigurationSerializable {

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): SelectedLocation {
            return SelectedLocation(map["01"] as Location, map["02"] as Location)
        }
    }

    override fun serialize(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["01"] = loc01
        map["02"] = loc02
        return map
    }
}