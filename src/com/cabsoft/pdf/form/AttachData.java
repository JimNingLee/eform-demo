package com.cabsoft.pdf.form;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import com.cabsoft.pdfutils.Attachments;
import com.cabsoft.utils.Files;
import com.cabsoft.utils.SystemUtils;

public class AttachData {
	private final String fsp = System.getProperty("file.separator");
	private String tmpDir = System.getProperty("java.io.tmpdir");
	
	public AttachData(){
		tmpDir = tmpDir.endsWith(fsp) ? tmpDir : tmpDir + fsp;
	}
	
	public String writeData(byte[] b, String ext) throws Exception{
		String exptFileName = SystemUtils.getDateTime("yyyyMMdd_hhmmss");
		String fs = tmpDir + exptFileName + "." + ext;
		Files.writeFile(b, fs);
		return fs;
	}
	
	public void addAttach(ByteArrayOutputStream src, OutputStream dest, String[] attfs, String[] desc) throws Exception {
		try{
	        Attachments attachment = new Attachments();
	        attachment.addAttachments(src, dest , attfs, desc);
	        
	        eraseAttachData(attfs);
		}catch(Exception e){
			throw new Exception(e);
		}
	}
	
	public void eraseAttachData(String[] fs){
        for(int i=0; i<fs.length; i++){
        	File ff = new File(fs[i]);
        	if(ff.exists()){
        		ff.delete();
        	}
        }
	}

}
