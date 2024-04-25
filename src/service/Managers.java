package service;

public class Managers {

    public  TaskManager getDefault() {

        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    public HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}