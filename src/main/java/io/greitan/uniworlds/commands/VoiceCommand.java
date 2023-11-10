package io.greitan.uniworlds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import de.tr7zw.nbtapi.*;
import de.tr7zw.nbtapi.iface.*;

public class VoiceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);

            Bukkit.getLogger().info("nbtData: " + nbt);
            player.sendMessage("Hell!" + nbt);
        } else {
            sender.sendMessage("4len!");
        }
        return true;
    }
}
