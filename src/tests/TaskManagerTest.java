package tests;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager manager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Task taskWithWrongId;
    protected Epic epic1;
    protected Epic epic2;
    protected Epic epicWithWrongId;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Subtask subtask3;
    protected Subtask subtaskWithWrongId;

    @BeforeEach
    public void initializeVariables() {
        task1 = new Task("Задача 1", "Описание задачи 1", 5, Status.NEW, LocalDateTime.of(2022, 10, 19, 11, 11, 11), Duration.ofMinutes(601L));
        task2 = new Task("Задача 2", "Описание задачи 2", 7, Status.DONE, LocalDateTime.of(2022, 10, 18, 1, 1, 1), Duration.ofMinutes(30L));
        task3 = new Task("Задача 3", "Описание задачи 3", 8, Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 19, 2, 0, 0), Duration.ofMinutes(10));
        taskWithWrongId = new Task("Задача N", "Описание задачи N", -100, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(5));

        epic1 = new Epic("Эпик 1", "Описание эпика 1", 1);
        epic2 = new Epic("Эпик 2", "Описание эпика 2", 2);
        epicWithWrongId = new Epic("Эпик 3", "Описание эпика 3", -100);

        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1, 3, Status.NEW, LocalDateTime.of(2022, 10, 19, 11, 11, 11), Duration.ofMinutes(601));
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 1, 4, Status.DONE, LocalDateTime.of(2022, 10, 18, 1, 1, 1), Duration.ofMinutes(30));
        subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", 1, 6, Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 19, 2, 0, 0), Duration.ofMinutes(10));
        subtaskWithWrongId = new Subtask("Подзадача 3", "Описание подзадачи 3", 1, -3, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(6));

    }

    @Test
    void getTasksTests() {
        assertDoesNotThrow(() -> manager.getTasks(), "Исключение при вызове метода getTasks() в случае отсутствия задач");
        assertEquals(new HashMap<Integer, Task>(), manager.getTasks(), "В случае отсутствия задач возвращает значение, отличное от HashMap<Integer,Task>()");
        manager.addTask(task1);
        assertEquals(task1, manager.getTasks().values().toArray()[0], "Добавленная и возвращенная задачи не совпадают");
        manager.addTask(task2);
        assertEquals(2, manager.getTasks().size(), "Отличается количество добавленных и возвращенных задач");
    }

    @Test
    void clearTasksTests() {
        assertDoesNotThrow(() -> manager.clearTasks(), "Исключение при вызове метода clearTasks() в случае отсутствия задач");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.clearTasks();
        assertEquals(0, manager.getTasks().size(), "Не получилось удалить все задачи");
    }

    @Test
    void getTaskTests() {
        assertDoesNotThrow(() -> manager.getTask(0), "Исключение при вызове метода getTask(0)");
        assertNull(manager.getTask(-1), "В случае неверного id возвращается значение, отличное от null");
        manager.addTask(task1);
        assertEquals(task1, manager.getTask(5), "Добавленная и возвращенная задачи не совпадают");
    }

    @Test
    void addTaskTests() {
        manager.addTask(task1);
        final Task savedTask = manager.getTask(5);
        assertNotNull(savedTask, "Добавленная задача не найдена в хранилище менеджера");
        assertEquals(task1, savedTask, "Добавленная и возвращенная задачи не совпадают");
        assertDoesNotThrow(() -> manager.addTask(null), "Исключение при вызове метода addTask(null)");
        manager.addTask(taskWithWrongId);
        assertEquals(1, manager.getTasks().size(), "Добавляется задача с неправильным id");
        Task task4 = new Task("Задача 4", "Описание задачи 4", 9, Status.DONE, LocalDateTime.of(2022, 10, 19, 12, 12, 12), Duration.ofMinutes(10));
        Task task5 = new Task("Задача 5", "Описание задачи 5", 158, Status.NEW, null, null);
        Task task6 = new Task("Задача 6", "Описание задачи 6", 155, Status.NEW, null, null);
        manager.addTask(task2);
        manager.addTask(task5);
        manager.addTask(task3);
        manager.addTask(task4);
        manager.addTask(task6);
        assertEquals(List.of(task2, task3, task1, task6, task5), manager.getPrioritizedTasks(), "Список добавленных задач отличается от ожидаемого");
    }

    @Test
    void updateTaskTests() {
        assertDoesNotThrow(() -> manager.updateTask(null), "Исключение при вызове метода updateTask(null)");
        assertDoesNotThrow(() -> manager.updateTask(taskWithWrongId), "Исключение при попытке обновить не существующую задачу");
        manager.addTask(task1);
        Task newTask = new Task("Task 1", "Task description 1", 5, Status.DONE, LocalDateTime.of(2022, 10, 19, 11, 11, 11), Duration.ofMinutes(601));
        manager.updateTask(newTask);
        assertEquals(newTask, manager.getTask(5), "Задача после обновления отличается от ожидаемой");
        assertEquals(1, manager.getTasks().size(), "Количество задач после обновления задачи отличается от ожидаемого");
        Task task5 = new Task("Задача 5", "Описание задачи 5", 7, Status.DONE, LocalDateTime.of(2022, 10, 18, 1, 11, 30), Duration.ofMinutes(15));
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task5);
        assertEquals(List.of(task2, task3, newTask), manager.getPrioritizedTasks(), "Список добавленных задач отличается от ожидаемого");
    }

    @Test
    void removeTaskTests() {
        assertDoesNotThrow(() -> manager.removeTask(0), "Исключение при вызове метода removeTask(0)");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.removeTask(5);
        assertNull(manager.getTask(5), "Метод removeTask(int id) не работает");
        assertEquals(1, manager.getTasks().size(), "Количество задач после удаление отличается от ожидаемого");
    }

    @Test
    void getEpicsTests() {
        assertDoesNotThrow(() -> manager.getEpics(), "Исключение при вызове метода getEpics() в случае отсутствия эпиков");
        assertEquals(new HashMap<Integer, Task>(), manager.getEpics(), "В случае отсутствия эпиков возвращает значение, отличное от HashMap<Integer,Epic>()");
        manager.addEpic(epic1);
        assertEquals(epic1, manager.getEpics().values().toArray()[0], "Добавленный и возвращенный эпики не совпадают");
        manager.addEpic(epic2);
        assertEquals(2, manager.getEpics().size(), "Отличается количество добавленных и возвращенных эпиков'");
    }

    @Test
    void clearEpicsTests() {
        assertDoesNotThrow(() -> manager.clearEpics(), "Исключение при вызове метода clearEpics() в случае отсутствия эпиков");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.clearEpics();
        assertEquals(0, manager.getEpics().size(), "Не получилось удалить все эпики");
    }

    @Test
    void getEpicTests() {
        assertDoesNotThrow(() -> manager.getEpic(0), "Исключение при вызове метода getEpic(0)");
        assertNull(manager.getEpic(-1), "В случае неверного id возвращается значение, отличное от null");
        manager.addEpic(epic1);
        assertEquals(epic1, manager.getEpic(1), "Добавленный и возвращенный эпики не совпадают");
    }

    @Test
    void addEpicTests() {
        manager.addEpic(epic1);
        final Epic savedEpic = manager.getEpic(1);
        assertNotNull(savedEpic, "Добавленный эпик не найден в хранилище менеджера");
        assertEquals(epic1, savedEpic, "Добавленный и возвращенный эпики не совпадают");
        assertDoesNotThrow(() -> manager.addEpic(null), "Исключение при вызове метода addEpic(null)");
        manager.addEpic(epicWithWrongId);
        assertEquals(1, manager.getEpics().size(), "Добавляется эпик с неправильным id");
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        assertEquals(subtask2.getStartTime(), manager.getEpic(1).getStartTime(), "Не корректно рассчитывается время начала эпика");
        assertEquals(subtask1.getEndTime(), manager.getEpic(1).getEndTime(), "Не корректно рассчитывается время окончания эпика");
    }

    @Test
    void updateEpicTests() {
        assertDoesNotThrow(() -> manager.updateEpic(null), "Исключение при вызове метода updadeEpic(null)");
        assertDoesNotThrow(() -> manager.updateEpic(epicWithWrongId), "Исключение при попытке обновить не существующий эпик");
        manager.addEpic(epic1);
        final Epic newEpic = new Epic("Epic 2", "Epic description 2", 1);
        manager.updateEpic(newEpic);
        assertEquals(newEpic, manager.getEpic(1), "Эпик после обновления отличается от ожидаемого");
        assertEquals(1, manager.getEpics().size(), "Количество эпиков после обновления эпика отличается от ожидаемого");
    }

    @Test
    void removeEpicTests() {
        assertDoesNotThrow(() -> manager.removeEpic(0), "Исключение при вызове метода removeEpic(0)");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.removeEpic(1);
        assertNull(manager.getEpic(1), "Метод removeEpic(int id) не работает");
        assertNull(manager.getSubtask(3), "removeEpic(int id) не удаляет подзадачи");
        assertNull(manager.getSubtask(4), "removeEpic(int id) не удаляет подзадачи");
        assertEquals(1, manager.getEpics().size(), "Количество эпиков после удаление отличается от ожидаемого");
    }

    @Test
    void getEpicSubtasksTests() {
        assertDoesNotThrow(() -> manager.getEpicSubtasks(0), "Исключение при вызове метода getEpicSubtasks(0)");
        manager.addEpic(epic1);
        assertEquals(new HashMap<Integer, Subtask>(), manager.getEpicSubtasks(1), "В случае отсутствия подзадач возвращает значение, отличное от HashMap<Integer,Subtask>");
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(List.of(subtask1, subtask2), new ArrayList<>(manager.getEpicSubtasks(1).values()), "Метод getEpicSubtasks не работает");
    }

    @Test
    void getSubtasksTests() {
        assertDoesNotThrow(() -> manager.getSubtasks(), "Исключение при вызове метода getSubtasks()");
        assertEquals(new HashMap<Integer, Subtask>(), manager.getSubtasks(), "В случае отсутствия подзадач возвращает значение, отличное от HashMap<Integer,Subtask>");
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(List.of(subtask1, subtask2), new ArrayList<>(manager.getSubtasks().values()), "Метод getSubtasks не работает");
    }

    @Test
    void clearSubtasksTests() {
        assertDoesNotThrow(() -> manager.clearSubtasks(), "Исключение при вызове метода clearSubtasks() в случае отсутствия подзадач");
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.clearSubtasks();
        assertEquals(0, manager.getSubtasks().size(), "Не получилось удалить все подзадачи");
        assertEquals(0, manager.getEpicSubtasks(1).size(), "Не удаляются id подзадач из эпика");
    }

    @Test
    void getSubtaskTests() {
        assertDoesNotThrow(() -> manager.getSubtask(0), "Исключение при вызове метода getSubtask(0)");
        assertNull(manager.getSubtask(-1), "В случае неверного id возвращается значение, отличное от null");
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(subtask1, manager.getSubtask(3), "Метод getSubtask не работает");
        assertEquals(subtask2, manager.getSubtask(4), "Метод getSubtask не работает");
    }

    @Test
    void addSubtaskTests() {
        assertDoesNotThrow(() -> manager.addSubtask(null), "Исключение при вызове метода addSubtask(null)");
        manager.addSubtask(subtask1);
        assertEquals(0, manager.getSubtasks().size(), "Удалось записать подзадачу в несуществующий эпик");
        manager.addEpic(epic1);
        manager.addSubtask(null);
        assertEquals(0, manager.getSubtasks().size(), "Удалось записать пустую подздачу");
        manager.addSubtask(subtaskWithWrongId);
        assertEquals(0, manager.getSubtasks().size(), "Удалось записать подзадачу с некорректным id");
        Subtask subtask4 = new Subtask("Подзадача 4", "Описание подзадачи 4", 1, 15, Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 19, 12, 12, 12), Duration.ofMinutes(10));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask4);
        assertEquals(List.of(subtask2, subtask3, subtask1), manager.getPrioritizedTasks(), "Список подзадач отличается от ожидаемого");
    }

    @Test
    void updateSubtaskTests() {
        assertDoesNotThrow(() -> manager.updateSubtask(null), "Исключение при вызове метода updateSubtask(null)");
        assertDoesNotThrow(() -> manager.updateSubtask(new Subtask("1", "2", 100, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(601L))), "Исключение при попытке обновить не существующую задачу");
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        final Subtask newSubtask = new Subtask("Subtask 2", "Subtask description 2", 1, 3, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(601L));
        manager.updateSubtask(newSubtask);
        assertEquals(newSubtask, manager.getSubtask(3), "Подзадача после обновления отличается от ожидаемой");
        assertEquals(1, manager.getSubtasks().size(), "Количество подзадач после обновления отличается от ожидаемого");
        manager.clearSubtasks();
        Subtask subtask5 = new Subtask("Подзадача 5", "Описание подзадачи 5", 1, 4, Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 18, 1, 11, 30), Duration.ofMinutes(15));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        assertEquals(List.of(subtask2, subtask3, subtask1), manager.getPrioritizedTasks(), "Список подзадач отличается от ожидаемого");
    }

    @Test
    void removeSubtaskTests() {
        assertDoesNotThrow(() -> manager.removeSubtask(0), "Исключение при вызове метода removeSubtask(0)");
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.removeSubtask(3);
        assertNull(manager.getSubtask(3), "Метод removeSubtask(int id) не работает");
    }

    @Test
    void getHistoryTests() {
        assertEquals(0, manager.getHistory().size(), "Метод не корректно работает для случая пустой истории");
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.getTask(5);
        manager.getSubtask(3);
        manager.getEpic(1);
        assertEquals(List.of(task1, manager.getEpic(1)), manager.getHistory(), "Сохраненная история отлична от ожидаемой");
    }

    @Test
    void getPrioritizedTasksTests() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask2);
        assertEquals(List.of(task2, subtask3, task1), manager.getPrioritizedTasks(), "Метод getPrioritizedTasks работает не корректно");
    }
}