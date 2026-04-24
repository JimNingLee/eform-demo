package com.cabsoft.pdf.form;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cabsoft.GlobalParams;
import com.cabsoft.text.pdf.BaseFont;

public class BaseFontUtil {
	private static Map<String, BaseFont> cache = new ConcurrentHashMap<String, BaseFont>();
	
	public static BaseFont getBaseFont(File fontfile){
		return getBaseFont(fontfile.getAbsolutePath());
	}
	
	public static BaseFont getBaseFont(String filename){
		BaseFont font = null;
		
        if (cache != null) {
            if ((font = cache.get(filename)) != null) {
                return font;
            }
        }
        
        String fName = "";
        try {
        	fName = GlobalParams.getInstance().getFontPath() + filename;
            File f = new File(fName);
            if (f.exists() == true) {
                font = BaseFont.createFont(fName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true);
                cache.put(filename, font);
            } else {
                font = BaseFont.createFont(fName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true);
            }
        } catch (Exception ex) {
            //font = BaseFont.createFont(fName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true);
        }
        return font;
	}
}
