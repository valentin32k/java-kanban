package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.KVTaskClient;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient tasksServerClient;

    public HttpTaskManager(URI serverUrl) {
        super(null);
        tasksServerClient = new KVTaskClient(serverUrl);
        loadValues();
    }

    private void loadValues() {
        Gson gson = new Gson();
        String tasksStr = tasksServerClient.load("tasks");
        String epicsStr = tasksServerClient.load("epics");
        String subtasksStr = tasksServerClient.load("subtasks");
        String historyStr = tasksServerClient.load("history");
        if (!tasksStr.isEmpty()) {
            ArrayList<Task> tasksArray = gson.fromJson(tasksStr, new TypeToken<Collection<Task>>() {}.getType());
            tasksArray.forEach(this::addTask);
        }
        if (!subtasksStr.isEmpty()) {
            ArrayList<Epic> epicsArray = gson.fromJson(epicsStr, new TypeToken<ArrayList<Epic>>() {}.getType());
            epicsArray.forEach(this::addEpic);
        }
        if (!subtasksStr.isEmpty()) {
            ArrayList<Subtask> subtasksArray = gson.fromJson(subtasksStr, new TypeToken<ArrayList<Subtask>>() {}.getType());
            subtasksArray.forEach(this::addSubtask);
        }
        if (!historyStr.isEmpty()) {
            ArrayList<Integer> historyIds = gson.fromJson(historyStr, new TypeToken<ArrayList<Integer>>() {}.getType());
            for (Integer id : historyIds) {
                getTask(id);
                getEpic(id);
                getSubtask(id);
            }
        }
    }

    @Override
    protected void save() {
        try {
            Gson gson = new Gson();
            tasksServerClient.put("tasks", gson.toJson(tasksById.values()));
            tasksServerClient.put("epics", gson.toJson(epicsById.values()));
            tasksServerClient.put("subtasks", gson.toJson(subtasksById.values()));
            List<AbstractTask> historyTasks = getHistory();
            List<Integer> historyIds = new ArrayList<>();
            historyTasks.forEach(h -> historyIds.add(h.getId()));
            tasksServerClient.put("history", gson.toJson(historyIds));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}