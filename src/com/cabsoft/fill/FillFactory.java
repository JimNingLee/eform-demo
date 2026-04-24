package com.cabsoft.fill;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.rx.engine.RXDataSource;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXParameter;
import com.cabsoft.rx.engine.ReportExpress;
import com.cabsoft.rx.engine.ReportExpressFillManager;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.utils.SystemUtils;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class FillFactory {

	private static final Log log = LogFactory.getLog(FillFactory.class);
	private Connection logConn = null;
	private String ReportFileName = "";
	// private long TotalPages = 0;
	private String FilledDate = "";
	private long startTime = 0;
	private long ElapseTime = 0;
	private SimpleFillProgressMonitor monitor;

	public FillFactory() {
		logConn = null;
		monitor = null;
		ReportFileName = "";
		FilledDate = "";
		startTime = 0;
		ElapseTime = 0;
	}

	public FillFactory(Connection con) {
		logConn = con;
		monitor = null;
		ReportFileName = "";
		FilledDate = "";
		startTime = 0;
		ElapseTime = 0;
	}

	public String fillReportToFile(String sourceFileName, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(sourceFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		String destFileName = ReportExpressFillManager.fillReportToFile(sourceFileName, parameters, conn);
		ElapseTime = calcElapseTime();
		return destFileName;
	}

	public String fillReportToFile(String sourceFileName, Map parameters) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(sourceFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		String destFileName = ReportExpressFillManager.fillReportToFile(sourceFileName, parameters);
		ElapseTime = calcElapseTime();
		return destFileName;
	}

	public void fillReportToFile(String sourceFileName, String destFileName, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(destFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToFile(sourceFileName, destFileName, parameters, conn);
		ElapseTime = calcElapseTime();
	}

	public void fillReportToFile(String sourceFileName, String destFileName, Map parameters) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(destFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToFile(sourceFileName, destFileName, parameters);
		ElapseTime = calcElapseTime();
	}

	public void fillReportToFile(ReportExpress reportExpress, String destFileName, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(destFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToFile(reportExpress, destFileName, parameters, conn);
		ElapseTime = calcElapseTime();
	}

	public void fillReportToFile(ReportExpress reportExpress, String destFileName, Map parameters) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(destFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToFile(reportExpress, destFileName, parameters);
		ElapseTime = calcElapseTime();
		
	}

	public ReportExpressPrint fillReport(String sourceFileName, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(sourceFileName, parameters, conn);
		ReportFileName = getNameOnly(sourceFileName);
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public ReportExpressPrint fillReport(String sourceFileName, Map parameters) throws RXException {
		log.debug("Fill [Start]");

		FilledDate = getCurrentDate();
		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);
		
		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(sourceFileName, parameters);

		ReportFileName = getNameOnly(sourceFileName);
		ElapseTime = calcElapseTime();
		log.debug("Fill [End]");
		return rxPrint;
	}

	public void fillReportToStream(InputStream inputStream, OutputStream outputStream, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToStream(inputStream, outputStream, parameters, conn);
		ElapseTime = calcElapseTime();
		
	}

	public void fillReportToStream(InputStream inputStream, OutputStream outputStream, Map parameters) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToStream(inputStream, outputStream, parameters);
		ElapseTime = calcElapseTime();
		
	}

	public void fillReportToStream(ReportExpress reportExpress, OutputStream outputStream, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = reportExpress.getName();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToStream(reportExpress, outputStream, parameters, conn);
		ElapseTime = calcElapseTime();
		
	}

	public void fillReportToStream(ReportExpress reportExpress, OutputStream outputStream, Map parameters) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = reportExpress.getName();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToStream(reportExpress, outputStream, parameters);
		ElapseTime = calcElapseTime();
		
	}

	public ReportExpressPrint fillReport(InputStream inputStream, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(inputStream, parameters, conn);
		ReportFileName = rxPrint.getName();
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public ReportExpressPrint fillReport(InputStream inputStream, Map parameters) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(inputStream, parameters);
		ReportFileName = rxPrint.getName();
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public ReportExpressPrint fillReport(ReportExpress reportExpress, Map parameters, Connection conn) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(reportExpress, parameters, conn);
		ReportFileName = rxPrint.getName();
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public ReportExpressPrint fillReport(ReportExpress reportExpress, Map parameters) throws RXException {
		FilledDate = getCurrentDate();
		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(reportExpress, parameters);
		ReportFileName = rxPrint.getName();
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public String fillReportToFile(String sourceFileName, Map parameters, RXDataSource dataSource) throws RXException {
		String destFileName = "";
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToFile(sourceFileName, parameters, dataSource);
		ReportFileName = getNameOnly(sourceFileName);
		ElapseTime = calcElapseTime();
		
		return destFileName;
	}

	public void fillReportToFile(String sourceFileName, String destFileName, Map parameters, RXDataSource dataSource) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(destFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToFile(sourceFileName, destFileName, parameters, dataSource);
		ElapseTime = calcElapseTime();
		
	}

	public void fillReportToFile(ReportExpress reportExpress, String destFileName, Map parameters, RXDataSource dataSource) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = getNameOnly(destFileName);

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToFile(reportExpress, destFileName, parameters, dataSource);
		ElapseTime = calcElapseTime();
		
	}

	public ReportExpressPrint fillReport(String sourceFileName, Map parameters, RXDataSource dataSource) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(sourceFileName, parameters, dataSource);
		ReportFileName = getNameOnly(sourceFileName);
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public void fillReportToStream(InputStream inputStream, OutputStream outputStream, Map parameters, RXDataSource dataSource) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToStream(inputStream, outputStream, parameters, dataSource);
		ElapseTime = calcElapseTime();
		
	}

	public void fillReportToStream(ReportExpress reportExpress, OutputStream outputStream, Map parameters, RXDataSource dataSource) throws RXException {
		FilledDate = getCurrentDate();
		ReportFileName = reportExpress.getName();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressFillManager.fillReportToStream(reportExpress, outputStream, parameters, dataSource);
		ElapseTime = calcElapseTime();
		
	}

	public ReportExpressPrint fillReport(InputStream inputStream, Map parameters, RXDataSource dataSource) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(inputStream, parameters, dataSource);
		ReportFileName = rxPrint.getName();
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public ReportExpressPrint fillReport(ReportExpress reportExpress, Map parameters, RXDataSource dataSource) throws RXException {
		FilledDate = getCurrentDate();

		monitor = new SimpleFillProgressMonitor();
		parameters.put(RXParameter.FILL_PROGRESS_MONITOR, monitor);

		ReportExpressPrint rxPrint = ReportExpressFillManager.fillReport(reportExpress, parameters, dataSource);
		ReportFileName = rxPrint.getName();
		ElapseTime = calcElapseTime();
		
		return rxPrint;
	}

	public String getFilledDateTime() {
		return FilledDate;
	}

	public long getElapseTime() {
		return ElapseTime;
	}

	public long calcElapseTime() {
		long etime = System.currentTimeMillis();
		return etime - startTime;
	}

	private String getNameOnly(String fs) {
		String f = SystemUtils.replaceSystemPathString(fs);
		return f.substring(f.lastIndexOf(SystemUtils.FILE_SEPARATOR) + 1);
	}

	private String getCurrentDate() {
		startTime = System.currentTimeMillis();

		SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdfNow.format(new Date(startTime));
	}
}
