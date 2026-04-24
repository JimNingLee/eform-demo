package com.cabsoft.sign.service;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.GlobalParams;
import com.cabsoft.SignCertFactory;
import com.cabsoft.pdfutils.CertInfo;
import com.cabsoft.pdfutils.ImageToByte;
import com.cabsoft.pdfutils.Sign.SignCertUtil;
import com.cabsoft.pdfutils.Sign.Interface.ICertInfo;
import com.cabsoft.pdfutils.Sign.Interface.ISignPdf;
import com.cabsoft.pdfutils.Sign.SignPdf;
import com.cabsoft.smartcert.mobile.PdfPKCS7Info;
import com.cabsoft.utils.StringUtils;

public class PdfSignService {
	private static final Log log = LogFactory.getLog(PdfSignService.class);

	public static PdfPKCS7Info signPdfSelf(OutputStream out, ByteArrayOutputStream bo, String webinf, String pwd) throws Exception {
		return signPdfSelf(out, bo, webinf, pwd, false);
	}	
	
	public static PdfPKCS7Info signPdfSelf(OutputStream out, ByteArrayOutputStream bo, String webinf, String pwd, boolean rxcode) throws Exception {
		return signPdfSelf(out, bo, webinf, pwd, false, null, rxcode);
	}
	
	public static PdfPKCS7Info signPdfSelf(OutputStream out, ByteArrayOutputStream bo, String webinf, String pwd,
			boolean removeBookMarkAll, String startWith, boolean rxcode) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();
		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		String timestamppath = globalParams.getTimestampPath();

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

		certInfo.setPdfPermision(globalParams.getPdfPermision());
		certInfo.setPdfEncryption(globalParams.getPdfEncryptAlg());
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> getPdfEncryptAlg = " + globalParams.getPdfEncryptAlg());
		
		certInfo.setCertificationLevel(globalParams.getCertificationLevel());
		certInfo.setSignatureAlgorithm(globalParams.getSignatureAlgorithm());
		certInfo.setLocation(globalParams.getLocation());
		certInfo.setPos(globalParams.getSignPosition());
		certInfo.setReason(globalParams.getReason());

		if (StringUtils.isEmpty(timestamppath)) {
			certInfo.setTimeStampPath(globalParams.getTimestampPath());
		} else {
			certInfo.setTimeStampPath(timestamppath);
		}

		certInfo.setDisplayTimeStamp(globalParams.isDiaplayTimestamp());
		certInfo.setSignatureName(globalParams.getSignatureName());
		certInfo.setSignerContact(globalParams.getSignerContact());
		
		String company = globalParams.getProperty("com.cabsoft.rx.pdfsign.campany");
		if(company==null || "".equals(company)){
			company = globalParams.getSignatureName();
		}
		certInfo.setTimeStampOrgan(company);
		
		/**
		 * 인영 마크에 시각 정보 표시 여부
		 */
		certInfo.setDisplayTimeStamp(true);

		/**
		 * 인영 마크의 크기(%)
		 */
		certInfo.setScalefactor(10.0f);

		/**
		 * 전자서명 인영 마크 이미지 파일
		 * 
		 * sign_c1.png - 미검증 sign_c2.png - 진본 sign_c3.png - 변조 sign_c4.png -
		 * 인증서검증실패
		 */
		certInfo.setSealImage("sign_c2.png");

		/**
		 * 검증 결과에 따른 인영 마크 설정
		 */
		certInfo.setShadowMark2(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c2.png"));
		certInfo.setShadowMark3(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c3.png"));
		certInfo.setShadowMark4(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c4.png"));

		/**
		 * 워터마크 설정
		 */
		boolean isInsertWaterMark = globalParams.getBooleanProperty("com.cabsoft.rx.smartcert.insertwatermark", false);
		if(isInsertWaterMark){
			certInfo.setWatermark(globalParams.getCabsoftPath()  + globalParams.getProperty("com.cabsoft.rx.smartcert.watermark"));
			certInfo.setWatermarkTrasparancy(globalParams.getFloatProperty("com.cabsoft.watermark.transparancy", 0.4f));
		}

		
		/**
		 * 시각 정보를 Multi-line로 표시 사각형 인영 마크를 사용할 경우 false로 설정
		 */
		certInfo.setMultiline(true);
		certInfo.setCircleType(true);

		/**
		 * 폰트 설정
		 */
		Font font = new Font("Times New Roman", Font.PLAIN, 60);
		certInfo.setFont(font);
		certInfo.setColor(Color.black);

		/**
		 * 시작 Y 좌표 및 줄간 간격 - 원인 경우 아래의 설정을 따름
		 */
		certInfo.setTop(280);
		certInfo.setVGap(60);

		if(rxcode){
//			certInfo.setCommentTitle("인쇄시 유의사항");
//			certInfo.setComments("인쇄시 [실제크기]를 선택하셔야합니다.");
		}
		
		certInfo.setDetached(true);

		if (!StringUtils.isEmpty(pwd)) {
			certInfo.setUserPassword(pwd);
		}

		ISignPdf signservlet = new SignPdf();

		signservlet.Sign(bo, out, certInfo, removeBookMarkAll, startWith);

		/*
		 * 모바일에서 스마트 코드를 이용하여 원본대조를 하는 경우 PDF 문서를 검증하지 않고 발급 정보를 기록하기 위해 추가됨
		 */
		PdfPKCS7Info info = new PdfPKCS7Info(certInfo, null);
		log.debug("발급 일시: " + info.getSignDate());
		log.debug(info.getInfoString());

		return info;
	}
	
	public static PdfPKCS7Info signPdfSelf2File(FileOutputStream out, ByteArrayOutputStream o, String webinf, String pwd) throws Exception {
		return signPdfSelf2File(out, o, webinf, pwd, false, null);
	}

	public static PdfPKCS7Info signPdfSelf2File(FileOutputStream out, ByteArrayOutputStream o, String webinf, String pwd,
			boolean removeBookMarkAll, String startWith) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		String timestamppath = globalParams.getTimestampPath();

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

		certInfo.setPdfPermision(globalParams.getPdfPermision());
		certInfo.setPdfEncryption(globalParams.getPdfEncryptAlg());
		
		certInfo.setCertificationLevel(globalParams.getCertificationLevel());
		certInfo.setSignatureAlgorithm(globalParams.getSignatureAlgorithm());
		certInfo.setLocation(globalParams.getLocation());
		certInfo.setPos(globalParams.getSignPosition());
		certInfo.setReason(globalParams.getReason());

		if (timestamppath == null || timestamppath.equalsIgnoreCase("")) {
			certInfo.setTimeStampPath(globalParams.getTimestampPath());
		} else {
			certInfo.setTimeStampPath(timestamppath);
		}

		certInfo.setDisplayTimeStamp(globalParams.isDiaplayTimestamp());
		certInfo.setSignatureName(globalParams.getSignatureName());
		certInfo.setSignerContact(globalParams.getSignerContact());

		/**
		 * 인영 마크에 시각 정보 표시 여부
		 */
		certInfo.setDisplayTimeStamp(true);

		/**
		 * 인영 마크의 크기(%)
		 */
		certInfo.setScalefactor(10.0f);

		/**
		 * 전자서명 인영 마크 이미지 파일
		 * 
		 * sign_c1.png - 미검증 sign_c2.png - 진본 sign_c2.png - 변조 sign_c4.png -
		 * 인증서검증실패
		 */
		certInfo.setSealImage("sign_c1.png");

		/**
		 * 검증 결과에 따른 인영 마크 설정
		 */
		certInfo.setShadowMark2(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c2.png"));
		certInfo.setShadowMark3(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c3.png"));
		certInfo.setShadowMark4(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c4.png"));

		/**
		 * 시각 정보를 Multi-line로 표시 사각형 인영 마크를 사용할 경우 false로 설정
		 */
		certInfo.setMultiline(true);
		certInfo.setCircleType(true);

		/**
		 * 폰트 설정
		 */
		Font font = new Font("Times New Roman", Font.PLAIN, 60);
		certInfo.setFont(font);

		certInfo.setColor(Color.black);

		/**
		 * 시작 Y 좌표 및 줄간 간격 - 사각인 경우 아래의 설정을 따름 certInfo.setTop(80);
		 * certInfo.setVGap(60);
		 */

		/**
		 * 시작 Y 좌표 및 줄간 간격 - 원인 경우 아래의 설정을 따름
		 */
		certInfo.setTop(280);
		certInfo.setVGap(60);

		certInfo.setDetached(true);

		if (pwd != null && !pwd.equalsIgnoreCase("")) {
			certInfo.setUserPassword(pwd);
		}

		ISignPdf signservlet = new SignPdf();

		signservlet.Sign(o, out, certInfo, removeBookMarkAll, startWith);

		/*
		 * 모바일에서 스마트 코드를 이용하여 원본대조를 하는 경우 PDF 문서를 검증하지 않고 발급 정보를 기록하기 위해 추가됨
		 */
		PdfPKCS7Info info = new PdfPKCS7Info(certInfo, null);
		log.debug("발급 일시: " + info.getSignDate());
		log.debug(info.getInfoString());

		return info;
	}

	public static PdfPKCS7Info signPdfSelf(FileOutputStream out, ByteArrayOutputStream bo, String webinf, String pwd) throws Exception {
		return signPdfSelf(out, bo, webinf, pwd, false, null);
	}
	
	public static PdfPKCS7Info signPdfSelf(FileOutputStream out, ByteArrayOutputStream bo, String webinf, String pwd,
			boolean removeBookMarkAll, String startWith) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		String timestamppath = globalParams.getTimestampPath();

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

		certInfo.setPdfPermision(globalParams.getPdfPermision());
		certInfo.setPdfEncryption(globalParams.getPdfEncryptAlg());
		
		certInfo.setCertificationLevel(globalParams.getCertificationLevel());
		certInfo.setSignatureAlgorithm(globalParams.getSignatureAlgorithm());
		certInfo.setLocation(globalParams.getLocation());
		certInfo.setPos(globalParams.getSignPosition());
		certInfo.setReason(globalParams.getReason());

		if (timestamppath == null || timestamppath.equalsIgnoreCase("")) {
			certInfo.setTimeStampPath(globalParams.getTimestampPath());
		} else {
			certInfo.setTimeStampPath(timestamppath);
		}

		certInfo.setDisplayTimeStamp(globalParams.isDiaplayTimestamp());
		certInfo.setSignatureName(globalParams.getSignatureName());
		certInfo.setSignerContact(globalParams.getSignerContact());

		/**
		 * 인영 마크에 시각 정보 표시 여부
		 */
		certInfo.setDisplayTimeStamp(true);

		/**
		 * 인영 마크의 크기(%)
		 */
		certInfo.setScalefactor(10.0f);

		/**
		 * 전자서명 인영 마크 이미지 파일
		 * 
		 * sign_c1.png - 미검증 sign_c2.png - 진본 sign_c2.png - 변조 sign_c4.png -
		 * 인증서검증실패
		 */
		certInfo.setSealImage("sign_c1.png");

		/**
		 * 검증 결과에 따른 인영 마크 설정
		 */
		certInfo.setShadowMark2(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c2.png"));
		certInfo.setShadowMark3(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c3.png"));
		certInfo.setShadowMark4(ImageToByte.getImage(certInfo.getTimeStampPath() + "sign_c4.png"));

		/**
		 * 시각 정보를 Multi-line로 표시 사각형 인영 마크를 사용할 경우 false로 설정
		 */
		certInfo.setMultiline(true);
		certInfo.setCircleType(true);

		/**
		 * 폰트 설정
		 */
		Font font = new Font("Times New Roman", Font.PLAIN, 60);
		certInfo.setFont(font);

		certInfo.setColor(Color.black);/**/

		/**
		 * 시작 Y 좌표 및 줄간 간격 - 사각인 경우 아래의 설정을 따름 certInfo.setTop(80);
		 * certInfo.setVGap(60);
		 */

		/**
		 * 시작 Y 좌표 및 줄간 간격 - 원인 경우 아래의 설정을 따름
		 */
		certInfo.setTop(280);
		certInfo.setVGap(60);

		certInfo.setDetached(true);

		if (pwd != null && !pwd.equalsIgnoreCase("")) {
			certInfo.setUserPassword(pwd);
		}

		ISignPdf signservlet = new SignPdf();
		signservlet.Sign(bo, out, certInfo, removeBookMarkAll, startWith);

		/*
		 * 모바일에서 스마트 코드를 이용하여 원본대조를 하는 경우 PDF 문서를 검증하지 않고 발급 정보를 기록하기 위해 추가됨
		 */
		PdfPKCS7Info info = new PdfPKCS7Info(certInfo, null);
		log.debug("발급 일시: " + info.getSignDate());
		log.debug(info.getInfoString());

		return info;
	}
}
