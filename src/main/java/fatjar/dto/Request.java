package fatjar.dto;

import java.io.Serializable;

public class Request implements Serializable {

    private ParamMap<String, Param<String, Object>> headers;
    private ParamMap<String, Param<String, Object>> params;
    private Session session;
    private byte[] body;

    public Request(ParamMap<String, Param<String, Object>> params, ParamMap<String, Param<String, Object>> headers, Session session) {
        this.session = session;
        this.params = params;
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Session getSession() {
        return session;
    }

    public ParamMap<String, Param<String, Object>> getHeaders() {
        return headers;
    }

    public ParamMap<String, Param<String, Object>> getParams() {
        return params;
    }

    public boolean hasParams(String... params) {
        for (String param : params) {
            if (getParam(param) == null) {
                return false;
            }
        }
        return true;
    }

    public String getParam(String name) {
        return getParam(name, null);
    }

    public String getParam(String name, String defaultValue) {
        if (this.getParams().containsKey(name)) {
            return String.valueOf(this.getParams().getValue(name));
        } else {
            return defaultValue;
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + headers +
                ", params=" + params +
                ", session=" + session +
                '}';
    }

}
