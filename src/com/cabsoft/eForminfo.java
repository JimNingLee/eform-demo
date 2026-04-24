package com.cabsoft;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

import com.cabsoft.utils.StringUtils;


@SuppressWarnings("unused")
public class eForminfo {
	private Logger logger = Logger.getLogger("eForminfo");
	private Map<String, String> infoMap = null;

	private static eForminfo instance = null;

	// 생성자
	private eForminfo() {
		infoMap = new HashMap<String, String>();
		setMapTime("StartDateTime");
	}

	private static class Singleton {
		private static final eForminfo instance = new eForminfo();
	}

	// 조회
	public static eForminfo getInstance() {
		return Singleton.instance;
	}

	// function
	public void setMap(String key, String value) {
		infoMap.put(key, value);
	}

	public void setMapTime(String key) {
		infoMap.put(key, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	public void setMapCount(String key) {
		String tmp = infoMap.get(key);
		long lng = 1;
		if (!StringUtils.isNull(tmp)) {
			lng = Long.valueOf(tmp) + 1;
		}
		infoMap.put(key, String.valueOf(lng));
	}

	public String getMap(String key) {
		return infoMap.get(key);
	}

	public Map getMap() {
		return infoMap;
	}

	public int getMapSize() {
		return infoMap.size();
	}

	public String toString() {
		return infoMap.toString();
	}

	public void saveMap(String fileNM) {
		try {
			Properties properties = new Properties();

			// for (Map.Entry<String, String> entry : infoMap.entrySet()) {
			// properties.put(entry.getKey(), entry.getValue());
			// }

			properties.putAll(infoMap);
			properties.store(new FileOutputStream(fileNM), null);
			logger.debug("fileNM:" + fileNM);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadMap(String fileNM) {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(fileNM));
			// for (String key : properties.stringPropertyNames()) {
			// infoMap.put(key, properties.get(key).toString());
			// }
			infoMap = new HashMap<String, String>((Map) properties);
			logger.debug("loadMap:" + fileNM);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
