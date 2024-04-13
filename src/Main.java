import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.create(new Task("Задача № 1", "Описание задачи 1", TaskStatus.NEW));
        Task task2 = taskManager.create(new Task("Задача № 2", "Описание задачи 2", TaskStatus.IN_PROGRESS));

        Epic epic1 = taskManager.createEpic(new Epic("Новый эпик", "описание"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask
                                       ("Новыя подзадача", "описание подзадачи", TaskStatus.NEW, epic1));
        Subtask subtask2 = taskManager.createSubtask
                (new Subtask("Вторая подзадача", "описание второй подзадачи", TaskStatus.IN_PROGRESS,
                        epic1));

        Epic epic2 = taskManager.createEpic(new Epic("Второй эпик", "описание вторового эпика"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Третья подзадача",
                "описание третьей подзадачи", TaskStatus.IN_PROGRESS, epic2));

        System.out.println(("All task: " + taskManager.getAllTasks()));
        System.out.println("All epic: " + taskManager.getAllEpics());
        System.out.println("All subtask: " + taskManager.getAllSubtasks());



    }
}
