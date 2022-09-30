package manager;

import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface HistoryManager {
    void add(AbstractTask task);

    void remove(int id);

    List<AbstractTask> getHistory();

//    Я тут засомневался. Может быть методам historyToString и
//    historyFromString все таки место в FileBackedTaskManager???
    static String historyToString(HistoryManager manager) {
//    Не сообразил как по другому из List<AbstractTask> сделать List<String>,
//    а без этого на моем компьютере эта магия работать отказывалась
        List<String> list = new ArrayList<>();
        for (AbstractTask task : manager.getHistory()) {
            list.add(String.valueOf(task.getId()));
        }
        return list.stream().collect(Collectors.joining(","));
    }

    static List<Integer> historyFromString(String value) {
        if (value != null) {
            String[] tasksId = value.split(",");
            List<Integer> list = new ArrayList<>();
            for (String id : tasksId) {
                list.add(Integer.valueOf(id));
            }
            return list;
        } else {
            return null;
        }
    }
}
