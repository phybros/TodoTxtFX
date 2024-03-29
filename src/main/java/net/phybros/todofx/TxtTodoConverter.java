package net.phybros.todofx;

import javafx.scene.control.Label;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtTodoConverter {

    private static final String projectsRegex = " (?<projects>\\+\\S+)";
    private static final Pattern projectsPattern = Pattern.compile(projectsRegex);
    private static final String contextsRegex = " (?<contexts>@\\S+)";
    private static final Pattern contextsPattern = Pattern.compile(contextsRegex);
    private static final String todoTxtPattern = "^(?<completed>x )?(\\((?<priority>[A-Z])\\))? ?(?<completionDate>\\d{4}-\\d{2}-\\d{2})? ?(?<creationDate>\\d{4}-\\d{2}-\\d{2})? ?(?<remainder>.*)$";
    private static final Pattern taskPattern = Pattern.compile(todoTxtPattern);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String dateToString(LocalDate date) {
        return date.format(formatter);
    }

    public static TxtTask fromString(String input) {
        TxtTask result = new TxtTask();
        result.setRaw(input.trim());

        Matcher m = taskPattern.matcher(input);

        if (m.find()) {
            result.setCompleted(m.group("completed") != null);
            result.setPriority(m.group("priority"));
            result.setName(m.group("remainder"));

            String completionDateString = m.group("completionDate");
            result.setCompletionDate(completionDateString != null ? LocalDate.parse(completionDateString, formatter) : null);

            String creationDateString = m.group("creationDate");
            result.setCreationDate(creationDateString != null ? LocalDate.parse(creationDateString, formatter) : null);
        }

        return result;
    }

    public static String makeString(TxtTask task) {
        StringBuilder sb = new StringBuilder("");

        if (task.isCompleted()) sb.append("x ");
        if (task.getPriority() != null) {
            sb.append(String.format("(%s) ", task.getPriority()));
        }
        if (task.getCompletionDate() != null) {
            sb.append(task.getCompletionDate().format(formatter));
            sb.append(" ");
        }
        if (task.getCreationDate() != null) {
            sb.append(task.getCreationDate().format(formatter));
            sb.append(" ");
        }
        if (task.getName() != null) {
            sb.append(task.getName());
        }

        return sb.toString();
    }

    public static List<Label> makeTextFlow(TxtTask task) {
        List<Label> results = new ArrayList<>();
        String taskName = task.getName();

        Matcher projectsMatcher = projectsPattern.matcher(taskName);
        Matcher contextsMatcher = contextsPattern.matcher(taskName);

        List<TextBoundary> boundaries = new ArrayList<>();

        while (projectsMatcher.find()) {
            boundaries.add(new TextBoundary(projectsMatcher.start("projects"), projectsMatcher.end("projects"), "project"));
        }

        while (contextsMatcher.find()) {
            boundaries.add(new TextBoundary(contextsMatcher.start("contexts"), contextsMatcher.end("contexts"), "context"));
        }

        boundaries.sort(Comparator.comparing(TextBoundary::getStart));

        int lastIndex = 0;

        for (TextBoundary b : boundaries) {
            String preceding = taskName.substring(lastIndex, b.getStart());
            String projectName = taskName.substring(b.getStart(), b.getEnd());

            results.add(new Label(preceding));

            Label projectLabel = new Label(projectName);
            projectLabel.getStyleClass().add(b.getType());
            results.add(projectLabel);

            lastIndex = b.getEnd();
        }

        Label remainder = new Label(task.getName().substring(lastIndex));
        results.add(remainder);

        return results;
    }
}
