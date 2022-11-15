package tests;

import manager.FileBackedTaskManager;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class FileBackedTaskManagerTest extends TaskManagerTest {
    private static final String FILE_CONTENT_WHEN_WRITING = "id,type,name,status,description,epic,start_time,duration" +
            "1,TASK,Task,NEW,Task description,2022-10-22T21:41:21,360000" +
            "2,EPIC,Epic1,NEW,Epic description1,2022-10-23T22:22:22.000000010,122460000" +
            "3,EPIC,Epic2,NEW,Epic description1,null,null" +
            "4,SUBTASK,Subtask,NEW,Subtask description,2,2022-10-23T22:22:22.000000010,3660000" +
            "5,SUBTASK,Subtask,NEW,Subtask description,2,2022-10-24T22:22:22.000000010,36060000" +

            "2,3,4,1";
    Task task = new Task("Task", "Task description", 1, Status.NEW, LocalDateTime.of(2022, 10, 22, 21, 41, 21, 0), Duration.ofMinutes(6));
    Epic epic1 = new Epic("Epic1", "Epic description1", 2);
    Epic epic2 = new Epic("Epic2", "Epic description1", 3);
    Subtask subtask1 = new Subtask("Subtask", "Subtask description", 2, 4, Status.NEW, LocalDateTime.of(2022, 10, 23, 22, 22, 22, 10), Duration.ofMinutes(61L));
    Subtask subtask2 = new Subtask("Subtask", "Subtask description", 2, 5, Status.NEW, LocalDateTime.of(2022, 10, 24, 22, 22, 22, 10), Duration.ofMinutes(601L));


    @BeforeEach
    public void createClass() throws IOException {
        manager = new FileBackedTaskManager(Paths.get("tasks.txt"));
    }

    @Test
    public void writeFileTests() {
//        given
        manager = new FileBackedTaskManager(Paths.get("tasks.txt"));
        manager.addTask(task);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.getEpic(2);
        manager.getEpic(3);
        manager.getTask(1);
        manager.getSubtask(4);
        manager.getTask(1);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("tasks.txt"), StandardCharsets.UTF_8)) {
//            when
            StringBuilder fileData = new StringBuilder();
            while (reader.ready()) {
                fileData.append(reader.readLine());
            }
//            then
            Assertions.assertEquals(FILE_CONTENT_WHEN_WRITING, fileData.toString(), "Записанные значения отличаются от ожидаемых");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readFileTests() {
//        when
        manager = Managers.loadFromFile(Paths.get("tasksTest.txt"));
//        then
        Assertions.assertEquals(task, manager.getTasks().values().toArray()[0], "Ошибка при чтении задач");
        Assertions.assertEquals(subtask1, manager.getSubtask(4), "Ошибка при чтении подзадачи 1");
        Assertions.assertEquals(subtask2, manager.getSubtask(5), "Ошибка при чтении подзадачи 2");
        Assertions.assertEquals(epic2, manager.getEpic(3), "Ошибка при чтении эпика 2");
        Assertions.assertEquals(List.of(task, subtask1, subtask2, epic2), manager.getHistory(), "Ошибка при чтении истории");
    }
}