package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CsvConverters {
    public static final String HEADER_LINE = "id,type,name,status,description,epic,start_time,duration\n";

    public static String taskToCsvString(Task task) {
        LocalDateTime startTime = task.getStartTime();
        String startTimeString;
        if (startTime == null) {
            startTimeString = "null";
        } else {
            startTimeString = startTime.toString();
        }
        Duration duration = task.getDuration();
        String durationString;
        if (duration == null) {
            durationString = "null";
        } else {
            durationString = String.valueOf(duration.toMillis());
        }
        return task.getId() +
                "," + TaskType.TASK +
                "," + task.getName() +
                "," + task.getStatus() +
                "," + task.getDescription() +
                "," + startTimeString +
                "," + durationString;
    }

    public static Task taskFromCsvString(String value) {
        String[] taskArray = value.split(",");
        LocalDateTime startTime;
        if (taskArray[5] == null) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(taskArray[5]);
        }
        return new Task(taskArray[2],
                taskArray[4],
                Integer.valueOf(taskArray[0]),
                Status.valueOf(taskArray[3]),
                startTime,
                Duration.ofMillis(Long.valueOf(taskArray[6])));
    }

    public static String subtaskToCsvString(Subtask subtask) {
        return subtask.getId() +
                "," + TaskType.SUBTASK +
                "," + subtask.getName() +
                "," + subtask.getStatus() +
                "," + subtask.getDescription() +
                "," + subtask.getEpicId() +
                "," + subtask.getStartTime().toString() +
                "," + subtask.getDuration().toMillis();
    }

    public static Subtask subtaskFromCsvString(String value) {
        String[] subtaskArray = value.split(",");
        return new Subtask(subtaskArray[2],
                subtaskArray[4],
                Integer.valueOf(subtaskArray[5]),
                Integer.valueOf(subtaskArray[0]),
                Status.valueOf(subtaskArray[3]),
                LocalDateTime.parse(subtaskArray[6]),
                Duration.ofMillis(Long.valueOf(subtaskArray[7])));
    }

    public static String epicToCsvString(Epic epic) {
        String startTime;
        if (epic.getStartTime() == null) {
            startTime = "null";
        } else {
            startTime = epic.getStartTime().toString();
        }
        String duration;
        if (epic.getDuration() == null) {
            duration = "null";
        } else {
            duration = String.valueOf(epic.getDuration().toMillis());
        }
        return epic.getId() +
                "," + TaskType.EPIC +
                "," + epic.getName() +
                "," + epic.getStatus() +
                "," + epic.getDescription() +
                "," + startTime +
                "," + duration;
    }

    public static Epic epicFromCsvString(String value) {
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
