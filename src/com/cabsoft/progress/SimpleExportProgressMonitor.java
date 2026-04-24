package com.cabsoft.progress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.rx.engine.export.RXExportProgressMonitor;

public class SimpleExportProgressMonitor implements RXExportProgressMonitor {

	private static final Log logger = LogFactory.getLog(SimpleExportProgressMonitor.class);
	private String ReportName = "";
	private String Type = "";

	public SimpleExportProgressMonitor(String ReportName, String Type) {
		this.ReportName = ReportName;
		this.Type = Type;
	}

	public void afterPageExport() {
		logger.debug("내보내기 완료");
	}

	public void afterPageExport(int reportIndex, int pageIndex) {
		logger.debug(ReportName + "." + Type.toLowerCase() + " - " + String.valueOf(reportIndex + 1) + "," + String.valueOf(pageIndex + 1) + " 내보내기 완료");
	}

	public void startExport(int reportIndex, int reportSize, int pageSize) {
		logger.debug(ReportName + "." + Type.toLowerCase() + " - " + String.valueOf(reportSize) + "," + String.valueOf(reportIndex + 1) + "," + String.valueOf(pageSize) + " 내보내기 시작");
	}

	public void endExport() {
		logger.debug(ReportName + "." + Type.toLowerCase() + " 전체 보고서 내보내기 완료");
	}
}
