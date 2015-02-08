/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.stylinhats.data;

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
    private String m_unlockCode = "FREE";
    private String m_unlockMethod = "This hat is free!";
    
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
        m_unlockCode = (String) loadJsonString(hatJson, "unlockCode", m_unlockCode);
        
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
    public String getUnlockCode() {
        return m_unlockCode;
    }
    
    /**
     * 
     * @return 
     */
    public String getUnlockMethod() {
        return m_unlockMethod;
    }
}
