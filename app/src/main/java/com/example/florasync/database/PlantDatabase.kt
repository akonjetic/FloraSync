package com.example.florasync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.florasync.database.entities.DiaryEntry
import com.example.florasync.database.entities.MyPlant
import com.example.florasync.database.entities.PlantTask
import com.example.florasync.database.entities.TaskOccurrence
import com.example.florasync.database.entities.TaskOccurrenceWithTask
import com.example.florasync.database.helpers.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MyPlant::class, TaskOccurrence::class, PlantTask::class, DiaryEntry::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun getPlantDao(): PlantDao

    companion object {
        private var instance: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase? {
            if (instance == null) {
                instance = buildDatabase(context)
            }
            return instance
        }

        private fun buildDatabase(context: Context): PlantDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PlantDatabase::class.java,
                "PlantDatabase"
            )
                .fallbackToDestructiveMigration()
                .addCallback(roomDatabaseCallback)
                .build()
        }

        private val roomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    instance?.getPlantDao()?.insertInitialData()
                }
            }
        }
    }
}