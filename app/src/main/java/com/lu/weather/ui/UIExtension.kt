package com.lu.weather.ui

import com.lu.weather.R
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val HH = "HH"
const val HHmm = "HH:mm"
const val EEE = "EEE"

fun timeStampToTimeWithFormat(timeStamp: Long, timeZone: String, format: String): String {
    val outputFormatter = DateTimeFormatter.ofPattern(format)
    val instant = Instant.ofEpochMilli(timeStamp * 1000)
    val zonedDateTime = instant.atZone(ZoneId.of(timeZone))
    return zonedDateTime.format(outputFormatter)
}

fun Int.codeToDrawable(isDay: Boolean = true) = when (this) {
    200, 201, 202, 230, 231, 232, 233 -> R.drawable.thunder_storm
    300, 301, 302 -> R.drawable.drizzle_freezing_drizzle
    500, 501 -> R.drawable.rain
    502, 511, 520, 521, 522, 900 -> R.drawable.heavy_rain
    600, 601 -> R.drawable.snow
    602 -> R.drawable.heavy_snow
    610, 611, 612 -> R.drawable.freezing_sleet
    621, 622, 623 -> R.drawable.heavy_snow
    700, 711, 731, 741 -> R.drawable.fog
    721 -> R.drawable.haze
    800 -> if (isDay) R.drawable.clear_mostly_clear else R.drawable.clear_mostly_clear_night
    801, 802, 803 -> if (isDay) R.drawable.partly_cloudy else R.drawable.partly_cloudy_night
    804 -> R.drawable.cloudy

    else -> R.drawable.cloudy
}

fun Int.codeToBackgroundColor(isDay: Boolean = true) = when (this) {
    200, 201, 202, 230, 231, 232, 233 -> R.color.storm
    300, 301, 302, 500, 501, 502, 511, 520, 521, 522, 900 -> R.color.rainy
    600, 601, 602, 610, 611, 612, 621, 622, 623 -> R.color.snow
    700, 711, 731, 741 -> R.color.fog
    721, 800 -> R.color.sunny
    801, 802, 803, 804 -> R.color.cloudy

    else -> R.color.sunny
}

fun Int.uvString() = when (this) {
    0 -> R.string.uv_lowest
    in 1..2 -> R.string.uv_low
    in 3..5 -> R.string.uv_medium
    in 6..7 -> R.string.uv_high
    in 8..10 -> R.string.uv_very_high
    else -> R.string.uv_extreme
}

fun Int.airQualityString() = when (this) {
    in 0..50 -> R.string.air_lvl_good
    in 51..100 -> R.string.air_lvl_moderate
    in 101..150 -> R.string.air_lvl_high
    in 151..200 -> R.string.air_lvl_unhealthy
    in 201..300 -> R.string.air_lvl_very_unhealthy
    else -> R.string.air_lvl_hazardous
}

fun Int.errorCodeToStr() = when(this) {
    1 -> R.string.connection_error
    2 -> R.string.unexpected_error
    else -> R.string.unexpected_error
}

fun Int.toHumidStr() = when(this) {
    in 0..30 -> R.string.low_humid
    in 31..50 -> R.string.fair_humid
    else -> R.string.too_humid
}

/*For parsing sunrise and sunset time-string in UTC to city' timezone*/
fun String.utcStringToTime(timeZone: String, format: String): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    val localTimeInUtc = LocalTime.parse(this, formatter)
    val zonedDateTimeInUtc =
        ZonedDateTime.of(localTimeInUtc.atDate(java.time.LocalDate.now()), ZoneId.of("UTC"))
    val zonedDateTimeInTargetZone = zonedDateTimeInUtc.withZoneSameInstant(ZoneId.of(timeZone))
    return zonedDateTimeInTargetZone.format(formatter)
}