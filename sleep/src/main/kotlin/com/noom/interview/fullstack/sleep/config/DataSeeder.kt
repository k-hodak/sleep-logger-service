package com.noom.interview.fullstack.sleep.config

import com.noom.interview.fullstack.sleep.model.MorningFeeling
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

/**
 * Seeds the database with sample sleep data on application startup.
 * Only runs in "local" profile for local development.
 */
@Configuration
@Profile("local")
class DataSeeder(
    @Value("\${sleep.seeder.days:30}")
    private val seedDays: Int
) {

    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)

    @Bean
    fun seedSleepData(sleepLogRepository: SleepLogRepository): CommandLineRunner {
        return CommandLineRunner {
            val yesterday = LocalDate.now().minusDays(1)
            val seedPlan = calculateSeedPlan(sleepLogRepository, yesterday) ?: return@CommandLineRunner

            val sleepLogs = generateSleepLogs(seedPlan.days, seedPlan.startDate)
            sleepLogRepository.saveAll(sleepLogs)

            logger.info("Seeded {} sleep log entries.", sleepLogs.size)
        }
    }

    private data class SeedPlan(val days: Int, val startDate: LocalDate)

    private fun calculateSeedPlan(repository: SleepLogRepository, yesterday: LocalDate): SeedPlan? {
        val count = repository.count()

        if (count == 0L) {
            logger.info("No sleep data found, seeding {} days.", seedDays)
            return SeedPlan(seedDays, yesterday)
        }

        val latestDate = repository.findTopByOrderBySleepDateDesc()?.sleepDate

        if (latestDate != null && latestDate >= yesterday) {
            logger.info("Sleep data is up to date ({} records, latest: {}), skipping seed.", count, latestDate)
            return null
        }

        val daysSinceLastSeed = latestDate?.let {
            ChronoUnit.DAYS.between(it, yesterday).toInt()
        } ?: seedDays

        return if (daysSinceLastSeed > seedDays) {
            logger.info("Gap since last seed ({} days) exceeds {}, seeding {} days.", daysSinceLastSeed, seedDays, seedDays)
            SeedPlan(seedDays, yesterday)
        } else if (daysSinceLastSeed > 0) {
            logger.info("Filling gap of {} days since last seed date {}.", daysSinceLastSeed, latestDate)
            SeedPlan(daysSinceLastSeed, yesterday)
        } else {
            logger.info("No days to seed.")
            null
        }
    }

    private fun generateSleepLogs(days: Int, startDate: LocalDate): List<SleepLog> {
        logger.info("Generating {} sleep logs starting from {}", days, startDate)
        return ((days - 1) downTo 0).map { daysAgo ->
            val date = startDate.minusDays(daysAgo.toLong())
            generateSleepLog(date)
        }
    }

    private fun generateSleepLog(sleepDate: LocalDate): SleepLog {
        val bedTime = randomBedTime()
        val totalTimeInBed = randomSleepDuration()
        val wakeTime = bedTime.plus(totalTimeInBed)

        return SleepLog(
            id = null,
            userId = DEFAULT_USER_ID,
            sleepDate = sleepDate,
            bedTime = bedTime,
            wakeTime = wakeTime,
            totalTimeInBed = totalTimeInBed,
            morningFeeling = randomMorningFeeling()
        )
    }

    companion object {
        private const val DEFAULT_USER_ID = 1L
    }

    private fun randomBedTime(): LocalTime {
        val bedHour = if (Random.nextBoolean()) Random.nextInt(21, 24) else 0
        val bedMinute = Random.nextInt(0, 60)
        return LocalTime.of(bedHour, bedMinute)
    }

    private fun randomSleepDuration(): Duration {
        val hours = Random.nextInt(5, 10).toLong()
        val minutes = Random.nextInt(0, 60).toLong()
        return Duration.ofHours(hours).plusMinutes(minutes)
    }

    private fun randomMorningFeeling(): MorningFeeling {
        return when (Random.nextInt(100)) {
            in 0..25 -> MorningFeeling.BAD
            in 26..65 -> MorningFeeling.OK
            else -> MorningFeeling.GOOD
        }
    }
}
