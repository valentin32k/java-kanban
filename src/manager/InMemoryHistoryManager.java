package manager;

import tasks.AbstractTask;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int STORED_NUMBER_OF_RECORDS = 10;
    private final List<AbstractTask> history;


    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public void add(AbstractTask task) {
        if (task != null) {
            history.add(task);
        }
        if (history.size() > STORED_NUMBER_OF_RECORDS) {
            history.remove(0);
        }
    }

    @Override
    public List<AbstractTask> getHistory() {
        return history;
    }
}
