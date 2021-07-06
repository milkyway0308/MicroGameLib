package skywolf46.microgamelib.util

import org.bukkit.Material

object MaterialFinder {
    fun findMaterials(vararg names: String): Material? {
        for (x in names) {
            try {
                return Material.valueOf(x)
            } catch (_: Exception) {
                // Ignored
            }
        }
        return null
    }
}