package tests;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import manager.JsonTaskSerializer;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
    private String apiToken;
    private JsonTaskSerializer jsonTaskSerializer;
    HttpTaskServer taskServer;

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

    private String getValuesFromKVServer(String key) throws IOException, InterruptedException {
        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder();
        HttpRequest request = reqBuilder.uri(URI.create("http://localhost:8078/load/" + key + "?API_TOKEN=" + apiToken)).GET().build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @BeforeEach
    public void createClass() throws IOException, InterruptedException {
//        given
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
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8078/register")).build();
        apiToken = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    @Test
    public void taskHandlerTests() throws IOException, InterruptedException {
//        when
//        Добавление задач через HttpTaskServer
        String jsonStr = jsonTaskSerializer.taskToString(task1);
        sendRequestToTaskManager("task", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.taskToString(task2);
        sendRequestToTaskManager("task", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.taskToString(task3);
        sendRequestToTaskManager("task", jsonStr, "POST");
//        Обновление задачи через HttpTaskServer
        Task updatedTask = new Task("Обновленная задача 2", task2.getDescription(), task2.getId(), Status.DONE, task2.getStartTime(), task2.getDuration());
        jsonStr = jsonTaskSerializer.taskToString(updatedTask);
        sendRequestToTaskManager("task?id=" + task2.getId(), jsonStr, "POST");
//        then
//        Проверка наличия добавленных задач на KV сервере
        String responseBody = getValuesFromKVServer("tasks");
        responseBody = responseBody.substring(1, responseBody.length() - 1);
        String[] tasksArray = responseBody.split("---");
        List<Task> returnedTasks = new ArrayList<>();
        Arrays.stream(tasksArray).forEach(t -> returnedTasks.add(jsonTaskSerializer.taskFromString(JsonParser.parseString("\"" + t + "\"").getAsString())));
        Assertions.assertEquals(List.of(task1, updatedTask, task3), returnedTasks);

//        Запрос задач через HttpTaskServer
//        when
        responseBody = sendRequestToTaskManager("task", "", "GET");
        String[] tasksA = responseBody.split("---");
        List<Task> retTasks = new ArrayList<>();
        Arrays.stream(tasksA).forEach(t -> retTasks.add(jsonTaskSerializer.taskFromString(t)));
//        then
        Assertions.assertEquals(List.of(task1, updatedTask, task3), retTasks);

//        Запрос задачи по id через HttpTaskServer
//        when
        String tasksString = sendRequestToTaskManager("task?id=" + task3.getId(), "", "GET");
//        then
        Assertions.assertEquals(task3, jsonTaskSerializer.taskFromString(tasksString));

//        Удаление задачи через HttpTaskServer
//        when
        sendRequestToTaskManager("task?id=" + task1.getId(), "", "DELETE");
        responseBody = getValuesFromKVServer("tasks");
        responseBody = responseBody.substring(1, responseBody.length() - 1);
        String[] array = responseBody.split("---");
        Set<Integer> ids = new HashSet<>();
        Arrays.stream(array).forEach(t -> ids.add(jsonTaskSerializer.taskFromString(JsonParser.parseString("\"" + t + "\"").getAsString()).getId()));
//        then
        Assertions.assertFalse(ids.contains(task1.getId()));
        Assertions.assertNull(manager.getTask(task1.getId()));

//        Удаление задач через HttpTaskServer
//        when
        sendRequestToTaskManager("task", "", "DELETE");
//        then
        Assertions.assertEquals("\"\"", getValuesFromKVServer("tasks"));
        Assertions.assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void epicHandlerTests() throws IOException, InterruptedException {
//        when
//        Добавление эпиков через HttpTaskServer
        String jsonStr = jsonTaskSerializer.epicToString(epic1);
        sendRequestToTaskManager("epic", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.epicToString(epic2);
        sendRequestToTaskManager("epic", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.epicToString(epic3);
        sendRequestToTaskManager("epic", jsonStr, "POST");
//        Обновление эпиков через HttpTaskServer
        Epic updatedEpic = new Epic("Обновленный эпик 1", "Обновленное описание эпика 1", 9);
        jsonStr = jsonTaskSerializer.epicToString(updatedEpic);
        sendRequestToTaskManager("epic?id=" + updatedEpic.getId(), jsonStr, "POST");

//        then
//        Проверка наличия добавленных эпиков на KV сервере
        String response = getValuesFromKVServer("epics");
        response = response.substring(1, response.length() - 1);
        String[] epicsArray = response.split("---");
        List<Epic> returnedEpics = new ArrayList<>();
        Arrays.stream(epicsArray).forEach(t -> returnedEpics.add(jsonTaskSerializer.epicFromString(JsonParser.parseString("\"" + t + "\"").getAsString())));
        Assertions.assertEquals(List.of(updatedEpic, epic2, epic3), returnedEpics);

//        Запрос эпиков через HttpTaskServer
//        when
        response = sendRequestToTaskManager("epic", "", "GET");
        String[] epicsA = response.split("---");
        List<Epic> retEpics = new ArrayList<>();
        Arrays.stream(epicsA).forEach(t -> retEpics.add(jsonTaskSerializer.epicFromString(t)));
//        then
        Assertions.assertEquals(List.of(updatedEpic, epic2, epic3), retEpics);

//        Запрос эпиков по id через HttpTaskServer
//        when
        String epicString = sendRequestToTaskManager("epic?id=" + epic3.getId(), "", "GET");
//        then
        Assertions.assertEquals(epic3, jsonTaskSerializer.epicFromString(epicString));

//        Запрос подзадач эпика через HttpTaskServer
//        when
        jsonStr = jsonTaskSerializer.subtaskToString(subtask1);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.subtaskToString(subtask2);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.subtaskToString(subtask3);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
        response = sendRequestToTaskManager("subtask/epic&?id=10", "", "GET");
        String[] subtaskArray = response.split("---");
        List<Subtask> retSubtask = new ArrayList<>();
        Arrays.stream(subtaskArray).forEach(t -> retSubtask.add(jsonTaskSerializer.subtaskFromString(t)));
//        then
        Assertions.assertEquals(List.of(subtask1, subtask2, subtask3), retSubtask);

//        Удаление эпиков через HttpTaskServer
//        when
        sendRequestToTaskManager("epic?id=" + updatedEpic.getId(), "", "DELETE");
        response = getValuesFromKVServer("epics");
        response = response.substring(1, response.length() - 1);
        String[] array = response.split("---");
        Set<Integer> ids = new HashSet<>();
        Arrays.stream(array).forEach(t -> ids.add(jsonTaskSerializer.epicFromString(JsonParser.parseString("\"" + t + "\"").getAsString()).getId()));
//        then
        Assertions.assertFalse(ids.contains(updatedEpic.getId()));
        Assertions.assertNull(manager.getEpic(updatedEpic.getId()));

//        Удаление эпиков через HttpTaskServer
//        when
        sendRequestToTaskManager("epic", "", "DELETE");
//        then
        Assertions.assertEquals("\"\"", getValuesFromKVServer("epics"));
        Assertions.assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void subtaskHandlerTests() throws IOException, InterruptedException {
//        when
//        Добавление подзадач через HttpTaskServer
        String jsonStr = jsonTaskSerializer.epicToString(epic2);
        sendRequestToTaskManager("epic", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.subtaskToString(subtask1);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.subtaskToString(subtask2);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
        jsonStr = jsonTaskSerializer.subtaskToString(subtask3);
        sendRequestToTaskManager("subtask", jsonStr, "POST");
//        Обновление подзадач через HttpTaskServer
        Subtask updatedSubtask = new Subtask("Обновленная подзадача 2", "Обновленное описание подзадачи 2", 10, 13, Status.IN_PROGRESS, LocalDateTime.of(2022, 10, 21, 2, 0, 0), Duration.ofMinutes(10));
        jsonStr = jsonTaskSerializer.subtaskToString(updatedSubtask);
        sendRequestToTaskManager("subtask?id=" + updatedSubtask.getId(), jsonStr, "POST");

//        then
//        Проверка наличия добавленных подзадач на KV сервере
        String response = getValuesFromKVServer("subtasks");
        response = response.substring(1, response.length() - 1);
        String[] subtaskArray = response.split("---");
        List<Subtask> returnedSubtasks = new ArrayList<>();
        Arrays.stream(subtaskArray).forEach(t -> returnedSubtasks.add(jsonTaskSerializer.subtaskFromString(JsonParser.parseString("\"" + t + "\"").getAsString())));
        Assertions.assertEquals(List.of(subtask1, updatedSubtask, subtask3), returnedSubtasks);

//        Запрос подзадач через HttpTaskServer
//        when
        response = sendRequestToTaskManager("subtask", "", "GET");
        String[] subtasksA = response.split("---");
        List<Subtask> retEpics = new ArrayList<>();
        Arrays.stream(subtasksA).forEach(t -> retEpics.add(jsonTaskSerializer.subtaskFromString(t)));
//        then
        Assertions.assertEquals(List.of(subtask1, updatedSubtask, subtask3), retEpics);

//        Запрос подзадач по id через HttpTaskServer
//        when
        String subtaskString = sendRequestToTaskManager("subtask?id=" + subtask3.getId(), "", "GET");
//        then
        Assertions.assertEquals(subtask3, jsonTaskSerializer.subtaskFromString(subtaskString));

//        Удаление подзадач по id через HttpTaskServer
//        when
        sendRequestToTaskManager("subtask?id=" + subtask1.getId(), "", "DELETE");
        response = getValuesFromKVServer("subtasks");
        response = response.substring(1, response.length() - 1);
        String[] array = response.split("---");
        Set<Integer> ids = new HashSet<>();
        Arrays.stream(array).forEach(t -> ids.add(jsonTaskSerializer.subtaskFromString(JsonParser.parseString("\"" + t + "\"").getAsString()).getId()));
//        then
        Assertions.assertFalse(ids.contains(subtask1.getId()));
        Assertions.assertNull(manager.getSubtask(subtask1.getId()));

//        Удаление эпиков через HttpTaskServer
//        when
        sendRequestToTaskManager("subtask", "", "DELETE");
//        then
        Assertions.assertEquals("\"\"", getValuesFromKVServer("subtasks"));
        Assertions.assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void historyHandlerTests() throws IOException, InterruptedException {
//        when
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
//        Запрос истории на KV сервере
        String response = getValuesFromKVServer("history");
        Gson gson = new Gson();
        response = gson.fromJson(response, String.class);
        response = response.substring(1, response.length() - 1);
        List<Integer> history = jsonTaskSerializer.historyFromString(response);
//        then
        Assertions.assertEquals(List.of(13, 10, 7, 12, 5), history);

//        Запрос истории через HttpTaskServer
//        when
        response = sendRequestToTaskManager("history", "", "GET");
        response = response.substring(1, response.length() - 1);
        history = jsonTaskSerializer.historyFromString(response);
//        then
        Assertions.assertEquals(List.of(13, 10, 7, 12, 5), history);
    }

    @Test
    public void prioritizedTaskHandlerTests() throws IOException, InterruptedException {
//        when
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
//        Запрос задач через HttpTaskServer
        String response = sendRequestToTaskManager("", "", "GET");
        String[] prioritizedTasksA = response.split("---");
        List<AbstractTask> prioritizedTasks = new ArrayList<>();
        Arrays.stream(prioritizedTasksA).forEach(t -> prioritizedTasks.add(jsonTaskSerializer.prioritizedTaskFromString(t)));
//        then
        Assertions.assertEquals(List.of(task2, task1, subtask1, subtask2), prioritizedTasks);
    }

    @AfterEach
    public void closeServer() {
        server.stop();
        taskServer.stop();
    }
}