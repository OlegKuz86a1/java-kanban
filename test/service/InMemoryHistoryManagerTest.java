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
    Managers managers;

    @BeforeEach
    void beforeEach() {
        managers = new Managers();
        historyManager = managers.getDefaultHistory();
        inMemoryTaskManager = managers.getDefault();
    }

    @Test
    void addHistory() {
        final Task task = inMemoryTaskManager.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }
}