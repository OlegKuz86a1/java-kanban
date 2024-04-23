package service;

import model.Task;

import java.util.LinkedList;

public interface HistoryManager {

     LinkedList<Task> getHistory();
     void add(Task task);
}
