package io.greitan.mineserv.listeners;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import io.greitan.mineserv.GeyserVoice;
import io.greitan.mineserv.utils.Language;

public class PlayerJoinListener implements Listener {

    private final GeyserVoice plugin;
    private final String lang;
    private boolean isConnected = false;

    // Get the plugin and lang interfaces.
    public PlayerJoinListener(GeyserVoice plugin, String lang)
    {
        this.plugin = plugin;
        this.lang = lang;
    }

    // Player Join event.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        isConnected = plugin.isConnected();
        Player player = event.getPlayer();

        String playerKey = plugin.getConfig().getString("config.players."+player.getName());

        // Auto bind player.
        if (isConnected && Objects.nonNull(playerKey))
        {
            boolean isBinded = plugin.bind(playerKey, player);
            // Player binded.
            if (isBinded)
            {
                player.sendMessage(Component.text(Language.getMessage(lang, "plugin-autobind-success")).color(NamedTextColor.AQUA));
            }
            // Bind failed.
            else
            {
                player.sendMessage(Component.text(Language.getMessage(lang, "plugin-autobind-failed")).color(NamedTextColor.RED));
            }
        }
    }
}
