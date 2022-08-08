package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.HashSet;

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

    private int getId() {
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
//        Здесь мне бы хотелось написать что-нибудь вроде:
//        public void addTask(String name, String description, byte status) {
//            int id = this.getId();
//            tasksById.put(id, new Task(name,description,id,status));
//        },
//        но требование ТЗ - передавать методу новый объект (экземпляр класса Task).
//        В этой версии метода addTask предполагаю, что task приходит с нулевым id (добавил в Task новый конструктор),
//        а корректный id присваивается менеджером.
//        Мне кажется криво, но как правильно я не знаю.
//        Подскажи правильный вариант реализации метода, на следующей итерации все поправлю
        if (task != null) {
            int id = this.getId();
            tasksById.put(id, new Task(task.getName(), task.getDescription(), id, task.getStatus()));
        } else {
            System.out.println("Передано пустое значение task");
        }
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
        if (epic != null) {
            int id = this.getId();
            epicsById.put(id, new Epic(epic.getName(), epic.getDescription(), id));
        } else {
            System.out.println("Передано пустое значение epic");
        }
        if (!epic.getSubtasksId().isEmpty()) {
            System.out.println("Записан только эпик. Для добавления подзадач необходимо воспользоваться методом addSubtask()");
        }
    }

    public void updateEpic(Epic epic) {
        if ((epic != null) && (epicsById.get(epic.getId()) != null)) {
            if (!epic.getSubtasksId().isEmpty()) {
                System.out.println("Перечень подзадач не будет добавлен в эпик. " +
                        "Для изменения состава подзадач необходимо использовать методы addSubtask(), removeSubtask()");
            }
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

    public void removeEpic(int id) {
        if (epicsById.get(id) != null) {
            for (Integer subtaskKey : epicsById.get(id).getSubtasksId()) {
                subtasksById.remove(subtaskKey);
            }
            epicsById.remove(id);
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

    public HashMap<Integer, Subtask> getEpicSubtasks(int id) {
        if (epicsById.get(id) != null) {
            HashMap<Integer, Subtask> subtasksList = new HashMap<>();
            for (Integer subtaskKey : epicsById.get(id).getSubtasksId()) {
                subtasksList.put(subtaskKey, subtasksById.get(subtaskKey));
            }
            return subtasksList;
        } else {
            System.out.println("Эпика с таким id не существует");
            return null;
        }
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
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            int subtaskId = this.getId();

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

    public void updateSubtask(Subtask subtask) {
        if ((subtask != null)
                && (subtasksById.get(subtask.getId()) != null)
                && (epicsById.get(subtask.getEpicId()) != null)) {
            int newEpicId = subtask.getEpicId();
            int currentEpicId = subtasksById.get(subtask.getId()).getEpicId();
            if (currentEpicId != newEpicId) {
                epicsById.get(currentEpicId).removeSubtaskId(subtask.getId());
                epicsById.get(currentEpicId).updateEpicStatus(subtasksById);
                epicsById.get(newEpicId).addSubtaskId(subtask.getId());
            }
            subtasksById.put(subtask.getId(), subtask);
            epicsById.get(newEpicId).updateEpicStatus(subtasksById);
        } else {
            System.out.println("Переданы не корректные значения");
        }
    }

    public void removeSubtask(int id) {
        if (subtasksById.get(id) != null) {
            int epicId = subtasksById.get(id).getEpicId();
            epicsById.get(epicId).removeSubtaskId(id);
            subtasksById.remove(id);
            epicsById.get(epicId).updateEpicStatus(subtasksById);
        } else {
            System.out.println("Подзадачи с таким id не существует");
        }
    }
}
