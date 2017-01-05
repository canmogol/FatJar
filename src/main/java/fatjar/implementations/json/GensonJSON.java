package fatjar.implementations.json;

import com.owlike.genson.Genson;
import fatjar.JSON;
import fatjar.Log;

import java.util.Optional;

public class GensonJSON implements JSON {

    private final Genson genson;

    public GensonJSON() {
        genson = new Genson();
    }

    public <T> Optional<T> fromJson(String json, Class<T> tClass) {
        try {
            T t = genson.deserialize(json, tClass);
            return Optional.ofNullable(t);
        } catch (Exception e) {
            Log.error("got exception while creating object from json, exception: " + e, e);
            return Optional.empty();
        }
    }

    public Optional<String> toJson(Object object) {
        try {
            String content = genson.serialize(object);
            return Optional.ofNullable(content);
        } catch (Exception e) {
            Log.error("got exception while creating json string from object, exception: " + e, e);
            return Optional.empty();
        }
    }

}
