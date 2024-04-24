package service;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();

    }

    private static final HistoryManager DEFAULT_HISTORY_MANAGER = new InMemoryHistoryManager();

    public static HistoryManager getDefaultHistory() {
        return DEFAULT_HISTORY_MANAGER;
    }
}