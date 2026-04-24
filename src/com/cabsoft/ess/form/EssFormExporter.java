package com.cabsoft.ess.form;

import javax.servlet.http.HttpServletRequest;

import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintFrame;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintPage;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.util.RXProperties;

@SuppressWarnings({"deprecation", "unused"})
public class EssFormExporter {
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	
	ReportExpressPrint rxprint = null;
	
    private String customer = "";
    private String product = "";
    private String reportFile = "";
    
    /**
     * 간편 입력을 위한 설정
     */
    private FormPages frmPages = null;
    private FormPage frmPage = null;
	
    public EssFormExporter(ReportExpressPrint rxprint, String useragent){
    	this.rxprint = rxprint;
		frmPages = new FormPages(useragent, "");
    }
    
    public EssFormExporter(ReportExpressPrint rxprint, HttpServletRequest request){
    	this.rxprint = rxprint;
    	String ua = request.getHeader("User-Agent");
		frmPages = new FormPages(request.getHeader("User-Agent"), request.getHeader("Accept"));
    }
    
	public FormPages getFormPages(){
		return frmPages;
	}
	
	public void setCustomer(String customer){
		this.customer = customer;
	}
	
	public void setProduct(String product){
		this.product = product;
	}
	
	public void setReportFile(String reportFile){
		this.reportFile = reportFile;
	}
	
	public void doExport(){
		frmPages.setCustomerName(customer);
		frmPages.setProductName(product);
		//frmPages.setProductName(reportFile);
		
		for(RXPrintPage page : rxprint.getPages()){
			frmPage = new FormPage();
			exportPage(page);
			frmPages.addPage(frmPage);
		}
	}
	
	private void exportPage(RXPrintPage page){
		for(RXPrintElement element : page.getElements()){
			if (element instanceof RXPrintText) {
				exportText((RXPrintText)element);
			} else if (element instanceof RXPrintImage) {
				exportImage((RXPrintImage)element);
			} else if (element instanceof RXPrintFrame) {
				exportFrame((RXPrintFrame)element);
			}
		}
	}
	
	private void exportFrame(RXPrintFrame frame){
		for(RXPrintElement element : frame.getElements()){
			if (element instanceof RXPrintText) {
				exportText((RXPrintText)element);
			} else if (element instanceof RXPrintImage) {
				exportImage((RXPrintImage)element);
			} else if (element instanceof RXPrintFrame) {
				exportFrame((RXPrintFrame)element);
			}
		}
	}
	
	private void exportImage(RXPrintImage image){
		String id = RXProperties.getProperty(image, PROPERTY_HTML_ID);
		id = id==null ? "" : id;
		
		if(!"".equals(id)){
			frmPage.addElement(image);
		}
	}
	
	private void exportText(RXPrintText text){
		String id = RXProperties.getProperty(text, PROPERTY_HTML_ID);
		id = id==null ? "" : id;
		
		if(!"".equals(id)){
			frmPage.addElement(text);
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		ReportExpressPrint rxprint = (ReportExpressPrint)RXobjLoader.loadObject("d:/eformbank/A3.rxprint");
//		
//		EssFormExporter eee = new EssFormExporter(rxprint);
//		eee.setCustomer("홍길동");
//		eee.setProduct("테스트 상품");
//		eee.doExport();
//		FormPages formPages = eee.getFormPages();
//		formPages.setExportHeaderFooter(true);
//		String s = formPages.exportForm();
//		System.out.println(s);
//	}
}
