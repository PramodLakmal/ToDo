package com.pramodlakmal.taskmanager.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramodlakmal.taskmanager.db.TodoDatabase
import com.pramodlakmal.taskmanager.model.Priority
import com.pramodlakmal.taskmanager.model.Task
import com.pramodlakmal.taskmanager.model.Today
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainViewModel(
    private val todoDatabase: TodoDatabase
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())

    val today: MutableLiveData<Today> by lazy { MutableLiveData<Today>() }

    fun getToday() {
        val time = System.currentTimeMillis()
        today.value = Today(
            date = dateFormat.format(time).toInt(),
            day = dayFormat.format(time),
            monthYear = monthYearFormat.format(time)
        )
    }

    fun addTask(task: Task) = viewModelScope.launch {
        todoDatabase.getTaskDao().upsertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        todoDatabase.getTaskDao().deleteTask(task)
    }

    fun getTasks() = todoDatabase.getTaskDao().getTasks()

    fun setPriority(priority: Priority, id: Int) = viewModelScope.launch {
        todoDatabase.getTaskDao().setPriority(priority, id)
    }

    fun updateTask(task: Task): Job {
        if (task.id == null) throw IllegalStateException("Task id can not be null")
        return viewModelScope.launch {
            todoDatabase
                .getTaskDao()
                .updateTask(task.id, task.title, task.description, task.priority)
        }
    }

}