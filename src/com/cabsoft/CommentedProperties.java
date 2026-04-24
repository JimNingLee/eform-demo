package com.cabsoft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * The CommentedProperties class is an extension of java.util.Properties to
 * allow retention of comment lines and blank (whitespace only) lines in the
 * properties file.
 *
 * Written for Java version 1.4
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommentedProperties extends java.util.Properties {

    private static final long serialVersionUID = 878131664782812301L;

    /**
     * Use a Vector to keep a copy of lines that are a comment or 'blank'
     */
    public Vector<String> lineData = new Vector(0, 1);

    /**
     * Use a Vector to keep a copy of lines containing a key, i.e. they are a
     * property.
     */
    public Vector<String> keyData = new Vector(0, 1);
    
    public String propertiesPath = "";

    public void load() throws Exception {
    	load("ISO-8859-1");
    }
    
    public void load(String encoding) throws Exception {
    	InputStream inStream = null;
    	FileInputStream fis = null;
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    	try
    	{
			if (classLoader != null)
			{
				propertiesPath = classLoader.getResource("reportexpress.properties").getPath();
				//propertiesPath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\webapps\\eformbank\\WEB-INF\\classes\\reportexpress.properties";
				//System.out.println("$$$$$$$$$$$$ propertiesPath : " + propertiesPath);
				//inStream = classLoader.getResourceAsStream("reportexpress.properties");
				fis = new FileInputStream(new File(propertiesPath));
				inStream = fis;
			}

			if(inStream == null){
				classLoader = getClass().getClassLoader();
				propertiesPath = classLoader.getResource("reportexpress.properties").getPath();
				fis = new FileInputStream(new File(propertiesPath));
				inStream = fis;
			}

			load(inStream, encoding);
    	} catch (Exception e) {
    		if(inStream != null){inStream.close();}
    		if(fis != null){fis.close();}
    	}
    	
    }
    
    /**
     * Load properties from the specified InputStream. Overload the load method
     * in Properties so we can keep comment and blank lines.
     *
     * @param inStream The InputStream to read.
     */
    public void load(InputStream inStream) throws IOException {
        load(inStream, "ISO-8859-1");
    }
    
    /**
     * @param inStream
     * @param encoding
     * @throws IOException 
     */
    public void load(InputStream inStream, String encoding) throws IOException {
        // The spec says that the file must be encoded using ISO-8859-1.
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, encoding));
        String line;

        while ((line = reader.readLine()) != null) {
            char c = 0;
            int pos = 0;
            // Leading whitespaces must be deleted first.
            while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                pos++;
            }

	    // If empty line or begins with a comment character, save this line
            // in lineData and save a "" in keyData.
            if ((line.length() - pos) == 0 || line.charAt(pos) == '#' || line.charAt(pos) == '!') {
                lineData.add(line);
                keyData.add("");
                continue;
            }

	    // The characters up to the next Whitespace, ':', or '='
            // describe the key. But look for escape sequences.
            // Try to short-circuit when there is no escape char.
            int start = pos;
            boolean needsEscape = line.indexOf('\\', pos) != -1;
            StringBuffer key = needsEscape ? new StringBuffer() : null;

            while (pos < line.length() && !Character.isWhitespace(c = line.charAt(pos++)) && c != '=' && c != ':') {
                if (needsEscape && c == '\\') {
                    if (pos == line.length()) {
			// The line continues on the next line. If there
                        // is no next line, just treat it as a key with an
                        // empty value.
                        line = reader.readLine();
                        if (line == null) {
                            line = "";
                        }
                        pos = 0;
                        while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                            pos++;
                        }
                    } else {
                        c = line.charAt(pos++);
                        switch (c) {
                            case 'n':
                                key.append('\n');
                                break;
                            case 't':
                                key.append('\t');
                                break;
                            case 'r':
                                key.append('\r');
                                break;
                            case 'u':
                                if (pos + 4 <= line.length()) {
                                    char uni = (char) Integer.parseInt(
                                            line.substring(pos, pos + 4), 16);
                                    key.append(uni);
                                    pos += 4;
                                } // else throw exception?
                                break;
                            default:
                                key.append(c);
                                break;
                        }
                    }
                } else if (needsEscape) {
                    key.append(c);
                }
            }

            boolean isDelim = (c == ':' || c == '=');

            String keyString;
            if (needsEscape) {
                keyString = key.toString();
            } else if (isDelim || Character.isWhitespace(c)) {
                keyString = line.substring(start, pos - 1);
            } else {
                keyString = line.substring(start, pos);
            }

            while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                pos++;
            }

            if (!isDelim && (c == ':' || c == '=')) {
                pos++;
                while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                    pos++;
                }
            }

            // Short-circuit if no escape chars found.
            if (!needsEscape) {
                put(keyString, line.substring(pos));
		// Save a "" in lineData and save this
                // keyString in keyData.
                lineData.add("");
                keyData.add(keyString);
                continue;
            }

            // Escape char found so iterate through the rest of the line.
            StringBuffer element = new StringBuffer(line.length() - pos);
            while (pos < line.length()) {
                c = line.charAt(pos++);
                if (c == '\\') {
                    if (pos == line.length()) {
                        // The line continues on the next line.
                        line = reader.readLine();

			// We might have seen a backslash at the end of
                        // the file. The JDK ignores the backslash in
                        // this case, so we follow for compatibility.
                        if (line == null) {
                            break;
                        }

                        pos = 0;
                        while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos))) {
                            pos++;
                        }
                        element.ensureCapacity(line.length() - pos + element.length());
                    } else {
                        c = line.charAt(pos++);
                        switch (c) {
                            case 'n':
                                element.append('\n');
                                break;
                            case 't':
                                element.append('\t');
                                break;
                            case 'r':
                                element.append('\r');
                                break;
                            case 'u':
                                if (pos + 4 <= line.length()) {
                                    char uni = (char) Integer.parseInt(line.substring(pos, pos + 4), 16);
                                    element.append(uni);
                                    pos += 4;
                                } // else throw exception?
                                break;
                            default:
                                element.append(c);
                                break;
                        }
                    }
                } else {
                    element.append(c);
                }
            }
            put(keyString, element.toString());
	    // Save a "" in lineData and save this
            // keyString in keyData.
            lineData.add("");
            keyData.add(keyString);
        }
    }

    public void store() throws Exception{
    	FileOutputStream fos = new FileOutputStream(propertiesPath);
    	store(fos);
    }
    
    public void store(String encoding) throws Exception{
    	FileOutputStream fos = new FileOutputStream(propertiesPath);
    	store(fos, encoding);
    }
    
    /**
     * Write the properties to the specified OutputStream.
     *
     * Overloads the store method in Properties so we can put back comment and
     * blank lines.
     *
     * @param out The OutputStream to write to.
     * @param header Ignored, here for compatability w/ Properties.
     *
     * @exception IOException
     */
    public void store(OutputStream out) throws IOException {
        store(out, "ISO-8859-1");
    }
    
    public void store(OutputStream out, String encoding) throws IOException {
        // The spec says that the file must be encoded using ISO-8859-1.
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, encoding));

        // We ignore the header, because if we prepend a commented header
        // then read it back in it is now a comment, which will be saved
        // and then when we write again we would prepend Another header...
        String line;
        String key;
        StringBuffer s = new StringBuffer();

        for (int i = 0; i < lineData.size(); i++) {
            line = (String) lineData.get(i);
            key = (String) keyData.get(i);
            if (key.length() > 0) { // This is a 'property' line, so rebuild it
                formatForOutput(key, s, true);
                s.append('=');
                formatForOutput((String) get(key), s, false);
                writer.println(s);
            } else { // was a blank or comment line, so just restore it
                writer.println(line);
            }
        }
        writer.flush();
    }

    /**
     * Need this method from Properties because original code has StringBuilder,
     * which is an element of Java 1.5, used StringBuffer instead (because this
     * code was written for Java 1.4)
     *
     * @param str - the string to format
     * @param buffer - buffer to hold the string
     * @param key - true if str the key is formatted, false if the value is
     * formatted
     */
    private void formatForOutput(String str, StringBuffer buffer, boolean key) {
        if (key) {
            buffer.setLength(0);
            buffer.ensureCapacity(str.length());
        } else {
            buffer.ensureCapacity(buffer.length() + str.length());
        }
        boolean head = true;
        int size = str.length();
        for (int i = 0; i < size; i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\n':
                    buffer.append("\\n");
                    break;
                case '\r':
                    buffer.append("\\r");
                    break;
                case '\t':
                    buffer.append("\\t");
                    break;
                case ' ':
                    buffer.append(head ? "\\ " : " ");
                    break;
                case '\\':
                case '!':
                case '#':
                case '=':
/*
 * 문자 :가 \:로 저장되는 것을 방지하기 위해 주석 처리함
 */
//                case ':':
//                    buffer.append('\\').append(c);
//                    break;
                default:
                    if (c < ' ' || c > '~') {
                    	/*******************************************
                    	 * 유니코드로 저장하는 경우
                    	 *******************************************/
                    	/*
                        String hex = Integer.toHexString(c);
                        buffer.append("\\u0000".substring(0, 6 - hex.length()));
                        buffer.append(hex);
                        */
                        buffer.append(c);
                    } else {
                        buffer.append(c);
                    }
            }
            if (c != ' ') {
                head = key;
            }
        }
    }

    /**
     * 프로퍼티 추가
     *
     * @param keyString 키.
     * @param value 값.
     */
    public void add(String keyString, String value) {
    	if(contains(keyString)){
    		remove(keyString);
    	}
    	put(keyString, value);
    	lineData.add("");
    	keyData.add(keyString);
    }

    /**
     * 주석 또는 줄바꿈 추가
     *
     * @param line 주석 또는 줄 바꿈
     */
    public void addLine(String line) {
        lineData.add(line);
        keyData.add("");
    }

    /**
     * 주석 추가
     *
     * @param comment 주석
     */
    public void addComment(String comment){
        lineData.add("# " + comment);
        keyData.add("");
    }

    /**
     * 주석 추가
     *
     * @param comment 주석
     * @param idx index
     */
    public void addComment(String comment, int idx){
    	lineData.add(idx, "# " + comment);
    	keyData.add(idx, "");
    }
    
    /**
     * 프로퍼티 제거
     *
     * @param idx index
     */
    public void remove(int idx){
    	lineData.remove(idx);
    	keyData.remove(idx);
    }

    public static void main(String[] args) throws Exception {
        CommentedProperties p = new CommentedProperties();
        
        p.load("utf-8");

        Vector lineData = p.lineData;
        Vector keyData = p.keyData;
        
        for (int i = 0; i < lineData.size(); i++) {
            String line = (String) lineData.get(i);
            String key = (String) keyData.get(i);
            if (key.length() > 0) {
                System.out.println(key + " = " + p.getProperty(key));
            } else {
                System.out.println(line);
            }
        }
        

        p.add("com.cabsoft.rx.pdfsign.reason", "전자서명 예제");
        p.store("utf-8");
        
        
    }
}
