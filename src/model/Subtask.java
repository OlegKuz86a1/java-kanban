package model;

public class Subtask extends Task {

    private final Epic epic;

    public Subtask(String name, String description, TaskStatus status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Subtask(String name, String description, int id, TaskStatus status, Epic epic) {
        super(name, description, id, status);
        this.epic = epic;
    }
    @Override
    public Integer getEpicId() {
        return epic.getId();
    }

    public Epic getEpic() {
        return epic;
    }
    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "} " + super.toString();
    }


}
