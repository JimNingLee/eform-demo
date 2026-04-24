package com.cabsoft.markany;

import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.GlobalParams;
import com.cabsoft.RXSession;
import com.cabsoft.massive.PageView;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.export.RXHtmlExporterParameter;
import com.cabsoft.utils.StringUtils;
import com.cabsoft.utils.sysinfo;

@SuppressWarnings({ "deprecation", "rawtypes", "unused" })
public class ExportMarkAnyHtml {
	private static final Log log = LogFactory.getLog(ExportMarkAnyHtml.class);

	/**
	 * HTML 문서로 내보내기
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static void Html(String jobID, HttpServletRequest request, HttpServletResponse response, 
			ReportExpressPrint rxPrint, HashMap hmap) throws Exception {
		log.debug("HTML 보고서 내보내기:"+jobID);
		try {
			String header = (String) hmap.get("header");
			
			GlobalParams globalParams = GlobalParams.getInstance();

			if (globalParams == null) {
				globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
			}

			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();

			log.debug("globalParams.getContextpath(): " + globalParams.getContextpath());
			
			RXSession ss = (RXSession) request.getSession().getAttribute(jobID+"_session");
			PageView[] pgView = ss.getPageView();
			
        	MarkAnyViewer exporter = new MarkAnyViewer();
			
			exporter.setParameter(RXHtmlExporterParameter.USER_AGENT, (String) request.getHeader("User-Agent").toLowerCase());
			exporter.setParameter(RXHtmlExporterParameter.ZOOM_RATIO, (float) 96.0f / 72.0f);
			
			exporter.setParameter(RXHtmlExporterParameter.HTML_HEADER, header);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_WRITER, out);

			exporter.setParameter(RXHtmlExporterParameter.BETWEEN_PAGES_HTML, "<p class=\"breakhere\" /><!-- MarkAny Page Gubun -->");
			
			exporter.setParameter(RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.valueOf(false));
			exporter.setParameter(RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.valueOf(true));
			exporter.setParameter(RXHtmlExporterParameter.IS_WRAP_BREAK_WORD, true);
			exporter.setParameter(RXHtmlExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(RXHtmlExporterParameter.IMAGES_URI, hmap.get("image_uri"));

			exporter.setTableSummary("현재 화면은 $$$ 페이지 입니다.");
			
			exporter.setUseAbsolutePosition(false);

			String webimgurl = (String) hmap.get("webimgurl");
			if (StringUtils.isEmpty(webimgurl)) {
				log.error("이미지 경로가 설정되어 있지 않습니다.");
				throw new Exception("이미지 경로가 설정되어 있지 않습니다.");
			} else {
				exporter.exportReport(webimgurl, request.getSession().getId());
			}
		} catch (RXException e) {
			log.error(e);
			throw new Exception(e);
		}
	}
	
	public static StringBuffer HtmlBuffer(String jobID, HttpServletRequest request, HttpServletResponse response, 
			ReportExpressPrint rxPrint, HashMap hmap) throws Exception {
		log.debug("HTML 보고서 내보내기:"+jobID);
		try {
			String header = (String) hmap.get("header");
			
			GlobalParams globalParams = GlobalParams.getInstance();

			if (globalParams == null) {
				globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
			}

			StringBuffer sb = new StringBuffer();
			
			log.debug("globalParams.getContextpath(): " + globalParams.getContextpath());
			
			RXSession ss = (RXSession) request.getSession().getAttribute(jobID+"_session");
			PageView[] pgView = ss.getPageView();
			
			String agent = request.getHeader("User-Agent");
			
        	MarkAnyViewer exporter = new MarkAnyViewer();
			
			exporter.setParameter(RXHtmlExporterParameter.USER_AGENT, (String) request.getHeader("User-Agent").toLowerCase());
			exporter.setParameter(RXHtmlExporterParameter.ZOOM_RATIO, (float) 96.0f / 72.0f);
			exporter.setParameter(RXHtmlExporterParameter.HTML_HEADER, header);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STRING_BUFFER, sb);

			if("ms".equals(sysinfo.os(agent)) && !"safari".equals(sysinfo.bw(agent))){
				exporter.setParameter(RXHtmlExporterParameter.BETWEEN_PAGES_HTML, "<p class=\"breakhere\" /><br style='height:0; line-height:0'/><!-- MarkAny Page Gubun -->");
			}else{
				exporter.setParameter(RXHtmlExporterParameter.BETWEEN_PAGES_HTML, "<p class=\"breakhere\" /><!-- MarkAny Page Gubun -->");
			}
			
			exporter.setParameter(RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.valueOf(false));
			exporter.setParameter(RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.valueOf(true));
			exporter.setParameter(RXHtmlExporterParameter.IS_WRAP_BREAK_WORD, true);
			exporter.setParameter(RXHtmlExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(RXHtmlExporterParameter.IMAGES_URI, hmap.get("image_uri"));
			
			/*
			 * htmp.position=absolute를 사용하는 경우 아래를 설정해야 이미지가 보임
			 */
			exporter.setParameter(RXHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);

			exporter.setTableSummary("현재 화면은 $$$ 페이지 입니다.");
			
			exporter.setUseAbsolutePosition(false);
			
			String webimgurl = (String) hmap.get("webimgurl");
			if (StringUtils.isEmpty(webimgurl)) {
				log.error("이미지 경로가 설정되어 있지 않습니다.");
				throw new Exception("이미지 경로가 설정되어 있지 않습니다.");
			} else {
				exporter.exportReport(webimgurl, request.getSession().getId());
			}
			return sb;
		} catch (RXException e) {
			log.error(e);
			throw new Exception(e);
		}
	}
}
