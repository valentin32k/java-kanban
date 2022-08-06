package tasks;

import java.util.HashMap;
import java.util.HashSet;

public class Epic extends AbstractTask {

    private final HashSet<Integer> subtasksId;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtasksId = new HashSet<>();
    }

    public final HashSet<Integer> getSubtasksId() {
        return subtasksId;
    }

    public final void clearSubtasksId() {
        subtasksId.clear();
        setStatus(NEW);
    }

    public final void addSubtaskId(int id) {
        subtasksId.add(id);
    }

    public final void removeSubtaskId(int id) {
        subtasksId.remove(id);
    }

    public final void checkEpicStatus(HashMap<Integer, Subtask> subtasksById) {
        boolean isNew = true;
        boolean isDone = true;

        for (Integer id : subtasksId) {
            if (subtasksById.get(id).getStatus() != NEW) {
                isNew = false;
            }
            if (subtasksById.get(id).getStatus() != DONE) {
                isDone = false;
            }
        }

        if (isNew) {
            this.setStatus(NEW);
        } else if (isDone) {
            this.setStatus(DONE);
        } else {
            this.setStatus(IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }
}
