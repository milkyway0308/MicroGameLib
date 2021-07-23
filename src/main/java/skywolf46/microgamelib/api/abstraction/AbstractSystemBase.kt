package skywolf46.microgamelib.api.abstraction

import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.inject.util.ConditionScheduler

abstract class AbstractSystemBase {
    @Inject
    protected lateinit var condition: ConditionScheduler

    fun after(tick: Long, unit: () -> Unit) {
        condition.syncDelay(InjectScope.STAGE, tick, unit)
    }

    fun repeatAfter(period: Long, unit: () -> Unit) {
        condition.syncRepeat(InjectScope.STAGE, 0L, period, unit)
    }

    fun repeatAfter(delay: Long, period: Long, unit: () -> Unit) {
        condition.syncRepeat(InjectScope.STAGE, delay, period, unit)
    }
}