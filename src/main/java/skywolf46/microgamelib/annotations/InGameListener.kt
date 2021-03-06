package skywolf46.microgamelib.annotations

import org.bukkit.event.EventPriority

/**
 * Dynamically register listener.
 * Cause [InGameListener] work with class-depth based storage, all extended event will called with injected handler.
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
// TODO Use includeEntity parameter to process non-polayer entity
annotation class InGameListener(val priority: EventPriority = EventPriority.NORMAL, val includeEntity: Boolean = false)
