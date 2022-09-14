import manager.Managers;
import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {


        System.out.println("\n1. Две задачи, эпик с тремя подзадачами и эпик без подзадач");
        TaskManager manager = Managers.getDefault();
        manager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        manager.addTask(new Task("Задача 2", "Описание задачи 2", Status.DONE));
        for (Task task : manager.getTasks().values()) {
            System.out.println(task);
        }
        manager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        manager.addSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", 3, Status.NEW));
        manager.addSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", 3, Status.IN_PROGRESS));
        manager.addSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3", 3, Status.DONE));
        for (Epic epic : manager.getEpics().values()) {
            System.out.println(epic);
        }
        for (Subtask subtask : manager.getEpicSubtasks(3).values()) {
            System.out.println(subtask);
        }
        manager.addEpic(new Epic("Эпик 2", "Описание эпика 2"));
        System.out.println(manager.getEpic(7));

        System.out.println("\n2-3. Запросить задачи, убедиться в отсутствии повторов");
        for (AbstractTask task : manager.getHistory()) {
            System.out.println(task);
        }
        manager.getEpic(3);
        manager.getEpic(7);
        System.out.println();
        for (AbstractTask task : manager.getHistory()) {
            System.out.println(task);
        }
        manager.getTask(2);
        manager.getEpic(3);
        manager.getEpic(7);
        manager.getTask(1);
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getSubtask(4);

        System.out.println();
        for (AbstractTask task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("\n4. Удалить задачу, убедиться что она пропала из истории");
        manager.removeTask(1);
        for (AbstractTask task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("\n5. Удалить эпик с тремя подзадачами, убедиться, что из истории удалился эпик и подзадачи");
        manager.removeEpic(3);
        for (AbstractTask task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

