package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFileTask(Path tasksFile) {
        return new FileBackedTaskManager(tasksFile);
    }

    public static FileBackedTaskManager loadFromFile(Path tasksFile) {
        FileBackedTaskManager manager = new FileBackedTaskManager(tasksFile);
        try (BufferedReader reader = Files.newBufferedReader(tasksFile, StandardCharsets.UTF_8)) {
            String tmpString;
            String[] tmpArray;
            while (reader.ready()) {
                tmpString = reader.readLine();
                tmpArray = tmpString.split(",");
                if (!tmpString.isEmpty()) {
                    switch (tmpArray[1]) {
                        case "TASK":
                            manager.addTask(Task.fromString(tmpString));
                            break;
                        case "EPIC":
                            manager.addEpic(Epic.fromString(tmpString));
                            break;
                        case "SUBTASK":
                            manager.addSubtask(Subtask.fromString(tmpString));
                            break;
                    }
                } else {
                    String str = reader.readLine();
                    List<Integer> historyFromFile = HistoryManager.historyFromString(str);
                    if (historyFromFile != null) {
                        for (Integer id : historyFromFile) {
                            manager.getTask(id);
                            manager.getEpic(id);
                            manager.getSubtask(id);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }
}