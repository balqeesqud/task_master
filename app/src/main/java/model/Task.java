package model;


import java.util.Date;

import enums.TaskState;

public class Task {
    public Long id;
    private String title;
    private String body;
    private TaskState state;

    java.util.Date dateCreated;

    public Task(String title, String body, Date dateCreated, TaskState state) {
        this.title = title;
        this.body = body;
        this.dateCreated = dateCreated;
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }
}
