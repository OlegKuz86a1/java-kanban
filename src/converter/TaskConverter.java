package converter;

import model.Task;

public class TaskConverter {

    public String taskInFiletoString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + "," + task.getEpicId();
    }
}
