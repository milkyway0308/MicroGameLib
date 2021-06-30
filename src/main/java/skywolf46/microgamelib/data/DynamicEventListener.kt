package skywolf46.microgamelib.data

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import skywolf46.commandannotation.kotlin.data.Arguments
import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.ClassUtil.iterateParentClasses
import skywolf46.microgamelib.MicroGameLib
import java.lang.IllegalStateException
import java.lang.reflect.Field
import java.lang.reflect.Method

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
        event.iterateParentClasses {
            for (x in declaredFields) {
                if (Entity::class.java.isAssignableFrom(x.type)) {
                    x.isAccessible = true
                    targetEntityFields += x
                }
            }
        }
//        if (targetEntityFields.isEmpty())
//            throw IllegalStateException("Cannot listen event ${event.name} : Event not contains \"org.bukkit.Entity\" implementing field")
        Bukkit.getPluginManager().registerEvent(event, EmptyListener, priority,
            { _, ev -> onEvent(ev) }, MicroGameLib.inst)
    }

    fun create(mtd: Method): EventInvokerReady {
        return EventInvokerReady(mtd, {
            listeners += this
        }) {
            listeners -= this
        }
    }

    fun onEvent(ev: Any) {
        if (!event.isAssignableFrom(ev.javaClass)) {
            throw IllegalStateException("Cannot call event ${event.name} : Object ${ev.javaClass.name} provided, Cannot cast object")
        }
        val eventArgument = ArgumentStorage()
        eventArgument.addArgument(ev)
        if (targetEntityFields.isEmpty()) {
            for (y in ArrayList(listeners))
                y.method.invoke(eventArgument)
        } else {
            for (enField in targetEntityFields) {
                enField[ev]?.apply {
                    eventArgument.addArgument(this)
                }
            }
            val entityList = eventArgument[Entity::class.java]
            listeners.filter {
                for (x in entityList)
                    if (x !is Player || it.condition.invoke(x))
                        return@filter true
                return@filter false
            }.forEach {
                it.method.invoke(eventArgument)
            }
        }
    }

    object EmptyListener : Listener
}