package org.bali.boxpvp.BoxPvP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Arena {
    private final Location start;
    private final Location end;

    public Arena(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    public static Arena fromString(String arenaString) {
        String[] parts = arenaString.split(";");
        String[] startCoords = parts[0].split(",");
        String[] endCoords = parts[1].split(",");
        World world = Bukkit.getWorld(parts[2].trim());

        Location start = new Location(world, Integer.parseInt(startCoords[0]), Integer.parseInt(startCoords[1]), Integer.parseInt(startCoords[2]));
        Location end = new Location(world, Integer.parseInt(endCoords[0]), Integer.parseInt(endCoords[1]), Integer.parseInt(endCoords[2]));

        return new Arena(start, end);
    }

    public boolean contains(Location location) {
        return location.getWorld().equals(start.getWorld())
                && location.getX() >= Math.min(start.getX(), end.getX())
                && location.getX() <= Math.max(start.getX(), end.getX())
                && location.getY() >= Math.min(start.getY(), end.getY())
                && location.getY() <= Math.max(start.getY(), end.getY())
                && location.getZ() >= Math.min(start.getZ(), end.getZ())
                && location.getZ() <= Math.max(start.getZ(), end.getZ());
    }
}