package fatjar.dto;

public enum SessionKeys {

    USERNAME("USERNAME"),
    PASSWORD("PASSWORD"),
    AUTHENTICATION_TYPE("AUTHENTICATION_TYPE"),
    SESSION_STORED_AT("SESSION_STORED_AT"),
    SESSION_ID("SESSION_ID"),
    IS_LOGGED("IS_LOGGED"),
    COOKIE_SIGN_KEY("COOKIE_SIGN_KEY"),
    GROUP_NAMES("GROUP_NAMES"),
    COOKIE("Cookie");

    private final String value;

    SessionKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
