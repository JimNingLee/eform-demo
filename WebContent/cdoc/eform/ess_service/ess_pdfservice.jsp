<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="java.net.URLEncoder"%>
<%@ page errorPage="../error.jsp" %>
<%@ include file="../ess_beans/ess_PDFExportService.jsp"%>
<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
String errUrl = "../error.jsp?msg=";
if (!request.isRequestedSessionIdValid()) {
	response.sendRedirect(errUrl + java.net.URLEncoder.encode("timeout", "utf-8"));
} else {
	try {
		out.clear();
		out = pageContext.pushBody();

		EssPdfExport service = new EssPdfExport();
		service.export(request, response);
	} catch (Exception e) {
		e.printStackTrace();
		response.sendRedirect(errUrl + java.net.URLEncoder.encode(e.getMessage(), "utf-8"));
	}
}
%>