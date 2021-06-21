package skywolf46.microgamelib.storage

import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.ConstructorInvoker
import skywolf46.extrautility.util.PriorityReference
import java.lang.Exception

class InjectReference : ArgumentStorage() {

    fun inject(topParent: ArgumentStorage?, invoker: List<ConstructorInvoker>) {
        // Temporary add proxy to get all values from project
        if (topParent != null)
            addProxy(topParent)
        for (x in invoker) {
            try {
                x.call(this)
            } catch (e: Exception) {
                System.err.println("Failed to create instance of ${x.constructor.declaringClass.name} : ${e.javaClass.name} (${e.message})")
            }
        }
        // Cleanup proxy, bind to parent
        if (topParent != null) {
            removeProxy(topParent)
            topParent.addProxy(this)
        }
    }
}