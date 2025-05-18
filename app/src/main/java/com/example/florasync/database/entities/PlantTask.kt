package com.example.florasync.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    foreignKeys = [ForeignKey(
        entity = MyPlant::class,
        parentColumns = ["myPlantId"],
        childColumns = ["plantId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PlantTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val title: String,
    val icon: String, // npr. 💧☀️🍃
    val repeatDays: List<String>, // ["Monday", "Thursday"]
    val createdAt: LocalDate = LocalDate.now()
)
