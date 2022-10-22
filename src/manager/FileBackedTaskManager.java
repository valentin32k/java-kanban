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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
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
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
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
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private void save() {
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