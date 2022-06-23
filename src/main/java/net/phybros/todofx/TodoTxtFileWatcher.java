package net.phybros.todofx;

import javafx.application.Platform;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class TodoTxtFileWatcher extends FileWatcher {

    public TodoTxtFileWatcher(String watchFile) {
        super(watchFile);
    }

    @Override
    public void onModified() {
        Instant lastSave = TxtTodoManager.getInstance().getLastSave();
        Instant fiveSecondsAgo = Instant.now().minus(Duration.ofSeconds(15));

        if (lastSave.isBefore(fiveSecondsAgo)) {

            Platform.runLater(() -> {
                try {
                    TxtTodoManager.getInstance().readFile();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

        } else {
            System.out.println("File changed, but I saved recently ... ignoring.");
        }
    }
}
