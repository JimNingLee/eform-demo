package com.cabsoft.jscrypt.cipher;
import com.cabsoft.jscrypt.cipher.RSAKey;

import java.math.BigInteger;

public class RSA {

	private RSAKey key;
	
	public RSA(int nbit){
		key = RSAKey.generate(nbit);
	}
	
	public RSA(){
		this(1024);
	}
	
	public RSA(RSAKey key){
		this.key = key;
	}
		
	public String decrypt(String encText){
		BigInteger modulus = key.getModulus();
		BigInteger privateExponent = key.getPrivateExponent();
		 
		BigInteger enc = new BigInteger(encText, 16);
		BigInteger dec = enc.modPow(privateExponent, modulus);
		
//		System.out.println("DEC = " + asHex(dec.toByteArray()));
		
		String plainText =  new String(dec.toByteArray());
//		System.out.println("decrypted strnig:" + new String(dec.toByteArray()));
		
		return plainText;
	}

	public static String asHex (byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}	

}
