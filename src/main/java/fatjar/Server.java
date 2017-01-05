package fatjar;

import fatjar.dto.Request;
import fatjar.dto.Response;
import fatjar.dto.Status;
import fatjar.implementations.server.CurrentServer;

import java.util.HashMap;
import java.util.Map;

public interface Server {

    static Server create() {
        return Server.create(Type.UndertowServer, new HashMap<>());
    }

    static Server create(Map<ServerParams, String> params) {
        return Server.create(Type.UndertowServer, params);
    }

    static Server create(Server.Type type, Map<ServerParams, String> params) {
        return CurrentServer.create(type, params);
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
        UndertowServer, GrizzlyServer, NettyServer
    }

    @FunctionalInterface
    interface RequestResponse {

        void apply(Request request, Response response) throws ServerException;

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
