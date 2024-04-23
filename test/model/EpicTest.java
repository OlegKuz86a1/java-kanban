package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;


class EpicTest {
    TaskManager inMemoryTaskManager;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
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