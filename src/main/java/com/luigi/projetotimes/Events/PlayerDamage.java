package com.luigi.projetotimes.Events;

import com.luigi.projetotimes.ProjetoTime;
import com.luigi.projetotimes.Times.Time;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamage implements Listener {

    private ProjetoTime plugin;

    public PlayerDamage(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player target = (Player) event.getEntity();

            Time damagerTeam = getPlayerTeam(damager);
            Time targetTeam = getPlayerTeam(target);

            if (damagerTeam != null && damagerTeam.equals(targetTeam)) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.GRAY+"Você não pode atacar membros do seu próprio time.");
            }
        }
    }

    private Time getPlayerTeam(Player player) {
        for (Time time : plugin.getTimes().values()) {
            if (time.getJogadores().contains(player.getName())) {
                return time;
            }
        }
        return null;
    }
}
