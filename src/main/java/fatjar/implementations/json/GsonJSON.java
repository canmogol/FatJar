package fatjar.implementations.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fatjar.JSON;

public class GsonJSON implements JSON {

    private final Gson gson;

    public GsonJSON() {
	GsonBuilder builder = new GsonBuilder();
	gson = builder.create();
    }

    public <T> T fromJson(String json, Class<T> tClass) {
	return gson.fromJson(json, tClass);
    }

    public String toJson(Object object) {
	return gson.toJson(object);
    }

}
