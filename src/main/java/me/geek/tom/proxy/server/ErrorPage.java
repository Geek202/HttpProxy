package me.geek.tom.proxy.server;

public class ErrorPage {
    public static final ErrorPage ERROR_PAGE = new ErrorPage("error.html");

    private String content = null;
    private final String name;

    public ErrorPage(String name) {
        this.name = name;
    }

    @SuppressWarnings("ConstantConditions")
    public String getPage() throws Exception {
        if (content == null)
            content = new String(this.getClass().getClassLoader().getResourceAsStream(this.name).readAllBytes())
                    .replace("${server_os}", System.getProperty("os.name"));

        return content;
    }
}
