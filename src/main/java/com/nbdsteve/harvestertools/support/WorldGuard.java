package com.nbdsteve.harvestertools.support;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;

public class WorldGuard {
    public static boolean allowsBreak(Location loc) {
        ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
        if (set.queryState(null, DefaultFlag.BLOCK_BREAK) == StateFlag.State.DENY) {
            return false;
        }
        return true;
    }
}