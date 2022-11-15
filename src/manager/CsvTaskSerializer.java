package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvTaskSerializer implements TaskSerializer {
    public static final String HEADER_LINE = "id,type,name,status,description,epic,start_time,duration\n";

    @Override
    public String taskToString(Task task) {
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
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + startTimeString + "," + durationString;
    }

    @Override
    public Task taskFromString(String value) {
        String[] taskArray = value.split(",");
        LocalDateTime startTime;
        if (taskArray[5] == null) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(taskArray[5]);
        }
        return new Task(taskArray[2], taskArray[4], Integer.parseInt(taskArray[0]), Status.valueOf(taskArray[3]), startTime, Duration.ofMillis(Long.parseLong(taskArray[6])));
    }

    @Override
    public String subtaskToString(Subtask subtask) {
        return subtask.getId() + "," + TaskType.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId() + "," + subtask.getStartTime().toString() + "," + subtask.getDuration().toMillis();
    }

    @Override
    public Subtask subtaskFromString(String value) {
        String[] subtaskArray = value.split(",");
        return new Subtask(subtaskArray[2], subtaskArray[4], Integer.parseInt(subtaskArray[5]), Integer.parseInt(subtaskArray[0]), Status.valueOf(subtaskArray[3]), LocalDateTime.parse(subtaskArray[6]), Duration.ofMillis(Long.parseLong(subtaskArray[7])));
    }

    @Override
    public String epicToString(Epic epic) {
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
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription() + "," + startTime + "," + duration;
    }

    @Override
    public Epic epicFromString(String value) {
        String[] epicArray = value.split(",");
        return new Epic(epicArray[2], epicArray[4], Integer.parseInt(epicArray[0]));
    }

    @Override
    public String historyToString(List<AbstractTask> tasks) {
        List<String> list = new ArrayList<>();
        for (AbstractTask task : tasks) {
            list.add(String.valueOf(task.getId()));
        }
        return String.join(",", list);
    }

    @Override
    public List<Integer> historyFromString(String value) {
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