package com.cabsoft.site.kfda;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RXCodeInfo  implements IRXCodeInfo {
    
    private final static Log log = LogFactory.getLog(RXCodeInfo.class);
    
    private final float mm2pixel = 2.83464f;
    
    /**
     * 발급 번호 정보
     */
    private boolean insertID = false;
    private String issuerID = "";
    private String issuerIDString = "";
    private String font_file = "";
    private int font_size = 10;
    private float id_x = 0f;
    private float id_y = 0f;
    
    /**
     * 고밀도 바코드 정보
     */
    private boolean insertBarcode = false;
    private float barcode_size_percent = 23.98f;
    private String data = "";
    private int pages_num = 0;
    private float barcode_x = 0f;
    private float barcode_y = 0f;
    
    /**
     * QR CODE 정보
     */
    private boolean insertQRCode = false;
    private boolean lastPageOnly = true;
    private String probeUrl = "";
    private float qr_size_percent = 27.5f;
    private float qr_x = 0f;
    private float qr_y = 0f;

    public RXCodeInfo(String layout) throws Exception{
        File file = new File(layout);
        DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
        Document doc = docBuild.parse(file);
        doc.getDocumentElement().normalize();
       
        /**
         * 발급 번호 정보
         */
        log.debug("발급 번호 정보");
        NodeList nodeList = doc.getElementsByTagName("issuerid");
        Node parentNode = nodeList.item(0);

        if (parentNode.getNodeType() == Node.ELEMENT_NODE) {

            Element issueridElement = (Element) parentNode;
            	
            // 발급 번호 표시 여부
            NodeList nlist = issueridElement.getElementsByTagName("insert");
            Element element = (Element) nlist.item(0);
            Node node = element.getFirstChild();
            String v = node.getNodeValue().trim();
            insertID = v.equalsIgnoreCase("true");
            log.debug("insert: " + v);

            if(insertID){
                // 폰트 크기
                nlist = issueridElement.getElementsByTagName("fontsize");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                font_size = Integer.parseInt(v);
                log.debug("fontsize: " + v);

                // x 좌표
                nlist = issueridElement.getElementsByTagName("x");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                id_x = Float.parseFloat(v);
                log.debug("x: " + node.getNodeValue());

                // y 좌표
                nlist = issueridElement.getElementsByTagName("y");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                id_y = Float.parseFloat(v);
                log.debug("y: " + node.getNodeValue());
            }
        }
        
        /**
         * 고밀도 바코드 정보
         */
        log.debug("고밀도 바코드 정보");
        nodeList = doc.getElementsByTagName("hdbarcode");
        parentNode = nodeList.item(0);

        if (parentNode.getNodeType() == Node.ELEMENT_NODE) {

            Element issueridElement = (Element) parentNode;

            // 고밀도 바코드 입력 여부
            NodeList nlist = issueridElement.getElementsByTagName("insert");
            Element element = (Element) nlist.item(0);
            Node node = element.getFirstChild();
            String v = node.getNodeValue().trim();
            insertBarcode = v.equalsIgnoreCase("true");
            log.debug("insert: " + node.getNodeValue());

            if(insertBarcode){
                // 바코드 크기 (%) 기본 23.98
                nlist = issueridElement.getElementsByTagName("size");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                barcode_size_percent = Float.parseFloat(v);
                log.debug("scale factor: " + node.getNodeValue());

                // x 좌표
                nlist = issueridElement.getElementsByTagName("x");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                barcode_x = Float.parseFloat(v);
                log.debug("x: " + node.getNodeValue());

                // y 좌표
                nlist = issueridElement.getElementsByTagName("y");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                barcode_y = Float.parseFloat(v);
                log.debug("y: " + node.getNodeValue());
            }
        }
        
        /**
         * QR Code 입력 정보
         */
        log.debug("QR Code 입력 정보");
        nodeList = doc.getElementsByTagName("qrcode");
        parentNode = nodeList.item(0);

        if (parentNode.getNodeType() == Node.ELEMENT_NODE) {

            Element issueridElement = (Element) parentNode;

            // QR Code 표시 여부
            NodeList nlist = issueridElement.getElementsByTagName("insert");
            Element element = (Element) nlist.item(0);
            Node node = element.getFirstChild();
            String v = node.getNodeValue().trim();
            insertQRCode = v.equalsIgnoreCase("true");
            log.debug("insert: " + node.getNodeValue());

            if(insertQRCode){
                // 원본 대조 URL
                nlist = issueridElement.getElementsByTagName("probeurl");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                probeUrl = node.getNodeValue().trim();
                log.debug("probeurl: " + node.getNodeValue());

                // 맨 마지막 메이지에만 삽입 여부
                nlist = issueridElement.getElementsByTagName("lastpageonly");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                lastPageOnly = v.equalsIgnoreCase("true");
                log.debug("lastpageonly: " + node.getNodeValue());

                // QR Code 크기 (%)
                nlist = issueridElement.getElementsByTagName("size");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                qr_size_percent = Float.parseFloat(v);
                log.debug("size: " + node.getNodeValue());

                // x 좌표
                nlist = issueridElement.getElementsByTagName("x");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                qr_x = Float.parseFloat(v);
                log.debug("x: " + node.getNodeValue());

                // y 좌표
                nlist = issueridElement.getElementsByTagName("y");
                element = (Element) nlist.item(0);
                node = element.getFirstChild();
                v = node.getNodeValue().trim();
                qr_y = Float.parseFloat(v);
                log.debug("y: " + node.getNodeValue());
            }
        }
    }
    
    /**
     * 발급 번호 삽입 여부
     * @return 
     */
    public boolean isInsertID() {
        return insertID;
    }

    /**
     * 발급 번호 삽입 여부
     * @param insertID 
     */
    public void setInsertID(boolean insertID) {
        this.insertID = insertID;
    }

    /*** 
     * 발급 번호
     * @return 
     */
    public String getIssuerID() {
        return issuerID;
    }

    /**
     * 발급 번호
     * @param issuerID 
     */
    public void setIssuerID(String issuerID) {
        this.issuerID = issuerID;
    }

    /**
     * 발급 번호 문자
     * @return 
     */
    public String getIssuerIDString() {
        return issuerIDString;
    }

    /**
     * 발급 번호 문자
     * @param issuerIDString 
     */
    public void setIssuerIDString(String issuerIDString) {
        this.issuerIDString = issuerIDString;
    }

    /**
     * 발급 번호 폰트 파일
     * @return 
     */
    public String getFontFile() {
        return font_file;
    }

    /**
     * 발급 번호 폰트 파일
     * @param font_file 
     */
    public void setFontFile(String font_file) {
        this.font_file = font_file;
    }
    
    /**
     * 발급 번호 글자 크기(pt)
     * @return 
     */
    public int getFont_size() {
        return font_size;
    }

    /**
     * 발급 번호 글자 크기(pt)
     * @param font_size 
     */
    public void setFont_size(int font_size) {
        this.font_size = font_size;
    }

    /**
     * 발급 번호 X 좌표 (pixel)
     * @return 
     */
    public float getId_x() {
        return id_x*mm2pixel;
    }

    /**
     * 발급 번호 X 좌표 (mm)
     * @param id_x 
     */
    public void setId_x(float id_x) {
        this.id_x = id_x;
    }

    /**
     * 발급 번호 Y 좌표 (pixel)
     * @return 
     */
    public float getId_y() {
        return id_y*mm2pixel;
    }

    /**
     * 발급 번호 Y 좌표 (mm)
     * @param id_y 
     */
    public void setId_y(float id_y) {
        this.id_y = id_y;
    }

    /**
     * 고밀도 바코드 삽입 여부
     * @return 
     */
    public boolean isInsertBarcode() {
        return insertBarcode;
    }

    /**
     * 고밀도 바코드 삽입 여부
     * @param insertBarcode 
     */
    public void setInsertBarcode(boolean insertBarcode) {
        this.insertBarcode = insertBarcode;
    }

    /**
     * 고밀도 바코드 크기(%)
     * @return 
     */
    public float getBarcode_size_percent() {
        return barcode_size_percent;
    }

    /**
     * 고밀도 바코드 크기(%)
     * @param barcode_size_percent 
     */
    public void setBarcode_size_percent(float barcode_size_percent) {
        this.barcode_size_percent = barcode_size_percent;
    }

    /**
     * 고밀도 바코드 데이터(HTML)
     * @return 
     */
    public String getBarcodeData() {
        return data;
    }

    /**
     * 고밀도 바코드 데이터(HTML)
     * @param data 
     */
    public void setBarcodeData(String data) {
        this.data = data;
    }

    /**
     * PDF 문서 패이지 수
     * @return 
     */
    public int getPages_Num() {
        return pages_num;
    }

    /**
     * PDF 문서 페이지 수
     * @param pages_num 
     */
    public void setPages_Num(int pages_num) {
        this.pages_num = pages_num;
    }

    /**
     * 고밀도 바코드 X 좌표 (pixel)
     * @return 
     */
    public float getBarcode_x() {
        return barcode_x*mm2pixel;
    }

    /**
     * 고밀도 바코드 X 좌표 (mm)
     * @param barcode_x 
     */
    public void setBarcode_x(float barcode_x) {
        this.barcode_x = barcode_x;
    }

    /**
     * 고밀도 바코드 Y 좌표 (pixel)
     * @return 
     */
    public float getBarcode_y() {
        return barcode_y*mm2pixel;
    }

    /**
     * 고밀도 바코드 Y 좌표 (mm)
     * @param barcode_y 
     */
    public void setBarcode_y(float barcode_y) {
        this.barcode_y = barcode_y;
    }

    /**
     * QRCode 삽입 여부
     * @return 
     */
    public boolean isInsertQRCode() {
        return insertQRCode;
    }

    /**
     * QRCode 삽입 여부
     * @param insertQRCode 
     */
    public void setInsertQRCode(boolean insertQRCode) {
        this.insertQRCode = insertQRCode;
    }

    /**
     * QR Code 마지막 페이지에만 삽입
     * @return 
     */
    public boolean isLastPageOnly() {
        return lastPageOnly;
    }

    /**
     *  QR Code 마지막 페이지에만 삽입
     * @param lastPageOnly 
     */
    public void setLastPageOnly(boolean lastPageOnly) {
        this.lastPageOnly = lastPageOnly;
    }

    /**
     * QRCode를 이용한 원본 대조 URL
     * @return 
     */
    public String getProbeUrl() {
        return probeUrl;
    }

    /**
     * QRCode를 이용한 원본 대조 URL
     * @param probeUrl 
     */
    public void setProbeUrl(String probeUrl) {
        this.probeUrl = probeUrl;
    }

    /**
     * QRCode 크기(%)
     * @return 
     */
    public float getQr_size_percent() {
        return qr_size_percent;
    }

    /**
     * QRCode 크기(%)
     * @param qr_size_percent 
     */
    public void setQr_size_percent(float qr_size_percent) {
        this.qr_size_percent = qr_size_percent;
    }

    /**
     * QRCode X 좌표 (pixel)
     * @return 
     */
    public float getQr_x() {
        return qr_x*mm2pixel;
    }

    /**
     * QRCode X 좌표 (mm)
     * @param qr_x 
     */
    public void setQr_x(float qr_x) {
        this.qr_x = qr_x;
    }

    /**
     * QRCode Y 좌표 (pixel)
     * @return 
     */
    public float getQr_y() {
        return qr_y*mm2pixel;
    }

    /**
     * QRCode Y 좌표 (mm)
     * @param qr_y 
     */
    public void setQr_y(float qr_y) {
        this.qr_y = qr_y;
    }
    
    /**
     * QR Code로 만들어질 데이터
     * @return 
     */
    public String getQRData(){
        return probeUrl + "?s=" + issuerID;
    }
}
