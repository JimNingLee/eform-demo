package com.cabsoft.stress;

import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.GlobalParams;
import com.cabsoft.progress.SimpleExportProgressMonitor;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.export.RXXlsExporter;
import com.cabsoft.rx.engine.export.RXXlsExporterParameter;
import com.cabsoft.utils.StackTrace;

public class ExportXlsStressService {
	private static final Log log = LogFactory.getLog(ExportXlsStressService.class);
	static GlobalParams globalParams;

	public static void test(String jobID, OutputStream out, ReportExpressPrint rxPrint) throws Exception {
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
			
			exporter.setParameter(RXExporterParameter.OUTPUT_STREAM ,out);
			
			exporter.setParameter(RXExporterParameter.PROGRESS_MONITOR, new SimpleExportProgressMonitor(rxPrint.getName(), "XLS"));
			exporter.exportReport();
			out.flush();
			out.close();
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
}
