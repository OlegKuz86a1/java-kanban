package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

class EpicTest {

    TaskManager inMemoryTaskManager;
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        inMemoryTaskManager = new InMemoryTaskManager((InMemoryHistoryManager) historyManager);
    }

    @Test
    void getStatus() {
        final Epic epic = inMemoryTaskManager.createEpic
                (new Epic("Test addNewEpic", "Test addNewEpic description"));
        final Subtask subtask = inMemoryTaskManager.createSubtask
                (new Subtask("Test addNewSubtask", "Test addNewSubtask description",TaskStatus.NEW, epic));

        assertEquals(TaskStatus.NEW, epic.getStatus(), "statuses are not equal");
    }
}