package skywolf46.microgamelib.data

import skywolf46.extrautility.util.ConstructorInvoker
import skywolf46.extrautility.util.MethodInvoker
import skywolf46.extrautility.util.MethodUtil
import skywolf46.microgamelib.annotations.InGameListener
import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException
import java.lang.reflect.Field

class GameStageData(val target: Class<*>, val gameName: String, val stageName: String) {
    val innerListeners = mutableListOf<MethodInvoker>()
    val innerFinalizer = mutableListOf<MethodInvoker>()
    val innerInjects = mutableListOf<Field>()
    val constructedInvoker: ConstructorInvoker

    init {
        try {
            constructedInvoker = ConstructorInvoker(target.constructors[0])
        } catch (_: IndexOutOfBoundsException) {
            System.err.println("Cannot load stage ${target.name} : All constructor is private")
            throw IllegalStateException()
        }
        val filter = MethodUtil.filter(target)
        filter.filter(InGameListener::class.java).methods.forEach {
            innerListeners += MethodInvoker(it)
        }
    }
}