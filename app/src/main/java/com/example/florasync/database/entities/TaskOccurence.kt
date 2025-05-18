package com.example.florasync.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    foreignKeys = [ForeignKey(
        entity = PlantTask::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskOccurrence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val date: LocalDate,
    val isCompleted: Boolean = false
)


data class TaskOccurrenceWithTask(
    val id: Long,
    val taskId: Long,
    val plantId: Long,
    val date: LocalDate,
    val isCompleted: Boolean,
    val title: String,
    val icon: String,
    val nickname: String?
)

