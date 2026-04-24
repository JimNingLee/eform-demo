package com.cabsoft.smartcert.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.cabsoft.GlobalParams;
import com.cabsoft.sign.service.PdfSignService;
import com.cabsoft.sign.tsa.service.KoscomTSAService;
import com.cabsoft.sign.tsa.service.YessignTSAService;
import com.cabsoft.smartcert.mobile.PdfPKCS7Info;

public class PdfSignTsa {
	public PdfSignTsa() {

	}

	public PdfPKCS7Info signPdf(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd) throws Exception {
		GlobalParams globalParams = GlobalParams.getInstance();
		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		PdfPKCS7Info info = null;
		info = PdfSignService.signPdfSelf(out, o, webinf, pwd);
		return info;
	}

	public PdfPKCS7Info signPdfWithTsaKoscom(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd) throws Exception {
		return KoscomTSAService.signPdfWithTSAOCSP(out, o, webinf, pwd);
	}

	public PdfPKCS7Info signPdfWithTsaYessign(OutputStream out, ByteArrayOutputStream o, String webinf, String pwd) throws Exception {
		return YessignTSAService.signPdfWithTSAOCSP(out, o, webinf, pwd);
	}

}
