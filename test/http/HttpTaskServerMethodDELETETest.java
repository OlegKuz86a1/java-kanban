package http;

import http.server.HttpTaskServer;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerMethodDELETETest {
    TaskManager manager;
    HttpTaskServer taskServer;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskServer = new HttpTaskServer(manager);
        manager.deleteAll();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        manager.create( new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now()));
        manager.create(new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(15)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response1.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
    }
}
