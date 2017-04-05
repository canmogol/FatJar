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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void connectionTest() throws HttpClient.HttpClientException {
        expectedException.expect(HttpClient.HttpClientException.class);
        expectedException.expectMessage(containsString(" "));
        String content = HttpClient.create().url("http://echo.jsontest.com/key/value/one/two").method(HttpMethod.GET).send().getContentAsString().replace("\n", "");
        assertThat(content, allOf(is(ACTUAL), instanceOf(String.class), endsWith("}"), notNullValue(), not(""), startsWith("{")));
        assertEquals(content, ACTUAL);
    }

}
