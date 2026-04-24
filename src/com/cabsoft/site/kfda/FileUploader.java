package com.cabsoft.site.kfda;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.cabsoft.sign.service.PdfSignService;
import com.cabsoft.smartcert.mobile.PdfPKCS7Info;
import com.cabsoft.text.pdf.PdfReader;
import com.cabsoft.utils.Files;
import com.cabsoft.utils.IssuerID;
import com.oreilly.servlet.MultipartRequest;

public class FileUploader {
	private String folderPath = ""; // 파일이 저장될 경로
	private String fontFile = "";	// 폰트 파일
	private String probeUrl = "";
	
	public void setFolderPath(String folderPath){
		this.folderPath = folderPath;
	}
	
	public void setFontFile(String fontFile){
		this.fontFile = fontFile;
	}
	
	public void setProbeUrl(String probeUrl){
		this.probeUrl = probeUrl;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation", "unused" })
	public void doUpload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		int postMaxSize = 100 * 1024 * 1024;
		String encoding = "UTF-8";

		MultipartRequest mRequest = new MultipartRequest(request, folderPath,
				postMaxSize, encoding, null);

		// 받은 데이터 출력
		String key, value;
		Enumeration<String> enumer = mRequest.getParameterNames();
		while (enumer.hasMoreElements()) {
			key = enumer.nextElement();
			value = mRequest.getParameter(key);

			//System.out.println(key + " ===> " + value);
		}

		// 파일 이름 출력
		String outfs = mRequest.getFilesystemName("pdffile");
		String pdffile = folderPath + outfs;
		String htmlfile = folderPath + mRequest.getFilesystemName("htmlfile");
		String xmlfile = folderPath + mRequest.getFilesystemName("xmlfile");
		
		System.out.println("pdffile = " + pdffile);
		System.out.println("htmlfile = " + htmlfile);
		System.out.println("xmlfile = " + xmlfile);

		PdfReader reader = new PdfReader(new FileInputStream(pdffile), "com/cabsoft".getBytes());
		int page_num = reader.getNumberOfPages();
		
        IssuerID id = new IssuerID();
//        String issueID = id.getIssuerID();
        String issueID =(String)request.getSession().getAttribute("issueID");
		
        IRXCodeInfo rx = new RXCodeInfo(xmlfile);
        
        /**
         * 발급 번호
         */
        /**
         * XML로 읽어옴
         * rx.setInsertBarcode(true);
         * rx.setFont_size(8);
        * rx.setId_x(10f);
        * rx.setId_y(267f);
        */
        
        rx.setFontFile(fontFile);
        rx.setIssuerID(issueID);
        rx.setIssuerIDString("발급 번호: " + id.getIssuerIDString(issueID));

        
        /**
         * 고밀도 바코드
         */
        /**
         * XML로 읽어옴
         * rx.setInsertBarcode(true);
         * rx.setBarcode_size_percent(23.98f);
         * rx.setBarcode_x(18f);
         * rx.setBarcode_y(17f);
         */
        String html =  new String( Files.readFile(htmlfile),"UTF-8");
        rx.setBarcodeData(html);
        rx.setPages_Num(page_num);
        
        // 원본 htm 파일 저장
        File f = new File( folderPath+issueID+".htm"); 
        FileUtils.writeStringToFile(f, html, "UTF-8");
        
        
        /**
         * QR Code
         */
        /**
         * XML로 읽어옴
         * rx.setInsertQRCode(true);
         * rx.setLastPageOnly(true);
         * rx.setQr_size_percent(27.5f);
         * rx.setQr_x(177f);
         * rx.setQr_y(42f);
        */
        rx.setProbeUrl(probeUrl);
        
        response.setContentType("application/pdf;charset=utf-8");
		response.addHeader("Content-Disposition", "attachment; filename=" +issueID + ".pdf;");
        
        ServletOutputStream out = response.getOutputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RXApplyRXCode appRXCode = new RXApplyRXCode(rx);
        appRXCode.doProcess(baos, reader);
        baos.flush();
        baos.close();
        
        String webinf = request.getRealPath("/WEB-INF/");
        ByteArrayOutputStream pdfbaos = new ByteArrayOutputStream();
		PdfPKCS7Info info = PdfSignService.signPdfSelf(pdfbaos, baos, webinf, null);
		pdfbaos.flush();
		pdfbaos.close();

		// pdf파일 저장
		
		Files.writeFile(pdfbaos.toByteArray(), folderPath+issueID+".pdf");
		
		out.write(pdfbaos.toByteArray());
		out.flush();
		out.close();
		
	}
}
