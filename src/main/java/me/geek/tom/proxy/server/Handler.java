package me.geek.tom.proxy.server;

import me.geek.tom.proxy.client.HttpClient;
import me.geek.tom.proxy.config.Configuration;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.CharBuffer;

public class Handler extends AbstractHandler {

    private static final Logger LOGGER = LogManager.getLogger("ProxyServer");

    private Configuration config;

    public Handler(Configuration config) {
        this.config = config;
    }

    @Override
    public void handle(String target, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        String hostname = baseRequest.getHeader("Host");
        if (hostname.contains(":"))
            hostname = hostname.split(":")[0];
        String host = config.getTo(hostname);
        if (!target.contains("world/world"))
            LOGGER.info("Got request for: " + hostname + target + " forwarding to " + host + target);
        if (host == null)
            sendError(response);
        else
            proxy(host, target, baseRequest, response);
        baseRequest.setHandled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void proxy(String host, String target, Request baseRequest, HttpServletResponse response) throws IOException {
        //response.getWriter().write("This would be the page from " + host + " at " + target);
        //response.setContentType("text/plain");
        boolean ok = HttpClient.makeRequest(baseRequest, host + target, res -> {
            try {
                res.headers().iterator().forEachRemaining(head -> response.setHeader(head.component1(), head.component2()));
            } catch (NullPointerException ignored) {}
            response.setStatus(res.code());
            //LOGGER.info("Return code: " + res.code());
            try {
                byte[] r = res.body().bytes();
                if (!target.contains("world/world"))
                    LOGGER.info("Forwarding " + r.length + " bytes!");
                response.getOutputStream().write(r);
            } catch (NullPointerException ignored) {}
        });
        if (!ok) sendError(response);
    }

    private void sendError(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        try {
            response.getWriter().write(ErrorPage.ERROR_PAGE.getPage());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("oops, well that didnt quite work out how i thought lol.");
        }
    }
}
