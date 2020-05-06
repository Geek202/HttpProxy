package me.geek.tom.proxy.server;

import java.io.InputStream;

public class ErrorPage {
    public static final ErrorPage ERROR_PAGE = new ErrorPage("error.html");

    private String content = null;
    private final String name;

    public ErrorPage(String name) {
        this.name = name;
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public String getPage() throws Exception {
        if (content == null) {
            InputStream stream =  this.getClass().getClassLoader().getResourceAsStream(this.name);
            byte[] data = new byte[stream.available()];
            stream.read(data);
            content = new String(data)
                    .replace("${server_os}", System.getProperty("os.name"));
        }

        return content;
    }
}
