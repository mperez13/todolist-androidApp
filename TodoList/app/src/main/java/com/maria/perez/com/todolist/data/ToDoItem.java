package com.maria.perez.com.todolist.data;


public class ToDoItem {
    private String description;
    private String dueDate;

    // Add category and completed
    private String category;
    private boolean completed;

    public ToDoItem(String description, String dueDate, String category, boolean completed) {
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.completed = completed;
    }

    /* Getter and setter for DESCRIPTION */
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) { this.description = description; }

    /* Getter and setter for DUE DATE */
    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    /* Getter and setter for CATEGORY */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /* Getter and setter for COMPLETED */
    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed){ this.completed = completed;
    }
}
