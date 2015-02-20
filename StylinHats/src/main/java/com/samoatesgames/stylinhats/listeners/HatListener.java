package com.samoatesgames.stylinhats.listeners;

import com.samoatesgames.stylinhats.StylinHats;
import com.samoatesgames.stylinhats.UnlockableEventType;
import com.samoatesgames.stylinhats.data.Hat;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 *
 * @author Sam
 */
public class HatListener implements Listener {
    
    private final StylinHats m_plugin;
    
    /**
     * 
     * @param plugin 
     */
    public HatListener(StylinHats plugin) {
        m_plugin = plugin;
    }
    
    /**
     * 
     * @param event 
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        
        final Player player = event.getPlayer();
        if (!m_plugin.hasPermission(player, "hat.command")) {
            return;
        }
        
        final Material type = event.getBlock().getType();
        
        List<Hat> hats = m_plugin.getUnlockableHats(UnlockableEventType.Break);
        for (Hat hat : hats) {
            if (hat.getIcon() == type) {
                m_plugin.unlockHat(player, hat);
                return;
            }            
        }
        
    }
    
    /**
     * 
     * @param event 
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        
        final Player player = event.getPlayer();
        if (!m_plugin.hasPermission(player, "hat.command")) {
            return;
        }
        
        List<Hat> hats = m_plugin.getUnlockableHats(UnlockableEventType.Position);
        for (Hat hat : hats) {
            if (m_plugin.hasPermission(player, hat.getPermission())) {
                continue;
            }
            
            if (hat.hasParameter("Y_GREATER_THAN")) {
                float yPos = Float.parseFloat(hat.getParameter("Y_GREATER_THAN"));
                if (player.getLocation().getY() >= yPos) {
                    m_plugin.unlockHat(player, hat);
                }
            }
        }
        
    }
    
    /**
     * 
     * @param event 
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        final Player player = (Player) event.getEntity();
        if (!m_plugin.hasPermission(player, "hat.command")) {
            return;
        }

        List<Hat> hats = m_plugin.getUnlockableHats(UnlockableEventType.Damaged);
        for (Hat hat : hats) {
            if (m_plugin.hasPermission(player, hat.getPermission())) {
                continue;
            }

            if (hat.hasParameter("FALL_AND_SURVIVE") && event.getCause() == DamageCause.FALL) {
                float distance = Float.parseFloat(hat.getParameter("FALL_AND_SURVIVE"));
                float fallDistance = player.getFallDistance();
                if (fallDistance >= distance) {
                    if (player.getHealth() - event.getFinalDamage() > 0) {
                        m_plugin.unlockHat(player, hat);
                    }                    
                }
            }            
        }        
    }
    
    /**
     * 
     * @param event 
     */
    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        
        final Player player = (Player) event.getWhoClicked();
        if (!m_plugin.hasPermission(player, "hat.command")) {
            return;
        }
        
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            ItemStack item = recipe.getResult();
            if (item != null) {
                Material type = item.getType();
                
                List<Hat> hats = m_plugin.getUnlockableHats(UnlockableEventType.Craft);
                for (Hat hat : hats) {
                    if (m_plugin.hasPermission(player, hat.getPermission())) {
                        continue;
                    }
                    
                    if (hat.hasParameter("Type")) {
                        Material hatType = Material.valueOf(hat.getParameter("Type"));
                        if (hatType == type) {
                            m_plugin.unlockHat(player, hat);
                            return;
                        }
                    }
                } 
            }
        }
    }
    
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {

        Entity entity = event.getEntity().getKiller();
        if (entity == null) {
            return;
        }

        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile)entity;
            if (projectile.getShooter() instanceof LivingEntity) {
                entity = (Entity) projectile.getShooter();
            }
        } 
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        final Player player = (Player)entity;
        if (!m_plugin.hasPermission(player, "hat.command")) {
            return;
        }

        List<Hat> hats = m_plugin.getUnlockableHats(UnlockableEventType.Kill);
        for (Hat hat : hats) {
            if (m_plugin.hasPermission(player, hat.getPermission())) {
                continue;
            }

            if (hat.hasParameter("Type")) {
                EntityType entityType = EntityType.valueOf(hat.getParameter("Type"));
                if (entityType == entity.getType()) {
                    if (hat.hasParameter("Location")) {
                        if (hat.getParameter("Location").equalsIgnoreCase("Underwater")) {
                            Material headBlock = player.getEyeLocation().getBlock().getType();                            
                            if (headBlock != Material.WATER && headBlock != Material.STATIONARY_WATER) {
                                continue;
                            }
                        }
                    }
                    
                    m_plugin.unlockHat(player, hat);
                    return;
                }
            }
        }
    }
    
}
