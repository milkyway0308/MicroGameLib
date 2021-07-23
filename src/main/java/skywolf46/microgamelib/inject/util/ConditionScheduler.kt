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
            BukkitRunnableImpl({
                scopeOf(scope) -= this
            }, unit).apply {
                scopeOf(scope) += this
                runTaskLater(MicroGameLib.inst, delay)
            }
        }
    }


    fun syncRepeat(scope: InjectScope, delay: Long, period: Long, unit: () -> Unit) {
        BukkitRunnableImpl({
            scopeOf(scope) -= this
        }, unit).apply {
            scopeOf(scope) += this
            runTaskTimer(MicroGameLib.inst, delay, period)
        }
    }

    fun scopeOf(scope: InjectScope) = scopeMap.computeIfAbsent(scope) { mutableListOf() }

    internal fun endScope(scope: InjectScope) {
        scopeMap.remove(scope)?.toList()?.forEach {
            it.cancel()
        }
    }


    inner class BukkitRunnableImpl(val unregisterer: BukkitRunnableImpl.() -> Unit, val unit: () -> Unit) :
        BukkitRunnable() {
        override fun run() {
            unit()
            unregisterer(this)
        }

        override fun cancel() {
            super.cancel()
            unregisterer(this)
        }
    }

}