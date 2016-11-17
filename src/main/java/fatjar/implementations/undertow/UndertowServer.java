package fatjar.implementations.undertow;

import fatjar.JSON;
import fatjar.Log;
import fatjar.RequestResponse;
import fatjar.Server;
import fatjar.dto.*;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class UndertowServer implements Server {

    private int port = 9080;
    private String hostname = "localhost";
    private String applicationCookieName = "APPLICATION_NAME";
    private String cookieSignSecretKey = "SIGN_KEY";
    private Map<HttpMethod, Map<String, List<RequestResponse>>> pathFunctions = new HashMap<>();
    private Map<String, List<RequestResponse>> filterFunctions = new HashMap<>();
    private Map<String, List<RequestResponse>> wildcardFunctions = new HashMap<>();
    private Map<Status, RequestResponse> statusFunctions = new HashMap<>();

    private UndertowServer() {
        this(new HashMap<>());
    }

    public UndertowServer(Map<ServerParams, String> params) {
        for (HttpMethod protocol : HttpMethod.values()) {
            pathFunctions.put(protocol, new HashMap<>());
        }
        this.port = Integer.parseInt(params.getOrDefault(ServerParams.PORT, "9080"));
        this.hostname = params.getOrDefault(ServerParams.HOST, "0.0.0.0");
        this.applicationCookieName = params.getOrDefault(ServerParams.APPLICATION_NAME, "APPLICATION_NAME");
        this.cookieSignSecretKey = params.getOrDefault(ServerParams.SIGN_KEY, "SIGN_KEY");
    }

    public static Server create() {
        return new UndertowServer();
    }

    public static Server create(Map<ServerParams, String> params) {
        return new UndertowServer(params);
    }

    @Override
    public Server listen(int port, String hostname) {
        Log.info("listening host:port " + hostname + ":" + port);
        this.port = port;
        this.hostname = hostname;
        return this;
    }

    @Override
    public Server filter(String path, RequestResponse requestResponse) {
        if (path.endsWith("*")) {
            String wildcardPath = path.split("\\*")[0];
            if (!this.wildcardFunctions.containsKey(wildcardPath)) {
                this.wildcardFunctions.put(wildcardPath, new LinkedList<>());
            }
            this.wildcardFunctions.get(wildcardPath).add(requestResponse);
        } else {
            if (!this.filterFunctions.containsKey(path)) {
                this.filterFunctions.put(path, new LinkedList<>());
            }
            this.filterFunctions.get(path).add(requestResponse);
        }
        return this;
    }

    @Override
    public Server register(Status status, RequestResponse requestResponse) {
        this.statusFunctions.put(status, requestResponse);
        return this;
    }

    @Override
    public Server get(String path, RequestResponse requestResponse) {
        this.getPathFunctionList(HttpMethod.GET, path).add(requestResponse);
        return this;
    }

    @Override
    public Server post(String path, RequestResponse requestResponse) {
        this.getPathFunctionList(HttpMethod.POST, path).add(requestResponse);
        return this;
    }

    @Override
    public Server delete(String path, RequestResponse requestResponse) {
        this.getPathFunctionList(HttpMethod.DELETE, path).add(requestResponse);
        return this;
    }

    @Override
    public Server put(String path, RequestResponse requestResponse) {
        this.getPathFunctionList(HttpMethod.PUT, path).add(requestResponse);
        return this;
    }

    @Override
    public void start() {
        Undertow server = Undertow.builder()
                .addHttpListener(port, hostname)
                .setHandler(new UndertowHttpHandler()).build();
        server.start();
    }

    private List<RequestResponse> getPathFunctionList(HttpMethod httpMethod, String path) {
        if (!this.pathFunctions.get(httpMethod).containsKey(path)) {
            this.pathFunctions.get(httpMethod).put(path, new LinkedList<>());
        }
        return this.pathFunctions.get(httpMethod).get(path);
    }

    /**
     * UndertowServer's Http handler
     */
    class UndertowHttpHandler implements HttpHandler {

        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            // create Request DTO, Request object isolates undertow API
            Request request = createRequest(exchange);

            // Response object will write the content to undertow exchange's sender
            Response response = new Response(new ParamMap<>(), request.getSession(), new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    write(new byte[]{(byte) b});
                }

                @Override
                public void write(byte[] b) throws IOException {
                    if (b != null && b.length > 0) {
                        exchange.setResponseContentLength(b.length);
                        exchange.getResponseSender().send(ByteBuffer.wrap(b));
                    }
                }
            }) {
                @Override
                public void write() {
                    exchange.setStatusCode(getStatus().getStatus());
                    for (Param<String, Object> param : getHeaders().values()) {
                        exchange.getResponseHeaders().put(new HttpString(param.getKey()), String.valueOf(param.getValue()));
                    }
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, getContentType());
                    exchange.getResponseHeaders().put(Headers.SET_COOKIE, request.getSession().toCookie());
                    super.write();
                }
            };

            try {
                // find the http method, GET, POST etc.
                HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod().toString());
                // check if there are any handlers registered for this uri
                // wildcards and filters will not be called if there are no handlers exist
                if (pathFunctions.containsKey(httpMethod) && pathFunctions.get(httpMethod).containsKey(exchange.getRequestURI())) {
                    // call wildcards
                    this.handleWildcards(exchange.getRequestURI(), request, response);
                    // call filters
                    this.handleFilters(exchange.getRequestURI(), request, response);
                    // run registered handlers for this url
                    this.handleRequestResponse(httpMethod, exchange.getRequestURI(), request, response);
                } else {
                    // notify client that there is no handler for this call
                    throw new ServerException(Status.STATUS_NOT_FOUND, exchange.getRequestURI());
                }
            } catch (ServerException e) {
                // notify client that server got an exception
                this.handleServerError(request, response, e);
            }
        }

        private void handleRequestResponse(HttpMethod httpMethod, String path, Request request, Response response) throws ServerException {
            for (RequestResponse requestResponseFunction : pathFunctions.get(httpMethod).get(path)) {
                requestResponseFunction.apply(request, response);
            }
        }

        private void handleWildcards(String path, Request request, Response response) throws ServerException {
            List<RequestResponse> wildcardRequestResponseFunctions = wildcardFunctions.keySet()
                    .stream()
                    .filter(path::startsWith)
                    .flatMap(wildcardPath -> wildcardFunctions.get(wildcardPath).stream())
                    .collect(Collectors.toList());
            // calling apply inside the forEach() method will mask the wildcard function's exception,
            // so call the apply function in a for loop
            for (RequestResponse requestResponse : wildcardRequestResponseFunctions) {
                requestResponse.apply(request, response);
            }
        }

        private void handleFilters(String path, Request request, Response response) throws ServerException {
            if (filterFunctions.containsKey(path)) {
                List<RequestResponse> filters = filterFunctions.get(path);
                for (RequestResponse requestResponse : filters) {
                    requestResponse.apply(request, response);
                }
            }
        }

        private void handleServerError(Request request, Response response, ServerException e) {
            try {
                if (e.getStatus() != null && statusFunctions.containsKey(e.getStatus())) {
                    response.setStatus(e.getStatus());
                    RequestResponse requestResponse = statusFunctions.get(e.getStatus());
                    requestResponse.apply(request, response);
                } else {
                    this.handleError(request, response, e);
                }
            } catch (ServerException e1) {
                this.handleError(request, response, e1);
            }
        }

        private void handleError(Request request, Response response, ServerException e) {
            Map<String, Object> responseMap = new TreeMap<>();
            responseMap.put("error", String.valueOf(e));
            if (e.getStatus() != null) {
                response.setStatus(e.getStatus());
                responseMap.put("status", e.getStatus());
            } else {
                response.setStatus(Status.STATUS_INTERNAL_SERVER_ERROR);
                responseMap.put("status", String.valueOf(Status.STATUS_INTERNAL_SERVER_ERROR.getStatus()));
            }
            String content = JSON.toJson(responseMap);
            response.setContent(content);
            response.write();
        }

        private Request createRequest(HttpServerExchange exchange) {

            ParamMap<String, Param<String, Object>> headers = new ParamMap<>();
            headers.addParam(new Param<>(RequestKeys.PROTOCOL.getValue(), exchange.getRequestMethod().toString()));
            headers.addParam(new Param<>(RequestKeys.URI.getValue(), exchange.getRequestURI()));
            headers.addParam(new Param<>(RequestKeys.URL.getValue(), exchange.getRequestURL()));
            headers.addParam(new Param<>(RequestKeys.HEADER_NAMES.getValue(), exchange.getRequestHeaders().getHeaderNames().stream().map(HttpString::toString).collect(Collectors.toList())));
            headers.addParam(new Param<>(RequestKeys.HOST_NAME.getValue(), exchange.getHostName()));
            headers.addParam(new Param<>(RequestKeys.HOST_PORT.getValue(), exchange.getHostPort()));
            headers.addParam(new Param<>(RequestKeys.QUERY_STRING.getValue(), exchange.getQueryString()));
            headers.addParam(new Param<>(RequestKeys.REQUEST_METHOD.getValue(), exchange.getRequestMethod().toString()));
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

            Session session;
            if (headers.containsKey(SessionKeys.COOKIE.getValue().toLowerCase()) ||
                    headers.containsKey(SessionKeys.COOKIE.getValue())) {
                try {
                    session = new Session(
                            String.valueOf(headers.get(SessionKeys.COOKIE.getValue()).getValue()),
                            cookieSignSecretKey,
                            applicationCookieName
                    );
                } catch (Exception e) {
                    session = new Session(
                            String.valueOf(headers.get(SessionKeys.COOKIE.getValue().toLowerCase()).getValue()),
                            cookieSignSecretKey,
                            applicationCookieName
                    );
                }
            } else {
                session = new Session("", cookieSignSecretKey, applicationCookieName);
            }

            Request request = new Request(params, headers, session);
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod().toString())) {
                try {
                    FormEncodedDataDefinition formEncodedDataDefinition = new FormEncodedDataDefinition();
                    FormDataParser parser = formEncodedDataDefinition.create(exchange);
                    FormData formData = parser.parseBlocking();
                    for (String key : formData) {
                        System.out.println(key + ":");
                        for (FormData.FormValue value : formData.get(key)) {
                            System.out.println("\t" + value.getValue());
                            request.getParams().addParam(new Param<>(key, value.getValue()));
                        }
                    }
                } catch (Exception ex) {
                    Log.error("could not handle post request, exception: " + ex);
                }
            } else {
                exchange.getRequestReceiver().receiveFullBytes((e, data) -> {
                            request.setBody(data);
                        },
                        (e, exception) -> {
                            exception.printStackTrace();
                        }
                );
            }

            // return Request object
            return request;
        }
    }

}
