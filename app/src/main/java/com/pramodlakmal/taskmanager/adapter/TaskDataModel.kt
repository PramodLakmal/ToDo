package com.pramodlakmal.taskmanager.adapter

import androidx.annotation.Keep
import com.pramodlakmal.taskmanager.model.Priority
import com.pramodlakmal.taskmanager.model.TypeTask

sealed class TaskDataModel {

    @Keep
    data class Header(
        val title: String
    ) : TaskDataModel()

    @Keep
    data class Task(
        val id: Int,
        var title: String,
        var description: String,
        var priority: Priority
    ) : TaskDataModel() {
        fun toTask() = com.pramodlakmal.taskmanager.model.Task(title, description, priority, id)
        val taskType = if (priority == Priority.DONE) TypeTask.DONE else TypeTask.TODO
    }
}