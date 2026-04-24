/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cabsoft.pdf.form.crypt;

/**
 *
 * @author Administrator
 */
public class Base64 {

    /**
     *
     * @param src
     * @return
     * @throws Exception
     */
    public static String encode(byte[] src) throws Exception{
        String ret = "";
        ret = new BASE64Encoder().encode(src, false);
        return ret;
    }

    /**
     *
     * @param src
     * @param wordwrap
     * @return
     * @throws Exception
     */
    public static String encode(byte[] src, boolean wordwrap) throws Exception{
        String ret = "";

        ret = new BASE64Encoder().encode(src, wordwrap);
        return ret;
    }

    /**
     *
     * @param src
     * @return
     * @throws Exception
     */
    public static String encode(String src) throws Exception{
        String ret = "";
        ret = new BASE64Encoder().encode(src.getBytes(), false);
        return ret;
    }

    /**
     *
     * @param src
     * @param wordwrap
     * @return
     * @throws Exception
     */
    public static String encode(String src, boolean wordwrap) throws Exception{
        String ret = "";

        ret = new BASE64Encoder().encode(src.getBytes(), wordwrap);
        return ret;
    }

    /**
     *
     * @param src
     * @return
     * @throws Exception
     */
    public static byte[] decode2byte(String src) throws Exception{
        return new BASE64Decoder().decodeBuffer(src);
    }

    /**
     *
     * @param src
     * @return
     * @throws Exception
     */
    public static String decode(String src) throws Exception{
        byte[] ret = decode2byte(src);
        return new String(ret);
    }

    /**
     *
     * @param src
     * @param charset
     * @return
     * @throws Exception
     */
    public static String decode(String src, String charset) throws Exception{
        byte[] ret = decode2byte(src);
        return new String(ret, charset);
    }

}
