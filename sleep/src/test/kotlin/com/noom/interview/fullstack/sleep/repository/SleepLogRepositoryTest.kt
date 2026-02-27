package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.MorningFeeling
import com.noom.interview.fullstack.sleep.model.SleepLog
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@DataJpaTest
@ActiveProfiles("test")
class SleepLogRepositoryTest {

    @Autowired
    private lateinit var sleepLogRepository: SleepLogRepository

    @BeforeEach
    fun setUp() {
        sleepLogRepository.deleteAll()
    }

    @Test
    fun `findTopByOrderBySleepDateDesc should return most recent sleep log`() {
        val oldLog = createSleepLog(LocalDate.now().minusDays(5))
        val recentLog = createSleepLog(LocalDate.now().minusDays(1))
        val middleLog = createSleepLog(LocalDate.now().minusDays(3))

        sleepLogRepository.saveAll(listOf(oldLog, recentLog, middleLog))

        val result = sleepLogRepository.findTopByOrderBySleepDateDesc()

        assertNotNull(result)
        assertEquals(LocalDate.now().minusDays(1), result?.sleepDate)
    }

    @Test
    fun `findTopByOrderBySleepDateDesc should return null when no records exist`() {
        val result = sleepLogRepository.findTopByOrderBySleepDateDesc()

        assertNull(result)
    }

    @Test
    fun `findByUserIdAndSleepDate should return sleep log for matching user and date`() {
        val userId = 1L
        val today = LocalDate.now()
        val sleepLog = createSleepLog(today, userId)
        sleepLogRepository.save(sleepLog)

        val result = sleepLogRepository.findByUserIdAndSleepDate(userId, today)

        assertNotNull(result)
        assertEquals(userId, result?.userId)
        assertEquals(today, result?.sleepDate)
    }

    @Test
    fun `findByUserIdAndSleepDate should return null when user does not match`() {
        val today = LocalDate.now()
        val sleepLog = createSleepLog(today, 1L)
        sleepLogRepository.save(sleepLog)

        val result = sleepLogRepository.findByUserIdAndSleepDate(2L, today)

        assertNull(result)
    }

    private fun createSleepLog(sleepDate: LocalDate, userId: Long = 1L): SleepLog {
        return SleepLog(
            id = null,
            userId = userId,
            sleepDate = sleepDate,
            bedTime = LocalTime.of(23, 0),
            wakeTime = LocalTime.of(7, 0),
            totalTimeInBed = Duration.ofHours(8),
            morningFeeling = MorningFeeling.GOOD
        )
    }
}
