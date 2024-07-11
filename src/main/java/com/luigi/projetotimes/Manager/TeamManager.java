package com.luigi.projetotimes.Manager;

import com.luigi.projetotimes.ProjetoTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamManager {

    private ProjetoTime plugin;

    public TeamManager(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToTeam(Player player, String teamName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        Scoreboard scoreboard = manager.getMainScoreboard();
        Team team = scoreboard.getTeam("team_" + player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam("team_" + player.getName());
        }
        ChatColor color = getTeamColor(teamName);
        team.setPrefix(color + teamName + " - ");
        team.addEntry(player.getName());
    }

    public void removePlayerFromTeam(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        Scoreboard scoreboard = manager.getMainScoreboard();
        Team team = scoreboard.getTeam("team_" + player.getName());
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
    }

    private ChatColor getTeamColor(String teamName) {
        int index = Integer.parseInt(teamName.replaceAll("\\D+", "")) - 1;
        ChatColor[] chatColors = {
                ChatColor.WHITE, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLACK,
                ChatColor.RED, ChatColor.DARK_RED, ChatColor.YELLOW, ChatColor.GOLD,
                ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA,
                ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE,
                ChatColor.GOLD
        };
        return chatColors[index % chatColors.length];
    }
}
