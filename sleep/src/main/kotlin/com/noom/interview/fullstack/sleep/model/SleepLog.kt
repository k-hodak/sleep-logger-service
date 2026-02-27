package com.noom.interview.fullstack.sleep.model

import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "sleep_log")
data class SleepLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "sleep_date", nullable = false)
    val sleepDate: LocalDate,

    @Column(name = "bed_time", nullable = false)
    val bedTime: LocalTime,

    @Column(name = "wake_time", nullable = false)
    val wakeTime: LocalTime,

    @Column(name = "total_time_in_bed", nullable = false)
    val totalTimeInBed: Duration,

    @Column(name = "morning_feeling", nullable = false)
    @Enumerated(EnumType.STRING)
    val morningFeeling: MorningFeeling
)
