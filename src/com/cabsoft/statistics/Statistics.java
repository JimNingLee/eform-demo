package com.cabsoft.statistics;

public class Statistics {
	private static Statistics instance = null;
	
	private long total = 0L;
	private float totalAvg = 0f;
	private long totalMin = Long.MAX_VALUE;
	private long totalMax = Long.MIN_VALUE;
	
	private long reportCount = 0L;
	private long excelCount = 0L;
	private long pdfCount = 0L;
	private long signCount = 0L;
	
	private long reportError = 0L;
	private long excelError = 0L;
	private long pdfError = 0L;
	private long signError = 0L;
	
	private long reportRun = 0L;
	private long reportRunMin = Long.MAX_VALUE;
	private long reportRunMax = Long.MIN_VALUE;
	private float reportAvg = 0f;
	
	private long excelRun = 0L;
	private long excelRunMin = Long.MAX_VALUE;
	private long excelRunMax = Long.MIN_VALUE;
	private float excelAvg = 0f;
	
	private long pdfRun = 0L;
	private long pdfRunMin = Long.MAX_VALUE;
	private long pdfRunMax = Long.MIN_VALUE;
	private float pdfAvg = 0f;
	
	private long signRun = 0L;
	private long signRunMin = Long.MAX_VALUE;
	private long signRunMax = Long.MIN_VALUE;
	private float signAvg = 0f;
	
    public synchronized static Statistics getInstance() {
    	if(instance==null){
    		instance = new Statistics();
    	}
        return instance;
    }
    
    public synchronized void reset() {
    	instance = new Statistics();
    }
	
    public synchronized void setTotal(long total){
    	this.total += total;
    	
    	totalMin = totalMin > total ? total : totalMin;
    	totalMax = totalMax < total ? total : totalMax;
		
    	totalAvg = signCount==0 ? 0f : (float)(this.total/signCount);
    }
    
	public synchronized float getTotalMin(){
		if(signCount==0){
			return 0f;
		}else{
			return (float)totalMin/1000f;
		}
	}
	
	public synchronized float getTotalMax(){
		if(signCount==0){
			return 0f;
		}else{
			return (float)totalMax/1000f;
		}
	}
	
	public synchronized float getTotalAvg(){
		if(signCount==0){
			return 0f;
		}else{
			return (float)totalAvg/1000f;
		}
	}
    
	public synchronized long getReportCount() {
		return reportCount;
	}
	
	public synchronized void addReportCount() {
		this.reportCount++;
	}
	
	public synchronized long getExcelCount() {
		return excelCount;
	}
	
	public synchronized void addExcelCount() {
		this.excelCount++;
	}
	
	public synchronized long getPdfCount() {
		return pdfCount;
	}
	
	public synchronized void addPdfCount() {
		this.pdfCount++;
	}
	
	public synchronized long getSngnCount() {
		return signCount;
	}
	
	public synchronized void addSngnCount() {
		this.signCount++;
	}
	public synchronized long getReportError() {
		return reportError;
	}
	
	public synchronized void addReportError() {
		this.reportCount--;
		this.reportError++;
	}
	
	public synchronized long getExcelError() {
		return excelError;
	}
	
	public synchronized void addExcelError() {
		this.excelCount--;
		this.excelError++;
	}
	
	public synchronized long getPdfError() {
		return pdfError;
	}
	
	public synchronized void addPdfError() {
		this.pdfCount--;
		this.pdfError++;
	}
	
	public synchronized long getSignError() {
		return signError;
	}
	
	public synchronized void addSignError() {
		this.signCount--;
		this.signError++;
	}
	
	public synchronized float getReportRun() {
		return (float)reportRun/1000f;
	}
	
	public synchronized void setReportRun(long reportRun) {
		this.reportRun += reportRun;
		
		reportRunMin = reportRunMin > reportRun ? reportRun : reportRunMin;
		reportRunMax = reportRunMax < reportRun ? reportRun : reportRunMax;
		
		reportAvg = reportCount==0 ? 0f : (float)(this.reportRun/reportCount);
	}
	
	public synchronized float getReportRunMin() {
		if(reportCount==0){
			return 0f;
		}else{
			return (float)reportRunMin/1000f;
		}
	}

	public synchronized float getReportRunMax() {
		if(reportCount==0){
			return 0f;
		}else{
			return (float)reportRunMax/1000f;
		}
	}

	public synchronized float getReportAvg() {
		return (float)reportAvg/1000f;
	}

	public synchronized float getExcelRun() {
		return (float)excelRun/1000f;
	}

	public synchronized void setExcelRun(long excelRun) {
		this.excelRun += excelRun;
		
		excelRunMin = excelRunMin > excelRun ? excelRun : excelRunMin;
		excelRunMax = excelRunMax < excelRun ? excelRun : excelRunMax;
		
		excelAvg = excelCount==0 ? 0f : (float)(this.excelRun/excelCount);
	}

	public synchronized float getExcelRunMin() {
		if(excelCount==0){
			return 0f;
		}else{
			return (float)excelRunMin/1000f;
		}
	}

	public synchronized float getExcelRunMax() {
		if(excelCount==0){
			return 0f;
		}else{
			return (float)excelRunMax/1000f;
		}
	}

	public synchronized float getExcelAvg() {
		return (float)excelAvg/1000f;
	}

	public synchronized float getPdfRun() {
		return (float)pdfRun/1000f;
	}

	public synchronized void setPdfRun(long pdfRun) {
		this.pdfRun += pdfRun;
		
		pdfRunMin = pdfRunMin > pdfRun ? pdfRun : pdfRunMin;
		pdfRunMax = pdfRunMax < pdfRun ? pdfRun : pdfRunMax;
		
		pdfAvg = pdfCount==0 ? 0f : (float)(this.pdfRun/pdfCount);
	}

	public synchronized float getPdfRunMin() {
		if(pdfCount==0){
			return 0f;
		}else{
			return (float)pdfRunMin/1000;
		}
	}

	public synchronized float getPdfRunMax() {
		if(pdfCount==0){
			return 0f;
		}else{
			return (float)pdfRunMax/1000f;
		}
	}

	public synchronized float getPdfAvg() {
		return (float)pdfAvg/1000f;
	}

	public synchronized float getSignRun() {
		return (float)signRun/1000;
	}

	public synchronized void setSignRun(long signRun) {
		this.signRun += signRun;
		
		signRunMin = signRunMin > signRun ? signRun : signRunMin;
		signRunMax = signRunMax < signRun ? signRun : signRunMax;
		
		signAvg = signCount==0 ? 0f : (float)(this.signRun/signCount);
	}

	public synchronized float getSignRunMin() {
		if(signCount==0){
			return 0f;
		}else{
			return (float)signRunMin/1000f;
		}
	}

	public synchronized float getSignRunMax() {
		if(signCount==0){
			return 0f;
		}else{
			return (float)signRunMax/1000f;
		}
	}

	public synchronized float getSignAvg() {
		return (float)signAvg/1000f;
	}
	
}
