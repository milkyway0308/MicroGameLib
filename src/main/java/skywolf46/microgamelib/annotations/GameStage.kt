package skywolf46.microgamelib.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class GameStage(val value: String, val gameName: String, val stagePriority: Int)