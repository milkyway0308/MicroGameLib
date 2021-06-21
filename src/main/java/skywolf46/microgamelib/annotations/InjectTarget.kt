package skywolf46.microgamelib.annotations

import skywolf46.microgamelib.enums.InjectScope

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class InjectTarget(val scope: InjectScope = InjectScope.GAME, val injectPriority: Int = Integer.MAX_VALUE)
