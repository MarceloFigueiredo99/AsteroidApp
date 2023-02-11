package com.udacity.asteroidradar

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val current: LocalDateTime = LocalDateTime.now()

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun getTodayDate(): String {
    return current.format(formatter)
}

fun getNextSevenDays(): String {
    return current.plusDays(7).format(formatter)
}
