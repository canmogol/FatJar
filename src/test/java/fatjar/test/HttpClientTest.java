package fatjar.test;

import fatjar.HttpClient;
import fatjar.dto.HttpMethod;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HttpClientTest {

    public static final String ACTUAL = "{   \"one\": \"two\",   \"key\": \"value\"}";
    public static final String INVALID_PROTOCOL_BOGUS = "Invalid protocol bogus";
    public static final String BOGUS_URL = "bogus://url";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void exceptionTest() throws HttpClient.HttpClientException {
        expectedException.expect(HttpClient.HttpClientException.class);
        expectedException.expectMessage(containsString(INVALID_PROTOCOL_BOGUS));
        String content = HttpClient.create().url(BOGUS_URL).method(HttpMethod.GET).send().getContentAsString();
        System.out.println("content = " + content);
    }


    @Test
    public void connectionTest() throws HttpClient.HttpClientException {
        String content = HttpClient.create().url("http://echo.jsontest.com/key/value/one/two").method(HttpMethod.GET).send().getContentAsString().replace("\n", "");
        assertThat(content, allOf(is(ACTUAL), instanceOf(String.class), endsWith("}"), notNullValue(), not(""), startsWith("{")));
        assertEquals(content, ACTUAL);
    }

}
