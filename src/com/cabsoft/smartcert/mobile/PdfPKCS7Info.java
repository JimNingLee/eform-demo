package com.cabsoft.smartcert.mobile;

import com.cabsoft.org.spongycastle.asn1.x500.RDN;
import com.cabsoft.org.spongycastle.asn1.x500.X500Name;
import com.cabsoft.org.spongycastle.asn1.x500.style.BCStyle;
import com.cabsoft.org.spongycastle.cert.X509CertificateHolder;
import com.cabsoft.org.spongycastle.jce.PrincipalUtil;
import com.cabsoft.org.spongycastle.tsp.TimeStampToken;
import com.cabsoft.org.spongycastle.util.Store;
import com.cabsoft.pdfutils.Sign.Interface.ICertInfo;
import com.cabsoft.utils.CertUtil;
import com.cabsoft.utils.DateTime;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PdfPKCS7Info implements Serializable {
	private static final long serialVersionUID = 20130416L;

	private transient ICertInfo certInfo = null;
	private transient TimeStampToken token = null;

	private String signDate = "";
	private String infoString = "";

	public PdfPKCS7Info(ICertInfo certInfo, TimeStampToken token) {
		this.certInfo = certInfo;
		this.token = token;

		signDate = certInfo.getReturnValue("SignDate");
		infoString = getSignCertInfo();
	}
	
	public PdfPKCS7Info(ICertInfo certInfo, TimeStampToken token, X509Certificate cert) {
		this.certInfo = certInfo;
		this.token = token;

		signDate = certInfo.getReturnValue("SignDate");
		infoString = getSignCertInfo(cert);
	}

	public String getSignDate() {
		return signDate;
	}

	public String getInfoString() {
		return infoString;
	}

	private String getSignCertInfo(X509Certificate cert) {
		StringBuffer sb = new StringBuffer();
		CertUtil certutil = new CertUtil();
		if(cert!=null){
			certutil.setX509Certificate(cert);
		}else{
			
		}

		sb.append("※ 전자서명 정보\n\n");
		sb.append("- 인증서 발급자: ").append(certutil.getIssuerDN()).append("\n");
		sb.append("- 인증서 발급 대상: ").append(certutil.getSubjectDN()).append("\n");
		sb.append("- 서명 알고리즘: ").append(certutil.getSigAlgName()).append("\n");
		try {
			sb.append("- 키 사용: ").append(certutil.getKeyUsage(",")).append("\n");
		} catch (Exception e) {
		}
		sb.append("- 인증서 유효기간: ").append(certutil.getNotBefore()).append(" ~ ").append(certutil.getNotAfter()).append("\n");
		sb.append("- 인증서 유효성: ").append((certutil.getValidity() == true ? "유효함" : "유효하지 않음")).append("\n");
		if (token != null) {
			sb.append("- 타임스탬프 적용: 적용됨\n");

			sb.append("\n※ 타임스탬프 정보").append("\n\n");
			sb.append("- 타임스탬프 시각: ").append(getTimeStampTime()).append("\n");
			sb.append("- 타임스탬프 기관: ").append(getTimeStampOrgan()).append("\n");
			X509Certificate tsacert = getTsaCertificates();
			if (tsacert != null) {
				sb.append("- 인증서 발급자: ").append(tsacert.getIssuerDN()).append("\n");
				sb.append("- 인증서 발급대상: ").append(tsacert.getSubjectDN()).append("\n");
				sb.append("- 인증서 유효기간: ").append(DateTime.getDate(tsacert.getNotBefore(), "yyyy-MM-dd HH:mm:ss")).append(" ~ ").append(DateTime.getDate(tsacert.getNotAfter(), "yyyy-MM-dd HH:mm:ss"))
						.append("\n");
			}
		} else {
			sb.append("- 타임스탬프 적용: 적용되지 않음\n");
		}

		return sb.toString();
	}
	
	private String getSignCertInfo() {
		return getSignCertInfo(certInfo.getCertificate());
	}

	private String getTimeStampOrgan() {
		String ret = "";

		try {
			X509Certificate cert = getTsaCertificates();

			X500Name x500name = X500Name.getInstance(PrincipalUtil.getSubjectX509Principal(cert));
			RDN[] cn = x500name.getRDNs(BCStyle.CN);
			if (cn != null && cn.length > 0)
				ret = cn[0].getFirst().getValue().toString();

			RDN[] email = x500name.getRDNs(BCStyle.EmailAddress);
			if (email != null && email.length > 0)
				ret += " <" + email[0].getFirst().getValue().toString() + ">";
		} catch (Exception e) {

		}
		return ret;
	}

	private String getTimeStampTime() {
		if (token == null) {
			return null;
		} else {
			return DateTime.getDate(token.getTimeStampInfo().getGenTime());
		}
	}

	private Certificate getCertificateFromFile(final InputStream inputStream) throws CertificateException {
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		Certificate certificate = certFactory.generateCertificate(inputStream);
		return certificate;
	}

	private Certificate getCertificateFromFile(final byte[] bytes) throws CertificateException {
		InputStream inputStream = new ByteArrayInputStream(bytes);
		return getCertificateFromFile(inputStream);
	}

	@SuppressWarnings("rawtypes")
	private X509Certificate getTsaCertificates() {
		X509Certificate cert = null;
		List<Certificate> certificates = new ArrayList<Certificate>();
		try {
			Store certificatesStore = token.getCertificates();
			Collection certificatesCollection = certificatesStore.getMatches(null);
			for (Object obj : certificatesCollection) {
				if (obj instanceof X509CertificateHolder) {
					X509CertificateHolder holder = (X509CertificateHolder) obj;
					byte[] encoded = holder.getEncoded();
					Certificate certificate = getCertificateFromFile(encoded);
					certificates.add(certificate);
				}
			}
			if (certificates.size() > 0) {
				cert = (X509Certificate) certificates.get(0);
			}
		} catch (Exception e) {

		}
		return cert;
	}
}
