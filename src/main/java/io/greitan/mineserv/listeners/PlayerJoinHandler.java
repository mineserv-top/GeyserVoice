package io.greitan.mineserv.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import io.greitan.mineserv.GeyserVoice;
import io.greitan.mineserv.utils.Language;
import io.greitan.mineserv.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Objects;

public class PlayerJoinHandler implements Listener {

    private final GeyserVoice plugin;
    private final String lang;

    public PlayerJoinHandler(GeyserVoice plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean isConnected = plugin.isConnected();
        Player player = event.getPlayer();
        String playerBindKey = plugin.getConfig().getString("config.players." + player.getName());

        if (isConnected && Objects.nonNull(playerBindKey)) {
            handleAutoBind(playerBindKey, player);
        }
    }

    private void handleAutoBind(String playerBindKey, Player player) {
        boolean isBound = plugin.bind(playerBindKey, player);

        if (isBound) {
            String playerName = player.getName();
            String connectMessage = Language.getMessage(lang, "player-connect").replace("$player", playerName);

            Logger.info(connectMessage);

            boolean sendConnectMessage = plugin.getConfig().getBoolean("config.voice.send-connect-message");
            if (sendConnectMessage) {
                Bukkit.broadcast(Component.text(connectMessage).color(NamedTextColor.YELLOW));
            }
        } else {
            player.sendMessage(Component.text(Language.getMessage(lang, "plugin-autobind-failed")).color(NamedTextColor.RED));
        }
    }
}
