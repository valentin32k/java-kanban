package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.JsonTaskSerializer;
import manager.Managers;
import manager.TaskManager;
import manager.TaskSerializer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8081;
    private final TaskManager taskManager;
    private final Gson gson;
    HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault(URI.create("http://localhost:8078"));
        gson = new Gson();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/subtask/epic", new EpicSubtaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.createContext("/tasks", new PrioritizedTasksHandler());
        httpServer.start();
        System.out.println("Сервер запущен! Порт: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
    }

    public TaskManager getManager() {
        return taskManager;
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                String method = httpExchange.getRequestMethod();
                Integer id;
                String response = null;
                URI uri = httpExchange.getRequestURI();
                String query = uri.getQuery();
                if (query != null) {
                    String[] queryArray = query.split("=");
                    if (queryArray.length == 2 && queryArray[0].equals("id")) {
                        id = Integer.valueOf(queryArray[1]);
                        switch (method) {
                            case "GET":
                                if (taskManager.getTask(id) == null) {
                                    response = "Задачи c id = " + id + " в хранилище не найдены";
                                } else {
                                    response = gson.toJson(taskManager.getTask(id));
                                }
                                break;
                            case "POST":
                                InputStream inputStream = httpExchange.getRequestBody();
                                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                Task task = gson.fromJson(body, Task.class);
                                Task updatedTask = new Task(task.getName(), task.getDescription(), id, task.getStatus(), task.getStartTime(), task.getDuration());
                                task = taskManager.updateTask(updatedTask);
                                if (task != null) {
                                    response = gson.toJson(task);
                                } else {
                                    response = "Не удалось обновить задачу";
                                }
                                break;
                            case "DELETE":
                                taskManager.removeTask(id);
                                response = "Задача удалена";
                                break;
                        }
                    }
                } else {
                    switch (method) {
                        case "GET":
                            if (taskManager.getTasks() == null) {
                                response = "Задачи в хранилище не найдены";
                            } else {
                                TaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
                                List<String> list = new ArrayList<>();
                                taskManager.getTasks().values().forEach(t -> list.add(jsonTaskSerializer.taskToString(t)));
                                response = String.join("---", list);
                            }
                            break;
                        case "POST":
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Gson gson = new Gson();
                            Task task = gson.fromJson(body, Task.class);
                            task = taskManager.addTask(task);
                            if (task != null) {
                                response = gson.toJson(task);
                            } else {
                                response = "Не удалось добавить задачу";
                            }
                            break;
                        case "DELETE":
                            taskManager.clearTasks();
                            response = "Задачи удалены";
                            break;
                    }
                }
                if (response != null) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Bad request".getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    private class SubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                String method = httpExchange.getRequestMethod();
                Integer id;
                String response = null;
                URI uri = httpExchange.getRequestURI();
                String query = uri.getQuery();
                if (query != null) {
                    String[] queryArray = query.split("=");
                    if (queryArray.length == 2 && queryArray[0].equals("id")) {
                        id = Integer.valueOf(queryArray[1]);
                        switch (method) {
                            case "GET":
                                if (taskManager.getSubtask(id) == null) {
                                    response = "Подзадача c id = " + id + " в хранилище не найдена";
                                } else {
                                    response = gson.toJson(taskManager.getSubtask(id));
                                }
                                break;
                            case "POST":
                                InputStream inputStream = httpExchange.getRequestBody();
                                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                Gson gson = new Gson();
                                Subtask subtask = gson.fromJson(body, Subtask.class);
                                Subtask updatedSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getEpicId(), id, subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());
                                subtask = taskManager.updateSubtask(updatedSubtask);
                                if (subtask != null) {
                                    response = gson.toJson(subtask);
                                } else {
                                    response = "Не удалось обновить задачу";
                                }
                                break;
                            case "DELETE":
                                taskManager.removeSubtask(id);
                                response = "Подзадача удалена";
                                break;
                        }
                    }
                } else {
                    switch (method) {
                        case "GET":
                            if (taskManager.getSubtasks() == null) {
                                response = "Задачи в хранилище не найдены";
                            } else {
                                TaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
                                List<String> list = new ArrayList<>();
                                taskManager.getSubtasks().values().forEach(t -> list.add(jsonTaskSerializer.subtaskToString(t)));
                                response = String.join("---", list);
                            }
                            break;
                        case "POST":
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Gson gson = new Gson();
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            subtask = taskManager.addSubtask(subtask);
                            if (subtask != null) {
                                response = gson.toJson(subtask);
                            } else {
                                response = "Не удалось добавить подзадачу";
                            }
                            break;
                        case "DELETE":
                            taskManager.clearSubtasks();
                            response = "Подзадачи удалены";
                            break;
                    }
                }
                if (response != null) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Bad request".getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    private class EpicSubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                String method = httpExchange.getRequestMethod();
                Integer id;
                String response = null;
                URI uri = httpExchange.getRequestURI();
                String query = uri.getQuery();
                if (query != null) {
                    String[] queryArray = query.split("=");
                    if (queryArray.length == 2 && queryArray[0].equals("id")) {
                        id = Integer.valueOf(queryArray[1]);
                        if (method.equals("GET")) {
                            if (taskManager.getEpicSubtasks(id) != null) {
                                TaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
                                List<String> list = new ArrayList<>();
                                taskManager.getEpicSubtasks(id).values().forEach(t -> list.add(jsonTaskSerializer.subtaskToString(t)));
                                response = String.join("---", list);
                            }
                        }
                    }
                }
                if (response != null) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Bad request".getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    private class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                String method = httpExchange.getRequestMethod();
                Integer id;
                String response = null;
                URI uri = httpExchange.getRequestURI();
                String query = uri.getQuery();
                if (query != null) {
                    String[] queryArray = query.split("=");
                    if (queryArray.length == 2 && queryArray[0].equals("id")) {
                        id = Integer.valueOf(queryArray[1]);
                        switch (method) {
                            case "GET":
                                if (taskManager.getEpic(id) == null) {
                                    response = "Эпик c id = " + id + " в хранилище не найден";
                                } else {
                                    response = gson.toJson(taskManager.getEpic(id));
                                }
                                break;
                            case "POST":
                                InputStream inputStream = httpExchange.getRequestBody();
                                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                Gson gson = new Gson();
                                Epic epic = gson.fromJson(body, Epic.class);
                                Epic updatedEpic = new Epic(epic.getName(), epic.getDescription(), id);
                                epic = taskManager.updateEpic(updatedEpic);
                                if (epic != null) {
                                    response = gson.toJson(epic);
                                } else {
                                    response = "Не удалось обновить эпик";
                                }
                                break;
                            case "DELETE":
                                taskManager.removeEpic(id);
                                response = "Эпик удален";
                                break;
                        }
                    }
                } else {
                    switch (method) {
                        case "GET":
                            if (taskManager.getEpics() == null) {
                                response = "Эпики в хранилище не найдены";
                            } else {
                                TaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
                                List<String> list = new ArrayList<>();
                                taskManager.getEpics().values().forEach(t -> list.add(jsonTaskSerializer.epicToString(t)));
                                response = String.join("---", list);
                            }
                            break;
                        case "POST":
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Gson gson = new Gson();
                            Epic epic = gson.fromJson(body, Epic.class);
                            epic = taskManager.addEpic(epic);
                            if (epic != null) {
                                response = gson.toJson(epic);
                            } else {
                                response = "Не удалось добавить эпик";
                            }
                            break;
                        case "DELETE":
                            taskManager.clearEpics();
                            response = "Эпики удалены";
                            break;
                    }
                }
                if (response != null) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Bad request".getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    private class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                if (httpExchange.getRequestMethod().equals("GET")) {
                    TaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
                    String response = jsonTaskSerializer.historyToString(taskManager.getHistory());
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Bad request".getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    private class PrioritizedTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                if (httpExchange.getRequestMethod().equals("GET")) {
                    List<String> list = new ArrayList<>();
                    JsonTaskSerializer jsonTaskSerializer = new JsonTaskSerializer();
                    taskManager.getPrioritizedTasks().forEach(t -> list.add(jsonTaskSerializer.prioritizedTaskToString(t)));
                    String response = String.join("---", list);
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Bad request".getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }
}
