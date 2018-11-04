package com.nbdsteve.harvestertools.event;

import com.nbdsteve.harvestertools.CollateBlocks;
import com.nbdsteve.harvestertools.HarvesterTools;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static org.bukkit.Material.CACTUS;
import static org.bukkit.Material.LEGACY_SUGAR_CANE_BLOCK;
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

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        //Get the player
        Player p = e.getPlayer();
        //Check that the player has the harvestertool in their hand
        if (p.getInventory().getItemInHand().hasItemMeta()) {
            if (p.getInventory().getItemInHand().getItemMeta().hasLore()) {
                ItemMeta toolMeta = p.getInventory().getItemInHand().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType;
                String level;
                //Get the level of harvester from the tool lore
                if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-1.unique")))) {
                    toolType = "harvester-tool-1";
                    level = "1";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-2.unique")))) {
                    toolType = "harvester-tool-2";
                    level = "2";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-3.unique")))) {
                    toolType = "harvester-tool-3";
                    level = "3";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-4.unique")))) {
                    toolType = "harvester-tool-4";
                    level = "4";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString("harvester-tool-5.unique")))) {
                    toolType = "harvester-tool-5";
                    level = "5";
                } else {
                    return;
                }
                //If the tool is in the correct mode this will be set to true
                boolean isSelling = false;
                //Store the strings for the mode that are provided in the harvester.yml
                String mode = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".mode-unique-id"));
                String sell = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".sell-mode"));
                String harvest = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".harvest-mode"));
                /*
                 * Check to see if the lore contains the unique mode id. If it does then get the line that it is on,
                 * then set the selling boolean to true or false.
                 */
                for (int i = 0; i < toolMeta.getLore().size(); i++) {
                    String currentMode = toolMeta.getLore().get(i);
                    if (currentMode.contains(mode)) {
                        if (currentMode.contains(harvest)) {
                            isSelling = false;
                        } else if (currentMode.contains(sell)) {
                            isSelling = true;
                        }
                    }
                }
                if (cb.getBlockList(level).containsKey(e.getBlock().getType().toString())) {
                    e.setCancelled(true);
                    //Store the price of the block
                    double price = (double) cb.getBlockList(level).get(e.getBlock().getType().toString());
                    //The sugar cane is so fucking stupid with the new spigot API, it needs to be like this otherwise it wont work properly
                    if (e.getBlock().getType().toString().equalsIgnoreCase("SUGAR_CANE_BLOCK") ||
                            e.getBlock().getType().equals(Material.SUGAR_CANE)) {
                        //For the blocks that break from the bottom need to check above for more blocks and break those
                        for (int i = 4; i >= 0; i--) {
                            Block check = e.getBlock().getRelative(0, i, 0);
                            if (check.getType().toString().equalsIgnoreCase("SUGAR_CANE_BLOCK") ||
                                    e.getBlock().getType().equals(Material.SUGAR_CANE)) {
                                check.getDrops().clear();
                                check.setType(Material.AIR);
                                if (isSelling) {
                                    econ.depositPlayer(p, price);
                                    for (String m : lpf.getMessages().getStringList("sell")) {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m).replace("%price%", String.valueOf(price)));
                                    }
                                } else {
                                    p.getInventory().addItem(new ItemStack(Material.SUGAR_CANE));
                                }
                            }
                        }
                    } else if (e.getBlock().getType().equals(Material.CACTUS)) {
                        for (int i = 4; i >= 0; i--) {
                            Block check = e.getBlock().getRelative(0, i, 0);
                            if (check.getType().equals(Material.CACTUS)) {
                                check.getDrops().clear();
                                check.setType(Material.AIR);
                                if (isSelling) {
                                    econ.depositPlayer(p, price);
                                    for (String m : lpf.getMessages().getStringList("sell")) {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m).replace("%price%", String.valueOf(price)));
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
                            for (String m : lpf.getMessages().getStringList("sell")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', m).replace("%price%", String.valueOf(price)));
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