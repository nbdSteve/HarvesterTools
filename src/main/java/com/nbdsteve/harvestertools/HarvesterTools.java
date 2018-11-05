package com.nbdsteve.harvestertools;

import com.nbdsteve.harvestertools.command.HarvestCommand;
import com.nbdsteve.harvestertools.event.BlockBreak;
import com.nbdsteve.harvestertools.event.ModeChange;
import com.nbdsteve.harvestertools.event.gui.GuiClick;
import com.nbdsteve.harvestertools.file.CollateBlocks;
import com.nbdsteve.harvestertools.file.LoadProvidedFiles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Core class for the HarvesterTools plugin.
 */
public final class HarvesterTools extends JavaPlugin {
    //Economy variable for the plugin
    private static Economy econ;
    //New LoadProvidedFiles instance
    private LoadProvidedFiles lpf;
    //New collate blocks instance
    private CollateBlocks cb;

    /**
     * Method called when the plugin starts, register all events and commands in this method
     */
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
        getCommand("harvest").setExecutor(new HarvestCommand(this));
        getCommand("h").setExecutor(new HarvestCommand(this));
        //Register the events for the plugin
        getServer().getPluginManager().registerEvents(new GuiClick(), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
        getServer().getPluginManager().registerEvents(new ModeChange(), this);
    }

    /**
     * Method called when the plugin is disabled
     */
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

    /**
     * Get the LoadProvidedFiles instance that has been created
     * @return LoadProvidedFiles instance
     */
    public LoadProvidedFiles getFiles() {
        return lpf;
    }

    /**
     * Get the CollateBlocks instance that has been created
     * @return CollateBlocks instance
     */
    public CollateBlocks getBlocks() {
        return cb;
    }

    /**
     * Get the servers economy
     * @return econ
     */
    public static Economy getEconomy() {
        return econ;
    }
}
