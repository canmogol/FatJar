package fatjar;

import fatjar.dto.Status;
import fatjar.implementations.undertow.UndertowServer;

import java.util.Map;

public interface Server {

    static Server create(Map<ServerParams, String> params) {
        return UndertowServer.create(params);
    }

    static Server create() {
        return UndertowServer.create();
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
