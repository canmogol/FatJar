package fatjar.test;

import fatjar.HttpClient;
import fatjar.implementations.server.dto.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

public class HttpClientTest {

    @Test
    public void connectionTest() throws HttpClient.HttpClientException {
        String content = HttpClient.create().url("http://echo.jsontest.com/key/value/one/two").method(HttpMethod.GET).send().getContentAsString().replace("\n", "");
        Assert.assertEquals(content, "{   \"one\": \"two\",   \"key\": \"value\"}");
    }

}
