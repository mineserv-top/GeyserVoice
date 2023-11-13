package io.greitan.mineserv.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import io.greitan.mineserv.GeyserVoice;
import io.greitan.mineserv.utils.Language;
import io.greitan.mineserv.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerQuitHandler implements Listener {

    private final GeyserVoice plugin;
    private final String lang;

    public PlayerQuitHandler(GeyserVoice plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        boolean isBound = plugin.getPlayerBinds().getOrDefault(player.getName(), false);

        if (plugin.isConnected() && isBound) {
            handlePlayerDisconnect(player);
        }
    }

    private void handlePlayerDisconnect(Player player) {
        boolean isDisconnected = plugin.disconnectPlayer(player);

        String playerName = player.getName();
        String disconnectMessage = Language.getMessage(lang, "player-disconnect-success").replace("$player", playerName);

        if (isDisconnected) {
            Logger.info(disconnectMessage);

            boolean sendDisconnectMessage = plugin.getConfig().getBoolean("config.voice.send-disconnect-message");
            if (sendDisconnectMessage) {
                Bukkit.broadcast(Component.text(disconnectMessage).color(NamedTextColor.YELLOW));
            }
        } else {
            Logger.error(Language.getMessage(lang, "player-disconnect-failed").replace("$player", playerName));
        }
    }
}
