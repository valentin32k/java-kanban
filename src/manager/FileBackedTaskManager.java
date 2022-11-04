package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path tasksFile;

    public FileBackedTaskManager(Path tasksFile) {
        this.tasksFile = tasksFile;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Task addTask(Task task) {
        Task tmpTask = super.addTask(task);
        save();
        return tmpTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task taskToReturn = super.updateTask(task);
        save();
        return taskToReturn;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic tmpEpic = super.addEpic(epic);
        save();
        return tmpEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic epicToReturn = super.updateEpic(epic);
        save();
        return epicToReturn;
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask tmpSubtask = super.addSubtask(subtask);
        save();
        return tmpSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask subtaskToReturn = super.updateSubtask(subtask);
        save();
        return subtaskToReturn;
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    protected void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(tasksFile, StandardCharsets.UTF_8)) {
            writer.write(CsvConverters.HEADER_LINE);
            for (Task task : getTasks().values()) {
                writer.write(CsvConverters.taskToCsvString(task) + "\n");
            }
            for (Epic epic : getEpics().values()) {
                writer.write(CsvConverters.epicToCsvString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks().values()) {
                writer.write(CsvConverters.subtaskToCsvString(subtask) + "\n");
            }
            writer.write("\n");
            writer.write(CsvConverters.historyToCsvString(history));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }
}