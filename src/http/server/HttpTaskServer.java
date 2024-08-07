package http.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handle.HandlerForAllTasks;
import service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class  HttpTaskServer {
    public static final int PORT = 8080;
    private TaskManager manager;
    protected HttpServer server;
    private Gson gson;

    public HttpTaskServer() {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        this.gson = Managers.getGson();
        try {
           this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        HandlerForAllTasks handlerForAllTasks = new HandlerForAllTasks(manager);
        server.createContext("/task", handlerForAllTasks);
        server.createContext("/epics", handlerForAllTasks);
        server.createContext("/subtasks", handlerForAllTasks);
        server.createContext("/prioritized", handlerForAllTasks);
        server.createContext("/history", handlerForAllTasks);
    }

    public void start() {
        System.out.println("Started TaskServer\n http://localhost: " + PORT);
         server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped TaskServer on port: " + PORT);
    }

    public static void main(String[] args) {
        TaskManager manager = FileBackedTaskManager.loadFromFile(Path.of("resources/task.csv"));
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
        httpTaskServer.stop();

    }
}
