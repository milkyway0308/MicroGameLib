package skywolf46.microgamelib.storage

import org.bukkit.entity.Entity
import org.bukkit.event.Event
import skywolf46.extrautility.data.ArgumentStorage
import skywolf46.extrautility.util.ClassUtil.iterateParentClasses
import skywolf46.extrautility.util.ConstructorInvoker
import skywolf46.extrautility.util.get
import skywolf46.microgamelib.annotations.Extract
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.data.*
import skywolf46.microgamelib.enums.InjectScope
import java.lang.reflect.Field
import kotlin.Exception

class InjectReference : ArgumentStorage() {
    companion object {
        private val storages = mutableMapOf<Class<*>, CachedInjectTargets>()
        private val storagesListener = mutableMapOf<Class<*>, CachedInGameListeners>()
    }

    val injectedListeners = mutableListOf<EventInvoker>()
    val transferTarget = mutableListOf<Any>()
    val transferListener = mutableListOf<EventInvoker>()

    fun inject(topParent: ArgumentStorage?, stage: GameInstanceObject?, invoker: List<ConstructorInvoker>) {
        // Temporary add proxy to get all values from project
        if (topParent != null)
            addProxy(topParent)
        val afterCall = mutableListOf<Any>()
        for (x in invoker) {
            try {
                val created = x.call(this)!!
                injectTo(created)
                addArgument(created)
                afterCall += created
            } catch (e: Exception) {
                System.err.println("Failed to create instance of ${x.constructor.declaringClass.name} : ${e.javaClass.name} (${e.message})")
                e.printStackTrace()
            }
        }
        // Register listener
        for (x in afterCall) {
            registerAllListeners(x, this, stage)
        }
    }

    fun registerAllListeners(
        target: Any,
        args: InjectReference,
        stage: GameInstanceObject?,
    ): List<EventInvoker> {
        return storagesListener.computeIfAbsent(target.javaClass) {
            CachedInGameListeners(it)
        }.register(args, target) {
            if (stage != null) {
                return@register it.hasMetadata("[MGLib] Game") && it.get<String>("[MGLib] Game") == stage.instanceName
            } else return@register true
        }
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
                if (list.isEmpty()) {
                    continue
                }
                for (count in 0 until y.size.coerceAtLeast(list.size)) {
                    y[count].set(target, list[0])
                }
            }
        }
    }

    fun unregisterAllListener() {
        injectedListeners.forEach { x ->
            x.unregister()
        }
    }

    fun extractTo(target: InjectReference) {
// TODO : add @Extract transfer
//        for (x in transferTarget) {
//            target.addArgument(x)
//        }
    }

    private class CachedInGameListeners(val cls: Class<*>) {
        // LifeCycle, Prepare
        val currentInvoker = mutableMapOf<InjectScope, MutableList<EventInvokerReady>>()
        val extractFields = mutableListOf<Pair<Extract, Field>>()

        init {
            for (x in cls.declaredMethods) {
                x.getAnnotation(InGameListener::class.java)?.apply {
                    try {
                        x.isAccessible = true
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
            for (x in cls.declaredFields) {
                x.getAnnotation(Extract::class.java)?.apply {
                    x.isAccessible = true
                    extractFields.add(this to x)
                }
            }
        }

        fun getScopedList(target: InjectScope): MutableList<EventInvokerReady> {
            return currentInvoker.computeIfAbsent(target) { mutableListOf() }
        }

        fun register(
            ref: InjectReference,
            instance: Any,
            condition: (Entity) -> Boolean,
        ): List<EventInvoker> {
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
            extractFields.forEach { x ->
                ref.registerAllListeners(x.second.get(instance), ref, ref[GameInstanceObject::class.java].run {
                    if (isEmpty())
                        null
                    else
                        get(0)
                })
            }
        }

        private fun attachGlobalListener(
            ref: InjectReference,
            instance: Any,
            condition: (Entity) -> Boolean,
        ) {
            if (InjectScope.GLOBAL in currentInvoker) {
                for (y in currentInvoker[InjectScope.GLOBAL]!!) {
                    InjectorClassManagerStorage.globalVariable.registerListener(y.register(instance, condition, {
                        addArgument(ref)
                        addArgument(instance)
                    }) {
                        InjectorClassManagerStorage.globalVariable.injectedListeners -= this
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
            val watcher = ref[GameInstanceWatcher::class].apply {
                if (isEmpty()) {
                    return
                }
            }[0]
            // No double initialize
            if (watcher.isInitialized(cls))
                return
            watcher.watchInitialized(cls)

            if (InjectScope.GAME in currentInvoker) {
                val proxy = (ref.getProxies()[0] as InjectReference)
                for (y in currentInvoker[InjectScope.GAME]!!) {
                    proxy.registerListener(y.register(instance, condition, {
                        addArgument(ref)
                        addArgument(instance)
                    }) {
                        proxy.injectedListeners -= this
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
                    ref.registerListener(y.register(instance, condition, {
                        addArgument(ref)
                        addArgument(instance)
                    }) {
                        ref.injectedListeners -= this
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
        val extractFields = mutableMapOf<InjectScope, MutableList<Pair<Class<*>, Extract>>>()

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