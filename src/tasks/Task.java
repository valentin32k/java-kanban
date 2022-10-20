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
        return "Task{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + super.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")) +
                ", duration=" + super.getDuration().toMinutes() +
                '}';
    }
}
