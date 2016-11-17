package fatjar.dto;

public enum SessionKeys {

    COOKIE_CONTENT("COOKIE_CONTENT"),
    COOKIE_SIGN_KEY("COOKIE_SIGN_KEY"),
    COOKIE_ENCRYPTED("COOKIE_ENCRYPTED"),
    COOKIE("Cookie");

    private final String value;

    SessionKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
