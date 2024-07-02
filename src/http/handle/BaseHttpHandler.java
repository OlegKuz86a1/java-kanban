package http.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected String response;
    protected Gson gson = Managers.getGson();
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange httpExchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(statusCode, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    public void sendNotFound(HttpExchange httpExchange, String text, int statusCode) throws IOException {
        sendText(httpExchange, text, statusCode);
    }

    public void sendHasInteractions(HttpExchange httpExchange, String text, int statusCode) throws IOException {
        sendText(httpExchange, text, statusCode);
    }

    protected String readingRequest(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    public abstract void get(HttpExchange httpExchange) throws IOException;

    public abstract void post(HttpExchange httpExchange) throws IOException;

    public abstract void delete(HttpExchange httpExchange) throws IOException;

    public void handelAssistant(HttpExchange exchange) throws IOException {
        try (exchange) {
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    get(exchange);
                    return;
                }
                case "POST": {
                    post(exchange);
                    return;
                }
                case "DELETE": {
                    delete(exchange);
                    return;
                }
                default: {
                    System.out.println("Ждем GET, DELETE или POST запрос, а получили - " + requestMethod);
                    exchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}

