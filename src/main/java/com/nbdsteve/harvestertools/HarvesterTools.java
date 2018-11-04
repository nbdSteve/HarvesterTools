package com.nbdsteve.harvestertools;

import com.nbdsteve.harvestertools.command.HarvesterCommand;
import com.nbdsteve.harvestertools.event.BlockBreak;
import com.nbdsteve.harvestertools.event.ModeChange;
import com.nbdsteve.harvestertools.event.gui.GuiClick;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class HarvesterTools extends JavaPlugin {
    //Economy variable for the plugin
    private static Economy econ;
    //New LoadProvidedFiles instance
    private LoadProvidedFiles lpf;
    //New collate blocks instance
    private CollateBlocks cb;

    @Override
    public void onEnable() {
        getLogger().info("Thanks for using HarvesterTools - nbdSteve");
        if (!setupEconomy()) {
            getLogger().severe("Vault.jar not found, disabling economy features.");
        }
        //Generate all of the provided files for the plugin
        this.lpf = new LoadProvidedFiles();
        this.cb = new CollateBlocks();
        //Collate the block prices for the plugin
        cb.calculateBlocks();
        //Register the commands for the plugin
        getCommand("harvest").setExecutor(new HarvesterCommand(this));
        getCommand("h").setExecutor(new HarvesterCommand(this));
        //Register the events for the plugin
        getServer().getPluginManager().registerEvents(new GuiClick(), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
        getServer().getPluginManager().registerEvents(new ModeChange(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Thanks for using HarvesterTools - nbdSteve");
    }
    //Set up the economy for the plugin
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    //Get the provided files instance
    public LoadProvidedFiles getFiles() {
        return lpf;
    }
    //Get the collate blocks instance
    public CollateBlocks getBlocks() {
        return cb;
    }
    //Get the economy
    public static Economy getEconomy() {
        return econ;
    }
}
