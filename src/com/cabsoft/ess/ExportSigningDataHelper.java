package com.cabsoft.ess;

public class ExportSigningDataHelper {
    /*
     * 보고서의 페이지 수와 페이지의 폭 그리고 높이를 픽셀 단위로 넘겨받아
     * 자바스크립트 변수에 할당한다.
     */
    public static String setMaxPages(int maxpages, int pageWidth, int pageHeight) {
        StringBuffer script = new StringBuffer();

        script.append("");

        /*
         * max_page, pageWidth, pageHeight는 include되는 자바스크립트에서
         * 정의한다.
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
    public static String getDocStart(int pageWidth, int pageHeight, int left_margin, int right_margin) {
        StringBuffer doc_start = new StringBuffer();

        doc_start.append("");

        /* 문서 시작 */

        doc_start.append("<div id=\"docs-editor-container\">");
        doc_start.append("<div id=\"docs-editor\">");
        doc_start.append("<div dir=\"ltr\" id=\"rxe-appview\" class=\"rxe-appview\">");
        doc_start.append("<div id=\"paper\" class=\"paper\">");
        doc_start.append("<div id=\"canvas\" style=\"overflow-y: scroll; height: " + String.valueOf(pageHeight) + "px;\" class=\"rxe-appview-editor\">");
        doc_start.append("<div id=\"doc\" style=\"left: 55px; top: 10px; width:" + pageWidth + "px; \" class=\"rxe-paginateddocumentplugin\">");

        return doc_start.toString();
    }

    /*
     * 문서 스타일 정의의 끝 및 HTML 문서의 끝
     */
    public static String getDocEnd() {
        StringBuffer doc_end = new StringBuffer();

        doc_end.append("");

        /* 문서 끝 */
        doc_end.append("<div id=\"send_div\" name=\"send_div\">");
        doc_end.append("</div>");
        doc_end.append("</div>");
        doc_end.append("</div>");
        doc_end.append("</div>");
        doc_end.append("</div>");
        doc_end.append("</div>");

        /*
         * 문서 다운로드를 위한 것으로 스마트폰에서는 적용되지 않으므로
         * 현재는 사용하지 않는다.

        doc_end.append("<iframe id=\"submitFrame\" style=\"display:none\"></iframe>");
        doc_end.append("</form>");
        doc_end.append("</body>");
        doc_end.append("</html>");
*/
        return doc_end.toString();
    }

    /*
     * 보고서 페이지의 시작 부분
     */
    public static String getPageStart(int screenW, int screenH) {
        StringBuffer page_start = new StringBuffer();

        page_start.append("");

        page_start.append("<div style=\"width: " + String.valueOf(screenW) + "px; background-color: rgb(255, 255, 255); height: 100%;\" class=\"rxe-page rxe-page-paginated\">");
        page_start.append("<div>\n");

        return page_start.toString();
    }

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
