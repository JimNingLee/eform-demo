<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page errorPage="../error.jsp" %>
<%@ include file="../ess_beans/ess_HtmlView.jsp"%>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	
	String insuranceID = (String)request.getParameter("InsuranceID");
	String status = (String)request.getParameter("Status");
	String message = (String)request.getParameter("Message");
	
	System.out.println(">>>>>>>>>>>>>>  InsuranceID = " + insuranceID);
	System.out.println(">>>>>>>>>>>>>> Status = " + status);
	System.out.println(">>>>>>>>>>>>>> Message = " + message);
%>