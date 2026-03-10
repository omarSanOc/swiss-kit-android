package com.epic_engine.swisskit.feature.notes.data.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.epic_engine.swisskit.feature.notes.domain.reminder.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : ReminderScheduler {

    override fun schedule(noteId: String, title: String, triggerAtMillis: Long) {
        val intent = Intent(context, NoteReminderReceiver::class.java).apply {
            putExtra(NoteReminderReceiver.EXTRA_NOTE_ID, noteId)
            putExtra(NoteReminderReceiver.EXTRA_NOTE_TITLE, title)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        }
    }

    override fun cancel(noteId: String) {
        val intent = Intent(context, NoteReminderReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        alarmManager.cancel(pending)
        pending.cancel()
    }
}
