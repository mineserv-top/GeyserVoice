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

    public PositionsTask(GeyserVoice plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        isConnected = plugin.isConnected();
        String host = plugin.getHost();
        int port = plugin.getPort();
        String serverKey = plugin.getServerKey();
        String link = "http://" + host + ":" + port;

        if(isConnected){
            if (host != null && serverKey != null) {
                UpdatePlayersPacket updatePlayersPacket = new UpdatePlayersPacket();
                updatePlayersPacket.loginKey = serverKey;
                updatePlayersPacket.players = getPlayerDataList();

                Network.sendPostRequest(link, updatePlayersPacket);
            }
        }
    }

    public List<PlayerData> getPlayerDataList()
    {
        List<PlayerData> playerDataList = new ArrayList<>();

        for (Player player : Bukkit.getServer().getOnlinePlayers())
        {
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

    public double getCaveDensity(Player player)
    {
        if (!isConnected)
        {
            return 0.0;
        }
    
        String[] caveBlocks = {
            "STONE",
            "DIORITE",
            "GRANITE",
            "DEEPSLATE",
            "TUFF"
        };
    
        int block1 = Arrays.asList(caveBlocks).contains(getBlockType(getRelativeLocation(player.getLocation(), 0, 3, 0))) ? 1 : 0;
        int block2 = Arrays.asList(caveBlocks).contains(getBlockType(getRelativeLocation(player.getLocation(), -3, 0, 0))) ? 1 : 0;
        int block3 = Arrays.asList(caveBlocks).contains(getBlockType(getRelativeLocation(player.getLocation(), 3, 0, 0))) ? 1 : 0;
        int block4 = Arrays.asList(caveBlocks).contains(getBlockType(getRelativeLocation(player.getLocation(), 0, 0, 3))) ? 1 : 0;
        int block5 = Arrays.asList(caveBlocks).contains(getBlockType(getRelativeLocation(player.getLocation(), 0, 0, -3))) ? 1 : 0;
        int block6 = Arrays.asList(caveBlocks).contains(getBlockType(getRelativeLocation(player.getLocation(), 0, -3, 0))) ? 1 : 0;

        return (block1 + block2 + block3 + block4 + block5 + block6) / 6.0;
    }
    
    private Location getRelativeLocation(Location base, double x, double y, double z)
    {
        return new Location(base.getWorld(), base.getX() + x, base.getY() + y, base.getZ() + z);
    }
    
    private String getBlockType(Location location)
    {
        return location.getBlock().getType().toString();
    }

    private String getDimensionId(Player player)
    {
        String worldName = player.getWorld().getName();
        return worldName.equals("world") ? "minecraft:overworld" :
        worldName.equals("world_nether") ? "minecraft:nether" :
        worldName.equals("world_the_end") ? "minecraft:the_end" :
        "minecraft:unknown";
    }
}
