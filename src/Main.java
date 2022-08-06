import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
//        Manager manager = new Manager();

////        тестируем Tasks
//        manager.addTask(new Task("Задача 1",
//                "Описание 1",
//                manager.getId(),
//                AbstractTask.NEW));
//        System.out.println(manager.getTasks().values());
//        manager.clearTasks();
////        null
//        System.out.println(manager.getTasks().values());
//        manager.clearTasks();
//        System.out.println(manager.getTask(21));
//        manager.removeTask(123);
//
////        Не null
//        System.out.println();
//        manager.addTask(new Task("Задача 1",
//                "Описание 1",
//                manager.getId(),
//                AbstractTask.NEW));
//        manager.addTask(new Task("Задача 2",
//                "Описание 2",
//                manager.getId(),
//                AbstractTask.NEW));
//        manager.addTask(new Task("Задача 3",
//                "Описание 3",
//                manager.getId(),
//                AbstractTask.NEW));
//        for (Task task : manager.getTasks().values()) {
//            System.out.println(task);
//        }
//
//        Set<Integer> set = manager.getTasks().keySet();
//        Integer[] keys = set.toArray(new Integer[set.size()]);
//
//        Task tmpTask = manager.getTask(keys[1]);
//        manager.updateTask(new Task(tmpTask.getName(),
//                tmpTask.getDescription(),
//                tmpTask.getId(),
//                AbstractTask.IN_PROGRESS));
//        tmpTask = manager.getTask(keys[2]);
//        manager.updateTask(new Task(tmpTask.getName(),
//                tmpTask.getDescription(),
//                tmpTask.getId(),
//                AbstractTask.DONE));
//        System.out.println();
//        for (Task task : manager.getTasks().values()) {
//            System.out.println(task);
//        }
//
//        manager.removeTask(keys[0]);
//        System.out.println();
//        for (Task task : manager.getTasks().values()) {
//            System.out.println(task);
//        }



//        // Тестируем Epic + Subtask
//        manager.addEpic(new Epic("Эпик 1", "Описание 1", manager.getId()));
//        manager.addSubtask(new Subtask("Задача 1.1","Описание 1.1",1,manager.getId(),AbstractTask.NEW));
//        manager.addSubtask(new Subtask("Задача 1.2","Описание 1.2",1,manager.getId(),AbstractTask.NEW));
//        manager.addSubtask(new Subtask("Задача 1.3","Описание 1.3",1,manager.getId(),AbstractTask.NEW));
//
//        manager.addEpic(new Epic("Эпик 2", "Описание 2", manager.getId()));
//
//        manager.addEpic(new Epic("Эпик 3", "Описание 3", manager.getId()));
//        manager.addSubtask(new Subtask("Задача 3.1","Описание 3.1",6,manager.getId(),AbstractTask.NEW));
//        manager.addSubtask(new Subtask("Задача 3.2","Описание 3.2",6,manager.getId(),AbstractTask.NEW));
//
//
//
//        for (Epic epic : manager.getEpics().values()) {
//            System.out.println(epic);
//            for (Subtask st : manager.getEpicSubtasks(epic.getId()).values()) {
//                System.out.println(st);
//            }
//            System.out.println();
//        }
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
//        }
    }
}
