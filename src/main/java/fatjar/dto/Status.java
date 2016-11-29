package fatjar.dto;

import java.io.Serializable;

public class Status implements Serializable {

    // Informational responses
    public final static Status STATUS_CONTINUE = new Status(100, "Continue");
    public final static Status STATUS_SWITCHING_PROTOCOL = new Status(101, "Switching Protocol");

    // Successful responses
    public final static Status STATUS_OK = new Status(200, "OK");
    public final static Status STATUS_CREATED = new Status(201, "Created");
    public final static Status STATUS_ACCEPTED = new Status(202, "Accepted");
    public final static Status STATUS_NON_AUTHORITATIVE_INFORMATION = new Status(203, "Non-Authoritative Information");
    public final static Status STATUS_NO_CONTENT = new Status(204, "No Content");
    public final static Status STATUS_RESET_CONTENT = new Status(205, "Reset Content");
    public final static Status STATUS_PARTIAL_CONTENT = new Status(206, "Partial Content");
    public final static Status STATUS_AUTHENTICATION_SUCCESSFUL = new Status(230, "Authentication Successful");

    // Redirection messages
    public final static Status STATUS_MULTIPLE_CHOICE = new Status(300, "Multiple Choice");
    public final static Status STATUS_MOVED_PERMANENTLY = new Status(301, "Moved Permanently");
    public final static Status STATUS_FOUND = new Status(302, "Found");
    public final static Status STATUS_SEE_OTHER = new Status(303, "See Other");
    public final static Status STATUS_NOT_MODIFIED = new Status(304, "Not Modified");
    public final static Status STATUS_USE_PROXY = new Status(305, "Use Proxy");
    public final static Status STATUS_UNUSED = new Status(306, "unused");
    public final static Status STATUS_TEMPORARY_REDIRECT = new Status(307, "Temporary Redirect");
    public final static Status STATUS_PERMANENT_REDIRECT = new Status(308, "Permanent Redirect");

    // Client error responses
    public final static Status STATUS_BAD_REQUEST = new Status(400, "Bad Request");
    public final static Status STATUS_UNAUTHORIZED = new Status(401, "Unauthorized");
    public final static Status STATUS_PAYMENT_REQUIRED = new Status(402, "Payment Required");
    public final static Status STATUS_FORBIDDEN = new Status(403, "Forbidden");
    public final static Status STATUS_NOT_FOUND = new Status(404, "Not Found");
    public final static Status STATUS_METHOD_NOT_ALLOWED = new Status(405, "Method Not Allowed");
    public final static Status STATUS_NOT_ACCEPTABLE = new Status(406, "Not Acceptable");
    public final static Status STATUS_PROXY_AUTHENTICATION_REQUIRED = new Status(407, "Proxy Authentication Required");
    public final static Status STATUS_REQUEST_TIMEOUT = new Status(408, "Request Timeout");
    public final static Status STATUS_CONFLICT = new Status(409, "Conflict");
    public final static Status STATUS_GONE = new Status(410, "Gone");
    public final static Status STATUS_LENGTH_REQUIRED = new Status(411, "Length Required");
    public final static Status STATUS_PRECONDITION_FAILED = new Status(412, "Precondition Failed");
    public final static Status STATUS_REQUEST_ENTITY_TOO_LARGE = new Status(413, "Request Entity Too Large");
    public final static Status STATUS_REQUEST_URI_TOO_LONG = new Status(414, "Request-URI Too Long");
    public final static Status STATUS_UNSUPPORTED_MEDIA_TYPE = new Status(415, "Unsupported Media Type");
    public final static Status STATUS_REQUESTED_RANGE_NOT_SATISFIABLE = new Status(416, "Requested Range Not Satisfiable");
    public final static Status STATUS_EXPECTATION_FAILED = new Status(417, "Expectation Failed");

    // Server error responses
    public final static Status STATUS_INTERNAL_SERVER_ERROR = new Status(500, "Internal Server Error");
    public final static Status STATUS_NOT_IMPLEMENTED = new Status(501, "Not Implemented");
    public final static Status STATUS_BAD_GATEWAY = new Status(503, "Bad Gateway");
    public final static Status STATUS_SERVICE_UNAVAILABLE = new Status(503, "Service Unavailable");
    public final static Status STATUS_GATEWAY_TIMEOUT = new Status(504, "Gateway Timeout");
    public final static Status STATUS_HTTP_VERSION_NOT_SUPPORTED = new Status(505, "HTTP Version Not Supported");

    private final int status;
    private final String message;

    public Status(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
