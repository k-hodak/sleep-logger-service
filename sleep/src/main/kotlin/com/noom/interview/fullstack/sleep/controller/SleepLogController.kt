package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.model.SleepLogRequest
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        logger.info("Received request from user {} to create sleep log: {}", userId, request)
        sleepLogService.createSleepLog(userId, request)
        logger.info("Created sleep log for user: {}", userId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}

