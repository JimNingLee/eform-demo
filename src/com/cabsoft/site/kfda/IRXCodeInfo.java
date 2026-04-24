package com.cabsoft.site.kfda;

public interface IRXCodeInfo {
    /**
     * 발급 번호 삽입 여부
     * @return 
     */
    public boolean isInsertID();

    /**
     * 발급 번호 삽입 여부
     * @param insertID 
     */
    public void setInsertID(boolean insertID);

    /*** 
     * 발급 번호
     * @return 
     */
    public String getIssuerID();

    /**
     * 발급 번호
     * @param issuerID 
     */
    public void setIssuerID(String issuerID);

    /**
     * 발급 번호 문자
     * @return 
     */
    public String getIssuerIDString();

    /**
     * 발급 번호 문자
     * @param issuerIDString 
     */
    public void setIssuerIDString(String issuerIDString);

    /**
     * 발급 번호 폰트 파일
     * @return 
     */
    public String getFontFile();

    /**
     * 발급 번호 폰트 파일
     * @param font_file 
     */
    public void setFontFile(String font_file);
    
    /**
     * 발급 번호 글자 크기(pt)
     * @return 
     */
    public int getFont_size();

    /**
     * 발급 번호 글자 크기(pt)
     * @param font_size 
     */
    public void setFont_size(int font_size);

    /**
     * 발급 번호 X 좌표 (pixel)
     * @return 
     */
    public float getId_x();

    /**
     * 발급 번호 X 좌표 (mm)
     * @param id_x 
     */
    public void setId_x(float id_x);

    /**
     * 발급 번호 Y 좌표 (pixel)
     * @return 
     */
    public float getId_y();

    /**
     * 발급 번호 Y 좌표 (mm)
     * @param id_y 
     */
    public void setId_y(float id_y);

    /**
     * 고밀도 바코드 삽입 여부
     * @return 
     */
    public boolean isInsertBarcode();

    /**
     * 고밀도 바코드 삽입 여부
     * @param insertBarcode 
     */
    public void setInsertBarcode(boolean insertBarcode);

    /**
     * 고밀도 바코드 크기(%)
     * @return 
     */
    public float getBarcode_size_percent();

    /**
     * 고밀도 바코드 크기(%)
     * @param barcode_size_percent 
     */
    public void setBarcode_size_percent(float barcode_size_percent);

    /**
     * 고밀도 바코드 데이터(HTML)
     * @return 
     */
    public String getBarcodeData();

    /**
     * 고밀도 바코드 데이터(HTML)
     * @param data 
     */
    public void setBarcodeData(String data);

    /**
     * PDF 문서 패이지 수
     * @return 
     */
    public int getPages_Num();

    /**
     * PDF 문서 페이지 수
     * @param pages_num 
     */
    public void setPages_Num(int pages_num);

    /**
     * 고밀도 바코드 X 좌표 (pixel)
     * @return 
     */
    public float getBarcode_x();

    /**
     * 고밀도 바코드 X 좌표 (mm)
     * @param barcode_x 
     */
    public void setBarcode_x(float barcode_x);

    /**
     * 고밀도 바코드 Y 좌표 (pixel)
     * @return 
     */
    public float getBarcode_y();

    /**
     * 고밀도 바코드 Y 좌표 (mm)
     * @param barcode_y 
     */
    public void setBarcode_y(float barcode_y);

    /**
     * QRCode 삽입 여부
     * @return 
     */
    public boolean isInsertQRCode();

    /**
     * QRCode 삽입 여부
     * @param insertQRCode 
     */
    public void setInsertQRCode(boolean insertQRCode);

    /**
     * QR Code 마지막 페이지에만 삽입
     * @return 
     */
    public boolean isLastPageOnly();

    /**
     *  QR Code 마지막 페이지에만 삽입
     * @param lastPageOnly 
     */
    public void setLastPageOnly(boolean lastPageOnly);
    
    /**
     * QRCode를 이용한 원본 대조 URL
     * @return 
     */
    public String getProbeUrl();

    /**
     * QRCode를 이용한 원본 대조 URL
     * @param probeUrl 
     */
    public void setProbeUrl(String probeUrl);

    /**
     * QRCode 크기(%)
     * @return 
     */
    public float getQr_size_percent();

    /**
     * QRCode 크기(%)
     * @param qr_size_percent 
     */
    public void setQr_size_percent(float qr_size_percent);

    /**
     * QRCode X 좌표 (pixel)
     * @return 
     */
    public float getQr_x();

    /**
     * QRCode X 좌표 (mm)
     * @param qr_x 
     */
    public void setQr_x(float qr_x);

    /**
     * QRCode Y 좌표 (pixel)
     * @return 
     */
    public float getQr_y();

    /**
     * QRCode Y 좌표 (mm)
     * @param qr_y 
     */
    public void setQr_y(float qr_y);
    
    /**
     * QR Code로 만들어질 데이터
     * @return 
     */
    public String getQRData();
}
