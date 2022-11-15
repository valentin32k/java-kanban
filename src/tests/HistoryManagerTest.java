package tests;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {

    private HistoryManager manager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Subtask subtask1;

    @BeforeEach
    public void initializeVariable() {
//        given
        manager = new InMemoryHistoryManager();
        task1 = new Task("Задача 1", "Описание задачи 1", 5, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        task2 = new Task("Задача 2", "Описание задачи 2", 7, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(61));
        epic1 = new Epic("Эпик 1", "Описание эпика 1", 1);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1, 3, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
    }

    @Test
    void addTasksInHistoryTested() {
//        when
        manager.add(null);
//        then
        assertEquals(0, manager.getHistory().size(), "Удалось сделать запись null");
//        when
        manager.add(task1);
        manager.add(task2);
        manager.add(task1);
        manager.add(epic1);
        manager.add(subtask1);
        manager.add(task2);
//        then
        assertEquals(List.of(task1, epic1, subtask1, task2), manager.getHistory(), "Добавление задач в историю работает не корректно");
    }

    @Test
    void removeTasksFromHistoryTested() {
//        when
        manager.remove(0);
        manager.add(task1);
        manager.add(task2);
        manager.add(task1);
        manager.add(epic1);
        manager.add(subtask1);
        manager.add(task2);
        manager.remove(5);
        manager.remove(3);
        manager.remove(7);
//        then
        assertEquals(List.of(epic1), manager.getHistory(), "Метод remove работает не корректно");
    }

    @Test
    void getHistoryTested() {
//        when
        manager.getHistory();
        manager.add(task1);
        manager.add(task2);
        manager.add(task1);
        manager.add(epic1);
        manager.add(subtask1);
        manager.add(task2);
//        then
        assertEquals(List.of(task1, epic1, subtask1, task2), manager.getHistory(), "Добавление задач в историю работает не корректно");
    }
}