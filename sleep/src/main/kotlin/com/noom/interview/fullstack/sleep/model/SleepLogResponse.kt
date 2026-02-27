package com.noom.interview.fullstack.sleep.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class SleepLogResponse(
    val sleepDate: LocalDate,
    val bedTime: LocalTime,
    val wakeTime: LocalTime,
    val totalTimeInBed: String,
    val morningFeeling: MorningFeeling
) {
    companion object {
        fun from(sleepLog: SleepLog): SleepLogResponse {
            return SleepLogResponse(
                sleepDate = sleepLog.sleepDate,
                bedTime = sleepLog.bedTime,
                wakeTime = sleepLog.wakeTime,
                totalTimeInBed = formatDuration(sleepLog.totalTimeInBed),
                morningFeeling = sleepLog.morningFeeling
            )
        }

        private fun formatDuration(duration: Duration): String {
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            return "${hours}h ${minutes}m"
        }
    }
}

