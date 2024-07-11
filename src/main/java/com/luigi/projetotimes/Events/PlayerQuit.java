package com.luigi.projetotimes.Events;

import com.luigi.projetotimes.ProjetoTime;
import com.luigi.projetotimes.Times.Time;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private ProjetoTime plugin;

    public PlayerQuit(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Time time : plugin.getTimes().values()) {
            if (time.removerJogador(player.getName())) {
                plugin.clearArmor(player);
                plugin.getTeamManager().removePlayerFromTeam(player);
            }
        }
        plugin.getScoreboardManager().updateScoreboard(player);
    }
}
