package com.cabsoft.pdf.form;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.text.Rectangle;
import com.cabsoft.text.pdf.AcroFields;
import com.cabsoft.text.pdf.PdfReader;
import com.cabsoft.text.pdf.AcroFields.Item;

public class PdfFormValidation {

	private static final Log log = LogFactory.getLog(PdfFormValidation.class);
	private PdfReader pdfReader = null;

	public PdfFormValidation(String pdf_file) throws Exception{
		try{
			pdfReader = new PdfReader(pdf_file);
		}catch(Exception e){
			throw new Exception(e);
		}
	}
	
	public PdfFormValidation(File pdf_file) throws Exception{
		try{
			pdfReader = new PdfReader(new FileInputStream(pdf_file));
		}catch(Exception e){
			throw new Exception(e);
		}
	}
	
	public PdfFormValidation(FileInputStream pdf_file) throws Exception{
		try{
			pdfReader = new PdfReader(pdf_file);
		}catch(Exception e){
			throw new Exception(e);
		}
	}
	
	public PdfFormValidation(byte[] pdf_data) throws Exception{
		try{
			pdfReader = new PdfReader(pdf_data);
		}catch(Exception e){
			throw new Exception(e);
		}
	}
	
	public HashMap<String, Item> getAcroFields(){
		AcroFields acroFields = pdfReader.getAcroFields();
		Rectangle pr = pdfReader.getPageSize(1);
        HashMap<String, Item> fields = (HashMap<String, Item>) acroFields.getFields();
        Set<Entry<String, Item>> entrySet = fields.entrySet();
        for (Entry<String, Item> entry : entrySet) {
            String key = entry.getKey();
            String value = acroFields.getField(key);
            Rectangle rect = acroFields.getFieldPositions(key).get(0).position;
            float sz = 2f*96f/72f;
            System.out.println(key + " = (" + (int)(rect.getLeft()*sz) + "," + (int)((pr.getHeight()-rect.getTop())*sz) + ") - (" + (int)(rect.getRight()*sz) + "," + (int)((pr.getHeight()-rect.getBottom())*sz)+")");
            log.debug(key + " = " + value);
        }
        return fields;
	}
	
	public HashMap<String, String> getFields(){
		AcroFields acroFields = pdfReader.getAcroFields();
		HashMap<String, String> fields = new HashMap<String, String>();
        HashMap<String, Item> acro_fields = (HashMap<String, Item>) acroFields.getFields();
        Set<Entry<String, Item>> entrySet = acro_fields.entrySet();
        for (Entry<String, Item> entry : entrySet) {
            String key = entry.getKey();
            String value = acroFields.getField(key);
            log.debug(key + " = " + value);
            fields.put(key, value);
        }
        return fields;
	}
	
	public static void main(String[] args) throws Exception {
		PdfFormValidation v = new PdfFormValidation(new File("d:/기업은행.pdf"));
		
		HashMap<String, Item> MAP = v.getAcroFields();
		
	}
}
