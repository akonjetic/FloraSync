package com.example.florasync.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.florasync.model.dto.PlantDto
import com.example.florasync.network.FloraSyncRetrofitInstance
import com.example.florasync.network.PlantPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class NetworkViewModel : ViewModel() {

    val listOfAllPlants = MutableLiveData<ArrayList<PlantDto>>()
    val chosenPlant = MutableLiveData<PlantDto>()

    val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val plants: Flow<PagingData<PlantDto>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            Pager(PagingConfig(pageSize = 30)) {
                PlantPagingSource(FloraSyncRetrofitInstance.getService(), query)
            }.flow.cachedIn(viewModelScope)
        }

   fun getAllPlants(context: Context){
                viewModelScope.launch {
                    listOfAllPlants.value = FloraSyncRetrofitInstance.getService().getPlants() as ArrayList<PlantDto>
                }
            }

    fun getPlantById(id: Long){
                viewModelScope.launch {
                    chosenPlant.value = FloraSyncRetrofitInstance.getService().getPlantById(id)
                }
            }


}