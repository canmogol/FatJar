package fatjar;

import fatjar.implementations.genson.GensonJSON;

public interface JSON {

    static <T> T fromJson(String json, Class<T> tClass) {
        return new GensonJSON().fromJson(json, tClass);
    }

    static String toJson(Object object) {
        return new GensonJSON().toJson(object);
    }

}
