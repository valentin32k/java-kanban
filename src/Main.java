import manager.Managers;
import manager.TaskManager;
import tasks.*;

import java.util.Set;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

//        тестируем Tasks
        manager.addTask(new Task("Задача 1",
                "Описание 1",
                Status.NEW));
        System.out.println(manager.getTasks().values());
        manager.clearTasks();
//        null
        System.out.println(manager.getTasks().values());
        manager.clearTasks();
        System.out.println(manager.getTask(21));
        manager.removeTask(123);

//        Не null
        System.out.println();
        manager.addTask(new Task("Задача 1",
                "Описание 1",
                Status.NEW));
        manager.addTask(new Task("Задача 2",
                "Описание 2",
                Status.DONE));
        manager.addTask(new Task("Задача 3",
                "Описание 3",
                Status.NEW));
        for (Task task : manager.getTasks().values()) {
            System.out.println(task);
        }

        Set<Integer> set = manager.getTasks().keySet();
        Integer[] keys = set.toArray(new Integer[set.size()]);

        Task tmpTask = manager.getTask(keys[1]);
        manager.updateTask(new Task(tmpTask.getName(),
                tmpTask.getDescription(),
                tmpTask.getId(),
                Status.IN_PROGRESS));
        tmpTask = manager.getTask(keys[2]);
        manager.updateTask(new Task(tmpTask.getName(),
                tmpTask.getDescription(),
                tmpTask.getId(),
                Status.DONE));
        System.out.println();
        for (Task task : manager.getTasks().values()) {
            System.out.println(task);
        }

        manager.removeTask(keys[0]);
        System.out.println();
        for (Task task : manager.getTasks().values()) {
            System.out.println(task);
        }


        // Тестируем Epic + Subtask
        manager.addEpic(new Epic("Эпик 1", "Описание 1"));
        manager.addEpic(new Epic("Эпик 2", "Описание 2"));
        manager.addEpic(new Epic("Эпик 3", "Описание 3"));


        manager.addSubtask(new Subtask("Задача 1.1", "Описание 1.1", 5, Status.NEW));
        manager.addSubtask(new Subtask("Задача 1.2", "Описание 1.2", 5, Status.NEW));
        manager.addSubtask(new Subtask("Задача 1.3", "Описание 1.3", 5, Status.NEW));
        manager.addSubtask(new Subtask("Задача 3.1", "Описание 3.1", 7, Status.NEW));
        manager.addSubtask(new Subtask("Задача 3.2", "Описание 3.2", 7, Status.NEW));


        for (Epic epic : manager.getEpics().values()) {
            manager.getEpic(epic.getId());
            manager.getEpicSubtasks(epic.getId());
            for (Subtask st : manager.getEpicSubtasks(epic.getId()).values()) {
                manager.getSubtask(st.getId());
            }
        }


        System.out.println();
        System.out.println("История");
        for (AbstractTask task : manager.getHistory()) {
            System.out.println(task);
        }
//
//        manager.updateSubtask(new Subtask("Задача 3.1","Описание 3.1",6,7,AbstractTask.DONE));
//        manager.updateSubtask(new Subtask("Задача 3.2","Описание 3.2",6,8,AbstractTask.IN_PROGRESS));
//
//        Epic currEpic = manager.getEpic(6);
//        HashSet<Integer> ssId = currEpic.getSubtasksId();
//        Epic newEpic = new Epic("Эпик 333", "Описание 333", 6);
//
//        for (Integer id : ssId) {
//            newEpic.addSubtaskId(id);
//        }
//        manager.updateEpic(newEpic);
//
//        manager.removeSubtask(8);
//        manager.removeSubtask(7);
//
//        for (Epic epic : manager.getEpics().values()) {
//            System.out.println(epic);
//            for (Subtask st : manager.getEpicSubtasks(epic.getId()).values()) {
//                System.out.println(st);
//            }
//            System.out.println();


    }
}

