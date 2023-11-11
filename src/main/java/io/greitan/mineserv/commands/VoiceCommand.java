package io.greitan.mineserv.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import io.greitan.mineserv.GeyserVoice;
import io.greitan.mineserv.network.Network;
import io.greitan.mineserv.network.Payloads.BindingPacket;
import io.greitan.mineserv.utils.Language;
import io.greitan.mineserv.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VoiceCommand implements CommandExecutor, TabCompleter {

    private final GeyserVoice plugin;
    private final String lang;
    private boolean isConnected = false;

    public VoiceCommand(GeyserVoice plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        isConnected = plugin.isConnected();
        
        String host = plugin.getConfig().getString("config.host");
        int port = plugin.getConfig().getInt("config.port");
        String link = "http://" + host + ":" + port;
        String serverKey = plugin.getConfig().getString("config.server-key");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("bind") && player.hasPermission("voice.bind")) {
                    String key = args[1];
                    if(key == null){return false;}

                    String nick = player.getName();
                    int id = player.getEntityId();

                    plugin.getConfig().set("config.players."+player.getName(), key);
                    plugin.saveConfig();

                    BindingPacket bindingPacket = new BindingPacket();
                    bindingPacket.playerId = id;
                    bindingPacket.gamertag = nick;
                    bindingPacket.playerKey = key;
                    bindingPacket.loginKey = serverKey;
                    
                    Boolean isBinded = Network.sendPostRequest(link, bindingPacket);
                    if(isBinded){
                        String msg = Language.getMessage(lang, "cmd-bind-connect");
                        player.sendMessage(Component.text(msg).color(NamedTextColor.AQUA));
                    } else {
                        String msg = Language.getMessage(lang, "cmd-bind-disconnect");
                        player.sendMessage(Component.text(msg).color(NamedTextColor.YELLOW));
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("setup") && player.hasPermission("voice.setup")) {
                    String newHost = args[1];
                    String newPort = args[2];
                    String newKey = args[3];

                    if(newHost != null && newPort != null && newKey != null){
                        plugin.getConfig().set("config.host", newHost);
                        plugin.getConfig().set("config.port", newPort);
                        plugin.getConfig().set("config.server-key", newKey);
                        plugin.saveConfig();
                        plugin.reloadConfig();
                        player.sendMessage(Component.text(Language.getMessage(lang, "plugin-connect-connect")).color(NamedTextColor.AQUA));

                        plugin.reload(player.getName());
                        return true; 
                    } else {
                        player.sendMessage(Component.text(Language.getMessage(lang, "plugin-connect-connect")).color(NamedTextColor.AQUA));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("connect") && player.hasPermission("voice.connect")) {
                    if (host != null && serverKey != null) {
                        Boolean force = Boolean.valueOf(args[1]);
                        Boolean isConnected = plugin.connect(host, port, serverKey, force);
                        if(isConnected){
                            player.sendMessage(Component.text(Language.getMessage(lang, "plugin-connect-connect")).color(NamedTextColor.AQUA));
                        } else {
                            player.sendMessage(Component.text(Language.getMessage(lang, "plugin-connect-disconnect")).color(NamedTextColor.YELLOW));
                        }
                        return true;
                    } else {
                        player.sendMessage(Component.text(Language.getMessage(lang, "plugin-connect-invalid-data")).color(NamedTextColor.RED));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("voice.reload")) {
                    plugin.reload(player.getName());
                    player.sendMessage(Component.text(Language.getMessage(lang, "cmd-reload")).color(NamedTextColor.GREEN));
                    return true;
                } else {
                    player.sendMessage(Component.text(Language.getMessage(lang, "cmd-not-exists")).color(NamedTextColor.GREEN));
                    return true;
                }
            }
            player.sendMessage(Component.text(Language.getMessage(lang, "cmd-invalid-args")).color(NamedTextColor.RED));
            return true;
        } else if(args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reload("server");
                Logger.log(Component.text(Language.getMessage(lang, "cmd-reload")).color(NamedTextColor.GREEN));
                return true;
            } else {
                sender.sendMessage(Component.text(Language.getMessage(lang, "cmd-not-player")).color(NamedTextColor.RED));
                return true;
            }
        } else {
            sender.sendMessage(Component.text(Language.getMessage(lang, "cmd-invalid-args")).color(NamedTextColor.RED));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();
    
        if (args.length == 1) {
            List<String> options = Arrays.asList("bind", "setup", "connect", "reload");
            StringUtil.copyPartialMatches(args[0], options, completions);
        }
    
        if (args.length == 2 && args[0].equalsIgnoreCase("setup")) {
            List<String> options = Arrays.asList("host port key");
            StringUtil.copyPartialMatches(args[1], options, completions);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("connect")) {
            List<String> options = Arrays.asList("true", "false");
            StringUtil.copyPartialMatches(args[1], options, completions);
        }
    
        Collections.sort(completions);
        return completions;
    }
    
}
