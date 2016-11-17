package fatjar.implementations.json;

import fatjar.JSON;

public class Genson implements JSON {

    public <T> T fromJson(String json, Class<T> tClass) {
        return new com.owlike.genson.Genson().deserialize(json, tClass);
    }

    public String toJson(Object object) {
        return new com.owlike.genson.Genson().serialize(object);
    }

}
