package manager;

import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskSerializer {
    String taskToString(Task task);

    Task taskFromString(String value);

    String subtaskToString(Subtask subtask);

    Subtask subtaskFromString(String value);

    String epicToString(Epic epic);

    Epic epicFromString(String value);

    String historyToString(List<AbstractTask> tasks);

    List<Integer> historyFromString(String value);
}
