package fatjar;

import fatjar.implementations.json.CurrentJSON;

public interface JSON {

    static JSON create() {
        return JSON.create(Type.GensonJSON);
    }

    static JSON create(Type type) {
        return CurrentJSON.create(type);
    }

    <T> T fromJson(String json, Class<T> tClass);

    String toJson(Object object);

    enum Type {
        GensonJSON, JacksonJSON, GsonJSON
    }

}
