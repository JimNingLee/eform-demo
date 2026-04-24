package com.cabsoft.fill;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.rx.engine.fill.RXFillProgressMonitor;

public class SimpleFillProgressMonitor implements RXFillProgressMonitor {

	private static final Log logger = LogFactory.getLog(SimpleFillProgressMonitor.class);
	private String ReportName = "";
	private int TotalPages = 0;
	private String FilledDate = "";
	private long startTime = 0;
	private long ElapseTime = 0;

	public SimpleFillProgressMonitor() {
		this.ReportName = "";
		this.TotalPages = 0;
		this.FilledDate = "";
		this.startTime = 0;
		this.ElapseTime = 0;
	}

	public void startFill() {
		FilledDate = getCurrentDate();
		logger.debug("보고서 생성 시작 [" + FilledDate + "]");
	}

	public void afterPageAdd(int PageNum) {
		logger.debug(String.valueOf(PageNum) + " 번째 페이지 생성 완료");
	}

	public void endFill(String ReportName, int TotalPages) {
		this.TotalPages = TotalPages;
		this.ReportName = ReportName;
		ElapseTime = calcElapseTime();
		logger.debug(ReportName + " - " + String.valueOf(TotalPages) + " 페이지 보고서 생성 완료 [" + ElapseTime + "]ms");
	}

	private String getCurrentDate() {
		startTime = System.currentTimeMillis();

		SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdfNow.format(new Date(startTime));
	}

	private long calcElapseTime() {
		long etime = System.currentTimeMillis();
		return etime - startTime;
	}

	public String getReportName() {
		return ReportName;
	}

	public int getTotalPages() {
		return TotalPages;
	}
}
