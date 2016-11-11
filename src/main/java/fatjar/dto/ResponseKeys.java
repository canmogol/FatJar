package fatjar.dto;

public enum ResponseKeys {

    PROTOCOL("PROTOCOL"),
    STATUS("STATUS"),
    MESSAGE("MESSAGE"),
    EXPIRES("Expires"),
    CACHE_CONTROL("Cache-Control"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    RESPONSE_TYPE("RESPONSE_TYPE"),
    SERVER("Server");


    private final String value;

    ResponseKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
