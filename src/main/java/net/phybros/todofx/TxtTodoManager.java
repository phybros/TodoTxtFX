package net.phybros.todofx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class TxtTodoManager {
    private static TxtTodoManager single_instance = null;

    private Path todoTxtPath;

    private Instant lastSave = Instant.EPOCH;

    private boolean ignoreDataChanges = false;

    private boolean dirty = false;

    public Instant getLastSave() {
        return lastSave;
    }

    public void setLastSave(Instant lastSave) {
        this.lastSave = lastSave;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    private ObservableList<TxtTask> tasks = FXCollections.observableList(new ArrayList<>());

    public ObservableList<TxtTask> getTasks() {
        return tasks;
    }

    public void setTasks(ObservableList<TxtTask> tasks) {
        this.tasks = tasks;
    }

    public boolean isIgnoreDataChanges() {
        return ignoreDataChanges;
    }

    public void setIgnoreDataChanges(boolean ignoreDataChanges) {
        this.ignoreDataChanges = ignoreDataChanges;
    }

    public Path getTodoTxtPath() {
        return todoTxtPath;
    }

    public void setTodoTxtPath(Path todoTxtPath) {
        this.todoTxtPath = todoTxtPath;
    }

    public void readFile() throws FileNotFoundException {
        this.ignoreDataChanges = true;
        File txtFile = new File(getInstance().getTodoTxtPath().toString());

        if (txtFile.exists() && txtFile.canRead()) {
            tasks.clear();
            Scanner sc = new Scanner(txtFile);
            while (sc.hasNextLine()) {
                TxtTask newTask = TxtTodoConverter.fromString(sc.nextLine());
                tasks.add(newTask);
            }
        }

        this.sortTasks();
        this.ignoreDataChanges = false;
    }

    public void writeFile() throws IOException {
        this.ignoreDataChanges = true;
        StringBuilder sb = new StringBuilder();

        tasks.forEach(task -> {
            sb.append(TxtTodoConverter.makeString(task));
            sb.append("\n");
        });

        try {
            FileWriter w = new FileWriter(getInstance().getTodoTxtPath().toFile());
            w.write(sb.toString());
            w.close();
            getInstance().setDirty(false);
            lastSave = Instant.now();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            this.ignoreDataChanges = true;
        }
    }

    private TxtTodoManager() {

    }

    // Static method
    // Static method to create instance of Singleton class
    public static TxtTodoManager getInstance() {
        if (single_instance == null)
            single_instance = new TxtTodoManager();

        return single_instance;
    }

    public void sortTasks() {
        this.ignoreDataChanges = true;
        if (this.tasks.size() > 1) {
            Collections.sort(this.tasks, Comparator.nullsLast(
                            Comparator.comparing(TxtTask::isCompleted, Comparator.nullsLast(Comparator.naturalOrder()))
                                    .thenComparing(TxtTask::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                                    .thenComparing(TxtTask::getName, Comparator.nullsLast(Comparator.naturalOrder()))
                    )
            );
        }
        this.ignoreDataChanges = false;
    }
}
