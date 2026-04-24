package com.cabsoft.exporters.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.GlobalParams;
import com.cabsoft.sign.service.PdfSignTsaService;
import com.cabsoft.smartcert.mobile.PdfPKCS7Info;
import com.cabsoft.text.pdf.ICC_Profile;
import com.cabsoft.text.pdf.PdfCopyFields;
import com.cabsoft.text.pdf.PdfReader;
import com.cabsoft.utils.StackTrace;
import com.cabsoft.utils.SystemUtils;
import com.cabsoft.pdfutils.ICC_ProfileData;
import com.cabsoft.pdfutils.Watermark;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.exporters.pdf.RXPdfExporter;
import com.cabsoft.rx.engine.export.RXPdfExporterParameter;
import com.cabsoft.rx.engine.fill.RXAbstractLRUVirtualizer;
import com.cabsoft.rx.engine.util.RXLoader;

@SuppressWarnings("deprecation")
public class ExportPdfService {
	private static final Log log = LogFactory.getLog(ExportPdfService.class);

	// parameters에는 인증서 저장 경로, Timestamp 이미지 경로와 ICC Profile이 포함된다.
	@SuppressWarnings("unused")
	private Map<String, Object> parameters = null;
	private OutputStream out;
	private ReportExpressPrint rxPrint;
	private ICC_Profile icc = null;

	GlobalParams globalParams = null;
	boolean SignOnly = false;

	public PdfPKCS7Info exportPdf(String webinf, OutputStream out, String rxPrintFile, Map<String, Object> parameters,
			String pwd) throws Exception {
		ReportExpressPrint rxPrint = (ReportExpressPrint) RXLoader.loadObject(rxPrintFile);
		return exportPdf(webinf, out, rxPrint, parameters, pwd);

	}

	public PdfPKCS7Info exportPdf(String webinf, OutputStream out, ReportExpressPrint rxPrint,
			Map<String, Object> parameters, String pwd) throws Exception {
		this.out = out;
		this.rxPrint = rxPrint;
		this.parameters = parameters;

		globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		PdfPKCS7Info info = null;

		String Process = (String) parameters.get("Process");
		String signOnly = (String) parameters.get("SignOnly");
		SignOnly = (signOnly != null && signOnly.equalsIgnoreCase("1")) ? true : false;
		Object virtualizer = (Object) parameters.get("virtualizer");

		icc = ICC_ProfileData.getRGBProfile();

		String CertFilePath = globalParams.getCabsoftPath() + "cert/";
		CertFilePath = SystemUtils.replaceSystemPathString(CertFilePath);
		parameters.put("CertFilePath", CertFilePath);

		parameters.put("TimestampPath", globalParams.getTimestampPath());

		log.debug("Process = " + Process);
		log.debug("signOnly = " + signOnly);
		log.debug("CertFilePath = " + CertFilePath);

		try {
			if (Process.equalsIgnoreCase("PDF")) {
				Pdf();
			} else if (Process.equalsIgnoreCase("SignedPDF")) {
				info = SignPdf(webinf, pwd);
			} else if (Process.equalsIgnoreCase("PDFA")) {
				PdfA();
			} else if (Process.equalsIgnoreCase("SignedPDFA")) {
				info = PdfASign(webinf, pwd);
			}
		} catch (Exception e) {
			log.error(StackTrace.getStackTrace(e));
			throw new Exception(e);
		}

		// Virtualizer Cleanup
		try {
			if (virtualizer != null)
				((RXAbstractLRUVirtualizer) virtualizer).cleanup();
		} catch (Exception e) {
			log.error(e);
		}

		return info;

	}

	public PdfPKCS7Info exportPdf(String webinf, OutputStream out, List<ReportExpressPrint> rxPrintList,
			Map<String, Object> parameters, String pwd) throws Exception {
		this.out = out;
		// this.rxPrint = rxPrint;
		this.parameters = parameters;

		globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(webinf);
		}

		PdfPKCS7Info info = null;

		String Process = (String) parameters.get("Process");
		String signOnly = (String) parameters.get("SignOnly");
		SignOnly = (signOnly != null && signOnly.equalsIgnoreCase("1")) ? true : false;
		Object virtualizer = (Object) parameters.get("virtualizer");

		icc = ICC_ProfileData.getRGBProfile();

		String CertFilePath = globalParams.getCabsoftPath() + "cert/";
		CertFilePath = SystemUtils.replaceSystemPathString(CertFilePath);
		parameters.put("CertFilePath", CertFilePath);

		parameters.put("TimestampPath", globalParams.getTimestampPath());

		log.debug("Process = " + Process);
		log.debug("signOnly = " + signOnly);
		log.debug("CertFilePath = " + CertFilePath);

		try {
			if (Process.equalsIgnoreCase("PDF")) {
				// Pdf(rxPrintList); // 자동으로 생성
				PdfManual(rxPrintList);// 수동으로 생성
			} else if (Process.equalsIgnoreCase("SignedPDF")) {
				info = SignPdf(webinf, pwd);
			} else if (Process.equalsIgnoreCase("PDFA")) {
				PdfA();
			} else if (Process.equalsIgnoreCase("SignedPDFA")) {
				info = PdfASign(webinf, pwd);
			}
		} catch (Exception e) {
			log.error(StackTrace.getStackTrace(e));
			throw new Exception(e);
		}

		// Virtualizer Cleanup
		try {
			if (virtualizer != null)
				((RXAbstractLRUVirtualizer) virtualizer).cleanup();
		} catch (Exception e) {
			log.error(e);
		}

		return info;

	}

	/**
	 * PDF 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	private void Pdf() throws RXException, Exception {
		log.debug("PDF 내보내기");
		try {

			RXPdfExporter exporter = new RXPdfExporter();

			exporter.setParameter(RXPdfExporterParameter.IS_TAGGED, false);

			// PDF 보안 설정
			exporter.setParameter(RXPdfExporterParameter.OWNER_PASSWORD, "com/cabsoft");
			exporter.setParameter(RXPdfExporterParameter.PERMISSIONS, globalParams.getPdfPermision());

			// exporter.setParameter(RXPdfExporterParameter.PDF_VERSION,
			// RXPdfExporterParameter.PDF_VERSION_1_5);

			// PDF 메타데이터 설정
			exporter.setParameter(RXPdfExporterParameter.METADATA_TITLE,
					globalParams.getProperty("com.cabsoft.rx.pdf.title", "Test generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_AUTHOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.author", "ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_SUBJECT,
					globalParams.getProperty("com.cabsoft.rx.pdf.subject", "PDF generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_KEYWORDS,
					globalParams.getProperty("com.cabsoft.rx.pdf.keywords", "ReportExpress Enterprise,Cabsoft"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_CREATOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.creator", "ReportExpress"));

			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);

			// exporter.setParameter(RXPdfExporterParameter.IS_COMPRESSED,
			// Boolean.TRUE);

			// 워터마크 삽입
			boolean isInsertWaterMark = globalParams.getBooleanProperty("com.cabsoft.rx.html.insertwatermark", false);

			if (isInsertWaterMark) {

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, bos);
				exporter.exportReport();
				bos.flush();
				bos.close();

				ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
				PdfReader reader = new PdfReader(is, "com/cabsoft".getBytes());

				float transparancy = globalParams.getFloatProperty("com.cabsoft.watermark.transparancy", -1.0f);
				Watermark.InsertWatermark(out, reader,
						globalParams.getCabsoftPath() + globalParams.getProperty("com.cabsoft.rx.html.watermark"),
						transparancy);
				is.close();

			} else {
				exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			}

			exporter.exportReport();

		} catch (RXException e) {
			String st = StackTrace.getStackTrace(e);
			if (st.indexOf("connection abort") > -1) {
				throw new Exception(e);
			}
		}
	}

	private void Pdf(List<ReportExpressPrint> rxPrintList) throws RXException, Exception {
		log.debug("PDF 내보내기(자동)");
		try {

			RXPdfExporter exporter = new RXPdfExporter();

			exporter.setParameter(RXPdfExporterParameter.IS_TAGGED, false);

			// PDF 보안 설정
			exporter.setParameter(RXPdfExporterParameter.OWNER_PASSWORD, "com/cabsoft");
			exporter.setParameter(RXPdfExporterParameter.PERMISSIONS, globalParams.getPdfPermision());

			// exporter.setParameter(RXPdfExporterParameter.PDF_VERSION,
			// RXPdfExporterParameter.PDF_VERSION_1_5);

			// PDF 메타데이터 설정
			exporter.setParameter(RXPdfExporterParameter.METADATA_TITLE,
					globalParams.getProperty("com.cabsoft.rx.pdf.title", "Test generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_AUTHOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.author", "ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_SUBJECT,
					globalParams.getProperty("com.cabsoft.rx.pdf.subject", "PDF generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_KEYWORDS,
					globalParams.getProperty("com.cabsoft.rx.pdf.keywords", "ReportExpress Enterprise,Cabsoft"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_CREATOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.creator", "ReportExpress"));

			// exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT,
			// rxPrint);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT_LIST, rxPrintList);

			// exporter.setParameter(RXPdfExporterParameter.IS_COMPRESSED,
			// Boolean.TRUE);

			// 워터마크 삽입
			boolean isInsertWaterMark = globalParams.getBooleanProperty("com.cabsoft.rx.html.insertwatermark", false);

			if (isInsertWaterMark) {

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, bos);
				exporter.exportReport();
				bos.flush();
				bos.close();

				ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
				PdfReader reader = new PdfReader(is, "com/cabsoft".getBytes());

				float transparancy = globalParams.getFloatProperty("com.cabsoft.watermark.transparancy", -1.0f);
				Watermark.InsertWatermark(out, reader,
						globalParams.getCabsoftPath() + globalParams.getProperty("com.cabsoft.rx.html.watermark"),
						transparancy);
				is.close();

			} else {
				exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			}

			exporter.exportReport();

		} catch (RXException e) {
			String st = StackTrace.getStackTrace(e);
			if (st.indexOf("connection abort") > -1) {
				throw new Exception(e);
			}
		}
	}

	/**
	 * PDF 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	private void PdfManual(List<ReportExpressPrint> rxPrintList) throws RXException, Exception {
		log.debug("PDF 내보내기(수동)-다건");
		try {

			RXPdfExporter exporter = new RXPdfExporter();

			exporter.setParameter(RXPdfExporterParameter.IS_TAGGED, false);

			// PDF 보안 설정
			exporter.setParameter(RXPdfExporterParameter.OWNER_PASSWORD, "com/cabsoft");
			exporter.setParameter(RXPdfExporterParameter.PERMISSIONS, globalParams.getPdfPermision());

			// exporter.setParameter(RXPdfExporterParameter.PDF_VERSION,
			// RXPdfExporterParameter.PDF_VERSION_1_5);

			// PDF 메타데이터 설정
			exporter.setParameter(RXPdfExporterParameter.METADATA_TITLE,
					globalParams.getProperty("com.cabsoft.rx.pdf.title", "Test generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_AUTHOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.author", "ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_SUBJECT,
					globalParams.getProperty("com.cabsoft.rx.pdf.subject", "PDF generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_KEYWORDS,
					globalParams.getProperty("com.cabsoft.rx.pdf.keywords", "ReportExpress Enterprise,Cabsoft"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_CREATOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.creator", "ReportExpress"));

			// exporter.setParameter(RXPdfExporterParameter.IS_COMPRESSED,
			// Boolean.TRUE);

			// 워터마크 삽입
			boolean isInsertWaterMark = globalParams.getBooleanProperty("com.cabsoft.rx.html.insertwatermark", false);

			PdfCopyFields copy = new PdfCopyFields(out);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int i = 0; i < rxPrintList.size(); i++) {
				this.rxPrint = rxPrintList.get(i);
				exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);

				log.debug("isInsertWaterMark:" + isInsertWaterMark);
				
				if (isInsertWaterMark) {
					exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, bos);
					exporter.exportReport();
					//bos.flush();
					// bos.close();

					ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
					PdfReader reader = new PdfReader(is, "com/cabsoft".getBytes());

					float transparancy = globalParams.getFloatProperty("com.cabsoft.watermark.transparancy", -1.0f);
					Watermark.InsertWatermark(bos, reader,
							globalParams.getCabsoftPath() + globalParams.getProperty("com.cabsoft.rx.html.watermark"),
							transparancy);
					is.close();

				} else {
					exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, bos);
					exporter.exportReport();
				}
				ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
				PdfReader reader = new PdfReader(is, "com/cabsoft".getBytes());
				is.close();
				copy.addDocument(reader);

			}
			bos.close();
			copy.close();

		} catch (RXException e) {
			String st = StackTrace.getStackTrace(e);
			if (st.indexOf("connection abort") > -1) {
				throw new Exception(e);
			}
		}
	}

	/**
	 * 전자서명된 PDF 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @param certFile
	 * @param password
	 * @throws Exception
	 */

	private PdfPKCS7Info SignPdf(String webinf, String pwd) throws Exception {
		log.debug("전자서명된 PDF 내보내기 " + SignOnly);
		PdfPKCS7Info info = null;
		try {
			ByteArrayOutputStream o = new ByteArrayOutputStream();

			RXPdfExporter exporter = new RXPdfExporter();

			// PDF 메타데이터 설정
			exporter.setParameter(RXPdfExporterParameter.METADATA_TITLE,
					globalParams.getProperty("com.cabsoft.rx.pdf.title", "Test generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_AUTHOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.author", "ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_SUBJECT,
					globalParams.getProperty("com.cabsoft.rx.pdf.subject", "PDF generated by ReportExpress"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_KEYWORDS,
					globalParams.getProperty("com.cabsoft.rx.pdf.keywords", "ReportExpress Enterprise,Cabsoft"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_CREATOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.creator", "ReportExpress"));

			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, o);
			exporter.exportReport();
			o.flush();
			o.close();

			if (SignOnly == false) {
				log.debug("전자서명 및 TSA");

				info = PdfSignTsaService.signPdf(out, o, webinf, pwd);

			} else {
				/*
				 * 전자서명만 하는 경우
				 */
				log.debug("전자서명만 하는 경우");
				info = PdfSignTsaService.signPdf(out, o, webinf, pwd);
			}
		} catch (RXException e) {
			throw new Exception(e);
		}
		return info;
	}

	/**
	 * PDF/A 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @param icc
	 * @param certFile
	 * @param password
	 * @throws Exception
	 */
	private void PdfA() throws Exception {
		log.debug("PDF/A 내보내기");
		try {

			RXPdfExporter exporter = new RXPdfExporter();

			exporter.setParameter(RXPdfExporterParameter.METADATA_TITLE,
					globalParams.getProperty("com.cabsoft.rx.pdf.title"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_AUTHOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.author"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_SUBJECT,
					globalParams.getProperty("com.cabsoft.rx.pdf.subject"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_KEYWORDS,
					globalParams.getProperty("com.cabsoft.rx.pdf.keywords"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_CREATOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.creator"));

			exporter.setParameter(RXPdfExporterParameter.ICC_PROFILE, icc);

			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXPdfExporterParameter.PDF_FORMAT, "PDFA1B");

			// 전자서명을 위해 ByteArrayOutputStream로 받는다.
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
		} catch (RXException e) {
			throw new Exception(e);
		}
	}

	private void PdfA(List<ReportExpressPrint> rxPrintList) throws Exception {
		log.debug("PDF/A 내보내기");
		try {

			RXPdfExporter exporter = new RXPdfExporter();

			exporter.setParameter(RXPdfExporterParameter.METADATA_TITLE,
					globalParams.getProperty("com.cabsoft.rx.pdf.title"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_AUTHOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.author"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_SUBJECT,
					globalParams.getProperty("com.cabsoft.rx.pdf.subject"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_KEYWORDS,
					globalParams.getProperty("com.cabsoft.rx.pdf.keywords"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_CREATOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.creator"));

			exporter.setParameter(RXPdfExporterParameter.ICC_PROFILE, icc);

			// exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT,
			// rxPrint);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT_LIST, rxPrintList);

			exporter.setParameter(RXPdfExporterParameter.PDF_FORMAT, "PDFA1B");

			// 전자서명을 위해 ByteArrayOutputStream로 받는다.
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
		} catch (RXException e) {
			throw new Exception(e);
		}
	}

	/*
	 * 전자서명된 PDF/A 내보내기
	 */
	private PdfPKCS7Info PdfASign(String webinf, String pwd) throws Exception {
		log.debug("전자서명된 PDF/A 내보내기");
		PdfPKCS7Info info = null;
		try {
			ByteArrayOutputStream o = new ByteArrayOutputStream();

			RXPdfExporter exporter = new RXPdfExporter();

			exporter.setParameter(RXPdfExporterParameter.METADATA_TITLE,
					globalParams.getProperty("com.cabsoft.rx.pdf.title"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_AUTHOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.author"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_SUBJECT,
					globalParams.getProperty("com.cabsoft.rx.pdf.subject"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_KEYWORDS,
					globalParams.getProperty("com.cabsoft.rx.pdf.keywords"));
			exporter.setParameter(RXPdfExporterParameter.METADATA_CREATOR,
					globalParams.getProperty("com.cabsoft.rx.pdf.creator"));

			exporter.setParameter(RXPdfExporterParameter.ICC_PROFILE, icc);

			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXPdfExporterParameter.PDF_FORMAT, "PDFA1B");

			// 전자서명을 위해 ByteArrayOutputStream로 받는다.
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, o);
			exporter.exportReport();
			o.flush();
			o.close();

			if (SignOnly == false) {
				log.debug("전자서명 및 TSA");
				info = PdfSignTsaService.signPdf(out, o, webinf, pwd);
			} else {
				/*
				 * 전자서명만 하는 경우
				 */
				log.debug("전자서명만 하는 경우");
				info = PdfSignTsaService.signPdf(out, o, webinf, pwd);
			}
		} catch (RXException e) {
			throw new Exception(e);
		}
		return info;
	}
}
