package service;

public class Managers {
    private static final HistoryManager DEFAULT_HISTORY_MANAGER = new InMemoryHistoryManager();

    public static HistoryManager getDefaultHistory() {
        return DEFAULT_HISTORY_MANAGER;
    }
}