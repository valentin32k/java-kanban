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

    private static class CustomLinkedList {
        private Node head;
        private Node tail;
        final private Map<Integer, List<Node>> listNodeById;

        public CustomLinkedList() {
            head = null;
            tail = null;
            listNodeById = new HashMap<>();
        }

        private void removeNode(Node node) {
            if (node == head) {
                head = node.next;
                if (head != null) {
                    head.prev = null;
                } else {
                    tail = null;
                }
            } else if (node == tail) {
                tail = node.prev;
                tail.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            int nodeId = node.value.getId();
            List<Node> listNode = listNodeById.get(nodeId);
            listNode.remove(0);
            if (listNode.isEmpty()) {
                listNodeById.remove(nodeId);
            }
        }

        public void removeById(int id) {
            List<Node> listNode = listNodeById.get(id);
            if (listNode != null) {
                removeNode(listNode.get(0));
            }
        }

        public boolean contains(int taskId) {
            return listNodeById.containsKey(taskId);
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
            List<Node> listNode = listNodeById.computeIfAbsent(task.getId(), k -> new ArrayList<>());
            listNode.add(node);
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