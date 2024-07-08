package com.luigi.projetotimes.Events;

import com.luigi.projetotimes.ProjetoTime;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    private ProjetoTime plugin;

    public PlayerDeathListener(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        for (ItemStack item : event.getDrops()) {
            if (item.getType() == Material.CHEST && "Times".equals(item.getItemMeta().getDisplayName())) {
                item.setAmount(0);
            }
        }
        plugin.giveTeamSelector(player);
    }
}
