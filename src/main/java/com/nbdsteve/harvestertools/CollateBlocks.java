package com.nbdsteve.harvestertools;

import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class CollateBlocks {
    //Private hashmap to store the blocks & their values
    private HashMap<String, Double> blockList;
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register the provided files instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();

    public void calculateBlocks() {
        blockList = new HashMap<>();
        double price = 0;
        for(String m : lpf.getSell().getStringList("block-prices")) {
            String[] parts = m.split("-");
            try {
                price = Double.parseDouble(parts[1]);
            } catch (NumberFormatException e) {
                pl.getLogger().severe
                        ("In the sell.yml you have an invalid price for the block: " + parts[0] + "! Make sure the price is a valid double.");
            }
            blockList.put(parts[0].toUpperCase(), price);
        }
        pl.getLogger().info("Calculating all block prices...");
    }

    public HashMap getBlockList() {
        return blockList;
    }
}
