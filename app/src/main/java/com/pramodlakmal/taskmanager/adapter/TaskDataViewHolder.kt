package com.pramodlakmal.taskmanager.adapter

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pramodlakmal.taskmanager.R
import com.pramodlakmal.taskmanager.model.Priority
import com.pramodlakmal.taskmanager.model.TypeTask
import com.pramodlakmal.taskmanager.databinding.ItemHeaderBinding
import com.pramodlakmal.taskmanager.databinding.ItemTaskBinding

sealed class TaskDataViewHolder(
    binding: ViewBinding,
    val taskDataAdapter: TaskDataAdapter,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    class HeaderViewHolder(
        private val itemBinding: ItemHeaderBinding,
        taskDataAdapter: TaskDataAdapter,
        context: Context
    ) : TaskDataViewHolder(itemBinding, taskDataAdapter, context) {
        fun bind(item: TaskDataModel.Header) {
            itemBinding.textView.text = item.title
        }
    }

    class TaskViewHolder(
        private val itemBinding: ItemTaskBinding,
        taskDataAdapter: TaskDataAdapter,
        context: Context
    ) : TaskDataViewHolder(itemBinding, taskDataAdapter, context) {
        fun bind(item: TaskDataModel.Task) {
            val priority = "${item.priority.name} priority"

            val color = when (item.priority) {
                Priority.NORMAL -> getColor(R.color.normal_priority)
                Priority.DONE -> getColor(R.color.done_priority)
                Priority.HIGH -> getColor(R.color.high_priority)
            }

            val drawable = when (item.taskType) {
                TypeTask.DONE -> getDrawable(R.drawable.ic_round_check_circle_24)
                TypeTask.TODO -> getDrawable(R.drawable.ic_round_circle_24)
            }

            itemBinding.apply {
                task.backgroundTintList = ColorStateList.valueOf(color)
                tvPriority.text = priority
                tvTitle.text = item.title
                tvDescription.text = item.description.ifEmpty {
                    context.getString(R.string.no_description)
                }

                imageView.setImageDrawable(drawable)
                imageView.setOnClickListener {
                    taskDataAdapter.checkTodoListener(item)
                }
                task.setOnClickListener {
                    taskDataAdapter.itemTodoListener(item)
                }
            }

        }

        private fun getColor(color: Int) = ContextCompat.getColor(context, color)
        private fun getDrawable(drawable: Int) = ContextCompat.getDrawable(context, drawable)
    }
}