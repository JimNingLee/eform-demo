package com.cabsoft.jscrypt.cipher;

import com.cabsoft.jscrypt.cipher.RSA;
import com.cabsoft.jscrypt.cipher.RSAKey;
import com.cabsoft.jscrypt.cipher.RSAKeyInstance;
import com.cabsoft.jscrypt.cipher.TEA;

public class JSCrypt {
	public static String Decrypt(String cipherText, String pwd) throws Exception{
		RSAKeyInstance key = RSAKeyInstance.getInstance();
		RSAKey rsaKey = key.getKey();
		com.cabsoft.jscrypt.cipher.RSA rsa = new RSA(rsaKey);
		String teaKey = rsa.decrypt(pwd);
		com.cabsoft.jscrypt.cipher.TEA tea = new TEA(teaKey);
		return tea.decrypt(cipherText);
	}
}
