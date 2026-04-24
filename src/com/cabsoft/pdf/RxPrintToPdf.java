package com.cabsoft.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cabsoft.exporters.service.ExportPdfService;
import com.cabsoft.rx.engine.ReportExpressPrint;

public class RxPrintToPdf extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4357971219147930021L;

	/**
	* ReportExpressPrint 객체를 받아 특정 경로에 PDF 파일로 저장합니다.
	* @param request HttpServletRequest
	* @param rxPrint 역직렬화된 ReportExpressPrint 객체
	* @param outputFileName 확장자를 제외한 출력 파일 이름
	*/
	public void generatePdfToFile(HttpServletRequest request, ReportExpressPrint rxPrint, String outputFileName, String folderDir)
			throws Exception {

		// 1. WEB-INF 경로를 얻어옵니다
		String webInfPath = request.getServletContext().getRealPath("/WEB-INF");

		// 2. PDF 파일이 저장될 경로를 지정합니다.
		String outputFolderPath = folderDir + "\\pdf_reports";

		// 출력 폴더가 없으면 자동으로 생성합니다.
		File outputDir = new File(outputFolderPath);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		// 3. 최종적으로 저장될 파일의 전체 경로를 설정합니다.
		File outputFile = new File(outputDir, outputFileName + ".pdf");

		// 4. FileOutputStream을 사용하여 파일에 직접 PDF 내용을 씁니다.
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SignOnly", "1");
			parameters.put("Process", "pdf");

			ExportPdfService pdf = new ExportPdfService();

			// 변경점: response.getOutputStream() 대신 FileOutputStream 객체(fos)를 전달합니다.
			pdf.exportPdf(webInfPath, fos, rxPrint, parameters, "");

			// 다운로드와 관련된 response 헤더 설정은 모두 필요 없으므로 제거했습니다.
		} catch(Exception e) {
			e.printStackTrace();	
		}
	}
	
	public void convert_to_pdf(HttpServletRequest request, HttpServletResponse response) {
	    // 위에서 선언한 PDF 생성기 클래스의 인스턴스를 만듭니다.
		
		RxPrintToPdf rxPrintToPdf = new RxPrintToPdf();

		String folderDir = request.getParameter("folderName");

	    // .rxprint 원본 파일들이 있는 폴더 경로입니다.
	    File folder = new File(folderDir);
	    File[] files = folder.listFiles();
	    
	    StringBuilder processLog = new StringBuilder();
	    
	    if (files == null) {
	    	processLog.append("<li>오류: " + folderDir + " 폴더를 찾을 수 없거나 경로가 잘못되었습니다.</li>");
	    } else {
	    	// 폴더 내의 각 파일을 순회합니다.
	    	for (File file : files) {
	    		// .rxprint 확장자를 가진 파일만 처리합니다.
	    		if (file.isFile() && file.getName().endsWith(".rxprint")) {
	    			ReportExpressPrint rxPrint = null;
	    			String baseFileName = file.getName().replace(".rxprint", "");
	    
	    			try {
	    				// 파일을 읽어와 객체로 역직렬화합니다.
	    				try {
	    					FileInputStream fileIn = new FileInputStream(file);
	    					ObjectInputStream objectIn = new ObjectInputStream(fileIn);
	    					rxPrint = (ReportExpressPrint) objectIn.readObject();
	    				}catch(Exception e) {
	    					e.printStackTrace();
	    				}
	    
	    				// PDF 파일 생성을 요청합니다.
	    				rxPrintToPdf.generatePdfToFile(request, rxPrint, baseFileName, folderDir);
	    
	    			} catch (Exception e) {
	    				e.printStackTrace(); // 서버 콘솔에 전체 에러 로그를 출력합니다.
	    			}
	   			}
	   		}
	   	}
	}
}
