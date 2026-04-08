package com.epic_engine.swisskit.feature.notes.data.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRecurrence
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRequest
import com.epic_engine.swisskit.feature.notes.domain.reminder.ReminderScheduler
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class NoteReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var noteRepository: NoteRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: return
        val title = intent.getStringExtra(EXTRA_NOTE_TITLE)
            ?: context.getString(R.string.notes_reminder_default_title)
        val recurrence = intent.getStringExtra(EXTRA_RECURRENCE)
            ?.let { runCatching { NoteReminderRecurrence.valueOf(it) }.getOrNull() }
            ?: NoteReminderRecurrence.ONE_TIME

        showNotification(context, noteId, title)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val note = noteRepository.getById(noteId) ?: return@launch
                if (recurrence == NoteReminderRecurrence.DAILY) {
                    val nextTrigger = Calendar.getInstance().apply {
                        timeInMillis = note.reminderAt ?: System.currentTimeMillis()
                        add(Calendar.DAY_OF_YEAR, 1)
                    }.timeInMillis
                    val request = NoteReminderRequest(nextTrigger, NoteReminderRecurrence.DAILY)
                    noteRepository.save(note.copy(reminderAt = nextTrigger))
                    reminderScheduler.schedule(note.id, note.title, request)
                } else {
                    noteRepository.save(note.copy(reminderAt = null, reminderRecurrence = null))
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, noteId: String, title: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(context, manager)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_note)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.notes_reminder_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(noteId.hashCode(), notification)
    }

    private fun ensureChannel(context: Context, manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notes_reminder_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        const val EXTRA_NOTE_TITLE = "note_title"
        const val EXTRA_RECURRENCE = "recurrence"
        const val CHANNEL_ID = "notes_reminders"
    }
}
