package com.hax.bungeelobbyserverplugin;

import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BungeeLobbyServerPluginBungeeCord extends Plugin {

    public static BungeeLobbyServerPluginBungeeCord instance;
    String portrange;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        this.portrange = ConfigHelper.getPortRange();
        this.getProxy().getPluginManager().registerListener(this, new PingListener());
        this.getProxy().getScheduler().schedule(this, () -> {
            int start = Integer.parseInt(portrange.split("-")[0]);
            int end = Integer.parseInt(portrange.split("-")[1]);

            for (int i = start; i<=end; i++) {
                String response = new ServerChecker().checkServer(i);
                if (response!=null) {
                    ServerHelper.addServer(
                    ProxyServer.getInstance().constructServerInfo(
                            String.format("%s-%d",
                                    response.split("§")[0].replaceAll(" ", "_"),
                                    new Random().nextInt(100)),
                            Util.getAddr("127.0.0.1:" + i),
                            "",
                            false));
                } else {
                    ServerHelper.removeServer(Util.getAddr("127.0.0.1:" + i));
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BungeeLobbyServerPluginBungeeCord get() {
        return instance;
    }
}
