package com.example.todolist.repository

import com.example.todolist.database.TodoDao
import com.example.todolist.model.TodoItem
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()
    val activeTodos: Flow<List<TodoItem>> = todoDao.getActiveTodos()
    val completedTodos: Flow<List<TodoItem>> = todoDao.getCompletedTodos()
    val categories: Flow<List<String>> = todoDao.getCategories()
    val activeCount: Flow<Int> = todoDao.getActiveCount()

    suspend fun insertTodo(todo: TodoItem) = todoDao.insertTodo(todo)

    suspend fun updateTodo(todo: TodoItem) = todoDao.updateTodo(todo)

    suspend fun deleteTodo(todo: TodoItem) = todoDao.deleteTodo(todo)

    suspend fun getTodoById(id: Int) = todoDao.getTodoById(id)

    fun getTodosByCategory(category: String): Flow<List<TodoItem>> =
        todoDao.getTodosByCategory(category)

    suspend fun deleteCompletedTodos() = todoDao.deleteCompletedTodos()
}
