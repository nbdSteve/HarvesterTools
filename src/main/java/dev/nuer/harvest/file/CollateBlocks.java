package dev.nuer.harvest.file;

import dev.nuer.harvest.HarvesterTools;
import dev.nuer.harvest.exception.InvalidLevelException;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * This is called when the plugin starts up. It creates 9 different hashmaps, one for each tool that contains
 * the block name and its corresponding price - these values are read from the blocks.yml file.
 */
public class CollateBlocks {
    //Private hashmap to store the blocks & their values, 1 hashmap for each level
    private HashMap<String, Double> blockListTool1;
    private HashMap<String, Double> blockListTool2;
    private HashMap<String, Double> blockListTool3;
    private HashMap<String, Double> blockListTool4;
    private HashMap<String, Double> blockListTool5;
    private HashMap<String, Double> blockListTool6;
    private HashMap<String, Double> blockListTool7;
    private HashMap<String, Double> blockListTool8;
    private HashMap<String, Double> blockListTool9;
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register the provided files instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();

    /**
     * Calculate the block prices for each tool.
     */
    public void calculateBlocks() {
        //Tedious but it works, can probably be improved
        blockListTool1 = new HashMap<>();
        blockListTool2 = new HashMap<>();
        blockListTool3 = new HashMap<>();
        blockListTool4 = new HashMap<>();
        blockListTool5 = new HashMap<>();
        blockListTool6 = new HashMap<>();
        blockListTool7 = new HashMap<>();
        blockListTool8 = new HashMap<>();
        blockListTool9 = new HashMap<>();
        double priceLevel1 = 0;
        double priceLevel2 = 0;
        double priceLevel3 = 0;
        double priceLevel4 = 0;
        double priceLevel5 = 0;
        double priceLevel6 = 0;
        double priceLevel7 = 0;
        double priceLevel8 = 0;
        double priceLevel9 = 0;
        for (String m : lpf.getBlocks().getStringList("blocks")) {
            String[] parts = m.split("-");
            try {
                priceLevel1 = Double.parseDouble(parts[1]);
                priceLevel2 = Double.parseDouble(parts[2]);
                priceLevel3 = Double.parseDouble(parts[3]);
                priceLevel4 = Double.parseDouble(parts[4]);
                priceLevel5 = Double.parseDouble(parts[5]);
                priceLevel6 = Double.parseDouble(parts[6]);
                priceLevel7 = Double.parseDouble(parts[7]);
                priceLevel8 = Double.parseDouble(parts[8]);
                priceLevel9 = Double.parseDouble(parts[9]);
            } catch (NumberFormatException e) {
                pl.getLogger().severe
                        ("In the blocks.yml you have an invalid price for the block: " + parts[0] + "! Make sure the price is a valid double.");
            }
            blockListTool1.put(parts[0].toUpperCase(), priceLevel1);
            blockListTool2.put(parts[0].toUpperCase(), priceLevel2);
            blockListTool3.put(parts[0].toUpperCase(), priceLevel3);
            blockListTool4.put(parts[0].toUpperCase(), priceLevel4);
            blockListTool5.put(parts[0].toUpperCase(), priceLevel5);
            blockListTool6.put(parts[0].toUpperCase(), priceLevel6);
            blockListTool7.put(parts[0].toUpperCase(), priceLevel7);
            blockListTool8.put(parts[0].toUpperCase(), priceLevel8);
            blockListTool9.put(parts[0].toUpperCase(), priceLevel9);
        }
        pl.getLogger().info("Calculating block prices...");
    }

    /**
     * Get the hashmap for that level of harvester
     *
     * @param level string, must not be null.
     * @return the hashmap for that tool, if the level is not specified return null.
     * @throws InvalidLevelException throws if the level of tool is invalid.
     */

    public HashMap getBlockList(String level) throws InvalidLevelException {
        //This is an easy way to determine which price to use
        if (level.equalsIgnoreCase("1")) {
            return blockListTool1;
        } else if (level.equalsIgnoreCase("2")) {
            return blockListTool2;
        } else if (level.equalsIgnoreCase("3")) {
            return blockListTool3;
        } else if (level.equalsIgnoreCase("4")) {
            return blockListTool4;
        } else if (level.equalsIgnoreCase("5")) {
            return blockListTool5;
        } else if (level.equalsIgnoreCase("6")) {
            return blockListTool6;
        } else if (level.equalsIgnoreCase("7")) {
            return blockListTool7;
        } else if (level.equalsIgnoreCase("8")) {
            return blockListTool8;
        } else if (level.equalsIgnoreCase("9")) {
            return blockListTool9;
        } else {
            throw new InvalidLevelException("Internal Error. The level of harvester is invalid - please contact the developer.");
        }
    }
}
