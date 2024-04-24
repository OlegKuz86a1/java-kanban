package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId;


    public LinkedList<Task> getHistory() {
        return new LinkedList<>(Managers.getDefaultHistory().getHistory());
    }


    @Override
    public Task create(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
        epic.setStatus(defineStatus(epic.getSubtaskIds()));
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++generatorId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        Set<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.add(subtask.getId());
        epic.setStatus(defineStatus(subtaskIds));
        return subtask;
    }

    @Override
    public Task getById(int id) {
        Task task = tasks.get(id);
        Managers.getDefaultHistory().add(task);
        return task;

    }

    @Override
    public Epic getByIDEpic(int id) {
        Epic epic = epics.get(id);
        Managers.getDefaultHistory().add(epic);
        return epic;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> filteredSubtasks = subtasks.values().stream()
                .filter(subtask -> subtask.getEpic().getId() == epicId)
                .toList();
        filteredSubtasks.forEach(subtask -> Managers.getDefaultHistory().add(subtask));
        return filteredSubtasks;

    }


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    @Override
    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.setStatus(defineStatus(epic.getSubtaskIds()));
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
        });
    }

    @Override
    public void deleteById(int id) {
        Task removedTask = tasks.remove(id);
    }

    @Override
    public void deleteByIdEpic(int id) {
        Epic epic = epics.remove(id);
        epic.getSubtaskIds().forEach(subtasks::remove);
    }

    @Override
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

