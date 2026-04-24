package com.cabsoft.config;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigManager {
	private static Log log = LogFactory.getLog(ConfigManager.class);
    private static LinkedHashMap<String, Config> configs = new LinkedHashMap<String, Config>();

    /**
     * Private constructor
     */
    protected ConfigManager() {
    }

    /**
     * Returns specified config instance.
     * @param name
     * @return
     * @throws Exception
     */
    public Config getConfig(String name) throws Exception {
        Config config = configs.get(name);
        if (config == null) {
            ConfigManagerException e = new ConfigManagerException(ConfigManagerException.CONFIG_NOT_FOUND);
            e.printStackTrace();
            throw e;
        }
        config.setName(name);
        return config;
    }

    private boolean checkKorean(String str){
    	for(int i=0; i<str.length(); i++){
    		char c = str.charAt(i);
    		if( ( 0xAC00 <= c && c <= 0xD7A3 ) || ( 0x3131 <= c && c <= 0x318E ) ){
    			return true;
    		}
    	}
    	return false;
    }
    
    public String getValue(String name) throws Exception {
    	if(checkKorean(name)){
    		return name;
    	}else{
	        Config config = configs.get(name);
	        if (config == null) {
	            //ConfigManagerException e = new ConfigManagerException(ConfigManagerException.CONFIG_NOT_FOUND);
	            log.debug("Config [ " + name + " ] was Not Found");
	            return "";
	        }
	        config.setName(name);
	        return config.getString();
    	}
    }

    public String getValue(String name, String def) {
        Config config = configs.get(name);
        if (config == null) {
            return def;
        }
        config.setName(name);
        return config.getString();
    }

    public boolean getBoolean(String name) throws Exception {
        Config config = configs.get(name);
        if (config == null) {
            ConfigManagerException e = new ConfigManagerException(ConfigManagerException.CONFIG_NOT_FOUND);
            e.printStackTrace();
            throw e;
        }
        config.setName(name);
        return config.getBoolean();
    }

    public boolean getBoolean(String name, boolean def) {
        Config config = configs.get(name);
        if (config == null) {
            return def;
        }
        config.setName(name);
        return config.getBoolean();
    }

    public int getInteger(String name) throws Exception {
        Config config = configs.get(name);
        if (config == null) {
            ConfigManagerException e = new ConfigManagerException(ConfigManagerException.CONFIG_NOT_FOUND);
            e.printStackTrace();
            throw e;
        }
        config.setName(name);
        return Integer.parseInt(config.getString());
    }

    public int getInteger(String name, int def) {
        Config config = configs.get(name);
        if (config == null) {
            return def;
        }
        config.setName(name);
        return Integer.parseInt(config.getString());
    }

    public Long getLong(String name) throws Exception {
        Config config = configs.get(name);
        if (config == null) {
            ConfigManagerException e = new ConfigManagerException(ConfigManagerException.CONFIG_NOT_FOUND);
            e.printStackTrace();
            throw e;
        }
        config.setName(name);
        return Long.parseLong(config.getString());
    }

    public Long getLong(String name, long def) {
        Config config = configs.get(name);
        if (config == null) {
            return def;
        }
        config.setName(name);
        return Long.parseLong(config.getString());
    }

    /**
     * Adds new config instance into query pool instance.
     * @param name
     * @param query
     * @throws Exception
     */
    protected void addConfig(String name, Config config) throws Exception {
        configs.put(name, config);
    }

    /**
     * Removes specified config instance from query pool instance.
     * @param name
     */
    protected void removeConfig(String name) {
        configs.remove(name);
    }

    /**
     * Removes all config instance in query pool instance.
     */
    protected void removeConfigs() {
        configs.clear();
    }

    /**
     * Returns size of config instance.
     * @return
     */
    public int getCurrentConfigSize() {
        return configs.size();
    }

    @SuppressWarnings("rawtypes")
    public String[] getNames(){
        String[] ret = null;
        int size = configs.size();

		Iterator it = configs.keySet().iterator();
        ret = new String[size];
        int cnt = 0;
        while(it.hasNext()){
            ret[cnt] = (String)it.next();
            cnt++;
        }

        return ret;
    }
}
