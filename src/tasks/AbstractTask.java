package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

public abstract class AbstractTask {

    private final String name;
    private final String description;
    private final int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    protected AbstractTask(String name, String description, int id, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    protected AbstractTask(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final int getId() {
        return id;
    }

    public final Status getStatus() {
        return status;
    }

    public final LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    protected final void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask task = (AbstractTask) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status && Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }
}
