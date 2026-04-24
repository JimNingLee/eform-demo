package com.cabsoft.jscrypt.cipher;

import java.util.Date;

public class RSAKeyInstance {
	protected static Object lock = new Object();
	
	private com.cabsoft.jscrypt.cipher.RSAKey key = null;
	
	private static final long duration = 30L;
	private static RSAKeyInstance instance = null;
	private static long instanceTime = 0L;
	
	public RSAKeyInstance(){
		if(key==null){
			synchronized (lock) {
				instanceTime = new Date().getTime() + duration * 1000 * 60 * 60 * 24;
				key = com.cabsoft.jscrypt.cipher.RSAKey.generate(1024);
			}
		}
	}
	
	public static RSAKeyInstance getInstance(){
		if(instance==null || checkUpdateKey()){
			instance = new RSAKeyInstance();
		}
		return instance;
	}
	
	private static boolean checkUpdateKey(){
		long currentTime = new Date().getTime();
		return instanceTime<=currentTime;
	}

	public RSAKey getKey() {
		return key;
	}
	
}
