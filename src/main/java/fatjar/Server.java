package fatjar;

import fatjar.dto.RequestResponse;
import fatjar.implementations.undertow.UndertowServer;

public interface Server {

    static Server create() {
        return UndertowServer.create();
    }

    Server listen(int port, String hostname);

    Server get(String path, RequestResponse requestResponse);

    Server post(String path, RequestResponse requestResponse);

    Server delete(String path, RequestResponse requestResponse);

    Server put(String path, RequestResponse requestResponse);

    void start();

}
