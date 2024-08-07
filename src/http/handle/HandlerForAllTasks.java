package http.handle;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerIntersectionException;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.LinkedList;

public class HandlerForAllTasks extends BaseHttpHandler implements HttpHandler {

    public HandlerForAllTasks(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handelAssistant(exchange);
    }

    @Override
    public void get(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathMassive = path.split("/");

        if (path.contains("subtasks")) {
            if (pathMassive.length == 2) {
                response = gson.toJson(taskManager.getAllSubtasks());
                sendText(httpExchange, response, 200);

            } else {
                try {
                    int id = Integer.parseInt(pathMassive[2]);
                    Subtask subtask = taskManager.getSubtaskId(id);
                    if (subtask != null) {
                        response = gson.toJson(subtask);
                        sendText(httpExchange, response, 200);
                    } else {
                        sendNotFound(httpExchange, "the task with this ID was not found", 404);
                    }
                } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("the index was entered incorrectly");
                }
            }
        }

        if (path.contains("tasks")) {
            if (pathMassive.length == 2) {
                response = gson.toJson(taskManager.getAllTasks());
                sendText(httpExchange, response, 200);
            } else {
                try {
                    int id = Integer.parseInt(pathMassive[2]);
                    Task task = taskManager.getById(id);
                    if (task != null) {
                        response = gson.toJson(task);
                        sendText(httpExchange, response, 200);
                    } else {
                        sendNotFound(httpExchange, "the task with this ID was not found", 404);
                    }
                } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("the index was entered incorrectly");
                }
            }
        }

        if (path.contains("epics")) {
            if (pathMassive.length == 2) {
                response = gson.toJson(taskManager.getAllEpics());
                sendText(httpExchange, response, 200);
            } else {
                try {
                    int id = Integer.parseInt(pathMassive[2]);
                    Epic epic = taskManager.getByIDEpic(id);
                    if (epic != null) {
                        response = gson.toJson(epic);
                        sendText(httpExchange, response, 200);
                    } else {
                        sendNotFound(httpExchange, "the task with this ID was not found", 404);
                    }
                } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("the index was entered incorrectly");
                }
            }
        }

        if (path.contains("prioritized")) {
            response = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(httpExchange, response, 200);
        }

        if (path.contains("history")) {
            LinkedList<Task> history = taskManager.getHistory();
            response = gson.toJson(history);
            sendText(httpExchange, response, 200);
        }
    }

    @Override
    public void post(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String requestBody = readingRequest(httpExchange);

        if (requestBody.isEmpty()) {
            sendNotFound(httpExchange, "an empty request", 404);
            return;
        }

        if (path.contains("subtasks")) {
            try {
                Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                if (subtask != null) {
                    if (subtask.getId() != null) {
                        taskManager.updateSubtask(subtask);
                        sendText(httpExchange, "the task has been updated", 201);
                    } else {
                        Task isEmptyEpic = taskManager.createSubtask(subtask);
                        sendText(httpExchange, isEmptyEpic.toString(), 201);
                    }
                }
            } catch (ManagerIntersectionException v) {
                sendHasInteractions(httpExchange, "the intersection of tasks", 406);

            } catch (JsonSyntaxException e) {
                System.out.println("Incorrect JSON format");
            }
        }

        if (path.contains("tasks")) {
            try {
                Task task = gson.fromJson(requestBody, Task.class);
                if (task != null) {
                    if (task.getId() != null) {
                        taskManager.update(task);
                        sendText(httpExchange, "the task has been updated", 201);
                    } else {
                        Task newTask = taskManager.create(task);
                        sendText(httpExchange, newTask.toString(), 201);
                    }
                }
            } catch (ManagerIntersectionException v) {
                sendHasInteractions(httpExchange, "the intersection of tasks", 406);
            } catch (JsonSyntaxException e) {
                System.out.println("Incorrect JSON format");
            }
        }

            if (path.contains("epics")) {
                try {
                    Epic epic = gson.fromJson(requestBody, Epic.class);
                    if (epic != null) {
                        if (epic.getId() != null) {
                            taskManager.updateEpic(epic);
                            sendText(httpExchange, "the task has been updated", 201);
                        } else {
                            Task isEmptyEpic = taskManager.createEpic(epic);
                            sendText(httpExchange, isEmptyEpic.toString(), 201);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("Incorrect JSON format");
                }
            }
        }

    @Override
    public void delete(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathMassive = path.split("/");
        if (pathMassive.length == 2) {
            sendNotFound(httpExchange, "the task was not found", 404);
            return;
        }

        if (path.contains("subtasks")) {
            try {
                int id = Integer.parseInt(pathMassive[2]);
                taskManager.deleteByIdSubtask(id);
                sendText(httpExchange, "the subtask has been deleted", 204);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("the index was entered incorrectly");
            }
        }

        if (path.contains("tasks")) {
            try {
                int id = Integer.parseInt(pathMassive[2]);
                taskManager.deleteById(id);
                sendText(httpExchange, "the task has been deleted", 204);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("the index was entered incorrectly");
            }
        }

        if (path.contains("epics")) {
            try {
                int id = Integer.parseInt(pathMassive[2]);
                taskManager.deleteByIdEpic(id);
                sendText(httpExchange, "the epic has been deleted", 204);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("the index was entered incorrectly");
            }
        }
    }
}


