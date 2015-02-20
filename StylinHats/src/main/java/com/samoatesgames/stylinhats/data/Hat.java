/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.stylinhats.data;

import com.samoatesgames.stylinhats.UnlockType;
import com.samoatesgames.stylinhats.UnlockableEventType;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.json.simple.JSONObject;

/**
 *
 * @author Sam
 */
public class Hat {

    private Material m_icon = Material.CHEST;
    private String m_name = "Hat";
    private String m_type = "block";
    private UnlockType m_unlockCode = UnlockType.Free;
    private String m_unlockMethod = "This hat is free!";
    private UnlockableEventType m_unlockableType = UnlockableEventType.Break;
    private final Map<String, String> m_parameters = new HashMap<String, String>();
    
    /**
     * 
     */
    public Hat() {        
    }

    /**
     * 
     * @param hatJson
     */
    public void loadHat(JSONObject hatJson) {

        // name
        m_name = (String) loadJsonString(hatJson, "name", m_name);

        // icon
        m_icon = Material.valueOf((String) loadJsonString(hatJson, "icon", m_icon));

        // type
        m_type = (String) loadJsonString(hatJson, "type", m_type);
        
        // unlock code
        String unlockCode = (String) loadJsonString(hatJson, "unlockCode", m_unlockCode.name());
        if (unlockCode.startsWith("Unlockable")) {
            m_unlockCode = UnlockType.Unlockable;
            String[] parts = unlockCode.split(":");
            m_unlockableType = UnlockableEventType.valueOf(parts[1]);
            
            for (int i = 2; i < parts.length; ++i) {
                String[] param = parts[i].split(",");
                if (param.length == 2) {
                    m_parameters.put(param[0], param[1]);
                }
            }            
        } else {
            m_unlockCode = UnlockType.valueOf(unlockCode);
        }
         
        // unlock method
        m_unlockMethod = (String) loadJsonString(hatJson, "unlockMethod", m_unlockMethod);       
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private Object loadJsonString(JSONObject json, String key, Object defaultValue) {

        if (!json.containsKey(key)) {
            return defaultValue;
        }
        return json.get(key);
    }
    
    /**
     * 
     * @return 
     */
    public Material getIcon() {
        return m_icon;
    }

    /**
     * 
     * @return 
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * 
     * @return 
     */
    public String getType() {
        return m_type;
    }
    
    /**
     * 
     * @return 
     */
    public UnlockType getUnlockCode() {
        return m_unlockCode;
    }
    
    /**
     * 
     * @return 
     */
    public String getUnlockMethod() {
        return m_unlockMethod;
    }
    
    /**
     * 
     * @return 
     */
    public UnlockableEventType getUnlockableEventType() {
        return m_unlockableType;
    }
    
    /**
     * 
     * @return 
     */
    public String getPermission() {
        return "hat." + m_name.toLowerCase().replaceAll(" ", "_");
    }
    
    /**
     * 
     * @param name
     * @return 
     */
    public boolean hasParameter(String name) {
        return m_parameters.containsKey(name);
    }
    
    /**
     * 
     * @param name
     * @return 
     */
    public String getParameter(String name) {
        return m_parameters.get(name);
    }
}
