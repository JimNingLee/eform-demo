package com.cabsoft;

import java.io.File;

import com.cabsoft.config.ConfigManager;
import com.cabsoft.config.ConfigManagerFactory;

public class ErrorCode {
	protected static Object lock = new Object();
	private static ErrorCode instance = null;
	
	private ConfigManager configManager = null;
	private String err_fs = "";
	
	private ErrorCode(String err_fs) throws Exception{
		this.err_fs = err_fs;
		configManager = null;
		Init();
	}
	
	private void Init() throws Exception{
		File fs = new File(err_fs);
		configManager = ConfigManagerFactory.getConfigManager(fs);
	}
	
	public static ErrorCode getInstance(){
		return instance;
	}
	
	public static ErrorCode getInstance(String err_fs) throws Exception{
		if (instance == null) {
			instance = new ErrorCode(err_fs);
		}
		return instance;
	}

	/**
	 * @return the configManager
	 */
	public ConfigManager getConfigManager() {
		return configManager;
	}

	/**
	 * @param configManager the configManager to set
	 */
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
}
