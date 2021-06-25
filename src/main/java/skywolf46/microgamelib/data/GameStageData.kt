package skywolf46.microgamelib.data

import skywolf46.extrautility.util.ClassUtil.iterateParentClasses
import skywolf46.extrautility.util.ConstructorInvoker
import skywolf46.extrautility.util.MethodInvoker
import skywolf46.extrautility.util.MethodUtil
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.storage.InjectReference
import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException
import java.lang.reflect.Field

class GameStageData(val instance: GameInstanceData, val stageName: String, val target: Class<*>) {
    val innerFinalizer = mutableListOf<MethodInvoker>()
    val constructedInvoker: ConstructorInvoker

    init {
        try {
            constructedInvoker = ConstructorInvoker(target.constructors[0])
        } catch (_: IndexOutOfBoundsException) {
            System.err.println("Cannot load stage ${target.name} : All constructor is private")
            throw IllegalStateException()
        }
        val filter = MethodUtil.filter(*mutableListOf<Class<*>>().apply {
            target.iterateParentClasses {
                add(this)
            }
        }.toTypedArray())
        filter.filter(InGameListener::class.java).methods.forEach {
            innerListeners += MethodInvoker(it)
            println("Registered ${it.method.declaringClass.name}#${it.method.name}")
        }
    }

    fun constructStage(argument: InjectReference): Any {
        val data = constructedInvoker.call(argument)
        // Add global proxy.
        argument.injectTo(data!!)
        return data
    }
}