package fatjar;

import fatjar.dto.Request;
import fatjar.dto.Response;

@FunctionalInterface
public interface RequestResponse {

    void apply(Request request, Response response) throws Server.ServerException;

}
