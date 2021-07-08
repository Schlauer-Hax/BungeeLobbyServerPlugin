package com.hax.bungeelobbyserverplugin;

import io.netty.channel.unix.DomainSocketAddress;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class ConfigHelper {

    private static File file;
    private static Configuration bungeeConfig;
    private static boolean locked; // TODO: This is dumb. Writes are lost when locked

    static {
        // Plugins are loaded before the config (on first run) so uhhhhh, here we go
        setupConfig();

        if (locked) {
            ProxyServer.getInstance().getScheduler().schedule(BungeeLobbyServerPluginBungeeCord.get(), new Runnable() {
                @Override
                public void run() {
                    setupConfig();
                }
            }, 5L, TimeUnit.SECONDS);
        }
    }

    public static String socketAddressToString(SocketAddress socketAddress) {
        String addressString;

        if (socketAddress instanceof DomainSocketAddress) {
            addressString = "unix:" + ((DomainSocketAddress) socketAddress).path();
        } else if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetAddress = (InetSocketAddress) socketAddress;

            addressString = inetAddress.getHostString() + ":" + inetAddress.getPort();
        } else {
            addressString = socketAddress.toString();
        }

        return addressString;
    }

    public static void addToConfig(ServerInfo serverInfo) {
        if (locked) {
            return;
        }

        bungeeConfig.set("servers." + serverInfo.getName() + ".motd", serverInfo.getMotd().replace(ChatColor.COLOR_CHAR, '&'));
        bungeeConfig.set("servers." + serverInfo.getName() + ".address", socketAddressToString(serverInfo.getSocketAddress()));
        bungeeConfig.set("servers." + serverInfo.getName() + ".restricted", serverInfo.isRestricted());
        saveConfig();
    }

    public static void removeFromConfig(ServerInfo serverInfo) {
        removeFromConfig(serverInfo.getName());
    }

    public static void removeFromConfig(String name) {
        if (locked) {
            return;
        }

        bungeeConfig.set("servers." + name, null);
        saveConfig();
    }

    private static void saveConfig() {
        if (locked) {
            return;
        }

        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(bungeeConfig, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPortRange()  {
        return bungeeConfig.getString("portrange");
    }

    private static void setupConfig() {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            file = new File(ProxyServer.getInstance().getPluginsFolder().getParentFile(), "config.yml");

            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);

            bungeeConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(isr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }

                if (isr != null) {
                    isr.close();
                }
            } catch (IOException ignored) {}
        }

        locked = bungeeConfig == null;
    }

}
