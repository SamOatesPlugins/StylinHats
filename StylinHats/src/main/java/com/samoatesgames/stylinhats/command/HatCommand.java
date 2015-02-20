/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.stylinhats.command;

import com.samoatesgames.samoatesplugincore.commands.BasicCommandHandler;
import com.samoatesgames.samoatesplugincore.commands.PluginCommandManager;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import com.samoatesgames.stylinhats.StylinHats;
import com.samoatesgames.stylinhats.callbacks.HatGuiCallback;
import com.samoatesgames.stylinhats.data.Hat;
import com.samoatesgames.stylinhats.data.HatStatus;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Sam
 */
public class HatCommand extends BasicCommandHandler {

    private final StylinHats m_plugin;
    
    public HatCommand(StylinHats hat) {
        super("hat");
        m_plugin = hat;
    }

    @Override
    public boolean execute(PluginCommandManager manager, CommandSender sender, String[] arguments) {
        
        if (!manager.hasPermission(sender, "hat.command")) {
            manager.sendMessage(sender, "You don't have permission to wear a hat.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (arguments.length == 0) {
            showHatsGUI(player);
            return true;
        }
        
        if (arguments.length == 1) {
            if (arguments[0].equalsIgnoreCase("remove")) {
                removeHat(manager, player);
                return true;
            }
            
            if (manager.hasPermission(sender, "hat.command.id")) {
                try {
                    int id = Integer.parseInt(arguments[0]);
                    ItemStack i = new ItemStack(id);
                    m_plugin.equiptHat(player, i.getType().name(), i.getType());
                } catch (Exception ex) {}                
            }
        }
        
        return true;        
    }
    
    /**
     * 
     * @param player 
     */
    private void removeHat(PluginCommandManager manager, Player player) {
        
        EntityEquipment equiptment = player.getEquipment();
        ItemStack helmet = equiptment.getHelmet();
        if (!m_plugin.isHat(helmet)) {
            manager.sendMessage(player, "You aren't wearing a stylin hat.");
            return;
        }

        equiptment.setHelmet(null);
        manager.sendMessage(player, "Your stylin hat was removed.");
    }
    
    /**
     * 
     * @param sender 
     */
    private void showHatsGUI(Player player) {
        
        final Map<String, Hat> hats = m_plugin.getHats();
        final int noofKits = hats.size();
        final int rowCount = (int) Math.ceil(noofKits / 9.0f);

        GuiInventory inventory = new GuiInventory(m_plugin);
        inventory.createInventory("Hat Selection", rowCount);

        for (Hat hat : hats.values()) {

            ItemStack item = null;
            String[] details = new String[3];

            if (!player.hasPermission("hat.command." + hat.getUnlockCode().name().toLowerCase())) {
                continue;
            }
            
            HatStatus status = m_plugin.canPlayerEquiptHat(player, hat);
            
            if (status.canEquipt) {
                details[0] = ChatColor.GREEN + "Available";
                details[1] = hat.getUnlockMethod();
                details[2] = ChatColor.GOLD + "Left click to equipt hat";     
                item = new ItemStack(hat.getIcon());
            } else {
                details[0] = ChatColor.RED + "Locked";
                details[1] = hat.getUnlockMethod();
                item = new ItemStack(Material.COAL_BLOCK);
            }

            inventory.addMenuItem(hat.getName(), item, details, new HatGuiCallback(m_plugin, player, hat, status));
        }

        inventory.open(player);
        
    }
    
}
