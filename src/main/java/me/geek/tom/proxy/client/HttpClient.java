package me.geek.tom.proxy.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Enumeration;
import java.util.stream.Collectors;

public class HttpClient {

    private static final Logger LOGGER = LogManager.getLogger("HttpProxyClient");

    public static boolean makeRequest(org.eclipse.jetty.server.Request req, String url, IHandler handler) {
        OkHttpClient client = new OkHttpClient();

        Request.Builder request = new Request.Builder();
        request.url(req.getScheme() + "://" + url);
        if (!req.getMethod().equals("GET")) {
            try {
                String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                RequestBody reqBody = RequestBody.create(body.getBytes());
                request.method(req.getMethod(), reqBody);
            } catch (IOException e) {
                //e.printStackTrace();
                request.method(req.getMethod(), null);
            }
        }
        Enumeration<String> headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            String val = req.getHeader(header);
            request.addHeader(header, val);
        }

        try (Response response = client.newCall(request.build()).execute()) {
            handler.accept(response);
            return true;
        } catch (ConnectException ignored) {
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface IHandler {
        void accept(Response res) throws IOException;
    }
}
