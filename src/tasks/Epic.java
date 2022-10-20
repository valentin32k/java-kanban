package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends AbstractTask {

    private final HashSet<Integer> subtasksId;

    public Epic(String name, String description, int id) {
        super(name, description, id, null, null);
        subtasksId = new HashSet<>();

    }

    public Epic(String name, String description) {
        super(name, description, 0, null, null);
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
        updateEpicTime(subtasksById);
    }

    public final void updateEpicTime(HashMap<Integer, Subtask> subtasksById) {
        Comparator<LocalDateTime> localDateTimeComparator = LocalDateTime::compareTo;
        Optional<LocalDateTime> epicStartTime = subtasksById.values().stream().map(AbstractTask::getStartTime).min(localDateTimeComparator);
        if (epicStartTime.isPresent()) {
            setStartTime(epicStartTime.get());
        } else {
            setStartTime(null);
        }
        Optional<LocalDateTime> epicEndTime = subtasksById.values().stream().map(t -> t.getStartTime().plus(t.getDuration())).max(localDateTimeComparator);
        if (epicStartTime.isPresent() && epicEndTime.isPresent()) {
            setDuration(Duration.between(epicStartTime.get(), epicEndTime.get()));
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId);
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
