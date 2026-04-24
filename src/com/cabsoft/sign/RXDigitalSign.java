package com.cabsoft.sign;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import com.cabsoft.org.spongycastle.cms.CMSProcessableByteArray;
import com.cabsoft.org.spongycastle.cms.CMSSignedData;
import com.cabsoft.org.spongycastle.cms.CMSSignedDataGenerator;
import com.cabsoft.org.spongycastle.cms.SignerInformation;
import com.cabsoft.org.spongycastle.cms.SignerInformationStore;
import com.cabsoft.org.spongycastle.jce.provider.SpongyCastleProvider;
import com.cabsoft.org.spongycastle.asn1.cms.Attribute;
import com.cabsoft.org.spongycastle.asn1.cms.AttributeTable;
import com.cabsoft.org.spongycastle.asn1.cms.CMSAttributes;
import com.cabsoft.org.spongycastle.asn1.cms.Time;
import com.cabsoft.pdf.form.crypt.Hex;
import com.cabsoft.rxe.LString;
import com.cabsoft.rxe.TrueTypeFont;
import com.cabsoft.utils.Base64Util;
import com.cabsoft.utils.Compress;
import com.cabsoft.utils.Files;

@SuppressWarnings({"deprecation", "rawtypes"})
public class RXDigitalSign {
	CMSSignedData signedData;
	private X509Certificate cert;
	SignerInformation signerInformation;
	private CMSProcessableByteArray signedContent;
	private byte[] content;
	private byte[] contentDigest;
	private Time signingTime;

	/**
	 * 
	 * @param cert
	 * @param privatekey
	 * @param org
	 * @throws Exception
	 */
	public void sign(X509Certificate cert, PrivateKey privatekey, byte[] org) throws Exception {
		this.cert = cert;
		List<X509Certificate> certs = new ArrayList<X509Certificate>();
		certs.add(cert);

		CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
		sgen.addSigner(privatekey, cert, CMSSignedDataGenerator.DIGEST_SHA1);

		sgen.addCertificatesAndCRLs(CertStore.getInstance("Collection",
				new CollectionCertStoreParameters(certs), "SC"));

		signedData = sgen.generate(new CMSProcessableByteArray(org), true, "SC");
	}
	
	/**
	 * 
	 * @return
	 */
	public CMSSignedData getSignedData(){
		return signedData;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] getEncodedSignedData() throws Exception{
		return signedData.getEncoded();
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getBase64SignedData() throws Exception{
		return new String(Base64Util.encode(getEncodedSignedData()));
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCompressedSignedData()  throws Exception{
		return Compress.ZipBase64(getEncodedSignedData());
	}
	
	/**
	 * 
	 * @param signedData
	 * @return
	 * @throws Exception
	 */
	public boolean verify(CMSSignedData signedData) throws Exception {
		this.signedData = signedData;
		boolean verified = false;
		
		CertStore store_cert = signedData.getCertificatesAndCRLs("Collection", "SC");

		SignerInformationStore signers = signedData.getSignerInfos();

		for (Iterator i = signers.getSigners().iterator(); i.hasNext();) {
			SignerInformation signer = (SignerInformation) i.next();
			Collection certCollection = store_cert.getCertificates(null);
			if (!certCollection.isEmpty()) {
				X509Certificate cert = (X509Certificate) certCollection.iterator().next();
				if (signer.verify(cert.getPublicKey(), "SC")) {
					AttributeTable attr = signer.getSignedAttributes();
					if (attr != null){
						Attribute t = attr.get(CMSAttributes.signingTime);
						this.signingTime = Time.getInstance(t.getAttrValues().getObjectAt(0).toASN1Primitive());
					}
					this.cert = cert;
                    this.contentDigest = signer.getContentDigest();
					this.signerInformation = signer;
					verified = true;
					break;
				}
			}
		}

		signedContent = (CMSProcessableByteArray) signedData.getSignedContent();

		ByteArrayInputStream bais = (ByteArrayInputStream) signedContent.getInputStream();
		content = new byte[bais.available()];
		bais.read(content);
		bais.close();

		return verified;
	}
	
        /**
         * 
         * @param sd
         * @return
         * @throws Exception 
         */
        public boolean verify(byte[] sd) throws Exception {
		CMSSignedData s = new CMSSignedData(sd);
		return verify(s);
        }
	/**
	 * 
	 * @param sd
	 * @return
	 * @throws Exception
	 */
	public boolean verifyCompressedSignedData(String sd) throws Exception {
		byte[] ssd = Compress.UnzipBase64ToBytes(sd);
		
		CMSSignedData s = new CMSSignedData(ssd);
		
		return verify(s);
	}
	
	/**
	 * 
	 * @param sd
	 * @return
	 * @throws Exception
	 */
	public boolean verifyBase64SignedData(String sd) throws Exception {
		byte[] ssd = Base64Util.decode(sd.getBytes());
		
		CMSSignedData s = new CMSSignedData(ssd);
		
		return verify(s);
	}

	/**
	 * 
	 * @return
	 */
	public X509Certificate getCertificate() {
		return cert;
	}
        
	public List<X509Certificate> getCertificateList() {
            List<X509Certificate> list = new ArrayList<X509Certificate>();
            list.add(cert);
		return list;
	}

	/**
	 * 
	 * @return
	 */
	public SignerInformation getSignerInformation() {
		return signerInformation;
	}

	/**
	 * 
	 * @return
	 */
	public CMSProcessableByteArray getSignedContent() {
		return signedContent;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getContent() {
		return content;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDecodedContent() {
		return URLDecoder.decode(new String(content));
	}

    public byte[] getContentDigest() {
        return contentDigest;
    }

    public List<byte[]> getContentDigestList() {
        List<byte[]> list = new ArrayList<byte[]>();
        list.add(contentDigest);
        return list;
    }

	public Time getSigningTime() {
		return signingTime;
	}
	
	public String getSigningDateTime(){
        Date date = signingTime.getDate();
        TimeZone tz = TimeZone.getTimeZone("GMT+9");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", java.util.Locale.KOREA);
        sdf.setTimeZone(tz);

        return sdf.format(date);
	}
	
	public String getIssuerDN(){
		return cert.getIssuerDN().getName();
	}
	
	public String getIssuer(String name) throws Exception{
		String dn = getIssuerDN();
		String ret = "";
		LdapName ln = new LdapName(dn);
		for(Rdn rdn : ln.getRdns()){
            if (rdn.getType().equalsIgnoreCase("CN")) {
            	ret = (String) rdn.getValue();
            	break;
            }
		}
		return ret;
	}
	
	public String getSubjectDN(){
		return cert.getSubjectDN().getName();
	}
	
	public String getSubject(String name) throws Exception {
		String dn = getSubjectDN();
		String ret = "";
		LdapName ln = new LdapName(dn);
		for(Rdn rdn : ln.getRdns()){
            if (rdn.getType().equalsIgnoreCase("CN")) {
            	ret = (String) rdn.getValue();
            	break;
            }
		}
		return ret;
	}
	
	public String getSubjectName() throws Exception {
		String cn = getSubject("CN");
		String ret = "";
		if(cn.indexOf("(")>0){
			ret = cn.substring(0, cn.indexOf("("));
		}else{
			ret = cn;
		}
		return ret;
		
	}
	
	public String  getCertFingerPrint() throws Exception{
		byte[] der = cert.getEncoded();
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(der);
		byte[] digest = md.digest();
		return Hex.dumpHex(digest).toUpperCase();
	}
	
	public void setCertificate(X509Certificate cert) {
		this.cert = cert;
	}
    
    public static void main(String[] args) throws Exception {
        Security.addProvider(new SpongyCastleProvider());
        
        String d = "MIAGCSqGSIb3DQEHAqCAMIACAQExCzAJBgUrDgMCGgUAMIAGCSqGSIb3DQEHAaCAJIAEggPoJTNDYnIl"
                + "MkYlM0UlM0NjZW50ZXIlM0UlMEElM0N0YWJsZStjZWxsc3BhY2luZyUzRCUyNzAlMjcrY2VsbHBhZGRp"
                + "bmclM0QlMjcwJTI3K2JvcmRlciUzRCUyNzAlMjcrd2lkdGglM0QlMjcxMDAlMjUlMjcrc3R5bGUlM0Ql"
                + "Mjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUz"
                + "QSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0UlMEElM0N0cit3aWR0aCUzRCUyNzEwMCUyNSUyNyUzRSUw"
                + "QSUzQ3RkK3N0eWxlJTNEJTIycGFkZGluZyUzQTIlM0Jmb250LXNpemUlM0ExMnB4JTNCK2ZvbnQtZmFt"
                + "aWx5JTNBJTIyJUVCJTgyJTk4JUVCJTg4JTk0JUVBJUIzJUEwJUVCJTk0JTk1JUVDJUJEJTk0JUVCJTk0"
                + "JUE5JTJDc2VvdWwlMkN2ZXJkYW5hJTIyJTNCK2NvbG9yJTNBJTIzNzM3MzczJTNCK2xpbmUtaGVpZ2h0"
                + "JTNBMTRweCUzQiUyMiUzRSUwQSUzQ3RhYmxlK2NlbGxzcGFjaW5nJTNEJTI3MCUyNytjZWxscGFkZGlu"
                + "ZyUzRCUyNzAlMjcrYm9yZGVyJTNEJTI3MCUyNyt3aWR0aCUzRCUyNzEwMCUyNSUyNyUzRSUwQSUzQ3Ry"
                + "JTNFJTBBJTNDdGQrc3R5bGUlM0QlMjJmb250LXNpemUlM0ExMnB4JTNCK2ZvbnQtZmFtaWx5JTNBJTIy"
                + "JUVCJTgyJTk4JUVCJTg4JTk0JUVBJUIzJUEwJUVCJTk0JTk1JUVDJUJEJTk0JUVCJTk0JUE5JTJDc2Vv"
                + "dWwlMkN2ZXJkYW5hJTIyJTNCK2NvbG9yJTNBJTIzNzM3MzczJTNCK2xpbmUtaGVpZ2h0JTNBMTRweCUz"
                + "QiUyMiUzRSUzQ2JyJTJGJTNFJTNDJTJGdGQlM0UlMEElM0MlMkZ0ciUzRSUwQSUzQ3RyJTNFJTBBJTND"
                + "dGQrYWxpZ24lM0QlMjJsZWZ0JTIyK3N0eWxlJTNEJTIyZm9udC1zaXplJTNBMTJweCUzQitmb250LWZh"
                + "bWlseSUzQSUyMiVFQiU4MiU5OCVFQiU4OCU5NCVFQSVCMyVBMCVFQiU5NCU5NSVFQyVCRCU5NCVFQiU5"
                + "NCVBOSUyQ3Nlb3VsJTJDdmVyZGFuYSUyMiUzQitjb2xvciUzQSUyMzczNzM3MyUzQitsaW5lLWhlaWdo"
                + "dCUzQTE0cHglM0IlMjIlM0UlMjZuYnNwJTNCJTI2bmJzcASCA+glM0IlMjZuYnNwJTNCJTNDc3Bhbitz"
                + "dHlsZSUzRCUyMmNvbG9yJTNBJTIzMDAwMDAwJTNCK2ZvbnQtc2l6ZSUzQTE0JTNCK2ZvbnQtd2VpZ2h0"
                + "JTNBYm9sZCUzQiUyMiUzRSVFRCU5OSU4RCVFQSVCOCVCOCVFQiU4RiU5OSUzQyUyRnNwYW4lM0UrJUVC"
                + "JThCJTk4JUVDJTlEJTk4KyUzQ3NwYW4rc3R5bGUlM0QlMjJjb2xvciUzQSUyMzAwMDAwMCUzQitmb250"
                + "LXNpemUlM0ExNCUzQitmb250LXdlaWdodCUzQWJvbGQlM0IlMjIlM0VBMyUzQyUyRnNwYW4lM0UlRUMl"
                + "OTclOTArJUVCJThDJTgwJUVEJTk1JTlDKyVFQyVBMCU4NCVFQyU5RSU5MCVFQyU4NCU5QyVFQiVBQSU4"
                + "NSslRUMlQTAlOTUlRUIlQjMlQjQlRUMlOUUlODUlRUIlOEIlODglRUIlOEIlQTQuJTNDYnIlMkYlM0Ul"
                + "M0NiciUyRiUzRSUzQ2NlbnRlciUzRSUzQ3RhYmxlK2NlbGxzcGFjaW5nJTNEJTI3MCUyNytjZWxscGFk"
                + "ZGluZyUzRCUyNzAlMjcrd2lkdGglM0QlMjcxMDAlMjUlMjcrc3R5bGUlM0QlMjdCT1JERVItQ09MTEFQ"
                + "U0UlM0ErY29sbGFwc2UlM0Jmb250LXNpemUlM0ExMnB4JTNCK2ZvbnQtZmFtaWx5JTNBJTIyJUVCJTgy"
                + "JTk4JUVCJTg4JTk0JUVBJUIzJUEwJUVCJTk0JTk1JUVDJUJEJTk0JUVCJTk0JUE5JTJDc2VvdWwlMkN2"
                + "ZXJkYW5hJTIyJTNCK2NvbG9yJTNBJTIzNzM3MzczJTNCK2xpbmUtaGVpZ2h0JTNBMTRweCUzQiUyNyUz"
                + "RSUwQSUzQ3RyK3dpZHRoJTNEJTI3MTAwJTI1JTI3JTNFJTBBJTNDdGQrYWxpZ24lM0QlMjJsZWZ0JTIy"
                + "K3N0eWxlJTNEJTI3Ym9yZGVyJTNBMXB4K3NvbGlkK3JnYiUyODIwNSUyQysyMTQlMkMrMjI3JTI5JTNC"
                + "K3BhZGRpbmclM0ErNXB4KzhweCs1cHgrOHB4JTNCJTI3JTNFJUVDJTlFJTkwJUVEJTk1JTg0KyVFQyU4"
                + "NCU5QyVFQiVBQSU4NSUzQyUyRnRkJTNFJTBBJTNDdGQrYWxpZ24lM0QlMjJsZWZ0JTIyK3N0eWxlJTNE"
                + "JTI3Ym9yZGVyJTNBMXB4K3NvbGlkK3JnYiUyODIwNSUyQysyMTQlMkMrMjI3JTI5JTNCK3BhZGRpbmcl"
                + "M0ErNXB4KzhweCs1cHgrOHB4BIID6CUzQiUyNyUzRSVFQyU5RSU5MCVFRCU5NSU4NCslRUMlODQlOUMl"
                + "RUIlQUElODUlRUIlOTAlQTglM0MlMkZ0ZCUzRSUwQSUzQyUyRnRyJTNFJTBBJTNDdHIrd2lkdGglM0Ql"
                + "MjcxMDAlMjUlMjclM0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUlM0QlMjdib3JkZXIl"
                + "M0ExcHgrc29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4"
                + "KzVweCs4cHglM0IlMjclM0UlRUMlQTMlQkMlRUMlODYlOEMlM0MlMkZ0ZCUzRSUwQSUzQ3RkK2FsaWdu"
                + "JTNEJTIybGVmdCUyMitzdHlsZSUzRCUyN2JvcmRlciUzQTFweCtzb2xpZCtyZ2IlMjgyMDUlMkMrMjE0"
                + "JTJDKzIyNyUyOSUzQitwYWRkaW5nJTNBKzVweCs4cHgrNXB4KzhweCUzQiUyNyUzRSVFQSVCMCU4MCVF"
                + "QiU4MiU5OCVFQiU4QiVBNCVFQiU5RCVCQyUzQyUyRnRkJTNFJTBBJTNDJTJGdHIlM0UlMEElM0N0cit3"
                + "aWR0aCUzRCUyNzEwMCUyNSUyNyUzRSUwQSUzQ3RkK2FsaWduJTNEJTIybGVmdCUyMitzdHlsZSUzRCUy"
                + "N2JvcmRlciUzQTFweCtzb2xpZCtyZ2IlMjgyMDUlMkMrMjE0JTJDKzIyNyUyOSUzQitwYWRkaW5nJTNB"
                + "KzVweCs4cHgrNXB4KzhweCUzQiUyNyUzRSVFQyU4NCVBNCVFQiVBQSU4NSVFRCU5OSU5NSVFQyU5RCVC"
                + "OCslRUIlOEYlOTklRUMlOUQlOTglM0MlMkZ0ZCUzRSUwQSUzQ3RkK2FsaWduJTNEJTIybGVmdCUyMitz"
                + "dHlsZSUzRCUyN2JvcmRlciUzQTFweCtzb2xpZCtyZ2IlMjgyMDUlMkMrMjE0JTJDKzIyNyUyOSUzQitw"
                + "YWRkaW5nJTNBKzVweCs4cHgrNXB4KzhweCUzQiUyNyUzRSVFQiU4MiVCNCVFQyU5QSVBOSVFQyU5RCU4"
                + "NCslRUQlOTklOTUlRUMlOUQlQjgrJUVEJTk1JTk4JUVDJTk4JTgwJUVDJThBJUI1JUVCJThCJTg4JUVC"
                + "JThCJUE0LiUzQyUyRnRkJTNFJTBBJTNDJTJGdHIlM0UlMEElM0N0cit3aWR0aCUzRCUyNzEwMCUyNSUy"
                + "NyUzRSUwQSUzQ3RkK2FsaWduJTNEJTIybGVmdCUyMitzdHlsZSUzRCUyN2JvcmRlciUzQTFweCtzb2xp"
                + "ZCsEggPocmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4"
                + "cHglM0IlMjclM0UlRUIlOEMlODAlRUMlQjYlOUMlMjglRUQlOTUlOUMlRUIlOEYlODQlMjklRUElQjgl"
                + "ODglRUMlOTUlQTElM0MlMkZ0ZCUzRSUwQSUzQ3RkK2FsaWduJTNEJTIybGVmdCUyMitzdHlsZSUzRCUy"
                + "N2JvcmRlciUzQTFweCtzb2xpZCtyZ2IlMjgyMDUlMkMrMjE0JTJDKzIyNyUyOSUzQitwYWRkaW5nJTNB"
                + "KzVweCs4cHgrNXB4KzhweCUzQiUyNyUzRSVFRCU5NSVBRCVFQiVBQSVBOSVFRCU5NSU5QyVFQSVCOCU4"
                + "MCVFQiVBQSU4NSslRUQlODUlOEMlRUMlOEElQTQlRUQlOEElQjglM0MlMkZ0ZCUzRSUwQSUzQyUyRnRy"
                + "JTNFJTBBJTNDdHIrd2lkdGglM0QlMjcxMDAlMjUlMjclM0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQl"
                + "MjIrc3R5bGUlM0QlMjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjkl"
                + "M0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0UlRUMlODQlQTQlRUIlQUElODUlRUMl"
                + "OUQlODQrJUVCJTkzJUEzJUVBJUIzJUEwJTNDJTJGdGQlM0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQl"
                + "MjIrc3R5bGUlM0QlMjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjkl"
                + "M0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0UlRUIlODIlQjQlRUMlOUElQTklRUMl"
                + "OUQlODQrJUVEJTk5JTk1JUVDJTlEJUI4KyVFRCU5NSU5OCVFQyU5OCU4MCVFQyU4QSVCNSVFQiU4QiU4"
                + "OCVFQiU4QiVBNC4lM0MlMkZ0ZCUzRSUwQSUzQyUyRnRyJTNFJTBBJTNDdHIrd2lkdGglM0QlMjcxMDAl"
                + "MjUlMjclM0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUlM0QlMjdib3JkZXIlM0ExcHgr"
                + "c29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4"
                + "cHglM0IlMjclM0UlRUQlOTklOTUlRUMlOTUlQkQlRUQlOTUlQTglM0MlMkZ0ZASCA+glM0UlMEElM0N0"
                + "ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUlM0QlMjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4MjA1"
                + "JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0UlRUIl"
                + "ODIlQjQlRUMlOUElQTklRUMlOUQlODQrJUVEJTk5JTk1JUVDJTlEJUI4KyVFRCU5NSU5OCVFQyU5OCU4"
                + "MCVFQyU4QSVCNSVFQiU4QiU4OCVFQiU4QiVBNC4lM0MlMkZ0ZCUzRSUwQSUzQyUyRnRyJTNFJTBBJTND"
                + "dHIrd2lkdGglM0QlMjcxMDAlMjUlMjclM0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUl"
                + "M0QlMjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGlu"
                + "ZyUzQSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0VTTVMrJUVEJTg2JUI1JUVDJUE3JTgwJUVDJTg0JTlD"
                + "JUVCJUI5JTg0JUVDJThBJUE0JTNDJTJGdGQlM0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5"
                + "bGUlM0QlMjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFk"
                + "ZGluZyUzQSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0UlRUMlODQlQTAlRUQlODMlOUQlRUQlOTUlQTgl"
                + "M0MlMkZ0ZCUzRSUwQSUzQyUyRnRyJTNFJTBBJTNDdHIrd2lkdGglM0QlMjcxMDAlMjUlMjclM0UlMEEl"
                + "M0N0ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUlM0QlMjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4"
                + "MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0VF"
                + "LW1haWwrJUVEJTg2JUI1JUVDJUE3JTgwJUVDJTg0JTlDJUVCJUI5JTg0JUVDJThBJUE0JTNDJTJGdGQl"
                + "M0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUlM0QlMjdib3JkZXIlM0ExcHgrc29saWQr"
                + "cmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4cHglM0Il"
                + "MjclM0UlRUMlODQlQTAlRUQlODMlOUQlRUMlOTUlBIID6Dg4JUVEJTk1JUE4JTNDJTJGdGQlM0UlMEEl"
                + "M0MlMkZ0ciUzRSUwQSUzQ3RyK3dpZHRoJTNEJTI3MTAwJTI1JTI3JTNFJTBBJTNDdGQrYWxpZ24lM0Ql"
                + "MjJsZWZ0JTIyK3N0eWxlJTNEJTI3Ym9yZGVyJTNBMXB4K3NvbGlkK3JnYiUyODIwNSUyQysyMTQlMkMr"
                + "MjI3JTI5JTNCK3BhZGRpbmclM0ErNXB4KzhweCs1cHgrOHB4JTNCJTI3JTNFJUVBJUI4JUIwJUVEJTgz"
                + "JTgwJTNDJTJGdGQlM0UlMEElM0N0ZCthbGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUlM0QlMjdib3JkZXIl"
                + "M0ExcHgrc29saWQrcmdiJTI4MjA1JTJDKzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4"
                + "KzVweCs4cHglM0IlMjclM0UlRUMlODQlQTAlRUQlODMlOUQlRUMlOTUlODglRUQlOTUlQTglM0MlMkZ0"
                + "ZCUzRSUwQSUzQyUyRnRyJTNFJTBBJTNDdHIrd2lkdGglM0QlMjcxMDAlMjUlMjclM0UlMEElM0N0ZCth"
                + "bGlnbiUzRCUyMmxlZnQlMjIrc3R5bGUlM0QlMjdib3JkZXIlM0ExcHgrc29saWQrcmdiJTI4MjA1JTJD"
                + "KzIxNCUyQysyMjclMjklM0IrcGFkZGluZyUzQSs1cHgrOHB4KzVweCs4cHglM0IlMjclM0UlRUMlODQl"
                + "OUMlRUIlQjklODQlRUMlOEElQTQlRUMlOEIlQTAlRUMlQjIlQUQlRUMlOTclQUMlRUIlQjYlODAlM0Ml"
                + "MkZ0ZCUzRSUwQSUzQ3RkK2FsaWduJTNEJTIybGVmdCUyMitzdHlsZSUzRCUyN2JvcmRlciUzQTFweCtz"
                + "b2xpZCtyZ2IlMjgyMDUlMkMrMjE0JTJDKzIyNyUyOSUzQitwYWRkaW5nJTNBKzVweCs4cHgrNXB4Kzhw"
                + "eCUzQiUyNyUzRSVFQiU4RiU5OSVFQyU5RCU5OCVFRCU5NSVBOCUzQyUyRnRkJTNFJTBBJTNDJTJGdHIl"
                + "M0UlMEElM0N0cit3aWR0aCUzRCUyNzEwMCUyNSUyNyUzRSUwQSUzQ3RkK2FsaWduJTNEJTIybGVmdCUy"
                + "MitzdHlsZSUzRCUyN2JvcmRlciUzQTFweCtzb2xpZCtyZ2IlMjgyMDUlMkMrMjE0JTJDKzIyNyUyOSUz"
                + "QitwYWRkaW5nJTNBKzVweCs4cHgrNXB4KzhweCUzQiUyNyUzRSVFQyU4MiVBQyVFQyVBMCU4NCVFQSVC"
                + "MyVBMCVFQyVBNyU4MCUEggFyRUMlOTglODglRUMlQTAlOTUlRUMlOUQlQkMtMTAlRUMlOUQlQkMlRUMl"
                + "QTAlODQlM0MlMkZ0ZCUzRSUwQSUzQ3RkK2FsaWduJTNEJTIybGVmdCUyMitzdHlsZSUzRCUyN2JvcmRl"
                + "ciUzQTFweCtzb2xpZCtyZ2IlMjgyMDUlMkMrMjE0JTJDKzIyNyUyOSUzQitwYWRkaW5nJTNBKzVweCs4"
                + "cHgrNXB4KzhweCUzQiUyNyUzRSVFQiU4RiU5OSVFQyU5RCU5OCVFRCU5NSVBOCUzQyUyRnRkJTNFJTBB"
                + "JTNDJTJGdHIlM0UlMEElM0MlMkZ0YWJsZSUzRSUzQyUyRmNlbnRlciUzRSUzQyUyRnRkJTNFJTBBJTND"
                + "JTJGdHIlM0UlMEElM0MlMkZ0YWJsZSUzRSUwQSUzQyUyRnRkJTNFJTBBJTNDJTJGdHIlM0UlMEElM0Ml"
                + "MkZ0YWJsZSUzRSUwQSUzQyUyRmNlbnRlciUzRQAAAAAAAKCAMIID6jCCAtKgAwIBAgIGAULlW5HaMA0G"
                + "CSqGSIb3DQEBCwUAMIG8MQswCQYDVQQGEwJLUjEqMCgGA1UEAwwhUmVwb3J0RXhwcmVzcyBFbnRlcnBy"
                + "aXNlIGVGb3JtIENBMR8wHQYDVQQLDBZSZXNlYXJjaCAmIERldmVsb3BtZW50MRgwFgYDVQQKDA9DQUJT"
                + "T0ZUV0FSRS5DT00xDjAMBgNVBAcMBVNlb3VsMQ4wDAYDVQQIDAVTZW91bDEmMCQGCSqGSIb3DQEJARYX"
                + "c3VwcG9ydEBjYWJzb2Z0d2FyZS5jb20wHhcNMTMxMjEyMDU0OTQ4WhcNMTcxMjExMDQ0OTM1WjBpMQsw"
                + "CQYDVQQGEwJLUjEOMAwGA1UEAwwFZUZvcm0xDDAKBgNVBAsMA0RldjEUMBIGA1UECgwLQ2Fic29mdHdh"
                + "cmUxJjAkBgkqhkiG9w0BCQEWF3N1cHBvcnRAY2Fic29mdHdhcmUuY29tMIIBIjANBgkqhkiG9w0BAQEF"
                + "AAOCAQ8AMIIBCgKCAQEA0bCrQVLA7Hp0gcA8Ls6886z4o4fKb+fh3VU1/LsRCsg7RyKVJ04f/AVdO1DJ"
                + "YFzlduKTzOUHFhKFfRiPy2yxhlwlRyRqn5b6CiUqYwBj8Kmya/UDFKb6NMiAL8OOSPhmQuK0BXWAJ/S0"
                + "+yD3RprFBRpcVfb4X1m2Fd0vonPu2w7X6RHPw1xZnJ5F0sE0UivZ36SQ9X+bEs1Plcyixx+aHCNKKI0i"
                + "s8YNq4aDT2R8tkd2dZHrW1BKQ1Ewo+e4f6QCaEagNJszEBjceb/wt8OAjT0sWlsLt3IVMHc+srNsuH9s"
                + "6+DJWKuDsaFXK5cXF1sXGpswV3tbz1iFg7wHEdeSNwIDAQABo0QwQjAMBgNVHRMBAf8EAjAAMA4GA1Ud"
                + "DwEB/wQEAwIE8DAiBgNVHREEGzAZgRdzdXBwb3J0QGNhYnNvZnR3YXJlLmNvbTANBgkqhkiG9w0BAQsF"
                + "AAOCAQEAxARVvXqeW2b/1hBUydJ1B0qGiXsNqXJj7OEEa/sO4MIQr19NoRimOVJGDqop4EDmnSxvYwr6"
                + "T4ky+M9M3R4cKw/t7FIIZ2NDRzIcI0lsO2eTtzvMtIsDzPKgHrdMUxRTfMAf9eIDqVFIDLPV+BjvD748"
                + "vzIE3Vt6pEv6offeDF1uqMp/iWdiJg3dU42wCLegYNa0hEQFrodYfTlMb82c+iNqq91q7aHec2PRu5xZ"
                + "MYQhFf2CM4VX7yz5m1YTWKTFAdQrRSjWFtUvj03dOMDYOKXQqSwd+rrZmul/2ut+8bXs1DSD81fNamqC"
                + "Lw9EH7ux7Grl2Uop33zYrSzUR5TlBwAAMYICTjCCAkoCAQEwgccwgbwxCzAJBgNVBAYTAktSMSowKAYD"
                + "VQQDDCFSZXBvcnRFeHByZXNzIEVudGVycHJpc2UgZUZvcm0gQ0ExHzAdBgNVBAsMFlJlc2VhcmNoICYg"
                + "RGV2ZWxvcG1lbnQxGDAWBgNVBAoMD0NBQlNPRlRXQVJFLkNPTTEOMAwGA1UEBwwFU2VvdWwxDjAMBgNV"
                + "BAgMBVNlb3VsMSYwJAYJKoZIhvcNAQkBFhdzdXBwb3J0QGNhYnNvZnR3YXJlLmNvbQIGAULlW5HaMAkG"
                + "BSsOAwIaBQCgXTAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xNTAxMTMw"
                + "ODQ0MTZaMCMGCSqGSIb3DQEJBDEWBBSEQVf+wD8sgAzzLtcBMpaD8vTJSzANBgkqhkiG9w0BAQEFAASC"
                + "AQAoaQ5C6PecANlX/p1Yl/2gThD4CIEBgixCXiPsB2xUDlMiv2rKMeDibO8sQk01m+E3pv7Lq3Yine9v"
                + "QStLm0ywVKzDPiqkouiKFjBuv2VxsDk62X/sToaLJEMB4zvSFOhfYKS8bUCGKpZsrlIBMkNoQyC64wys"
                + "pZbfB2WCH7GVoMO6NhMQTXok0BYl3UYOHBf71gbFy7Q7xU85xe6bvJieJBIDvcPb4Fm18sz+gih8lh/3"
                + "zQOYZPtB4psFG1NZJJA5RhGCN64/36dgU2vB1BJz26w5DgzwGExEd59wWZ/fJiWO5YnkHKDiChCCzqlZ"
                + "MN4Lc5DDBqTliq62jE0PN0V4AAAAAAAA";
        
        RXDigitalSign sign = new RXDigitalSign();
        sign.verifyBase64SignedData(d);
        
        System.out.println(URLDecoder.decode(new String(sign.getContent())));

        System.out.println(sign.getSigningDateTime());
        
        System.out.println(sign.getIssuer("CN"));
        System.out.println(sign.getSubject("CN"));
        
        byte[] b = Files.readFile("d:/eformbank/signCert.der");
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate signcert = (X509Certificate) certFactory.generateCertificate(bais);
        
        //sign.setCertificate(signcert);
        System.out.println(sign.getIssuer("CN"));
        System.out.println(sign.getSubject("CN"));
        System.out.println(sign.getSubjectName());
        
        b = Files.readFile("d:/eformbank/추가서명.png");
        ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(b));
        BufferedImage m = ImageIO.read(stream);

        int h = m.getHeight();
        int w = m.getWidth();
        
        Graphics2D g = m.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        Font font = TrueTypeFont.getFont("d:/eformbank/", "malgun.ttf").deriveFont((float) 36f);
        font = font.deriveFont(Font.BOLD);
        
        g.setColor(Color.BLACK);
        g.setFont(font);
        
        
        g.drawString(sign.getSubjectName(), 120f, 50f);
        
        font = font.deriveFont(Font.PLAIN, 24);
        g.setFont(font);
        g.drawString("발급대상: " + sign.getSubject("CN"), 20f, 100f);
        g.drawString("발급자: " + sign.getIssuer("CN"), 20f, 130f);
        g.drawString("서명일자: " + sign.getSigningDateTime(), 20f, 160f);

        String a = sign.getCertFingerPrint();
        String[] s = LString.split(a, "\n");
        
        font = TrueTypeFont.getFont("d:/eformbank/", "couri.ttf").deriveFont((float) 11f);
//        font = new Font("Courier New", Font.PLAIN, 11);
        g.setFont(font);
        System.out.println(s.length);
        for(int i=0; i<s.length; i++){
        	g.drawString(s[i], 12f, (float)(180 + i*13));
        }
        
        //g.drawString(s[1], 20f, 220f);
        
        g.dispose();
        
        ImageIO.write(m, "png", new FileOutputStream("d:/eformbank/000.png"));
        
    }
        
        
}
