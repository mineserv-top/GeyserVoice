package io.greitan.mineserv.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.greitan.mineserv.GeyserVoice;
import io.greitan.mineserv.network.Network;
import io.greitan.mineserv.network.Payloads.BindingPacket;
import io.greitan.mineserv.utils.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerJoinListener implements Listener {

    private final GeyserVoice plugin;
    private final String lang;
    private boolean isConnected = false;

    public PlayerJoinListener(GeyserVoice plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        isConnected = plugin.isConnected();

        String host = plugin.getConfig().getString("config.host");
        int port = plugin.getConfig().getInt("config.port");

        String link = "http://" + host + ":" + port;

        String serverKey = plugin.getConfig().getString("config.server-key");
        String playerKey = plugin.getConfig().getString("config.players."+player.getName());

        if (isConnected && playerKey != null) {
            int id = player.getEntityId();

            BindingPacket bindingPacket = new BindingPacket();
            bindingPacket.playerId = id;
            bindingPacket.gamertag = player.getName();
            bindingPacket.playerKey = playerKey;
            bindingPacket.loginKey = serverKey;

            boolean isBinded = Network.sendPostRequest(link, bindingPacket);

            if (isBinded) {
                player.sendMessage(Component.text(Language.getMessage(lang, "plugin-autobind-success")).color(NamedTextColor.AQUA));
            } else {
                player.sendMessage(Component.text(Language.getMessage(lang, "plugin-autobind-failed")).color(NamedTextColor.YELLOW));
            }
        }
    }
}
