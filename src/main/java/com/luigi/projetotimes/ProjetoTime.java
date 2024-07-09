package com.luigi.projetotimes;

import com.luigi.projetotimes.Commands.Commands;
import com.luigi.projetotimes.Commands.WhiterEffect;
import com.luigi.projetotimes.Events.*;
import com.luigi.projetotimes.Manager.TeamManager;
import com.luigi.projetotimes.ScoreBoard.ScoreboardManager;
import com.luigi.projetotimes.Times.Time;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class ProjetoTime extends JavaPlugin implements Listener {

    private Map<String, Time> times = new HashMap<>();
    private Map<String, Integer> playerKills = new HashMap<>();
    private ScoreboardManager scoreboardManager;
    private TeamManager teamManager;
    private InventoryClick inventoryClick; // Adiciona essa linha

    @Override
    public void onEnable() {
        this.getCommand("times").setExecutor(new Commands(this));
        this.getCommand("wither").setExecutor(new WhiterEffect(this));
        getServer().getPluginManager().registerEvents(new InventoryClick(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamage(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
        getServer().getPluginManager().registerEvents(new PlayerKillEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);

        scoreboardManager = new ScoreboardManager(this);
        teamManager = new TeamManager(this);
        inventoryClick = new InventoryClick(this); // Adiciona essa linha

        // Atualiza o scoreboard para todos os jogadores quando o plugin é ativado
        scoreboardManager.updateAllScoreboards();

        // Agendar tarefa para atualizar o scoreboard a cada 5 segundos
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                scoreboardManager.updateAllScoreboards();
            }
        }, 0L, 100L); // 100 ticks = 5 segundos
    }

    @Override
    public void onDisable() {
        resetAllPlayers();
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void giveTeamSelector(Player player) {
        ItemStack teamSelector = new ItemStack(Material.CHEST);
        ItemMeta meta = teamSelector.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("Times");
            teamSelector.setItemMeta(meta);
        }
        player.getInventory().setItem(8, teamSelector);
    }

    public void updateTeamSelectionMenu() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equals("Escolha um Time")) {
                inventoryClick.openTeamSelectionMenu(player, 1); // Corrige essa linha
            }
        }
    }

    public Map<String, Time> getTimes() {
        return times;
    }

    public int getPlayerKills(Player player) {
        return playerKills.getOrDefault(player.getName(), 0);
    }

    public void setPlayerKills(Player player, int kills) {
        playerKills.put(player.getName(), kills);
    }

    public void resetAllPlayers() {
        for (Time time : times.values()) {
            for (String jogador : time.getJogadores()) {
                Player player = Bukkit.getPlayer(jogador);
                if (player != null) {
                    clearArmor(player);
                    teamManager.removePlayerFromTeam(player);
                    playerKills.put(player.getName(), 0);
                }
            }
        }
        times.clear();
    }

    public void clearArmor(Player player) {
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setHelmet(null);
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public InventoryClick getInventoryClick() { // Adiciona este método getter
        return inventoryClick;
    }
}
