package tasks;

public class Subtask extends AbstractTask {
    private final int epicId;

    public Subtask(String name, String description, int epicId, int id, byte status) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, byte status) {
        super(name, description, 0, status);
        this.epicId = epicId;
    }

    public final int getEpicId() {
        return epicId;
    }

    public String toString() {
        return "Subtask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", epicId=" + epicId +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }
}
