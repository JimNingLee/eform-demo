package com.cabsoft;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.pdfutils.Sign.SignCertUtil;

@SuppressWarnings("unused")
public class SignCertFactory {
	protected static Object lock = new Object();

	private static Log log = LogFactory.getLog(SignCertFactory.class);

	private static long certdate = 0L;
	
	private static SignCertFactory instance = null;

	private SignCertUtil pdfSignCert = null;
	private SignCertUtil tsaSignCert = null;
	private SignCertUtil tsaKmCert = null;

	private SignCertFactory() {
		pdfSignCert = null;
		tsaSignCert = null;
		tsaKmCert = null;
	}

	/**
	 * SignCertFactory 인스턴스 생성
	 * 
	 * @return
	 */
	public static SignCertFactory getInstance() {
		if (instance ==  null) {
			log.debug("===============> Create new SignCertFactory inatance");
			instance = new SignCertFactory();
		} else {
			log.debug("===============> SignCertFactory inatance already created.");
		}

		return instance;
	}

	/**
	 * PDF 전자서명용 인증서
	 * 
	 * @return
	 */
	public SignCertUtil getPdfSignCert() {
		return pdfSignCert;
	}

	/**
	 * PDF 전자서명용 인증서
	 * 
	 * @param pdfSignCert
	 */
	public void setPdfSignCert(SignCertUtil pdfSignCert) {
		synchronized (lock) {
			this.pdfSignCert = pdfSignCert;
		}
	}

	/**
	 * TSA 서명용 인증서
	 * 
	 * @return
	 */
	public SignCertUtil getTsaSignCert() {
		return tsaSignCert;
	}

	/**
	 * TSA 서명용 인증서
	 * 
	 * @param tsaSignCert
	 */
	public void setTsaSignCert(SignCertUtil tsaSignCert) {
		synchronized (lock) {
			this.tsaSignCert = tsaSignCert;
		}
	}

	/**
	 * TSA 암복호화용 인증서
	 * 
	 * @return
	 */
	public SignCertUtil getTsaKmCert() {
		return tsaKmCert;
	}

	/**
	 * TSA 암복호화용 인증서
	 * 
	 * @param tsaKmCert
	 */
	public void setTsaKmCert(SignCertUtil tsaKmCert) {
		synchronized (lock) {
			this.tsaKmCert = tsaKmCert;
		}
	}
}
