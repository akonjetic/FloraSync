package com.example.florasync.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.florasync.database.PlantDatabase
import com.example.florasync.database.entities.MyPlant
import com.example.florasync.model.dto.PlantDto
import com.example.florasync.model.mappers.toMyPlant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalPlantsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = PlantDatabase.getDatabase(application)!!.getPlantDao()

    val myPlants: LiveData<List<MyPlant>> = dao.getAllMyPlantsFlow()

    fun insertMyPlant(plant: PlantDto, nickname: String) {
        val myPlant = MyPlant(
            id = 0L,
            nickname = nickname,
            name = plant.name,
            typeDescription = plant.type,
            typeValue = plant.typeValue,
            typeIcon = plant.typeIcon,
            waterDescription = plant.water,
            waterValue = plant.waterValue,
            waterIcon = plant.waterIcon,
            lightDescription = plant.light,
            lightValue = plant.lightValue,
            lightIcon = plant.lightIcon,
            temperatureDescription = plant.temperature,
            temperatureValue = plant.temperatureValue,
            temperatureIcon = plant.temperatureIcon,
            imageUrl = plant.imageUrl.toString(),
            funFact = plant.funFact
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertMyPlant(myPlant)
        }
    }

    /*fun addMyPlant(plantDto: PlantDto) {
        viewModelScope.launch {
            val myPlant = plantDto.toMyPlant()
            dao.insertMyPlant(myPlant)
        }
    }*/

    fun removePlantById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deletePlantById(id)
        }
    }


}
