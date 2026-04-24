package com.cabsoft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cabsoft.rx.engine.ReportExpressPrint;

public class RXMSession implements Serializable {
	private static final long serialVersionUID = -7804876773348597912L;

	private String jobID = "";
	private List<String> rptFile = new ArrayList<String>();
	private List<String> issueID = new ArrayList<String>();
	private List<String> issueIDString = new ArrayList<String>();
	private List<String> xmlData = new ArrayList<String>();
	private List<ReportExpressPrint> rxprintData = new ArrayList<ReportExpressPrint>();

	private byte[] pdfData = null;
	private String userpwd = "";
	private boolean reqpasswd = false;

	private boolean existRXCode = false;

	/**
	 * Plugin 모드 동작여부
	 */
	private boolean pluginMode = false;

	public RXMSession() {
		jobID = "";
		rptFile = new ArrayList<String>();
		issueID = new ArrayList<String>();
		issueIDString = new ArrayList<String>();
		xmlData = new ArrayList<String>();
		rxprintData = new ArrayList<ReportExpressPrint>();
		rxprintData.clear();
		pdfData = null;
		userpwd = "";
		reqpasswd = false;
		pluginMode = false;
	}

	public String getJobID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public List<String> getRptFile() {
		return rptFile;
	}

	public void setRptFile(List<String> rptFile) {
		this.rptFile = rptFile;
	}

	public List<String> getIssueID() {
		return issueID;
	}

	public void setIssueID(List<String> issueID) {
		this.issueID = issueID;
	}

	public List<String> getIssueIDString() {
		return issueIDString;
	}

	public void setIssueIDString(List<String> issueIDString) {
		this.issueIDString = issueIDString;
	}

	public List<String> getXmlData() {
		return xmlData;
	}

	public void setXmlData(List<String> xmlData) {
		this.xmlData = xmlData;
	}

	public List<ReportExpressPrint> getRxprintData() {
		return rxprintData;
	}

	public void setRxprintData(List<ReportExpressPrint> rxprintData) {
		this.rxprintData = rxprintData;
	}

	public byte[] getPdfData() {
		return pdfData;
	}

	public void setPdfData(byte[] pdfData) {
		this.pdfData = pdfData;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
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

	public boolean isPluginMode() {
		return pluginMode;
	}

	public void setPluginMode(boolean pluginMode) {
		this.pluginMode = pluginMode;
	}
}
