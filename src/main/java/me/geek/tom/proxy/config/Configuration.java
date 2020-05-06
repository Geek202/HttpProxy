package me.geek.tom.proxy.config;

import java.util.List;

public class Configuration {

    public List<Site> sites;
    public int port;

    public String getTo(String from) {
        for (Site site : sites) {
            if (site.at.equals(from))
                return site.go;
        }
        return null;
    }
}
