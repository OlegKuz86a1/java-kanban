package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    TaskManager inMemoryTaskManager;
    Managers managers;
    Path tempFile;
    @BeforeEach
    void beforeEach() {
        managers = new Managers();
        inMemoryTaskManager = managers.getDefault();
    }

    @Test
    public void whenSaveEmptyListThenLoadEmptyList() throws IOException {
        Path tempFile = Path.of(File.createTempFile("test", ".csv").getAbsolutePath());
        InMemoryHistoryManager historyManagerBefore = new InMemoryHistoryManager();
        FileBackedTaskManager taskManagerBefore = new FileBackedTaskManager(historyManagerBefore, tempFile);
        taskManagerBefore.saveInFile();
        taskManagerBefore.deleteAll();

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(Files.exists(tempFile));
        assertTrue(taskManagerAfter.getAllTasks().isEmpty());
        assertTrue(taskManagerAfter.getAllEpics().isEmpty());
        assertTrue(taskManagerAfter.getAllSubtasks().isEmpty());
    }

    @Test
    public void whenSaveTwoTasksThenLoadTheSameTwoTasks() throws IOException {
        final Task task = inMemoryTaskManager.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));
        final Task task1 = inMemoryTaskManager.create
                (new Task("Test addNewTask1", "Test addNewTask1 description", TaskStatus.NEW));
        Path tempFile = Path.of(File.createTempFile("test", ".csv").getAbsolutePath());
        InMemoryHistoryManager historyManagerBefore = new InMemoryHistoryManager();
        FileBackedTaskManager taskManagerBefore = new FileBackedTaskManager(historyManagerBefore, tempFile);
        Task saved = taskManagerBefore.create(task);
        Task saved1 = taskManagerBefore.create(task1);
        assertTrue(Files.exists(tempFile));
        assertFalse(taskManagerBefore.getAllTasks().isEmpty());

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(Files.exists(tempFile));
        assertFalse(taskManagerAfter.getAllTasks().isEmpty());
        assertEquals(2, taskManagerAfter.getAllTasks().size());
        assertEquals(saved.getTaskType(), taskManagerAfter.getAllTasks().getFirst().getTaskType());
        assertEquals(saved.getDescription(), taskManagerAfter.getAllTasks().getFirst().getDescription());
        assertEquals(saved.getName(), taskManagerAfter.getAllTasks().getFirst().getName());
        assertEquals(saved.getId(), taskManagerAfter.getAllTasks().getFirst().getId());

        assertEquals(saved1.getTaskType(), taskManagerAfter.getAllTasks().get(1).getTaskType());
        assertEquals(saved1.getDescription(), taskManagerAfter.getAllTasks().get(1).getDescription());
        assertEquals(saved1.getName(), taskManagerAfter.getAllTasks().get(1).getName());
        assertEquals(saved1.getId(), taskManagerAfter.getAllTasks().get(1).getId());
    }
}
