package com.cabsoft.pdf.form;

import java.io.Serializable;

import com.cabsoft.pdf.form.crypt.QueryBuilder;
import com.cabsoft.utils.Base64Util;

public class CommucationData implements Serializable {

    private static final long serialVersionUID = 20130905L;
    
    private String error = "";
    private byte[] pdf = null;
    private String signInfo = "";
    private String signUrl = "";
    private String signPos = "";

    public CommucationData() {
        error = "";
        pdf = null;
        signInfo = "";
        signUrl = "";
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

	public String getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}

	public String getSignUrl() {
		return signUrl;
	}

	public void setSignUrl(String signUrl) {
		this.signUrl = signUrl;
	}
	
	public String getSignPos() {
		return signPos;
	}

	public void setSignPos(String signPos) {
		this.signPos = signPos;
	}

	public String buildQuery(String charset) throws Exception{
		QueryBuilder qry = new QueryBuilder();
		String pdfStr = new String(Base64Util.encode(pdf, false));
		qry.addQuery("Error", error);
		qry.addQuery("SignUrl", signUrl);
		qry.addQuery("SignInfo", signInfo);
		qry.addQuery("SignPos", signPos);
		qry.addQuery("PDF", pdfStr);
		return qry.buildQuery(charset);
	}

}
