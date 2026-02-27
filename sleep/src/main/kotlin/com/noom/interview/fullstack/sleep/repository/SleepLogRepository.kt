package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SleepLogRepository : JpaRepository<SleepLog, Long> {

    fun findTopByOrderBySleepDateDesc(): SleepLog?

    fun findByUserIdAndSleepDate(userId: Long, sleepDate: LocalDate): SleepLog?

    fun findByUserIdAndSleepDateBetweenOrderBySleepDateAsc(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SleepLog>

    /**
     * Calculates sleep averages using PostgreSQL.
     * Returns null if no records found in the date range.
     */
    @Query("""
        SELECT 
            MIN(sleep_date) as startDate,
            MAX(sleep_date) as endDate,
            AVG(total_time_in_bed) as avgTotalMinutes,
            AVG(EXTRACT(HOUR FROM bed_time) * 60 + EXTRACT(MINUTE FROM bed_time) + 
                CASE WHEN EXTRACT(HOUR FROM bed_time) < 18 THEN 1440 ELSE 0 END) as avgBedTimeMinutes,
            AVG(EXTRACT(HOUR FROM wake_time) * 60 + EXTRACT(MINUTE FROM wake_time)) as avgWakeTimeMinutes,
            COUNT(*) FILTER (WHERE morning_feeling = 'BAD') as badCount,
            COUNT(*) FILTER (WHERE morning_feeling = 'OK') as okCount,
            COUNT(*) FILTER (WHERE morning_feeling = 'GOOD') as goodCount
        FROM sleep_log 
        WHERE user_id = :userId 
        AND sleep_date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    fun findAveragesByUserIdAndDateRange(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): SleepAveragesProjection?
}
