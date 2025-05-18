package com.example.florasync.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.io.Serializable

data class Plant(
    val id: Long,
    val name: String,
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
    val imageUrl: String,
    val funFact: String,
): Serializable