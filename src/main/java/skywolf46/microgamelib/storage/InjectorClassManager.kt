package skywolf46.microgamelib.storage

import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.ClassUtil
import skywolf46.extrautility.util.ClassUtil.iterateParentClasses
import skywolf46.extrautility.util.ConstructorInvoker
import skywolf46.extrautility.util.PriorityReference

class InjectorClassManager : ArrayList<PriorityReference<Class<*>>>() {

    override fun add(element: PriorityReference<Class<*>>): Boolean {
        val added = super.add(element)
        sort()
        return added
    }


    fun applyReferences(storageParent: InjectReference?, storage: InjectReference, debug: Boolean = false) {
        storage.inject(storageParent, kotlin.run {
            val lst = mutableListOf<ConstructorInvoker>()
            for (x in this) {
                lst.add(ConstructorInvoker(x.data.constructors[0]))
                if (debug)
                    println("§e--- Constructed reference ${x.data.name}")
            }
            return@run lst
        })
    }

}