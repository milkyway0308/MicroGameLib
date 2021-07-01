package skywolf46.microgamelib.data

import org.bukkit.entity.Entity
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.MethodInvoker

data class EventInvoker(
    val condition: (Entity) -> Boolean,
    val method: MethodInvoker,
    val includeEntity: Boolean,
    val preProcess: ArgumentStorage.() -> Unit,
    private val unregisterer: EventInvoker.() -> Unit,
) {
    fun unregister() {
        unregisterer(this)
    }
}