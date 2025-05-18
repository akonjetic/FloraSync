package com.example.florasync.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "DiaryEntry",
    foreignKeys = [
        ForeignKey(
            entity = MyPlant::class,
            parentColumns = ["myPlantId"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long?,
    val imageUri: String?,
    val date: LocalDate = LocalDate.now(),
    val height: Float?,
    val leafSize: Float?,
    val flowerCount: Int?,
    val description: String?,
    val activity: String?
)