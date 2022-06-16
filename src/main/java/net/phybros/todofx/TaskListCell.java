package net.phybros.todofx;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
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
            populateTask(txtTask);

            editContent.setText(TxtTodoConverter.makeString(txtTask));
            editContent.setOnAction(event -> commitEdit(getItem()));

            setGraphic(taskContent);
        } else {
            // set everything blank
            setGraphic(null);
        }
    }

    void populateTask(TxtTask txtTask)
    {
        //taskContent.getChildren().clear();

        if (txtTask.isCompleted()) {
            // If it's done, just show it raw and gray and italic
            completedTask.setText(TxtTodoConverter.makeString(txtTask));
//            taskContent.getChildren().addAll(completedTask);
            taskContent = new TextFlow(completedTask);
        } else {

            if (txtTask.isCompleted()) {
                completed.setText("x ");
            } else {
                completed.setText("");
            }

            if (txtTask.getPriority() != null) {
                priority.setText(String.format("(%s) ", txtTask.getPriority()));
            } else {
                priority.setText("");
            }

            if (txtTask.getCompletionDate() != null) {
                String dateString = TxtTodoConverter.dateToString(txtTask.getCompletionDate());
                completionDate.setText(String.format("%s ", dateString));
            } else {
                completionDate.setText("");
            }

            if (txtTask.getCreationDate() != null) {
                String dateString = TxtTodoConverter.dateToString(txtTask.getCreationDate());
                creationDate.setText(String.format("%s ", dateString));
            } else {
                creationDate.setText("");
            }

            List<Node> nodes = new ArrayList<>();
            nodes.add(completed);
            nodes.add(priority);
            nodes.add(completionDate);
            nodes.add(creationDate);
            nodes.addAll(TxtTodoConverter.makeTextFlow(txtTask));

            taskContent = new TextFlow(nodes.toArray(new Node[0]));
//            taskContent.getChildren().addAll(completed, priority, completionDate, creationDate, name);
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
        txtTask.update(editContent.getText().trim());
        super.commitEdit(txtTask);

        populateTask(getItem());

        setGraphic(taskContent);
        taskContent.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        System.out.println("Cancelled editing");
    }


}
