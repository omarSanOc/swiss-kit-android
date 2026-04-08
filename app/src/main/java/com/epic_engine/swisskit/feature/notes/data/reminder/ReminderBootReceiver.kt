package com.epic_engine.swisskit.feature.notes.data.reminder

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRequest
import com.epic_engine.swisskit.feature.notes.domain.reminder.ReminderScheduler
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderBootReceiver : BroadcastReceiver() {

    @Inject lateinit var noteRepository: NoteRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notesWithReminders = noteRepository.observeAll().first()
                    .filter { it.reminderAt != null && it.reminderAt > System.currentTimeMillis() }
                for (note in notesWithReminders) {
                    val request = NoteReminderRequest(
                        triggerAtMillis = note.reminderAt!!,
                        recurrence = note.reminderRecurrence
                            ?: com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRecurrence.ONE_TIME
                    )
                    reminderScheduler.schedule(note.id, note.title, request)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
