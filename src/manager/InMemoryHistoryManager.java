package manager;

import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<AbstractTask> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(AbstractTask task) {
        if (task != null) {
            history.add(task);
        }
        if (history.size() > 10) {
            history.remove(0);
        }
    }

    @Override
    public List<AbstractTask> getHistory() {
        return history;
    }
}
