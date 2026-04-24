package com.cabsoft.fill;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.fill.FillListener;

public class RXFillListner implements FillListener {
	private static final Log log = LogFactory.getLog(RXFillListner.class);
	
	private Timer timer = null;
	
	private Thread fillingThread;
	private String sourceFile = "";
	@SuppressWarnings("unused")
	private String xml = "";
	private boolean enableInterrupt;
	private int timeout;
	private int maxPages;
	private int page;
	private String reportName;
	private long startTime;
	private boolean isStart;
	
	public RXFillListner(){
		fillingThread = null;
		sourceFile = "";
		xml = "";
		enableInterrupt = false;
		timeout = 0;
		maxPages = 0;
		reportName = "";
		page = 0;
		startTime = 0L;
		isStart = false;
	}
	
	public void pageGenerated(ReportExpressPrint rxprint, int pageIndex) {
		//
	}

	public void pageUpdated(ReportExpressPrint rxprint, int pageIndex) {
		//
	}

	public void setFillingThread(Thread fillingThread) {
		this.fillingThread = fillingThread;
		if(isStart==true && fillingThread==null && timer!=null){
			timer.cancel();
			timer = null;
			isStart = false;
		}
	}

	public void setSourceFile(String sourceFile){
		this.sourceFile = sourceFile;
	}
	

	public void setXml(String xml){
		this.xml = xml;
	}
	
	public void setEnableInterrupt(boolean enableInterrupt){
		this.enableInterrupt = enableInterrupt;
	}

	public void setTimeOut(int timeOut) {
		this.timeout = timeOut*1000;
	}

	public void setMaxPages(int maxpages) {
		this.maxPages = maxpages;
	}
	
	public void startFill(String reportName){
		this.reportName = reportName;
		startTime = System.currentTimeMillis();
		isStart = true;
		FillTimer filltimer = new FillTimer();
		timer = new Timer();
		timer.schedule(filltimer, timeout);
	}
	
	public void addPage(int page){
		this.page = page;
		log.debug("page = " + page);
	}
	
	public void checkStatus(){
		if(enableInterrupt==true){
			long curTime = System.currentTimeMillis();
			if(timeout>0){
				long elTime = curTime - startTime;
				if(elTime>=timeout){
					InterruptFillingThread();
				}
			}
			if(maxPages>0){
				if(page>maxPages){
					InterruptFillingThread();
				}
			}
		}
	}
	
	public void FillFinished(){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
	}
	
	public void InterruptFillingThread(){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		
		if(fillingThread!=null){
			long curTime = System.currentTimeMillis();
			float runTime = (float)(curTime - startTime)/1000.0f;
			log.error("보고서 채우기 강제 종료");
			log.error("보고서 파일: " + sourceFile);
			log.error("보고서 이름: " + reportName);
			//log.error("XML 데이터: " + xml);
			log.error("생성된 보고서 페이지: " + page + " / " + maxPages);
			log.error("실행 기간(초): "  + runTime + " / " + timeout/1000 + "초");
			fillingThread.interrupt();
		}
	}
	
    class FillTimer extends TimerTask {
        public void run() {
            timer.cancel();
            timer = null;
            InterruptFillingThread();
        }
    } 
	
}
