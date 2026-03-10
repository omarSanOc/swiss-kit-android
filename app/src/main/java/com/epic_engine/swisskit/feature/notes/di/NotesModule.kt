package com.epic_engine.swisskit.feature.notes.di

import android.app.AlarmManager
import android.content.Context
import com.epic_engine.swisskit.core.database.SwissKitDatabase
import com.epic_engine.swisskit.feature.notes.data.local.NoteDao
import com.epic_engine.swisskit.feature.notes.data.reminder.AlarmManagerReminderScheduler
import com.epic_engine.swisskit.feature.notes.data.repository.NoteRepositoryImpl
import com.epic_engine.swisskit.feature.notes.domain.reminder.ReminderScheduler
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotesModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: AlarmManagerReminderScheduler): ReminderScheduler

    companion object {
        @Provides
        fun provideNoteDao(db: SwissKitDatabase): NoteDao = db.noteDao()

        @Provides
        @Singleton
        fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}
