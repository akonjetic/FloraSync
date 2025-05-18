package com.example.florasync.model.mappers

import com.example.florasync.database.entities.MyPlant
import com.example.florasync.model.Plant
import com.example.florasync.model.dto.PlantDto

fun MyPlant.toDomainModel(): Plant {
    return Plant(
        id = id,
        name = name,
        nickname = nickname,
        typeDescription = typeDescription,
        typeValue = typeValue,
        typeIcon = typeIcon,
        waterDescription = waterDescription,
        waterValue = waterValue,
        waterIcon = waterIcon,
        lightDescription = lightDescription,
        lightValue = lightValue,
        lightIcon = lightIcon,
        temperatureDescription = temperatureDescription,
        temperatureValue = temperatureValue,
        temperatureIcon = temperatureIcon,
        imageUrl = imageUrl,
        funFact = funFact
    )
}

fun PlantDto.toMyPlant(nickname: String?): MyPlant = MyPlant(
    id = 0L, // Room će dodijeliti ID jer je autoGenerate = true
    name = name,
    nickname = nickname,
    typeDescription = type,
    typeValue = typeValue,
    typeIcon = typeIcon,
    waterDescription = water,
    waterValue = waterValue,
    waterIcon = waterIcon,
    lightDescription = light,
    lightValue = lightValue,
    lightIcon = lightIcon,
    temperatureDescription = temperature,
    temperatureValue = temperatureValue,
    temperatureIcon = temperatureIcon,
    imageUrl = imageUrl.toString(),
    funFact = funFact
)
