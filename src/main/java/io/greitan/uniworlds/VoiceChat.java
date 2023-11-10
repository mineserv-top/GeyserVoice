package io.greitan.uniworlds;

import lombok.Getter; 

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

//import io.greitan.uniworlds.listeners.MainListener;
import io.greitan.uniworlds.commands.VoiceCommand;

import java.util.logging.Logger;

public class VoiceChat extends JavaPlugin {
    @Getter
    private static VoiceChat instance;

    @Getter
    private static Logger jlogger;

    @Override
    public void onEnable() {
        instance = this; 
        jlogger = super.getLogger();

      	jlogger.info("Plugin loaded!");

        //Bukkit.getPluginManager().registerEvents(new MainListener(), this);

        getCommand("voice").setExecutor(new VoiceCommand());
    }
}