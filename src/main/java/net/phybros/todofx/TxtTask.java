package net.phybros.todofx;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TxtTask {

    public TxtTask() {
        projects = new ArrayList<>();
        contexts = new ArrayList<>();
    }

    private String name;

    private boolean completed;

    private String priority;

    private LocalDate completionDate;

    private LocalDate creationDate;

    private List<String> projects;

    private List<String> contexts;

    public List<String> getContexts() {
        return contexts;
    }

    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public void update(String rawString) {
        TxtTask newTask = TxtTodoConverter.fromString(rawString);
        this.completed = newTask.isCompleted();
        this.priority = newTask.getPriority();
        this.completionDate = newTask.getCompletionDate();
        this.creationDate = newTask.getCreationDate();
        this.name = newTask.getName();
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TxtTask{" +
                "name='" + name + '\'' +
                ", completed=" + completed +
                ", priority='" + priority + '\'' +
                ", completionDate=" + completionDate +
                ", creationDate=" + creationDate +
                '}';
    }
}
