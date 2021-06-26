package skywolf46.microgamelib.storage

import org.bukkit.entity.Entity
import org.bukkit.event.Event
import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.ClassUtil.iterateParentClasses
import skywolf46.extrautility.util.ConstructorInvoker
import skywolf46.microgamelib.annotations.Extract
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.data.DynamicEventListener
import skywolf46.microgamelib.data.EventInvoker
import skywolf46.microgamelib.data.EventInvokerReady
import skywolf46.microgamelib.data.GameInstanceWatcher
import skywolf46.microgamelib.enums.InjectScope
import java.lang.reflect.Field
import kotlin.Exception

class InjectReference : ArgumentStorage() {
    companion object {
        private val storages = mutableMapOf<Class<*>, CachedInjectTargets>()
    }

    val injectedListeners = mutableListOf<EventInvoker>()

    fun inject(topParent: ArgumentStorage?, invoker: List<ConstructorInvoker>) {
        // Temporary add proxy to get all values from project
        if (topParent != null)
            addProxy(topParent)
        for (x in invoker) {
            try {
                val created = x.call(this)
                injectTo(created!!)
                addArgument(created)
            } catch (e: Exception) {
                System.err.println("Failed to create instance of ${x.constructor.declaringClass.name} : ${e.javaClass.name} (${e.message})")
                e.printStackTrace()
            }
        }
        // Cleanup proxy, bind to parent
        if (topParent != null) {
            removeProxy(topParent)
            topParent.addProxy(this)
        }
        if (injectedListeners.isEmpty())
            registerAllListeners()
    }

    fun registerAllListeners(target: Any) {

    }

    override fun newInstance(): ArgumentStorage {
        return InjectReference()
    }

    override fun shallowCopy(shallowCopyProxy: Boolean): InjectReference {
        return super.shallowCopy(shallowCopyProxy) as InjectReference
    }

    override fun deepCopy(): InjectReference {
        return super.deepCopy() as InjectReference
    }

    fun getProxies() = ArrayList(proxies)

    fun injectTo(target: Any) {
        storages.computeIfAbsent(target.javaClass) {
            CachedInjectTargets(it)
        }.apply {
            for ((x, y) in injectFields) {
                val list = get(x)
                for (count in 0 until y.size.coerceAtLeast(list.size)) {
                    y[count].set(target, list[count])
                }
            }
        }
    }

    fun uninjectAllListener() {
        injectedListeners.forEach {
            it.unregister()
        }
    }


    private class CachedInGameListeners(val cls: Class<*>) {
        // LifeCycle, Prepare
        val currentInvoker = mutableMapOf<InjectScope, MutableList<EventInvokerReady>>()
        val fields = mutableListOf<Class<*>>()

        init {
            for (x in cls.methods) {
                x.getAnnotation(InGameListener::class.java)?.apply {
                    try {
                        getScopedList(InjectScope.STAGE)
                            .add(DynamicEventListener.eventOf(x.parameters[0].type as Class<Event>,
                                priority)
                                .create(x)
                            )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            for (x in cls.fields) {
                x.getAnnotation(Extract::class.java)?.apply {

                }
            }
        }

        fun getScopedList(target: InjectScope): MutableList<EventInvokerReady> {
            return currentInvoker.computeIfAbsent(target) { mutableListOf() }
        }

        fun register(ref: InjectReference, instance: Any, condition: (Entity) -> Boolean): List<EventInvoker> {
            val invokers = mutableListOf<EventInvoker>()
            register(ref, instance, condition, mutableListOf())
            return invokers
        }

        private fun register(
            ref: InjectReference,
            instance: Any,
            condition: (Entity) -> Boolean,
            alreadyWorked: MutableList<Class<*>>,
        ) {
            // To prevent looped register
            if (cls in alreadyWorked)
                return
            alreadyWorked += cls
            attachGlobalListener(ref, instance, condition)
            attachGameListener(ref, instance, condition)
            attachStageListener(ref, instance, condition)
        }

        private fun attachGlobalListener(
            ref: InjectReference,
            instance: Any,
            condition: (Entity) -> Boolean,
        ) {
            if (InjectScope.GLOBAL in currentInvoker) {
                for (y in currentInvoker[InjectScope.GLOBAL]!!) {
                    InjectorClassManagerStorage.globalVariable.registerListener(y.register(instance, condition) {
                        addArgument(ref)
                        addArgument(instance)
                    })
                }
                // Do not register twice
                currentInvoker.remove(InjectScope.GLOBAL)
            }
        }

        private fun attachGameListener(
            ref: InjectReference,
            instance: Any,
            condition: (Entity) -> Boolean,
        ) {
            val watcher = ref[GameInstanceWatcher::class][0]
            // No double initialize
            if (watcher.isInitialized(cls))
                return
            watcher.watchInitialized(cls)
            if (InjectScope.GAME in currentInvoker) {
                for (y in currentInvoker[InjectScope.GAME]!!) {
                    (ref.getProxies()[0] as InjectReference).registerListener(y.register(instance, condition) {
                        addArgument(ref)
                        addArgument(instance)
                    })
                }
            }
        }

        private fun attachStageListener(
            ref: InjectReference,
            instance: Any,
            condition: (Entity) -> Boolean,
        ) {
            if (InjectScope.STAGE in currentInvoker) {
                for (y in currentInvoker[InjectScope.STAGE]!!) {
                    ref.registerListener(y.register(instance, condition) {
                        addArgument(ref)
                        addArgument(instance)
                    })
                }
            }
        }
    }

    private fun registerListener(listener: EventInvoker) {
        injectedListeners += listener
    }

    private class CachedInjectTargets(cls: Class<*>) {
        val injectFields = mutableMapOf<Class<*>, MutableList<Field>>()

        init {
            cls.iterateParentClasses {
                for (x in this.declaredFields) {
                    x.getAnnotation(Inject::class.java)?.apply {
                        injectFields.computeIfAbsent(x.type) { mutableListOf() }.add(x)
                    }
                }
            }
        }


    }
}