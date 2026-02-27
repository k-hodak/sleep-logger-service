package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.model.SleepLogRequest
import com.noom.interview.fullstack.sleep.model.SleepLogResponse
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sleep")
class SleepLogController(private val sleepLogService: SleepLogService) {

    private val logger = LoggerFactory.getLogger(SleepLogController::class.java)

    @PostMapping
    fun createSleepLog(
        @RequestHeader("X-User-Id") userId: Long,
        @RequestBody request: SleepLogRequest
    ): ResponseEntity<Void> {
        logger.info("Received request to create sleep log: {}", request)
        sleepLogService.createSleepLog(userId, request)
        logger.info("Created new sleep log.")
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/last-night")
    fun getLastNightSleepLog(
        @RequestHeader("X-User-Id") userId: Long
    ): ResponseEntity<SleepLogResponse> {
        logger.info("Received request to get last night's sleep log.")
        val sleepLog = sleepLogService.getLastNightSleepLog(userId)
        return if (sleepLog != null) {
            logger.info("Returning last night's sleep log.")
            ResponseEntity.ok(SleepLogResponse.from(sleepLog))
        } else {
            logger.info("No sleep log found.")
            ResponseEntity.notFound().build()
        }
    }
}
