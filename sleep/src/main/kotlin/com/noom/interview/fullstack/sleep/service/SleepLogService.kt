package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.MorningFeeling
import com.noom.interview.fullstack.sleep.model.SleepAveragesResponse
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepLogRequest
import com.noom.interview.fullstack.sleep.model.toSleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.util.Objects

@Service
class SleepLogService(private val sleepLogRepository: SleepLogRepository) {

    private val logger = LoggerFactory.getLogger(SleepLogService::class.java)

    fun createSleepLog(userId: Long, request: SleepLogRequest): SleepLog {
        logger.info("Creating new sleep log.")
        val sleepLog = request.toSleepLog(userId)
        val saved = sleepLogRepository.save(sleepLog)
        logger.info("Saved new sleep log.")
        return saved
    }

    fun getLastNightSleepLog(userId: Long): SleepLog? {
        val today = LocalDate.now()
        logger.info("Fetching last night's sleep log (date: {})", today)
        val sleepLog = sleepLogRepository.findByUserIdAndSleepDate(userId, today)
        if (Objects.isNull(sleepLog)) {
            logger.info("No sleep log found on date: {}", today)
        }
        return sleepLog
    }

    /**
     * Option 1: Kotlin calculates averages (current approach)
     * Fetches all records and calculates in JVM.
     */
    fun getLast30DaysAverages(userId: Long): SleepAveragesResponse? {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        logger.info("Fetching last 30 days averages (from {} to {})", startDate, endDate)

        val sleepLogs = sleepLogRepository.findByUserIdAndSleepDateBetweenOrderBySleepDateAsc(userId, startDate, endDate)

        if (sleepLogs.isEmpty()) {
            logger.info("No sleep logs found in the last 30 days")
            return null
        }

        logger.info("Found {} sleep logs", sleepLogs.size)
        return SleepAveragesResponse.from(sleepLogs)
    }

    /**
     * Option 2: PostgreSQL calculates averages (more efficient for large datasets)
     * Database does the aggregation, returns only the result.
     */
    fun getLast30DaysAveragesSQL(userId: Long): SleepAveragesResponse? {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        logger.info("Fetching last 30 days averages via SQL (from {} to {})", startDate, endDate)

        val projection = sleepLogRepository.findAveragesByUserIdAndDateRange(userId, startDate, endDate)

        // SQL aggregation returns a row even with no data (with nulls), so check startDate
        val startDateResult = projection?.getStartDate()
        if (startDateResult == null) {
            logger.info("No sleep logs found in the last 30 days")
            return null
        }

        logger.info("Found averages via SQL")

        return SleepAveragesResponse(
            dateRangeStart = startDateResult,
            dateRangeEnd = projection.getEndDate()!!,  // Safe: if startDate exists, endDate exists
            averageTotalTimeInBed = formatMinutes(projection.getAvgTotalMinutes()),
            averageBedTime = minutesToLocalTime(projection.getAvgBedTimeMinutes()),
            averageWakeTime = minutesToLocalTime(projection.getAvgWakeTimeMinutes()),
            morningFeelingFrequencies = mapOf(
                MorningFeeling.BAD to projection.getBadCount(),
                MorningFeeling.OK to projection.getOkCount(),
                MorningFeeling.GOOD to projection.getGoodCount()
            )
        )
    }

    private fun formatMinutes(totalMinutes: Double): String {
        val hours = (totalMinutes / 60).toInt()
        val minutes = (totalMinutes % 60).toInt()
        return "${hours}h ${minutes}m"
    }

    private fun minutesToLocalTime(minutes: Double): LocalTime {
        val normalizedMinutes = (minutes.toInt()) % (24 * 60)
        return LocalTime.of(normalizedMinutes / 60, normalizedMinutes % 60)
    }
}
