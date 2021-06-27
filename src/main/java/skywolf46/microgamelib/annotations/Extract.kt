package skywolf46.microgamelib.annotations

import skywolf46.microgamelib.enums.InjectScope

/**
 * Extract annotated field to target scope when stage ended.
 * [InjectScope.STAGE] will not work cause STAGE argument remove when stage finished.
 *
 * if [extractLast] is false, object will be extracted when object destroyed.
 * As example, this code will extract / register annotated object when object constructed, and destroy after stage complete.
 *       @Extract(scope = InjectScope.STAGE, extractLast = false)
 *      private lateinit var test : TestObject
 *
 * But if [extractLast] is true, object will be extract when stage complete, and remain on next stage.
 *      @Extract(scope = InjectScope.STAGE, extractLast = true)
 *      private lateinit var test : TestObject
 *
 * If scope selected to [InjectScope.GLOBAL], object will not be extracted, and it will be re-constructed / registered every time.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Extract(val scope: InjectScope = InjectScope.STAGE, val extractLast: Boolean = true) {

}