package com.nbdsteve.harvestertools;

import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class CollateBlocks {
    //Private hashmap to store the blocks & their values, 1 hashmap for each level
    private HashMap<String, Double> blockListTool1;
    private HashMap<String, Double> blockListTool2;
    private HashMap<String, Double> blockListTool3;
    private HashMap<String, Double> blockListTool4;
    private HashMap<String, Double> blockListTool5;
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register the provided files instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();

    public void calculateBlocks() {
        //Tedious but it works, can probably be improved
        blockListTool1 = new HashMap<>();
        blockListTool2 = new HashMap<>();
        blockListTool3 = new HashMap<>();
        blockListTool4 = new HashMap<>();
        blockListTool5 = new HashMap<>();
        double priceLevel1 = 0;
        double priceLevel2 = 0;
        double priceLevel3 = 0;
        double priceLevel4 = 0;
        double priceLevel5 = 0;
        for(String m : lpf.getBlocks().getStringList("blocks")) {
            String[] parts = m.split("-");
            try {
                priceLevel1 = Double.parseDouble(parts[1]);
                priceLevel2 = Double.parseDouble(parts[2]);
                priceLevel3 = Double.parseDouble(parts[3]);
                priceLevel4 = Double.parseDouble(parts[4]);
                priceLevel5 = Double.parseDouble(parts[5]);
            } catch (NumberFormatException e) {
                pl.getLogger().severe
                        ("In the blocks.yml you have an invalid price for the block: " + parts[0] + "! Make sure the price is a valid double.");
            }
            blockListTool1.put(parts[0].toUpperCase(), priceLevel1);
            blockListTool2.put(parts[0].toUpperCase(), priceLevel2);
            blockListTool3.put(parts[0].toUpperCase(), priceLevel3);
            blockListTool4.put(parts[0].toUpperCase(), priceLevel4);
            blockListTool5.put(parts[0].toUpperCase(), priceLevel5);
        }
        pl.getLogger().info("Calculating all block prices...");
    }

    public HashMap getBlockList(String level) {
        if (level.equalsIgnoreCase("1")) {
            return blockListTool1;
        } else if (level.equalsIgnoreCase("2"))  {
            return blockListTool2;
        } else if (level.equalsIgnoreCase("3"))  {
            return blockListTool3;
        } else if (level.equalsIgnoreCase("4"))  {
            return blockListTool4;
        } else if (level.equalsIgnoreCase("5"))  {
            return blockListTool5;
        }
        return blockListTool1;
    }
}
