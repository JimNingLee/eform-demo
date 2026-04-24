package com.cabsoft.exporters.service;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import com.cabsoft.GlobalParams;
import com.cabsoft.RXSession;
import com.cabsoft.datasource.JBBankDataSource;
import com.cabsoft.fill.FillFactory;
import com.cabsoft.massive.PageView;
import com.cabsoft.progress.SimpleExportProgressMonitor;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.RXParameter;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.export.RXExcelApiExporter;
import com.cabsoft.rx.engine.export.RXXlsExporter;
import com.cabsoft.rx.engine.export.RXXlsExporterParameter;
import com.cabsoft.rx.engine.export.ooxml.RXXlsxExporter;
import com.cabsoft.rx.engine.query.RXXPathQueryExecuterFactory;
import com.cabsoft.utils.Compress;
import com.cabsoft.utils.Files;
import com.cabsoft.utils.RXDomDocument;
import com.cabsoft.utils.StackTrace;
import com.cabsoft.utils.StringUtils;

@SuppressWarnings({ "deprecation", "unused" })
public class ExportXlsService {
	private static final Log log = LogFactory.getLog(ExportXlsService.class);
	static GlobalParams globalParams;

	/**
	 * 엑셀 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static void Xlsx(String jobID, OutputStream out, HttpServletResponse response, ReportExpressPrint rxPrint)
			throws Exception {
		log.debug("XLSX 내보내기");
		try {
			RXXlsxExporter exporter = new RXXlsxExporter();

			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR,
					new SimpleExportProgressMonitor(rxPrint.getName(), "Xls"));
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}

	/**
	 * 엑셀 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static void Xlsx(String jobID, OutputStream out, HttpServletResponse response, List<ReportExpressPrint> rxPrintList)
			throws Exception {
		log.debug("XLSX 내보내기");
		try {
			RXXlsxExporter exporter = new RXXlsxExporter();

			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT_LIST, rxPrintList);
			
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}
	public static void jxl(String jobID, OutputStream out, HttpServletResponse response, ReportExpressPrint rxPrint)
			throws Exception {
		log.debug("JXL 내보내기");
		try {
			RXExcelApiExporter exporter = new RXExcelApiExporter();
			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR,
					new SimpleExportProgressMonitor(rxPrint.getName(), "JXL"));
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}

	public static void xls(String jobID, OutputStream out, HttpServletResponse response, ReportExpressPrint rxPrint)
			throws Exception {
		log.debug("XLS 내보내기");
		try {
			RXXlsExporter exporter = new RXXlsExporter();
			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR,
					new SimpleExportProgressMonitor(rxPrint.getName(), "XLS"));
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}

	public static void xls(String jobID, OutputStream out, HttpServletResponse response,
			List<ReportExpressPrint> rxPrintList) throws Exception {
		log.debug("XLS 내보내기(다건)");
		try {
			RXExcelApiExporter exporter = new RXExcelApiExporter();
			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT_LIST, rxPrintList);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
			
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}

	/**
	 * 엑셀 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static void Xlsx(String jobID, String filename, OutputStream out, HttpServletRequest request,
			HttpServletResponse response, String curPageView) throws Exception {
		log.debug("XLSX 내보내기 - 페이지 마진을 없애기 위해 새로 채움");
		try {

			ReportExpressPrint rxPrint = FillReport(jobID, request, filename, curPageView);

			RXXlsxExporter exporter = new RXXlsxExporter();

			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR,
					new SimpleExportProgressMonitor(rxPrint.getName(), "XLSX"));
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}

	public static void jxl(String jobID, String filename, OutputStream out, HttpServletRequest request,
			HttpServletResponse response, String curPageView) throws Exception {
		log.debug("JXL 내보내기 - 페이지 마진을 없애기 위해 새로 채움");
		try {
			ReportExpressPrint rxPrint = FillReport(jobID, request, filename, curPageView);

			RXExcelApiExporter exporter = new RXExcelApiExporter();
			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR,
					new SimpleExportProgressMonitor(rxPrint.getName(), "JXL"));
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}

	public static void xls(String jobID, String filename, OutputStream out, HttpServletRequest request,
			HttpServletResponse response, String curPageView) throws Exception {
		log.debug("LSX 내보내기 - 페이지 마진을 없애기 위해 새로 채움");
		try {
			ReportExpressPrint rxPrint = FillReport(jobID, request, filename, curPageView);

			RXXlsExporter exporter = new RXXlsExporter();
			exporter.setParameter(RXXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.TRUE);
			exporter.setParameter(RXXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				log.error(e);
				throw new Exception(e);
			}
		}
	}

	/*
	 * 보고서가 한 페이지 이상인 경우 페이지 헤더 정보를 제거하도록 새로 생성한다.
	 */
	private static ReportExpressPrint FillReport(String jobID, HttpServletRequest request, String filename,
			String curPageView) throws Exception {
		ServletContext context = request.getSession().getServletContext();
		log.debug("페이지 정보를 없애기 위해 IS_IGNORE_PAGINATION을 true로 하여 rxprint를 새로 만듬");

		ReportExpressPrint rxPrint = null;

		globalParams = GlobalParams.getInstance();

		if (globalParams == null) {
			globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
		}

		String fs = StringUtils.nvl(
				context.getRealPath(globalParams.getContextpath() + "/reports/" + filename + ".report"), "");

		try {
			File reportFile = new File(fs);
			if (!reportFile.exists()) {
				throw new Exception("컴파일된 보고서 파일 " + fs + "을(를) 찾을 수 없습니다. 먼저 보고서 서식을 컴파일하기시 바랍니다.");
			}
			Map<String, Object> params = new HashMap<String, Object>();
			FillFactory fill = new FillFactory();

			params.put(RXParameter.REPORT_LOCALE, Locale.US);
			params.put("imgDir", context.getRealPath(globalParams.getImagepath()) + "/");
			params.put("jobID", jobID);

			params.put(RXParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);

			if (curPageView == null || "".equals(curPageView) || "-1".equals(curPageView)) {
				// 세션에 있는 xmldata를 이용하여 가져오기
				Document document = RXDomDocument.parse(((RXSession) request.getSession().getAttribute(
						jobID + "_session")).getXmlData());
				params.put(RXXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
				params.put(RXXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
				params.put(RXXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
				params.put(RXXPathQueryExecuterFactory.XML_LOCALE, Locale.KOREAN);

				rxPrint = fill.fillReport(fs, params);
			} else {
				PageView[] pgView = ((RXSession) request.getSession().getAttribute(jobID + "_session")).getPageView();
				int curPg = Integer.parseInt(curPageView);
				curPg = curPg - 1;
				curPg = curPg < 0 ? 0 : curPg;

				String json = "";
				byte[] jsonBytes = ((RXSession) request.getSession().getAttribute(jobID + "_session")).getJson();
				if (jsonBytes != null) {
					json = new String(Compress.Unzip(jsonBytes));
				} else {
					jsonBytes = Files.readFile(context.getRealPath("/WEB-INF/properties/cabsoft/xml/") + "/" + filename
							+ ".json");
					json = new String(jsonBytes, "utf-8");
				}

				JBBankDataSource datasource = new JBBankDataSource(json);
				datasource.setRecordList(pgView[curPg].getStartIndex(), pgView[curPg].getEndIndex());

				params.put("title", datasource.getTitle());
				params.put("LangType", datasource.getLangType());
				params.put("HeaderColName", datasource.getHeaderColName());
				params.put("HeaderData", datasource.getHeaderData());
				params.put("ListColName", datasource.getListColName());
				params.put("FooterColName", datasource.getFooterColName());
				params.put("FooterData", datasource.getFooterData());

				rxPrint = fill.fillReport(fs, params, datasource);

			}
		} catch (RXException e) {
			log.error(e);
			throw new Exception(e);
		}
		return rxPrint;
	}
}
