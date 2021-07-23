package skywolf46.microgamelib.api.abstraction

import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.api.data.StageWinnerManager
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.inject.impl.GameParty

abstract class AbstractAttachableSystem : AbstractSystemBase(), IAttachableSystem {
    lateinit var unregister: () -> Unit

    @Inject
    lateinit var instance: GameInstanceObject

    @Inject
    lateinit var party: GameParty

    @Inject
    lateinit var winnerManager: StageWinnerManager

    override fun bindUnregisterLambda(unit: () -> Unit) {
        unregister = unit
    }

    fun unregister() {
        unregister.invoke()
    }


}