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
public abstract class CharacterDecoder {

    /**
     *
     */
    public CharacterDecoder() {
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
     * @param pushbackinputstream
     * @param outputstream
     * @throws IOException
     */
    protected void decodeBufferPrefix(PushbackInputStream pushbackinputstream, OutputStream outputstream) throws IOException {
    }

    /**
     *
     * @param pushbackinputstream
     * @param outputstream
     * @throws IOException
     */
    protected void decodeBufferSuffix(PushbackInputStream pushbackinputstream, OutputStream outputstream) throws IOException {
    }

    /**
     *
     * @param pushbackinputstream
     * @param outputstream
     * @return
     * @throws IOException
     */
    protected int decodeLinePrefix(PushbackInputStream pushbackinputstream, OutputStream outputstream) throws IOException {
        return bytesPerLine();
    }

    /**
     *
     * @param pushbackinputstream
     * @param outputstream
     * @throws IOException
     */
    protected void decodeLineSuffix(PushbackInputStream pushbackinputstream, OutputStream outputstream) throws IOException {
    }

    /**
     *
     * @param pushbackinputstream
     * @param outputstream
     * @param i
     * @throws IOException
     */
    protected void decodeAtom(PushbackInputStream pushbackinputstream, OutputStream outputstream, int i) throws IOException {
        throw new CEStreamExhausted();
    }

    /**
     *
     * @param inputstream
     * @param abyte0
     * @param i
     * @param j
     * @return
     * @throws IOException
     */
    protected int readFully(InputStream inputstream, byte abyte0[], int i, int j) throws IOException {
        for (int k = 0; k < j; k++) {
            int l = inputstream.read();
            if (l == -1) {
                return k != 0 ? k : -1;
            }
            abyte0[k + i] = (byte) l;
        }

        return j;
    }

    /**
     *
     * @param inputstream
     * @param outputstream
     * @throws IOException
     */
    @SuppressWarnings("unused")
	public void decodeBuffer(InputStream inputstream, OutputStream outputstream) throws IOException {
        int j = 0;
        PushbackInputStream pushbackinputstream = new PushbackInputStream(inputstream);
        decodeBufferPrefix(pushbackinputstream, outputstream);
        try {
            do {
                int k = decodeLinePrefix(pushbackinputstream, outputstream);
                int i;
                for (i = 0; i + bytesPerAtom() < k; i += bytesPerAtom()) {
                    decodeAtom(pushbackinputstream, outputstream, bytesPerAtom());
                    j += bytesPerAtom();
                }

                if (i + bytesPerAtom() == k) {
                    decodeAtom(pushbackinputstream, outputstream, bytesPerAtom());
                    j += bytesPerAtom();
                } else {
                    decodeAtom(pushbackinputstream, outputstream, k - i);
                    j += k - i;
                }
                decodeLineSuffix(pushbackinputstream, outputstream);
            } while (true);
        } catch (CEStreamExhausted cestreamexhausted) {
            decodeBufferSuffix(pushbackinputstream, outputstream);
        }
    }

    /**
     *
     * @param s
     * @return
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
	public byte[] decodeBuffer(String s) throws IOException {
        byte abyte0[] = new byte[s.length()];
        s.getBytes(0, s.length(), abyte0, 0);
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        decodeBuffer(((InputStream) (bytearrayinputstream)), ((OutputStream) (bytearrayoutputstream)));
        return bytearrayoutputstream.toByteArray();
    }

    /**
     *
     * @param inputstream
     * @return
     * @throws IOException
     */
    public byte[] decodeBuffer(InputStream inputstream) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        decodeBuffer(inputstream, ((OutputStream) (bytearrayoutputstream)));
        return bytearrayoutputstream.toByteArray();
    }

    /**
     *
     * @param s
     * @return
     * @throws IOException
     */
    public ByteBuffer decodeBufferToByteBuffer(String s) throws IOException {
        return ByteBuffer.wrap(decodeBuffer(s));
    }

    /**
     *
     * @param inputstream
     * @return
     * @throws IOException
     */
    public ByteBuffer decodeBufferToByteBuffer(InputStream inputstream) throws IOException {
        return ByteBuffer.wrap(decodeBuffer(inputstream));
    }
}
