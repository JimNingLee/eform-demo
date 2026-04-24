package com.cabsoft.serialize;

import com.cabsoft.rx.engine.ReportExpressPrint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RxObjectTransfer extends HashMap<String, Object> implements Serializable
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7033332085226581019L;

	public RxObjectTransfer() {
		put("code", "success");
	}
	  
	public byte[] getImage() {
		return (byte[])get("image");
	}
	  
	public byte[] getPdf() {
		return (byte[])get("pdf");
	}
	  
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getSignList() {
		return (List<Map<String, String>>)get("sign");
	}
	
	public String getHtml() {
		return (String)get("html");
	}
	  
	public ReportExpressPrint getRxPrint() {
		return (ReportExpressPrint)get("rxprint");
	}
	  
	public String getCode() {
		return (String)get("code");
	}
	  
	public String getMessage() {
		return (String)get("message");
	}
	  
	public String getDetailMessage() {
		return (String)get("detailMessage");
	}
}
