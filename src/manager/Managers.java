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

    public static FileBackedTaskManager loadFromFile(Path tasksFile) {
        FileBackedTaskManager manager = new FileBackedTaskManager(tasksFile);
        try (BufferedReader reader = Files.newBufferedReader(tasksFile, StandardCharsets.UTF_8)) {
//            Мне казалось, что создавать новые переменные на каждой итерации цикла - это лишние затраты машинного времени.
//            Тем более, что область видимости этих переменных ограничена try, в котором ничего кроме цикла и нет
            while (reader.ready()) {
                String tmpString = reader.readLine();
                String[] tmpArray = tmpString.split(",");
                if (!tmpString.isEmpty()) {
                    switch (tmpArray[1]) {
                        case "TASK":
                            manager.addTask(Task.fromCsvString(tmpString));
                            break;
                        case "EPIC":
                            manager.addEpic(Epic.fromCsvString(tmpString));
                            break;
                        case "SUBTASK":
                            manager.addSubtask(Subtask.fromCsvString(tmpString));
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