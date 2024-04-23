package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

   private final LinkedList<Task> listTasks = new LinkedList<>();


    @Override
    public LinkedList<Task> getHistory() {
        return listTasks;
    }

    @Override
    public void add(Task task) {
        if(listTasks.size() >= 10) {
            listTasks.removeFirst();
        }
        listTasks.add(task);
    }

}
