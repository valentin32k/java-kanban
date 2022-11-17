package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
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
        List<Integer> historyIds = new ArrayList<>();
        tasks.forEach(h -> historyIds.add(h.getId()));
        return gson.toJson(historyIds);
    }

    @Override
    public List<Integer> historyFromString(String value) {
        return gson.fromJson(value, new TypeToken<ArrayList<Integer>>() {
        }.getType());
    }
}
