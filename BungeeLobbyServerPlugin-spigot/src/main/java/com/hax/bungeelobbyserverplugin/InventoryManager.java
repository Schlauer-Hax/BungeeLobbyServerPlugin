package com.hax.bungeelobbyserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryManager implements InventoryHolder, Listener {

    Inventory inventory;
    Plugin plugin;
    BungeeChannelApi bungeeChannelApi;

    public InventoryManager(Plugin plugin, BungeeChannelApi bungeeChannelApi) {
        this.plugin = plugin;
        this.bungeeChannelApi = bungeeChannelApi;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openNavigator(List<String> serverlist, HashMap<String, Integer> datamap) {
        this.inventory = Bukkit.createInventory(this, 9, "Servers");

        List<ItemStack> items = new ArrayList<>();
        serverlist.forEach(server -> {
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(server);
            meta.setLore(Arrays.asList(datamap.get(server) + " Players online."));

            item.setItemMeta(meta);
            items.add(item);
        });

        this.inventory = orderInventory(items);

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.openInventory(this.inventory);
        });
    }

    public Inventory orderInventory(List<ItemStack> contents) {
        // 2 - 0 0 0 1 0 1 0 0 0 - 3
        // 4 - 0 0 1 1 0 1 1 0 0 - 2
        // 6 - 0 1 1 1 0 1 1 1 0 - 1
        // 8 - 1 1 1 1 0 1 1 1 1 - 0
        // (8-x)/2

        // 1 - 0 0 0 0 1 0 0 0 0 - 4
        // 3 - 0 0 0 1 1 1 0 0 0 - 3
        // 5 - 0 0 1 1 1 1 1 0 0 - 2
        // 7 - 0 1 1 1 1 1 1 1 0 - 1
        // 9 - 1 1 1 1 1 1 1 1 1 - 0
        // (9-x) / 2

        String order;
        if (contents.size() % 2 == 0) {
            int lr = (8-contents.size())/2;
            order = "0".repeat(lr)+"1".repeat(4-lr)+"0"+"1".repeat(4-lr)+"0".repeat(lr);
        } else {
            int lr = (9-contents.size())/2;
            order = "0".repeat(lr)+"1".repeat(contents.size())+"0".repeat(lr);
        }

        for (int i = 0; i<order.length(); i++) {
            if (order.split("")[i].equals("1")) {
                inventory.setItem(i, contents.get(0));
                contents.remove(0);
            }
        }
        return inventory;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != this.inventory) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        Player p = (Player) e.getWhoClicked();

        this.bungeeChannelApi.connect(p, e.getCurrentItem().getItemMeta().getDisplayName());
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().equals(this.inventory)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

    }
}
