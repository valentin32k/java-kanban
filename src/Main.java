import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        Path tasksFile = Paths.get("tasks.txt");

//        1. Заведите несколько разных задач, эпиков и подзадач.
        TaskManager manager = new FileBackedTaskManager(tasksFile);
        manager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        manager.addTask(new Task("Задача 2", "Описание задачи 2", Status.DONE));
        manager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        manager.addSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", 3, Status.NEW));
        manager.addSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", 3, Status.IN_PROGRESS));
        manager.addSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3", 3, Status.DONE));
        manager.addEpic(new Epic("Эпик 2", "Описание эпика 2"));

//        2. Запросите некоторые из них, чтобы заполнилась история просмотра.
        manager.getEpic(3);
        manager.getEpic(7);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getEpic(7);
        manager.getTask(1);
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getSubtask(4);

//        3. Создайте новый FileBackedTasksManager менеджер из этого же файла.
        FileBackedTaskManager newManager = Managers.loadFromFile(tasksFile);
        newManager.addTask(new Task("Задача 3", "Описание задачи 3", Status.DONE));

//        4. Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом, есть в новом менеджере.
        System.out.println("1. Tasks\n- old");
        for (AbstractTask task : manager.getTasks().values()) {
            System.out.println(task);
        }
        System.out.println("- new");
        for (AbstractTask task : newManager.getTasks().values()) {
            System.out.println(task);
        }

        System.out.println("\n2. Epic\n- old");
        for (Epic epic : manager.getEpics().values()) {
            System.out.println(epic);
        }
        System.out.println("- new");
        for (Epic epic : newManager.getEpics().values()) {
            System.out.println(epic);
        }

        System.out.println("\n3. Subtasks\n- old");
        for (Subtask subtask : manager.getSubtasks().values()) {
            System.out.println(subtask);
        }
        System.out.println("- new");
        for (Subtask subtask : newManager.getSubtasks().values()) {
            System.out.println(subtask);
        }

        System.out.println("\n4. History\n- old");
        for (AbstractTask task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("- new");
        for (AbstractTask task : newManager.getHistory()) {
            System.out.println(task);
        }
    }
}

