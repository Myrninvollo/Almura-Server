package org.bukkit.craftbukkit.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.spigotmc.SpigotConfig;

public class ProtocolCommand extends Command {

    public ProtocolCommand(String name) {
        super(name);
        this.description = "Gets the current minecraft and protocol version being used.";
        this.usageMessage = "/protocol";
        this.setPermission("bukkit.command.protocol");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (SpigotConfig.versionSpoof) {
            sender.sendMessage("This server is currently being spoofed as a " + ChatColor.GREEN +
                               "Minecraft " + SpigotConfig.minecraftVersion + ChatColor.RESET + " server, using " +
                                ChatColor.GREEN + "Protocol " + SpigotConfig.protocolVersion + ChatColor.RESET + ".");
        } else {
            sender.sendMessage("This server is currently not being spoofed as another Minecraft or Protocol version.");
        }
        return true;
    }
}
