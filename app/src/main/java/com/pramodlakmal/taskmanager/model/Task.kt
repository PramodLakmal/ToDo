package com.pramodlakmal.taskmanager.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.pramodlakmal.taskmanager.adapter.TaskDataModel

@Entity(tableName = "tasks")
data class Task(
    var title: String,
    var description: String,
    var priority: Priority,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
) {

    fun toDataModel(): TaskDataModel.Task {
        if (id == null) throw IllegalStateException("TaskDataModel.Task id can not be null")
        return TaskDataModel.Task(id, title, description, priority)
    }

    @Ignore
    val taskType = if (priority == Priority.DONE) TypeTask.DONE else TypeTask.TODO
}