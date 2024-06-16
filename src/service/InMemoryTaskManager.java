package service;

import exception.InvalidTaskException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected  int generatorId;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        }

    public LinkedList<Task> getHistory() {
        return new LinkedList<>(historyManager.getAll());
    }

    TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));



    @Override
    public Task create(Task task) {
        validateAndAddToSorted(task, null);
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        validateAndAddToSorted(subtask, null);
        subtask.setId(++generatorId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        Set<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.add(subtask.getId());
        fillEpic(epic);
        return subtask;
    }

    @Override
    public Task getById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;

    }


    @Override
    public Epic getByIDEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> filteredSubtasks = subtasks.values().stream()
                .filter(subtask -> subtask.getEpic().getId() == epicId)
                .toList();

        filteredSubtasks.forEach(historyManager::add);
        return filteredSubtasks;
    }


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    @Override
    public void update(Task task) {
        validateAndAddToSorted(task, tasks.get(task.getId()));
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        validateAndAddToSorted(subtask, subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        fillEpic(epic);
    }

    @Override
    public void deleteAll() {
        sortedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        deleteAllSubtask();
        epics.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.values().stream().map(s -> (Task) s).toList().forEach(sortedTasks::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
        });
    }

    @Override
    public void deleteById(int id) {
        sortedTasks.remove(tasks.remove(id));
    }

    @Override
    public void deleteByIdEpic(int id) {
        Epic epic = epics.remove(id);
        epic.getSubtaskIds().forEach(subtaskId -> {
            sortedTasks.remove((Task) subtasks.remove(subtaskId));
        });
    }

    @Override
    public void deleteByIdSubtask(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        sortedTasks.remove(removedSubtask);
        Epic epic = removedSubtask.getEpic();
        epic.getSubtaskIds().remove(id);
        fillEpic(epic);
    }

    public void fillEpic(Epic epic) {
        Set<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds == null || subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        
        AtomicReference<LocalDateTime> startTime = new AtomicReference<>();
        AtomicReference<LocalDateTime> endTime = new AtomicReference<>();
        AtomicReference<Duration> totalDuration = new AtomicReference<>();
        BinaryOperator<Duration> adder = Duration::plus;

        List<TaskStatus> statuses = subtaskIds.stream()
                .map(id -> {
                    Subtask subtask = subtasks.get(id);
                    if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                        if (startTime.get() == null || startTime.get().isAfter(subtask.getStartTime())) {
                            startTime.set(subtask.getStartTime());
                        }
                        if (endTime.get() == null || endTime.get().isBefore(subtask.getEndTime())) {
                            endTime.set(subtask.getEndTime());
                        }
                        if (totalDuration.get() != null) {
                            totalDuration.getAndAccumulate(subtask.getDuration(), adder);
                        } else {
                            totalDuration.set(subtask.getDuration());
                        }
                    }
                    return subtask.getStatus();
                })
                .distinct()
                .toList();

        epic.setStatus(statuses.size() == 1 ? statuses.getFirst() : TaskStatus.IN_PROGRESS);
        epic.setDuration(totalDuration.get());
        epic.setStartTime(startTime.get());
        epic.setEndTime(endTime.get());
    }

    public void validateAndAddToSorted(Task addedTask, Task oldTask) {
        if (addedTask.getStartTime() == null) {
            return;
        }
        System.out.println(sortedTasks);
        if (sortedTasks.stream().anyMatch(addedTask::doesIntersect)) {
            throw new InvalidTaskException("The task intersects with other tasks");
        }
        if (oldTask != null) {
            sortedTasks.remove(oldTask);
        }
        sortedTasks.add(addedTask);
    }
}

