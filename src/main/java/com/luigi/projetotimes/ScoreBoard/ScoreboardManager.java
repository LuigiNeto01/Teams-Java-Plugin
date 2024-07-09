package com.luigi.projetotimes.ScoreBoard;

import com.luigi.projetotimes.ProjetoTime;
import com.luigi.projetotimes.Times.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardManager {

    private ProjetoTime plugin;

    public ScoreboardManager(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(Player player) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            plugin.getLogger().severe("ScoreboardManager is null");
            return;
        }
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD + "SpartaMC");

        int score = 15;

        // Time do jogador
        String timeName = getPlayerTime(player.getName());
        objective.getScore(ChatColor.BLUE + "Time: " + (timeName != null ? timeName : "Sem time")).setScore(score--);

        // Jogadores do time
        objective.getScore(" ").setScore(score--); // Espaço vazio
        objective.getScore(ChatColor.BLUE + "Jogadores do time:").setScore(score--);
        Time playerTime = plugin.getTimes().get(timeName);
        if (playerTime != null) {
            List<String> jogadores = playerTime.getJogadores();
            for (int i = 0; i < playerTime.getCapacidade(); i++) {
                String nomeJogador = i < jogadores.size() ? jogadores.get(i) : "vazio";
                int kills = i < jogadores.size() ? plugin.getPlayerKills(Bukkit.getPlayer(jogadores.get(i))) : 0;
                objective.getScore(ChatColor.GRAY + " - " + nomeJogador + ": " + kills).setScore(score--);
            }
        } else {
            objective.getScore(ChatColor.GRAY + " - vazio").setScore(score--);
        }

        // Ranking dos times
        objective.getScore("  ").setScore(score--); // Espaço vazio
        objective.getScore(ChatColor.BLUE + "Ranking dos times:").setScore(score--);
        List<Map.Entry<String, Integer>> sortedTimes = getSortedTimes();
        for (int i = 0; i < 3 && i < sortedTimes.size(); i++) {
            Map.Entry<String, Integer> entry = sortedTimes.get(i);
            if(i==0){objective.getScore(ChatColor.GRAY + " - " + entry.getKey() + ": " + ChatColor.YELLOW +entry.getValue()).setScore(score--);}
            if(i==1){objective.getScore(ChatColor.GRAY + " - " + entry.getKey() + ": " + ChatColor.GRAY +entry.getValue()).setScore(score--);}
            if(i==2){objective.getScore(ChatColor.GRAY + " - " + entry.getKey() + ": " + ChatColor.GOLD +entry.getValue()).setScore(score--);}

        }

        player.setScoreboard(scoreboard);
    }

    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    private String getPlayerTime(String playerName) {
        for (Time time : plugin.getTimes().values()) {
            if (time.getJogadores().contains(playerName)) {
                return time.getNome();
            }
        }
        return null;
    }

    private List<Map.Entry<String, Integer>> getSortedTimes() {
        Map<String, Integer> timeKills = new HashMap<>();
        for (Map.Entry<String, Time> entry : plugin.getTimes().entrySet()) {
            int totalKills = entry.getValue().getJogadores().stream()
                    .mapToInt(jogador -> plugin.getPlayerKills(Bukkit.getPlayer(jogador)))
                    .sum();
            timeKills.put(entry.getKey(), totalKills);
        }
        return timeKills.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }
}
