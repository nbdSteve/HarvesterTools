package com.nbdsteve.harvestertools.event;

import com.nbdsteve.harvestertools.CollateBlocks;
import com.nbdsteve.harvestertools.HarvesterTools;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static org.bukkit.Material.CACTUS;
import static org.bukkit.Material.SUGAR_CANE;

public class BlockBreak implements Listener {
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register the provided files instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();
    //Register the collate blocks instance
    private CollateBlocks cb = ((HarvesterTools) pl).getBlocks();
    //Get the server economy
    Economy econ = HarvesterTools.getEconomy();

    public void onBreak(BlockBreakEvent e) {
        //Get the player
        Player p = e.getPlayer();
        //Check that the player has the harvestertool in their hand
        if (p.getInventory().getItemInHand().hasItemMeta()) {
            if (p.getInventory().getItemInHand().getItemMeta().hasLore()) {
                ItemMeta toolMeta = p.getInventory().getItemInHand().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType;
                //Get the level of harvester from the tool lore
                if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-1.unique")))) {
                    toolType = "harvester-tool-1";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-2.unique")))) {
                    toolType = "harvester-tool-2";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-3.unique")))) {
                    toolType = "harvester-tool-3";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-4.unique")))) {
                    toolType = "harvester-tool-4";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-5.unique")))) {
                    toolType = "harvester-tool-5";
                } else {
                    return;
                }
                //If the tool is in the correct mode this will be set to true
                boolean isSelling = false;
                //Store the strings for the mode that are provided in the harvester.yml
                String mode = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".mode-unique-id"));
                String sell = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".sell-mode"));
                String harvest = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".sell-mode"));
                /*
                 * Check to see if the lore contains the unique mode id. If it does then get the line that it is on,
                 * then reset the line with the unique mode id + the opposing mode (sell or harvest).
                 */
                for (int i = 0; i < toolMeta.getLore().size(); i++) {
                    String currentMode = toolMeta.getLore().get(i);
                    if (currentMode.contains(mode)) {
                        if (currentMode.contains(harvest)) {
                            isSelling = false;
                        } else if (toolLore.contains(sell)) {
                            isSelling = true;
                        }
                    }
                }
                if (cb.getBlockList().containsKey(e.getBlock())) {
                    //Store the price of the block
                    double price = (double) cb.getBlockList().get(e.getBlock());
                    if (e.getBlock().getType() == SUGAR_CANE) {
                        for (int i = 3; i >= 0; i--) {
                            Block check = e.getBlock().getRelative(0, i, 0);
                            if (check.getType() == SUGAR_CANE) {
                                check.setType(Material.AIR);
                                if (isSelling) {
                                    econ.depositPlayer(p, price);
                                    if (lpf.getHarvester().getBoolean("enable-sell-messages")) {
                                        for (String m : lpf.getMessages().getStringList("sell")) {
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', m).replace("%price%", String.valueOf(price)));
                                        }
                                    }
                                } else {
                                    p.getInventory().addItem(new ItemStack(SUGAR_CANE));
                                }
                            }
                        }
                    } else if (e.getBlock().getType() == CACTUS) {
                        for (int i = 3; i >= 0; i--) {
                            Block check = e.getBlock().getRelative(0, i, 0);
                            if (check.getType() == CACTUS) {
                                check.setType(Material.AIR);
                                if (isSelling) {
                                    econ.depositPlayer(p, price);
                                    if (lpf.getHarvester().getBoolean("enable-sell-messages")) {
                                        for (String m : lpf.getMessages().getStringList("sell")) {
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', m).replace("%price%", String.valueOf(price)));
                                        }
                                    }
                                } else {
                                    p.getInventory().addItem(new ItemStack(CACTUS));
                                }
                            }
                        }
                    } else {
                        if (isSelling) {
                            e.getBlock().getDrops().clear();
                            econ.depositPlayer(p, price);
                            if (lpf.getHarvester().getBoolean("enable-sell-messages")) {
                                for (String m : lpf.getMessages().getStringList("sell")) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', m).replace("%price%", String.valueOf(price)));
                                }
                            }
                        } else {
                            for (ItemStack item : e.getBlock().getDrops()) {
                                p.getInventory().addItem(item);
                            }
                            e.getBlock().getDrops().clear();
                        }
                    }
                }
            }
        }
    }
}