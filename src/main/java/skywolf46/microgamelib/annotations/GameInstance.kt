package skywolf46.microgamelib.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class GameInstance(val value: String, val allowMultiInstance: Boolean = false) {
}