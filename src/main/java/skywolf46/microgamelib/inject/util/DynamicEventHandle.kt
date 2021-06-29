package skywolf46.microgamelib.inject.util

import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.storage.InjectReference
import skywolf46.microgamelib.storage.InjectorClassManagerStorage

@InjectTarget(InjectScope.GAME)
class DynamicEventHandle {

    @Inject
    private lateinit var gameInstance: GameInstanceObject

    fun bindListenerTo(scope: InjectScope, instance: Any): () -> Unit {
        val list = when (scope) {
            InjectScope.STAGE -> gameInstance.stageArgument!!
            InjectScope.GAME ->  gameInstance.stageArgument!!.getProxies()[0] as InjectReference
            InjectScope.GLOBAL -> InjectorClassManagerStorage.globalVariable
        }.registerAllListeners(instance,  gameInstance.stageArgument!!, gameInstance)
        return {
            for (x in list)
                x.unregister()
        }
    }
}