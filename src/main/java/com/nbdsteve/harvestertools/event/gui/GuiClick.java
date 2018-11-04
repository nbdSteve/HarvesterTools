package com.nbdsteve.harvestertools.event.gui;

import com.nbdsteve.harvestertools.HarvesterTools;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiClick implements Listener {
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register LoadProvideFiles instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();
    //Get the server economy
    private Economy econ = HarvesterTools.getEconomy();

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory i = e.getClickedInventory();

        if (i != null) {
            if (i.getName()
                    .equals(ChatColor.translateAlternateColorCodes('&', lpf.getConfig().getString("gui.name")))) {
                e.setCancelled(true);

                ItemMeta toolMeta = e.getCurrentItem().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType = "";
                String ttool = "";
                String perm = "";
                NumberFormat df = new DecimalFormat("#,###");

                if (toolMeta.getDisplayName() != " ") {
                    if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-1-gui.unique")))) {
                        perm = "1";
                        ttool = "harvester-tool-1";
                        toolType = "harvester-tool-1-gui";
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-2-gui.unique")))) {
                        perm = "2";
                        ttool = "harvester-tool-2";
                        toolType = "harvester-tool-2-gui";
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-3-gui.unique")))) {
                        perm = "3";
                        ttool = "harvester-tool-3";
                        toolType = "harvester-tool-3-gui";
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-4-gui.unique")))) {
                        perm = "4";
                        ttool = "harvester-tool-4";
                        toolType = "harvester-tool-4-gui";
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-5-gui.unique")))) {
                        perm = "5";
                        ttool = "harvester-tool-5";
                        toolType = "harvester-tool-5-gui";
                    } else {
                        return;
                    }
                    if (p.hasPermission("harvester.gui." + perm)) {
                        if (p.getInventory().firstEmpty() != -1) {
                            double price = lpf.getHarvester().getDouble(toolType + ".price");

                            if (econ.getBalance(p) >= price) {
                                econ.withdrawPlayer(p, price);
                                ItemStack item = new ItemStack(
                                        Material.valueOf(lpf.getHarvester().getString(toolType + ".gui-item").toUpperCase()));
                                ItemMeta itemMeta = item.getItemMeta();
                                List<String> itemLore = new ArrayList<String>();

                                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        lpf.getHarvester().getString(ttool + ".name")));
                                for (String lore : lpf.getHarvester().getStringList(ttool + ".lore")) {
                                    String mode = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(ttool + ".harvest-mode"));
                                    itemLore.add(ChatColor.translateAlternateColorCodes('&', lore).replace("%mode%", mode));
                                }
                                for (String ench : lpf.getHarvester().getStringList(ttool + ".enchantments")) {
                                    String[] parts = ench.split("-");
                                    itemMeta.addEnchant(Enchantment.getByName(parts[0]), Integer.parseInt(parts[1]), true);
                                }
                                itemMeta.setLore(itemLore);
                                item.setItemMeta(itemMeta);
                                p.getInventory().addItem(item);
                                String newPrice = df.format(price);

                                for (String line : lpf.getMessages().getStringList("purchase")) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("%cost%",
                                            newPrice));
                                }
                                p.closeInventory();
                            } else {
                                String newPrice = df.format(price);
                                String newBal = df.format(econ.getBalance(p));

                                for (String line : lpf.getMessages().getStringList("insufficient-funds")) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', line)
                                            .replace("%bal%", newBal)
                                            .replace("%cost%", newPrice));
                                }
                                p.closeInventory();
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("inventory-full")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            p.closeInventory();
                        }
                    } else {
                        for (String line : lpf.getMessages().getStringList("no-buy-permission")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                        p.closeInventory();
                    }
                    return;
                }
                return;
            }
            return;
        }
        return;
    }
}
