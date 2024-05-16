import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.*;

public class Main {

    public static void main(String[] args) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new InMemoryTaskManager(historyManager);


        Task task1 = taskManager.create(new Task(" № 1", "Описание задачи 1", TaskStatus.NEW));
        Task task2 = taskManager.create(new Task("Задача № 2", "Описание задачи 2", TaskStatus.IN_PROGRESS));
        Task task3 = taskManager.create(new Task("Задача № 3", "Описание задачи 3", TaskStatus.IN_PROGRESS));
        Task task4 = taskManager.create(new Task("Задача № 4", "Описание задачи 4", TaskStatus.NEW));
        Task task5 = taskManager.create(new Task("Задача № 5", "Описание задачи 5", TaskStatus.IN_PROGRESS));
        Task task6 = taskManager.create(new Task("Задача № 6", "Описание задачи 6", TaskStatus.NEW));

        Epic epic1 = taskManager.createEpic(new Epic("Новый эпик", "описание"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask
                                       ("Новыя подзадача", "описание подзадачи", TaskStatus.NEW, epic1));
        Subtask subtask2 = taskManager.createSubtask
                (new Subtask("Вторая подзадача", "описание второй подзадачи", TaskStatus.IN_PROGRESS,
                        epic1));
        Epic epic2 = taskManager.createEpic(new Epic("Второй эпик", "описание вторового эпика"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Третья подзадача",
                "описание третьей подзадачи", TaskStatus.IN_PROGRESS, epic2));

        taskManager.getById(1); //1
        taskManager.getById(2); //2
        taskManager.getById(3); //3
        taskManager.getById(5); //5
        taskManager.getSubtasksByEpicId(10); //7
       taskManager.getByIDEpic(10); //8
        taskManager.getSubtasksByEpicId(10); //9
        taskManager.getById(5); //10
        taskManager.getById(1);
        taskManager.getByIDEpic(10);

        System.out.println(taskManager.getHistory()); //проверяем список просмотров



    }
}
