package model;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {

    private final Set<Integer> subtaskIds = new HashSet<>();

    public Epic (String name, String description) {
        super(name, description);
    }

    @Override
    public TaskStatus getStatus() {
        return super.getStatus();
    }

    public Set<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                "} " + super.toString();
    }
}

