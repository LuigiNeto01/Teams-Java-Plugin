package com.luigi.projetotimes.Events;

import com.luigi.projetotimes.ProjetoTime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {

    private ProjetoTime plugin;

    public PlayerJoinEventListener(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.giveTeamSelector(event.getPlayer());
    }
}
