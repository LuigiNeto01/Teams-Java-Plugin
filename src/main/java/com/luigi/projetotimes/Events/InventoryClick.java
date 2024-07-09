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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryClick implements Listener {

    private final ProjetoTime plugin;

    public InventoryClick(ProjetoTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Escolha um Time - Página")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            ItemMeta meta = clickedItem.getItemMeta();

            if (meta != null && meta.getDisplayName() != null) {
                if (meta.getDisplayName().equals(ChatColor.GOLD + "Próxima Página")) {
                    openTeamSelectionMenu(player, getCurrentPage(event.getView().getTitle()) + 1);
                    return;
                } else if (meta.getDisplayName().equals(ChatColor.GOLD + "Página Anterior")) {
                    openTeamSelectionMenu(player, getCurrentPage(event.getView().getTitle()) - 1);
                    return;
                }
            }

            if (clickedItem.getType() == Material.LEATHER_CHESTPLATE && meta != null) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                String timeName = ChatColor.stripColor(leatherMeta.getDisplayName());

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

                Color color = leatherMeta.getColor();
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
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.CHEST && item.getItemMeta() != null && "Times".equals(item.getItemMeta().getDisplayName())) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                openTeamSelectionMenu(player, 1);
                event.setCancelled(true);
            }
        }
    }

    public void openTeamSelectionMenu(Player player, int page) {
        int itemsPerPage = 21;
        List<Time> sortedTimes = plugin.getTimes().values().stream().sorted((t1, t2) -> {
            int num1 = Integer.parseInt(t1.getNome().replaceAll("\\D+", ""));
            int num2 = Integer.parseInt(t2.getNome().replaceAll("\\D+", ""));
            return Integer.compare(num1, num2);
        }).collect(Collectors.toList());

        int totalItems = sortedTimes.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        if (page < 1 || page > totalPages) {
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "Escolha um Time - Página " + page);

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, totalItems);

        for (int i = start; i < end; i++) {
            Time time = sortedTimes.get(i);
            ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + time.getNome());
            meta.setColor(getColorFromTimeName(time.getNome()));

            // Adiciona a descrição com os jogadores do time
            List<String> lore = time.getJogadores().stream().map(jogador -> ChatColor.GRAY + " - " + jogador).collect(Collectors.toList());
            int vagasVazias = time.getCapacidade() - time.getJogadores().size();
            for (int j = 0; j < vagasVazias; j++) {
                lore.add(ChatColor.GRAY + " - vazio");
            }
            meta.setLore(lore);

            item.setItemMeta(meta);

            // Adiciona o item no inventário conforme o layout
            int row = ((i - start) / 7) + 1; // Pula a primeira linha
            int col = (i - start) % 7;
            int slot = row * 9 + col + 1;
            inventory.setItem(slot, item);
        }

        // Adiciona botões de navegação
        if (page > 1) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta previousMeta = previousPage.getItemMeta();
            previousMeta.setDisplayName(ChatColor.GOLD + "Página Anterior");
            previousPage.setItemMeta(previousMeta);
            inventory.setItem(45, previousPage);
        }

        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GOLD + "Próxima Página");
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }

        player.openInventory(inventory);
    }

    private int getCurrentPage(String title) {
        try {
            String[] parts = title.split(" ");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return 1;
        }
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
