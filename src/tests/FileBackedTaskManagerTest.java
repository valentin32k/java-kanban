package tests;

import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void createClass() {
        manager = new FileBackedTaskManager(Paths.get("tasks.txt"));
    }

    @Test
    public void saveTests() {
        manager.clearSubtasks();
        TaskManager newManager = Managers.loadFromFile(Paths.get("tasks.txt"));
        assertEquals(manager, newManager, "Сохраненный и восстановленный менеджеры отличаются");
        Task task = new Task("Task", "Task description", 1, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(600L));
        Epic epic = new Epic("Epic", "Epic description", 2);
        Subtask subtask = new Subtask("Subtask", "Subtask description", 2, 3, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(601L));
        manager.addEpic(epic);
        newManager = Managers.loadFromFile(Paths.get("tasks.txt"));
        assertEquals(manager, newManager, "Сохраненный и восстановленный менеджеры отличаются");
        manager.addTask(task);
        manager.addSubtask(subtask);
        newManager = Managers.loadFromFile(Paths.get("tasks.txt"));
        assertEquals(manager, newManager, "Сохраненный и восстановленный менеджеры отличаются");
    }
}