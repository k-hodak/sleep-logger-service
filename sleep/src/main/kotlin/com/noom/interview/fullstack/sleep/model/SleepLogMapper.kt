package com.noom.interview.fullstack.sleep.model

import java.time.Duration
import java.time.LocalDate

/**
 * Converts a SleepLogRequest to a SleepLog.
 */
fun SleepLogRequest.toSleepLog(userId: Long): SleepLog {
    val bedTimestamp = parseBedTimestamp()
    val wakeTimestamp = parseWakeTimestamp()

    require(wakeTimestamp.isAfter(bedTimestamp)) {
        "Wake time must be after bed time."
    }

    return SleepLog(
        id = null,
        userId = userId,
        sleepDate = LocalDate.now(),  // Always the day of submission
        bedTime = bedTimestamp.toLocalTime(),
        wakeTime = wakeTimestamp.toLocalTime(),
        totalTimeInBed = Duration.between(bedTimestamp, wakeTimestamp),
        morningFeeling = morningFeeling
    )
}
