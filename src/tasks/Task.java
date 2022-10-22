package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task extends AbstractTask {

    public Task(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, 0, status, startTime, duration);
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
        return "Task{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + startTimeString +
                ", duration=" + durationString +
                '}';
    }
}
