<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page errorPage="../../error.jsp" %>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);

	String errUrl = "./error.jsp?msg=";
	if (!request.isRequestedSessionIdValid()) {
		response.sendRedirect(errUrl + java.net.URLEncoder.encode("timeout", "utf-8"));
	} else {
		response.sendRedirect(request.getContextPath() + "/cdoc/eform/homepage/pdfviewer/viewer.jsp");
	}
%>
