package fatjar.dto;

import fatjar.Log;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Request implements Serializable {

    private static Map<String, String> mimeTypes = new HashMap<>();
    private ParamMap<String, Param<String, Object>> headers;
    private ParamMap<String, Param<String, Object>> params;
    private Session session;
    private byte[] body;
    private byte[] post;
    private Map<String, String> postMap = null;

    public Request(ParamMap<String, Param<String, Object>> params, ParamMap<String, Param<String, Object>> headers, Session session) {
        this.session = session;
        this.params = params;
        this.headers = headers;
    }

    public String getMimeType(String file) {
        if (mimeTypes.isEmpty()) {
            URL url = getClass().getClassLoader().getResource(".");
            if (url != null) {
                try {
                    File mimeTypeMapFile = new File(url.getPath() + "/MimeTypesMap.properties");
                    Properties properties = new Properties();
                    properties.load(new FileReader(mimeTypeMapFile));
                    for (Object key : properties.keySet()) {
                        String valueString = String.valueOf(properties.get(key));
                        String[] values = valueString.split(" ");
                        for (String value : values) {
                            mimeTypes.put(value.trim(), String.valueOf(key).trim());
                        }
                    }
                } catch (Exception e) {
                    Log.error("got exception while reading mime types, error: " + e, e);
                }
            }
        }
        String fileType = file.substring(file.lastIndexOf(".") + 1);
        return mimeTypes.get(fileType);
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

    public ParamMap<String, Param<String, Object>> getQueryParams() {
        return params;
    }

    public boolean hasQueryParams(String... params) {
        for (String param : params) {
            if (getQueryParam(param) == null) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPostParams(String... params) {
        for (String param : params) {
            if (getPostParam(param) == null) {
                return false;
            }
        }
        return true;
    }

    public String getHeader(RequestKeys requestKey) {
        String key = requestKey.toString();
        return getHeader(key);
    }

    public String getHeader(String key) {
        if (getHeaders().containsKey(key) && getHeaders().getValue(key) != null) {
            return getHeaders().getValue(key).toString();
        } else {
            return null;
        }
    }

    public String getQueryParam(String name) {
        return getQueryParam(name, null);
    }

    public String getQueryParam(String name, String defaultValue) {
        if (this.getQueryParams().containsKey(name)) {
            return String.valueOf(this.getQueryParams().getValue(name));
        } else {
            return defaultValue;
        }
    }

    public byte[] getPost() {
        return post;
    }

    public void setPost(byte[] post) {
        this.post = post;
    }

    public String getPostParam(String name) {
        if (this.postMap == null) {
            this.postMap = new HashMap<>();
            try {
                String postString = new String(this.post);
                String[] keyValuePairs = postString.split("&");
                for (String keyValuePair : keyValuePairs) {
                    String[] keyValue = keyValuePair.split("=");
                    if (keyValue.length == 2) {
                        this.postMap.put(keyValue[0], keyValue[1]);
                    }
                }
            } catch (Exception e) {
                Log.error("got exception while reading body to map, error: " + e, e);
            }
        }
        return this.postMap.get(name);
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
