package com.cabsoft.exporters;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import com.cabsoft.progress.SimpleExportProgressMonitor;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.export.ooxml.RXDocxExporter;
import com.cabsoft.utils.StackTrace;
import com.cabsoft.utils.SystemUtils;

public class ExportDoc {
	/**
	 * MS 워드 내보내기
	 * 
	 * @param response
	 * @param rxPrint
	 * @throws Exception
	 */
	public static void Docx(String jobID, HttpServletResponse response, ReportExpressPrint rxPrint) throws Exception {
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			response.addHeader("Content-Disposition", "attachment; filename=" + SystemUtils.getDateTime("yyyyMMdd_hhmmss") + ".docx");
			ServletOutputStream out = response.getOutputStream();

			RXDocxExporter exporter = new RXDocxExporter();

			exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, out);
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR, new SimpleExportProgressMonitor(rxPrint.getName(), "DOCX"));
			exporter.exportReport();
		} catch (RXException e) {
			String se = StackTrace.getStackTrace(e);
			if (se.indexOf("java.net.SocketException") > -1) {
			} else {
				throw new Exception(e);
			}
		}
	}

}
