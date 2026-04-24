package com.cabsoft.ess;

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

@SuppressWarnings({ "deprecation", "rawtypes", "unused" })
public class ExportEss {
	private static final Log log = LogFactory.getLog(ExportEss.class);

	/**
	 * HTML 문서로 내보내기
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static void Ess(String jobID, HttpServletRequest request, HttpServletResponse response, ReportExpressPrint rxPrint, HashMap hmap) throws Exception {
		Ess(jobID, request, response, rxPrint, hmap, false);
	}
	
	public static void Ess(String jobID, HttpServletRequest request, HttpServletResponse response, ReportExpressPrint rxPrint, HashMap hmap, boolean useOverlap) throws Exception {
		log.debug("ESS 보고서 내보내기:"+jobID);
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
			
			EssViewer exporter = new EssViewer();
			
			exporter.setParameter(RXHtmlExporterParameter.USER_AGENT, (String) request.getHeader("User-Agent").toLowerCase());
			exporter.setParameter(RXHtmlExporterParameter.HTML_HEADER, header);
//			exporter.setParameter(RXHtmlExporterParameter.ZOOM_RATIO, (float) 96.0f / 72.0f);
			exporter.setParameter(RXHtmlExporterParameter.ZOOM_RATIO, (float) 2.0f);
			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_WRITER, out);

			exporter.setParameter(RXHtmlExporterParameter.BETWEEN_PAGES_HTML, "<p class=\"breakhere\" />");
			
			exporter.setParameter(RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, true);
			exporter.setParameter(RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, true);
			exporter.setParameter(RXHtmlExporterParameter.IS_WRAP_BREAK_WORD, true);
			exporter.setParameter(RXHtmlExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(RXHtmlExporterParameter.IMAGES_URI, hmap.get("image_uri"));
			
			exporter.setParameter(RXHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);

			exporter.setTableSummary("현재 화면은 $$$ 페이지 입니다.");
			exporter.setUseOverlap(useOverlap);
			
			exporter.setFormName("essForm");
			exporter.setCustomer(ss.getCustomer());
			exporter.setProduct(ss.getProduct());

			String webimgurl = (String) hmap.get("webimgurl");

			if (StringUtils.isEmpty(webimgurl)) {
				exporter.exportReport(jobID, request.getSession().getId());
			} else {
				exporter.exportReport(jobID, webimgurl, request.getSession().getId());
			}
			
			boolean previewOnly = (Boolean)hmap.get("previewOnly");
			exporter.setPreviewOnly(previewOnly);
			ss.setExptHMap(exporter.getExptHMap());
			
//			Map params = exporter.getEssParams();
//			log.debug("입력 받을 데이터 수: " + params.size());
//			Iterator it = params.keySet().iterator();
//			while (it.hasNext()) {
//				String key = (String) it.next();
//				System.out.println("입력 받을 변수: " + key);
//				log.debug("입력 받을 변수: " + key);
//			}
			
		} catch (RXException e) {
			log.error(e);
			throw new Exception(e);
		}
	}
}
