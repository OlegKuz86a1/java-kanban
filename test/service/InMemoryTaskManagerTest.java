package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager inMemoryTaskManager;
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    public void addNewTasks(){
       final Task task = inMemoryTaskManager.create
               (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));
       final Task taskForCheck = inMemoryTaskManager.getById(1);
       assertNotNull(taskForCheck, "the task was not found");
       assertEquals(task, taskForCheck, "the task are not equal");

       final ArrayList<Task> tasks = (ArrayList<Task>) inMemoryTaskManager.getAllTasks();
        assertNotNull(tasks, "the tasks are not returned");
        assertEquals(1, tasks.size(), "incorrect number of tasks");
        assertEquals(task, tasks.get(0),"the task are not equal" );
    }

    @Test
    public void addNewEpics(){
        final Epic epic = inMemoryTaskManager.createEpic
                (new Epic("Test addNewEpic", "Test addNewEpic description"));

        final Epic epicForCheck = inMemoryTaskManager.getByIDEpic(1);
        assertNotNull(epicForCheck, "the epic was not found");
        assertEquals(epic, epicForCheck, "the epic are not equal");

        final ArrayList<Epic> epics = (ArrayList<Epic>) inMemoryTaskManager.getAllEpics();
        assertNotNull(epics, "the epics are not returned");
        assertEquals(1, epics.size(), "incorrect number of epics");
        assertEquals(epic, epics.get(0),"the epics are not equal" );
    }

    @Test
    public void addNewSubtask() {
        final Epic epic = inMemoryTaskManager.createEpic
                (new Epic("Test addNewEpicForCheckSubtask", "Test addNewEpicForCheckSubtask description"));
        final Subtask subtask = inMemoryTaskManager.createSubtask
                (new Subtask("Test addNewTask", "Test addNewTask description", TaskStatus.NEW, epic));

        final List<Subtask> subtasks = (List<Subtask>) inMemoryTaskManager.getAllSubtasks();
        assertNotNull(subtasks, "the subtask are not returned");
        assertEquals(1, subtasks.size(), "incorrect number of subtask");
        assertEquals(subtask, subtasks.get(0),"the subtask are not equal" );
    }

    @Test
    public void deleteAllTask() {
        final Task task = inMemoryTaskManager.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));
        inMemoryTaskManager.deleteAll();
        final Task taskForCheck = inMemoryTaskManager.getById(1);
        assertNull(taskForCheck);
    }

    @Test
    public void deleteAllEpics(){
        final Epic epic = inMemoryTaskManager.createEpic
                (new Epic("Test addNewEpic", "Test addNewEpic description"));
        inMemoryTaskManager.deleteAllEpic();
        final Epic epicForCheck = inMemoryTaskManager.getByIDEpic(1);
        assertNull(epicForCheck);
    }

    @Test
    public void deleteAllSubtask() {
        final Epic epic = inMemoryTaskManager.createEpic
                (new Epic("Test addNewEpicForCheckSubtask", "Test addNewEpicForCheckSubtask description"));
        final Subtask subtask = inMemoryTaskManager.createSubtask
                (new Subtask("Test addNewTask", "Test addNewTask description", TaskStatus.NEW, epic));
        inMemoryTaskManager.deleteAllSubtask();
        final ArrayList<Subtask> subtasks = (ArrayList<Subtask>) inMemoryTaskManager.getAllSubtasks();
        assertEquals(0, subtasks.size());
    }

    @Test
    public void UpdateAndCompareReplacementTaskWithBeingReplaced() {
        final Task task = inMemoryTaskManager.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));
        final Task taskForUpdates = new Task
                ("Test addUpdateTask", "Test addUpdateTask description", TaskStatus.NEW);

        taskForUpdates.setId(1);
        inMemoryTaskManager.update(taskForUpdates);
        assertEquals(taskForUpdates, inMemoryTaskManager.getById(1));
    }

    @Test
    public void UpdateAndCompareReplacementEpicWithBeingReplaced() {
        final Epic epic = inMemoryTaskManager.createEpic
                (new Epic("Test addNewEpic", "Test addNewEpic description"));
        final Epic epicForUpdates = new Epic("Test addUpdateEpic", "Test addUpdateEpic description");

        epicForUpdates.setId(1);
        inMemoryTaskManager.update(epicForUpdates);
        assertEquals(epicForUpdates, inMemoryTaskManager.getByIDEpic(1));
    }

    @Test
    public void UpdateAndCompareReplacementSubtaskWithBeingReplaced() {
        final Epic epic = inMemoryTaskManager.createEpic
                (new Epic("Test addNewEpic", "Test addNewEpic description"));
        final Subtask subtask = inMemoryTaskManager.createSubtask
                (new Subtask("Test addNewSubtask", "Test addNewSubtask description",TaskStatus.NEW, epic));
        final Subtask subtaskForUpdates = new Subtask
                ("Test addUpdateSubtask", "Test addUpdateSubtask description", TaskStatus.IN_PROGRESS, epic);

        subtaskForUpdates.setId(1);
        inMemoryTaskManager.update(subtaskForUpdates);
        assertNotEquals(subtaskForUpdates, subtask);
    }
}