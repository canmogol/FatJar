package fatjar.implementations.genson;

import com.owlike.genson.Genson;
import fatjar.JSON;

public class GensonJSON implements JSON {

    public <T> T fromJson(String json, Class<T> tClass) {
        return new Genson().deserialize(json, tClass);
    }

    public String toJson(Object object) {
        return new Genson().serialize(object);
    }

}
