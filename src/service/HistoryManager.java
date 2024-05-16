package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public interface HistoryManager {

     List<Task> getAll();
     void add(Task task);
     void remove(int id);
}
