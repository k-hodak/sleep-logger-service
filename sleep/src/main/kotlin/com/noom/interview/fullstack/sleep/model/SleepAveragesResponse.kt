package com.noom.interview.fullstack.sleep.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class SleepAveragesResponse(
    val dateRangeStart: LocalDate,
    val dateRangeEnd: LocalDate,
    val averageTotalTimeInBed: String,
    val averageBedTime: LocalTime,
    val averageWakeTime: LocalTime,
    val morningFeelingFrequencies: Map<MorningFeeling, Int>
) {
    companion object {
        fun from(sleepLogs: List<SleepLog>): SleepAveragesResponse {
            require(sleepLogs.isNotEmpty()) { "Cannot calculate averages from empty list" }

            return SleepAveragesResponse(
                dateRangeStart = sleepLogs.first().sleepDate,
                dateRangeEnd = sleepLogs.last().sleepDate,
                averageTotalTimeInBed = formatDuration(calculateAverageDuration(sleepLogs)),
                averageBedTime = calculateAverageBedTime(sleepLogs.map { it.bedTime }),
                averageWakeTime = calculateAverageWakeTime(sleepLogs.map { it.wakeTime }),
                morningFeelingFrequencies = calculateMorningFeelingFrequencies(sleepLogs)
            )
        }

        private fun calculateAverageDuration(sleepLogs: List<SleepLog>): Duration {
            val totalMinutes = sleepLogs.sumOf { it.totalTimeInBed.toMinutes() }
            return Duration.ofMinutes(totalMinutes / sleepLogs.size)
        }

        /**
         * Calculates average bedtime.
         *
         * Logic: Bed times typically range from evening (18:00) to early morning (05:59).
         * To average times that cross midnight, we shift times before 18:00 by +24 hours,
         * then normalize the result back to 24-hour format.
         *
         * Example: [23:00, 01:00] → [23h, 25h] → avg 24h → normalized 00:00
         */
        private fun calculateAverageBedTime(times: List<LocalTime>): LocalTime {
            val eveningThresholdMinutes = 18 * 60 // 18:00 = 1080 minutes
            val dayInMinutes = 24 * 60            // 24:00 = 1440 minutes

            val adjustedMinutes = times.map { time ->
                val minutes = time.hour * 60 + time.minute
                if (minutes < eveningThresholdMinutes) {
                    minutes + dayInMinutes
                } else {
                    minutes
                }
            }

            val averageMinutes = adjustedMinutes.average().toInt()

            // Normalize back to 24-hour format
            val normalizedMinutes = averageMinutes % dayInMinutes
            return LocalTime.of(normalizedMinutes / 60, normalizedMinutes % 60)
        }

        /**
         * Calculates average wake time.
         *
         * Logic: Wake times are typically in the morning (05:00-12:00).
         * Simple average works since they rarely cross midnight.
         */
        private fun calculateAverageWakeTime(times: List<LocalTime>): LocalTime {
            val totalMinutes = times.sumOf { it.hour * 60 + it.minute }
            val averageMinutes = totalMinutes / times.size
            return LocalTime.of(averageMinutes / 60, averageMinutes % 60)
        }

        private fun calculateMorningFeelingFrequencies(sleepLogs: List<SleepLog>): Map<MorningFeeling, Int> {
            return sleepLogs
                .groupingBy { it.morningFeeling }
                .eachCount()
        }

        private fun formatDuration(duration: Duration): String {
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            return "${hours}h ${minutes}m"
        }
    }
}

