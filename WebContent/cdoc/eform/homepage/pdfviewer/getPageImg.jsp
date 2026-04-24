<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.ByteArrayOutputStream"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.fasterxml.jackson.core.type.TypeReference"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@ page import="com.cabsoft.ecrypt.Base64"%>
<%@ page import="com.cabsoft.pdoc.loader.CabPdfLoader"%>
<%@ page import="com.cabsoft.pdoc.pdf.img.PdfImageConverter"%>
<%@ page import="com.cabsoft.utils.StackTrace"%>
<%@ page import="org.apache.pdfbox.pdmodel.PDDocument"%>
<%@ page import="org.apache.commons.logging.Log"%>
<%@ page import="org.apache.commons.logging.LogFactory"%>
<%@ page errorPage="../../error.jsp"%>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Log log = LogFactory.getLog("pdfviewer.getPageImg");
    PDDocument pdf = null;

    try {
        // POST body JSON 파싱
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(sb.toString(), new TypeReference<Map<String, Object>>(){});

        float zoom    = Float.parseFloat(body.get("zoom").toString());
        int pageIdx   = Integer.parseInt(body.get("pageIdx").toString());
        String pdfen  = (String) body.get("pdfen");

        // Base64 디코딩 → PDF 로드
        Base64 dec = new Base64();
        byte[] pdfBytes = dec.decode2byte(pdfen);
        pdf = CabPdfLoader.loadPdfByteArray(pdfBytes, null);

        // 페이지 → PNG 변환
        byte[] pageImgBytes = PdfImageConverter.convertPngImg(pageIdx - 1, zoom, 2, pdf);
        String base64Img = Base64.encode(pageImgBytes, false);

        out.clear();
        out = pageContext.pushBody();
        out.write("{\"head\":{\"cd\":2000},\"data\":{\"pageImg\":\"" + base64Img + "\",\"pageIdx\":" + pageIdx + "}}");

    } catch (Exception e) {
        log.error(StackTrace.getStackTrace(e));
        out.clear();
        out = pageContext.pushBody();
        String msg = (e.getMessage() != null ? e.getMessage() : "이미지 변환 오류").replace("\"","\\\"");
        out.write("{\"head\":{\"cd\":5000,\"msg\":\"" + msg + "\"}}");
    } finally {
        if (pdf != null) {
            try { pdf.close(); } catch (Exception ignore) {}
        }
    }
%>
