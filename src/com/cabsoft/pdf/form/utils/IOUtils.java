package com.cabsoft.pdf.form.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

@SuppressWarnings("javadoc")
public class IOUtils {

    private IOUtils() {
    }

    /**
     *
     * @param fileName
     * @return
     * @throws Exception
     */
	public static byte[] readFile(String fileName) throws Exception {
        byte[] data = null;
        int size = 0;
        File file = null;
        FileInputStream fis = null;
        try {
            file = new File(fileName);
            fis = new FileInputStream(file);
            size = fis.available();
            data = new byte[size];
            fis.read(data);
            fis.close();
        } catch (Exception e) {
            throw new IOUtilsException("Read File byte[] Error", e);
        }
        return data;
    }
    
    public static OutputStream readFile2OutputStream(String fileName) throws Exception {
        byte[] b = readFile(fileName);
        ByteArrayOutputStream baos  = new ByteArrayOutputStream();
        baos.write(b);
        baos.flush();
        baos.close();
        return baos;
        
    }

    /**
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String sreadFile(String fileName) throws Exception {
        String ret = "";
        ret = new String(readFile(fileName));
        return ret;
    }

    // 바이너리 데이터(data)를 fileName으로 저장
    /**
     *
     * @param data
     * @param fileName
     * @throws Exception
     */
    public static void writeFile(byte[] data, String fileName) throws Exception {
        try{
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(data);
            fos.close();
        }catch(Exception e){
            throw new IOUtilsException("File Write byte[] Error", e);
        }
    }

    // 텍스트 데이터(data)를 fileName으로 저장
    /**
     *
     * @param data
     * @param fileName
     * @throws Exception
     */
    public static void swriteFile(String data, String fileName) throws Exception {
        writeFile(data.getBytes(), fileName);
    }

    //파일 삭제
    /**
     *
     * @param fs
     */
    public static void deleteFile(String fs) throws IOUtilsException{
        File f = new File(fs);
        try {
            if (!f.delete()) {
                throw new IOUtilsException("File " + fs + "Delete Failure");
            }
        } catch (Exception e) {
            throw new IOUtilsException("File " + fs + "Delete Failure", e);
        }
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static String[] FileList(String filePath) throws IOUtilsException {
        String files[] = null;
        try{
            File path = new File(filePath);
            files = path.list();
        }catch(Exception e){
            throw new IOUtilsException("Get File List from " + filePath, e);
        }
        return files;
    }

    /**
     *
     * @param args
     */
//    public static void main(String[] args) {
//        String list[] = FileList("c:/JSP/firefox/data1/");
//        if(list!=null){
//            System.out.println(list.length);
//            for(int i=0; i<list.length; i++){
//                System.out.println(list[i]);
//            }
//        }
//    }
}

