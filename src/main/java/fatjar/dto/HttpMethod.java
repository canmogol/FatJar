package fatjar.dto;

public enum HttpMethod {
    GET("GET"), HEAD("HEAD"), POST("POST"), PUT("PUT"), DELETE("DELETE"),
    TRACE("TRACE"), OPTIONS("OPTIONS"), CONNECT("CONNECT"), PATCH("PATCH");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
