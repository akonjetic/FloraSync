package com.example.florasync.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serial
import java.io.Serializable

@Entity(tableName = "MyPlant")
data class MyPlant(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "myPlantId")
    val id: Long,
    @ColumnInfo(name = "myPlantName")
    val name: String,
    @ColumnInfo(name = "nickname")
    val nickname: String?,
    val typeDescription: String,
    val typeValue: String,
    val typeIcon: String,
    val waterDescription: String,
    val waterValue: String,
    val waterIcon: String,
    val lightDescription: String,
    val lightValue: String,
    val lightIcon: String,
    val temperatureDescription: String,
    val temperatureValue: String,
    val temperatureIcon: String,
    @ColumnInfo(name = "myPlantImageUrl")
    val imageUrl: String,
    val funFact: String,
): Serializable