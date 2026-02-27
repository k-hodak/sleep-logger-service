package com.noom.interview.fullstack.sleep.repository

import java.time.LocalDate

/**
 * Projection interface for sleep averages calculated by PostgreSQL.
 *
 * Note: getStartDate() and getEndDate() return null when no records exist
 * because SQL aggregation still returns a row with NULL values.
 */
interface SleepAveragesProjection {
    fun getStartDate(): LocalDate?
    fun getEndDate(): LocalDate?
    fun getAvgTotalMinutes(): Double
    fun getAvgBedTimeMinutes(): Double
    fun getAvgWakeTimeMinutes(): Double
    fun getBadCount(): Int
    fun getOkCount(): Int
    fun getGoodCount(): Int
}

