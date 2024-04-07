package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private int generatorId;

    public Task create(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);

        if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = subtask.getEpic();
            Set<Integer> subtaskIds = epic.getSubtaskIds();
            subtaskIds.add(subtask.getId());
            epic.setStatus(defineStatus(subtaskIds));
        } else if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            epic.setStatus(defineStatus(epic.getSubtaskIds()));
        }

        return task;
    }

    public Task getById(int id) {

        return tasks.get(id);
    }

    public List<Task> getAll() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> getAllTasks() {
        return tasks.values().stream()
                .filter(task -> task.getClass() == Task.class)
                .toList();
    }

    public List<Epic> getAllEpics() {
        return tasks.values().stream()
                .filter(task -> task.getClass() == Epic.class)
                .map(task -> (Epic) task)
                .toList();
    }

    public List<Subtask> getAllSubtasks() {
        return tasks.values().stream()
                .filter(task -> task.getClass() == Subtask.class)
                .map(task -> (Subtask) task)
                .toList();
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = (Epic) tasks.get(epicId);

        return epic.getSubtaskIds().stream()
                .map(tasks::get)
                .filter(task -> task.getClass() == Subtask.class)
                .map(task -> (Subtask) task)
                .toList();
    }

    public void update(Task task) {
        tasks.put(task.getId(), task);

        if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = subtask.getEpic();
            epic.setStatus(defineStatus(epic.getSubtaskIds()));
        }
    }

    public void deleteAll() {
        tasks.clear();
    }

    public void deleteById(int id) {
        Task removedTask = tasks.remove(id);

        if (removedTask.getClass() == Subtask.class) {
            Subtask removedSubtask = (Subtask) removedTask;
            Epic epic = removedSubtask.getEpic();
            epic.getSubtaskIds().remove(id);
            epic.setStatus(defineStatus(epic.getSubtaskIds()));
        }

        if (removedTask.getClass() == Epic.class) {
            Epic epic = (Epic) removedTask;
            epic.getSubtaskIds().forEach(tasks::remove);
        }
    }

    private TaskStatus defineStatus(Set<Integer> subtaskIds) {
        if (subtaskIds == null || subtaskIds.isEmpty()) {
            return TaskStatus.NEW;
        }

        List<TaskStatus> statuses = subtaskIds.stream()
                .map(id -> tasks.get(id).getStatus())
                .distinct()
                .toList();

        return statuses.size() == 1 ? statuses.getFirst() : TaskStatus.IN_PROGRESS;
    }
}
