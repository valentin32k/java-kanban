package manager;

import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    HashMap<Integer, Task> getTasks();

    void clearTasks();

    Task getTask(int id);

    Task addTask(Task task);

    void updateTask(Task task);

    void removeTask(int id);

    HashMap<Integer, Epic> getEpics();

    void clearEpics();

    Epic getEpic(int id);

    Epic addEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpic(int id);

    HashMap<Integer, Subtask> getEpicSubtasks(int id);

    HashMap<Integer, Subtask> getSubtasks();

    void clearSubtasks();

    Subtask getSubtask(int id);

    Subtask addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    List<AbstractTask> getHistory();

    List<AbstractTask> getPrioritizedTasks();
}