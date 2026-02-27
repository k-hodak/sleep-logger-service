package com.noom.interview.fullstack.sleep.config

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Duration

@Converter(autoApply = true)
class DurationConverter : AttributeConverter<Duration, Long> {

    override fun convertToDatabaseColumn(duration: Duration?): Long? {
        return duration?.toMinutes()
    }

    override fun convertToEntityAttribute(minutes: Long?): Duration? {
        return minutes?.let { Duration.ofMinutes(it) }
    }
}

