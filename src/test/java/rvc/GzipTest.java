package rvc;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rvc.http.Response;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GzipTest {

    RvcServer server;
    HttpClient httpClient = new DefaultHttpClient();

    String content = "Lorem ipsum dolor sit amet, per ei alia numquam. Luptatum aliquando rationibus te pri, ut eos quando partem dissentiunt. Nulla ornatus suavitate an est. Ei brute facete prompta nec, vel constituto reprimique ea, duo eu minim mediocrem. Ad vim posse suavitate qualisque. Est id graeco everti appareat.";

    @Before
    public void start() {
        server = new RvcServer()

                .get("/gzip", () -> {
                    Response.get().gzip();
                    return content;
                })
                .quickStart();
    }

    @Test
    public void test() {
        HttpGet httpGet = new HttpGet("http://localhost:4567/gzip");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            InputStream is = response.getEntity().getContent();

//            assertArrayEquals(bytes, b);

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
