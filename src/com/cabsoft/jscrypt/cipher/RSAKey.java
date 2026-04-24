package com.cabsoft.jscrypt.cipher;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.Serializable;

public class RSAKey  implements Serializable {
	private static final long serialVersionUID = -2950743952895913849L;
	private BigInteger privateExponent;
	private BigInteger publicExponent;
	private BigInteger modulus;

	public RSAKey(BigInteger d, BigInteger e, BigInteger m) {
		this.privateExponent = d;
		this.publicExponent = e;
		this.modulus = m;
	}

	public BigInteger getPrivateExponent() {
		return this.privateExponent;
	}

	public BigInteger getPublicExponent() {
		return this.publicExponent;
	}

	public BigInteger getModulus() {
		return this.modulus;
	}

	public static RSAKey generate(int nbit) {
		// generate an N-bit (roughly) public and private key

		SecureRandom random = new SecureRandom();
		BigInteger one = new BigInteger("1");

		BigInteger p = BigInteger.probablePrime(nbit / 2, random);
		BigInteger q = BigInteger.probablePrime(nbit / 2, random);
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

		BigInteger m = p.multiply(q);
		BigInteger e = new BigInteger("65537"); // common value in practice = 2^16 + 1
 
		BigInteger d = e.modInverse(phi);

		return new RSAKey(d, e, m);
	}

	public static String toHex(BigInteger value) {
		byte b[] = value.toByteArray();

		StringBuffer strbuf = new StringBuffer(b.length * 2);
		int i;

		for (i = 0; i < b.length; i++) {
			if (((int) b[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) b[i] & 0xff, 16));
		}

		return strbuf.toString();
	}
}
