package com.cabsoft.sign.service;

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
import com.cabsoft.eForminfo;
import com.cabsoft.org.spongycastle.tsp.TimeStampTokenInfo;
import com.cabsoft.pdfutils.CertInfo;
import com.cabsoft.pdfutils.ImageToByte;
import com.cabsoft.pdfutils.Sign.security.TSAClient;
import com.cabsoft.pdfutils.Sign.security.TSAClientSpongyCastle;
import com.cabsoft.pdfutils.Sign.security.TSAInfoSpongyCastle;
import com.cabsoft.pdfutils.Sign.SignCertUtil;
import com.cabsoft.pdfutils.Sign.Interface.ICertInfo;
import com.cabsoft.pdfutils.Sign.Interface.ITimestampOCSP;
import com.cabsoft.pdfutils.Sign.TimestampOCSP;
import com.cabsoft.sign.relay.PdfSignRelayService;
import com.cabsoft.smartcert.mobile.PdfPKCS7Info;
import com.cabsoft.utils.Hex;
import com.cabsoft.utils.OID;
import com.cabsoft.utils.StackTrace;
import com.cabsoft.utils.StringUtils;



/**
 * 전자문서에 전자서명을 구현
 * 
 * @author ykkim@cabsoftware.com
 * 
 */
public class PdfSignTsaService {
	private static final Log log = LogFactory.getLog(PdfSignTsaService.class);

	public static PdfPKCS7Info signPdf(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();
		eForminfo eforminfo = eForminfo.getInstance();
		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		PdfPKCS7Info info = null;

		boolean withTSA = globalParams.getWithTSA();
		boolean userealy = globalParams.getBooleanProperty("com.cabsoft.tsa.relay.use", false);
		String TSAType = globalParams.getTSAType();
		
		log.debug("withTSA = " + withTSA);
		log.debug("TSAType = " + TSAType);

		if (withTSA == false) {
			info = PdfSignService.signPdfSelf(out, o, webinf, pwd);
			eforminfo.setMapTime("PdfSignService");
			eforminfo.setMapCount("PdfSignServiceCnt");
		} else {
			if(userealy){
				info = PdfSignRelayService.signPdf(out, o, webinf, pwd);
				eforminfo.setMapTime("PdfSignServiceTSARelay");
				eforminfo.setMapCount("PdfSignServiceTSARelayCnt");
			}else{
				info = signPdfWithTSAOCSP( out, o, webinf, pwd, false , null);
				eforminfo.setMapTime("PdfSignServiceTSA");
				eforminfo.setMapCount("PdfSignServiceTSACnt");
			}
		}
		
		return info;
	}

	private static PdfPKCS7Info signPdfWithTSAOCSP(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd,
			boolean removeBookMarkAll, String startWith) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}
		String TSAType = globalParams.getTSAType();
		boolean TSASocket = globalParams.getTSASocket();
		String timestamppath = globalParams.getTimestampPath();

		String TSA_URL = "";
		String TSA_ACCNT = "";
		String TSA_PASSW = "";
		String alg = "";
		String tsaSignCert ="";
		String tsaPrivateKey = "";
		String tsaPriPwd = "";
		
		String kmCert = "";
		String kmPrivateKey = "";
		String kmPriPwd = "";
		
		String certPath = globalParams.getCabsoftPath() + "cert/";
		
		if ("yessign".equalsIgnoreCase(TSAType)) {
			TSA_URL = globalParams.getProperty("com.cabsoft.yessign.url");
			TSA_ACCNT = globalParams.getProperty("com.cabsoft.yessign.userid");
			TSA_PASSW = globalParams.getProperty("com.cabsoft.yessign.password");
			alg = globalParams.getProperty("com.cabsoft.yessign.alg");
		}else if("koscom".equalsIgnoreCase(TSAType)){
			TSA_URL = globalParams.getProperty("com.cabsoft.koscom.url");
			TSA_ACCNT = globalParams.getProperty("com.cabsoft.koscom.userid");
			TSA_PASSW = globalParams.getProperty("com.cabsoft.koscom.password");
			alg = globalParams.getProperty("com.cabsoft.koscom.alg");
			
		}else if("kica".equalsIgnoreCase(TSAType)){
			/*
			 * KICA 연동시 문의 바람
			 */
			TSA_URL = globalParams.getProperty("com.cabsoft.kica.url");
			TSA_ACCNT = globalParams.getProperty("com.cabsoft.kica.userid");
			TSA_PASSW = globalParams.getProperty("com.cabsoft.kica.password");
			alg = globalParams.getProperty("com.cabsoft.kica.alg");
			
		} else {
			// 값이 없는 경우 디폴트값 설정(cabsoft)
			 TSA_URL = globalParams.getProperty("com.cabsoft.rx.cabsoft.TSA_URL");
			 TSA_ACCNT = globalParams.getProperty("com.cabsoft.rx.cabsoft.TSA_ACCOUNT");
			 TSA_PASSW = globalParams.getProperty("com.cabsoft.rx.cabsoft.TSA_PASSWORD");
			alg = globalParams.getProperty("com.cabsoft.cabsoft.alg");
		}

		String signaturename = globalParams.getProperty("com.cabsoft.rx.pdfsign.signaturename");
		boolean displaystamp = "true".equalsIgnoreCase(globalParams.getProperty("com.cabsoft.rx.pdfsign.displaystamp")) ? true : false ;
		String signercontact = globalParams.getProperty("com.cabsoft.rx.pdfsign.signercontact");
		String location = globalParams.getProperty("com.cabsoft.rx.pdfsign.location");
		String reason = globalParams.getProperty("com.cabsoft.rx.pdfsign.reason");
		
		TSAClient tsaClient = new TSAClientSpongyCastle(TSA_URL, TSA_ACCNT, TSA_PASSW, 4096, alg);

		ICertInfo certInfo = new CertInfo();

		/**
		 * PDF 전자서명용 인증서
		 * 
		 * 인증서에 대한 싱글톤
		 */
		SignCertUtil signCert = null;
		if (SignCertFactory.getInstance().getPdfSignCert() == null) {
			if (globalParams.isPFX() == true) {
				signCert = new SignCertUtil(globalParams.getSignCertFile(),
						globalParams.getSignCertPwd(), globalParams.isWithBC());
			} else {
				signCert = new SignCertUtil(globalParams.getSignCertFile(),
						globalParams.getSignCertKeyFile(),
						globalParams.getSignCertPwd());
			}
			SignCertFactory.getInstance().setPdfSignCert(signCert);
		} else {
			signCert = SignCertFactory.getInstance().getPdfSignCert();
		}
		certInfo.CertInfo(signCert);
	
	
			if ("yessign".equalsIgnoreCase(TSAType)) {
				/**
				 * TSA 인증서에 대한 싱글톤
				 */
				SignCertUtil signCertUtil = null;
				if (SignCertFactory.getInstance().getTsaSignCert() == null) {
					tsaSignCert = globalParams.getProperty("com.cabsoft.yessign.sign.cert");
					tsaPrivateKey = globalParams.getProperty("com.cabsoft.yessign.sign.privatekey");
					tsaPriPwd = globalParams.getProperty("com.cabsoft.yessign.sign.password");
					
					log.debug("tsaSignCert = " + tsaSignCert);
					log.debug("tsaPrivateKey = " + tsaPrivateKey);
					signCertUtil = new SignCertUtil(certPath + tsaSignCert, certPath + tsaPrivateKey, tsaPriPwd);
					SignCertFactory.getInstance().setTsaSignCert(signCertUtil);
				} else {
					signCertUtil = SignCertFactory.getInstance().getTsaSignCert();
				}
				certInfo.setSignCertUtil(signCertUtil);
				if (TSASocket){
					/*************************************************************************************
					 * Socket 방식을 사용하는 경우
					 *************************************************************************************/
					 SignCertUtil kmCertUtil = null;
					 if(SignCertFactory.getInstance().getTsaKmCert()==null){
						 kmCert = globalParams.getProperty("com.cabsoft.yessign.km.cert");
						 kmPrivateKey = globalParams.getProperty("com.cabsoft.yessign.km.privatekey");
						 kmPriPwd = globalParams.getProperty("com.cabsoft.yessign.km.password");
						log.debug("kmCert = " + tsaSignCert);
						log.debug("kmPrivateKey = " + tsaPrivateKey);
							
						 kmCertUtil =new SignCertUtil(certPath+kmCert, certPath+kmPrivateKey, kmPriPwd);
						 SignCertFactory.getInstance().setTsaKmCert(kmCertUtil); 
					 }else{
							 kmCertUtil = SignCertFactory.getInstance().getTsaKmCert(); 
					 }
					 
					 String host = globalParams.getProperty("com.cabsoft.yessign.socket.server");
					 int port = globalParams.getIntegerProperty("com.cabsoft.yessign.socket.port");
					 String signatureAlgorithm = globalParams.getProperty("com.cabsoft.yessign.socket.signatureAlgorithm");
					 
					 tsaClient = new TSAClientSpongyCastle(host, port, 4096, signatureAlgorithm, alg);
					 
					 certInfo.setKmCertUtil(kmCertUtil);
					/*************************************************************************************/
				}
		}
			
		tsaClient.setTSAInfo(new TSAInfoSpongyCastle(){
			public void inspectTimeStampTokenInfo(TimeStampTokenInfo info) {
				log.debug("\n===========================================================");
				log.debug("General Time: " + info.getGenTime());
				log.debug("Serial Number: " + info.getSerialNumber());
				log.debug("Hash Algorithm: " + OID.getAlgName(info.getHashAlgorithm().getAlgorithm().getId()));
				log.debug("Message Imprint Digest: " + new String(Hex.encode(info.getMessageImprintDigest())));
				log.debug("===========================================================\n");
	            }
			});
			
		/**
		 * 타임스탬프 기관
		 */
		certInfo.setTimeStampOrgan(TSAType);

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
		certInfo.setSealImage(TSAType+"_c" + String.valueOf(globalParams.getDefaultTimestamp()) + ".png");
		certInfo.setMultiline(true);
		certInfo.setCircleType(true);
		certInfo.setTop(280);
		certInfo.setVGap(60);

		certInfo.setPos(globalParams.getSignPosition());

		/**
		 * 검증 결과에 따른 인영 마크 설정
		 */
		certInfo.setShadowMark2(ImageToByte.getImage(certInfo.getTimeStampPath() + TSAType+"_c2.png"));
		certInfo.setShadowMark3(ImageToByte.getImage(certInfo.getTimeStampPath() + TSAType+"_c3.png"));
		certInfo.setShadowMark4(ImageToByte.getImage(certInfo.getTimeStampPath() + TSAType+"_c4.png"));

//		/**
//		 * PDF 검증기에서 사용될 화면 및 인쇄용 워터마크 설정
//		 */
//		certInfo.setPrintWatermark(ImageToByte.getImage(certInfo.getTimeStampPath() + "wooribank.png"));
//		certInfo.setScreenWatermark(ImageToByte.getImage(certInfo.getTimeStampPath() + "wooribank.png"));

		certInfo.setDetached(true);

		if (pwd != null && !pwd.equalsIgnoreCase("")) {
			certInfo.setUserPassword(pwd);
		}

		/*
		 * TSA 정보를 ICertInfo에 설정
		 */
		certInfo.setTsc(tsaClient);

		/*
		 * PDFA 변환 설정
		 */
		/*
		 * certInfo.setPDFA_Type(PdfWriter.PDFA1A); ConvertPDFA c = new
		 * ConvertPDFA(); c.setIccProfile(globalParams.getIccfile());
		 * certInfo.setConvertPDFA(c);
		 */
		try {
			ITimestampOCSP signservlet = new TimestampOCSP();
			signservlet.Sign(o, out, certInfo, removeBookMarkAll, startWith);
		}catch (Exception e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1 || se.indexOf("ClientAbortException") > -1) {
//				System.out.println(">>>>>>>> java.net.SocketException");
			}else{
				log.error(StackTrace.getStackTrace(e));
				String errMsg = "["+TSAType+" TSA]\n";
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
		} 
		/*
		 * 모바일에서 스마트 코드를 이용하여 원본대조를 하는 경우 PDF 문서를 검증하지 않고 발급 정보를 기록하기 위해 추가됨
		 */
		PdfPKCS7Info info = new PdfPKCS7Info(certInfo, tsaClient.getTimeStampToken());
		log.debug("발급 일시: " + info.getSignDate());
		log.debug(info.getInfoString());

		return info;

	}	
}
