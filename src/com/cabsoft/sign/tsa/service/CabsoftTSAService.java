package com.cabsoft.sign.tsa.service;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.GlobalParams;
import com.cabsoft.SignCertFactory;
import com.cabsoft.pdfutils.CertInfo;
import com.cabsoft.pdfutils.ImageToByte;
import com.cabsoft.pdfutils.Sign.SignCertUtil;
import com.cabsoft.pdfutils.Sign.SignaturePos;
import com.cabsoft.pdfutils.Sign.Interface.ICertInfo;
import com.cabsoft.pdfutils.Sign.Interface.ITimestampOCSP;
import com.cabsoft.pdfutils.Sign.security.TSAClient;
import com.cabsoft.pdfutils.Sign.security.TSAClientSpongyCastle;
import com.cabsoft.pdfutils.Sign.TimestampOCSP;
import com.cabsoft.smartcert.mobile.PdfPKCS7Info;
import com.cabsoft.utils.StringUtils;

public class CabsoftTSAService {
	private static final Log log = LogFactory.getLog(CabsoftTSAService.class);

	public static PdfPKCS7Info signPdfWithTSAOCSP(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd) throws Exception {
		return signPdfWithTSAOCSP(out,  o, webinf, pwd, false, null);
	}
	
	public static PdfPKCS7Info signPdfWithTSAOCSP(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd,
			boolean removeBookMarkAll, String startWith) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		String timestamppath = globalParams.getTimestampPath();

		String TSA_URL = globalParams.getProperty("com.cabsoft.rx.cabsoft.TSA_URL");
		String TSA_ACCNT = globalParams.getProperty("com.cabsoft.rx.cabsoft.TSA_ACCOUNT");
		String TSA_PASSW = globalParams.getProperty("com.cabsoft.rx.cabsoft.TSA_PASSWORD");

		TSAClient tsc = new TSAClientSpongyCastle(TSA_URL, TSA_ACCNT, TSA_PASSW, 4096, globalParams.getProperty("com.cabsoft.cabsoft.alg"));

		ICertInfo certInfo = new CertInfo();

		/**
		 * 인증서에 대한 싱글톤
		 */
		SignCertUtil signCert = null;
		if (SignCertFactory.getInstance().getPdfSignCert() == null) {
			if (globalParams.isPFX() == true) {
				signCert = new SignCertUtil(globalParams.getSignCertFile(), globalParams.getSignCertPwd(), globalParams.isWithBC());
			} else {
				signCert = new SignCertUtil(globalParams.getSignCertFile(), globalParams.getSignCertKeyFile(), globalParams.getSignCertPwd());
			}
			SignCertFactory.getInstance().setPdfSignCert(signCert);
		} else {
			signCert = SignCertFactory.getInstance().getPdfSignCert();
		}

		certInfo.CertInfo(signCert);

		/**
		 * 타임스탬프 기관
		 */
		certInfo.setTimeStampOrgan("Cabsoft");

		/**
		 * Certification Level. NOT_CERTIFIED - creates an ordinary signature
		 * aka an approval or a recipient signature. A document can be signed
		 * for approval by one or more recipients. CERTIFIED_NO_CHANGES_ALLOWED
		 * - creates a certification signature aka an author signature. After
		 * the signature is applied, no changes to the document will be allowed.
		 * CERTIFIED_FORM_FILLING - creates a certification signature for the
		 * author of the document. Other people can still fill out form fields
		 * or add approval signatures without invalidating the signature.
		 * CERTIFIED_FORM_FILLING_AND_ANNOTATIONS - creates a certification
		 * signature. Other people can still fill out form fields- or add
		 * approval signatures as well as annotations without invalidating the
		 * signature.
		 */
		certInfo.setCertificationLevel(globalParams.getCertificationLevel());
		certInfo.setPdfPermision(globalParams.getPdfPermision());
		certInfo.setPdfEncryption(globalParams.getPdfEncryptAlg());

		/**
		 * 서명 위치
		 */
		certInfo.setLocation("CABSOFTWARE.COM");

		/**
		 * 서명 원인
		 */
		certInfo.setReason("전자서명 및 타임스템프 테스트");

		/*
		 * Signature Name - 서명자 이름
		 */
		certInfo.setSignatureName("(주) 캡소프트");

		/*
		 * 서명자의 연락처 정보
		 */
		certInfo.setSignerContact("support@cabsoftware.com");

		/**
		 * Message Digest Algorithm
		 */
		certInfo.setDigestAlgorithm("SHA256");

		if (timestamppath == null || timestamppath.equalsIgnoreCase("")) {
			certInfo.setTimeStampPath(globalParams.getTimestampPath());
		} else {
			certInfo.setTimeStampPath(timestamppath);
		}

		/**
		 * 인영 마크에 시각 정보 표시 여부
		 */
		certInfo.setDisplayTimeStamp(true);

		/**
		 * 인영 마크의 크기(%)
		 */
		certInfo.setScalefactor(10.0f);

		/**
		 * 폰트 설정
		 */
		Font font = new Font("Times New Roman", Font.PLAIN, 60);
		certInfo.setFont(font);
		certInfo.setColor(Color.BLACK);

		/**
		 * 시작 Y 좌표 및 줄간 간격 - 원인 경우 아래의 설정을 따름
		 */
		certInfo.setSealImage("sign_c" + String.valueOf(globalParams.getDefaultTimestamp()) + ".png");
		certInfo.setMultiline(true);
		certInfo.setCircleType(true);
		certInfo.setTop(300);
		certInfo.setVGap(60);

		certInfo.setPos(SignaturePos.LeftTop);

		/**
		 * 검증 결과에 따른 인영 마크 설정
		 */
		certInfo.setShadowMark2(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c2.png"));
		certInfo.setShadowMark3(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c3.png"));
		certInfo.setShadowMark4(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c4.png"));

		/**
		 * PDF 검증기에서 사용될 화면 및 인쇄용 워터마크 설정
		 */
		certInfo.setPrintWatermark(ImageToByte.getImage(certInfo.getTimeStampPath() + "watermark_small.png"));
		certInfo.setScreenWatermark(ImageToByte.getImage(certInfo.getTimeStampPath() + "watermark_small.png"));

		certInfo.setDetached(true);

		if (pwd != null && !pwd.equalsIgnoreCase("")) {
			certInfo.setUserPassword(pwd);
		}

		/*
		 * TSA 정보를 ICertInfo에 설정
		 */
		certInfo.setTsc(tsc);

		/*
		 * PDFA 변환 설정
		 */
		/*
		 * certInfo.setPDFA_Type(PdfWriter.PDFA1A); ConvertPDFA c = new
		 * ConvertPDFA(); c.setIccProfile(globalParams.getIccfile());
		 * certInfo.setConvertPDFA(c);
		 */
		try{
			ITimestampOCSP signservlet = new TimestampOCSP();
			signservlet.Sign(o, out, certInfo, removeBookMarkAll, startWith);
		}catch (Exception e) {
			log.error(e);
			String errMsg = "[Cabsoft TSA]\n";
			HashMap<String,String> rMap = certInfo.getReturnValues();
			
	        Iterator<String> it = rMap.keySet().iterator();
	        while(it.hasNext()){
	            String key = it.next();
	            String value = rMap.get(key);
	            if (!StringUtils.isNull(value) && key.indexOf("error")>-1){
	            	errMsg +=  (rMap.get(key) + "\n");
	            }
	            log.debug(key + " = " + value);
	        } 
	        throw new Exception( errMsg );
		} 

		/*
		 * 모바일에서 스마트 코드를 이용하여 원본대조를 하는 경우 PDF 문서를 검증하지 않고 발급 정보를 기록하기 위해 추가됨
		 */
		PdfPKCS7Info info = new PdfPKCS7Info(certInfo, tsc.getTimeStampToken());
		log.debug("발급 일시: " + info.getSignDate());
		log.debug(info.getInfoString());

		return info;

	}
}
