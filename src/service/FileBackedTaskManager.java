package service;

import converter.TaskConverter;
import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static model.TaskType.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private  Path path;

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }



    public void saveInFile() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            writer.write("ID, TYPE, NAME, STATUS, DESCRIPTION, EPIC");
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.write(TaskConverter.taskInFiletoString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.write(TaskConverter.taskInFiletoString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.write(TaskConverter.taskInFiletoString(entry.getValue()));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), path);

        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(path)))) {
            String line;
            reader.readLine();
            int idMax = -1;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
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
                switch (taskType) {
                    case TASK:
                        manager.tasks.put(id, new Task(name, description, id, taskStatus));
                        break;
                    case EPIC:
                        manager.epics.put(id, new Epic(name, description, id, taskStatus));
                        break;
                    case SUBTASK:
                        manager.subtasks.put(id, new Subtask(name, description, id, taskStatus, epic));
                        break;
                    default:
                        throw new ManagerLoadException("Unsupported task type: " + taskType);
                }
            }
            manager.generatorId = idMax++;

        } catch (IOException e) {
            throw new ManagerLoadException("Failed to load tasks from file");
        }
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
        epic.setStatus(defineStatus(epic.getSubtaskIds()));
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
}
