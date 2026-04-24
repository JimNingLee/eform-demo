<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="java.io.ByteArrayOutputStream"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.StringReader"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Locale"%>
<%@ page import="java.util.Map"%>
<%@ page import="javax.servlet.ServletContext"%>
<%@ page import="javax.xml.parsers.DocumentBuilder"%>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory"%>
<%@ page import="org.apache.commons.logging.Log"%>
<%@ page import="org.apache.commons.logging.LogFactory"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.xml.sax.InputSource"%>
<%@ page import="com.cabsoft.GlobalParams"%>
<%@ page import="com.cabsoft.RXSession"%>
<%@ page import="com.cabsoft.ecrypt.Base64"%>
<%@ page import="com.cabsoft.exporters.ExportPdf"%>
<%@ page import="com.cabsoft.fill.FillFactory"%>
<%@ page import="com.cabsoft.fill.RXFillListner"%>
<%@ page import="com.cabsoft.rx.engine.ReportExpress"%>
<%@ page import="com.cabsoft.rx.engine.ReportExpressPrint"%>
<%@ page import="com.cabsoft.rx.engine.RXException"%>
<%@ page import="com.cabsoft.rx.engine.RXParameter"%>
<%@ page import="com.cabsoft.rx.engine.query.RXXPathQueryExecuterFactory"%>
<%@ page import="com.cabsoft.rx.engine.util.RXLoader"%>
<%@ page import="com.cabsoft.utils.StringUtils"%>
<%@ page import="com.cabsoft.utils.SystemUtils"%>
<%@ page import="com.cabsoft.utils.StackTrace"%>
<%@ page import="com.cabsoft.utils.PdocUtil"%> <%-- PdocUtil 임포트 추가 --%>
<%@ page errorPage="../../error.jsp"%>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Log log = LogFactory.getLog("pdfviewer.getDocInfo");
    String jobID = (String) session.getAttribute("jobID");

    if (StringUtils.isEmpty(jobID) || !request.isRequestedSessionIdValid()) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.write("{\"head\":{\"cd\":4001,\"msg\":\"세션이 만료되었습니다.\"}}");
        return;
    }

    RXSession ss = (RXSession) session.getAttribute(jobID + "_session");
    if (ss == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"head\":{\"cd\":4002,\"msg\":\"문서 정보를 찾을 수 없습니다.\"}}");
        return;
    }

    try {
        GlobalParams globalParams = GlobalParams.getInstance();
        if (globalParams == null) {
            globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
        }

        ServletContext context = request.getSession().getServletContext();
        String filename = ss.getRptFile();
        String ReportXmlString = ss.getXmlData();

        // 서식 파일 경로
        String fs = StringUtils.nvl(
            context.getRealPath(globalParams.getContextpath() + "/reports/" + filename + ".report"), "");
        fs = SystemUtils.replaceSystemPathString(fs);

        File reportFile = new File(fs);
        if (!reportFile.exists()) {
            out.write("{\"head\":{\"cd\":4003,\"msg\":\"서식 파일을 찾을 수 없습니다: " + filename + "\"}}");
            return;
        }

        // 보고서 로드
        ReportExpress report = (ReportExpress) RXLoader.loadObject(fs);
        String subreport_dir = fs.substring(0, fs.lastIndexOf(SystemUtils.FILE_SEPARATOR)) + SystemUtils.FILE_SEPARATOR;

        // XML 파싱 (XXE 방어)
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new InputSource(new StringReader(ReportXmlString)));

        // 파라미터 구성
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RXXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
        params.put(RXXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
        params.put(RXXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
        params.put(RXXPathQueryExecuterFactory.XML_LOCALE, Locale.KOREAN);
        params.put(RXParameter.REPORT_LOCALE, Locale.US);
        params.put("SUBREPORT_DIR", subreport_dir);
        params.put("imgDir", context.getRealPath(globalParams.getImagepath()) + SystemUtils.FILE_SEPARATOR);
        params.put("jobID", jobID);

        // FillListner
        RXFillListner filllistner = new RXFillListner();
        filllistner.setSourceFile(filename);
        filllistner.setXml(ReportXmlString);
        filllistner.setEnableInterrupt(globalParams.getBooleanProperty("com.cabsoft.rx.interrupt", false));
        filllistner.setMaxPages(globalParams.getIntegerProperty("com.cabsoft.rx.maxpages", 0));
        filllistner.setTimeOut(globalParams.getIntegerProperty("com.cabsoft.rx.timeout", 0));
        params.put("FillListener", filllistner);

        ReportExpressPrint rxPrint = null;
        try {
            rxPrint = fill(report, params, document, fill_factory());
        } finally {
            filllistner.FillFinished();
        }

        // PDF 생성
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, Object> pdfParams = new HashMap<String, Object>();
        pdfParams.put("Process", "PDF");
        ExportPdf exportPdf = new ExportPdf();
        exportPdf.exportPdf("", baos, rxPrint, pdfParams);

        // 세션에 PDF 저장 (내보내기용)
        session.setAttribute(jobID + "_pdf", baos.toByteArray());

        // Base64 인코딩
        Base64 enc = new Base64();
        String pdfEncode = enc.encode(baos.toByteArray()).replaceAll("\\s+", "");

        // -----------------------------------------------------------
        // 수정 포인트: 직접 메서드 호출 대신 PdocUtil 사용
        // -----------------------------------------------------------
        PdocUtil pdocUtil = new PdocUtil();
        String retStr = pdocUtil.getDocInfo(rxPrint, pdfEncode);
        out.write(retStr);
        // -----------------------------------------------------------

    } catch (Exception e) {
        log.error(StackTrace.getStackTrace(e));
        String msg = (e.getMessage() != null ? e.getMessage() : "내부 오류").replace("\"","\\\"");
        out.write("{\"head\":{\"cd\":5000,\"msg\":\"" + msg + "\"}}");
    }
%>
<%!
    private FillFactory fill_factory() { return new FillFactory(); }

    private ReportExpressPrint fill(ReportExpress report, Map<String, Object> params,
            Document document, FillFactory fill) throws Exception {
        try {
            if (document != null) {
                return fill.fillReport(report, params);
            } else {
                return fill.fillReport(report, params, new com.cabsoft.rx.engine.RXEmptyDataSource());
            }
        } catch (com.cabsoft.rx.engine.RXException e) {
            throw new Exception("fill: " + e.getMessage());
        }
    }
%>