package io.greitan.mineserv;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.greitan.mineserv.commands.VoiceCommand;
import io.greitan.mineserv.listeners.*;
import io.greitan.mineserv.network.Network;
import io.greitan.mineserv.network.Payloads.*;
import io.greitan.mineserv.tasks.PositionsTask;
import io.greitan.mineserv.utils.*;

import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

/**
 * Main plugin class for GeyserVoice.
 */
public class GeyserVoice extends JavaPlugin {
    private static @Getter GeyserVoice instance;
    private @Getter boolean isConnected = false;
    private @Getter String host = "";
    private @Getter int port = 0;
    private @Getter String serverKey = "";
    private @Getter Map<String, Boolean> playerBinds = new HashMap<>();

    private String lang;

    /**
     * Executes upon enabling the plugin.
     */
    @Override
    public void onEnable() {
        instance = this;
        
        lang = getConfig().getString("config.lang");
        int positionTaskInterval = getConfig().getInt("position-task-interval", 1);
        Language.init(this);
    
        VoiceCommand voiceCommand = new VoiceCommand(this, lang);
        getCommand("voice").setExecutor(voiceCommand);
        getCommand("voice").setTabCompleter(voiceCommand);
        new PositionsTask(this).runTaskTimer(this, positionTaskInterval, 1);
        getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this, lang), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitHandler(this, lang), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder(this).register();
        }

        this.reload();
    }

    /**
     * Reloads the plugin configuration and initializes connections.
     */
    public void reload() {
        saveDefaultConfig();
        reloadConfig();
        Logger.info(Language.getMessage(lang, "plugin-config-loaded"));
        Logger.info(Language.getMessage(lang, "plugin-command-execuror"));

        host = getConfig().getString("config.host");
        port = getConfig().getInt("config.port");
        serverKey = getConfig().getString("config.server-key");

        isConnected = connect(true);

        int proximityDistance = getConfig().getInt("config.voice.proximity-distance");
        Boolean proximityToggle = getConfig().getBoolean("proximity-toggle");
        Boolean voiceEffects = getConfig().getBoolean("voice-effects");

        updateSettings(proximityDistance, proximityToggle, voiceEffects);
    }

    /**
     * Connects to the server.
     *
     * @param force Indicates whether to force a connection.
     * @return True if connected successfully, otherwise false.
     */
    public Boolean connect(Boolean force) {
        if (isConnected && !force) return true;
    
        if (Objects.nonNull(host) && Objects.nonNull(serverKey)) {
            String link = "http://" + host + ":" + port;
    
            // Create MCCommPacket object.
            MCCommPacket mCCommPacket = new MCCommPacket();
            mCCommPacket.PacketType = 0; // Assign the appropriate PacketType
    
            // Create request data object.
            LoginPacket loginPacket = new LoginPacket();
            loginPacket.LoginKey = serverKey;
    
            mCCommPacket.PacketData = loginPacket; // Assign the LoginPacket to PacketData
    
            isConnected = Network.sendPostRequest(link, mCCommPacket);
            if (isConnected) {
                Logger.info(Language.getMessage(lang, "plugin-connect-connect"));
                return true;
            } else {
                Logger.warn(Language.getMessage(lang, "plugin-connect-disconnect"));
                return false;
            }
        } else {
            Logger.warn(Language.getMessage(lang, "plugin-connect-invalid-data"));
            return false;
        }
    }

    /**
     * Binds a player to the voice chat server.
     *
     * @param playerKey The key associated with the player.
     * @param player    The player to bind.
     * @return True if the binding was successful, otherwise false.
     */
    public Boolean bind(String playerKey, Player player) {
        if (!isConnected || Objects.isNull(host) || Objects.isNull(serverKey)) return false;
        String link = "http://" + host + ":" + port;
    
        getConfig().set("config.players." + player.getName(), playerKey);
        saveConfig();
    
        // Create MCCommPacket object.
        MCCommPacket mCCommPacket = new MCCommPacket();
        mCCommPacket.PacketType = 1; // Assign the appropriate PacketType
    
        // Create request data object.
        BindingPacket bindingPacket = new BindingPacket();
        bindingPacket.PlayerId = player.getEntityId();
        bindingPacket.Gamertag = player.getName();
        bindingPacket.PlayerKey = playerKey;
        bindingPacket.LoginKey = serverKey;
    
        mCCommPacket.PacketData = bindingPacket; // Assign the BindingPacket to PacketData
    
        boolean bindStatus = Network.sendPostRequest(link, mCCommPacket);
    
        playerBinds.put(player.getName(), bindStatus);
    
        return bindStatus;
    }


    /**
     * Disconnects a player from the voice chat server.
     *
     * @param player The player to disconnect.
     * @return True if the disconnection was successful, otherwise false.
     */
    public Boolean disconnectPlayer(Player player) {
        if (!isConnected || Objects.isNull(host) || Objects.isNull(serverKey)) return false;
        String link = "http://" + host + ":" + port;
    
        // Create MCCommPacket object.
        MCCommPacket mCCommPacket = new MCCommPacket();
        mCCommPacket.PacketType = 5; // Assign the appropriate PacketType
    
        // Create request data object.
        DisconnectPlayerPacket disconnectPlayerPacket = new DisconnectPlayerPacket();
        disconnectPlayerPacket.LoginKey = serverKey;
        disconnectPlayerPacket.PlayerId = player.getEntityId();
    
        mCCommPacket.PacketData = disconnectPlayerPacket; // Assign the DisconnectPlayerPacket to PacketData
    
        boolean disconnectStatus = Network.sendPostRequest(link, mCCommPacket);
    
        return disconnectStatus;
    }

    /**
     * Updates the voice chat settings.
     *
     * @param proximityDistance Proximity distance setting.
     * @param proximityToggle   Proximity toggle setting.
     * @param voiceEffects      Voice effects setting.
     * @return True if settings were updated successfully, otherwise false.
     */
    public Boolean updateSettings(int proximityDistance, Boolean proximityToggle, Boolean voiceEffects) {
        if (!isConnected || Objects.isNull(host) || Objects.isNull(serverKey)) return false;
        String link = "http://" + host + ":" + port;
    
        // Create MCCommPacket object.
        MCCommPacket mCCommPacket = new MCCommPacket();
        mCCommPacket.PacketType = 3; // Assign the appropriate PacketType
    
        // Create request data object.
        UpdateSettingsPacket updateSettingsPacket = new UpdateSettingsPacket();
        updateSettingsPacket.LoginKey = serverKey;
        updateSettingsPacket.ProximityDistance = proximityDistance;
        updateSettingsPacket.ProximityToggle = proximityToggle;
        updateSettingsPacket.VoiceEffects = voiceEffects;
    
        mCCommPacket.PacketData = updateSettingsPacket; // Assign the UpdateSettingsPacket to PacketData
    
        return Network.sendPostRequest(link, mCCommPacket);
    }
}