package fatjar.server.dto;

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

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + headers +
                ", params=" + params +
                ", session=" + session +
                '}';
    }


}
