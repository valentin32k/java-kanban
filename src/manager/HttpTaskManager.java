package manager;

import com.google.gson.JsonParser;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient tasksServerClient;

    public HttpTaskManager(URI serverUrl) {
        super(null);
        tasksServerClient = new KVTaskClient(serverUrl);
        loadValues();
    }

    private void loadValues() {
        TaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
        String tasksStr = tasksServerClient.load("tasks");
        String epicsStr = tasksServerClient.load("epics");
        String subtasksStr = tasksServerClient.load("subtasks");
        String historyString = tasksServerClient.load("history");
        if (!tasksStr.isEmpty()) {
            tasksStr = tasksStr.substring(1, tasksStr.length() - 1);
            String[] tasksArray = tasksStr.split("---");
            for (String taskStr : tasksArray) {
                Task task = jsonTaskSerializer.taskFromString(JsonParser.parseString("\"" + taskStr + "\"").getAsString());
                addTask(task);
            }
        }
        if (!epicsStr.isEmpty()) {
            epicsStr = epicsStr.substring(1, epicsStr.length() - 1);
            String[] epicsArray = epicsStr.split("---");
            for (String epicStr : epicsArray) {
                Epic epic = jsonTaskSerializer.epicFromString(JsonParser.parseString("\"" + epicStr + "\"").getAsString());
                addEpic(epic);
            }
        }
        if (!subtasksStr.isEmpty()) {
            subtasksStr = subtasksStr.substring(1, subtasksStr.length() - 1);
            String[] subtaskArray = subtasksStr.split("---");
            for (String subtaskStr : subtaskArray) {
                Subtask subtask = jsonTaskSerializer.subtaskFromString(JsonParser.parseString("\"" + subtaskStr + "\"").getAsString());
                addSubtask(subtask);
            }
        }
        if (historyString.length() > 6) {
            historyString = historyString.substring(3, historyString.length() - 3);
            List<Integer> history = jsonTaskSerializer.historyFromString(historyString);
            for (Integer id : history) {
                getTask(id);
                getEpic(id);
                getSubtask(id);
            }
        }
    }

    @Override
    protected void save() {
        try {
            TaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
            List<String> list = new ArrayList<>();
            tasksById.values().forEach(t -> list.add(jsonTaskSerializer.taskToString(t)));
            String tasksStr = String.join("---", list);
            tasksServerClient.put("tasks", tasksStr);
            list.clear();
            epicsById.values().forEach(t -> list.add(jsonTaskSerializer.epicToString(t)));
            String epicsStr = String.join("---", list);
            tasksServerClient.put("epics", epicsStr);
            list.clear();
            subtasksById.values().forEach(t -> list.add(jsonTaskSerializer.subtaskToString(t)));
            String subtaskStr = String.join("---", list);
            tasksServerClient.put("subtasks", subtaskStr);
            list.clear();
            tasksServerClient.put("history", jsonTaskSerializer.historyToString(getHistory()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}