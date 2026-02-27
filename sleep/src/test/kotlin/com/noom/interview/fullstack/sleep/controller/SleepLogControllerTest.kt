package com.noom.interview.fullstack.sleep.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.noom.interview.fullstack.sleep.model.MorningFeeling
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepLogRequest
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@WebMvcTest(SleepLogController::class)
class SleepLogControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var sleepLogService: SleepLogService

    @Test
    fun `POST should create sleep log and return 201`() {
        // Given
        val userId = 1L
        val request = SleepLogRequest(
            timeInBedInterval = "2026-02-25T23:00:00/2026-02-26T07:00:00",
            morningFeeling = MorningFeeling.GOOD
        )
        val savedLog = SleepLog(
            id = 1L,
            userId = userId,
            sleepDate = LocalDate.of(2026, 2, 26),
            bedTime = LocalTime.of(23, 0),
            wakeTime = LocalTime.of(7, 0),
            totalTimeInBed = Duration.ofHours(8),
            morningFeeling = MorningFeeling.GOOD
        )
        whenever(sleepLogService.createSleepLog(any(), any())).thenReturn(savedLog)

        // When & Then
        mockMvc.post("/api/sleep") {
            header("X-User-Id", userId)
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            content { string("") }
        }
    }
}

