package com.cabsoft.fill;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.rx.engine.RXOrigin;
import com.cabsoft.rx.engine.RXPrintPage;
import com.cabsoft.rx.engine.ReportExpressPrint;

public class mergeReports {
	private static final Log log = LogFactory.getLog(mergeReports.class);

	public static ReportExpressPrint mergeReport(ReportExpressPrint main, List<ReportExpressPrint> lists) throws Exception {
		log.debug("Main 보고서에 다른 보고서 리스트를 추가함.");
		for (int i = 0, n = lists.size(); i < n; i++) {
			ReportExpressPrint rxp = (ReportExpressPrint) lists.get(i);
			List<RXPrintPage> pages = rxp.getPages();
			List<RXOrigin> origins = rxp.getOriginsList();
			int cnt = 0;
			for (RXPrintPage page : pages) {
				main.addPage(page);
				main.addOrigin(origins.get(cnt));
			}
		}

		return main;
	}

	public static ReportExpressPrint mergeReport(ReportExpressPrint main, ReportExpressPrint rxprint) throws Exception {
		log.debug("Main 보고서에 다른 보고서를 추가함.");
		List<RXPrintPage> pages = rxprint.getPages();
		List<RXOrigin> origins = rxprint.getOriginsList();
		int cnt = 0;
		for (RXPrintPage page : pages) {
			main.addPage(page);
			main.addOrigin(origins.get(cnt));
		}

		return main;
	}

	public static ReportExpressPrint mergeCoverPage(ReportExpressPrint corverPage, ReportExpressPrint Contnets) throws Exception {
		log.debug("보고서(Contents) 첫 페이지에 표지(coverPahe)를 추가함");
		List<RXPrintPage> pages = corverPage.getPages();
		List<RXOrigin> origins = corverPage.getOriginsList();
		int cnt = 0;
		for (RXPrintPage page : pages) {
			Contnets.addPage(cnt, page);
			Contnets.addOrigin(origins.get(cnt));
			cnt++;
		}
		return Contnets;
	}
}
