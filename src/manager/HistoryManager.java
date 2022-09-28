package manager;

import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    void add(AbstractTask task);

    void remove(int id);

    List<AbstractTask> getHistory();

    static String historyToString(HistoryManager manager) {
        List<AbstractTask> history = manager.getHistory();
        String historyId = "";
        for (AbstractTask task : manager.getHistory()) {
            historyId += task.getId() + ",";
        }
        return historyId;
    }

    static List<Integer> historyFromString(String value) {
        if (value != null) {
            String[] tasksId = value.split(",");
            List<Integer> list = new ArrayList<>();
            for (String id : tasksId) {
                if (!"".equals(id)) {
                    list.add(Integer.valueOf(id));
                }
            }
            return list;
        } else {
            return null;
        }
    }
}
