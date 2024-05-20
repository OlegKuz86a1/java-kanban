package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history = new HashMap<>();

    private static class Node {

       private Node next;
       private Task item;
       private Node prev;

        public Node(Node prev, Task item, Node next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    private Node first;
    private Node last;

    @Override
    public List<Task> getAll() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;

        while (current != null) {
            list.add(current.item);
            current = current.next;
        }
        return list;
    }

    @Override
    public void add(Task task) {

        if (task == null) {
            return;
        }

        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }

        linkLast(task);
        history.put(task.getId(), last);

    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);

        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(last, task, null);
        last = newNode;

        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
    }

    private void removeNode(Node node) {

        if (node == null || node.next == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else {
            first = node.next;
            first.prev = null;
        }
        history.remove(node.item.getId());
    }
}

