package com.luigi.projetotimes.Events;

import com.luigi.projetotimes.ProjetoTime;
import com.luigi.projetotimes.Times.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class InventoryClick implements Listener {

    private ProjetoTime plugin;

    public InventoryClick(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Escolha um Time")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.LEATHER_CHESTPLATE) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack chestplate = event.getCurrentItem();
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            String timeName = ChatColor.stripColor(meta.getDisplayName());

            Time time = plugin.getTimes().get(timeName);
            if (time == null) {
                player.sendMessage(ChatColor.RED + "Time não encontrado.");
                return;
            }

            if (time.getJogadores().size() >= time.getCapacidade()) {
                player.sendMessage(ChatColor.RED + "Este time está cheio.");
                return;
            }

            // Remover jogador de qualquer time em que ele esteja atualmente
            for (Time t : plugin.getTimes().values()) {
                t.removerJogador(player.getName());
            }

            if (!time.adicionarJogador(player.getName())) {
                player.sendMessage(ChatColor.RED + "Este time está cheio.");
                return;
            }

            int index = Integer.parseInt(timeName.replaceAll("\\D+", "")) - 1;
            Color color = meta.getColor();
            player.getInventory().setChestplate(createColoredArmor(Material.LEATHER_CHESTPLATE, color));
            player.getInventory().setLeggings(createColoredArmor(Material.LEATHER_LEGGINGS, color));
            player.getInventory().setBoots(createColoredArmor(Material.LEATHER_BOOTS, color));
            player.getInventory().setHelmet(createColoredArmor(Material.LEATHER_HELMET, color));
            player.sendMessage(ChatColor.GREEN + "Você entrou no " + timeName);
            plugin.getTeamManager().addPlayerToTeam(player, timeName);
            plugin.getScoreboardManager().updateScoreboard(player);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.CHEST && item.getItemMeta() != null && "Times".equals(item.getItemMeta().getDisplayName())) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                openTeamSelectionMenu(player);
                event.setCancelled(true);
            }
        }
    }

    public void openTeamSelectionMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Escolha um Time");

        for (Time time : plugin.getTimes().values()) {
            ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setDisplayName(time.getNome());
            // Configure a cor do peitoral de acordo com a cor do time
            meta.setColor(getColorFromTimeName(time.getNome()));
            item.setItemMeta(meta);
            inventory.addItem(item);
        }

        player.openInventory(inventory);
    }

    private Color getColorFromTimeName(String timeName) {
        int index = Integer.parseInt(timeName.replaceAll("\\D+", "")) - 1;
        Color[] colors = {
                Color.WHITE, Color.GRAY, Color.SILVER, Color.BLACK,
                Color.RED, Color.MAROON, Color.YELLOW, Color.ORANGE,
                Color.LIME, Color.GREEN, Color.AQUA, Color.TEAL,
                Color.BLUE, Color.NAVY, Color.FUCHSIA, Color.PURPLE
        };
        return colors[index % colors.length];
    }

    private ItemStack createColoredArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return item;
    }
}
