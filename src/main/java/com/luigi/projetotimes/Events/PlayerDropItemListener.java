package com.luigi.projetotimes.Events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.CHEST
                && "Times".equals(event.getItemDrop().getItemStack().getItemMeta().getDisplayName())) {
            event.setCancelled(true);
        }
    }
}
