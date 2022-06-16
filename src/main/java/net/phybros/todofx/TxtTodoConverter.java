package net.phybros.todofx;

import javafx.scene.control.Label;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateToString(Date date) {
        return formatter.format(date);
    }

    public static TxtTask fromString(String input) {
        TxtTask result = new TxtTask();


        Matcher m = taskPattern.matcher(input);
        if (m.find()) {
            result.setCompleted(m.group("completed") != null);
            result.setPriority(m.group("priority"));
            result.setName(m.group("remainder"));

            try {
                String completionDateString = m.group("completionDate");
                result.setCompletionDate(completionDateString != null ? formatter.parse(completionDateString) : null);

                String creationDateString = m.group("creationDate");
                result.setCreationDate(creationDateString != null ? formatter.parse(creationDateString) : null);
            } catch (ParseException e) {
                // don't care
            }


        }

        System.out.println(result.toString());
        return result;
    }

    public static String makeString(TxtTask task) {
        StringBuilder sb = new StringBuilder("");

        if (task.isCompleted()) sb.append("x ");
        if (task.getPriority() != null) {
            sb.append(String.format("(%s) ", task.getPriority()));
        }
        if (task.getCompletionDate() != null) {
            sb.append(formatter.format(task.getCompletionDate()));
            sb.append(" ");
        }
        if (task.getCreationDate() != null) {
            sb.append(formatter.format(task.getCreationDate()));
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
            String preceding = task.getName().substring(lastIndex, b.getStart());
            String projectName = task.getName().substring(b.getStart(), b.getEnd());

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

    public static void styleRange(String text, int start, int end) {

    }

    private static List<Label> getLabelsForRegex(String input, Matcher m, String groupName, String cssClass) {
        List<Label> results = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();


        while (m.find()) {
            starts.add(m.start(groupName));
            ends.add(m.end(groupName));
        }

        int lastStart = 0;

        for (int i = 0; i < starts.size(); i++) {
            int start = starts.get(i);
            int end = ends.get(i);

            String preceding = input.substring(lastStart, start);
            String projectName = input.substring(start, end);

            results.add(new Label(preceding));

            Label projectLabel = new Label(projectName);
            projectLabel.getStyleClass().add(cssClass);
            results.add(projectLabel);

            lastStart = end;
        }

        Label remainder = new Label(input.substring(lastStart));
        results.add(remainder);

        return results;
    }
}
