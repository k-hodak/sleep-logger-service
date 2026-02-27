package com.noom.interview.fullstack.sleep.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.noom.interview.fullstack.sleep.model.MorningFeeling
import com.noom.interview.fullstack.sleep.model.SleepAveragesResponse
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
import org.springframework.test.web.servlet.get
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
            bedTime = LocalTime.of(23, 0),
            wakeTime = LocalTime.of(7, 0),
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

    @Test
    fun `GET last-night should return sleep log when found`() {
        // Given
        val userId = 1L
        val sleepLog = SleepLog(
            id = 1L,
            userId = userId,
            sleepDate = LocalDate.of(2026, 2, 27),
            bedTime = LocalTime.of(23, 0),
            wakeTime = LocalTime.of(7, 0),
            totalTimeInBed = Duration.ofHours(8),
            morningFeeling = MorningFeeling.GOOD
        )
        whenever(sleepLogService.getLastNightSleepLog(userId)).thenReturn(sleepLog)

        // When & Then
        mockMvc.get("/api/sleep/last-night") {
            header("X-User-Id", userId)
        }.andExpect {
            status { isOk() }
            jsonPath("$.sleepDate") { value("2026-02-27") }
            jsonPath("$.bedTime") { value("23:00:00") }
            jsonPath("$.wakeTime") { value("07:00:00") }
            jsonPath("$.totalTimeInBed") { value("8h 0m") }
            jsonPath("$.morningFeeling") { value("GOOD") }
        }
    }

    @Test
    fun `GET last-night should return 404 when not found`() {
        // Given
        val userId = 1L
        whenever(sleepLogService.getLastNightSleepLog(userId)).thenReturn(null)

        // When & Then
        mockMvc.get("/api/sleep/last-night") {
            header("X-User-Id", userId)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `GET averages should return averages when data exists`() {
        // Given
        val userId = 1L
        val averages = SleepAveragesResponse(
            dateRangeStart = LocalDate.of(2026, 1, 28),
            dateRangeEnd = LocalDate.of(2026, 2, 26),
            averageTotalTimeInBed = "7h 30m",
            averageBedTime = LocalTime.of(23, 15),
            averageWakeTime = LocalTime.of(6, 45),
            morningFeelingFrequencies = mapOf(
                MorningFeeling.GOOD to 15,
                MorningFeeling.OK to 10,
                MorningFeeling.BAD to 5
            )
        )
        whenever(sleepLogService.getLast30DaysAverages(userId)).thenReturn(averages)

        // When & Then
        mockMvc.get("/api/sleep/averages") {
            header("X-User-Id", userId)
        }.andExpect {
            status { isOk() }
            jsonPath("$.dateRangeStart") { value("2026-01-28") }
            jsonPath("$.dateRangeEnd") { value("2026-02-26") }
            jsonPath("$.averageTotalTimeInBed") { value("7h 30m") }
            jsonPath("$.averageBedTime") { value("23:15:00") }
            jsonPath("$.averageWakeTime") { value("06:45:00") }
            jsonPath("$.morningFeelingFrequencies.GOOD") { value(15) }
            jsonPath("$.morningFeelingFrequencies.OK") { value(10) }
            jsonPath("$.morningFeelingFrequencies.BAD") { value(5) }
        }
    }

    @Test
    fun `GET averages should return 404 when no data exists`() {
        // Given
        val userId = 1L
        whenever(sleepLogService.getLast30DaysAverages(userId)).thenReturn(null)

        // When & Then
        mockMvc.get("/api/sleep/averages") {
            header("X-User-Id", userId)
        }.andExpect {
            status { isNotFound() }
        }
    }
}
