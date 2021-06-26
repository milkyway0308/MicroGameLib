package skywolf46.microgamelib.data

import org.bukkit.entity.Entity
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.MethodInvoker
import java.lang.reflect.Method

data class EventInvokerReady(
    val method: Method,
    private val registerer: EventInvoker.() -> Unit,
    private val unregisterer: EventInvoker.() -> Unit,
) {
    fun register(
        target: Any,
        condition: (Entity) -> Boolean,
        preProcess: ArgumentStorage.() -> Unit,
    ): EventInvoker {
        val invoker = EventInvoker(condition, MethodInvoker(method, target), preProcess, unregisterer)
        registerer(invoker)
        return invoker
    }
}
