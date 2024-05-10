package com.pramodlakmal.taskmanager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pramodlakmal.taskmanager.db.TodoDatabase

@Suppress("UNCHECKED_CAST")
class MainViewModelProviderFactory(
    private val todoDatabase: TodoDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(todoDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}