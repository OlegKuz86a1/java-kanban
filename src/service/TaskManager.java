package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId;

    public Task create(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
        epic.setStatus(defineStatus(epic.getSubtaskIds()));
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++generatorId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        Set<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.add(subtask.getId());
        epic.setStatus(defineStatus(subtaskIds));
        return subtask;
    }

    public Task getById(int id) {
        return tasks.get(id);
    }
    public Epic getByIDEpic(int id) {
        return epics.get(id);
    }
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpic().getId() == epicId)
                .toList();
    }

    public Iterable<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public Iterable<Epic> getAllEpics() {
          return epics.values();
    }

    public Iterable<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.setStatus(defineStatus(epic.getSubtaskIds()));
    }

    public void deleteAll() {
        tasks.clear();
    }
    public void deleteAllEpic() {
        subtasks.clear();
        epics.clear();
    }
    public void deleteAllSubtask() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
        });
    }

    public void deleteById(int id) {
        Task removedTask = tasks.remove(id);
    }

    public void deleteByIdEpic(int id) {
        Epic epic = epics.remove(id);
        epic.getSubtaskIds().forEach(subtasks::remove);
    }

    public void deleteByIdSubtask(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        Epic epic = removedSubtask.getEpic();
        epic.getSubtaskIds().remove(id);
        epic.setStatus(defineStatus(epic.getSubtaskIds()));
    }

    private TaskStatus defineStatus(Set<Integer> subtaskIds) {
        if (subtaskIds == null || subtaskIds.isEmpty()) {
            return TaskStatus.NEW;
        }

        List<TaskStatus> statuses = subtaskIds.stream()
                .map(id -> subtasks.get(id).getStatus())
                .distinct()
                .toList();

        return statuses.size() == 1 ? statuses.getFirst() : TaskStatus.IN_PROGRESS;
    }
}
