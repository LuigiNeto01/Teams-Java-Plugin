package com.luigi.projetotimes.Events;

import com.luigi.projetotimes.ProjetoTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillEvent implements Listener {

    private ProjetoTime plugin;

    public PlayerKillEvent(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        if (killed.getKiller() instanceof Player) {
            Player killer = killed.getKiller();
            int kills = plugin.getPlayerKills(killer) + 1;
            plugin.setPlayerKills(killer, kills);
            plugin.getScoreboardManager().updateScoreboard(killer);
        }
        plugin.getScoreboardManager().updateScoreboard(killed);
    }
}
