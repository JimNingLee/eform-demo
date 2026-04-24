package com.cabsoft.pdf.form.crypt;

import java.io.*;
import java.security.MessageDigest;

public class md5 {

    public byte[] getResult() {
        return result;
    }

    public md5() {
        result = null;
    }

    public String encrypt(byte abyte0[]) throws Exception {
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        messagedigest.update(abyte0);
        result = messagedigest.digest();
        return byteArrayToHexString(result);
    }

    public byte[] encrypt(String abyte0) throws Exception {
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        messagedigest.update(abyte0.getBytes());
        result = messagedigest.digest();
        return result;
    }

    public String encryptFile(String s) throws FileNotFoundException, IOException, Exception {
        FileInputStream fileinputstream = new FileInputStream(s);
        byte abyte0[] = new byte[fileinputstream.available()];
        fileinputstream.read(abyte0, 0, fileinputstream.available());
        fileinputstream.close();
        return encrypt(abyte0);
    }

    public String encryptText(String s) throws Exception {
        byte abyte0[] = s.getBytes();
        return encrypt(abyte0);
    }

    public String encryptTextU(String s) throws Exception {
        byte abyte0[] = s.getBytes("UTF-16LE");
        return encrypt(abyte0);
    }

    @SuppressWarnings("unused")
	private String byteArrayToHexString(byte abyte0[]) {
        boolean flag = false;
        String as[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2);
        for (int i = 0; i < abyte0.length; i++) {
            byte byte0 = (byte) (abyte0[i] & 0xf0);
            byte0 >>>= 4;
            byte0 &= 0xf;
            stringbuffer.append(as[byte0]);
            byte0 = (byte) (abyte0[i] & 0xf);
            stringbuffer.append(as[byte0]);
        }

        String s = new String(stringbuffer);
        return s;
    }
    private byte result[];
}
