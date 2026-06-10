package com.example.todolist

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.database.TodoDatabase
import com.example.todolist.model.Priority
import com.example.todolist.model.TodoItem
import com.example.todolist.repository.TodoRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private lateinit var todoRepository: TodoRepository
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView
    private var filterMode = FilterMode.ALL

    enum class FilterMode {
        ALL, ACTIVE, COMPLETED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database and repository
        val db = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            TodoDatabase.DATABASE_NAME
        ).build()
        todoRepository = TodoRepository(db.todoDao())

        // Setup UI
        setupViews()
        setupRecyclerView()
        observeTodos()
    }

    private fun setupViews() {
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val prioritySpinner = findViewById<Spinner>(R.id.prioritySpinner)
        val categoryInput = findViewById<EditText>(R.id.categoryInput)
        val addButton = findViewById<Button>(R.id.addButton)
        val clearCompletedButton = findViewById<Button>(R.id.clearCompletedButton)
        val filterAllButton = findViewById<Button>(R.id.filterAllButton)
        val filterActiveButton = findViewById<Button>(R.id.filterActiveButton)
        val filterCompletedButton = findViewById<Button>(R.id.filterCompletedButton)
        val activeCountTextView = findViewById<TextView>(R.id.activeCountTextView)

        // Setup Priority Spinner
        val priorityAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Priority.values().map { it.name }
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = priorityAdapter

        // Add button listener
        addButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val priority = Priority.valueOf(prioritySpinner.selectedItem.toString())
            val category = categoryInput.text.toString().trim().ifEmpty { "General" }

            if (title.isNotEmpty()) {
                val todo = TodoItem(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category
                )
                lifecycleScope.launch {
                    todoRepository.insertTodo(todo)
                    titleInput.text.clear()
                    descriptionInput.text.clear()
                    categoryInput.text.clear()
                    Toast.makeText(this@MainActivity, "Todo added", Toast.LENGTH_SHORT).show()
                }
            } else {
                titleInput.error = "Title cannot be empty"
            }
        }

        // Clear completed button
        clearCompletedButton.setOnClickListener {
            lifecycleScope.launch {
                todoRepository.deleteCompletedTodos()
                Toast.makeText(this@MainActivity, "Completed todos cleared", Toast.LENGTH_SHORT).show()
            }
        }

        // Filter buttons
        filterAllButton.setOnClickListener {
            filterMode = FilterMode.ALL
            observeTodos()
            updateFilterButtons(filterAllButton, filterActiveButton, filterCompletedButton)
        }
        filterActiveButton.setOnClickListener {
            filterMode = FilterMode.ACTIVE
            observeTodos()
            updateFilterButtons(filterActiveButton, filterAllButton, filterCompletedButton)
        }
        filterCompletedButton.setOnClickListener {
            filterMode = FilterMode.COMPLETED
            observeTodos()
            updateFilterButtons(filterCompletedButton, filterAllButton, filterActiveButton)
        }

        // Observe active count
        lifecycleScope.launch {
            todoRepository.activeCount.collect { count ->
                activeCountTextView.text = "Active: $count"
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.todoRecyclerView)
        todoAdapter = TodoAdapter(
            onToggle = { todo -> toggleTodo(todo) },
            onDelete = { todo -> deleteTodo(todo) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = todoAdapter
    }

    private fun observeTodos() {
        lifecycleScope.launch {
            when (filterMode) {
                FilterMode.ALL -> todoRepository.allTodos
                FilterMode.ACTIVE -> todoRepository.activeTodos
                FilterMode.COMPLETED -> todoRepository.completedTodos
            }.collect { todos ->
                todoAdapter.submitList(todos)
            }
        }
    }

    private fun toggleTodo(todo: TodoItem) {
        lifecycleScope.launch {
            val updated = todo.copy(
                isCompleted = !todo.isCompleted,
                updatedAt = LocalDateTime.now()
            )
            todoRepository.updateTodo(updated)
        }
    }

    private fun deleteTodo(todo: TodoItem) {
        lifecycleScope.launch {
            todoRepository.deleteTodo(todo)
            Toast.makeText(this@MainActivity, "Todo deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFilterButtons(
        activeButton: Button,
        button1: Button,
        button2: Button
    ) {
        activeButton.setBackgroundColor(getColor(android.R.color.holo_blue_dark))
        activeButton.setTextColor(getColor(android.R.color.white))
        button1.setBackgroundColor(getColor(android.R.color.darker_gray))
        button1.setTextColor(getColor(android.R.color.white))
        button2.setBackgroundColor(getColor(android.R.color.darker_gray))
        button2.setTextColor(getColor(android.R.color.white))
    }
}
