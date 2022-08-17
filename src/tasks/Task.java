package tasks;

public class Task extends AbstractTask {

    public Task(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    public Task(String name, String description, Status status) {
        super(name, description, 0, status);
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
