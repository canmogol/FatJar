package fatjar.implementations.server;

import fatjar.Date;
import fatjar.*;
import fatjar.dto.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Undertow implements Server {

    private int port = 9080;
    private String hostname = "localhost";
    private String applicationCookieName = "APPLICATION_NAME";
    private String cookieSignSecretKey = "SIGN_KEY";
    private Map<HttpMethod, Map<String, List<RequestResponse>>> pathFunctions = new HashMap<>();
    private Map<HttpMethod, Map<String, List<RequestResponse>>> parametrizedPathFunctions = new HashMap<>();
    private Map<HttpMethod, Map<String, List<String>>> parametrizedPaths = new HashMap<>();
    private Map<String, List<RequestResponse>> filterFunctions = new HashMap<>();
    private Map<String, List<RequestResponse>> filterWildcardFunctions = new HashMap<>();
    private Map<Status, RequestResponse> statusFunctions = new HashMap<>();

    public Undertow(Map<ServerParams, String> params) {
	for (HttpMethod protocol : HttpMethod.values()) {
	    pathFunctions.put(protocol, new HashMap<>());
	    parametrizedPathFunctions.put(protocol, new HashMap<>());
	    parametrizedPaths.put(protocol, new HashMap<>());
	}
	this.port = Integer.parseInt(params.getOrDefault(ServerParams.PORT, String.valueOf(port)));
	this.hostname = params.getOrDefault(ServerParams.HOST, hostname);
	this.applicationCookieName = params.getOrDefault(ServerParams.APPLICATION_NAME, applicationCookieName);
	this.cookieSignSecretKey = params.getOrDefault(ServerParams.SIGN_KEY, cookieSignSecretKey);
    }

    public static Server create(Map<ServerParams, String> params) {
	return new Undertow(params);
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
	    if (!this.filterWildcardFunctions.containsKey(wildcardPath)) {
		this.filterWildcardFunctions.put(wildcardPath, new LinkedList<>());
	    }
	    this.filterWildcardFunctions.get(wildcardPath).add(requestResponse);
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
	this.addPathFunctionList(HttpMethod.GET, path).add(requestResponse);
	return this;
    }

    @Override
    public Server post(String path, RequestResponse requestResponse) {
	this.addPathFunctionList(HttpMethod.POST, path).add(requestResponse);
	return this;
    }

    @Override
    public Server delete(String path, RequestResponse requestResponse) {
	this.addPathFunctionList(HttpMethod.DELETE, path).add(requestResponse);
	return this;
    }

    @Override
    public Server put(String path, RequestResponse requestResponse) {
	this.addPathFunctionList(HttpMethod.PUT, path).add(requestResponse);
	return this;
    }

    @Override
    public void start() {
	try {
	    // create server object
	    io.undertow.Undertow server = io.undertow.Undertow.builder()
		    .addHttpListener(port, hostname)
		    .setHandler(new UndertowHttpHandler()).build();

	    // add to Metrics
	    Metrics.create().add(Metrics.Key.ServerType.name(), Type.Undertow.name());
	    Metrics.create().add(Metrics.Key.ServerPort.name(), port);
	    Metrics.create().add(Metrics.Key.ServerHostname.name(), hostname);
	    String services = pathFunctions.values().stream()
		    .flatMap(map -> map.keySet().stream())
		    .collect(Collectors.toSet())
		    .stream()
		    .collect(Collectors.joining(","));
	    Metrics.create().add(Metrics.Key.ServerServices.name(), services);

	    // start server
	    server.start();

	    // add to Metrics
	    Metrics.create().add(Metrics.Key.ServerStarted.name(), Date.create().getDate());

	} catch (RuntimeException e) {
	    Metrics.create().add(Metrics.Key.ServerStartFailed.name(), Date.create().getDate());
	    throw e;
	}
    }

    private List<RequestResponse> addPathFunctionList(HttpMethod httpMethod, String path) {
	if (path.contains("@")) {
	    String[] splits = path.split("@");
	    String wildPath = splits[0];
	    if (!this.parametrizedPaths.get(httpMethod).containsKey(wildPath)) {
		this.parametrizedPaths.get(httpMethod).put(wildPath, new LinkedList<>());
	    }
	    for (int i = 1; i < splits.length; i++) {
		this.parametrizedPaths.get(httpMethod).get(wildPath).add(splits[i].replace("/", ""));
	    }
	    if (!this.parametrizedPathFunctions.get(httpMethod).containsKey(wildPath)) {
		this.parametrizedPathFunctions.get(httpMethod).put(wildPath, new LinkedList<>());
	    }
	    return this.parametrizedPathFunctions.get(httpMethod).get(wildPath);
	} else {
	    if (!this.pathFunctions.get(httpMethod).containsKey(path)) {
		this.pathFunctions.get(httpMethod).put(path, new LinkedList<>());
	    }
	    return this.pathFunctions.get(httpMethod).get(path);
	}
    }

    /**
     * Undertow's Http handler
     */
    class UndertowHttpHandler implements HttpHandler {

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
	    // add to Metrics
	    Metrics.create().addRequestTime(Date.create().getDate());

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

		// wildcards and filters will not called even if there are handlers
		// call wildcards
		this.handleWildcards(exchange.getRequestURI(), request, response);
		// call filters
		this.handleFilters(exchange.getRequestURI(), request, response);

		// check if there are any handlers registered for this uri
		if (pathFunctions.containsKey(httpMethod) && pathFunctions.get(httpMethod).containsKey(exchange.getRequestURI())) {
		    // run registered handlers for this url
		    this.handleRequestResponse(httpMethod, exchange.getRequestURI(), request, response);
		    // set request to handled
		} else {
		    /**
		     * exchange.getRequestURI()     /html/user/login.html
		     * parametrizedPathFunctions            html, js, css, image
		     */
		    Map<String, List<RequestResponse>> requestMethodMap = parametrizedPathFunctions.get(httpMethod);
		    Map<String, List<String>> parametersMap = parametrizedPaths.get(httpMethod);

		    // status
		    boolean parametrizedStatus = false;

		    // check if parametrized method exists
		    for (String parametrized : requestMethodMap.keySet()) {
			if (exchange.getRequestURI().startsWith(parametrized)) {
			    // add parameter keys and values
			    String remainingURI = exchange.getRequestURI().substring(parametrized.length());
			    List<String> keys = parametersMap.get(parametrized);
			    String[] split = remainingURI.split("/");
			    for (int i = 0; i < split.length; i++) {
				request.getQueryParams().addParam(new Param<>("@" + keys.get(i), split[i]));
			    }
			    // find and execute parameter functions
			    List<RequestResponse> requestResponseList = requestMethodMap.get(parametrized);
			    for (RequestResponse requestResponse : requestResponseList) {
				requestResponse.apply(request, response);
				parametrizedStatus = true;
			    }
			}
		    }

		    // if no one handled this request, throw not found
		    if (!parametrizedStatus) {
			// notify client that there is no handler for this call
			throw new ServerException(Status.STATUS_NOT_FOUND, exchange.getRequestURI());
		    }
		}

	    } catch (ServerException e) {
		// add to Metrics 
		Metrics.create().add(Metrics.Key.LastErrorTime.name(), Date.create().getDate());

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
	    List<RequestResponse> wildcardRequestResponseFunctions = filterWildcardFunctions.keySet()
		    .stream()
		    .filter(path::startsWith)
		    .flatMap(wildcardPath -> filterWildcardFunctions.get(wildcardPath).stream())
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
	    String content = JSON.create().toJson(responseMap);
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
		exchange.getRequestReceiver().receiveFullBytes((e, data) -> {
			    if (data != null) {
				request.setPost(data);
			    }
			},
			(e, exception) -> {
			    Log.error("got exception at receiveFullBytes, exception: " + exception, exception);
			}
		);
	    } else {
		exchange.getRequestReceiver().receiveFullBytes((e, data) -> {
			    request.setBody(data);
			},
			(e, exception) -> {
			    Log.error("got exception while setting request body, exception: " + exception, exception);
			}
		);
	    }

	    // return Request object
	    return request;
	}
    }

}
