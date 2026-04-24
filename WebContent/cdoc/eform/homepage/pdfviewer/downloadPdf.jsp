<%@ page language="java" contentType="application/pdf" pageEncoding="UTF-8"%>
<%@ page import="com.cabsoft.utils.StringUtils"%>
<%@ page import="com.cabsoft.RXSession"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page errorPage="../../error.jsp"%>
<%
    String jobID = request.getParameter("jobID");
    if (StringUtils.isEmpty(jobID)) jobID = (String) session.getAttribute("jobID");

    if (!request.isRequestedSessionIdValid() || StringUtils.isEmpty(jobID)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "세션 만료");
        return;
    }

    byte[] pdfBytes = (byte[]) session.getAttribute(jobID + "_pdf");
    if (pdfBytes == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "PDF 데이터 없음. 먼저 뷰어를 열어주세요.");
        return;
    }

    RXSession ss = (RXSession) session.getAttribute(jobID + "_session");
    String filename = (ss != null && !StringUtils.isEmpty(ss.getRptFile())) ? ss.getRptFile() : "document";

    String ua = request.getHeader("User-Agent");
    String encodedName;
    if (ua != null && ua.contains("MSIE") || (ua != null && ua.contains("Trident"))) {
        encodedName = URLEncoder.encode(filename + ".pdf", "UTF-8").replaceAll("\\+", "%20");
    } else {
        encodedName = new String((filename + ".pdf").getBytes("UTF-8"), "ISO-8859-1");
    }

    response.setContentType("application/pdf");
    response.setContentLength(pdfBytes.length);
    response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragma", "no-cache");

    response.getOutputStream().write(pdfBytes);
    response.getOutputStream().flush();
%>
