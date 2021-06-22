package skywolf46.microgamelib.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class TargetType(vararg val leftType: KClass<*>)