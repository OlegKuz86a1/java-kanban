import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new FileBackedTaskManager(historyManager,
                Path.of("resources/task.csv"));

        Task task1 = taskManager.create(new Task("Задача № 1", "Описание задачи 1",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now()));
        Task task2 = taskManager.create(new Task("Задача № 2", "Описание задачи 2",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(15)));
        Task task3 = taskManager.create(new Task("Задача № 3", "Описание задачи 3",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(30)));
        Task task4 = taskManager.create(new Task("Задача № 4", "Описание задачи 4",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(45)));
        Task task5 = taskManager.create(new Task("Задача № 5", "Описание задачи 5",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(60)));
        Task task6 = taskManager.create(new Task("Задача № 6", "Описание задачи 6",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(75)));



        Epic epic1 = taskManager.createEpic(new Epic("Первый эпик", "описание"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Первая подзадача первого эпика",
                "описание подзадачи", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.now().plusMinutes(90), epic1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Вторая подзадача первого эпика",
                "описание второй подзадачи", TaskStatus.IN_PROGRESS, Duration.ofMinutes(15),
                LocalDateTime.now().plusMinutes(105), epic1));
        Epic epic2 = taskManager.createEpic(new Epic("Второй эпик", "описание вторового эпика"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Первая подзадача второго эпика",
                "описание третьей подзадачи", TaskStatus.IN_PROGRESS,Duration.ofMinutes(15),
                LocalDateTime.now().plusMinutes(120), epic2));

//        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager
//                .loadFromFile(Path.of("/home/oem/IdeaProjects/java-kanban/resources/task.csv"));
//
//        System.out.println(fileBackedTaskManager.getPrioritizedTasks());




    }
}
