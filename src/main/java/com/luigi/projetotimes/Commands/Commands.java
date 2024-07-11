package com.luigi.projetotimes.Commands;

import com.luigi.projetotimes.ProjetoTime;
import com.luigi.projetotimes.Times.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Commands implements CommandExecutor {

    private final ProjetoTime plugin;

    public Commands(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por um jogador.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Uso incorreto do comando. Uso correto: /times <criar|list|reset|entrar|sair>.");
            return true;
        }

        if (args[0].equalsIgnoreCase("criar")) {
            if (args.length != 3) {
                player.sendMessage(ChatColor.RED + "Uso incorreto do comando. Uso correto: /times criar <número de times> <jogadores por time>");
                return true;
            }

            int numeroDeTimes;
            int jogadoresPorTime;

            try {
                numeroDeTimes = Integer.parseInt(args[1]);
                jogadoresPorTime = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Os argumentos devem ser números.");
                return true;
            }

            int startingIndex = plugin.getTimes().size() + 1;

            for (int i = 0; i < numeroDeTimes; i++) {
                String nomeDoTime = "Time" + (startingIndex + i);
                plugin.getTimes().put(nomeDoTime, new Time(nomeDoTime, jogadoresPorTime));
                player.sendMessage(ChatColor.GREEN + "Time " + nomeDoTime + " criado com capacidade para " + jogadoresPorTime + " jogadores.");
            }

        } else if (args[0].equalsIgnoreCase("list")) {
            if (plugin.getTimes().isEmpty()) {
                player.sendMessage(ChatColor.RED + "Não há times criados.");
                return true;
            }

            List<Time> sortedTimes = new ArrayList<>(plugin.getTimes().values());
            sortedTimes.sort(Comparator.comparing(Time::getNome, new TimeNameComparator()));

            for (Time time : sortedTimes) {
                player.sendMessage(ChatColor.GREEN + "-> " + time.getNome());
                int vagasVazias = time.getCapacidade() - time.getJogadores().size();
                for (String jogador : time.getJogadores()) {
                    player.sendMessage(ChatColor.GRAY + " - " + jogador);
                }
                for (int i = 0; i < vagasVazias; i++) {
                    player.sendMessage(ChatColor.GRAY + " - vazio");
                }
            }

        } else if (args[0].equalsIgnoreCase("reset")) {
            resetAllPlayers();
            player.sendMessage(ChatColor.GREEN + "Todos os times foram resetados.");

        } else if (args[0].equalsIgnoreCase("entrar")) {
            if (plugin.getTimes().isEmpty()) {
                player.sendMessage(ChatColor.RED + "Não há times criados.");
                return true;
            }

            plugin.getInventoryClick().openTeamSelectionMenu(player, 1);

        } else if (args[0].equalsIgnoreCase("sair")) {
            for (Time time : plugin.getTimes().values()) {
                if (time.removerJogador(player.getName())) {
                    clearArmor(player);
                    player.sendMessage(ChatColor.GREEN + "Você saiu do " + time.getNome());
                    resetNameTag(player);
                    return true;
                }
            }
            player.sendMessage(ChatColor.RED + "Você não está em nenhum time.");
        } else {
            player.sendMessage(ChatColor.RED + "Uso incorreto do comando. Uso correto: /times <criar|list|reset|entrar|sair>.");
        }

        return true;
    }

    private void clearArmor(Player player) {
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
    }

    private void resetNameTag(Player player) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        org.bukkit.scoreboard.Scoreboard scoreboard = manager.getMainScoreboard();
        org.bukkit.scoreboard.Team team = scoreboard.getTeam("team_" + player.getName());
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
    }

    private void resetAllPlayers() {
        for (Time time : plugin.getTimes().values()) {
            for (String jogador : time.getJogadores()) {
                Player p = Bukkit.getPlayer(jogador);
                if (p != null) {
                    clearArmor(p);
                    resetNameTag(p);
                }
            }
        }
        plugin.getTimes().clear();
    }

    private static class TimeNameComparator implements Comparator<String> {
        @Override
        public int compare(String time1, String time2) {
            int num1 = Integer.parseInt(time1.replaceAll("\\D+", ""));
            int num2 = Integer.parseInt(time2.replaceAll("\\D+", ""));
            return Integer.compare(num1, num2);
        }
    }
}
