package fatjar.server.dto;

@FunctionalInterface
public interface RequestResponse {

    void apply(Request request, Response response);

}
