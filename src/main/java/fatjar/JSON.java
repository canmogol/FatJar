package fatjar;

import fatjar.implementations.json.CurrentJSON;

import java.util.Optional;

public interface JSON {

    static JSON create() {
        return JSON.create(Type.GensonJSON);
    }

    static JSON create(Type type) {
        return CurrentJSON.create(type);
    }

    <T> Optional<T> fromJson(String json, Class<T> tClass);

    Optional<String> toJson(Object object);

    enum Type {
        GensonJSON, JacksonJSON, GsonJSON
    }

}
