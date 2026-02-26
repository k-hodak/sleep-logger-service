package com.noom.interview.fullstack.sleep.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class SleepLog(
    val id: Long? = null,
    val sleepDate: LocalDate,
    val bedTime: LocalTime,
    val wakeTime: LocalTime,
    val totalTimeInBed: Duration,
    val morningFeeling: MorningFeeling
)

