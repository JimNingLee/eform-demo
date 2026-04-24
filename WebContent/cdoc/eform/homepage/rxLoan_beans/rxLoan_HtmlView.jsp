<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="java.io.File"%>
<%@ page language="java" import="java.io.FileNotFoundException"%>
<%@ page language="java" import="java.io.IOException"%>
<%@ page language="java" import="java.io.StringReader"%>
<%@ page language="java" import="java.util.HashMap"%>
<%@ page language="java" import="java.util.Locale"%>
<%@ page language="java" import="java.util.Map"%>

<%@ page language="java" import="javax.servlet.ServletContext"%>
<%@ page language="java" import="javax.servlet.ServletException"%>
<%@ page language="java" import="javax.servlet.http.HttpServletRequest"%>
<%@ page language="java" import="javax.servlet.http.HttpServletResponse"%>
<%@ page language="java" import="javax.xml.parsers.DocumentBuilderFactory"%>

<%@ page language="java" import="org.apache.commons.logging.Log"%>
<%@ page language="java" import="org.apache.commons.logging.LogFactory"%>
<%@ page language="java" import="org.w3c.dom.Document"%>
<%@ page language="java" import="org.xml.sax.InputSource"%>

<%@ page language="java" import="com.cabsoft.GlobalParams"%>
<%@ page language="java" import="com.cabsoft.rx.engine.ReportExpress"%>
<%@ page language="java" import="com.cabsoft.fill.FillFactory"%>
<%@ page language="java" import="com.cabsoft.fill.RXFillListner"%>
<%@ page language="java" import="com.cabsoft.ess.ExportEss"%>
<%@ page language="java" import="com.cabsoft.rx.engine.RXException"%>
<%@ page language="java" import="com.cabsoft.rx.engine.RXParameter"%>
<%@ page language="java" import="com.cabsoft.rx.engine.ReportExpressPrint"%>
<%@ page language="java" import="com.cabsoft.rx.engine.query.RXXPathQueryExecuterFactory"%>
<%@ page language="java" import="com.cabsoft.utils.Files"%>
<%@ page language="java" import="com.cabsoft.utils.RXDomDocument"%>
<%@ page language="java" import="com.cabsoft.utils.StackTrace"%>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SystemUtils"%>
<%@ page language="java" import="com.cabsoft.utils.sysinfo"%>
<%@ page language="java" import="com.cabsoft.utils.objSerializer"%>
<%@ page language="java" import="com.cabsoft.RXSession"%>

<%@ include file="rxLoan_header.jsp"%>

<%!@SuppressWarnings("deprecation")
	public class essService {

		private final Log log = LogFactory.getLog(essService.class);

		GlobalParams globalParams;
		private boolean useOverlap = false;
		private boolean devel = false;

		public void setUseOverlap(boolean useOverlap) {
			this.useOverlap = useOverlap;
		}

		public void setDevel(boolean devel) {
			this.devel = devel;
		}

		/**
		 * 보고서를 채움 보고서를 채우기 전에 바코드가 저장될 위치와 파일의 접두사를 정의하기 위해 InitBarcode() 함수 호출
		 * 
		 * @return ReportExpressPrint
		 * @throws Exception
		 */
		private ReportExpressPrint FillReport(String jobID, HttpServletRequest request, RXSession ss) throws Exception {
			HttpSession session = request.getSession();
			ServletContext context = request.getSession().getServletContext();
			String filename = ss.getRptFile();
			String xmlType = (String) request.getParameter("xmlType");
			String ReportXmlString = ss.getXmlData();

			FillFactory fill = new FillFactory();

			String fs = StringUtils.nvl(
					context.getRealPath(globalParams.getContextpath() + "/reports/" + filename + ".report"), "");
			fs = SystemUtils.replaceSystemPathString(fs);

			/*
			 * Fill Listner 설정
			 */
			RXFillListner filllistner = new RXFillListner();

			try {
				File reportFile = new File(fs);
				if (!reportFile.exists()) {
					String fsp = SystemUtils.FILE_SEPARATOR;
					String l_fs = fs.substring(fs.lastIndexOf(fsp)+1);
					l_fs = l_fs.substring(0, l_fs.lastIndexOf("."));
					throw new Exception("layoutfile$" + l_fs);
// 					throw new Exception("컴파일된 보고서 파일 " + fs + "을(를) 찾을 수 없습니다. 먼저 보고서 서식을 컴파일하기시 바랍니다.");
				}

				Document document = null;
				if ("xmlstring".equals(xmlType)) {
					log.debug("ReportXmlString:" + ReportXmlString);
					document = RXDomDocument.parse(ReportXmlString);
				} else {
					String xmlfilepath = "/WEB-INF/properties/cabsoft/xml/" + filename + ".xml";
					String xmlfile = (String) request.getParameter("xmlfile");
					if (xmlfile != null && !"".equals(xmlfile) && xmlfile.indexOf(".xml") > 0) {
						xmlfilepath = "/WEB-INF/properties/cabsoft/xml/" + xmlfile;
					}
					byte[] bxml = Files.readFile(context.getRealPath(xmlfilepath));
					ReportXmlString = new String(bxml, "euc-kr");
					InputSource is = new InputSource(new StringReader(ReportXmlString));
					System.err.println(">>>> ReportXmlString: " + ReportXmlString);
					document = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
					ss.setXmlData(ReportXmlString);
				}

				Map<String, Object> params = new HashMap<String, Object>();

				params.put(RXXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
				params.put(RXXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
				params.put(RXXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
				params.put(RXXPathQueryExecuterFactory.XML_LOCALE, Locale.KOREAN);
				params.put(RXParameter.REPORT_LOCALE, Locale.US);
				String subreport_dir = fs.substring(0, fs.lastIndexOf(SystemUtils.FILE_SEPARATOR))+ SystemUtils.FILE_SEPARATOR;
				params.put("SUBREPORT_DIR", subreport_dir);
				log.debug("SUBREPORT_DIR:" + subreport_dir);
				//서식폴더로 변경(/image/)
				//params.put("imgDir", context.getRealPath(globalParams.getImagepath()) + "/");
				params.put("imgDir", context.getRealPath(globalParams.getImagepath()) + "/");
				log.debug("imgDir:" + context.getRealPath(globalParams.getImagepath()) + "/");
				params.put("jobID", jobID);

				/*
				 * Fill Listner 설정
				 */
				filllistner.setSourceFile(filename);
				filllistner.setXml(ReportXmlString);
				filllistner.setEnableInterrupt(globalParams.getBooleanProperty("com.cabsoft.rx.interrupt", false));
				filllistner.setMaxPages(globalParams.getIntegerProperty("com.cabsoft.rx.maxpages", 0));
				filllistner.setTimeOut(globalParams.getIntegerProperty("com.cabsoft.rx.timeout", 0));
				params.put("FillListener", filllistner);

				if (filename.endsWith("_cert")) {
					params.put("IssuerID", "발급번호: " + jobID);
				}

				return fill.fillReport(fs, params);
			} catch (RXException e) {
				e.printStackTrace();
				log.error(e);
				throw new Exception("fill");
			} finally {
				filllistner.FillFinished();
			}
		}

		public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
				FileNotFoundException, IOException {

			System.err.println(">>> call rxLoan_HtmlView.jsp");

			request.setCharacterEncoding("utf-8");
			HttpSession session = request.getSession();
			String jobID = (String) session.getAttribute("jobID");
			RXSession ss = (RXSession) session.getAttribute(jobID + "_session");

			String signType = ss.getSignType();
			String rptTitle = StringUtils.nvl(request.getParameter("rptTitle"), "");
			boolean isSessionData = "true".equals(StringUtils.nvl(request.getParameter("isSessionData"), ""));

			try {
				globalParams = GlobalParams.getInstance();

				if (globalParams == null) {
					globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
				}

				// 보고서 생성
				ReportExpressPrint rxPrint = null;
				String rxprintData = ss.getRxprintData();

				if (isSessionData) {
					rxPrint = (ReportExpressPrint) com.cabsoft.utils.objLoader.loadObjectFromCompressedBase64(rxprintData);
				} else {
					System.err.println(">>> call rxLoan_HtmlView.jsp FillReport");
					rxPrint = FillReport(jobID, request, ss);
					System.err.println(">>> call rxLoan_HtmlView.jsp FillReport End");

					rxprintData = objSerializer.ObjectToCompress(rxPrint);
					ss.setRxprintData(rxprintData);
				}

				// 외부 리포트 명이 없는 경우 서식의 Report Name 적용
				if (StringUtils.isEmpty(rptTitle)) {
					rptTitle = rxPrint.getName();
				}

				String ua = request.getHeader("User-Agent");
				String os = sysinfo.os(ua);
				String bw = sysinfo.bw(ua);
				float ie_version = sysinfo.version(ua);

				String eformpath = (String) request.getContextPath() + globalParams.getContextpath();
				String basehref = (String) request.getContextPath() + globalParams.getContextpath() + "essviewer/";
				String exptFileName = (String) StringUtils.nvl(request.getParameter("exptFileName"), "report");
				String header = "";
				String webimgurl = (String) globalParams.getProperty("com.cabsoft.rx.webimgurl");
				EssHeaderBeans headerBeans = new EssHeaderBeans();
				headerBeans.setIEVersion(ie_version);
				headerBeans.setUseAbsolutePosition(true);
				log.debug("ie_version:"+ie_version);
				headerBeans.setSession(request.getSession());
				headerBeans.setDevel(devel);

				if("firefox".equals(bw) || "chrome".equals(bw) || "opera".equals(bw)){
					headerBeans.setBroswer("firefox");
				}else{
					headerBeans.setBroswer(bw);
				}

				String layerYn = "true";
				header = headerBeans.MenuStart("essViewer", basehref, rptTitle, "UTF-8", jobID, exptFileName, "ess.js", "form.js", signType, session.getId(), layerYn);
				header += headerBeans.MenuEnd(basehref);
				header += headerBeans.addCertKey(eformpath);
				header += headerBeans.downloadForm(jobID, exptFileName, ss.getUserpwd());

				HashMap<String, Object> hmap = new HashMap<String, Object>();
				hmap.put("image_uri", request.getContextPath() + globalParams.getContextpath()
						+ "imgservice.jsp?jobID=" + jobID + "&image=");

				if (StringUtils.isEmpty(webimgurl)) {
					hmap.put("webimgurl", null);
				} else {
					hmap.put("webimgurl", request.getContextPath() + webimgurl);
				}

				hmap.put("header", header);

				hmap.put("previewOnly", false);

				session.setAttribute(jobID + "_session", ss);

				request.setCharacterEncoding("UTF-8");
				response.setContentType("text/html;charset=UTF-8");

				ExportEss.Ess(jobID, request, response, rxPrint, hmap, useOverlap);

			} catch (Exception e) {
				e.printStackTrace();
				String se = StackTrace.getStackTrace(e);
				if (se.indexOf("java.net.SocketException") > -1) {

				} else {
					log.error(StackTrace.getStackTrace(e));
					throw new ServletException(e);
				}
			}
		}

	}%>
