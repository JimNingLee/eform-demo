/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cabsoft.pdf.form.crypt;

import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author Administrator
 */
public abstract class CharacterEncoder {

    /**
     *
     */
    protected PrintStream pStream;

    /**
     *
     */
    public CharacterEncoder() {
    }

    /**
     *
     * @return
     */
    protected abstract int bytesPerAtom();

    /**
     *
     * @return
     */
    protected abstract int bytesPerLine();

    /**
     *
     * @param outputstream
     * @throws IOException
     */
    protected void encodeBufferPrefix(OutputStream outputstream) throws IOException {
        pStream = new PrintStream(outputstream);
    }

    /**
     *
     * @param outputstream
     * @throws IOException
     */
    protected void encodeBufferSuffix(OutputStream outputstream) throws IOException {
    }

    /**
     *
     * @param outputstream
     * @param i
     * @throws IOException
     */
    protected void encodeLinePrefix(OutputStream outputstream, int i) throws IOException {
    }

    /**
     *
     * @param outputstream
     * @throws IOException
     */
    protected void encodeLineSuffix(OutputStream outputstream) throws IOException {
        pStream.println();
    }

    /**
     *
     * @param outputstream
     * @param abyte0
     * @param i
     * @param j
     * @throws IOException
     */
    protected abstract void encodeAtom(OutputStream outputstream, byte abyte0[], int i, int j) throws IOException;

    /**
     *
     * @param inputstream
     * @param abyte0
     * @return
     * @throws IOException
     */
    protected int readFully(InputStream inputstream, byte abyte0[]) throws IOException {
        for (int i = 0; i < abyte0.length; i++) {
            int j = inputstream.read();
            if (j == -1) {
                return i;
            }
            abyte0[i] = (byte) j;
        }

        return abyte0.length;
    }

    /**
     *
     * @param inputstream
     * @param outputstream
     * @param wordwrap
     * @throws IOException
     */
    public void encode(InputStream inputstream, OutputStream outputstream, boolean wordwrap) throws IOException {
        byte abyte0[] = new byte[bytesPerLine()];
        encodeBufferPrefix(outputstream);
        do {
            int j = readFully(inputstream, abyte0);
            if (j == 0) {
                break;
            }
            encodeLinePrefix(outputstream, j);
            for (int i = 0; i < j; i += bytesPerAtom()) {
                if (i + bytesPerAtom() <= j) {
                    encodeAtom(outputstream, abyte0, i, bytesPerAtom());
                } else {
                    encodeAtom(outputstream, abyte0, i, j - i);
                }
            }

            if (j < bytesPerLine()) {
                break;
            }
            if(wordwrap==true) encodeLineSuffix(outputstream);
        } while (true);
        encodeBufferSuffix(outputstream);
    }

    /**
     *
     * @param abyte0
     * @param outputstream
     * @param wrodwrap
     * @throws IOException
     */
    public void encode(byte abyte0[], OutputStream outputstream, boolean wrodwrap) throws IOException {
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        encode(((InputStream) (bytearrayinputstream)), outputstream, wrodwrap);
    }

    /**
     *
     * @param abyte0
     * @param wordwrap
     * @return
     */
    public String encode(byte abyte0[], boolean wordwrap) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        String s = null;
        try {
            encode(((InputStream) (bytearrayinputstream)), ((OutputStream) (bytearrayoutputstream)), wordwrap);
            s = bytearrayoutputstream.toString("8859_1");
        } catch (Exception exception) {
            throw new Error("ChracterEncoder::encodeBuffer internal error");
        }
        return s;
    }

    private byte[] getBytes(ByteBuffer bytebuffer) {
        byte abyte0[] = null;
        if (bytebuffer.hasArray()) {
            byte abyte1[] = bytebuffer.array();
            if (abyte1.length == bytebuffer.capacity() && abyte1.length == bytebuffer.remaining()) {
                abyte0 = abyte1;
                bytebuffer.position(bytebuffer.limit());
            }
        }
        if (abyte0 == null) {
            abyte0 = new byte[bytebuffer.remaining()];
            bytebuffer.get(abyte0);
        }
        return abyte0;
    }

    /**
     *
     * @param bytebuffer
     * @param outputstream
     * @param wordwrap
     * @throws IOException
     */
    public void encode(ByteBuffer bytebuffer, OutputStream outputstream, boolean wordwrap) throws IOException {
        byte abyte0[] = getBytes(bytebuffer);
        encode(abyte0, outputstream, wordwrap);
    }

    /**
     *
     * @param bytebuffer
     * @param wordwrap
     * @return
     */
    public String encode(ByteBuffer bytebuffer, boolean wordwrap) {
        byte abyte0[] = getBytes(bytebuffer);
        return encode(abyte0, wordwrap);
    }

    /**
     *
     * @param inputstream
     * @param outputstream
     * @param wordwrap
     * @throws IOException
     */
    public void encodeBuffer(InputStream inputstream, OutputStream outputstream, boolean wordwrap) throws IOException {
        byte abyte0[] = new byte[bytesPerLine()];
        encodeBufferPrefix(outputstream);
        int j;
        do {
            j = readFully(inputstream, abyte0);
            if (j == 0) {
                break;
            }
            encodeLinePrefix(outputstream, j);
            for (int i = 0; i < j; i += bytesPerAtom()) {
                if (i + bytesPerAtom() <= j) {
                    encodeAtom(outputstream, abyte0, i, bytesPerAtom());
                } else {
                    encodeAtom(outputstream, abyte0, i, j - i);
                }
            }

            if(wordwrap==true) encodeLineSuffix(outputstream);
        } while (j >= bytesPerLine());
        encodeBufferSuffix(outputstream);
    }

    /**
     *
     * @param abyte0
     * @param outputstream
     * @param wordwrap
     * @throws IOException
     */
    public void encodeBuffer(byte abyte0[], OutputStream outputstream, boolean wordwrap) throws IOException {
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        encodeBuffer(((InputStream) (bytearrayinputstream)), outputstream, wordwrap);
    }

    /**
     *
     * @param abyte0
     * @param wordwrap
     * @return
     */
    public String encodeBuffer(byte abyte0[], boolean wordwrap) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        try {
            encodeBuffer(((InputStream) (bytearrayinputstream)), ((OutputStream) (bytearrayoutputstream)), wordwrap);
        } catch (Exception exception) {
            throw new Error("ChracterEncoder::encodeBuffer internal error");
        }
        return bytearrayoutputstream.toString();
    }

    /**
     *
     * @param bytebuffer
     * @param outputstream
     * @param wordwrap
     * @throws IOException
     */
    public void encodeBuffer(ByteBuffer bytebuffer, OutputStream outputstream, boolean wordwrap) throws IOException {
        byte abyte0[] = getBytes(bytebuffer);
        encodeBuffer(abyte0, outputstream, wordwrap);
    }

    /**
     *
     * @param bytebuffer
     * @param wordwrap
     * @return
     */
    public String encodeBuffer(ByteBuffer bytebuffer, boolean wordwrap) {
        byte abyte0[] = getBytes(bytebuffer);
        return encodeBuffer(abyte0, wordwrap);
    }
}
