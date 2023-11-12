package io.greitan.mineserv.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import io.greitan.mineserv.GeyserVoice;

public class Placeholder extends PlaceholderExpansion  {
    private final GeyserVoice plugin;

    // Get the plugin interface.
    public Placeholder(GeyserVoice plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "voice";
    }

    @Override
    public String getAuthor() {
        return "GeyserVoice";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        // Voice icon placeholder "%voice_status%"
        if (identifier.equalsIgnoreCase("status")) {
            if (plugin.getPlayerBinds().getOrDefault(player.getName(), false))
            {
                return plugin.getConfig().getString("config.voice.in-voice-symbol");
            } 
            else
            {
                return plugin.getConfig().getString("config.voice.not-in-voice-symbol");
            }
        }
        return null;
    }
}
