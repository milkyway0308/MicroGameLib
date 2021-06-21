package skywolf46.microgamelib.data

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.SimplePluginManager
import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.MethodInvoker
import skywolf46.microgamelib.MicroGameLib
import java.lang.IllegalStateException
import java.lang.reflect.Field

class DynamicEventListener(val event: Class<Event>, val priority: EventPriority) {
    companion object {
        private val map = mutableMapOf<Pair<Class<Event>, EventPriority>, DynamicEventListener>()
        fun eventOf(evt: Class<Event>, priority: EventPriority): DynamicEventListener {
            return map.computeIfAbsent(evt to priority) { DynamicEventListener(evt, priority) }
        }
    }

    val targetEntityFields: MutableList<Field> = mutableListOf()
    val listeners = mutableListOf<EventInvoker>()

    init {
        for (x in event.fields) {
            if (Entity::class.java.isAssignableFrom(x.type)) {
                targetEntityFields += x
            }
        }
        if (targetEntityFields.isEmpty())
            throw IllegalStateException("Cannot listen event ${event.name} : Event not contains \"org.bukkit.Entity\" implementing field")
        Bukkit.getPluginManager().registerEvent(event, EmptyListener, priority,
            { _, ev -> onEvent(ev) }, MicroGameLib.inst)
    }

    fun addListener(mtd: MethodInvoker, condition: (Entity) -> Boolean): EventInvoker {
        val invoker = EventInvoker(condition, mtd)
        listeners += invoker
        return invoker
    }

    fun onEvent(ev: Any) {
        if (!event.isAssignableFrom(ev.javaClass)) {
            throw IllegalStateException("Cannot call event ${event.name} : Object ${ev.javaClass.name} provided, Cannot cast object")
        }
        val eventArgument = ArgumentStorage()
        for (enField in targetEntityFields) {
            enField[ev]?.apply {
                eventArgument.addArgument(this)
            }
        }
        val entityList = eventArgument[Entity::class.java]
        if (entityList == null)
            return
        for (entity in entityList) {
            for ((x, y) in listeners) {
                if (x.invoke(entity as Entity)) {
                    y.invoke(eventArgument)
                }
            }
        }
    }

    object EmptyListener : Listener
}