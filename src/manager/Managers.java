package manager;

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

    public static FileBackedTaskManager loadFromFile(Path tasksFile) {
        FileBackedTaskManager manager = new FileBackedTaskManager(tasksFile);
        try (BufferedReader reader = Files.newBufferedReader(tasksFile, StandardCharsets.UTF_8)) {
            while (reader.ready()) {
                String tmpString = reader.readLine();
                String[] tmpArray = tmpString.split(",");
                if (!tmpString.isEmpty()) {
                    switch (tmpArray[1]) {
                        case "TASK":
                            manager.addTask(CsvConverters.taskFromCsvString(tmpString));
                            break;
                        case "EPIC":
                            manager.addEpic(CsvConverters.EpicFromCsvString(tmpString));
                            break;
                        case "SUBTASK":
                            manager.addSubtask(CsvConverters.SubtaskFromCsvString(tmpString));
                            break;
                    }
                } else {
                    String str = reader.readLine();
                    List<Integer> historyFromFile = CsvConverters.historyFromCsvString(str);
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