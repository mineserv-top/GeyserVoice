package io.greitan.mineserv;

import lombok.Getter; 
import org.bukkit.plugin.java.JavaPlugin;

import io.greitan.mineserv.commands.VoiceCommand;
import io.greitan.mineserv.listeners.PlayerJoinListener;
import io.greitan.mineserv.network.Network;
import io.greitan.mineserv.network.Payloads.LoginPacket;
import io.greitan.mineserv.tasks.PositionsTask;
import io.greitan.mineserv.utils.Language;
import io.greitan.mineserv.utils.Logger;

import java.util.Objects;


public class GeyserVoice extends JavaPlugin {
    private static @Getter GeyserVoice instance;
    private @Getter boolean isConnected = false;
    private @Getter String host = "";
    private @Getter int port = 0;
    private @Getter String serverKey = "";

    private String lang;

    @Override
    public void onEnable() {
        instance = this;
    
        lang = getConfig().getString("config.lang");

        Language.init(this);
    
        VoiceCommand voiceCommand = new VoiceCommand(this, lang);
        getCommand("voice").setExecutor(voiceCommand);
        getCommand("voice").setTabCompleter(voiceCommand);
        new PositionsTask(this).runTaskTimer(this, 0, 1);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, lang), this);


        this.reload("server");
    }

    public void reload(String player) {
        if (!player.equals("server")) {
            Logger.info(Language.getMessage(lang, "plugin-reload-pl").replace("$player", player));
        }
        saveDefaultConfig();
        reloadConfig();
        Logger.info(Language.getMessage(lang, "plugin-config-loaded"));
        Logger.info(Language.getMessage(lang, "plugin-command-execuror"));

        host = getConfig().getString("config.host");
        port = getConfig().getInt("config.port");
        serverKey = getConfig().getString("config.server-key");

        isConnected = connect(host, port, serverKey, true);
    }

    public Boolean connect(String host, int port, String serverKey, Boolean force) {
        if (isConnected && !force) return true;
        
        if (Objects.nonNull(host) && Objects.nonNull(serverKey)) {
            String link = "http://" + host + ":" + port;
            LoginPacket loginPacket = new LoginPacket();
            loginPacket.loginKey = serverKey;
    
            isConnected = Network.sendPostRequest(link, loginPacket);
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
    
}