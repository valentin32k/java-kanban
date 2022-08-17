package manager;

import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    public HashMap<Integer, Task> getTasks();

    public void clearTasks();

    public Task getTask(int id);

    public void addTask(Task task);

    public void updateTask(Task task);

    public void removeTask(int id);

    public HashMap<Integer, Epic> getEpics();

    public void clearEpics();

    public Epic getEpic(int id);

    public void addEpic(Epic epic);

    public void updateEpic(Epic epic);

    public void removeEpic(int id);

    public HashMap<Integer, Subtask> getEpicSubtasks(int id);

    public HashMap<Integer, Subtask> getSubtasks();

    public void clearSubtasks();

    public Subtask getSubtask(int id);

    public void addSubtask(Subtask subtask);

    public void updateSubtask(Subtask subtask);

    public void removeSubtask(int id);

    public List<AbstractTask> getHistory();
}
