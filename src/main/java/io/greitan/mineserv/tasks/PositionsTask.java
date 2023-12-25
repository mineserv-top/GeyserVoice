package io.greitan.mineserv.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import io.greitan.mineserv.GeyserVoice;
import io.greitan.mineserv.network.Network;
import io.greitan.mineserv.network.Payloads.LocationData;
import io.greitan.mineserv.network.Payloads.PlayerData;
import io.greitan.mineserv.network.Payloads.UpdatePlayersPacket;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PositionsTask extends BukkitRunnable {
    private final GeyserVoice plugin;
    private boolean isConnected = false;

    public PositionsTask(GeyserVoice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        isConnected = plugin.isConnected();
        String host = plugin.getHost();
        int port = plugin.getPort();
        String serverKey = plugin.getServerKey();
        String link = "http://" + host + ":" + port;

        if (isConnected) {
            if (host != null && serverKey != null) {
                UpdatePlayersPacket updatePlayersPacket = new UpdatePlayersPacket();
                updatePlayersPacket.LoginKey = serverKey;
                updatePlayersPacket.Players = getPlayerDataList();

                Network.sendPostRequest(link, updatePlayersPacket);
            }
        }
    }

    public List<PlayerData> getPlayerDataList() {
        List<PlayerData> playerDataList = new ArrayList<>();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Location headLocation = player.getEyeLocation();

            LocationData locationData = new LocationData();
            locationData.x = headLocation.getX();
            locationData.y = headLocation.getY();
            locationData.z = headLocation.getZ();

            PlayerData playerData = new PlayerData();
            playerData.PlayerId = player.getEntityId();
            playerData.DimensionId = getDimensionId(player);
            playerData.Location = locationData;
            playerData.Rotation = player.getLocation().getYaw();

            if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                playerData.CaveDensity = getCaveDensity(player);
            } else {
                playerData.CaveDensity = 0.0;
            }
            playerData.IsDead = player.isDead();
            playerData.InWater = player.isInWater();

            playerDataList.add(playerData);
        }

        return playerDataList;
    }

    public double getCaveDensity(Player player) {
        if (!isConnected) {
            return 0.0;
        }

        String[] caveBlocks = {
            "STONE",
            "DIORITE",
            "GRANITE",
            "DEEPSLATE",
            "TUFF"
        };

        int blockCount = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location relativeLoc = getRelativeLocation(player.getLocation(), x, y, z);
                    if (Arrays.asList(caveBlocks).contains(getBlockType(relativeLoc))) {
                        blockCount++;
                    }
                }
            }
        }

        return blockCount / 27.0; // Total blocks checked
    }

    private Location getRelativeLocation(Location base, double x, double y, double z) {
        return new Location(base.getWorld(), base.getX() + x, base.getY() + y, base.getZ() + z);
    }

    private String getBlockType(Location location) {
        return location.getBlock().getType().toString();
    }

    private String getDimensionId(Player player) {
        String worldName = player.getWorld().getName();
        return switch (worldName) {
            case "world" -> "minecraft:overworld";
            case "world_nether" -> "minecraft:nether";
            case "world_the_end" -> "minecraft:the_end";
            default -> "minecraft:unknown";
        };
    }
}
