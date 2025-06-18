package com.example.florasync.viewmodel

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.florasync.database.PlantDatabase
import com.example.florasync.database.entities.DiaryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryDao = PlantDatabase.getDatabase(application)?.getPlantDao()

    fun getEntriesForPlant(plantId: Long): Flow<List<DiaryEntry>> =
        diaryDao?.getEntriesForPlant(plantId)!!

    val allEntries: Flow<List<DiaryEntry>> = diaryDao?.getAllDiaryEntries()!!

    fun addEntry(entry: DiaryEntry) = viewModelScope.launch {
        diaryDao?.insertDiaryEntry(entry)!!
    }

}