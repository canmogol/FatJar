package fatjar;


import fatjar.dto.HttpMethod;
import fatjar.implementations.httpclient.CurrentHttpClient;

import java.net.HttpCookie;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface HttpClient {

    static HttpClient create() {
        return HttpClient.create(Type.JettyHttpClient);
    }

    static HttpClient create(Type type) {
        return CurrentHttpClient.create(type);
    }

    HttpClient url(String url);

    HttpClient method(HttpMethod method);

    HttpClient send() throws HttpClient.HttpClientException;

    byte[] getContent();

    String getContentAsString();

    HttpClient agent(String browserAgent);

    HttpClient param(String key, String value);

    HttpClient file(Path filePath, String mimeType) throws HttpClientException;

    HttpClient timeout(int amount, TimeUnit timeUnit);

    HttpClient content(byte[] bytes, String mimeType);

    HttpClient content(String fileName, String mimeType) throws HttpClientException;

    HttpClient addCookie(String key, String value);

    HttpClient addCookie(String uri, String key, String value);

    HttpClient addCookie(URI uri, HttpCookie cookie);

    List<HttpCookie> getHttpCookies();

    Map<String, Map<String, String>> getCookies();

    Map<String, String> getCookies(String path);

    enum Type {
        JettyHttpClient, ApacheHttpClient, UndertowHttpClient
    }

    class HttpClientException extends Throwable {
        public HttpClientException(Throwable cause) {
            super(cause);
        }
    }
}
