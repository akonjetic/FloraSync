package com.example.florasync.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.florasync.database.PlantDatabase
import com.example.florasync.database.entities.PlantTask
import com.example.florasync.database.entities.TaskOccurrence
import com.example.florasync.database.entities.TaskOccurrenceWithTask
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = PlantDatabase.getDatabase(application)!!.getPlantDao()

    private val _allTasks = MutableStateFlow<List<TaskOccurrenceWithTask>>(emptyList())
    val allTasks: StateFlow<List<TaskOccurrenceWithTask>> = _allTasks.asStateFlow()

    val todayTasks: StateFlow<List<TaskOccurrenceWithTask>> = allTasks.mapLatest { list ->
        val today = LocalDate.now()
        list.filter { it.date == today && !it.isCompleted }
            .sortedBy { it.date }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val upcomingTasks: StateFlow<List<TaskOccurrenceWithTask>> = allTasks.mapLatest { list ->
        val today = LocalDate.now()
        list.filter { it.date.isAfter(today) && !it.isCompleted }
            .sortedBy { it.date }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val completedTasks: StateFlow<List<TaskOccurrenceWithTask>> = allTasks.mapLatest { list ->
        list.filter { it.isCompleted }
            .sortedBy { it.date }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadAllTasks()
    }

    fun loadAllTasks() {
        viewModelScope.launch {
            generateUpcomingOccurrences()
            _allTasks.value = dao.getAllTaskOccurrences()
        }
    }

    fun addTask(task: PlantTask) {
        viewModelScope.launch {
            val taskId = dao.insertPlantTask(task)
            generateUpcomingOccurrences(task.copy(id = taskId))
            loadAllTasks()
        }
    }

    fun deleteTask(task: PlantTask) {
        viewModelScope.launch {
            dao.deleteOccurrencesForTask(task.id)
            dao.deletePlantTask(task)
            loadAllTasks()
        }
    }

    fun deleteTaskById(taskId: Long) {
        viewModelScope.launch {
            val task = dao.getAllTasks().firstOrNull { it.id == taskId }
            task?.let {
                deleteTask(it)
            }
        }
    }

    fun markTaskCompleted(occurrenceId: Long, completed: Boolean) {
        viewModelScope.launch {
            dao.markOccurrence(occurrenceId, completed)
            loadAllTasks()
        }
    }

    fun getTasksForPlant(plantId: Long): StateFlow<List<PlantTask>> {
        val flow = MutableStateFlow<List<PlantTask>>(emptyList())

        viewModelScope.launch {
            flow.value = dao.getTasksForPlant(plantId)
        }

        return flow
    }


    private suspend fun generateUpcomingOccurrences(task: PlantTask? = null) {
        val tasks = task?.let { listOf(it) } ?: dao.getAllTasks()
        val today = LocalDate.now()

        tasks.forEach { t ->
            for (i in 0..13) {
                val date = today.plusDays(i.toLong())
                if (date.dayOfWeek.name in t.repeatDays) {
                    val exists = dao.getTasksForDate(date).any { it.taskId == t.id }
                    if (!exists) {
                        dao.insertOccurrence(TaskOccurrence(taskId = t.id, date = date))
                    }
                }
            }
        }
    }
}
