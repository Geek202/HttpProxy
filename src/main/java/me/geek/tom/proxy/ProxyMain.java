package me.geek.tom.proxy;

import kotlin.io.ByteStreamsKt;
import me.geek.tom.proxy.config.Configuration;
import me.geek.tom.proxy.server.ProxyServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.eclipse.jetty.server.Server;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

public class ProxyMain {

    private static final Logger LOGGER = LogManager.getLogger("HttpNameProxy");

    private Configuration config;
    private File configFile = new File("./config.yml");

    public void run(String[] args) {
        if (!configFile.exists()) {
            try {
                LOGGER.info("Creating default config...");
                FileOutputStream out = new FileOutputStream(configFile);
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.yml");

                assert in != null;
                ByteStreamsKt.copyTo(in, out, 1);
            } catch (IOException e) {
                LOGGER.fatal("FAILED TO CREATE DEFAULT CONFIG!", e);
                return;
            }
        }

        try {
            loadConfig();
        } catch (Exception e) {
            LOGGER.fatal("FAILED TO LOAD CONFIGURATION.", e);
            return;
        }

        ProxyServer proxyServer = new ProxyServer(config);
        Server server = proxyServer.start(config.port);
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() throws FileNotFoundException {
        Yaml yaml = new Yaml(new Constructor(Configuration.class));

        config = yaml.load(new FileInputStream(configFile));
    }

    public Configuration getConfig() {
        return config;
    }

    public static void main(String[] args) {
        System.setOut(IoBuilder.forLogger(LOGGER).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(LOGGER).setLevel(Level.ERROR).buildPrintStream());
        LOGGER.info("Starting proxy...");
        new ProxyMain().run(args);
        LOGGER.info("Bye!");
    }
}
