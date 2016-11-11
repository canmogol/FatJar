package fatjar.internal.dto;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Response implements Serializable {

    private final OutputStream outputStream;
    private ParamMap<String, Param<String, Object>> headers;
    private Session session;
    private Status status = Status.STATUS_OK;
    private String content = null;
    private byte[] contentChar = null;

    public Response(ParamMap<String, Param<String, Object>> headers, Session session, OutputStream outputStream) {
        this.headers = headers;
        this.session = session;
        this.outputStream = outputStream;
    }

    public ParamMap<String, Param<String, Object>> getHeaders() {
        return headers;
    }

    public void setHeaders(ParamMap<String, Param<String, Object>> headers) {
        this.headers = headers;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getContent() {
        if (content != null) {
            return content;
        } else if (contentChar != null) {
            return new String(contentChar);
        }
        return "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getContentChar() {
        return contentChar;
    }

    public void setContentChar(byte[] contentChar) {
        this.contentChar = contentChar;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headers=" + headers +
                ", session=" + session +
                ", status=" + status +
                ", content='" + content + '\'' +
                ", contentChar='" + Arrays.toString(contentChar) + '\'' +
                '}';
    }

    public void write() {
        StringBuilder sb = new StringBuilder();

        // add contentChar if not add content
        if (contentChar != null) {
            try {
                // write the headers
                try {
                    outputStream.write(sb.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    outputStream.write(sb.toString().getBytes());
                }

                //write the file content
                outputStream.write(getContentChar());

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            // append the content
            sb.append(getContent());

            // append the delimiters
            sb.append("\n\r\n\r");

            // write content to output stream
            try {
                try {
                    outputStream.write(sb.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    outputStream.write(sb.toString().getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
