package fatjar.internal.dto;

@FunctionalInterface
public interface RequestResponse {

    void apply(Request request, Response response);

}
