package fatjar.implementations.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fatjar.JSON;
import fatjar.Log;

import java.util.Optional;

public class GsonJSON implements JSON {

    private final Gson gson;

    public GsonJSON() {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    public <T> Optional<T> fromJson(String json, Class<T> tClass) {
        try {
            T t = gson.fromJson(json, tClass);
            return Optional.ofNullable(t);
        } catch (Exception e) {
            Log.error("got exception while creating object from json, exception: " + e, e);
            return Optional.empty();
        }
    }

    public Optional<String> toJson(Object object) {
        try {
            String content = gson.toJson(object);
            return Optional.ofNullable(content);
        } catch (Exception e) {
            Log.error("got exception while creating json string from object, exception: " + e, e);
            return Optional.empty();
        }
    }

}
