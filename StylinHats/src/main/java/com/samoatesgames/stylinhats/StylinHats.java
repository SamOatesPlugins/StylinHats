package com.samoatesgames.stylinhats;

import com.samoatesgames.samoatesplugincore.plugin.SamOatesPlugin;
import com.samoatesgames.stylinhats.command.HatCommand;
import com.samoatesgames.stylinhats.data.Hat;
import com.samoatesgames.stylinhats.data.HatStatus;
import com.samoatesgames.stylinhats.listeners.HatListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The main plugin class
 *
 * @author Sam Oates <sam@samoatesgames.com>
 */
public final class StylinHats extends SamOatesPlugin {

    /**
     *
     */
    private TreeMap<String, Hat> m_hats = new TreeMap<String, Hat>();

    /**
     * 
     */
    public static final String METAKEY = ChatColor.GOLD + "A Stylin Hat";

    /**
     * 
     */
    private Permission m_permissions = null;
    
    /**
     * Class constructor
     */
    public StylinHats() {
        super("StylinHats", "Hats", ChatColor.GOLD);
    }

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        super.onEnable();

        loadHats();

        m_commandManager.registerCommandHandler("hat", new HatCommand(this));

        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new HatListener(this), this);
        
        // Vault hookup
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            m_permissions = permissionProvider.getProvider();
        }
        
        this.logInfo("Succesfully enabled.");
    }

    /**
     * Register all configuration settings
     */
    public void setupConfigurationSettings() {

    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        m_hats.clear();
        m_permissions = null;
    }

    /**
     *
     */
    public void loadHats() {

        String configPath = this.getDataFolder() + File.separator + "hats.json";

        // Make sure the kit file exists
        File file = new File(configPath);
        if (!file.exists()) {
            return;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject hatsJson = (JSONObject) parser.parse(new FileReader(configPath));

            JSONArray hats = (JSONArray) hatsJson.get("hats");
            for (Object hatRaw : hats) {
                JSONObject jsonHat = (JSONObject) hatRaw;
                Hat newHat = new Hat();
                newHat.loadHat(jsonHat);
                m_hats.put(newHat.getName().toLowerCase(), newHat);
            }

        } catch (IOException ex) {
            this.logException("Error loading '" + configPath + "'", ex);
        } catch (ParseException ex) {
            this.logException("Error parsing '" + configPath + "'", ex);
        }

    }

    /**
     *
     * @return
     */
    public Map<String, Hat> getHats() {
        return Collections.synchronizedSortedMap(m_hats);
    }

    /**
     *
     * @param player
     * @param hat
     * @return
     */
    public HatStatus canPlayerEquiptHat(Player player, Hat hat) {

        HatStatus status = new HatStatus();

        if (hat.getUnlockCode() == UnlockType.Free) {
            status.canEquipt = true;
        }
        
        if (this.hasPermission(player, hat.getPermission())) {
            status.canEquipt = true;
        }

        return status;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getRawSlot() != 5) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (!isHat(item)) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        sendMessage(player, "Please use '/hat remove' to remove your hat.");
        event.getView().close();
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        List<ItemStack> hatlessDrops = new ArrayList<ItemStack>();
        List<ItemStack> drops = event.getDrops();

        for (ItemStack item : drops) {
            if (!isHat(item)) {
                hatlessDrops.add(item);
            }
        }

        drops.clear();
        drops.addAll(hatlessDrops);
    }

    /**
     * 
     * @param player
     * @param name
     * @param material 
     */
    public void equiptHat(Player player, String name, Material material) {

        EntityEquipment equiptment = player.getEquipment();

        ItemStack itemHat = new ItemStack(material);
        ItemMeta meta = itemHat.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lore = new ArrayList<String>();
        lore.add(StylinHats.METAKEY);
        meta.setLore(lore);

        itemHat.setItemMeta(meta);

        ItemStack currentHat = equiptment.getHelmet();
        if (currentHat != null && currentHat.getType() != Material.AIR) {
            if (!isHat(currentHat)) {
                this.sendMessage(player, "Your " + currentHat.getType().name().toLowerCase().replaceAll("_", " ") + " has been put in your inventory...");
                player.getInventory().addItem(currentHat);
                player.updateInventory();
            }
        }

        equiptment.setHelmet(itemHat);
    }

    /**
     *
     * @param item
     * @return
     */
    public boolean isHat(ItemStack item) {

        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) {
            return false;
        }

        if (!meta.getLore().get(0).equals(StylinHats.METAKEY)) {
            return false;
        }

        return true;
    }
    
    /**
     * 
     * @param type
     * @return 
     */
    public List<Hat> getUnlockableHats(UnlockableEventType type) {
        List<Hat> hats = new ArrayList<Hat>();
        
        for (Hat hat : m_hats.values()) {
            if (hat.getUnlockCode() == UnlockType.Unlockable
                && hat.getUnlockableEventType() == type) {
                hats.add(hat);
            }            
        }
        
        return hats;
    }
    
    /**
     * 
     * @param player
     * @param hat 
     */
    public void unlockHat(Player player, Hat hat) {
        
        if (!hasPermission(player, hat.getPermission())) {
            m_permissions.playerAdd(null, player, hat.getPermission());
            player.playEffect(EntityEffect.FIREWORK_EXPLODE);
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
            sendMessage(player, "You unlocked the " + hat.getName() + " hat!");
        }
        
    }
}
