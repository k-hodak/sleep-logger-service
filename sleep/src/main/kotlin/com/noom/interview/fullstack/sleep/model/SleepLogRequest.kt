package com.noom.interview.fullstack.sleep.model

import java.time.LocalTime

/**
 * Request to log sleep data.
 *
 * Example request:
 * {
 *   "bedTime": "23:30",
 *   "wakeTime": "07:00",
 *   "morningFeeling": "GOOD"
 * }
 */
data class SleepLogRequest(
    val bedTime: LocalTime,
    val wakeTime: LocalTime,
    val morningFeeling: MorningFeeling
)

