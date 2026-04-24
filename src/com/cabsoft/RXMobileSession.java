package com.cabsoft;

import java.io.Serializable;

public class RXMobileSession implements Serializable {
	private static final long serialVersionUID = -7804876773348597912L;
	
	private String jobID = "";
	private String rxprintData = "";
	private String issueID = "";
	private String rptFile = "";
	private String xmlData = "";
	private EnumXmlType xmltype = EnumXmlType.STRING;
	
	private byte[] pdfData = null;
	private String userpwd = "";
	private boolean reqpasswd = false;
	
	private boolean existRXCode = false;
	
	private byte[] json = null;
	
	public String getJobID() {
		return jobID;
	}
	
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	
	public String getRxprintData() {
		return rxprintData;
	}
	
	public void setRxprintData(String rxprintData) {
		this.rxprintData = rxprintData;
	}
	
	public String getIssueID() {
		return issueID;
	}
	
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	
	public String getRptFile() {
		return rptFile;
	}
	
	public void setRptFile(String rptFile) {
		this.rptFile = rptFile;
	}
	
	public String getXmlData() {
		return xmlData;
	}
	
	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}
	
	public byte[] getPdfData() {
		return pdfData;
	}
	
	public void setPdfData(byte[] pdfData) {
		this.pdfData = pdfData;
	}

	public byte[] getJson() {
		return json;
	}

	public void setJson(byte[] json) {
		this.json = json;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public EnumXmlType getXmltype() {
		return xmltype;
	}

	public void setXmltype(EnumXmlType xmltype) {
		this.xmltype = xmltype;
	}

	public boolean isReqpasswd() {
		return reqpasswd;
	}

	public void setReqpasswd(boolean reqpasswd) {
		this.reqpasswd = reqpasswd;
	}

	public boolean isExistRXCode() {
		return existRXCode;
	}

	public void setExistRXCode(boolean existRXCode) {
		this.existRXCode = existRXCode;
	}
}
