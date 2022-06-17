package net.phybros.todofx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.prefs.Preferences;

import static java.nio.file.StandardWatchEventKinds.*;

public class HelloApplication extends Application {

    public static final String prefsPath = "/net/phybros/todofx";
    public static final String todoTxtFileName = "TodoTxtFileName";
    private File chosenFile;

    @Override
    public void start(Stage stage) throws IOException {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        openFile(stage, prefs, false);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Open a new file
        KeyCombination openFileAccelerator = new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN);
        Runnable openFileRunnable = () -> {
            try {
                openFile(stage, prefs, true);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
        scene.getAccelerators().put(openFileAccelerator, openFileRunnable);

        // Save
        KeyCombination saveAccelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
        Runnable saveRunnale = () -> {
            try {
                TxtTodoManager.getInstance().writeFile();
                stage.setTitle("TodoTxtFX");
            } catch (IOException f) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("todo.txt not readable/writeable");
                alert.setContentText(String.format("Error: %s", f.getMessage()));
                alert.showAndWait();
                Platform.exit();
            }
        };

        scene.getAccelerators().put(saveAccelerator, saveRunnale);

        stage.setTitle("TodoTxtFX");
        stage.setScene(scene);

        stage.show();

        setUpFileWatcher();
    }

    public void openFile(Stage stage, Preferences prefs, boolean reload) throws FileNotFoundException {
        if (prefs.node(prefsPath).get(todoTxtFileName, "").isEmpty() || reload) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Todo.txt Files", "txt"));
            chosenFile = fileChooser.showOpenDialog(stage);

            if (chosenFile == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No File Chosen");
                alert.setContentText("Please relaunch the app and choose a todo.txt file");
                alert.showAndWait();
                Platform.exit();
            } else {
                prefs.node(prefsPath).put(todoTxtFileName, chosenFile.getAbsolutePath());
            }
        } else {
            chosenFile = new File(prefs.node(prefsPath).get(todoTxtFileName, ""));
        }

        if (chosenFile != null && chosenFile.exists() && chosenFile.canRead() && chosenFile.canWrite()) {
            TxtTodoManager.getInstance().setTodoTxtPath(chosenFile.toPath());
            TxtTodoManager.getInstance().readFile();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("todo.txt not readable");
            alert.setContentText(String.format("Couldn't read/write/find %s", chosenFile != null ? chosenFile.getAbsolutePath() : "File was null"));
            alert.showAndWait();
            Platform.exit();
        }
    }


    private void setUpFileWatcher() {
        Thread th = new Thread(() -> {
            // TODO: Set up file watcher
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = Paths.get(chosenFile.getParent());
                path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                boolean poll = true;
                while (poll) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        // TODO: Do some action when there is something detected
                        Platform.runLater(() -> {
                            Instant lastSave = TxtTodoManager.getInstance().getLastSave();
                            Instant fiveSecondsAgo = Instant.now().minus(Duration.ofSeconds(15));

                            if (lastSave.isBefore(fiveSecondsAgo)) {
                                if (event.context().toString().equals(chosenFile.getName()) && event.kind() == ENTRY_MODIFY) {
                                    try {
                                        TxtTodoManager.getInstance().readFile();
                                    } catch (FileNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            } else {
                                System.out.println("Saved recently, ignoring");
                            }
                        });
                    }
                    poll = key.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        th.setDaemon(true);
        th.start();
    }

    public static void main(String[] args) {
        launch();
    }
}