package com.cabsoft.sign.relay;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.GlobalParams;
import com.cabsoft.org.spongycastle.tsp.TimeStampToken;
import com.cabsoft.org.spongycastle.tsp.TimeStampTokenInfo;
import com.cabsoft.pdfutils.CertInfo;
import com.cabsoft.pdfutils.ImageToByte;
import com.cabsoft.pdfutils.Sign.security.SocketSignature;
import com.cabsoft.pdfutils.Sign.security.TSAInfoSpongyCastle;
import com.cabsoft.pdfutils.Sign.Interface.ICertInfo;
import com.cabsoft.pdfutils.Sign.Interface.IRemoteSignature;
import com.cabsoft.pdfutils.Sign.Interface.ITimestampOCSP;
import com.cabsoft.pdfutils.Sign.TimestampOCSP;
import com.cabsoft.smartcert.mobile.PdfPKCS7Info;
import com.cabsoft.text.pdf.PdfSignatureAppearance;
import com.cabsoft.tsa.server.sign.DataTransfer;
import com.cabsoft.tsa.server.sign.IDataTransfer;
import com.cabsoft.tsa.server.utils.Hex;
import com.cabsoft.tsa.server.utils.OID;
import com.cabsoft.utils.StringUtils;



/**
 * 전자문서에 전자서명을 구현
 * 
 * @author ykkim@cabsoftware.com
 * 
 */
public class PdfSignRelayService {
	private static final Log log = LogFactory.getLog(PdfSignRelayService.class);

	public static PdfPKCS7Info signPdf(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();
		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		PdfPKCS7Info info = null;

		info = relayService( out, o, webinf, pwd, false , null);
		
		return info;
	}

	private static PdfPKCS7Info relayService(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd,
			boolean removeBookMarkAll, String startWith) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}
		String host = globalParams.getProperty("com.cabsoft.tsa.relay.server");
		int port = globalParams.getIntegerProperty("com.cabsoft.tsa.relay.port", 4321);
		

		String signaturename = globalParams.getProperty("com.cabsoft.rx.pdfsign.signaturename");
		boolean displaystamp = "true".equalsIgnoreCase(globalParams.getProperty("com.cabsoft.rx.pdfsign.displaystamp")) ? true : false ;
		String signercontact = globalParams.getProperty("com.cabsoft.rx.pdfsign.signercontact");
		String location = globalParams.getProperty("com.cabsoft.rx.pdfsign.location");
		String reason = globalParams.getProperty("com.cabsoft.rx.pdfsign.reason");
		String alg = globalParams.getProperty("com.cabsoft.yessign.alg");
		String timestamppath = globalParams.getTimestampPath();
		boolean usetsa = globalParams.getBooleanProperty("com.cabsoft.tsa.relay.tsa", true);
		
		float transparancy = globalParams.getFloatProperty("com.cabsoft.watermark.transparancy", -1.0f);
		String wm = globalParams.getProperty("com.cabsoft.pdf.verify.watermark");
		wm = (wm==null || "".equals(wm)) ? "watermark.gif" : wm;
		
		//이전 PDF 생성에서 워터마크 삽입 여부
		boolean isInsertWaterMark = globalParams.getBooleanProperty("com.cabsoft.rx.html.insertwatermark", false);
		
		ICertInfo certInfo = new CertInfo();

		/**
		 * 타임스탬프 기관
		 */
		certInfo.setTimeStampOrgan("yessign");

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
		certInfo.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);

		/**
		 * 서명 위치
		 */
		certInfo.setLocation(location);

		/**
		 * 서명 원인
		 */
		certInfo.setReason(reason);

		/*
		 * Signature Name - 서명자 이름
		 */
		certInfo.setSignatureName(signaturename);

		/*
		 * 서명자의 연락처 정보
		 */
		certInfo.setSignerContact(signercontact);

		/**
		 * Message Digest Algorithm
		 */
		certInfo.setDigestAlgorithm(alg);

		if (timestamppath == null || timestamppath.equalsIgnoreCase("")) {
			certInfo.setTimeStampPath(globalParams.getTimestampPath());
		} else {
			certInfo.setTimeStampPath(timestamppath);
		}

		/**
		 * 인영 마크에 시각 정보 표시 여부
		 */
		certInfo.setDisplayTimeStamp(displaystamp);

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
		certInfo.setSealImage("yessign_c2.png");
		certInfo.setMultiline(true);
		certInfo.setCircleType(true);
		certInfo.setTop(280);
		certInfo.setVGap(60);

		certInfo.setPos(globalParams.getSignPosition());

		/**
		 * 검증 결과에 따른 인영 마크 설정
		 */
		certInfo.setShadowMark2(ImageToByte.getImage(certInfo.getTimeStampPath() + "yessign_c2.png"));
		certInfo.setShadowMark3(ImageToByte.getImage(certInfo.getTimeStampPath() + "yessign_c3.png"));
		certInfo.setShadowMark4(ImageToByte.getImage(certInfo.getTimeStampPath() + "yessign_c4.png"));

		/**
		 * PDF 검증기에서 사용될 화면 및 인쇄용 워터마크 설정
		 */
		if(!isInsertWaterMark){
			certInfo.setPrintWatermark(ImageToByte.getImage(certInfo.getTimeStampPath() + wm));
			certInfo.setScreenWatermark(ImageToByte.getImage(certInfo.getTimeStampPath() + wm));
			certInfo.setWatermarkTrasparancy(transparancy);
		}

		certInfo.setDetached(true);

		if (pwd != null && !pwd.equalsIgnoreCase("")) {
			certInfo.setUserPassword(pwd);
		}

		IRemoteSignature remoteSignature = null;
		IDataTransfer transferData = null;
		
		try {
			remoteSignature = new SocketSignature(host, port, alg);
			transferData = new DataTransfer();
			
			transferData.setUseTsa(usetsa);
			
	        remoteSignature.setTSAInfo(new TSAInfoSpongyCastle(){
	            public void inspectTimeStampTokenInfo(TimeStampTokenInfo info) {
	                 System.out.println("===========================================================");
	                 System.out.println("General Time: " + info.getGenTime());
	                 System.out.println("Serial Number: " + info.getSerialNumber());
	                 System.out.println("Hash Algorithm: " + OID.getAlgName(info.getHashAlgorithm().getAlgorithm().getId()));
	                 System.out.println("Message Imprint Digest: " + new String(Hex.encode(info.getMessageImprintDigest())));
	                 System.out.println("===========================================================");
	        }});
			
	        certInfo.setTransferData(transferData);
	        certInfo.setRemoteSignature(remoteSignature);
	        certInfo.setLocalTest(false);
	        ITimestampOCSP signTsa = new TimestampOCSP();
	        signTsa.Sign(o, out, certInfo, removeBookMarkAll, startWith);
	        String isOK = certInfo.getTransferData().getResult();
	        if(!isOK.equalsIgnoreCase("OK")){
	        	log.error("Relay Server Error: " + isOK);
	        	throw new Exception(isOK);
	        }
		}catch (Exception e) {
			log.error(e);
			String errMsg = "TSA Relay]\n";
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
	        log.error(e);
	        throw new Exception( errMsg );
		} 
		/*
		 * 모바일에서 스마트 코드를 이용하여 원본대조를 하는 경우 PDF 문서를 검증하지 않고 발급 정보를 기록하기 위해 추가됨
		 */
		TimeStampToken token = remoteSignature.getTimeStampToken();
		PdfPKCS7Info info = new PdfPKCS7Info(certInfo, token, certInfo.getTransferData().getCertificate());
		log.debug("발급 일시: " + info.getSignDate());
		log.debug(info.getInfoString());

		return info;

	}	
}
