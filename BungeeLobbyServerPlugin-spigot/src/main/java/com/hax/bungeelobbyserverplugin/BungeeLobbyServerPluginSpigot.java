package com.hax.bungeelobbyserverplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class BungeeLobbyServerPluginSpigot extends JavaPlugin implements Listener {

    BungeeChannelApi bungeeChannelApi;
    InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.bungeeChannelApi = BungeeChannelApi.of(this);
        this.inventoryManager = new InventoryManager(this, this.bungeeChannelApi);

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getServer().broadcastMessage("Welcome to the server!");
        event.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            bungeeChannelApi.getServers().whenComplete((strings, throwable) -> {
                getAllPlayerCounts(strings, 0, new HashMap<>());
            });
        }, 20);
    }

    public void getAllPlayerCounts(List<String> servers, int i, HashMap<String, Integer> datamap) {
        if (i >= servers.size()) {
            this.inventoryManager.openNavigator(servers, datamap);
        }
        String server = servers.get(i);
        bungeeChannelApi.getPlayerCount(server).whenComplete((integer, throwable) -> {
            datamap.put(server, integer);
            getAllPlayerCounts(servers, i + 1, datamap);
        });
    }
}
