package manager;

import com.google.gson.Gson;
import server.KVTaskClient;

import java.net.URI;

public class HTTPTaskManager extends FileBackedTaskManager {
    private KVTaskClient tasksServerClient;

    public HTTPTaskManager(URI serverUrl) {
        super(null);
        tasksServerClient = new KVTaskClient(serverUrl);
    }

    @Override
    protected void save() {
        try {
            Gson gson = new Gson();
            String jsonTasks = gson.toJson(getTasks());
            String jsonSubtasks = gson.toJson(getSubtasks());
            String jsonEpics = gson.toJson(getEpics());
            String jsonHistory = gson.toJson(getHistory());
            tasksServerClient.put("tasks", jsonTasks);
            tasksServerClient.put("subtasks", jsonSubtasks);
            tasksServerClient.put("epics", jsonEpics);
            tasksServerClient.put("history", jsonHistory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}