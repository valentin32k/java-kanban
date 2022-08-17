package manager;

import tasks.AbstractTask;

import java.util.List;

public interface HistoryManager {
    public void add(AbstractTask task);

    public List<AbstractTask> getHistory();
}
