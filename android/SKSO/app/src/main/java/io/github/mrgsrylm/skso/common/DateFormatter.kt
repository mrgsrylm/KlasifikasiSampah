package io.github.mrgsrylm.skso.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun unixTsToDate(unixTimestamp: Long): String {
    val date = Date(unixTimestamp * 1000L)
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

    return sdf.format(date)
}

fun unixTsToTime(unixTimestamp: Long): String {
    val date = Date(unixTimestamp * 1000L)
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

    return sdf.format(date)
}