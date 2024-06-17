package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final Epic epic;

    public Subtask(Integer id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime,
                   Epic epic) {
        super(id, name, description, status, duration, startTime);
        this.epic = epic;
    }

    public Subtask(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime,
                   Epic epic) {
        super(name, description, status, duration, startTime);
        this.epic = epic;
    }

    public Subtask(String name, String description, TaskStatus status, Epic epic) {
        super(name, description, status);
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
        return "Subtask{" + "epicId=" + epic.getId() +
                "} " + super.toString();
    }
}
