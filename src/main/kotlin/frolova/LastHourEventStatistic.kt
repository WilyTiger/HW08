package frolova

import frolova.time.Clock
import java.time.Instant

class LastHourEventStatistic(val clock: Clock) : EventStatistic {
    private val events: MutableMap<String, MutableList<Instant>> = mutableMapOf()
        get() = field.onEach { (_, timestamps) -> timestamps.dropOutdated() }

    private val hourMinutes = 60
    private val hourSeconds = 3600

    override fun incEvent(name: String) {
        events.getOrPut(name) { mutableListOf() } += clock.now()
    }

    override fun getEventStatisticByName(name: String): Double =
        clock.now().epochSecond.let { now ->
            events[name]
                ?.filter { now - hourSeconds <= it.epochSecond && it.epochSecond <= now }
                ?.let { it.size.toDouble() / hourMinutes } ?: 0.0
        }

    override fun getAllEventStatistic(): List<Pair<String, Double>> =
        events.keys.map { it to getEventStatisticByName(it) }

    override fun printStatistic() =
        getAllEventStatistic().forEach { (name, rpm) ->
            println("$name rpm = $rpm")
        }

    private fun MutableList<Instant>.dropOutdated() =
        clock.now().epochSecond.let { now ->
            val ind = indexOfFirst { it.epochSecond < now - hourSeconds }
            repeat(ind + 1) { removeFirst() }
        }
}
