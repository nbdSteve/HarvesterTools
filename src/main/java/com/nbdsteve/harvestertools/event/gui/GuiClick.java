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

/**
 * Event called when the player clicks inside the Merchant Gui for the plugin.
 */
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
        //Store the player
        Player p = (Player) e.getWhoClicked();
        //Store the inventory
        Inventory i = e.getClickedInventory();
        //Check that the inventory clicked was this inventory
        if (i != null) {
            if (i.getName()
                    .equals(ChatColor.translateAlternateColorCodes('&', lpf.getConfig().getString("gui.name")))) {
                e.setCancelled(true);
                //Store the details about the clicked item
                ItemMeta toolMeta = e.getCurrentItem().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType;
                String ttool;
                String perm;
                NumberFormat df = new DecimalFormat("#,###");
                //Check to see if it is a harvester tool
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
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-6-gui.unique")))) {
                        perm = "6";
                        ttool = "harvester-tool-6";
                        toolType = "harvester-tool-6-gui";
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-7-gui.unique")))) {
                        perm = "7";
                        ttool = "harvester-tool-7";
                        toolType = "harvester-tool-7-gui";
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-8-gui.unique")))) {
                        perm = "8";
                        ttool = "harvester-tool-8";
                        toolType = "harvester-tool-8-gui";
                    } else if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                            lpf.getHarvester().getString("harvester-tool-9-gui.unique")))) {
                        perm = "9";
                        ttool = "harvester-tool-9";
                        toolType = "harvester-tool-9-gui";
                    } else {
                        return;
                    }
                    //Verify that the player has permission to buy that tool
                    if (p.hasPermission("harvester.gui." + perm)) {
                        if (p.getInventory().firstEmpty() != -1) {
                            double price = lpf.getHarvester().getDouble(toolType + ".price");
                            //Check that the player has enough money to buy the tool
                            if (econ.getBalance(p) >= price) {
                                econ.withdrawPlayer(p, price);
                                //Create the item being bought
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
                                //Give the player the item
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
                }
            }
        }
    }
}