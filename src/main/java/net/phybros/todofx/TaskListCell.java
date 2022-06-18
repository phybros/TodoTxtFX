package net.phybros.todofx;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

public class TaskListCell extends ListCell<TxtTask> {
    private final Label completedTask;
    private final Label completed;
    private final Label priority;
    private final Label completionDate;
    private final Label creationDate;
    private final TextField editContent;
    private TextFlow taskContent;
    private List<Label> textFlowContents;
    private TxtTask theTask;

    public TxtTask getTheTask() {
        return theTask;
    }

    public void setTheTask(TxtTask theTask) {
        this.theTask = theTask;
    }

    public TaskListCell() {
        super();

        completedTask = new Label();


        completedTask.getStyleClass().add("completed-task");
        completed = new Label();
        priority = new Label();
        priority.getStyleClass().add("priority");

        completionDate = new Label();
        creationDate = new Label();

        editContent = new TextField();
    }

    @Override
    protected void updateItem(TxtTask txtTask, boolean empty) {
        super.updateItem(txtTask, empty);

        if (txtTask != null && !empty) {
//            populateTask(txtTask);

            setGraphic(taskContent);
        } else {
            // set everything blank
            setGraphic(null);
        }

    }

    public void populateTask()
    {
        if (!editContent.getText().equals(theTask.getName())) {
            editContent.setText(TxtTodoConverter.makeString(theTask));
            editContent.setOnAction(event -> {
                commitEdit(getItem());
            });
            editContent.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        if (theTask.isCompleted()) {
            // If it's done, just show it raw and gray and italic
            completedTask.setText(TxtTodoConverter.makeString(theTask));
//            taskContent.getChildren().addAll(completedTask);
            taskContent = new TextFlow(completedTask);
        } else {

            if (theTask.isCompleted()) {
                completed.setText("x ");
            } else {
                completed.setText("");
            }

            if (theTask.getPriority() != null) {
                priority.setText(String.format("(%s) ", theTask.getPriority()));
            } else {
                priority.setText("");
            }

            if (theTask.getCompletionDate() != null) {
                String dateString = TxtTodoConverter.dateToString(theTask.getCompletionDate());
                completionDate.setText(String.format("%s ", dateString));
            } else {
                completionDate.setText("");
            }

            if (theTask.getCreationDate() != null) {
                String dateString = TxtTodoConverter.dateToString(theTask.getCreationDate());
                creationDate.setText(String.format("%s ", dateString));
            } else {
                creationDate.setText("");
            }

                List<Node> nodes = new ArrayList<>();
                nodes.add(completed);
                nodes.add(priority);
                nodes.add(completionDate);
                nodes.add(creationDate);

                textFlowContents = TxtTodoConverter.makeTextFlow(theTask);
                nodes.addAll(textFlowContents);

                taskContent = new TextFlow(nodes.toArray(new Node[0]));
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        setGraphic(editContent);
        editContent.selectAll();
        editContent.requestFocus();
    }

    @Override
    public void commitEdit(TxtTask txtTask) {
        System.out.println("Done editing");
        String newName = editContent.getText().trim();
        if (!newName.equals(txtTask.getName())) {
            TxtTodoManager.getInstance().setDirty(true);
        }

        txtTask.update(editContent.getText().trim());
        super.commitEdit(txtTask);
        this.theTask = txtTask;
        populateTask();

        setGraphic(taskContent);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(taskContent);
    }


}
