package fatjar.implementations.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import fatjar.JSON;
import fatjar.Log;

import java.io.StringWriter;
import java.util.Optional;

public class JacksonJSON implements JSON {

    private final ObjectMapper mapper;

    public JacksonJSON() {
        mapper = new ObjectMapper();
    }

    public <T> Optional<T> fromJson(String json, Class<T> tClass) {
        try {
            T t = mapper.readValue(json, tClass);
            return Optional.ofNullable(t);
        } catch (Exception e) {
            Log.error("got exception while creating object from json, exception: " + e, e);
            return Optional.empty();
        }
    }

    public Optional<String> toJson(Object object) {
        try {
            StringWriter stringWriter = new StringWriter();
            mapper.writeValue(stringWriter, object);
            return Optional.ofNullable(stringWriter.toString());
        } catch (Exception e) {
            Log.error("got exception while creating json string from object, exception: " + e, e);
            return Optional.empty();
        }
    }

}
