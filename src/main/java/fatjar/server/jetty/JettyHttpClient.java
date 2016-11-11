package fatjar.server.jetty;

import fatjar.server.HttpClient;
import fatjar.server.dto.HttpMethod;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.PathContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JettyHttpClient implements HttpClient {

    private org.eclipse.jetty.client.HttpClient httpClient;
    private Request request;
    private ContentResponse contentResponse;

    private JettyHttpClient() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        httpClient = new org.eclipse.jetty.client.HttpClient(sslContextFactory);
        httpClient.setFollowRedirects(false);
        try {
            httpClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpClient create() {
        return new JettyHttpClient();
    }

    @Override
    public HttpClient url(String url) {
        request = httpClient.newRequest(url);
        return this;
    }

    @Override
    public HttpClient method(HttpMethod method) {
        request = request.method(org.eclipse.jetty.http.HttpMethod.valueOf(method.getValue()));
        return this;
    }

    @Override
    public HttpClient send() throws HttpClientException {
        try {
            contentResponse = request.send();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new HttpClientException(e);
        }
        return this;
    }

    @Override
    public byte[] getContent() {
        return contentResponse.getContent();
    }

    @Override
    public String getContentAsString() {
        return contentResponse.getContentAsString();
    }

    @Override
    public HttpClient agent(String browserAgent) {
        request.agent(browserAgent);
        return this;
    }

    @Override
    public HttpClient param(String key, String value) {
        request.param(key, value);
        return this;
    }

    @Override
    public HttpClient file(Path filePath, String mimeType) throws HttpClientException {
        try {
            request.file(filePath, mimeType);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
        return null;
    }

    @Override
    public HttpClient timeout(int amount, TimeUnit timeUnit) {
        request.timeout(amount, timeUnit);
        return this;
    }

    @Override
    public HttpClient content(byte[] bytes, String mimeType) {
        request.content(new BytesContentProvider(bytes), mimeType);
        return this;
    }

    @Override
    public HttpClient content(String fileName, String mimeType) throws HttpClientException {
        try {
            request.content(new PathContentProvider(Paths.get(fileName)), mimeType);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
        return this;
    }

    @Override
    public HttpClient addCookie(String key, String value) {
        HttpCookie cookie = new HttpCookie("foo", "bar");
        httpClient.getCookieStore().add(request.getURI(), cookie);
        return this;
    }

    @Override
    public HttpClient addCookie(String uri, String key, String value) {
        HttpCookie cookie = new HttpCookie("foo", "bar");
        httpClient.getCookieStore().add(URI.create(uri), cookie);
        return this;
    }

    @Override
    public HttpClient addCookie(URI uri, HttpCookie cookie) {
        httpClient.getCookieStore().add(uri, cookie);
        return this;
    }

    @Override
    public List<HttpCookie> getHttpCookies() {
        return httpClient.getCookieStore().getCookies();
    }

    @Override
    public Map<String, Map<String, String>> getCookies() {
        List<HttpCookie> httpCookies = httpClient.getCookieStore().getCookies();
        Map<String, Map<String, String>> cookies = new HashMap<>();
        for (HttpCookie cookie : httpCookies) {
            if (!cookies.containsKey(cookie.getPath())) {
                cookies.put(cookie.getPath(), new HashMap<>());
            }
            cookies.get(cookie.getPath()).put(cookie.getName(), cookie.getValue());
        }
        return cookies;
    }

    @Override
    public Map<String, String> getCookies(String path) {
        List<HttpCookie> httpCookies = httpClient.getCookieStore().getCookies();
        Map<String, String> cookies = new HashMap<>();
        for (HttpCookie cookie : httpCookies) {
            if (path.equals(cookie.getPath())) {
                cookies.put(cookie.getName(), cookie.getValue());
            }
        }
        return cookies;
    }

}
