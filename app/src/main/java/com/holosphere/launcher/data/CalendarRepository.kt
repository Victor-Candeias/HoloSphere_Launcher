package com.holosphere.launcher.data

import android.content.Context
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CalendarEvent(val title: String, val startMillis: Long, val endMillis: Long)

class CalendarRepository(private val context: Context) {
    suspend fun upcoming(limit: Int = 5): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val result = mutableListOf<CalendarEvent>()
        val projection = arrayOf(CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND)
        context.contentResolver.query(CalendarContract.Events.CONTENT_URI, projection, "${CalendarContract.Events.DTSTART} >= ?", arrayOf(System.currentTimeMillis().toString()), "${CalendarContract.Events.DTSTART} ASC")?.use { cursor ->
            val title = cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
            val start = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
            val end = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND)
            while (cursor.moveToNext() && result.size < limit) result += CalendarEvent(cursor.getString(title) ?: "Sem título", cursor.getLong(start), cursor.getLong(end))
        }
        result
    }
}
