package com.cabsoft;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import com.cabsoft.ess.form.FormPages;
import com.cabsoft.massive.PageView;

public class RXSession implements Serializable {
	private static final long serialVersionUID = -7804876773348597912L;
	
	private String jobID = "";
	private String rxprintData = "";
	private String issueID = "";
	private String rptFile = "";
	private String dataFile = "";
	private String xmlData = "";
	private String xmltype = "";
	
	private byte[] pdfData = null;
	private String userpwd = "";
	private boolean reqpasswd = false;
	
	private PageView[] pageView = null;
	private byte[] json = null;
	
	private String customer = "";
	private String product = "";
	private String signedData = "";
	private String signType = "0";
	private boolean usePdf = false;
	private String tiffServer = "";
	private String receiveUrl = "";
	private HashMap<String,Object> exptHMap = null; 
	
	// 툴바 유형 (2014년 5월 26일 장보은 추가)
	public final static String TOOLBAR_DEFAULT = "default";
	public final static String TOOLBAR_IOS = "ios";

	private String toolbarType = TOOLBAR_DEFAULT;
	
	/**
	 * 고밀도 바코드 존재 여부
	 */
	private boolean existRXCode = false;
	
	/**
	 * Plugin 모드 동작여부
	 */
	private boolean pluginMode = false;
	
	/**
	 * ESS FORM 페이지
	 */
	private FormPages formPages = null;
	
	/**
	 * 자필 서명 정보
	 */
	private String hwSignInfo = "";
	
	
	/**
	 * 보고서 실행에 따른 고유의 Job ID
	 * @return
	 */
	public String getJobID() {
		return jobID;
	}
	
	/**
	 * 보고서 실행에 따른 고유의 Job ID 설정
	 * @param jobID
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	
	/**
	 * 압축 및 직렬화 된 rxprint
	 * @return
	 */
	public String getRxprintData() {
		return rxprintData;
	}
	
	/**
	 * 압축 및 직렬화 된 rxprint 설정
	 * @param rxprintData
	 */
	public void setRxprintData(String rxprintData) {
		this.rxprintData = rxprintData;
	}
	
	/**
	 * 발급 번호
	 * @return
	 */
	public String getIssueID() {
		return issueID;
	}
	
	/**
	 * 발급 번호 설정
	 * @param issueID
	 */
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	
	/**
	 * 보고서 서식 파일
	 * @return
	 */
	public String getRptFile() {
		return rptFile;
	}
	
	/**
	 * 보고서 서식 파일 설정
	 * @param rptFile
	 */
	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	/**
	 * 보고서 서식 파일
	 * @return
	 */
	public String getDataFile() {
		return dataFile;
	}
	
	/**
	 * 보고서 서식 파일 설정
	 * @param rptFile
	 * @throws Exception 
	 */
	public void setRptFile(String rptFile) throws Exception {
		BufferedImage img = com.cabsoft.Barcode.createCode93("*302118 *",false,false);
		this.rptFile = rptFile;
	}
	
	
	
	/**
	 * XML 데이터
	 * @return
	 */
	public String getXmlData() {
		return xmlData;
	}
	
	/**
	 * XML 데이터 설정
	 * @param xmlData
	 */
	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}
	
	/**
	 * PDF 데이터
	 * @return
	 */
	public byte[] getPdfData() {
		return pdfData;
	}
	
	/**
	 * PDF 데이터 설정
	 * @param pdfData
	 */
	public void setPdfData(byte[] pdfData) {
		this.pdfData = pdfData;
	}

	/**
	 * 대용량 보고서 페이지 뷰
	 * @return
	 */
	public PageView[] getPageView() {
		return pageView;
	}

	/**
	 * 대용량 보고서 페이지 뷰 설정
	 * @param pageView
	 */
	public void setPageView(PageView[] pageView2) {
		if (pageView2 == null) {
			this.pageView = null;
		} else {
			this.pageView = (PageView[]) Arrays.copyOf(pageView2, pageView2.length);
		}
	}

	/**
	 * JSON 데이터
	 * @return
	 */
	public byte[] getJson() {
		return json;
	}

	/**
	 * JSON 데이터 설정
	 * @param json
	 */
	public void setJson(byte[] json2) {
		if (json2 == null) {
			this.json = null;
		} else {
			this.json = Arrays.copyOf(json2, json2.length);
		}
	}

	/**
	 * PDF 사용자 암호
	 * @return
	 */
	public String getUserpwd() {
		return userpwd;
	}

	/**
	 * PDF 사용자 암호 설정
	 * @param userpwd
	 */
	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	/**
	 * 데이터 종류
	 * @return
	 */
	public String getXmltype() {
		return xmltype;
	}

	/**
	 * 데이터 종류 설정
	 * @param xmltype
	 */
	public void setXmltype(String xmltype) {
		this.xmltype = xmltype;
	}

	/**
	 * PDF 사용자 암호 입력 요구
	 * @return
	 */
	public boolean isReqpasswd() {
		return reqpasswd;
	}

	/**
	 * PDF 사용자 암호 입력 요구 설정
	 * @param reqpasswd
	 */
	public void setReqpasswd(boolean reqpasswd) {
		this.reqpasswd = reqpasswd;
	}

	/**
	 * 고객 이름
	 * @return
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * 고객 이름 설정
	 * @param customer
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	/**
	 * 제품 이름
	 * @return
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * 제품 이름 설정
	 * @param product
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * 전자서명 데이터
	 * @return
	 */
	public String getSignedData() {
		return signedData;
	}

	/**
	 * 전자서명 데이터 설정
	 * @param signedData
	 */
	public void setSignedData(String signedData) {
		this.signedData = signedData;
	}

	/**
	 * 전자서명 방식
	 * 0 - 데이터 입력 값
	 * 1 - 문서 전체
	 * @return
	 */
	public String getSignType() {
		return signType;
	}

	/**
	 * 전자서명 방식 설정
	 * 0 - 데이터 입력 값
	 * 1 - 문서 전체
	 * @param signType
	 */
	public void setSignType(String signType) {
		this.signType = signType;
	}

	/**
	 * 이미지 변환시 PDF 문서 사용여부를 반환한다.
	 * @return the usePdf
	 */
	public boolean isUsePdf() {
		return usePdf;
	}

	/**
	 * 이미지 변환시 PDF 문서 사용여부를 설정한다.
	 * @param usePdf the usePdf to set
	 */
	public void setUsePdf(boolean usePdf) {
		this.usePdf = usePdf;
	}
	
	/**
	 * 용량이 큰 rxprintData, xmlData, pdfData, pageView, json를 클리어 한다.
	 */
	public void clearData(){
		rxprintData = "";
		xmlData = "";
		pdfData = null;
		pageView = null;
		json = null;
	}
	
	/**
	 * 이미지 변환 서버를 반환한다.
	 * @return the tiffServer
	 */
	public String getTiffServer() {
		return tiffServer;
	}

	/**
	 * 이미지 변환 서버를 설정한다.
	 * @param tiffServer the tiffServer to set
	 */
	public void setTiffServer(String tiffServer) {
		this.tiffServer = tiffServer;
	}

	/**
	 * 이미지 변환 결과를 전송받을 URL을 반환한다.
	 * @return the receiveUrl
	 */
	public String getReceiveUrl() {
		return receiveUrl;
	}

	/**
	 * 이미지 변환 결과를 전송받을 URL을 설정한다.
	 * @param receiveUrl the receiveUrl to set
	 */
	public void setReceiveUrl(String receiveUrl) {
		this.receiveUrl = receiveUrl;
	}

	/**
	 * 모든 세션 데이터를 클리어한다.
	 */
	public void reset(){
		jobID = "";
		rxprintData = "";
		issueID = "";
		rptFile = "";
		xmlData = "";
		xmltype = "";
		
		pdfData = null;
		userpwd = "";
		reqpasswd = false;
		
		pageView = null;
		json = null;
		
		customer = "";
		product = "";
		signedData = "";
		signType = "0";
		usePdf = false;
		tiffServer = "";
		receiveUrl = "";
		exptHMap = null;
		
		toolbarType = TOOLBAR_DEFAULT;
	}

	public HashMap<String,Object> getExptHMap() {
		return exptHMap;
	}

	public void setExptHMap(HashMap<String,Object> exptmap) {
		this.exptHMap = exptmap;
	}

	public boolean isExistRXCode() {
		return existRXCode;
	}

	public void setExistRXCode(boolean existRXCode) {
		this.existRXCode = existRXCode;
	}
	
	public String getToolbarType() {
		return toolbarType;
	}

	public void setToolbarType(String toolbarType) {
		this.toolbarType = toolbarType;
	}

	public boolean isPluginMode() {
		return pluginMode;
	}

	public void setPluginMode(boolean pluginMode) {
		this.pluginMode = pluginMode;
	}

	public FormPages getFormPages() {
		return formPages;
	}

	public void setFormPages(FormPages formPages) {
		this.formPages = formPages;
	}

	public String getHwSignInfo() {
		return hwSignInfo;
	}

	public void setHwSignInfo(String hwSignInfo) {
		this.hwSignInfo = hwSignInfo;
	}
	
	
}
