package net.phybros.todofx;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Date;

public class HelloController {
    @FXML
    private TextField taskEntry;

    public static final String[] priorities = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    @FXML
    private ListView<TxtTask> taskList;

    int priorityToIndex(String priority) {
        for (int i = 0; i < priorities.length; i++) {
            if (priorities[i].equals(priority)) {
                return i;
            }
        }

        return 0;
    }

    @FXML
    public void onCommitEdit() {
        TxtTodoManager.getInstance().sortTasks();
        taskList.requestFocus();
    }

    @FXML
    public void initialize() {
        // make the list view editable
        taskList.setEditable(true);
        taskList.setCellFactory(new Callback<ListView<TxtTask>, ListCell<TxtTask>>() {
            @Override
            public ListCell<TxtTask> call(ListView<TxtTask> listView) {
                TaskListCell cell = new TaskListCell();
                return cell;
            }
        });

        // Set up the backing data for the list view
        taskList.setItems(TxtTodoManager.getInstance().getTasks());

        taskList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if (taskList.getItems().size() > 0) {
            taskList.getSelectionModel().select(TxtTodoManager.getInstance().getTasks().get(0));
        }

        taskList.setOnKeyPressed(keyEvent -> {
            if (keyEvent.isShortcutDown()) {

                TxtTask t;
                int newPriIndex;

                switch (keyEvent.getCode()) {
                    case UP:
                        t = taskList.getSelectionModel().getSelectedItem();
                        newPriIndex = priorityToIndex(t.getPriority()) - 1;
                        if (newPriIndex < 0) newPriIndex = 0;
                        t.setPriority(priorities[newPriIndex]);
                        TxtTodoManager.getInstance().getTasks().set(taskList.getSelectionModel().getSelectedIndex(), t);
                        TxtTodoManager.getInstance().sortTasks();

                        keyEvent.consume();

                        break;

                    case DOWN:
                        t = taskList.getSelectionModel().getSelectedItem();
                        newPriIndex = priorityToIndex(t.getPriority()) + 1;
                        if (newPriIndex >= priorities.length) newPriIndex = priorities.length - 1;
                        t.setPriority(priorities[newPriIndex]);
                        TxtTodoManager.getInstance().getTasks().set(taskList.getSelectionModel().getSelectedIndex(), t);
                        TxtTodoManager.getInstance().sortTasks();
                        keyEvent.consume();
                        break;

                    case LEFT:
                        t = taskList.getSelectionModel().getSelectedItem();
                        t.setPriority(null);
                        TxtTodoManager.getInstance().getTasks().set(taskList.getSelectionModel().getSelectedIndex(), t);
                        TxtTodoManager.getInstance().sortTasks();
                        keyEvent.consume();
                        break;

                    case N:
                        taskEntry.requestFocus();
                        keyEvent.consume();
                        break;

                    default:
                        break;
                }
            } else {
                if (keyEvent.getCode() == KeyCode.X) {
                    TxtTask t = taskList.getSelectionModel().getSelectedItem();

                    if (t.isCompleted()) {
                        t.setCompletionDate(null);
                        t.setCompleted(false);
                    } else {
                        t.setCompleted(true);
                        t.setCompletionDate(new Date());
                        t.setPriority(null);
                    }

                    TxtTodoManager.getInstance().getTasks().set(taskList.getSelectionModel().getSelectedIndex(), t);
                    TxtTodoManager.getInstance().sortTasks();
                    keyEvent.consume();
                }
            }
        });

        TxtTodoManager.getInstance().getTasks().addListener(new ListChangeListener<TxtTask>() {
            @Override
            public void onChanged(Change<? extends TxtTask> change) {
                if (!TxtTodoManager.getInstance().isIgnoreDataChanges()) {
                    TxtTodoManager.getInstance().setDirty(true);
                    ((Stage) taskList.getScene().getWindow()).setTitle("TodoTxtFX *");
                }
            }
        });
    }

    @FXML
    protected void onTaskEntrySubmit() {
        TxtTask newTask = new TxtTask();
        newTask.update(taskEntry.getText().trim());

        TxtTodoManager.getInstance().getTasks().add(newTask);

        taskList.requestFocus();
        taskList.getSelectionModel().select(newTask);
        taskEntry.setText("");
        TxtTodoManager.getInstance().sortTasks();
        TxtTodoManager.getInstance().setDirty(true);
    }
}
