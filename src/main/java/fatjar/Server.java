package fatjar;

import fatjar.dto.Status;
import fatjar.implementations.server.CurrentServer;

import java.util.HashMap;
import java.util.Map;

public interface Server {

    static Server create(Server.Type type, Map<ServerParams, String> params) {
        return CurrentServer.create(type, params);
    }

    static Server create(Map<ServerParams, String> params) {
        return Server.create(Type.Undertow, params);
    }

    static Server create() {
        return Server.create(Type.Undertow, new HashMap<>());
    }

    Server listen(int port, String hostname);

    Server filter(String path, RequestResponse requestResponse);

    Server register(Status status, RequestResponse requestResponse);

    Server get(String path, RequestResponse requestResponse);

    Server post(String path, RequestResponse requestResponse);

    Server delete(String path, RequestResponse requestResponse);

    Server put(String path, RequestResponse requestResponse);

    void start();

    enum ServerParams {
        PORT, HOST, APPLICATION_NAME, SIGN_KEY
    }

    enum Type {
        Undertow, Grizzly, Netty
    }

    class ServerException extends Throwable {

        private Status status = null;

        public ServerException(Status status) {
            this.status = status;
        }

        public ServerException(Status status, String message) {
            super(message);
            this.status = status;
        }

        public ServerException(String message) {
            super(message);
        }

        public ServerException(Throwable cause) {
            super(cause);
        }

        public Status getStatus() {
            return status;
        }
    }

}
