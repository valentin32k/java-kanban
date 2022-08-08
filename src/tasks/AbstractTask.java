package tasks;

public abstract class AbstractTask {

    private final String name;
    private final String description;
    private final int id;
    private byte status;

    protected AbstractTask(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    protected AbstractTask(String name, String description, int id, byte status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
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

    public final byte getStatus() {
        return status;
    }

    protected final void setStatus(byte status) {
        this.status = status;
    }
}
