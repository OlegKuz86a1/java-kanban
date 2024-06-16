package service;

import converter.TaskConverter;
import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static model.TaskType.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected   Path path;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public void saveInFile() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            writer.write("ID, TYPE, NAME, STATUS, DESCRIPTION, EPIC, DURATION, START_TIME");
            writer.newLine();

            tasks.forEach((id, task) -> {
                writeToFile(writer, task);
            });

            epics.forEach((id, epic) -> {
                writeToFile(writer, epic);
            });

            subtasks.forEach((id, subtask) -> {
                writeToFile(writer, subtask);
            });

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), path);

        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(path)))) {
            String line;
            reader.readLine();
            int idMax = -1;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 8) {
                    throw new ManagerLoadException("Invalid line format: " + line);
                }
                int id = Integer.parseInt(fields[0]);
                idMax = Math.max(idMax, id);
                TaskType taskType = TaskType.valueOf(fields[1].toUpperCase());
                String name = fields[2];
                TaskStatus taskStatus = TaskStatus.valueOf(fields[3].toUpperCase());
                String description = fields[4];
                Epic epic = null;
                if (taskType == SUBTASK) {
                    epic = manager.epics.get(Integer.parseInt(fields[5]));
                }
                Duration duration = !fields[6].equals("null") ? Duration.parse(fields[6]) : null;
                LocalDateTime startTime = !fields[7].equals("null") ? LocalDateTime.parse(fields[7], FORMATTER) : null;
                Task taskFromFile = switch (taskType) {
                    case TASK -> {
                        Task task = new Task(id, name, description, taskStatus, duration, startTime);
                        manager.tasks.put(id, task);
                        yield task;
                    }
                    case EPIC -> {
                        Epic task = new Epic(id, name, description, taskStatus, duration, startTime);
                        manager.epics.put(id, task);
                        yield task;
                    }
                    case SUBTASK -> {
                        Subtask task = new Subtask(id, name, description, taskStatus, duration, startTime, epic);
                        manager.subtasks.put(id, task);
                        yield task;
                    }
                    default -> throw new ManagerLoadException("Unsupported task type: " + taskType);
                };
                if (!(taskFromFile instanceof Epic) && taskFromFile.getStartTime() != null) {
                    manager.sortedTasks.add(taskFromFile);
                }
            }
            manager.generatorId = idMax++;
        } catch (IOException e) {
            throw new ManagerLoadException("Failed to load tasks from file");
        }

        reloadEpics(manager);
        return manager;
    }

    @Override
    public Task create(Task task) {
        Task createdTask = super.create(task);
        saveInFile();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        saveInFile();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        saveInFile();
        return createdSubtask;
    }

    @Override
    public void update(Task task) {
        super.update(task);
        saveInFile();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        saveInFile();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        Epic epic = subtask.getEpic();
        fillEpic(epic);
        saveInFile();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        saveInFile();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();

        saveInFile();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        saveInFile();
    }

    @Override
    public void deleteById(int id) {
        super.deleteById(id);
        saveInFile();
    }

    @Override
    public void deleteByIdEpic(int id) {
        super.deleteByIdEpic(id);
        saveInFile();
    }

    @Override
    public void deleteByIdSubtask(int id) {
        super.deleteByIdSubtask(id);
        saveInFile();
    }

    private static void reloadEpics(FileBackedTaskManager taskManager) {
        taskManager.getAllEpics().forEach(epic -> {
            AtomicReference<LocalDateTime> endTime = new AtomicReference<>();

            taskManager.getAllSubtasks().stream()
                    .filter(subtask -> Objects.equals(subtask.getEpicId(), epic.getId()))
                    .forEach(subtask -> {
                        epic.getSubtaskIds().add(subtask.getId());
                        if (subtask.getEndTime() != null && (endTime.get() == null || endTime.get().isBefore(subtask.getEndTime()))) {
                            endTime.set(subtask.getEndTime());
                        }
                    });

            epic.setEndTime(endTime.get());
        });
    }

    private void writeToFile(BufferedWriter writer, Task task) {
        try {
            writer.write(TaskConverter.taskInFiletoString(task));
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }
}
