package service;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.List;

public interface TaskManager {
    Task create(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task getById(int id);

    Epic getByIDEpic(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    Iterable<Task> getAllTasks();

    Iterable<Epic> getAllEpics();

    Iterable<Subtask> getAllSubtasks();

    void update(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteAll();

    void deleteAllEpic();

    void deleteAllSubtask();

    void deleteById(int id);

    void deleteByIdEpic(int id);

    void deleteByIdSubtask(int id);
}


