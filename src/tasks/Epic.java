package tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Epic extends AbstractTask {

    private final HashSet<Integer> subtasksId;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtasksId = new HashSet<>();
    }

    public Epic(String name, String description) {
        super(name, description, 0);
        subtasksId = new HashSet<>();
    }

    public final HashSet<Integer> getSubtasksId() {
        return subtasksId;
    }

    public final void clearSubtasksId() {
        subtasksId.clear();
        setStatus(Status.NEW);
    }

    public final void addSubtaskId(int id) {
        subtasksId.add(id);
    }

    public final void removeSubtaskId(int id) {
        subtasksId.remove(id);
    }

    public final void updateEpicStatus(HashMap<Integer, Subtask> subtasksById) {
        boolean isNew = true;
        boolean isDone = true;

        for (Integer id : subtasksId) {
            if (subtasksById.get(id).getStatus() != Status.NEW) {
                isNew = false;
            }
            if (subtasksById.get(id).getStatus() != Status.DONE) {
                isDone = false;
            }
        }

        if (isNew) {
            this.setStatus(Status.NEW);
        } else if (isDone) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    public static String toString(Epic epic) {
        return epic.getId() +
                "," + TaskType.EPIC +
                "," + epic.getName() +
                "," + epic.getStatus() +
                "," + epic.getDescription();
    }

    public static Epic fromString(String value) {
        String[] epicArray = value.split(",");
        return new Epic(epicArray[2], epicArray[4], Integer.valueOf(epicArray[0]));
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", subtasksId=" + Arrays.toString(subtasksId.toArray()) +
                '}';
    }
}
