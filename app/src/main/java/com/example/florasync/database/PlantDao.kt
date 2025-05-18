package com.example.florasync.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.florasync.database.entities.DiaryEntry
import com.example.florasync.database.entities.MyPlant
import com.example.florasync.database.entities.PlantTask
import com.example.florasync.database.entities.TaskOccurrence
import com.example.florasync.database.entities.TaskOccurrenceWithTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PlantDao {

    @Query("SELECT * FROM MyPlant")
    suspend fun getAllMyPlants(): List<MyPlant>

    @Query("SELECT * FROM MyPlant")
    fun getAllMyPlantsFlow(): LiveData<List<MyPlant>>

    @Query("DELETE FROM MyPlant WHERE myPlantId = :id")
    suspend fun deletePlantById(id: Long)

    @Insert
    suspend fun insertMyPlant(myPlant: MyPlant) : Long


    @Insert
    suspend fun insertPlantTask(task: PlantTask): Long

    @Delete
    suspend fun deletePlantTask(task: PlantTask)

    @Query("SELECT * FROM PlantTask WHERE plantId = :plantId")
    suspend fun getTasksForPlant(plantId: Long): List<PlantTask>

    @Query("SELECT * FROM PlantTask")
    suspend fun getAllTasks(): List<PlantTask>

    @Insert(onConflict = REPLACE)
    suspend fun insertOccurrence(occurrence: TaskOccurrence)

    @Query("""
SELECT TaskOccurrence.id, taskId, PlantTask.plantId, date, isCompleted, title, icon, 
       COALESCE(MyPlant.myPlantName, '') AS plantName,
       COALESCE(MyPlant.nickname, '') AS nickname
FROM TaskOccurrence
INNER JOIN PlantTask ON TaskOccurrence.taskId = PlantTask.id
INNER JOIN MyPlant ON PlantTask.plantId = MyPlant.myPlantId
WHERE date BETWEEN :startDate AND :endDate
""")
    suspend fun getOccurrencesBetweenDates(startDate: LocalDate, endDate: LocalDate): List<TaskOccurrenceWithTask>


    @Query("""
    SELECT TaskOccurrence.id, taskId, PlantTask.plantId, date, isCompleted, title, icon, MyPlant.nickname as nickname
    FROM TaskOccurrence
    INNER JOIN PlantTask ON TaskOccurrence.taskId = PlantTask.id
    INNER JOIN MyPlant ON PlantTask.plantId = MyPlant.myPlantId
    WHERE date = :date
""")
    suspend fun getTasksForDate(date: LocalDate): List<TaskOccurrenceWithTask>

    @Query("""
    SELECT TaskOccurrence.id, taskId, PlantTask.plantId, date, isCompleted, title, icon, MyPlant.nickname as nickname
    FROM TaskOccurrence
    INNER JOIN PlantTask ON TaskOccurrence.taskId = PlantTask.id
        INNER JOIN MyPlant ON PlantTask.plantId = MyPlant.myPlantId
""")
    suspend fun getAllTaskOccurrences(): List<TaskOccurrenceWithTask>



    @Query("UPDATE TaskOccurrence SET isCompleted = :completed WHERE id = :id")
    suspend fun markOccurrence(id: Long, completed: Boolean)

    @Query("DELETE FROM TaskOccurrence WHERE taskId = :taskId")
    suspend fun deleteOccurrencesForTask(taskId: Long)

    @Insert(onConflict = REPLACE)
    suspend fun insertDiaryEntry(entry: DiaryEntry): Long

    @Query("SELECT * FROM DiaryEntry WHERE plantId = :plantId ORDER BY date DESC")
    fun getEntriesForPlant(plantId: Long): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM DiaryEntry ORDER BY date DESC")
    fun getAllDiaryEntries(): Flow<List<DiaryEntry>>

    @Delete
    suspend fun deleteEntry(entry: DiaryEntry)


    suspend fun insertInitialData() {
        insertMyPlant(MyPlant(id = 1, name = "Monstera Deliciosa", typeDescription =  "Tropical Plant",
           typeValue = "Tropical", typeIcon = "🌴", waterDescription =  "Weekly", waterValue = "Regular",
            waterIcon = "🧴", lightDescription = "Medium indirect light", lightValue = "Medium",
            lightIcon = "🌥️", temperatureDescription = "18–27°C", temperatureValue = "Moderate",
            temperatureIcon = "🌡️", imageUrl = "/images/monstera_deliciosa.png", funFact = "Monstera leaves develop holes to withstand heavy rainfall.", nickname = "Kimi"))


    }
}