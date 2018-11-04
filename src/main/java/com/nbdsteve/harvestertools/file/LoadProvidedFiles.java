package com.nbdsteve.harvestertools.file;

import com.nbdsteve.harvestertools.HarvesterTools;
import com.nbdsteve.harvestertools.file.providedFile.GenerateProvidedFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class LoadProvidedFiles {
    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //HashMap to store the files
    private HashMap<Files, GenerateProvidedFile> fileList;

    /**
     * Enum to store each file, this is public so we can call methods on these
     */
    public enum Files {
        CONFIG, MESSAGES, HARVESTER, SELL
    }

    /**
     * Generate all of the files in the enum
     */
    public LoadProvidedFiles() {
        fileList = new HashMap<Files, GenerateProvidedFile>();
        fileList.put(Files.CONFIG, new GenerateProvidedFile("config.yml"));
        fileList.put(Files.MESSAGES, new GenerateProvidedFile("messages.yml"));
        fileList.put(Files.HARVESTER, new GenerateProvidedFile("harvester.yml"));
        fileList.put(Files.SELL, new GenerateProvidedFile("sell.yml"));
        pl.getLogger().info("Loading provided files...");
    }

    public FileConfiguration getConfig() {
        return fileList.get(Files.CONFIG).get();
    }

    public FileConfiguration getMessages() {
        return fileList.get(Files.MESSAGES).get();
    }

    public FileConfiguration getHarvester() {
        return fileList.get(Files.HARVESTER).get();
    }

    public FileConfiguration getSell() {
        return fileList.get(Files.SELL).get();
    }

    public void reload() {
        for (Files file : Files.values()) {
            fileList.get(file).reload();
        }
        pl.getLogger().info("Reloading provided files...");
    }
}