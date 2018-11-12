package com.example.i857501.todoapp;

public class Task {
    private String description;
    private boolean done;

    public Task(String decsription) {
        this.description = decsription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String toString() {
        String check = this.done ? "[X]" : "[  ]";
        return String.format("%s %s", check, this.description);
    }

    public void switchDone() {
        this.done = !done;
    }
}
