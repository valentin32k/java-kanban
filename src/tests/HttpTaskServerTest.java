package tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.JsonTaskSerializer;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class HttpTaskServerTest {
    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Epic epic2;
    private Epic epic3;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;
    private TaskManager manager;
    private KVServer server;
    private JsonTaskSerializer jsonTaskSerializer;
    HttpTaskServer taskServer;
    Gson gson;

    private String sendRequestToTaskManager(String kindOfTask, String jsonBody, String method) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonBody);
        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/tasks/" + kindOfTask));
        switch (method) {
            case "POST":
                reqBuilder.POST(body);
                break;
            case "GET":
                reqBuilder.GET();
                break;
            case "DELETE":
                reqBuilder.DELETE();
                break;
            default:
                return null;
        }
        HttpRequest request = reqBuilder.build();
        HttpClient client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    @BeforeEach
    public void createClass() throws IOException {
        task1 = new Task("Задача 1", "Описание задачи 1", 5, Status.NEW, LocalDateTime.of(2022, 10, 19, 11, 11, 11), Duration.ofMinutes(601L));
        task2 = new Task("Задача 2", "Описание задачи 2", 7, Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 18, 1, 1, 1), Duration.ofMinutes(30L));
        task3 = new Task("Задача 3", "Описание задачи 3", 8, Status.DONE, LocalDateTime.of(2022, 10, 19, 2, 0, 0), Duration.ofMinutes(10));
        epic1 = new Epic("Эпик 1", "Описание эпика 1", 9);
        epic2 = new Epic("Эпик 2", "Описание эпика 2", 10);
        epic3 = new Epic("Эпик 3", "Описание эпика 3", 11);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 10, 12, Status.NEW, LocalDateTime.of(2022, 10, 20, 2, 0, 0), Duration.ofMinutes(10));
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 10, 13, Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 21, 2, 0, 0), Duration.ofMinutes(10));
        subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", 10, 14, Status.DONE, LocalDateTime.of(2022, 10, 22, 2, 0, 0), Duration.ofMinutes(10));
        server = new KVServer();
        jsonTaskSerializer = new JsonTaskSerializer();
        server.start();
        taskServer = new HttpTaskServer();
        manager = taskServer.getManager();
        gson = new Gson();
    }

    @Test
    public void addTaskTests() throws IOException, InterruptedException {
//        when
        String jsonStr = jsonTaskSerializer.taskToString(task1);
        sendRequestToTaskManager("task", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.taskToString(task2);
        sendRequestToTaskManager("task", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.taskToString(task3);
        sendRequestToTaskManager("task", jsonStr, "POST");
//        then
        Assertions.assertArrayEquals(new Task[]{task1, task2, task3}, manager.getTasks().values().toArray());
    }

    @Test
    public void updateTaskTests() throws IOException, InterruptedException {
//        given
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
//        when
        Task updatedTask = new Task("Обновленная задача 2", task2.getDescription(), task2.getId(), Status.DONE, task2.getStartTime(), task2.getDuration());
        String jsonStr = jsonTaskSerializer.taskToString(updatedTask);
        sendRequestToTaskManager("task?id=" + task2.getId(), jsonStr, "POST");
//        then
        Assertions.assertArrayEquals(new Task[]{task1, updatedTask, task3}, manager.getTasks().values().toArray());
    }

    @Test
    public void getTasksTests() throws IOException, InterruptedException {
//        given
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
//        when
        String responseBody = sendRequestToTaskManager("task", "", "GET");
//        then
        List<Task> tasks = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertEquals(List.of(task1, task2, task3), tasks);
    }

    @Test
    public void getTaskByIdTests() throws IOException, InterruptedException {
//        given
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
//        when
        String tasksString = sendRequestToTaskManager("task?id=" + task3.getId(), "", "GET");
//        then
        Assertions.assertEquals(task3, jsonTaskSerializer.taskFromString(tasksString));
    }

    @Test
    public void removeTasksTests() throws IOException, InterruptedException {
//        given
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
//        when
        sendRequestToTaskManager("task", "", "DELETE");
//        then
        Assertions.assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void removeTaskByIdTests() throws IOException, InterruptedException {
//        given
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
//        when
        sendRequestToTaskManager("task?id=" + task1.getId(), "", "DELETE");
//        then
        Assertions.assertNull(manager.getTask(task1.getId()));
    }

    @Test
    public void addEpicTests() throws IOException, InterruptedException {
//        when
        String jsonStr = jsonTaskSerializer.epicToString(epic1);
        sendRequestToTaskManager("epic", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.epicToString(epic2);
        sendRequestToTaskManager("epic", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.epicToString(epic3);
        sendRequestToTaskManager("epic", jsonStr, "POST");
//        then
        Assertions.assertArrayEquals(new Epic[]{epic1, epic2, epic3}, manager.getEpics().values().toArray());
    }

    @Test
    public void updateEpicTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
//        when
        Epic updatedEpic = new Epic("Обновленный эпик 1", "Обновленное описание эпика 1", epic1.getId());
        String jsonStr = jsonTaskSerializer.epicToString(updatedEpic);
        sendRequestToTaskManager("epic?id=" + updatedEpic.getId(), jsonStr, "POST");
//        then
        Assertions.assertArrayEquals(new Epic[]{updatedEpic, epic2, epic3}, manager.getEpics().values().toArray());
    }

    @Test
    public void getEpicsTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
//        when
        String responseBody = sendRequestToTaskManager("epic", "", "GET");
//        then
        List<Epic> epics = gson.fromJson(responseBody, new TypeToken<List<Epic>>() {
        }.getType());
        Assertions.assertEquals(List.of(epic1, epic2, epic3), epics);
    }

    @Test
    public void getEpicByIdTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
//        when
        String epicString = sendRequestToTaskManager("epic?id=" + epic3.getId(), "", "GET");
//        then
        Assertions.assertEquals(epic3, jsonTaskSerializer.epicFromString(epicString));
    }

    @Test
    public void removeEpicsTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
//        when
        sendRequestToTaskManager("epic", "", "DELETE");
//        then
        Assertions.assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void removeEpicByIdTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
//        when
        sendRequestToTaskManager("epic?id=" + epic1.getId(), "", "DELETE");
//        then
        Assertions.assertNull(manager.getTask(epic1.getId()));
    }

    @Test
    public void getEpicSubtasksTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
//        when
        String response = sendRequestToTaskManager("subtask/epic&?id=" + epic2.getId(), "", "GET");
//        then
        List<Subtask> subtasks = gson.fromJson(response, new TypeToken<List<Subtask>>() {
        }.getType());
        Assertions.assertEquals(List.of(subtask1, subtask2, subtask3), subtasks);
    }

    @Test
    public void addSubtaskTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic2);
//        when
        String jsonStr = jsonTaskSerializer.subtaskToString(subtask1);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.subtaskToString(subtask2);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.subtaskToString(subtask3);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
//        then
        Assertions.assertArrayEquals(new Subtask[]{subtask1, subtask2, subtask3}, manager.getSubtasks().values().toArray());
    }

    @Test
    public void updateSubtaskTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
//        when
        Subtask updatedSubtask = new Subtask("Обновленная подзадача 2", "Обновленное описание подзадачи 2", 10, subtask2.getId(), Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 21, 2, 0, 0), Duration.ofMinutes(10));
        String jsonStr = jsonTaskSerializer.subtaskToString(updatedSubtask);
        sendRequestToTaskManager("subtask?id=" + updatedSubtask.getId(), jsonStr, "POST");
//        then
        Assertions.assertArrayEquals(new Subtask[]{subtask1, updatedSubtask, subtask3}, manager.getSubtasks().values().toArray());
    }

    @Test
    public void getSubtasksTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
//        when
        String responseBody = sendRequestToTaskManager("subtask", "", "GET");
//        then
        List<Subtask> subtasks = gson.fromJson(responseBody, new TypeToken<List<Subtask>>() {
        }.getType());
        Assertions.assertEquals(List.of(subtask1, subtask2, subtask3), subtasks);
    }

    @Test
    public void getSubtaskByIdTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
//        when
        String subtaskString = sendRequestToTaskManager("subtask?id=" + subtask2.getId(), "", "GET");
//        then
        Assertions.assertEquals(subtask2, jsonTaskSerializer.subtaskFromString(subtaskString));
    }

    @Test
    public void removeSubtasksTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
//        when
        sendRequestToTaskManager("subtask", "", "DELETE");
//        then
        Assertions.assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void removeSubtaskByIdTests() throws IOException, InterruptedException {
//        given
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
//        when
        sendRequestToTaskManager("subtask?id=" + subtask2.getId(), "", "DELETE");
//        then
        Assertions.assertNull(manager.getSubtask(subtask2.getId()));
    }

    @Test
    public void getHistoryTests() throws IOException, InterruptedException {
//        given
        String stringTask1 = jsonTaskSerializer.taskToString(task1);
        String stringTask2 = jsonTaskSerializer.taskToString(task2);
        String stringEpic2 = jsonTaskSerializer.epicToString(epic2);
        String stringSubtask1 = jsonTaskSerializer.subtaskToString(subtask1);
        String stringSubtask2 = jsonTaskSerializer.subtaskToString(subtask2);
        sendRequestToTaskManager("task", stringTask1, "POST");
        sendRequestToTaskManager("task", stringTask2, "POST");
        sendRequestToTaskManager("epic", stringEpic2, "POST");
        sendRequestToTaskManager("subtask", stringSubtask1, "POST");
        sendRequestToTaskManager("subtask", stringSubtask2, "POST");
        sendRequestToTaskManager("task?id=" + task1.getId(), "", "GET");
        sendRequestToTaskManager("subtask?id=" + subtask2.getId(), "", "GET");
        sendRequestToTaskManager("epic?id=" + epic2.getId(), "", "GET");
        sendRequestToTaskManager("task?id=" + task2.getId(), "", "GET");
        sendRequestToTaskManager("subtask?id=" + subtask1.getId(), "", "GET");
        sendRequestToTaskManager("task?id=" + task1.getId(), "", "GET");
//        when
        String response = sendRequestToTaskManager("history", "", "GET");
        List<Integer> history = jsonTaskSerializer.historyFromString(response);
//        then
        Assertions.assertEquals(List.of(13, 10, 7, 12, 5), history);
    }

    @Test
    public void prioritizedTaskHandlerTests() throws IOException, InterruptedException {
//        given
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
//        when
        String response = sendRequestToTaskManager("", "", "GET");
//        then
        List<Integer> prioritizedTasks = gson.fromJson(response, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        Assertions.assertEquals(List.of(7, 5, 12, 13), prioritizedTasks);
    }

    @AfterEach
    public void closeServer() {
        server.stop();
        taskServer.stop();
    }
}