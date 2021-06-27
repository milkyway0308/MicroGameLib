package skywolf46.microgamelib.api.stages

import skywolf46.microgamelib.annotations.Extract
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.data.GameInstanceObject
import skywolf46.microgamelib.inject.impl.GameParty

abstract class AbstractGameBase {
    @Inject
    lateinit var party: GameParty

    @Inject
    lateinit var instance: GameInstanceObject


}