package com.luigi.projetotimes.Commands;

import com.luigi.projetotimes.ProjetoTime;
import com.luigi.projetotimes.Times.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Commands implements CommandExecutor {

    private ProjetoTime plugin;

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

            List<Time> sortedTimes = new ArrayList<>(plugin.getTimes().values());
            sortedTimes.sort(Comparator.comparing(Time::getNome, new TimeNameComparator()));

            int totalItems = sortedTimes.size();
            int invSize = ((totalItems - 1) / 9 + 1) * 9;
            Inventory inv = Bukkit.createInventory(null, invSize, "Escolha um Time");

            for (int i = 0; i < sortedTimes.size(); i++) {
                Time time = sortedTimes.get(i);
                ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                Color color = getColor(i);
                meta.setColor(color);
                meta.setDisplayName(ChatColor.GREEN + time.getNome());

                // Adiciona a descrição com os jogadores do time
                List<String> lore = new ArrayList<>();
                for (String jogador : time.getJogadores()) {
                    lore.add(ChatColor.GRAY + " - " + jogador);
                }
                int vagasVazias = time.getCapacidade() - time.getJogadores().size();
                for (int j = 0; j < vagasVazias; j++) {
                    lore.add(ChatColor.GRAY + " - vazio");
                }
                meta.setLore(lore);

                item.setItemMeta(meta);
                inv.setItem(i, item);
            }

            player.openInventory(inv);

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

    private void updateNameTag(Player player, String timeName, ChatColor color) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        Scoreboard scoreboard = manager.getMainScoreboard();
        Team team = scoreboard.getTeam("team_" + player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam("team_" + player.getName());
        }
        team.setPrefix(color + timeName + " - ");
        team.addEntry(player.getName());
    }

    private Color getColor(int index) {
        Color[] colors = {
                Color.WHITE, Color.SILVER, Color.GRAY, Color.BLACK,
                Color.RED, Color.MAROON, Color.YELLOW, Color.OLIVE,
                Color.LIME, Color.GREEN, Color.AQUA, Color.TEAL,
                Color.BLUE, Color.NAVY, Color.FUCHSIA, Color.PURPLE,
                Color.ORANGE
        };
        return colors[index % colors.length];
    }

    private ChatColor getChatColor(int index) {
        ChatColor[] chatColors = {
                ChatColor.WHITE, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLACK,
                ChatColor.RED, ChatColor.DARK_RED, ChatColor.YELLOW, ChatColor.GOLD,
                ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA,
                ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE,
                ChatColor.GOLD
        };
        return chatColors[index % chatColors.length];
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
