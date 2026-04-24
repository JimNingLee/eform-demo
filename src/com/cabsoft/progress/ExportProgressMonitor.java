package com.cabsoft.progress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.rx.engine.export.RXExportProgressMonitor;

public class ExportProgressMonitor implements RXExportProgressMonitor {

	private static final Log logger = LogFactory.getLog(ExportProgressMonitor.class);

	public ExportProgressMonitor() {
	}

	public void afterPageExport() {
		logger.debug("내보내기 완료");
	}

	public void afterPageExport(int reportIndex, int pageIndex) {
		logger.debug(String.valueOf(reportIndex + 1) + "," + String.valueOf(pageIndex + 1) + " 내보내기 완료");
	}

	public void startExport(int reportIndex, int reportSize, int pageSize) {
		logger.debug(String.valueOf(reportIndex + 1) + "," + String.valueOf(pageSize) + " 내보내기 시작");
	}

	public void endExport() {
		logger.debug("전체 보고서 내보내기 완료");
	}
}
