package skywolf46.microgamelib.data

import org.bukkit.entity.Entity
import skywolf46.extrautility.util.MethodInvoker

data class EventInvoker(
    val condition: (Entity) -> Boolean,
    val method: MethodInvoker,
    private val unregisterer: EventInvoker.() -> Unit,
) {
    fun unregister() {
        unregisterer(this)
    }
}