package skywolf46.microgamelib.storage

import skywolf46.extrautility.util.PriorityReference
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.enums.InjectScope
import java.lang.IllegalStateException

object InjectorClassManagerStorage {
    private val scope = mutableMapOf<InjectScope, InjectorClassManager>()
    val globalVariable = InjectReference()

    fun addInject(cls: Class<*>) {
        val annotation = cls.getAnnotation(InjectTarget::class.java)
        if (annotation == null)
            throw IllegalStateException("Cannot register inject target ${cls.name} : Class not has \"InjectTarget\" annotation")
        scope.computeIfAbsent(annotation.scope) {
            InjectorClassManager()
        }.add(PriorityReference(cls, annotation.injectPriority))
    }

    fun finalizeGlobalInject() {
        (scope[InjectScope.GLOBAL] ?: InjectorClassManager()).applyReferences(null, globalVariable, true)
    }
}