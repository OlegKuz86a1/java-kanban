package service;

public class Managers {

    public  TaskManager getDefault() {

        return new FileBackedTaskManager(getDefaultHistory());
    }

    public HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}