package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.LinkedList;
import java.util.List;

public interface TaskManager {

    Task create(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task getById(int id);

    Epic getByIDEpic(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Subtask getSubtaskId(int id);

    List<Task> getPrioritizedTasks();

    void update(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteAll();

    void deleteAllEpic();

    void deleteAllSubtask();

    void deleteById(int id);

    void deleteByIdEpic(int id);

    void deleteByIdSubtask(int id);

    LinkedList<Task> getHistory();

}


