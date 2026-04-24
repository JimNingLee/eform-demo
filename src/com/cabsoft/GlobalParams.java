package com.cabsoft;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.text.pdf.PdfWriter;
import com.cabsoft.utils.StackTrace;
import com.cabsoft.utils.SystemUtils;

/**
 * @author 이강구
 *
 */

@SuppressWarnings({"deprecation", "unused"})
public class GlobalParams {
	private static Log log = LogFactory.getLog(GlobalParams.class);

	protected static Object lock = new Object();

	private static GlobalParams instance = null;
	
	private CommentedProperties prop = null;

	private String web_inf = "";

	/*
	 * reportexpress.properties 파일 수정 정보
	 */
	private static long propertiesDate = 0;

	private final String propertiesFS = "reportexpress.properties";
	
	private String encoding = "UTF-8";
	
	/*
	 * PDF Form에서 사용할 폰트 경로
	 */
	private String FontPath = "";

	private String contextpath = "";
	private String errorpageurl = "";
	private String imagepath = "";
	private String pdfversion = "4";
	private boolean savepdffile = false;

	/*
	 * 인증서 정보
	 */
	private String CabsoftPath = "";
	private String outputPath = "";
	private String signCertFile = "";
	private String signCertKeyFile = "";
	private String signCertPwd = "";
	private boolean PFX = true;
	private boolean WithBC = true;

	/*
	 * 전자서명 및 타임스탬프 정보
	 */
	private String SignatureAlgorithm = "";
	private String Location = "";
	private String Reason = "";
	private int DefaultTimestamp = 2;
	private String TimestampPath = "";
	private String TimestampImage = "";
	private String SignatureName = "";
	private String SignerContact = "";
	private int CertificationLevel = 3;
	private int SignPosition = 0;
	private boolean DiaplayTimestamp = true;

	/**
	 * TSA 정보
	 */
	private boolean WithTSA = true;
	private boolean WithTSA1 = true;
	private String MessageDigestAlg = "";
	private boolean WithKICA;
	private String Kica_url = "";
	private String Kica_UserID = "";
	private String Kica_UserPwd = "";
	private String Kica_MDAlg = "";

	/**
	 * 개발용 TSA 정보
	 */
	private String Tsa_url = "";
	private String Tsa_Account = "";
	private String Tsa_Pwd = "";
	private boolean WithOCSP;
	
	private String TSAType = "";
	private boolean TSASocket = false;
	private boolean withTSA;

	/*
	 * PDFA에 사용할 ICC 프로파일
	 */
	private String iccfile = "";

	/*
	 * PDFA 형식
	 */
	private String PDFA = "";

	/*
	 * SignedPDF 형식
	 */
	private String SignedPDFWaterMark = "";

	/*
	 * 증명서에서 사용할 이미지가 저장된 경로
	 */
	private String certImagePath = "";

	/*
	 * 기본 바코드 이미지 경로
	 */
	private String barcodePath = "";

	/*
	 * 원본 저장 경로
	 */
	private String original = "";

	private GlobalParams(ServletContext context) throws Exception {
		log.debug("Create bnw Instance");
		String fsp = System.getProperty("file.separator");

		web_inf = context.getRealPath("/WEB-INF/");
		web_inf = SystemUtils.replaceSystemPathString(web_inf);
		web_inf = web_inf.endsWith(fsp) ? web_inf : web_inf + fsp;

		InitParams(web_inf);

	}

	private GlobalParams(String webinf) throws Exception {
		log.debug("Create new Instance");
		web_inf = webinf;
		InitParams(web_inf);
	}

	protected boolean ConfigFileChanged(String webinf) {
		log.debug("ConfigFileChanged(String webinf) - Start");
		synchronized (lock) {
			boolean ret = false;

			try {
				String fsp = System.getProperty("file.separator");

				web_inf = SystemUtils.replaceSystemPathString(webinf);
				web_inf = web_inf.endsWith(fsp) ? web_inf : web_inf + fsp;

				// String cab = web_inf + "properties" + fsp + "cabsoft" + fsp;

				/* reportexpress.properties 체크 */
				File f = new File(web_inf + "classes" + fsp + propertiesFS);
				log.debug(propertiesDate + " : " + f.lastModified());
				if (propertiesDate != f.lastModified()) {
					log.debug(propertiesFS + " changed");
					log.debug(propertiesDate + " : " + f.lastModified());
					propertiesDate = f.lastModified();
					ret = true;
				}
			} catch (Exception e) {
				log.error(StackTrace.getStackTrace(e));
			}
			return ret;
		}
	}

	private void DisplayLog() {
		log.debug("eForm Configuration");
		/*
		 * 인증서 정보
		 */
		log.debug("CabsoftPath = " + CabsoftPath);
		System.out.println("CabsoftPath = " + CabsoftPath);
		log.debug("outputPath = " + outputPath);
		System.out.println("outputPath = " + outputPath);
		log.debug("signCertFile = " + signCertFile);
		System.out.println("signCertFile = " + signCertFile);
		log.debug("signCertKeyFile = " + signCertKeyFile);
		System.out.println("signCertKeyFile = " + signCertKeyFile);
		log.debug("signCertPwd = " + signCertPwd);
		System.out.println("signCertPwd = " + signCertPwd);
		log.debug("PFX = " + PFX);
		log.debug("WithBC = " + WithBC);

		/*
		 * 전자서명 및 타임스탬프 정보
		 */
		log.debug("SignatureAlgorithm = " + SignatureAlgorithm);
		log.debug("Location = " + Location);
		log.debug("Reason = " + Reason);
		log.debug("DefaultTimestamp = " + DefaultTimestamp);
		log.debug("TimestampPath = " + TimestampPath);
		log.debug("TimestampImage = " + TimestampImage);
		log.debug("SignatureName = " + SignatureName);
		log.debug("SignerContact = " + SignerContact);
		log.debug("CertificationLevel = " + CertificationLevel);
		log.debug("SignPosition = " + SignPosition);
		log.debug("DiaplayTimestamp = " + DiaplayTimestamp);

		/**
		 * TSA 정보
		 */
		log.debug("WithTSA = " + WithTSA);
		log.debug("MessageDigestAlg = " + MessageDigestAlg);
		log.debug("WithKICA = " + WithKICA);
		log.debug("Kica_url = " + Kica_url);
		log.debug("Kica_UserID = " + Kica_UserID);
		log.debug("Kica_UserPwd = " + Kica_UserPwd);
		log.debug("Kica_MDAlg = " + Kica_MDAlg);

		/**
		 * 개발용 TSA 정보
		 */
		log.debug("Tsa_url = " + Tsa_url);
		log.debug("Tsa_Account = " + Tsa_Account);
		log.debug("Tsa_Pwd = " + Tsa_Pwd);
		log.debug("WithOCSP = " + WithOCSP);

		/*
		 * PDFA에 사용할 ICC 프로파일
		 */
		log.debug("Icc_Profile = " + iccfile);

		/*
		 * PDFA 형식
		 */
		log.debug("PDFA = " + PDFA);

		/*
		 * 증명서에서 사용할 이미지가 저장된 경로
		 */
		log.debug("certImagePath = " + certImagePath);

		/*
		 * 기본 바코드 이미지 경로
		 */
		log.debug("barcodePath = " + barcodePath);

		/*
		 * 원본 저장 경로
		 */
		log.debug("original = " + original);

		/*
		 * SmartCert 뷰어 설정
		 */
		// log.debug("smartHeader = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcert/docheader.html"));
		// log.debug("smartMHeader = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcert/mdocheader.html"));
		// log.debug("smartSmartphoneHeader = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcert/smartphone.html"));
		// log.debug("smartFooter = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcert/footer.html"));

		/*
		 * SmartCert 원본 대조 뷰어 설정
		 */
		// log.debug("psmartHeader = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcertprobe/docheader.html"));
		// log.debug("psmartMHeader = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcertprobe/mdocheader.html"));
		// log.debug("psmartSmartphoneHeader = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcertprobe/smartphone.html"));
		// log.debug("psmartFooter = " +
		// SystemUtils.replaceSystemPathString(CabsoftPath +
		// "smartcertprobe/footer.html"));
	}

	private void InitParams(String WEBINF) throws Exception {
		synchronized (lock) {
			log.debug("파라매터 초기 설정");
			loadProperties();
			
			String fsp = System.getProperty("file.separator");

			WEBINF = SystemUtils.replaceSystemPathString(WEBINF);
			WEBINF = WEBINF.endsWith(fsp) ? WEBINF : WEBINF + fsp;

			CabsoftPath = getProperty("com.cabsoft.rx.propertypath");
			
			if(null == CabsoftPath || "".equals(CabsoftPath)){
				CabsoftPath = WEBINF + "properties" + fsp + "cabsoft" + fsp;
				outputPath = WEBINF + "output" + fsp;
			}else{
				CabsoftPath = CabsoftPath.endsWith(fsp) ? CabsoftPath : CabsoftPath + fsp;
				outputPath = CabsoftPath + "output" + fsp;
			}

			
			certImagePath = CabsoftPath + "images" + fsp;
			barcodePath = CabsoftPath + "barcode" + fsp;
			original = CabsoftPath + "original" + fsp;

			contextpath = getProperty("com.cabsoft.rx.contextpath");
			imagepath = getProperty("com.cabsoft.rx.imagepath");
			pdfversion = getProperty("com.cabsoft.rx.pdfversion");
			savepdffile = getBooleanProperty("com.cabsoft.rx.pdfsign.savepdffile");

			PFX = getBooleanProperty("com.cabsoft.rx.pdfsign.pfx");
			WithBC = getBooleanProperty("com.cabsoft.rx.pdfsign.widthbc");

			signCertFile = getProperty("com.cabsoft.rx.pdfsign.certfile");
			signCertFile = CabsoftPath + "cert" + fsp + signCertFile;

			signCertPwd = getProperty("com.cabsoft.rx.pdfsign.certpassword");
			if (PFX == false) {
				signCertKeyFile = getProperty("com.cabsoft.rx.pdfsign.certkeyfile");
				signCertKeyFile = CabsoftPath + "cert" + fsp + signCertKeyFile;
				signCertFile = signCertFile + ".der";
			} else {
				signCertFile = signCertFile + ".pfx";
			}

			SignatureAlgorithm = getProperty("com.cabsoft.rx.pdfsign.signaturealgorithm");
			Location = getProperty("com.cabsoft.rx.pdfsign.location");
			Reason = getProperty("com.cabsoft.rx.pdfsign.reason");
			DefaultTimestamp = getIntegerProperty("com.cabsoft.rx.pdfsign.defaulttimestamp", 3);
			TimestampPath = CabsoftPath + "timestamp" + fsp;
			TimestampImage = getProperty("com.cabsoft.rx.pdfsign.stampimage");
			SignatureName = getProperty("com.cabsoft.rx.pdfsign.signaturename");
			SignerContact = getProperty("com.cabsoft.rx.pdfsign.signercontact");
			CertificationLevel = getIntegerProperty("com.cabsoft.rx.pdfsign.certificationlevel", 3);
			SignPosition = getIntegerProperty("com.cabsoft.rx.pdfsign.signpos", 0);
			DiaplayTimestamp = getBooleanProperty("com.cabsoft.rx.pdfsign.displaystamp");

			WithTSA = getBooleanProperty("com.cabsoft.rx.pdfsign.withTSA");
			MessageDigestAlg = getProperty("com.cabsoft.rx.pdfsign.messagedigestalgorithm");
			WithKICA = getBooleanProperty("com.cabsoft.rx.pdfsign.kica");
			Kica_url = getProperty("com.cabsoft.rx.pdfsign.kica.url");
			Kica_UserID = getProperty("com.cabsoft.rx.pdfsign.kica.userid");
			Kica_UserPwd = getProperty("com.cabsoft.rx.pdfsign.kica.password");
			Kica_MDAlg = getProperty("com.cabsoft.rx.pdfsign.kica.alg");

			Tsa_url = getProperty("com.cabsoft.rx.pdfsign.TSA_URL");
			Tsa_Account = getProperty("com.cabsoft.rx.pdfsign.TSA_ACCOUNT");
			Tsa_Pwd = getProperty("com.cabsoft.rx.pdfsign.TSA_PASSWORD");
			WithOCSP = getBooleanProperty("com.cabsoft.rx.pdfsign.withOCSP");

			TSAType = getProperty("com.cabsoft.rx.pdfsign.TSAType");
			TSASocket = getBooleanProperty("com.cabsoft.rx.pdfsign.TSASocket");
			withTSA = getBooleanProperty("com.cabsoft.rx.pdfsign.withTSA");
			
			Kica_url = replaceNull(Kica_url);
			Tsa_url = replaceNull(Tsa_url);

			iccfile = CabsoftPath + "profile/" + getProperty("com.cabsoft.rx.pdfa.iccfile");
			iccfile = SystemUtils.replaceSystemPathString(iccfile);

			PDFA = getProperty("com.cabsoft.rx.pdfa.type");
			
			FontPath = CabsoftPath + "fonts/";
			
			SignedPDFWaterMark = CabsoftPath + "image/" + getProperty("com.cabsoft.rx.pdfsign.watermark");

			// if(WithBC==true){
			// Security.addProvider(new BouncyCastleProvider());
			// }

			DisplayLog();
		}
	}

	/**
	 * ServletContext를 이용하여 인스턴스 생성
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static GlobalParams getInstance(ServletContext context) throws Exception {
		String fsp = System.getProperty("file.separator");

		String WEBINF = context.getRealPath("/WEB-INF/");
		WEBINF = SystemUtils.replaceSystemPathString(WEBINF);
		WEBINF = WEBINF.endsWith(fsp) ? WEBINF : WEBINF + fsp;

		if (instance == null) {
			instance = new GlobalParams(context);
			log.debug("인스턴스 새로 생성");
		}
		return instance;
	}

	/**
	 * WEB-INF 경로를 이용하여 인스턴스 생성
	 * @param webinf
	 * @return
	 * @throws Exception
	 */
	public static GlobalParams getInstance(String webinf) throws Exception {
		if (instance == null) {
			instance = new GlobalParams(webinf);
		}
		return instance;
	}

	/**
	 * 인스턴스를 반환
	 * @return
	 * @throws Exception
	 */
	public static GlobalParams getInstance() throws Exception {
		log.debug("getInstance() = " + (instance == null ? "instance is null" : instance.getWebInf()));
		if (instance != null && instance.getWebInf() != null && !instance.getWebInf().equalsIgnoreCase("")) {
			log.debug("Check Configuration Changed");
			
			if (instance.ConfigFileChanged(instance.getWebInf()) == true) {
				instance = new GlobalParams(instance.getWebInf());
			}
		}
		return instance;
	}
	
	public void loadProperties() throws Exception{
		if(prop==null){
			prop = new CommentedProperties();
		}
		synchronized (lock) {
			prop.load(encoding);
		}
		
		//printPrintProperties();
	}
	
	@SuppressWarnings("rawtypes")
	private void printPrintProperties(){
        Vector lineData = prop.lineData;
        Vector keyData = prop.keyData;
        
        for (int i = 0; i < lineData.size(); i++) {
            String line = (String) lineData.get(i);
            String key = (String) keyData.get(i);
            if (key.length() > 0) {
                System.out.println(key + " = " + prop.getProperty(key));
            } else {
                System.out.println(line);
            }
        }
	}

	private String replaceNull(String src) {
		return src == null ? "" : src;
	}

	/**
	 * WEB-INF의 Cabsoft 경로를 구한다.
	 * @return
	 */
	public String getCabsoftPath() {
		return CabsoftPath;
	}

	/**
	 * PDF 전자서명용 인증서 파일을 반환한다.
	 * @return
	 */
	public String getSignCertFile() {
		return signCertFile;
	}

	/**
	 * PDF 전자서명용 인증서의 개인키 파일을 반환한다.
	 * @return
	 */
	public String getSignCertKeyFile() {
		return signCertKeyFile;
	}

	/**
	 * PDF 전자서명용 인증서의 개인키 파일에 대한 접근 암호를 반환한다.
	 * @return
	 */
	public String getSignCertPwd() {
		return signCertPwd;
	}

	/**
	 * 인증서가 PFX 형태인지를 봔환한다.
	 * @return
	 */
	public boolean isPFX() {
		return PFX;
	}

	/**
	 * 인영마크 이미지 경로를 반환한다.
	 * @return
	 */
	public String getTimestampPath() {
		return TimestampPath;
	}

	/**
	 * 전자서명 알고리즘을 반환한다.
	 * SignatureAlgorithm: SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA
	 * @return
	 */
	public String getSignatureAlgorithm() {
		return SignatureAlgorithm;
	}

	/**
	 * TSA 사용여부를 설정한다.
	 * @return
	 */
	public boolean  getWithTSA() {
		return withTSA;
	}

	/**
	 * TSA 기관을 설정한다.
	 * @return
	 */
	public String getTSAType() {
		return TSAType;
	}
	
	public boolean getTSASocket(){
		return TSASocket;
	}
	
	public String getLocation() {
		return Location;
	}
	/**
	 * 생성된 파일들이 저장되는 위치를 반환한다.
	 * 
	 * @return outputPath
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * 생성된 파일들이 저장되는 위치를 설정한다.
	 * @param outputPath
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	/**
	 * PDF 문서에 전자서명하는 이유를 반환한다.
	 * @return
	 */
	public String getReason() {
		return Reason;
	}

	/**
	 * 기본 인영마크 정보를 반환한다.
	 * 1 :
	 * 		sealImage = "timestamp1.png";
	 * 		font = new Font("Times New Roman", Font.PLAIN, 30);
	 * 		top = 170;
	 * 		left = 110;
	 * 		vgap = 45;
	 * 		hgap = 15;
	 * 		fontColor = Color.BLACK;
	 * 
	 * 2:
	 * 		sealImage = "timestamp2.png";
	 * 		font = new Font("Times New Roman", Font.PLAIN, 40);
	 * 		top = 170;
	 * 		left = 70;
	 * 		vgap = 35;
	 * 		hgap = 30;
	 * 		fontColor = Color.BLUE;
	 * 
	 * 3:
	 * 		sealImage = "timestamp3.png";
	 * 		font = new Font("Times New Roman", Font.BOLD, 36);
	 * 		top = 135;
	 * 		left = 40;
	 * 		vgap = 30;
	 * 		hgap = 30;
	 * 		fontColor = Color.BLUE; 
	 * @return
	 */
	public int getDefaultTimestamp() {
		return DefaultTimestamp;
	}

	/**
	 * 인영마크 이미지 파일을 반환한다.
	 * @return
	 */
	public String getTimestampImage() {
		return TimestampImage;
	}

	/**
	 * PDF 문서 전자서명자 이름을 반환한다.
	 * @return
	 */
	public String getSignatureName() {
		return SignatureName;
	}

	/**
	 * PDF 문서 전자서명자에 대한 연락처를 반환한다.
	 * @return
	 */
	public String getSignerContact() {
		return SignerContact;
	}

	/**
	 * Certification Level을 반환한다.
	 * 		NOT_CERTIFIED = 0;
	 * 		CERTIFIED_NO_CHANGES_ALLOWED = 1;
	 * 		CERTIFIED_FORM_FILLING = 2;
	 * 		CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;
	 * @return
	 */
	public int getCertificationLevel() {
		return CertificationLevel;
	}

	/**
	 * 인영마크 위치를 반환한다.
	 * 		NONE = -1
	 * 		LeftTop = 0
	 * 		RightTop = 1
	 * 		LeftBottom = 2
	 * 		RightBottom = 4
	 * @return
	 */
	public int getSignPosition() {
		return SignPosition;
	}

	/**
	 * 인영마크에 시각정보를 표시할지를 반환한다.
	 * @return
	 */
	public boolean isDiaplayTimestamp() {
		return DiaplayTimestamp;
	}

	/**
	 * 
	 * @return
	 * @deprecated
	 */
	public String getCertImagePath() {
		return certImagePath;
	}

	/**
	 * 
	 * @return
	 */
	public String getBarcodePath() {
		return hdBarcodePath(); //barcodePath
	}

	/**
	 * BouncyCastle Provider를 사용할지를 반환한다.
	 * @return
	 */
	public boolean isWithBC() {
		return WithBC;
	}

	/**
	 * 원본 문서 저장 위치를 반환한다.
	 * @return
	 * @deprecated
	 */
	public String getOriginal() {
		return original;
	}

	/**
	 * reportexpress.properties에서 key로 설정된 값을 문자열로 반환한다.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}
	
    public String getProperty(String key, String defValue) {
        String value = getProperty(key);
        return value == null ? defValue : value;
    }
	
	public void setProperties(String key, String value){
		prop.add(key, value);
		
	}
	
	public void store() throws Exception{
		prop.store(encoding);
	}

	/**
	 * reportexpress.properties에서 key로 설정된 값을 boolean으로 반환한다.
	 * @param key
	 * @return
	 */
	public boolean getBooleanProperty(String key) {
		return asBoolean(getProperty(key));
	}

	/**
	 * reportexpress.properties에서 key로 설정된 값을 boolean으로 반환한다.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanProperty(String key, boolean defaultValue) {
		String value = getProperty(key);
		return value == null ? defaultValue : asBoolean(value);
	}

	/**
	 * reportexpress.properties에서 key로 설정된 값을 정수형으로 반환한다.
	 * @param key
	 * @return
	 */
	public int getIntegerProperty(String key) {
		return asInteger(getProperty(key));
	}

	/**
	 * reportexpress.properties에서 key로 설정된 값을 정수형으로 반환한다.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getIntegerProperty(String key, int defaultValue) {
		String value = getProperty(key);
		return value == null ? defaultValue : asInteger(value);
	}

	/**
	 * reportexpress.properties에서 key로 설정된 값을 실수형으로 반환한다.
	 * @param key
	 * @return
	 */
	public float getFloatProperty(String key) {
		return asFloat(getProperty(key));
	}

	/**
	 * reportexpress.properties에서 key로 설정된 값을 실수형으로 반환한다.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public float getFloatProperty(String key, float defaultValue) {
		String value = getProperty(key);
		return value == null ? defaultValue : asFloat(value);
	}

	/**
	 * 문자열을 boolean형으로 변환한다.
	 * @param value
	 * @return
	 */
	public boolean asBoolean(String value) {
		return Boolean.valueOf(value).booleanValue();
	}

	/**
	 * 문자열을 정수형으로 변환한다.
	 * @param value
	 * @return
	 */
	public int asInteger(String value) {
		return Integer.parseInt(value);
	}

	/**
	 * 문자열을 실수형으로 변환한다.
	 * @param value
	 * @return
	 */
	public float asFloat(String value) {
		return Float.parseFloat(value);
	}

	/**
	 * Message Digest 알고리즘을 반환한다.
	 * @return
	 */
	public String getMessageDigestAlg() {
		return MessageDigestAlg;
	}

	/**
	 * 한국정보인증 TSA 사용 여부를 반환한다.
	 * @return
	 */
	public boolean isWithKICA() {
		return WithKICA;
	}

	/**
	 * 한국정보인증 TSA URL을 반환한다.
	 * @return
	 */
	public String getKica_url() {
		return Kica_url;
	}

	/**
	 * 한국정보인증 TSA 사용자 ID를 반환한다.
	 * @return
	 */
	public String getKica_UserID() {
		return Kica_UserID;
	}

	/**
	 * 한국정보인증 TSA 사용자 암호를 반환한다.
	 * @return
	 */
	public String getKica_UserPwd() {
		return Kica_UserPwd;
	}

	/**
	 * 한국정보인증 TSA Message Digest 알고리즘을 반환한다.
	 * @return
	 */
	public String getKica_MDAlg() {
		return Kica_MDAlg;
	}

	/**
	 * TSA URL을 반환한다.
	 * @return
	 */
	public String getTsa_url() {
		return Tsa_url;
	}

	/**
	 * TSA 사용자 ID를 반환한다.
	 * @return
	 */
	public String getTsa_Account() {
		return Tsa_Account;
	}

	/**
	 * TSA 사용자 암호를 반환한다.
	 * @return
	 */
	public String getTsa_Pwd() {
		return Tsa_Pwd;
	}

	/**
	 * TSA에서 OCSP를 사용할지를 반환한다.
	 * @return
	 */
	public boolean isWithOCSP() {
		return WithOCSP;
	}

	/**
	 * PDFA 문서 종류를 반환한다.
	 * PDFA Type: 0=PDFNONE, 1=PDFX1A2001, 2=PDFX32002, 3=PDFA1A, 4=PDFA1B
	 * @return
	 */
	public String getPDFA() {
		return PDFA;
	}

	
	/**
	 * PDF Form에서 사용할 폰트 경로
	 * @return
	 */
	public String getFontPath(){
		return FontPath;
	}

	/**
	 * WEB-INF 경로를 반환한다.
	 * @return
	 */
	public String getWebInf() {
		return web_inf == null ? "" : web_inf;
	}

	/**
	 * Context Path를 반환한다.
	 * @return
	 */
	public String getContextpath() {
		return contextpath;
	}

	/**
	 * 전자서명된 PDF에 사용될 워터마크 파일 이름을 반환한다.
	 * @return
	 */
	public String getSignedPDFWaterMark() {
		return SignedPDFWaterMark;
	}

	/**
	 * 전자서명된 PDF에 사용될 워터마크 파일 이름을 설정한다.
	 * @param signedPDFWaterMark
	 */
	public void setSignedPDFWaterMark(String signedPDFWaterMark) {
		SignedPDFWaterMark = signedPDFWaterMark;
	}

	/**
	 * 보고서에서 사용될 이미지 경로를 반환한다.
	 * @return
	 */
	public String getImagepath() {
		return imagepath;
	}

	/**
	 * 보고서에서 사용될 이미지 경로를 설정한다.
	 * @param imagepath
	 */
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	/**
	 * PDF 버전을 반환한다.
	 * 2 (Acrobat 3.x)   3 (Acrobat 4.x)   4 (Acrobat 5.x)   5 (Acrobat 6.x)   6 (Acrobat 7.x)   7 (Acrobat 8.x)
	 * @return
	 */
	public String getPdfVersion() {
		return pdfversion;
	}

	/**
	 * PDF 번전을 설정한다.
	 * 2 (Acrobat 3.x)   3 (Acrobat 4.x)   4 (Acrobat 5.x)   5 (Acrobat 6.x)   6 (Acrobat 7.x)   7 (Acrobat 8.x)
	 * @param pdfversion
	 */
	public void setPdfVersion(String pdfversion) {
		this.pdfversion = pdfversion;
	}

	/**
	 * PDF 파일 저장여부를 반환한다.
	 * @return
	 */
	public boolean isSavepdffile() {
		return savepdffile;
	}

	/**
	 * PDF 파일 저장여부를 설정한다.
	 * @param savepdffile
	 */
	public void setSavepdffile(boolean savepdffile) {
		this.savepdffile = savepdffile;
	}
	
	private String hdBarcodePath(){
		//barcodePath
		String fsp = SystemUtils.FILE_SEPARATOR;
		String p = barcodePath;
		p = p.endsWith(fsp) ? p : p + fsp;
		GregorianCalendar cal = new GregorianCalendar(); 
		p = p + DateCalc(cal.getTime());
		File f = new File(p);
		if(!f.exists()){
			f.mkdir();
		}
		p = p.endsWith(fsp) ? p : p +fsp;
		managerBarcodePath(cal);
		return p;
	}
	
	private void managerBarcodePath(GregorianCalendar cal){
		String d1 = DateCalc(cal.getTime());
		cal.add(GregorianCalendar.DATE,-1);
		String d2 =  DateCalc(cal.getTime());

		File f = new File(barcodePath);
		File[] filesAndDirs = f.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
        for (File file : filesDirs) {
            if(file.isDirectory()){
            	if(!file.getName().equalsIgnoreCase(d1) && !file.getName().equalsIgnoreCase(d2)){
                    deleteDirectory(file);
                }
            }
        }
	}
	
    public boolean deleteDirectory(File path) {
        if(!path.exists()) {
            return false;
        }
         
        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
         
        return path.delete();
    }
    
    private String DateCalc(Date d){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
        return formatter.format(d);
    }
    
	public String getErrorCode(){
		return CabsoftPath + "errorcode.xml";
	}

	public void setSignCertPwd(String signCertPwd) {
		this.signCertPwd = signCertPwd;
	}

	public void setPFX(boolean pFX) {
		PFX = pFX;
	}

	public void setSignCertFile(String signCertFile) {
		this.signCertFile = signCertFile;
	}
	
    public int getPdfEncryptAlg() throws Exception{
        String alg = getProperty("com.cabsoft.pdf.encrypt.algorithm", "AES128");
        if ("RC440".equals(alg)) {
            return PdfWriter.STANDARD_ENCRYPTION_40;
        } else if ("RC4128".equals(alg)) {
            return PdfWriter.STANDARD_ENCRYPTION_128;
        } else if ("AES128".equals(alg)) {
            return PdfWriter.ENCRYPTION_AES_128;
        } else if ("AES256".equals(alg)) {
            return PdfWriter.ENCRYPTION_AES_256;
        }else{
            throw new Exception(alg + "는(은) 지원하는 않는 암호화 알고리즘입니다.");
        }
    }
    
    public int getPdfPermision() {
        int permision = 0;
        if (getBooleanProperty("com.cabsoft.pdf.allow.print", true)) {
            permision += PdfWriter.ALLOW_PRINTING;
        }

        if (getBooleanProperty("com.cabsoft.pdf.allow.assembly", false)) {
            permision += PdfWriter.ALLOW_ASSEMBLY;
        }

        if (getBooleanProperty("com.cabsoft.pdf.allow.copy", false)) {
            permision += PdfWriter.ALLOW_COPY;
        }

        if (getBooleanProperty("com.cabsoft.pdf.allow.fillin", false)) {
            permision += PdfWriter.ALLOW_FILL_IN;
        }

        if (getBooleanProperty("com.cabsoft.pdf.allow.modifyannotations", false)) {
            permision += PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
        }

        if (getBooleanProperty("com.cabsoft.pdf.allow.modifycontents", false)) {
            permision += PdfWriter.ALLOW_MODIFY_CONTENTS;
        }
        if (getBooleanProperty("com.cabsoft.rx.pdf.allow.screenreaders", false)) {
            permision += PdfWriter.ALLOW_SCREENREADERS;
        }
        return permision;
    }
	
	/**
	 * 운영체제에 따라 페이지 높이를 재설정하기 위한 비율 반환
	 * @param userAgent
	 * @return
	 */
	
	/*
	 * <p class=\"breakhere\" /> 대신에
	 * <!-- MarkAny Page Gubun -->
	 * 이 태그를 사용해서 발생함.
	 */
/*
	public float getPageHeightZoom(String userAgent){
		float zoom = 1.0f;
		
		String os = sysinfo.os(userAgent);
		if(os.equalsIgnoreCase("mac")){
			String z = getProperty("com.cabsoft.page.height.mac");
			String s[] = StringUtils.split(z, "/");
			if(s.length==2){
				zoom = asFloat(s[0])/asFloat(s[1]);
			}
		}else if(os.equalsIgnoreCase("linux")){
			String z = getProperty("com.cabsoft.page.height.linux");
			String s[] = StringUtils.split(z, "/");
			if(s.length==2){
				zoom = asFloat(s[0])/asFloat(s[1]);
			}
		}else if(os.equalsIgnoreCase("ms")){
			String z = getProperty("com.cabsoft.page.height.ms");
			String s[] = StringUtils.split(z, "/");
			if(s.length==2){
				zoom = asFloat(s[0])/asFloat(s[1]);
			}
			zoom = 1.0f;
		}else{
			zoom = 1.0f;
		}
		zoom = 1.0f;
		return zoom;
	}
*/
	
}
