package rvc;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.*;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class RvcServerTest {

    RvcServer server;
    HttpClient httpClient = new DefaultHttpClient();


    @Before
    public void start() {
        server = new RvcServer();
        server.init();
        server.get("/test", () -> "test")
                .quickStart();
    }

    @Test
    public void test() {
        HttpGet httpGet = new HttpGet("http://localhost:4567/test");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            assertEquals(200, response.getStatusLine().getStatusCode());
            InputStream is = response.getEntity().getContent();
            String content = IOUtils.toString(is);
            assertEquals(content, "test");
        } catch (Exception ex) {
            fail(ex.toString());
        } finally {
            httpGet.releaseConnection();
        }

    }

    @After
    public void stop() throws Exception {
        server.stop();
    }
}
