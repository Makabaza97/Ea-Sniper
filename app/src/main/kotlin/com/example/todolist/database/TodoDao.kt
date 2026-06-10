package com.example.todolist.database

import androidx.room.*
import com.example.todolist.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: TodoItem): Long

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Query("SELECT * FROM todo_items ORDER BY priority DESC, dueDate ASC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getTodoById(id: Int): TodoItem?

    @Query("SELECT * FROM todo_items WHERE isCompleted = 0 ORDER BY priority DESC, dueDate ASC")
    fun getActiveTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE category = :category ORDER BY priority DESC, dueDate ASC")
    fun getTodosByCategory(category: String): Flow<List<TodoItem>>

    @Query("DELETE FROM todo_items WHERE isCompleted = 1")
    suspend fun deleteCompletedTodos()

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 0")
    fun getActiveCount(): Flow<Int>

    @Query("SELECT DISTINCT category FROM todo_items ORDER BY category")
    fun getCategories(): Flow<List<String>>
}
