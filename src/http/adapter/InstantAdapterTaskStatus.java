package http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.TaskStatus;

import java.io.IOException;

public class InstantAdapterTaskStatus extends TypeAdapter<TaskStatus> {

    @Override
    public void write(JsonWriter jsonWriter, TaskStatus status) throws IOException {
        jsonWriter.value(status.toString());
    }

    @Override
    public TaskStatus read(JsonReader jsonReader) throws IOException {
        final String text = jsonReader.nextString();
        if (text.equals("null")) {
            return null;
        }
        return TaskStatus.valueOf(text);
    }
}
