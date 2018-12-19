package com.nbdsteve.harvestertools.event;

import com.nbdsteve.harvestertools.exception.InvalidLevelException;
import com.nbdsteve.harvestertools.file.CollateBlocks;
import com.nbdsteve.harvestertools.HarvesterTools;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import com.nbdsteve.harvestertools.support.Factions;
import com.nbdsteve.harvestertools.support.MassiveCore;
import com.nbdsteve.harvestertools.support.WorldGuard;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

/**
 * Event called when the player breaks a block, most of the code is not executed unless they are using the
 * tool. The tool check is done first to reduce memory usage.
 */
public class BlockBreak implements Listener {
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register the provided files instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();
    //Register the collate blocks instance
    private CollateBlocks cb = ((HarvesterTools) pl).getBlocks();
    //Get the server economy
    private Economy econ = HarvesterTools.getEconomy();

    /**
     * All code for the event is store in this method.
     *
     * @param e the event, cannot be null.
     * @throws InvalidLevelException thrown if the level of tool is invalid.
     */
    @EventHandler
    public void onBreak(BlockBreakEvent e) throws InvalidLevelException {
        //Get the player
        Player p = e.getPlayer();
        //Check that the player has the harvestertool in their hand
        if (p.getInventory().getItemInHand().hasItemMeta()) {
            if (p.getInventory().getItemInHand().getItemMeta().hasLore()) {
                ItemMeta toolMeta = p.getInventory().getItemInHand().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType = null;
                String level = null;
                //Get the level of harvester from the tool lore
                for (int i = 1; i < 10; i++) {
                    String tool = "harvester-tool-" + String.valueOf(i);
                    try {
                        lpf.getHarvester().getString(tool + ".unique");
                        if (toolLore.contains(ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(tool + ".unique")))) {
                            toolType = tool;
                            level = String.valueOf(i);
                        }
                    } catch (Exception ex) {
                        //Do nothing, this tool isn't active or doesn't exist
                    }
                }
                if (toolType == null) {
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
                boolean wg = false;
                boolean fac = false;
                //Figure out which plugins are being used and what to support
                if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
                    wg = true;
                    if (!WorldGuard.allowsBreak(e.getBlock().getLocation())) {
                        e.setCancelled(true);
                        return;
                    }
                }
                if (Bukkit.getPluginManager().getPlugin("MassiveCore") != null) {
                    MassiveCore.canBreakBlock(p, e.getBlock());
                    fac = true;
                    if (!MassiveCore.canBreakBlock(p, e.getBlock())) {
                        e.setCancelled(true);
                        return;
                    }
                } else if (Bukkit.getServer().getPluginManager().getPlugin("Factions") != null) {
                    fac = true;
                    if (!Factions.canBreakBlock(p, e.getBlock())) {
                        e.setCancelled(true);
                        return;
                    }
                }
                if (cb.getBlockList(level).containsKey(e.getBlock().getType().toString())) {
                    e.setCancelled(true);
                    //Store the price of the block
                    double price = (double) cb.getBlockList(level).get(e.getBlock().getType().toString());
                    //Store the blocks location
                    int x = e.getBlock().getX();
                    int z = e.getBlock().getZ();
                    int y = e.getBlock().getY();
                    int height = y;
                    //The sugar cane item is annoying with the new spigot API, it needs to be like this otherwise it wont work properly
                    if (e.getBlock().getType().toString().equalsIgnoreCase("SUGAR_CANE_BLOCK") ||
                            e.getBlock().getType().equals(Material.SUGAR_CANE)) {
                        //Get the total height of the sugar cane and store it
                        while (p.getWorld().getBlockAt(x, height, z).getType().toString().equalsIgnoreCase("SUGAR_CANE_BLOCK") && height < 256) {
                            height++;
                        }
                        //Subtract one because that is how the while loop works
                        height--;
                        //Loop for all of the blocks and sell / harvest them, because we know they are sugar cane
                        for (int i = y; i <= height; height--) {
                            Block check = p.getWorld().getBlockAt(x, height, z);
                            if (wg && !WorldGuard.allowsBreak(check.getLocation())) {
                                //Do nothing just return to the start of the loop
                            } else if (fac && !Factions.canBreakBlock(p, check)) {
                                //Do nothing just return to the start of the loop
                            } else {
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
                        //Get the total height of the sugar cane and store it
                        while (p.getWorld().getBlockAt(x, height, z).getType().equals(Material.CACTUS) && height < 256) {
                            height++;
                        }
                        //Subtract one because that is how the while loop works
                        height--;
                        //Loop for all of the blocks and sell / harvest them, because we know they are sugar cane
                        for (int i = y; i <= height; height--) {
                            Block check = p.getWorld().getBlockAt(x, height, z);
                            if (wg && !WorldGuard.allowsBreak(check.getLocation())) {
                                //Do nothing just return to the start of the loop
                            } else if (fac && !Factions.canBreakBlock(p, check)) {
                                //Do nothing just return to the start of the loop
                            } else {
                                check.getDrops().clear();
                                check.setType(Material.AIR);
                                if (isSelling) {
                                    econ.depositPlayer(p, price);
                                    for (String m : lpf.getMessages().getStringList("sell")) {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m).replace("%price%", String.valueOf(price)));
                                    }
                                } else {
                                    p.getInventory().addItem(new ItemStack(Material.CACTUS));
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