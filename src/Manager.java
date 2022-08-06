import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasksById;
    private final HashMap<Integer, Epic> epicsById;
    private final HashMap<Integer, Subtask> subtasksById;
    private int idGenerator;

    public Manager() {
        tasksById = new HashMap<>();
        epicsById = new HashMap<>();
        subtasksById = new HashMap<>();
        idGenerator = 0;
    }

    public int getId() {
        return ++idGenerator;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasksById;
    }

    public void clearTasks() {
        tasksById.clear();
    }

    public Task getTask(int id) {
        return tasksById.get(id);
    }

    public void addTask(Task task) {
        tasksById.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        tasksById.put(task.getId(), task);
    }

    public void removeTask(int id) {
        tasksById.remove(id);
    }

    public HashMap<Integer, Epic> getEpics() {
        return epicsById;
    }

    public void clearEpics() {
        subtasksById.clear();
        epicsById.clear();
    }

    public Epic getEpic(int id) {
        return epicsById.get(id);
    }

    public void addEpic(Epic epic) {
        epicsById.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        epic.checkEpicStatus(subtasksById);
        epicsById.put(epic.getId(), epic);
    }

    public void removeEpic(int id) {
        for (Integer subtaskKey : epicsById.get(id).getSubtasksId()) {
            subtasksById.remove(subtaskKey);
        }
        epicsById.remove(id);
    }

    public HashMap<Integer, Subtask> getEpicSubtasks(int id) {
        HashMap<Integer, Subtask> subtasksList = new HashMap<>();
        for (Integer subtaskKey : epicsById.get(id).getSubtasksId()) {
            subtasksList.put(subtaskKey, subtasksById.get(subtaskKey));
        }
        return subtasksList;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasksById;
    }

    public void clearSubtasks() {
        for (Epic epic : epicsById.values()) {
            epic.clearSubtasksId();
        }
        subtasksById.clear();
    }

    public Subtask getSubtask(int id) {
        return subtasksById.get(id);
    }

    public void addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        int subtaskId = subtask.getId();

        Epic tmpEpic = epicsById.get(epicId);
        tmpEpic.addSubtaskId(subtaskId);
        subtasksById.put(subtask.getId(), subtask);
        tmpEpic.checkEpicStatus(subtasksById);
    }

    public void updateSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();

        subtasksById.put(subtask.getId(), subtask);
        epicsById.get(epicId).checkEpicStatus(subtasksById);
    }

    public void removeSubtask(int id) {
        int epicId = subtasksById.get(id).getEpicId();
        epicsById.get(epicId).removeSubtaskId(id);
        subtasksById.remove(id);
        epicsById.get(epicId).checkEpicStatus(subtasksById);
    }
}
