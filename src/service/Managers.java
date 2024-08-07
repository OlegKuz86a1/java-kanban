package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapter.InstantAdapterDuration;
import http.adapter.InstantAdapterLocalDataTime;
import http.adapter.InstantAdapterTaskStatus;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static   TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static  Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new InstantAdapterDuration())
                    .registerTypeAdapter(LocalDateTime.class, new InstantAdapterLocalDataTime())
                    .registerTypeAdapter(TaskStatus.class, new InstantAdapterTaskStatus());
        return gsonBuilder.create();
    }
}