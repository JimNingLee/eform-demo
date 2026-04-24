package com.cabsoft.config;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class ConfigManagerFactory {
    private static HashMap<File, ConfigManager> configManagers = new HashMap<File, ConfigManager>();
    private static HashMap<File, Long> versionTimes = new HashMap<File, Long>();

    /**
     * Returns Instance.
     * @param file
     * @return
     * @throws Exception
     */
    public synchronized static ConfigManager getConfigManager(File file) throws Exception {
        // Gets specifed instance and version time.
        ConfigManager instance = configManagers.get(file);
        Long versionTime = versionTimes.get(file);
        // If there's no specified instance, create and return new instance.
        if (instance == null) {
            createConfigManager(file);
            return configManagers.get(file);
        }
        // If version time is different from previous version time, create and store instance.
        if (file.lastModified() != versionTime.longValue()) {
            removeConfigManager(file);
            createConfigManager(file);
            return configManagers.get(file);
        }
        // If same configuration file and same version time(no modification), return current instance.
        return instance;
    }

    public synchronized static ConfigManager getConfigManager(String fs) throws Exception {
    	
        File file = new File(fs);
        // Gets specifed instance and version time.
        ConfigManager instance = configManagers.get(file);
        Long versionTime = versionTimes.get(file);
        // If there's no specified instance, create and return new instance.
        if (instance == null) {
            createConfigManager(file);
            return configManagers.get(file);
        }
        // If version time is different from previous version time, create and store instance.
        if (file.lastModified() != versionTime.longValue()) {
            removeConfigManager(file);
            createConfigManager(file);
            return configManagers.get(file);
        }
        // If same configuration file and same version time(no modification), return current instance.
        return instance;
    }

    /**
     * Creates Config instance in config file.
     * @param file
     * @throws Exception
     */
    protected synchronized static void createConfigManager(File file) throws Exception {
        ConfigManager configManager = new ConfigManager();
        javax.xml.parsers.DocumentBuilder documentBuilder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.parse(file);
        org.w3c.dom.NodeList nodeList = document.getElementsByTagName("Code");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node node = nodeList.item(i);
            String name = node.getAttributes().getNamedItem("name").getTextContent();
            String configString = node.getTextContent();
            
            Config config = Config.newInstance(configString);
            configManager.addConfig(name, config);
        }
        // Adding Static Instance.
        configManagers.put(file, configManager);
        versionTimes.put(file, new Long(file.lastModified()));
    }

    /**
     * Creates Config instance in config file.
     * @param file
     * @throws Exception
     */
    protected synchronized static void createConfigManager1(File file) throws Exception {
        ConfigManager configManager = new ConfigManager();
        javax.xml.parsers.DocumentBuilder documentBuilder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.parse(file);
        org.w3c.dom.NodeList nodeList = document.getElementsByTagName("Error");
        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node node = nodeList.item(i);
            String name = node.getAttributes().getNamedItem("Code").getTextContent();
            String configString = node.getTextContent();
            Config config = Config.newInstance(configString);
            configManager.addConfig(name, config);
        }
        // Adding Static Instance.
        configManagers.put(file, configManager);
        versionTimes.put(file, new Long(file.lastModified()));
    }

    /**
     *
     * @param file
     * @throws Exception
     */
    protected synchronized static void removeConfigManager(File file) throws Exception {
        ConfigManager configManager = configManagers.get(file);
        configManager.removeConfigs();
        configManagers.remove(file);
        versionTimes.remove(file);
    }

    /**
     * Returns Array of Instance Files.
     * @return
     * @throws Exception
     */
    public static File[] getConfigManagerFiles() throws Exception {
        File[] instanceFiles = new File[configManagers.size()];
        Set<Entry<File, ConfigManager>> set = configManagers.entrySet();
        Iterator<Entry<File, ConfigManager>> iter = set.iterator();
        int idx = 0;
        while (iter.hasNext()) {
            Entry<File, ConfigManager> entry = iter.next();
            instanceFiles[idx] = entry.getKey();
        }
        return instanceFiles;
    }
}
