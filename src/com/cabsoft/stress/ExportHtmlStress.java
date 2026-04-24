 package com.cabsoft.stress;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.GlobalParams;
import com.cabsoft.RXMSession;
import com.cabsoft.RXSession;
import com.cabsoft.html.HtmlViewer;
import com.cabsoft.massive.PageView;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.export.RXHtmlExporterParameter;
import com.cabsoft.utils.StringUtils;

@SuppressWarnings({ "deprecation", "rawtypes", "unused" })
public class ExportHtmlStress {
	private static final Log log = LogFactory.getLog(ExportHtmlStress.class);

	/**
	 * HTML 문서로 내보내기
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static boolean Test(String jobID, HttpServletRequest request, OutputStream out, 
			ReportExpressPrint rxPrint, HashMap hmap) throws Exception {
		
		return Test(jobID, request, out, rxPrint, hmap, false);
	}
	public static boolean Test(String jobID, HttpServletRequest request, OutputStream out, 
			ReportExpressPrint rxPrint, HashMap hmap, boolean useOverlap) throws Exception {
		log.debug("HTML 보고서 내보내기:"+jobID);
		try {
			String header = (String) hmap.get("header");

			GlobalParams globalParams = GlobalParams.getInstance();

			if (globalParams == null) {
				globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
			}

			log.debug("globalParams.getContextpath(): " + globalParams.getContextpath());
			
			RXSession ss = (RXSession) request.getSession().getAttribute(jobID+"_session");
			PageView[] pgView = ss.getPageView();
			
        	Integer curView = (Integer)hmap.get("__curView");
        	Integer maxRecords = (Integer)hmap.get("max_records");

			HtmlViewer exporter = new HtmlViewer();
			
        	if(pgView!=null && pgView.length>1){
        		exporter.setPageView(curView+1, pgView.length, pgView[pgView.length-1].getEndIndex(), 
        				pgView[curView].getStartIndex(), pgView[curView].getEndIndex());
        	}

        	String userAgent = (String) request.getHeader("User-Agent").toLowerCase();
			exporter.setParameter(RXHtmlExporterParameter.USER_AGENT, userAgent);
			exporter.setParameter(RXHtmlExporterParameter.HTML_HEADER, header);
			exporter.setParameter(RXHtmlExporterParameter.ZOOM_RATIO, (float) 96.0f / 72.0f);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);

			exporter.setParameter(RXHtmlExporterParameter.BETWEEN_PAGES_HTML, "<p class=\"breakhere\" /><br style='height:0; line-height:0'/>");
			
			exporter.setParameter(RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
			exporter.setParameter(RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, true);
			exporter.setParameter(RXHtmlExporterParameter.IS_WRAP_BREAK_WORD, true);
			exporter.setParameter(RXHtmlExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(RXHtmlExporterParameter.IMAGES_URI, hmap.get("image_uri"));
			
			/*
			 * 컨트롤이 겹쳐진 경우 아래와 같이 설정해야 함.
			 */
			exporter.setParameter(RXHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
			
			exporter.setTableSummary("현재 화면은 $$$ 페이지 입니다.");
			exporter.setUseOverlap(useOverlap);
			
			String webimgurl = (String) hmap.get("webimgurl");
			boolean previewOnly = (Boolean) hmap.get("previewOnly");
			exporter.setPreviewOnly(previewOnly);
			exporter.setAddscript((String)hmap.get("addscript"));

			if (StringUtils.isEmpty(webimgurl)) {
				exporter.exportReport(jobID, request.getSession().getId());
			} else {
				exporter.exportReport(jobID, webimgurl, request.getSession().getId());
			}
			return exporter.isRXCode();
		} catch (RXException e) {
			log.error(e);
			throw new Exception(e);
		}
	}

	public static boolean Test(String jobID, HttpServletRequest request, OutputStream out, 
			List<ReportExpressPrint> rxPrintList, HashMap hmap, boolean useOverlap) throws Exception {
		log.debug("HTML 보고서 내보내기:"+jobID);
		try {
			String header = (String) hmap.get("header");

			GlobalParams globalParams = GlobalParams.getInstance();

			if (globalParams == null) {
				globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
			}

			log.debug("globalParams.getContextpath(): " + globalParams.getContextpath());
			
			RXMSession ss = (RXMSession) request.getSession().getAttribute(jobID+"_session");
			
        	Integer curView = (Integer)hmap.get("__curView");
        	Integer maxRecords = (Integer)hmap.get("max_records");

			HtmlViewer exporter = new HtmlViewer();
			
        	String userAgent = (String) request.getHeader("User-Agent").toLowerCase();
			exporter.setParameter(RXHtmlExporterParameter.USER_AGENT, userAgent);
			exporter.setParameter(RXHtmlExporterParameter.HTML_HEADER, header);
			exporter.setParameter(RXHtmlExporterParameter.ZOOM_RATIO, (float) 96.0f / 72.0f);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT_LIST, rxPrintList);
			exporter.setParameter(RXExporterParameter.OUTPUT_WRITER, out);

			exporter.setParameter(RXHtmlExporterParameter.BETWEEN_PAGES_HTML, "<p class=\"breakhere\" /><br style='height:0; line-height:0'/>");
			
			exporter.setParameter(RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
			exporter.setParameter(RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, true);
			exporter.setParameter(RXHtmlExporterParameter.IS_WRAP_BREAK_WORD, true);
			exporter.setParameter(RXHtmlExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(RXHtmlExporterParameter.IMAGES_URI, hmap.get("image_uri"));
			
			/*
			 * 컨트롤이 겹쳐진 경우 아래와 같이 설정해야 함.
			 */
			exporter.setParameter(RXHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
			
			exporter.setTableSummary("현재 화면은 $$$ 페이지 입니다.");
			exporter.setUseOverlap(useOverlap);
			
			String webimgurl = (String) hmap.get("webimgurl");

			if (StringUtils.isEmpty(webimgurl)) {
				exporter.exportReport(jobID, request.getSession().getId());
			} else {
				exporter.exportReport(jobID, webimgurl, request.getSession().getId());
			}
			return exporter.isRXCode();
		} catch (RXException e) {
			log.error(e);
			throw new Exception(e);
		}
	}
}
