<%@ page language="java" contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.ErrorCode"%>
<%@ page language="java" import="com.cabsoft.GlobalParams"%>
<%
request.setCharacterEncoding("utf-8");
String errorfs = GlobalParams.getInstance(request.getRealPath("/WEB-INF/")).getErrorCode();
ErrorCode errorCode = ErrorCode.getInstance(errorfs);
String title = errorCode.getConfigManager().getValue("title");
String code = "";
String s_code = (String)request.getParameter("msg");
if(s_code!=null){
	s_code = s_code.trim();
	s_code = new String(s_code.getBytes("8859_1"), "UTF-8");
	String l_fs = "";
	if(s_code.indexOf("$")>0){
		l_fs = s_code.substring(s_code.indexOf("$")+1);
		code = s_code.substring(0, s_code.indexOf("$"));
	}else{
		code = s_code;
	}
	String msg = errorCode.getConfigManager().getValue(code);
	
	if(msg==null || msg.equals("")) {
		msg = new String(new String(code.getBytes("8859_1"), "UTF-8"));;
	}else{
		if("layoutfile".equals(code)){
			msg = msg + "<br/><br/><b>서식 이름: [" + l_fs + "]</b>";
		}
	}
	
	String flag = (String)request.getParameter("flag");
	String errMsg = title + "<br/><br/>" + msg;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="ko">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
<link href="./css/basic.css" rel="stylesheet" type="text/css"/>
<title>ReportExpress Error Page</title>
<style type="text/css">
p.overf {   
	background-color: #FFFFFF;  
	width: 390px; height: 100px;  
	border : none;  
	overflow : auto;  
	SCROLLBAR-FACE-COLOR: #aad988;  
	SCROLLBAR-HIGHLIGHT-COLOR: #aad988;  
	SCROLLBAR-SHADOW-COLOR: #80000f;  
	SCROLLBAR-3DLIGHT-COLOR: #80000f;  
	SCROLLBAR-ARROW-COLOR: #8000f;  
	SCROLLBAR-TRACK-COLOR: #aad9ff;  
	SCROLLBAR-DARKSHADOW-COLOR: #aad988;  
	SCROLLBAR-BASE-COLOR: #aad988;  
	font-family: 맑은 고딕, Arial;  
	font-size: 12px;  
	color: rgb(0,0,0);  
	padding:10px;
}
</style>
</head>
<body>
<table cellspacing="0" cellpadding="0" align="center" style="height:100%;">
<tr><td>
<!-- Background Layers -->
 <table width="500" cellpadding="0" cellspacing="0">
  <tr style="height:29px;">
   <td><img src="./images/box_left_081.gif" align="middle" alt=""/></td>
   <td width="100%" style="background:url(./images/box_bg_081.gif) repeat-x; padding:0px 0 0 0px; color:#ffffff;">
    <b>ReportExpress 처리 중 오류</b>
   </td>
   <td style="background:url(./images/box_bg_081.gif) repeat-x; color:#ffffff;">
   </td>
   <td><img src="./images/box_right_081.gif" align="middle" alt=""/></td>
  </tr>
 </table>

 <table width="500" cellpadding="0" cellspacing="0" style="border:1px #aaaaaa solid; border-top:none;" bgcolor="#f6f6f6" >
  <tr>
   <td style="padding:8px;">
   <table width="100%" cellpadding="0" cellspacing="0" bgcolor="#ffffff">
    <tr>
     <td><img src="./images/box_left_top_081.gif" align="middle" alt=""/></td>
     <td width="100%" style="background:url(./images/box_line_081.gif) top repeat-x;"></td>
     <td><img src="./images/box_right_top_081.gif" align="middle" alt=""/></td>
    </tr>
    <tr>
     <td style="background:url(./images/box_line_081.gif) left repeat-y;"></td>
     <td width="100%" style="color:#555555; line-height:1.4em;">
     <!-- 실 내용 -->

        <table border="0" bgcolor="#C9E2E2" cellpadding="0" cellspacing="1" width="100%">
        <tr bgcolor="#C9E2E2" style="height:30px">
            <td align="left" width="100%">&nbsp;&nbsp;<b>오류 내용</b></td>
        </tr>
        <tr bgcolor="#FFFFFF" style="height:100%">
            <td align="left" width="100%">
            	<p class="overf" align="justify" style="width:430px; height:300px;"><%=errMsg%></p>
            </td>
        </tr>
        </table>

     <!-- //실 내용 -->
     </td>
     <td style="background:url(./images/box_line_081.gif) right repeat-y;"></td>
    </tr>
    <tr>
     <td><img src="./images/box_left_btm_081.gif" align="middle"alt=""/></td>
     <td width="100%" style="background:url(./images/box_line_081.gif) bottom repeat-x;"></td>
     <td><img src="./images/box_right_btm_081.gif" align="middle" alt=""/></td>
    </tr>
   </table>
   </td>
  </tr>
 </table>
</td></tr></table>
</body>
</html>
<%}%>