package com.cabsoft.pdf.form.crypt;

//import java.io.PrintWriter;

/**
 * 
 * @author Administrator
 */
public class Hex {

    private Hex() {
    }

    /**
     *
     * @param hex
     * @return
     */
    public static String dumpHex(byte hex) {
        char hexChar[] = new char[2];
        hexChar[0] = hexDigits[hex >>> 4 & 0xf];
        hexChar[1] = hexDigits[hex & 0xf];
        return new String(hexChar);
    }

    /**
     *
     * @param hex
     * @param str
     */
    public static void appendHex(byte hex, StringBuffer str) {
        str.append(hexDigits[hex >>> 4 & 0xf]);
        str.append(hexDigits[hex & 0xf]);
    }

    /**
     *
     * @param strBuffer
     * @param byteArray
     * @param offset
     * @param length
     * @param separator
     */
    public static void dumpHex(StringBuffer strBuffer, byte byteArray[], int offset, int length, char separator) {
        if (byteArray == null || byteArray.length == 0) {
            return;
        }
        for (int i = offset; i < offset + length; i++) {
            appendHex(byteArray[i], strBuffer);
            if (separator != 0 && i != (offset + length) - 1) {
                strBuffer.append(separator);
            }
        }

    }

    /**
     *
     * @param sb
     * @param byteArray
     * @param offset
     * @param length
     * @param column
     * @param separator
     * @param indent
     */
    public static void prettyDump(StringBuffer sb, byte byteArray[], int offset, int length, int column, char separator, int indent) {
        int blockSize;
        if (separator == 0) {
            blockSize = column / 2;
        } else {
            blockSize = column / 3;
        }
        int leftOvers = length % blockSize;
        int lastBlockOffset = length - leftOvers;
        for (int i = 0; i < lastBlockOffset; i += blockSize) {
            if (indent > 0) {
                indent(sb, indent);
            }
            dumpHex(sb, byteArray, offset + i, blockSize, separator);
            sb.append("\n");
        }

        if (indent > 0) {
            indent(sb, indent);
        }
        dumpHex(sb, byteArray, lastBlockOffset, leftOvers, separator);
    }

    /**
     *
     * @param sb
     * @param byteArray
     * @param offset
     * @param length
     * @param column
     * @param separator
     */
    public static void prettyDump(StringBuffer sb, byte byteArray[], int offset, int length, int column, char separator) {
        prettyDump(sb, byteArray, offset, length, column, separator, 0);
    }

    /**
     *
     * @param byteArray
     * @param offset
     * @param length
     * @param separator
     * @return
     */
    public static String dumpHex(byte byteArray[], int offset, int length, char separator) {
        StringBuffer sb = new StringBuffer();
        dumpHex(sb, byteArray, offset, length, separator);
        return sb.toString();
    }

    /**
     *
     * @param byteArray
     * @param separator
     * @return
     */
    public static String dumpHex(byte byteArray[], char separator) {
        return dumpHex(byteArray, 0, byteArray.length, separator);
    }

    /**
     *
     * @param byteArray
     * @return
     */
    public static String dumpHex(byte byteArray[]) {
        return dumpHex(byteArray, '\0');
    }

    /**
     *
     * @param byteArray
     * @param offset
     * @param length
     * @param column
     * @param separator
     * @param indent
     * @return
     */
    public static String prettyDump(byte byteArray[], int offset, int length, int column, char separator, int indent) {
        StringBuffer sb = new StringBuffer();
        prettyDump(sb, byteArray, offset, length, column, separator, indent);
        return sb.toString();
    }

    /**
     *
     * @param byteArray
     * @param offset
     * @param length
     * @param column
     * @param separator
     * @return
     */
    public static String prettyDump(byte byteArray[], int offset, int length, int column, char separator) {
        StringBuffer sb = new StringBuffer();
        prettyDump(sb, byteArray, offset, length, column, separator, 0);
        return sb.toString();
    }

    /**
     *
     * @param byteArray
     * @param column
     * @param separator
     * @param indent
     * @return
     */
    public static String prettyDump(byte byteArray[], int column, char separator, int indent) {
        return prettyDump(byteArray, 0, byteArray.length, column, separator, indent);
    }

    /**
     *
     * @param byteArray
     * @param column
     * @param separator
     * @return
     */
    public static String prettyDump(byte byteArray[], int column, char separator) {
        return prettyDump(byteArray, 0, byteArray.length, column, separator, 0);
    }

    /**
     *
     * @param byteArray
     * @return
     */
    public static String prettyDump(byte byteArray[]) {
        return prettyDump(byteArray, 0, byteArray.length, 80, '\0');
    }

    /**
     *
     * @param hexString
     * @return
     */
    public static byte[] parseHexaString(String hexString) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hexString.length(); i++) {
            char current = hexString.charAt(i);
            if ('0' <= current && current <= '9' ||
                    'A' <= current && current <= 'F' ||
                    'a' <= current && current <= 'f') {
                sb.append(current);
            }
        }

        int leftover = sb.length() % 2;
        byte ret[] = new byte[sb.length() / 2 + leftover];
        if (leftover == 1) {
            ret[0] = parseHexaCharactor(sb.charAt(0));
            for (int i = 0; i < sb.length() / 2; i++) {
                ret[i + 1] = (byte) (parseHexaCharactor(sb.charAt(2 * i + 1)) << 4 | parseHexaCharactor(sb.charAt(2 * i + 2)));
            }
        } else {
            for (int i = 0; i < sb.length() / 2; i++) {
                ret[i] = (byte) (parseHexaCharactor(sb.charAt(2 * i)) << 4 | parseHexaCharactor(sb.charAt(2 * i + 1)));
            }
        }
        return ret;
    }

    private static byte parseHexaCharactor(char ch) {
        if ('0' <= ch && ch <= '9') {
            return (byte) (ch - 48);
        }
        if ('A' <= ch && ch <= 'F') {
            return (byte) (ch - 55);
        }
        if ('a' <= ch && ch <= 'f') {
            return (byte) (ch - 87);
        } else {
            return 0;
        }
    }

    private static void indent(StringBuffer buf, int n) {
        for (int i = 0; i < n; i++) {
            buf.append("    ");
        }
    }
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
