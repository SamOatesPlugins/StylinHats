/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.stylinhats.callbacks;

import com.samoatesgames.samoatesplugincore.gui.GuiCallback;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import com.samoatesgames.stylinhats.StylinHats;
import com.samoatesgames.stylinhats.data.Hat;
import com.samoatesgames.stylinhats.data.HatStatus;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Sam
 */
public class HatGuiCallback implements GuiCallback {

    private final StylinHats m_plugin;
    private final Player m_player;
    private final Hat m_hat;
    private final HatStatus m_status;
    
    public HatGuiCallback(StylinHats plugin, Player player, Hat hat, HatStatus status) {
        m_plugin = plugin;
        m_player = player;
        m_hat = hat;
        m_status = status;
    }
    
    public void onClick(GuiInventory inventory, InventoryClickEvent clickEvent) {

        if (!m_status.canEquipt) {
            return;
        }
        
        m_plugin.equiptHat(m_player, m_hat.getName(), m_hat.getIcon());
        
        inventory.close(m_player);
        m_player.closeInventory();    
    }
    
}
