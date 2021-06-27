package skywolf46.microgamelib.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class GameInstance(
    val value: String,
    val alwaysStarted: Boolean = false,
    val allowMultiInstance: Boolean = false,
) {
}