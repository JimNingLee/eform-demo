<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SystemUtils"%>
<%@ page language="java" import="com.cabsoft.RXSession"%>
<%@ page errorPage="../error.jsp" %>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	request.setCharacterEncoding("utf-8");

	String errUrl = "../error.jsp?msg=";
	if (!request.isRequestedSessionIdValid()) {
		response.sendRedirect(errUrl + java.net.URLEncoder.encode("timeout", "utf-8"));
	} else {
		
		// 이전 jobID 제거
		String jobID = (String) session.getAttribute("jobID");
		if (!StringUtils.isEmpty(jobID)) {
			session.setAttribute(jobID + "_session", null);
			session.removeAttribute(jobID + "_session");
			session.setAttribute("jobID", null);
			session.removeAttribute("jobID");
		}

		//jobid생성
		jobID = SystemUtils.GenerateID();
		session.setAttribute("jobID", jobID);

		String servlet = (String) request.getParameter("Servlet");

		// html 뷰어로 전달
		String signtype = (String) request.getParameter("signtype");
		String filename = (String) request.getParameter("ReportFile");
		String process = (String) request.getParameter("Process");
		String rptTitle = (String) request.getParameter("rptTitle");
		String xmlfile = (String) request.getParameter("xmlfile");
		String xmlType = ""; //xmlstring, 그외 
		
		// Layer 여부
		String n_isLayer	= StringUtils.nvl(request.getParameter("n_isLayer"), "false");
		
		signtype = signtype==null ? "" : signtype;

		// 보고서 설정값 설정
		RXSession ss = new RXSession();
		ss.setJobID(jobID);
		ss.setRxprintData("");
		ss.setIssueID("");
		ss.setRptFile(filename);
		ss.setXmlData("");
		ss.setPdfData(null);
		ss.setUserpwd("");
		ss.setPageView(null);
		ss.setJson(null);
		
		ss.setCustomer("홍길동");
		ss.setProduct("전자문서");
		ss.setSignType(signtype);

		session.setAttribute(jobID + "_session", ss);
%>
<html>
<head>
<META http-equiv=Content-Type content="text/html; charset=utf-8" Cache-control="no-cache" Pragma="no-cache">
<link href="../loading/jquery.loadmask.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../js/jquery-1.9.1.js"></script>
<script type="text/javascript" src="../loading/jquery-latest.pack.js"></script>
<script type='text/javascript' src='../loading/jquery.loadmask.js'></script>
<style type="text/css">
body {
	font-size: 11px;
	font-family: tahoma;
}

#content {
	padding: 5px;
	width: 200px;
}

#buttons {
	padding-left: 40px;
}
</style>
<script language="javascript">
	$(document).ready(function() {
		$("#body").mask("<%=rptTitle%> 생성 중 ...");
	} );
	
	function makeForm(url){
		var f = document.createElement("form");
	    f.setAttribute("method", "post");
	    f.setAttribute("action", url);
	    document.body.appendChild(f);
	      
	    return f;
	}
	
	function addData(name, value){
		var i = document.createElement("input");
		i.setAttribute("type","hidden");
		i.setAttribute("name",name);
		i.setAttribute("value",value);
		return i;
	}
	
	window.onload = function(){
		f = makeForm('<%=servlet%>');
		f.appendChild(addData('ReportFile', '<%=filename%>'));
		f.appendChild(addData('Process', '<%=process%>'));
		f.appendChild(addData('rptTitle', '<%=rptTitle%>'));
		f.appendChild(addData('xmlType', '<%=xmlType%>'));
		f.appendChild(addData('xmlfile', '<%=xmlfile%>'));
		f.appendChild(addData('n_isLayer', '<%=n_isLayer%>'));
		f.submit();
	}
</script>

</head>
<body id="body" leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0">

	<table cellspacing="0" cellpadding="0" align="center" style="height: 100%;">
		<tr>
			<td></td>
		</tr>
	</table>
</body>
</html>
<%
	}
%>
