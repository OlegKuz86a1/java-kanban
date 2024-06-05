package converter;

import model.Task;

public final class TaskConverter {

    private TaskConverter() {
    }
    
    public static String taskInFiletoString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + "," + task.getEpicId();
    }
}
