package dev.nuer.harvest.command;

import dev.nuer.harvest.file.CollateBlocks;
import dev.nuer.harvest.HarvesterTools;
import dev.nuer.harvest.file.LoadProvidedFiles;
import dev.nuer.harvest.gui.HarvesterGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the /harvest and /h command for the plugin
 */
public class HarvestCommand implements CommandExecutor {
    //Register class so that command will work
    public HarvestCommand(HarvesterTools pl) {
        this.pl = pl;
    }

    //Register the main class
    private Plugin pl = HarvesterTools.getPlugin(HarvesterTools.class);
    //Register LoadProvideFiles instance
    private LoadProvidedFiles lpf = ((HarvesterTools) pl).getFiles();
    //Register the collate blocks instance
    private CollateBlocks cb = ((HarvesterTools) pl).getBlocks();

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
        if (c.getName().equalsIgnoreCase("harvest") || c.getName().equalsIgnoreCase("h")) {
            if (args.length == 0) {
                if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                    if (s instanceof Player) {
                        if (s.hasPermission("harvester.gui")) {
                            HarvesterGui i = new HarvesterGui();
                            i.gui((Player) s);
                            for (String line : lpf.getMessages().getStringList("open-gui")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    }
                } else {
                    if (s instanceof Player) {
                        if (s.hasPermission("harvester.help")) {
                            for (String line : lpf.getMessages().getStringList("help")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        pl.getLogger().info("The help message can only be seen using game chat.");
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
                    if (s instanceof Player) {
                        if (s.hasPermission("harvester.help")) {
                            for (String line : lpf.getMessages().getStringList("help")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        pl.getLogger().info("The help message can only be seen using game chat.");
                    }
                } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                    if (s instanceof Player) {
                        if (s.hasPermission("harvester.reload")) {
                            lpf.reload();
                            cb.calculateBlocks();
                            for (String line : lpf.getMessages().getStringList("reload")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        lpf.reload();
                        cb.calculateBlocks();
                        pl.getLogger().info("You have successfully reloaded all files.");
                    }
                } else {
                    if (s instanceof Player) {
                        for (String line : lpf.getMessages().getStringList("invalid-command")) {
                            s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    } else {
                        pl.getLogger().info("The command you entered is invalid.");
                    }
                }
            } else if (args.length == 4 || args.length == 5) {
                if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("g")) {
                    if (s instanceof Player) {
                        if (!s.hasPermission("harvester.give")) {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            return true;
                        }
                    }
                    // Initializing variables for the tool
                    Player target = null;
                    int size = 0;
                    int amount = 1;
                    int x = 0;
                    String item = args[2].toUpperCase();
                    String level = "harvester-tool-" + args[3];
                    try {
                        target = pl.getServer().getPlayer(args[1]);
                    } catch (Exception e) {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-player")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The command you entered is invalid");
                        }
                    }
                    try {
                        size = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-level")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The level of harvester you entered is invalid, enter a int between 1-9.");
                        }
                    }
                    try {
                        Material.valueOf(item);
                    } catch (Exception e) {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-item")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The item you entered is invalid.");
                        }
                    }
                    if (args.length == 5) {
                        try {
                            amount = Integer.parseInt(args[4]);
                        } catch (Exception e) {
                            if (s instanceof Player) {
                                for (String line : lpf.getMessages().getStringList("invalid-amount")) {
                                    s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                                }
                            } else {
                                pl.getLogger().info("The amount you entered is invalid.");
                            }
                        }
                    }
                    if (size <= 9 && size >= 1) {
                        while (x < amount) {
                            ItemStack tool = new ItemStack(Material.valueOf(item));
                            ItemMeta toolMeta = tool.getItemMeta();
                            List<String> toolLore = new ArrayList<>();
                            toolMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                    lpf.getHarvester().getString(level + ".name")));

                            for (String lore : lpf.getHarvester().getStringList(level + ".lore")) {
                                String mode = ChatColor.translateAlternateColorCodes('&', lpf.getHarvester().getString(level + ".harvest-mode"));
                                toolLore.add(ChatColor.translateAlternateColorCodes('&', lore).replace("%mode%", mode));
                            }
                            for (String ench : lpf.getHarvester().getStringList(level + ".enchantments")) {
                                String[] parts = ench.split("-");
                                toolMeta.addEnchant(Enchantment.getByName(parts[0]), Integer.parseInt(parts[1]), true);
                            }
                            toolMeta.setLore(toolLore);
                            tool.setItemMeta(toolMeta);
                            target.getInventory().addItem(tool);
                            x++;
                        }
                    } else {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-level")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The level you entered is invalid, the level must be between 1-9.");
                        }
                    }
                } else {
                    if (s instanceof Player) {
                        if (s.hasPermission("harvester.give")) {
                            for (String line : lpf.getMessages().getStringList("invalid-command")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        pl.getLogger().info("the command you entered is invalid.");
                    }
                }
            } else {
                if (s instanceof Player) {
                    if (s.hasPermission("harvester.give") || s.hasPermission("harvester.reload")
                            || s.hasPermission("harvester.help")) {
                        for (String line : lpf.getMessages().getStringList("invalid-command")) {
                            s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    } else {
                        for (String line : lpf.getMessages().getStringList("no-permission")) {
                            s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    }
                } else {
                    pl.getLogger().info("the command you entered is invalid.");
                }
            }
        }
        return true;
    }
}
