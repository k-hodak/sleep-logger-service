package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepLogRequest
import com.noom.interview.fullstack.sleep.model.toSleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SleepLogService(private val sleepLogRepository: SleepLogRepository) {

    private val logger = LoggerFactory.getLogger(SleepLogService::class.java)

    fun createSleepLog(userId: Long, request: SleepLogRequest): SleepLog {
        logger.info("Creating sleep log for user: {}", userId)
        val sleepLog = request.toSleepLog(userId)
        logger.debug("Sleep log entity: userId={}, sleepDate={}, bedTime={}, wakeTime={}, totalTime={}, feeling={}",
            sleepLog.userId, sleepLog.sleepDate, sleepLog.bedTime, sleepLog.wakeTime, sleepLog.totalTimeInBed, sleepLog.morningFeeling)
        val saved = sleepLogRepository.save(sleepLog)
        logger.info("Saved sleep log with id: {} for user: {}", saved.id, userId)
        return saved
    }
}
