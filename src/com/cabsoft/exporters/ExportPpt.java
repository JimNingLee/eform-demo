package com.cabsoft.exporters;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import com.cabsoft.progress.SimpleExportProgressMonitor;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.export.ooxml.RXPptxExporter;
import com.cabsoft.utils.StackTrace;
import com.cabsoft.utils.SystemUtils;

public class ExportPpt {

	/**
	 * 파워포인트 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static void Pptx(String jobID, HttpServletResponse response, ReportExpressPrint rxPrint) throws Exception {
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
			response.addHeader("Content-Disposition", "attachment; filename=" + SystemUtils.getDateTime("yyyyMMdd_hhmmss") + ".pptx");
			ServletOutputStream out = response.getOutputStream();

			RXPptxExporter exporter = new RXPptxExporter();

			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR, new SimpleExportProgressMonitor(rxPrint.getName(), "PPTX"));
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
				// log.debug("java.net.SocketException");
			} else {
				throw new Exception(e);
			}
		}
	}
}
