package tasks;

public class Task extends AbstractTask {

    public Task(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    public Task(String name, String description, Status status) {
        super(name, description, 0, status);
    }

    public static String toCsvString(Task task) {
        return task.getId() +
                "," + TaskType.TASK +
                "," + task.getName() +
                "," + task.getStatus() +
                "," + task.getDescription();
    }

    public static Task fromCsvString(String value) {
        String[] taskArray = value.split(",");
        return new Task(taskArray[2], taskArray[4],
                Integer.valueOf(taskArray[0]), Status.valueOf(taskArray[3]));
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }
}
