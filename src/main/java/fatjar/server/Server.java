package fatjar.server;

import fatjar.server.dto.RequestResponse;
import fatjar.server.undertow.UndertowServer;

public interface Server {

    public static Server create() {
        return UndertowServer.create();
    }

    Server listen(int port, String hostname);

    Server get(String path, RequestResponse requestResponse);

    Server post(String path, RequestResponse requestResponse);

    Server delete(String path, RequestResponse requestResponse);

    Server put(String path, RequestResponse requestResponse);

    void start();

}
