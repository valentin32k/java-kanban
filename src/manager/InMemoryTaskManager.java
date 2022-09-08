package manager;

import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksById;
    private final HashMap<Integer, Epic> epicsById;
    private final HashMap<Integer, Subtask> subtasksById;
    private final HistoryManager history;
    private int idGenerator;


    public InMemoryTaskManager() {
        tasksById = new HashMap<>();
        epicsById = new HashMap<>();
        subtasksById = new HashMap<>();
        idGenerator = 0;
        history = Managers.getDefaultHistory();
    }

    private int getId() {
        return ++idGenerator;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasksById;
    }

    @Override
    public void clearTasks() {
        for (Integer taskId : tasksById.keySet()) {
            removeTask(taskId);
        }
    }

    @Override
    public Task getTask(int id) {
        history.add(tasksById.get(id));
        return tasksById.get(id);
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            int id = getId();
            tasksById.put(id, new Task(task.getName(), task.getDescription(), id, task.getStatus()));
        } else {
            System.out.println("Передано пустое значение task");
        }
    }

    @Override
    public void updateTask(Task task) {
        if ((task != null) && (tasksById.get(task.getId()) != null)) {
            tasksById.put(task.getId(), task);
        } else {
            System.out.println("Переданы не корректные значения");
        }
    }

    @Override
    public void removeTask(int id) {
        history.remove(id);
        tasksById.remove(id);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epicsById;
    }

    @Override
    public void clearEpics() {
        for (Integer epicId : epicsById.keySet()) {
            removeEpic(epicId);
        }
    }

    @Override
    public Epic getEpic(int id) {
        history.add(epicsById.get(id));
        return epicsById.get(id);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            int id = getId();
            epicsById.put(id, new Epic(epic.getName(), epic.getDescription(), id));
            if (!epic.getSubtasksId().isEmpty()) {
                System.out.println("Записан только эпик. Для добавления подзадач необходимо воспользоваться методом addSubtask()");
            }
        } else {
            System.out.println("Передано пустое значение epic");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if ((epic != null) && (epicsById.get(epic.getId()) != null)) {
            Epic currentEpic = epicsById.get(epic.getId());
            HashSet<Integer> currentSubtasksId = currentEpic.getSubtasksId();
            epic.clearSubtasksId();
            for (Integer id : currentSubtasksId) {
                epic.addSubtaskId(id);
            }
            epic.updateEpicStatus(subtasksById);
            epicsById.put(epic.getId(), epic);
        } else {
            System.out.println("Переданы не корректные значения");
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epicsById.get(id) != null) {
            Set<Integer> keySet = epicsById.get(id).getSubtasksId();
            Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);
            for (int i = 0; i < keyArray.length; i++) {
                removeSubtask(keyArray[i]);
            }
            history.remove(id);
            epicsById.remove(id);
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

    @Override
    public HashMap<Integer, Subtask> getEpicSubtasks(int id) {
        if (epicsById.get(id) != null) {
            HashMap<Integer, Subtask> epicSubtasksById = new HashMap<>();
            for (Integer subtaskKey : epicsById.get(id).getSubtasksId()) {
                epicSubtasksById.put(subtaskKey, subtasksById.get(subtaskKey));
            }
            return epicSubtasksById;
        } else {
            System.out.println("Эпика с таким id не существует");
            return null;
        }
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasksById;
    }

    @Override
    public void clearSubtasks() {
        for (Integer subtaskId : subtasksById.keySet()) {
            removeSubtask(subtaskId);
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        history.add(subtasksById.get(id));
        return subtasksById.get(id);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            int subtaskId = getId();

            Epic tmpEpic = epicsById.get(epicId);
            if (tmpEpic != null) {
                subtasksById.put(subtaskId, new Subtask(subtask.getName(),
                        subtask.getDescription(),
                        epicId,
                        subtaskId,
                        subtask.getStatus()));
                tmpEpic.addSubtaskId(subtaskId);
                tmpEpic.updateEpicStatus(subtasksById);
            } else {
                System.out.println("Эпика с указанным в подзадаче id не существует");
            }
        } else {
            System.out.println("Передано пустое значение subtask");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (subtasksById.get(subtask.getId()) == null) {
            return;
        }
        if (epicsById.get(subtask.getEpicId()) == null) {
            return;
        }
        int newEpicId = subtask.getEpicId();
        int currentEpicId = subtasksById.get(subtask.getId()).getEpicId();
        if (currentEpicId != newEpicId) {
            epicsById.get(currentEpicId).removeSubtaskId(subtask.getId());
            epicsById.get(currentEpicId).updateEpicStatus(subtasksById);
            epicsById.get(newEpicId).addSubtaskId(subtask.getId());
        }
        subtasksById.put(subtask.getId(), subtask);
        epicsById.get(newEpicId).updateEpicStatus(subtasksById);
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasksById.get(id) != null) {
            int epicId = subtasksById.get(id).getEpicId();
            epicsById.get(epicId).removeSubtaskId(id);
            subtasksById.remove(id);
            history.remove(id);
            epicsById.get(epicId).updateEpicStatus(subtasksById);
        } else {
            System.out.println("Подзадачи с таким id не существует");
        }
    }

    @Override
    public List<AbstractTask> getHistory() {
        return history.getHistory();
    }
}
