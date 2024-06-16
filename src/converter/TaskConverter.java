package converter;

import model.Subtask;
import model.Task;

public final class TaskConverter {

    private TaskConverter() {
    }

    public static String taskInFiletoString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + "," +
                (task instanceof Subtask ? ((Subtask) task).getEpicId() : null) + ","
                + task.getDuration() + "," + task.getStartTime();
    }
}
