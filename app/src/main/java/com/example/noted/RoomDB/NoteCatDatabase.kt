package com.example.noted.RoomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.locks.Lock


@Database(
    entities = [NoteCategory::class],
    version = 2
)
abstract class NoteCatDatabase : RoomDatabase() {

    abstract fun getNoteCat(): NoteCategoryDao

    companion object {
        @Volatile
        private var instance: NoteCatDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteCatDatabase::class.java,
            "NotedDatabase"
        ).build()
    }
}