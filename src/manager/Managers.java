package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Managers {
    public static TaskManager getDefault(URI serverUrl) {
        return new HttpTaskManager(serverUrl);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager loadFromFile(Path tasksFile) {
        FileBackedTaskManager manager = new FileBackedTaskManager(tasksFile);
        CsvTaskSerializer csvTaskSerializer = new CsvTaskSerializer();
        try (BufferedReader reader = Files.newBufferedReader(tasksFile, StandardCharsets.UTF_8)) {
            while (reader.ready()) {
                String tmpString = reader.readLine();
                String[] tmpArray = tmpString.split(",");
                if (!tmpString.isEmpty()) {
                    switch (tmpArray[1]) {
                        case "TASK":
                            manager.addTask(csvTaskSerializer.taskFromString(tmpString));
                            break;
                        case "EPIC":
                            manager.addEpic(csvTaskSerializer.epicFromString(tmpString));
                            break;
                        case "SUBTASK":
                            manager.addSubtask(csvTaskSerializer.subtaskFromString(tmpString));
                            break;
                    }
                } else {
                    String str = reader.readLine();
                    List<Integer> historyFromFile = csvTaskSerializer.historyFromString(str);
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