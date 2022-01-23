package frolova.time

import java.time.Instant

class NormalClock : Clock {
    override fun now(): Instant = Instant.now()
}