package com.luigi.projetotimes.Commands;

import com.luigi.projetotimes.ProjetoTime;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WhiterEffect implements CommandExecutor {

    private ProjetoTime plugin;

    public WhiterEffect(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return false;
        }

        int level;
        int duration;

        try {
            level = Integer.parseInt(args[0]);
            duration = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("O nível e o tempo devem ser números inteiros");
            return false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration * 20, level - 1));
            }
        }
        sender.sendMessage("Efeito aplicado nos players");
        return true;

    }
}
