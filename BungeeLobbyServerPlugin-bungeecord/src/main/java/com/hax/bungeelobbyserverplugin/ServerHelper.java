package com.hax.bungeelobbyserverplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ServerHelper {

    public static boolean serverExists(SocketAddress ip) {
        return getServerInfoByIp(ip) != null;
    }

    public static ServerInfo getServerInfoByIp(SocketAddress ip) {
        Optional<ServerInfo> info = getServers().values().stream().filter(serverInfo ->
                serverInfo.getSocketAddress().equals(ip)).findFirst();
        return info.orElse(null);
    }

    public static ServerInfo getServerInfo(String name) {
        return getServers().get(name);
    }

    public static void addServer(ServerInfo serverInfo) {
        if (serverExists(serverInfo.getSocketAddress())) {
            return;
        }

        getServers().put(serverInfo.getName(), serverInfo);
        ConfigHelper.addToConfig(serverInfo);
        System.out.println("Added new Server with url " + serverInfo.getSocketAddress() + " to config.");
    }

    public static void removeServer(SocketAddress ip) {
        if (!serverExists(ip)) {
            return;
        }

        ServerInfo info = getServerInfoByIp(ip);

        for (ProxiedPlayer p : info.getPlayers()) {
            p.connect(getServers().get(p.getPendingConnection().getListener().getServerPriority().get(0)));
        }

        getServers().remove(info.getName());
        ConfigHelper.removeFromConfig(info.getName());
        System.out.println("Removed Server with url " + info.getSocketAddress() + " from config.");
    }

    public static Map<String, ServerInfo> getServers() {
        return ProxyServer.getInstance().getServers();
    }

}
