package com.pramodlakmal.taskmanager.ui.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pramodlakmal.taskmanager.R
import com.pramodlakmal.taskmanager.adapter.TaskDataAdapter
import com.pramodlakmal.taskmanager.adapter.TaskDataModel
import com.pramodlakmal.taskmanager.databinding.ActivityMainBinding
import com.pramodlakmal.taskmanager.db.TodoDatabase
import com.pramodlakmal.taskmanager.model.Priority
import com.pramodlakmal.taskmanager.model.State
import com.pramodlakmal.taskmanager.model.Task
import com.pramodlakmal.taskmanager.ui.MainViewModel
import com.pramodlakmal.taskmanager.ui.MainViewModelProviderFactory
import com.pramodlakmal.taskmanager.ui.dialog.TaskDialog
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var _taskDialog: TaskDialog? = null
    private val taskDialog get() = _taskDialog!!

    override fun onCreate(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelProviderFactory(TodoDatabase(this))
        )[MainViewModel::class.java]

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        mainViewModel.getTasks().observe(this) { tasks ->
            updateTaskList(tasks)
        }

        setupSearchView()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { mainViewModel.searchTasks(it).observe(this@MainActivity) { tasks ->
                    updateTaskList(tasks)
                }}
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { mainViewModel.searchTasks(it).observe(this@MainActivity) { tasks ->
                    updateTaskList(tasks)
                }}
                return true
            }
        })
    }

    private fun updateTaskList(tasks: List<Task>) {
        val tasksList = tasks
            .sortedBy { it.taskType }
            .groupBy { it.taskType }
            .flatMap {
                listOf(
                    TaskDataModel.Header(it.key.name),
                    *(it.value
                        .sortedByDescending { t -> t.id!! }
                        .map { task ->
                            task.toDataModel()
                        }).toTypedArray()
                )
            }

        taskDataAdapter.differ.submitList(tasksList)
    }



    private fun setupToolbar() {
        mainViewModel.getToday()

        mainViewModel.today.observe(this) {
            binding.apply {
                tvDate.text = it.date.toString()
                tvDay.text = it.day
                tvMonthYear.text = it.monthYear
            }
        }

        binding.btnNewTask.setOnClickListener {
            showTaskDialog(null)
        }
    }

    private val taskDataAdapter by lazy {
        TaskDataAdapter(
            applicationContext,
            checkTodoListener = {
                val priority = if (it.priority != Priority.DONE) {
                    Priority.DONE
                } else Priority.NORMAL
                mainViewModel.setPriority(priority, it.id)
            },
            itemTodoListener = {
                showTaskDialog(it.toTask())
            }
        )
    }

    private fun setupRecyclerView() {
        binding.rvTasks.apply {
            adapter = taskDataAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    private fun showTaskDialog(task: Task?) {
        if (_taskDialog == null) {
            _taskDialog = TaskDialog(
                this, task,
                onSubmitClickListener = { s, t ->
                    when (s) {
                        State.ADD -> mainViewModel.addTask(t)
                        State.UPDATE -> mainViewModel.updateTask(t)
                    }
                    taskDialog.dismiss()
                    _taskDialog = null
                },
                onDeleteClickListener = {
                    mainViewModel.deleteTask(it)
                    taskDialog.dismiss()
                    _taskDialog = null
                }
            )
        }

        taskDialog.show()

        taskDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                Constraints.LayoutParams.MATCH_PARENT,
                Constraints.LayoutParams.WRAP_CONTENT
            )
        }

        taskDialog.setOnDismissListener {
            _taskDialog = null
        }
    }
}