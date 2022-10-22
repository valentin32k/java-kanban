package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends AbstractTask {
    private final int epicId;

    public Subtask(String name, String description, int epicId, int id, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, 0, status, startTime, duration);
        this.epicId = epicId;
    }

    public final int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public String toString() {
        LocalDateTime startTime = super.getStartTime();
        String startTimeString;
        if (startTime == null) {
            startTimeString = "null";
        } else {
            startTimeString = startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        }
        Duration duration = super.getDuration();
        String durationString;
        if (duration == null) {
            durationString = "null";
        } else {
            durationString = String.valueOf(duration.toMinutes());
        }
        return "Subtask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", epicId=" + epicId +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + startTimeString +
                ", duration=" + durationString +
                '}';
    }
}