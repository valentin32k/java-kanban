package tasks;

public class Subtask extends AbstractTask {
    private final int epicId;

    public Subtask(String name, String description, int epicId, int id, Status status) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, 0, status);
        this.epicId = epicId;
    }

    public final int getEpicId() {
        return epicId;
    }

    public static String toCsvString(Subtask subtask) {
        return subtask.getId() +
                "," + TaskType.SUBTASK +
                "," + subtask.getName() +
                "," + subtask.getStatus() +
                "," + subtask.getDescription() +
                "," + subtask.getEpicId();
    }

    public static Subtask fromCsvString(String value) {
        String[] subtaskArray = value.split(",");
        return new Subtask(subtaskArray[2], subtaskArray[4], Integer.valueOf(subtaskArray[5]),
                Integer.valueOf(subtaskArray[0]), Status.valueOf(subtaskArray[3]));
    }

    @Override
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