package com.noom.interview.fullstack.sleep.model

import java.time.LocalDateTime

/**
 * Expecting frontend to send a single ISO 8601 interval string for the sleep period.
 *
 * Example request:
 * {
 *   "timeInBedInterval": "2026-02-25T23:30:00/2026-02-26T07:00:00",
 *   "morningFeeling": "GOOD"
 * }
 */
data class SleepLogRequest(
    val timeInBedInterval: String,           // ISO 8601 interval: "startDateTime/endDateTime"
    val morningFeeling: MorningFeeling
) {
    fun parseBedTimestamp(): LocalDateTime {
        val parts = timeInBedInterval.split("/")
        require(parts.size == 2) { "Invalid interval format. Expected: startDateTime/endDateTime" }
        return LocalDateTime.parse(parts[0])
    }

    fun parseWakeTimestamp(): LocalDateTime {
        val parts = timeInBedInterval.split("/")
        require(parts.size == 2) { "Invalid interval format. Expected: startDateTime/endDateTime" }
        return LocalDateTime.parse(parts[1])
    }
}

