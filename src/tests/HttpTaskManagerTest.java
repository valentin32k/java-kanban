package tests;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class HttpTaskManagerTest extends FileBackedTaskManagerTest {
    private KVServer server;

    @Override
    @BeforeEach
    public void createClass() throws IOException {
        server = new KVServer();
        server.start();
        manager = Managers.getDefault(URI.create("http://localhost:8078"));
    }

    @Test
    public void loadTasksFromKvServerTests() {
//        when
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask3);
        manager.getTask(task2.getId());
        manager.getTask(task1.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());
        manager.getEpic(epic2.getId());
        manager.getSubtask(subtask2.getId());
        manager.getSubtask(subtask1.getId());
        TaskManager manager2 = Managers.getDefault(URI.create("http://localhost:8078"));
//        then
        Assertions.assertEquals(List.of(task1, task2, task3), new ArrayList<>(manager2.getTasks().values()));
        Assertions.assertEquals(2, manager2.getEpics().size());
        Assertions.assertEquals(List.of(subtask1), new ArrayList<>(manager2.getSubtasks().values()));
        Assertions.assertEquals(List.of(task2, task3, task1, epic2, subtask1), new ArrayList<>(manager2.getHistory()));
        Assertions.assertEquals(List.of(task2, task3, task1, subtask1), new ArrayList<>(manager2.getPrioritizedTasks()));
    }

    @AfterEach
    public void closeServer() {
        server.stop();
    }
}