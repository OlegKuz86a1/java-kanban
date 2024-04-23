package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager inMemoryTaskManager;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    void addHistory() {
        final Task task = inMemoryTaskManager.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));

        Managers.getDefaultHistory().add(task);
        final List<Task> history = Managers.getDefaultHistory().getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}