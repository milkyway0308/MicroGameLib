package skywolf46.microgamelib.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
// 해당 메서드가 포함된 클래스가 소멸될 때 해당 메서드를 호출합니다.
annotation class Finalize(val priority: Int = 0)
