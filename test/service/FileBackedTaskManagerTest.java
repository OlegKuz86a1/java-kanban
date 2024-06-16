package service;

import exception.InvalidTaskException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManagerBefore;
    private Path tempFile;

    @BeforeEach
    void beforeEach() throws IOException {
        tempFile = Path.of(File.createTempFile("test", ".csv").getAbsolutePath());
        InMemoryHistoryManager historyManagerBefore = new InMemoryHistoryManager();
        taskManagerBefore = new FileBackedTaskManager(historyManagerBefore, tempFile);
    }

    @Test
    public void whenSaveEmptyListThenLoadEmptyList() {
        taskManagerBefore.saveInFile();
        taskManagerBefore.deleteAll();

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(Files.exists(tempFile));
        assertTrue(taskManagerAfter.getAllTasks().isEmpty());
        assertTrue(taskManagerAfter.getAllEpics().isEmpty());
        assertTrue(taskManagerAfter.getAllSubtasks().isEmpty());
    }

    @Test
    public void whenSaveTwoTasksThenLoadTheSameTwoTasks() {
        final Task saved = taskManagerBefore.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW));
        final Task saved1 = taskManagerBefore.create
                (new Task("Test addNewTask1", "Test addNewTask1 description", TaskStatus.NEW));

        assertFalse(taskManagerBefore.getAllTasks().isEmpty());

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

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

    @Test
    public void whenSaveTaskWithoutStartTimeAndDurationThenLoadTheSameTask() {
        final Task saved = taskManagerBefore.create
                (new Task("Test task", "Test task description", TaskStatus.NEW));

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, taskManagerAfter.getAllTasks().size());
        assertEquals(saved.getTaskType(), taskManagerAfter.getAllTasks().getFirst().getTaskType());
        assertEquals(saved.getDescription(), taskManagerAfter.getAllTasks().getFirst().getDescription());
        assertEquals(saved.getName(), taskManagerAfter.getAllTasks().getFirst().getName());
        assertEquals(saved.getId(), taskManagerAfter.getAllTasks().getFirst().getId());

        assertNull(taskManagerAfter.getAllTasks().getFirst().getStartTime());
        assertNull(taskManagerAfter.getAllTasks().getFirst().getEndTime());
        assertNull(taskManagerAfter.getAllTasks().getFirst().getDuration());
    }

    @Test
    public void whenSaveTaskWithStartTimeAndDurationThenLoadTheSameTask() {
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(17L);
        final LocalDateTime expectedEndTime = startTime.plus(duration);

        final Task task = taskManagerBefore.create
                (new Task("Test task", "Test task description", TaskStatus.NEW, duration, startTime));
        Task saved = taskManagerBefore.create(task);

        assertEquals(saved.getEndTime(), expectedEndTime);

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, taskManagerAfter.getAllTasks().size());
        assertEquals(saved.getTaskType(), taskManagerAfter.getAllTasks().getFirst().getTaskType());
        assertEquals(saved.getDescription(), taskManagerAfter.getAllTasks().getFirst().getDescription());
        assertEquals(saved.getName(), taskManagerAfter.getAllTasks().getFirst().getName());
        assertEquals(saved.getId(), taskManagerAfter.getAllTasks().getFirst().getId());

        assertEquals(startTime, taskManagerAfter.getAllTasks().getFirst().getStartTime());
        assertEquals(expectedEndTime, taskManagerAfter.getAllTasks().getFirst().getEndTime());
        assertEquals(duration, taskManagerAfter.getAllTasks().getFirst().getDuration());
    }

    @Test
    public void whenSaveEpicWithStartTimeAndDurationThenLoadTheSameEpic() {
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(10L);
        final Epic savedEpic = taskManagerBefore.createEpic(new Epic("EpicName", "EpicDescription"));
        final Subtask sub1 = taskManagerBefore.createSubtask(new Subtask("Sub1Name", "Sub1Description",
                TaskStatus.NEW, duration, startTime, savedEpic));
        final Subtask sub2 = taskManagerBefore.createSubtask(new Subtask("Sub2Name", "Sub2Description",
                TaskStatus.DONE, duration, startTime.plusMinutes(15), savedEpic));
        final Subtask sub3 = taskManagerBefore.createSubtask(new Subtask("Sub3Name", "Sub3Description",
                TaskStatus.IN_PROGRESS, duration, startTime.plusMinutes(30), savedEpic));

        assertEquals(sub1.getStartTime(), savedEpic.getStartTime());
        assertEquals(Duration.ofMinutes(30), savedEpic.getDuration());
        assertEquals(sub3.getEndTime(), savedEpic.getEndTime());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus());

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

        Epic reloadEpic = taskManagerAfter.getByIDEpic(savedEpic.getId());

        assertEquals(savedEpic.getStartTime(), reloadEpic.getStartTime());
        assertEquals(savedEpic.getDuration(), reloadEpic.getDuration());
        assertEquals(savedEpic.getEndTime(), reloadEpic.getEndTime());
        assertEquals(new HashSet<>(List.of(sub1.getId(), sub2.getId(), sub3.getId())), reloadEpic.getSubtaskIds());
    }

    @Test
    public void whenGetPrioritizedTasksThenLoadSortedByStartTime() {
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(10L);
        final Epic epic = taskManagerBefore.createEpic(new Epic("EpicName", "EpicDescription"));
        final Subtask sub1 = taskManagerBefore.createSubtask(new Subtask("Sub1Name", "Sub1Description",
                TaskStatus.NEW, epic));
        final Subtask sub2 = taskManagerBefore.createSubtask(new Subtask("Sub2Name", "Sub2Description",
                TaskStatus.DONE, duration, startTime.plusMinutes(45), epic));
        final Subtask sub3 = taskManagerBefore.createSubtask(new Subtask("Sub3Name", "Sub3Description",
                TaskStatus.IN_PROGRESS, duration, startTime.plusMinutes(30), epic));
        final Task task1 = taskManagerBefore.create
                (new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW, duration,
                        startTime.plusMinutes(15)));
        final Task task2 = taskManagerBefore.create
                (new Task("Test addNewTask1", "Test addNewTask1 description", TaskStatus.NEW, duration,
                        startTime));

        List<Integer> expectedSortedTaskIds = new ArrayList<>(List.of(task2.getId(), task1.getId(), sub3.getId(), sub2.getId()));

        assertEquals(expectedSortedTaskIds.size(), taskManagerBefore.getPrioritizedTasks().size());

        FileBackedTaskManager taskManagerAfter = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> prioritizedTasksAfter = taskManagerAfter.getPrioritizedTasks();
        final int totalCount = taskManagerAfter.getAllTasks().size() + taskManagerAfter.getAllEpics().size() +
                taskManagerAfter.getAllSubtasks().size();

        assertEquals(expectedSortedTaskIds.size(), prioritizedTasksAfter.size());
        assertEquals(6, totalCount);
        assertFalse(prioritizedTasksAfter.contains(sub1));
        assertTrue(taskManagerAfter.getAllSubtasks().contains(sub1));

        IntStream.range(0, prioritizedTasksAfter.size())
                .forEach(i -> {
                    assertEquals(expectedSortedTaskIds.get(i), prioritizedTasksAfter.get(i).getId());
                });

        assertEquals(startTime, prioritizedTasksAfter.getFirst().getStartTime());
        assertEquals(startTime.plusMinutes(45), prioritizedTasksAfter.getLast().getStartTime());
    }

    @ParameterizedTest
    @ValueSource(ints = {10,29,30})
    public void whenCreateTaskEndItIntersectWithAnotherTaskThenThrowInvalidTaskException(int minutes) {
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(10L);
        final Epic epic = taskManagerBefore.createEpic(new Epic("EpicName", "EpicDescription"));
        taskManagerBefore.createSubtask(new Subtask("Sub1Name", "Sub1Description",
                TaskStatus.NEW, duration, startTime, epic));
        taskManagerBefore.createSubtask(new Subtask("Sub2Name", "Sub2Description",
                TaskStatus.DONE, duration, startTime.plusMinutes(20L), epic));

        InvalidTaskException exception = assertThrows(InvalidTaskException.class, () -> {
            taskManagerBefore.createSubtask(new Subtask("Sub3Name", "Sub3Description",
                    TaskStatus.IN_PROGRESS, duration, startTime.plusMinutes(minutes), epic));
        });

        assertEquals("The task intersects with other tasks", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {11,33})
    public void whenCreateTaskEndItDoesNotIntersectWithAnotherTaskThenSuccessfullySaved(int minutes) {
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(10L);
        final Epic epic = taskManagerBefore.createEpic(new Epic("EpicName", "EpicDescription"));
        taskManagerBefore.createSubtask(new Subtask("Sub1Name", "Sub1Description",
                TaskStatus.NEW, duration, startTime, epic));
        taskManagerBefore.createSubtask(new Subtask("Sub2Name", "Sub2Description",
                TaskStatus.DONE, duration, startTime.plusMinutes(22L), epic));
        taskManagerBefore.createSubtask(new Subtask("Sub3Name", "Sub3Description",
                TaskStatus.IN_PROGRESS, duration, startTime.plusMinutes(minutes), epic));

        assertEquals(3, taskManagerBefore.getAllSubtasks().size());
    }
}
