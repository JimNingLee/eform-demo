package com.cabsoft.ess;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TiffHttpClient {
	
	private String server = "";
	private String returnUrl = "";
	private String tiffData = "";
	private String infoData = "";
	private String JobID = "";
	private int pageCount = 0;
	private boolean usePdf = false;
	
	public TiffHttpClient(String JobID, String server, String returnUrl, String tiffData, String infoData, int pageCount){
		this.JobID = JobID;
		this.server = server;
		this.returnUrl = returnUrl;
		this.tiffData = tiffData;
		this.infoData = infoData;
		this.pageCount = pageCount;
		this.usePdf = false;
	}
	
	public TiffHttpClient(String JobID, String server, String returnUrl, String tiffData, String infoData, int pageCount, boolean usePdf){
		this.JobID = JobID;
		this.server = server;
		this.returnUrl = returnUrl;
		this.tiffData = tiffData;
		this.infoData = infoData;
		this.pageCount = pageCount;
		this.usePdf = usePdf;
	}
	
	public String doSend() {
		String ret = "";
		
		HttpClient httpclient = new DefaultHttpClient();

		try{
			if(server.startsWith("https://")){
                TrustManager easyTrustManager = new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                };
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[]{easyTrustManager}, null);

                SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
                //본인 인증 방식 (Self-Signed Certificate)일 경우
                //SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                Scheme sch = new Scheme("https", 443, socketFactory);
                httpclient.getConnectionManager().getSchemeRegistry().register(sch);
			}
			
	        HttpPost httppost = new HttpPost(server);

	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("JobID", JobID));
	        nameValuePairs.add(new BasicNameValuePair("Data", tiffData));
	        nameValuePairs.add(new BasicNameValuePair("JobInfo", infoData));
	        nameValuePairs.add(new BasicNameValuePair("PageCount", String.valueOf(pageCount)));
	        nameValuePairs.add(new BasicNameValuePair("UsePdf", (usePdf==true?"1":"0")));
	        nameValuePairs.add(new BasicNameValuePair("ReturnUrl", returnUrl));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        
	        HttpResponse response = httpclient.execute(httppost);
	        
	        HttpEntity resEntity = response.getEntity();
	
	        if (resEntity != null) {
	        	ret = EntityUtils.toString(response.getEntity());
	        }
	        EntityUtils.consume(resEntity);
		}catch(Exception e){
			ret = e.toString();
        } finally {
            try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
        }
		return ret;
	}
}
