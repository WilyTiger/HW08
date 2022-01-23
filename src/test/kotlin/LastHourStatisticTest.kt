import org.junit.jupiter.api.Test
import frolova.LastHourEventStatistic
import frolova.time.SettableClock
import java.time.Instant
import kotlin.math.abs

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.math.max

class LastHourStatisticTest {
    private val zeroClock = SettableClock(Instant.ofEpochSecond(0))
    private val zeroLastHourEventStatistic = LastHourEventStatistic(zeroClock)
    private val eps = 1e-6

    private fun equal(a: Double, b: Double): Boolean = abs(a - b) < eps

    @Test
    fun testOneEventManyTimesStatisticsByName() = zeroLastHourEventStatistic.run {
        val event = "event"
        (1..100).forEach {
            incEvent(event)
            assertTrue(equal(it / 60.0, getEventStatisticByName(event)))
        }
    }

    @Test
    fun testTenEventsTenTimesUniformStatisticsByName() = zeroLastHourEventStatistic.run {
        val events = List(10) { "event$it" }
        (1..10).forEach {
            events.forEach { event ->
                incEvent(event)
                assertTrue(equal(it / 60.0, getEventStatisticByName(event)))
            }
        }
    }

    @Test
    fun testTenEventsManyTimesRandomStatisticsByName() = zeroLastHourEventStatistic.run {
        val events = List(10) { "event$it" }
        val eventsCount = mutableMapOf<String, Int>().apply {
            events.forEach { put(it, 0) }
        }
        repeat(100) {
            events.random().let { event ->
                incEvent(event)
                eventsCount.merge(event, 1, Int::plus)
                val count = eventsCount[event]
                assertTrue(equal(count!! / 60.0, getEventStatisticByName(event)))
            }
        }
    }

    @Test
    fun testOneEventChangingTimeLessThanHour() = zeroLastHourEventStatistic.run {
        val event = "event"
        (1..100L).forEach {
            incEvent(event)
            zeroClock.now = Instant.ofEpochSecond(it)
            assertTrue(equal(it / 60.0, getEventStatisticByName(event)))
        }
    }

    @Test
    fun testChangingTimeMoreThanHourRegularEvents() = zeroLastHourEventStatistic.run {
        val event = "event"
        (1..100L).forEach {
            incEvent(event)
            zeroClock.now = Instant.ofEpochSecond(60 * it)
            val rpm = getEventStatisticByName(event)
            if (it < 60) {
                assertEquals(it / 60.0, rpm)
            } else {
                assertEquals(1.0, rpm)
            }
        }
    }

    @Test
    fun testChangingTimeMoreThanHourEventsDuringFirstHour() = zeroLastHourEventStatistic.run {
        val event = "event"
        (1..150L).forEach {
            if (it <= 60) {
                incEvent(event)
            }
            zeroClock.now = Instant.ofEpochSecond(60 * it)
            if (clock.now().epochSecond <= 3600) {
                assertEquals(it / 60.0, getEventStatisticByName(event))
            } else {
                val lastHourEventsCount = max(120 - it, 0)
                assertEquals(lastHourEventsCount / 60.0, getEventStatisticByName(event))
            }
        }
    }
}
