/******************************************
 * A4 용지: 755px X 1084px
 * 1inch = 25.4mm = 96pixel
 * 1mm = 3.78px
 ******************************************/

/******************************************
 * HTML 문서 구조
 ******************************************
 * 
 * <div id="docs-editor-container">
 *   <div id="docs-editor">
 *     <div id="rxe-appview">
 *       <div id="paper">
 *         <div id="canvas">
 *           <div id="doc"
 *           
 *             <div>
 *               <div>
 *                 1 페이지 내용
 *               </div>
 *             </div>
 *             
 *             <div>
 *               <div>
 *                 2 페이지 내용
 *               </div>
 *             </div>
 *             
 *             페이지 수 만큼 반복
 *             
 *           </vid>
 *         </div>
 *       </div>
 *     </div>
 *   </div>
 * </div>
 ******************************************/

package com.cabsoft.markany;

public class MarkAnyViewerHelper {
	/*
	 * 보고서의 페이지 수와 페이지의 폭 그리고 높이를 픽셀 단위로 넘겨받아 자바스크립트 변수에 할당한다.
	 */
	public static String setMaxPages(int maxpages, int pageWidth, int pageHeight){
		StringBuffer script = new StringBuffer();

		script.append("");

		/*
		 * max_page, pageWidth, pageHeight는 include되는 자바스크립트에서 정의한다.
		 */
		script.append("<script type=\"text/javascript\">");
		script.append("max_page=" + String.valueOf(maxpages) + ";");
		script.append("pageWidth=" + String.valueOf(pageWidth) + ";");
		script.append("pageHeight=" + String.valueOf(pageHeight) + ";");
		script.append("</script>");

		return script.toString();
	}

	/*
	 * 문서의 스타일을 정의한다.
	 */
	public static String getDocStart(String useragent, int pageWidth, int pageHeight, int left_margin, int right_margin) {
		StringBuffer doc_start = new StringBuffer();
		
		doc_start.append("");

		/* 문서 시작 */
		doc_start.append("<div id=\"docs-editor-container\">");
		doc_start.append("<div id=\"docs-editor\">");
		doc_start.append("<div dir=\"ltr\" id=\"rxe-appview\" class=\"rxe-appview\">");
		doc_start.append("<div id=\"paper\" class=\"paper\">");

		doc_start.append("<div id=\"canvas\" style=\"overflow-y: scroll; height: " + String.valueOf(pageHeight) + "px;\" class=\"rxe-appview-editor\">");

		/**
		 * 인쇄시에 페이지 높이를 픽셀단위로
		 * 미리보기만 하는 경우 100%로 조정
		 */
		doc_start.append("<div id=\"doc\" style=\"left: 55px; top: 10px; width:" + String.valueOf(pageWidth) + "px; height: " + String.valueOf(pageHeight) + "px;\" class=\"rxe-paginateddocumentplugin\">");
		return doc_start.toString();
	}

	/*
	 * 문서 스타일 정의의 끝 및 HTML 문서의 끝
	 */
	public static String getDocEnd(String htmlFooter) {
		StringBuffer doc_end = new StringBuffer();

		doc_end.append("");

		/* 문서 끝 */
		doc_end.append("</div>");
		doc_end.append("</div>");
		doc_end.append("</div>");
		doc_end.append("</div>");
		doc_end.append("</div>");
		doc_end.append("</div>");

		if (htmlFooter != null && !htmlFooter.equalsIgnoreCase("")) {
			doc_end.append(htmlFooter);
		} else {
			doc_end.append("</body>");
			doc_end.append("</html>");
		}
		return doc_end.toString();
	}

	/*
	 * 보고서 페이지의 시작 부분
	 */
	public static String getPageStart(int screenW, int screenH, boolean isAbsolutePosition) {
		StringBuffer page_start = new StringBuffer();

		page_start.append("");

		if(isAbsolutePosition==false){
			page_start.append("<div style=\"width: " + String.valueOf(screenW) + "px; background-color: rgb(255, 255, 255); height: " + String.valueOf(screenH)
					+ "px;\" class=\"rxe-page rxe-page-paginated\">");
		}else{
			page_start.append("<div style=\"width: " + String.valueOf(screenW) + "px; height: " + String.valueOf(screenH)
					+ "px;\" class=\"rxe-page rxe-page-paginated\">");
		}
		page_start.append("<div>\n");

		return page_start.toString();
	}

//	public static String getPageStart(int screenW) {
//		StringBuffer page_start = new StringBuffer();
//
//		page_start.append("");
//
//		page_start.append("<div style=\"width: " + String.valueOf(screenW) + "px; background-color: rgb(255, 255, 255); height: 100%;\" class=\"rxe-page rxe-page-paginated\">");
//		page_start.append("<div>\n");
//
//		return page_start.toString();
//	}

	/*
	 * 보고서 페이지의 끝 부분
	 */
	public static String getPageEnd() {
		StringBuffer page_end = new StringBuffer();

		page_end.append("");

		page_end.append("\n</div>");
		page_end.append("</div>\n");

		return page_end.toString();
	}
}
