package fatjar.implementations.httpclient;

import fatjar.HttpClient;
import fatjar.Log;

public class CurrentHttpClient {


    private CurrentHttpClient() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static HttpClient create(HttpClient.Type type) {
        HttpClient httpClient = null;
        String packageName = CurrentHttpClient.class.getPackage().getName();
        try {
            Class<? extends HttpClient> httpClientClass = Class.forName(packageName + "." + type.name()).asSubclass(HttpClient.class);
            httpClient = httpClientClass.newInstance();
        } catch (Exception e) {
            Log.error("could not create httpClient of type: " + type + " error: " + e, e);
        }
        return httpClient;
    }

}
