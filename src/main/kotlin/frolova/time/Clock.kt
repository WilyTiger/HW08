package frolova.time

import java.time.Instant

interface Clock {
    fun now(): Instant
}