package manager;

import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksById;
    private final HashMap<Integer, Epic> epicsById;
    private final HashMap<Integer, Subtask> subtasksById;
    private final TreeSet<AbstractTask> tasksTreeSet;
    protected final HistoryManager history;
    private int idGenerator;

    public InMemoryTaskManager() {
        tasksById = new HashMap<>();
        epicsById = new HashMap<>();
        subtasksById = new HashMap<>();
        history = Managers.getDefaultHistory();
        Comparator<AbstractTask> tasksComparator = Comparator.comparing(AbstractTask::getStartTime);
        tasksTreeSet = new TreeSet<>(tasksComparator);
        idGenerator = 0;
    }

    private int getId() {
        return ++idGenerator;
    }

    private boolean isTaskTimeValid(AbstractTask task, TreeSet<AbstractTask> tasksSet) {
        boolean isValid = tasksSet.stream().allMatch(t->{if ((t.getStartTime().isAfter(task.getStartTime()) &&
                (t.getStartTime().isBefore(task.getEndTime()))) ||
                ((t.getStartTime().isBefore(task.getStartTime())) &&
                        (t.getEndTime().isAfter(task.getStartTime())))) {
            return false;
        } else {
            return true;
        }});
        return isValid;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasksById;
    }

    @Override
    public void clearTasks() {
        Set<Integer> keySet = tasksById.keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);
        for (int i = 0; i < keyArray.length; i++) {
            removeTask(keyArray[i]);
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
            int id = task.getId();
            if (id == 0) {
                id = getId();
            } else if (id > idGenerator) {
                idGenerator = id;
            }
            if (id > 0) {
                Task tmpTask = new Task(task.getName(),
                        task.getDescription(),
                        id,
                        task.getStatus(),
                        task.getStartTime(),
                        task.getDuration());
                if (isTaskTimeValid(tmpTask,tasksTreeSet)) {
                    tasksById.put(id, tmpTask);
                    tasksTreeSet.add(tmpTask);
                } else {
                    System.out.println("Временной интервал задачи пересекается с другими задачами");
                }

            } else {
                System.out.println("Передан не корректный id");
            }
        } else {
            System.out.println("Передано пустое значение task");
        }
    }

    @Override
    public void updateTask(Task task) {
        if ((task != null) && (tasksById.get(task.getId()) != null)) {
            TreeSet<AbstractTask> tasksSet = tasksTreeSet;
            tasksSet.remove(task);
            if (isTaskTimeValid(task, tasksSet)) {
                removeTask(task.getId());
                tasksTreeSet.add(task);
                tasksById.put(task.getId(), task);
            } else {
                System.out.println("Временной интервал задачи пересекается с другими задачами");
            }
        } else {
            System.out.println("Переданы не корректные значения");
        }
    }

    @Override
    public void removeTask(int id) {
        history.remove(id);
        tasksTreeSet.remove(tasksById.get(id));
        tasksById.remove(id);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epicsById;
    }

    @Override
    public void clearEpics() {
        Set<Integer> keySet = epicsById.keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);
        for (int i = 0; i < keyArray.length; i++) {
            removeEpic(keyArray[i]);
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
            int id = epic.getId();
            if (id == 0) {
                id = getId();
            } else if (id > idGenerator) {
                idGenerator = id;
            }
            if (id > 0) {
                epicsById.put(id, new Epic(epic.getName(), epic.getDescription(), id));
                if (!epic.getSubtasksId().isEmpty()) {
                    System.out.println("Записан только эпик. Для добавления подзадач необходимо воспользоваться методом addSubtask()");
                }
            } else {
                System.out.println("Передан не корректный id");
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
        Set<Integer> keySet = subtasksById.keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);
        for (int i = 0; i < keyArray.length; i++) {
            removeSubtask(keyArray[i]);
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
            int subtaskId = subtask.getId();
            if (subtaskId == 0) {
                subtaskId = getId();
            } else if (subtaskId > idGenerator) {
                idGenerator = subtaskId;
            }
            if (subtaskId > 0) {
                Epic tmpEpic = epicsById.get(epicId);
                if (tmpEpic != null) {
                    Subtask tmpSubtask = new Subtask(subtask.getName(),
                            subtask.getDescription(),
                            epicId,
                            subtaskId,
                            subtask.getStatus(),
                            subtask.getStartTime(),
                            subtask.getDuration());
                    if (isTaskTimeValid(tmpSubtask,tasksTreeSet)) {
                        subtasksById.put(subtaskId, tmpSubtask);
                        tasksTreeSet.add(tmpSubtask);
                        tmpEpic.addSubtaskId(subtaskId);
                        tmpEpic.updateEpicStatus(subtasksById);
                    } else {
                        System.out.println("Временной интервал подзадачи пересекается с другими задачами");
                    }
                } else {
                    System.out.println("Эпика с указанным в подзадаче id не существует");
                }
            } else {
                System.out.println("Передан не корректный id подзадачи");
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
        TreeSet<AbstractTask> subtasksSet = tasksTreeSet;
        subtasksSet.remove(subtask);
        if (!isTaskTimeValid(subtask,subtasksSet)) {
            return;
        }
        int newEpicId = subtask.getEpicId();
        int currentEpicId = subtasksById.get(subtask.getId()).getEpicId();
        if (currentEpicId != newEpicId) {
            epicsById.get(currentEpicId).removeSubtaskId(subtask.getId());
            epicsById.get(currentEpicId).updateEpicStatus(subtasksById);
            epicsById.get(newEpicId).addSubtaskId(subtask.getId());
        }
        tasksTreeSet.remove(subtasksById.get(subtask.getId()));
        subtasksById.put(subtask.getId(), subtask);
        epicsById.get(newEpicId).updateEpicStatus(subtasksById);
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasksById.get(id) != null) {
            int epicId = subtasksById.get(id).getEpicId();
            epicsById.get(epicId).removeSubtaskId(id);
            tasksTreeSet.remove(subtasksById.get(id));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        boolean isHistoryIdEquals = Objects.equals(history.getHistory().stream().map(h -> h.getId()).collect(Collectors.toList()),
                that.history.getHistory().stream().map(h -> h.getId()).collect(Collectors.toList()));
        return idGenerator == that.idGenerator &&
                Objects.equals(tasksById, that.tasksById) &&
                Objects.equals(epicsById, that.epicsById) &&
                Objects.equals(subtasksById, that.subtasksById) &&
                Objects.equals(tasksTreeSet, that.tasksTreeSet) &&
                isHistoryIdEquals;
    }

    @Override
    public List<AbstractTask> getPrioritizedTasks() {
        return tasksTreeSet.stream().collect(Collectors.toList());
    }

}