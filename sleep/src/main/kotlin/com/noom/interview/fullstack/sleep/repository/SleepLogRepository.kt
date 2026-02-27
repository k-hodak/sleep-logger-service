package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SleepLogRepository : JpaRepository<SleepLog, Long> {

    fun findTopByOrderBySleepDateDesc(): SleepLog?

    fun findByUserIdAndSleepDate(userId: Long, sleepDate: LocalDate): SleepLog?
}
