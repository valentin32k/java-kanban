package manager;

import com.google.gson.Gson;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonTaskSerializer implements TaskSerializer {
    private final Gson gson = new Gson();

    @Override
    public String taskToString(Task task) {
        return gson.toJson(task);
    }

    @Override
    public Task taskFromString(String value) {
        return gson.fromJson(value, Task.class);
    }

    @Override
    public String subtaskToString(Subtask subtask) {
        return gson.toJson(subtask);
    }

    @Override
    public Subtask subtaskFromString(String value) {
        return gson.fromJson(value, Subtask.class);
    }

    @Override
    public String epicToString(Epic epic) {
        return gson.toJson(epic);
    }

    @Override
    public Epic epicFromString(String value) {
        return gson.fromJson(value, Epic.class);
    }

    @Override
    public String historyToString(List<AbstractTask> tasks) {
        List<String> list = new ArrayList<>();
        for (AbstractTask task : tasks) {
            list.add(String.valueOf(task.getId()));
        }
        String s = String.join(",", list);
        return gson.toJson(s);
    }

    @Override
    public List<Integer> historyFromString(String value) {
        if (value == null) {
            return null;
        }
        String[] tasksId = value.split(",");
        List<Integer> list = new ArrayList<>();
        Arrays.stream(tasksId).forEach(t -> list.add(Integer.valueOf(t)));
        return list;
    }

    public String prioritizedTaskToString(AbstractTask task) {
        if (Task.class.equals(task.getClass())) {
            return taskToString((Task) task);
        } else if (Subtask.class.equals(task.getClass())) {
            return subtaskToString((Subtask) task);
        } else {
            return null;
        }
    }

    public AbstractTask prioritizedTaskFromString(String value) {
        Subtask subtask = subtaskFromString(value);
        if (subtask.getEpicId() != 0) {
            return subtask;
        } else {
            return taskFromString(value);
        }
    }
}
