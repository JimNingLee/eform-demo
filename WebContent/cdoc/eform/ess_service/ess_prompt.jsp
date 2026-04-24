<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page errorPage="../error.jsp" %>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	request.setCharacterEncoding("utf-8");

	String errUrl = "../error.jsp?msg=";
	String referer = request.getHeader("referer");
	String errmsg = "";
	if (!request.isRequestedSessionIdValid() ) {
		errmsg="(103)서버와의 연결(세션)이 종료되었습니다.";
%>
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>Prompt</title>
	<META http-equiv=Content-Type content="text/html; charset=utf-8" Cache-control="no-cache" Pragma="no-cache">
	<script>

	function WinOnload() {
		alert("<%=errmsg+","+request.isRequestedSessionIdValid()+","+referer%>");	
		self.close();
	}
	</script>
	</head>
	<body onload="WinOnload();">
	</body>
	</html>
<%
	} else {

%>
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>Prompt</title>
	<META http-equiv=Content-Type content="text/html; charset=utf-8" Cache-control="no-cache" Pragma="no-cache">
	<script>
	
	function doClick() {
		window.returnValue = document.getElementById("txtinput").value;
		self.close();
	
	}
	function WinOnload() {
		var arr = window.dialogArguments;
		document.getElementById("txtcommnet").innerHTML = arr[0];
		document.getElementById("txtinput").value = arr[1];
	}
	</script>
	</head>
	<body topmargin="20px" onload="WinOnload();">
	<center>
	<table  border="0">
		<tr>
			<td align="left"><span type="text" name="txtcommnet" id="txtcommnet" value=""></span></td>
		</tr>
		<tr>
			<td><input type="text" name="txtinput" id="txtinput" value="" style="ime-mode: active;"></input></td>
		</tr>
		<tr>
			<td><input type="button" value="확인" onclick='doClick();'></input> <input type="button" value="취소"
				onclick='doClick();'></input>
			</td>
		</tr>
	</table>
	</center>
	</body>
	</html>
<% } %>