package com.nbdsteve.harvestertools.event;

import com.nbdsteve.harvestertools.HarvesterTools;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Event called when the player right clicks on a block on in the air with their mouse,
 * the tool check is done at the start to reduce memory usage.
 */
public class ModeChange implements Listener {
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register the provided files instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //Get the player
            Player p = e.getPlayer();
            //Check that the player has the harvestertool in their hand
            if (p.getInventory().getItemInHand().hasItemMeta()) {
                if (p.getInventory().getItemInHand().getItemMeta().hasLore()) {
                    ItemMeta toolMeta = p.getInventory().getItemInHand().getItemMeta();
                    List<String> toolLore = toolMeta.getLore();
                    String toolType = null;
                    //Get the level of harvester from the tool lore
                    for (int i = 1; i < 10; i++) {
                        String tool = "harvester-tool-" + String.valueOf(i);
                        try {
                            lpf.getHarvester().getString(tool + ".unique");
                            if (toolLore.contains(ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(tool + ".unique")))) {
                                toolType = tool;
                            }
                        } catch (Exception ex) {
                            //Do nothing, this tool isn't active or doesn't exist
                        }
                    }
                    if (toolType == null) {
                        return;
                    }
                    //Store the strings for the mode that are provided in the harvester.yml
                    String mode = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".mode-unique-id"));
                    String sell = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".sell-mode"));
                    String harvest = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(toolType + ".harvest-mode"));
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
                            } else if (currentMode.contains(sell)) {
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