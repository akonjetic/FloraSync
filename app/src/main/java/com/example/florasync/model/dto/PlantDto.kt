package com.example.florasync.model.dto

data class PlantDto(
    val id: Int,
    val name: String,
    val type: String,
    val typeValue: String,
    val typeIcon: String,
    val water: String,
    val waterValue: String,
    val waterIcon: String,
    val light: String,
    val lightValue: String,
    val lightIcon: String,
    val temperature: String,
    val temperatureValue: String,
    val temperatureIcon: String,
    val imageUrl: String?,
    val funFact: String
)
