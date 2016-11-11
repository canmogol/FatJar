package fatjar.server.undertow;

import fatjar.server.Server;
import fatjar.server.dto.*;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class UndertowServer implements Server {

    private int port = 9080;
    private String hostname = "localhost";
    private Map<HttpMethod, Map<String, RequestResponse>> pathFunctions = new HashMap<>();

    private UndertowServer() {
        for (HttpMethod protocol : HttpMethod.values()) {
            pathFunctions.put(protocol, new HashMap<>());
        }
    }

    public static Server create() {
        return new UndertowServer();
    }

    @Override
    public Server listen(int port, String hostname) {
        this.port = port;
        this.hostname = hostname;
        return this;
    }

    @Override
    public Server get(String path, RequestResponse requestResponse) {
        this.pathFunctions.get(HttpMethod.GET).put(path, requestResponse);
        return this;
    }

    @Override
    public Server post(String path, RequestResponse requestResponse) {
        this.pathFunctions.get(HttpMethod.POST).put(path, requestResponse);
        return this;
    }

    @Override
    public Server delete(String path, RequestResponse requestResponse) {
        this.pathFunctions.get(HttpMethod.DELETE).put(path, requestResponse);
        return this;
    }

    @Override
    public Server put(String path, RequestResponse requestResponse) {
        this.pathFunctions.get(HttpMethod.PUT).put(path, requestResponse);
        return this;
    }

    @Override
    public void start() {
        Undertow server = Undertow.builder()
                .addHttpListener(port, hostname)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                        Request request = createRequest(exchange);
                        Response response = new Response(request.getHeaders(), request.getSession(), new OutputStream() {
                            @Override
                            public void write(int b) throws IOException {
                            }

                            @Override
                            public void write(byte[] b) throws IOException {
                                exchange.getResponseSender().send(ByteBuffer.wrap(b));
                            }
                        });
                        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod().toString());
                        if (pathFunctions.containsKey(httpMethod) && pathFunctions.get(httpMethod).containsKey(exchange.getRequestURI())) {
                            RequestResponse requestResponse = pathFunctions.get(httpMethod).get(exchange.getRequestURI());
                            requestResponse.apply(request, response);
                        }
                    }

                    private Request createRequest(HttpServerExchange exchange) {

                        ParamMap<String, Param<String, Object>> headers = new ParamMap<>();
                        headers.addParam(new Param<>(RequestKeys.PROTOCOL.getValue(), exchange.getRequestMethod().toString()));
                        exchange.getRequestHeaders().getHeaderNames().forEach((headerName) -> {
                            HeaderValues headerValues = exchange.getRequestHeaders().get(headerName);
                            headers.addParam(new Param<>(headerName.toString(), headerValues.getFirst()));
                        });

                        ParamMap<String, Param<String, Object>> params = new ParamMap<>();
                        exchange.getPathParameters().forEach((key, values) -> {
                            params.addParam(new Param<>(key, values.getFirst()));
                        });
                        exchange.getQueryParameters().forEach((key, values) -> {
                            params.addParam(new Param<>(key, values.getFirst()));
                        });

                        Session session = new Session("");
                        if (headers.containsKey(SessionKeys.COOKIE.getValue().toLowerCase()) ||
                                headers.containsKey(SessionKeys.COOKIE.getValue())) {
                            try {
                                session = new Session(String.valueOf(headers.get(SessionKeys.COOKIE.getValue()).getValue()));
                            } catch (Exception e) {
                                session = new Session(String.valueOf(headers.get(SessionKeys.COOKIE.getValue().toLowerCase()).getValue()));
                            }
                        }
                        Request request = new Request(params, headers, session);
                        exchange.getRequestReceiver().receiveFullBytes((e, data) -> {
                                    request.setBody(data);
                                },
                                (e, exception) -> {
                                    exception.printStackTrace();
                                }
                        );
                        return request;
                    }
                }).build();
        server.start();
    }

}
