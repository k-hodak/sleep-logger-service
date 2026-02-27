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
import org.mockito.kotlin.argumentCaptor
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
            timeInBedInterval = "2026-02-25T23:00:00/2026-02-26T07:30:00",
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
}

