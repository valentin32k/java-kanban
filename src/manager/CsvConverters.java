package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvConverters {

    public static String taskToCsvString(Task task) {
        return task.getId() +
                "," + TaskType.TASK +
                "," + task.getName() +
                "," + task.getStatus() +
                "," + task.getDescription() +
                "," + task.getStartTime().toString() +
                "," + task.getDuration().toMillis();
    }

    public static Task taskFromCsvString(String value) {
        String[] taskArray = value.split(",");
        return new Task(taskArray[2],
                taskArray[4],
                Integer.valueOf(taskArray[0]),
                Status.valueOf(taskArray[3]),
                LocalDateTime.parse(taskArray[5]),
                Duration.ofMillis(Long.valueOf(taskArray[6])));
    }

    public static String SubtaskToCsvString(Subtask subtask) {
        return subtask.getId() +
                "," + TaskType.SUBTASK +
                "," + subtask.getName() +
                "," + subtask.getStatus() +
                "," + subtask.getDescription() +
                "," + subtask.getEpicId() +
                "," + subtask.getStartTime().toString() +
                "," + subtask.getDuration().toMillis();
    }

    public static Subtask SubtaskFromCsvString(String value) {
        String[] subtaskArray = value.split(",");
        return new Subtask(subtaskArray[2],
                subtaskArray[4],
                Integer.valueOf(subtaskArray[5]),
                Integer.valueOf(subtaskArray[0]),
                Status.valueOf(subtaskArray[3]),
                LocalDateTime.parse(subtaskArray[6]),
                Duration.ofMillis(Long.valueOf(subtaskArray[7])));
    }

    public static String EpicToCsvString(Epic epic) {
        return epic.getId() +
                "," + TaskType.EPIC +
                "," + epic.getName() +
                "," + epic.getStatus() +
                "," + epic.getDescription();
    }

    public static Epic EpicFromCsvString(String value) {
        String[] epicArray = value.split(",");
        return new Epic(epicArray[2], epicArray[4], Integer.valueOf(epicArray[0]));
    }

    static String historyToCsvString(HistoryManager manager) {
        List<String> list = new ArrayList<>();
        for (AbstractTask task : manager.getHistory()) {
            list.add(String.valueOf(task.getId()));
        }
        return list.stream().collect(Collectors.joining(","));
    }

    static List<Integer> historyFromCsvString(String value) {
        if (value != null) {
            String[] tasksId = value.split(",");
            List<Integer> list = new ArrayList<>();
            for (String id : tasksId) {
                list.add(Integer.valueOf(id));
            }
            return list;
        } else {
            return null;
        }
    }
}
