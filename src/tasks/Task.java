package tasks;

public class Task extends AbstractTask {

    public Task(String name, String description, int id, byte status) {
        super(name, description, id, status);
    }

    public Task(String name, String description, byte status) {
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
