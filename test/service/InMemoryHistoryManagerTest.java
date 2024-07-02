package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager inMemoryTaskManager;
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    void addHistory() {
        final Task task = inMemoryTaskManager.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));
        final Task task1 = inMemoryTaskManager.create
                (new Task("Test addNewTask1", "Test addNewTask1 description", TaskStatus.NEW));
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task);
        final List<Task> history = historyManager.getAll();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История пустая.");
    }
}