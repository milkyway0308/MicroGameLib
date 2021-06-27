package skywolf46.microgamelib.inject.util

import org.bukkit.scheduler.BukkitRunnable
import skywolf46.extrautility.util.schedule
import skywolf46.microgamelib.MicroGameLib
import skywolf46.microgamelib.annotations.InjectTarget
import skywolf46.microgamelib.enums.InjectScope

@InjectTarget(InjectScope.GAME)
class ConditionScheduler {
    private val scopeMap = mutableMapOf<InjectScope, MutableList<BukkitRunnable>>()

    fun syncDelay(unit: () -> Unit) {
        schedule(unit)
    }

    fun syncDelay(scope: InjectScope, delay: Long, unit: () -> Unit) {
        if (delay <= 0)
            syncDelay(unit)
        else {
            BukkitRunnableImpl {
                scopeOf(scope) -= this
            }.apply {
                scopeOf(scope) += this
                runTaskLater(MicroGameLib.inst, delay)
            }
        }
    }

    fun scopeOf(scope: InjectScope) = scopeMap.computeIfAbsent(scope) { mutableListOf() }

    class BukkitRunnableImpl(val unit: BukkitRunnableImpl.() -> Unit) : BukkitRunnable() {
        override fun run() {
            unit(this)
        }
    }

}