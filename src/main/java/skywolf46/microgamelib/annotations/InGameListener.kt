package skywolf46.microgamelib.annotations

import org.bukkit.event.EventPriority

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class InGameListener(val priority: EventPriority = EventPriority.NORMAL)
