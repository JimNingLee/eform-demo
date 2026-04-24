<%@ page language="java" contentType="image" %>
<%@ page language="java" import="com.cabsoft.image.ImageService"%>
<%
/*response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
*/
try{
	out.clear();
	out = pageContext.pushBody();
	
	String t = request.getParameter("t");
	ImageService img = new ImageService();
	img.service(request, response);
}catch(Exception e){
	out.println(e.toString());
}
%>