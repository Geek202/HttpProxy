package me.geek.tom.proxy.server;

import me.geek.tom.proxy.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;

public class ProxyServer {

    private static final Logger LOGGER = LogManager.getLogger("ProxyServer");
    private final Configuration config;

    public ProxyServer(Configuration config) {
        this.config = config;
    }

    public Server start(int port) {
        Server server = new Server(port);
        server.setHandler(new Handler(config));
        server.setStopAtShutdown(true);
        try {
            server.start();
        } catch (Exception e) {
            LOGGER.fatal("Failed to start server", e);
        }
        return server;
    }
}
