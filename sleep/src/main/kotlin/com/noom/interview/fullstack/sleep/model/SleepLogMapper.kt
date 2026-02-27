package com.noom.interview.fullstack.sleep.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/**
 * Converts a SleepLogRequest to a SleepLog.
 */
fun SleepLogRequest.toSleepLog(userId: Long): SleepLog {
    return SleepLog(
        id = null,
        userId = userId,
        sleepDate = LocalDate.now(),
        bedTime = bedTime,
        wakeTime = wakeTime,
        totalTimeInBed = calculateDuration(bedTime, wakeTime),
        morningFeeling = morningFeeling
    )
}

/**
 * Calculates duration between bedtime and wake time.
 * Handles overnight sleep (e.g., 23:00 to 07:00 = 8 hours).
 */
private fun calculateDuration(bedTime: LocalTime, wakeTime: LocalTime): Duration {
    return if (wakeTime.isAfter(bedTime)) {
        // Same day sleep (e.g., 01:00 to 09:00)
        Duration.between(bedTime, wakeTime)
    } else {
        // Overnight sleep (e.g., 23:00 to 07:00)
        val untilMidnight = Duration.between(bedTime, LocalTime.MAX).plusNanos(1)
        val afterMidnight = Duration.between(LocalTime.MIN, wakeTime)
        untilMidnight.plus(afterMidnight)
    }
}

