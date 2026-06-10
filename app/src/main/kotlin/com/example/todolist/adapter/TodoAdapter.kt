package com.example.todolist.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Priority
import com.example.todolist.model.TodoItem
import java.time.format.DateTimeFormatter

class TodoAdapter(
    private val onToggle: (TodoItem) -> Unit,
    private val onDelete: (TodoItem) -> Unit
) : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.todoTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.todoDescription)
        private val priorityTextView: TextView = itemView.findViewById(R.id.todoPriority)
        private val categoryTextView: TextView = itemView.findViewById(R.id.todoCategory)
        private val dueDateTextView: TextView = itemView.findViewById(R.id.todoDueDate)
        private val completedCheckBox: CheckBox = itemView.findViewById(R.id.completedCheckBox)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(todo: TodoItem) {
            titleTextView.text = todo.title
            descriptionTextView.text = todo.description
            priorityTextView.text = "${todo.priority.name}"
            categoryTextView.text = todo.category
            completedCheckBox.isChecked = todo.isCompleted

            // Apply strikethrough if completed
            if (todo.isCompleted) {
                titleTextView.paintFlags = titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                titleTextView.alpha = 0.5f
            } else {
                titleTextView.paintFlags = titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                titleTextView.alpha = 1.0f
            }

            // Set priority color
            val priorityColor = when (todo.priority) {
                Priority.LOW -> itemView.context.getColor(android.R.color.holo_green_dark)
                Priority.MEDIUM -> itemView.context.getColor(android.R.color.holo_blue_dark)
                Priority.HIGH -> itemView.context.getColor(android.R.color.holo_orange_dark)
                Priority.URGENT -> itemView.context.getColor(android.R.color.holo_red_dark)
            }
            priorityTextView.setTextColor(priorityColor)

            // Set due date
            if (todo.dueDate != null) {
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                dueDateTextView.text = "Due: ${todo.dueDate.format(formatter)}"
                dueDateTextView.visibility = View.VISIBLE
            } else {
                dueDateTextView.visibility = View.GONE
            }

            completedCheckBox.setOnCheckedChangeListener { _, _ ->
                onToggle(todo)
            }

            deleteButton.setOnClickListener {
                onDelete(todo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem) =
            oldItem == newItem
    }
}
