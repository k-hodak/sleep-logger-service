package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepLogRequest
import com.noom.interview.fullstack.sleep.model.toSleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
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
}
