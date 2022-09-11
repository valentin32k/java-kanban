package manager;

import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public void add(AbstractTask task) {
        if (task != null) {
            if (history.contains(task.getId())) {
                history.removeById(task.getId());
            }
            history.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        history.removeById(id);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return history.getTasks();
    }

    private class CustomLinkedList {
        private Node head;
        private Node tail;
        final private Map<Integer, Node> nodeById;

        public CustomLinkedList() {
            head = null;
            tail = null;
            nodeById = new HashMap<>();
        }

        private void removeNode(Node node) {
            if (node == head) {
                head = node.next;
                if (head != null) {
                    head.prev = null;
                }
            } else if (node == tail) {
                tail = node.prev;
                tail.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            nodeById.remove(node.value.getId());
        }

        public void removeById(int id) {
            Node node = nodeById.get(id);
            if (node != null) {
                removeNode(node);
            }
        }

        public boolean contains(int taskId) {
            return nodeById.containsKey(taskId);
        }

        public void linkLast(AbstractTask task) {
            Node node = new Node(task);
            if (tail == null) {
                head = node;
                tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
            nodeById.put(task.getId(), node);
        }

        public ArrayList<AbstractTask> getTasks() {
            ArrayList<AbstractTask> tasks = new ArrayList<>();
            Node node = head;
            while (node != null) {
                tasks.add(node.value);
                node = node.next;
            }
            return tasks;
        }
    }
}

class Node {
    public Node next;
    public Node prev;
    final public AbstractTask value;

    public Node(AbstractTask value) {
        this.value = value;
        next = null;
        prev = null;
    }
}
