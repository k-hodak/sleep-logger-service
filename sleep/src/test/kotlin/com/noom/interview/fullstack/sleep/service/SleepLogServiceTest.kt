package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.MorningFeeling
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepLogRequest
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
class SleepLogServiceTest {

    @Mock
    private lateinit var sleepLogRepository: SleepLogRepository

    @InjectMocks
    private lateinit var sleepLogService: SleepLogService

    @Test
    fun `createSleepLog should convert request and save to repository`() {
        // Given
        val userId = 1L
        val request = SleepLogRequest(
            bedTime = LocalTime.of(23, 0),
            wakeTime = LocalTime.of(7, 30),
            morningFeeling = MorningFeeling.GOOD
        )
        val captor = argumentCaptor<SleepLog>()
        whenever(sleepLogRepository.save(captor.capture())).thenAnswer {
            captor.firstValue.copy(id = 1L)
        }

        // When
        val result = sleepLogService.createSleepLog(userId, request)

        // Then
        assertNotNull(result.id)
        assertEquals(1L, result.id)

        val savedLog = captor.firstValue
        assertEquals(userId, savedLog.userId)
        assertEquals(LocalDate.now(), savedLog.sleepDate)
        assertEquals(LocalTime.of(23, 0), savedLog.bedTime)
        assertEquals(LocalTime.of(7, 30), savedLog.wakeTime)
        assertEquals(Duration.ofHours(8).plusMinutes(30), savedLog.totalTimeInBed)
        assertEquals(MorningFeeling.GOOD, savedLog.morningFeeling)
    }

    @Test
    fun `getLastNightSleepLog should return sleep log when found`() {
        // Given
        val userId = 1L
        val today = LocalDate.now()
        val sleepLog = SleepLog(
            id = 1L,
            userId = userId,
            sleepDate = today,
            bedTime = LocalTime.of(23, 0),
            wakeTime = LocalTime.of(7, 0),
            totalTimeInBed = Duration.ofHours(8),
            morningFeeling = MorningFeeling.GOOD
        )
        whenever(sleepLogRepository.findByUserIdAndSleepDate(userId, today)).thenReturn(sleepLog)

        // When
        val result = sleepLogService.getLastNightSleepLog(userId)

        // Then
        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals(userId, result?.userId)
        assertEquals(today, result?.sleepDate)
    }

    @Test
    fun `getLastNightSleepLog should return null when not found`() {
        // Given
        val userId = 1L
        val today = LocalDate.now()
        whenever(sleepLogRepository.findByUserIdAndSleepDate(userId, today)).thenReturn(null)

        // When
        val result = sleepLogService.getLastNightSleepLog(userId)

        // Then
        assertNull(result)
    }

    @Test
    fun `getLast30DaysAverages should return averages when data exists`() {
        // Given
        val userId = 1L
        val today = LocalDate.now()
        val sleepLogs = listOf(
            createSleepLog(today.minusDays(2), userId, LocalTime.of(23, 0), LocalTime.of(7, 0), Duration.ofHours(8), MorningFeeling.GOOD),
            createSleepLog(today.minusDays(1), userId, LocalTime.of(23, 30), LocalTime.of(7, 30), Duration.ofHours(8), MorningFeeling.OK),
            createSleepLog(today, userId, LocalTime.of(22, 30), LocalTime.of(6, 30), Duration.ofHours(8), MorningFeeling.GOOD)
        )
        whenever(sleepLogRepository.findByUserIdAndSleepDateBetweenOrderBySleepDateAsc(
            eq(userId),
            any(),
            any()
        )).thenReturn(sleepLogs)

        // When
        val result = sleepLogService.getLast30DaysAverages(userId)

        // Then
        assertNotNull(result)
        assertEquals(today.minusDays(2), result?.dateRangeStart)
        assertEquals(today, result?.dateRangeEnd)
        assertEquals("8h 0m", result?.averageTotalTimeInBed)
        assertEquals(mapOf(MorningFeeling.GOOD to 2, MorningFeeling.OK to 1), result?.morningFeelingFrequencies)
    }

    @Test
    fun `getLast30DaysAverages should return null when no data exists`() {
        // Given
        val userId = 1L
        whenever(sleepLogRepository.findByUserIdAndSleepDateBetweenOrderBySleepDateAsc(
            eq(userId),
            any(),
            any()
        )).thenReturn(emptyList())

        // When
        val result = sleepLogService.getLast30DaysAverages(userId)

        // Then
        assertNull(result)
    }


    private fun createSleepLog(
        sleepDate: LocalDate,
        userId: Long,
        bedTime: LocalTime,
        wakeTime: LocalTime,
        totalTimeInBed: Duration,
        morningFeeling: MorningFeeling
    ): SleepLog {
        return SleepLog(
            id = 1L,
            userId = userId,
            sleepDate = sleepDate,
            bedTime = bedTime,
            wakeTime = wakeTime,
            totalTimeInBed = totalTimeInBed,
            morningFeeling = morningFeeling
        )
    }
}
