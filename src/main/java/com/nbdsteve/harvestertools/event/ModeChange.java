package com.nbdsteve.harvestertools.event;

import com.nbdsteve.harvestertools.HarvesterTools;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ModeChange implements Listener {
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register the provided files instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();

    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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
                                //Changing the line of our lore
                                toolLore.set(i, (mode + " " + sell));
                                toolMeta.setLore(toolLore);
                                //Updating the lore for the players item
                                p.getItemInHand().setItemMeta(toolMeta);
                                for (String m : lpf.getMessages().getStringList("change-to-sell-mode")) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
                                }
                            } else if (toolLore.contains(sell)) {
                                //Changing the line of our lore
                                toolLore.set(i, (mode + " " + harvest));
                                toolMeta.setLore(toolLore);
                                //Updating the lore for the players item
                                p.getItemInHand().setItemMeta(toolMeta);
                                for (String m : lpf.getMessages().getStringList("change-to-harvest-mode")) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}