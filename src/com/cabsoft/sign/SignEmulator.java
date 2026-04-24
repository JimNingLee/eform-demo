package com.cabsoft.sign;

import com.cabsoft.org.spongycastle.cert.jcajce.JcaCertStore;
import com.cabsoft.org.spongycastle.cms.CMSProcessable;
import com.cabsoft.org.spongycastle.cms.CMSProcessableByteArray;
import com.cabsoft.org.spongycastle.cms.CMSSignedData;
import com.cabsoft.org.spongycastle.cms.CMSSignedDataGenerator;
import com.cabsoft.org.spongycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import com.cabsoft.org.spongycastle.operator.ContentSigner;
import com.cabsoft.org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import com.cabsoft.org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import com.cabsoft.org.spongycastle.util.Store;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class SignEmulator {

	public static byte[] sign1(X509Certificate cert, PrivateKey privatekey, byte[] org) throws Exception {
        Signature signature = Signature.getInstance("SHA1WithRSA", "SC");
        signature.initSign(privatekey);
        signature.update(org);
        
        //Build CMS
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        CMSProcessable content = new CMSProcessableByteArray(org);
        certList.add(cert);
        Store certs = new JcaCertStore(certList);
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("SC").build(privatekey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("SC").build()).build(sha1Signer, cert));
        gen.addCertificates(certs);
        CMSSignedData sigData = gen.generate(content, true, "SC");
        
        return sigData.getEncoded();
	}
	
	public static byte[] sign(X509Certificate cert, PrivateKey privatekey, byte[] org) throws Exception {
		RXDigitalSign digitalSign = new RXDigitalSign();
		digitalSign.sign(cert, privatekey, org);
		return digitalSign.getEncodedSignedData();
	}

}
