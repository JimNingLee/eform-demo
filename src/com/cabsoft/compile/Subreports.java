package com.cabsoft.compile;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.ReportExpressCompileManager;

public class Subreports {
	private static final String separator = System.getProperty("file.separator");
	private static String ReportPath = null;
	private static String FullName = null;
	private static String Name = null;
	private static Long VersionTimes = null;
	private static boolean IsCompiled = false;
	private static String[] SubsubreportName = null;
	private static int Depth = -1;
	private static ConcurrentHashMap<String, Subreports> Subsubreports = new ConcurrentHashMap<String, Subreports>();

	private static final Log log = LogFactory.getLog(Subreports.class);

	@SuppressWarnings("unused")
	private Subreports() throws RXException {
		throw new RXException("사용할 수 없는 생성자입니다.");
	}

	/**
	 * 서브리포트 처리
	 * 
	 * @param path
	 *            서브리포트 서식 경로
	 * @param fn
	 *            서브리포트 서식 이름
	 * @throws RXException
	 */
	public Subreports(String path, String fn) throws RXException {
		setReportPath(path);
		setFullName(path + separator + fn + ".rxxml");
		setName(fn);
		CompileSubreport();
	}

	/**
	 * 서브리포트 컴파일
	 * 
	 * @throws RXException
	 */
	private void CompileSubreport() throws RXException {
		File file = new File(FullName);
		if (IsCompiled == false || FileExist(ReportPath + separator + Name + ".report") == false || VersionTimes != file.lastModified()) {
			if (log.isInfoEnabled()) {
				log.info("서브리포트 컴파일: " + Name);
			}
			ReportExpressCompileManager.compileReportToFile(FullName, ReportPath + separator + Name + ".report");
			VersionTimes = file.lastModified();
			IsCompiled = true;
			Depth = 1;
		} else {
			if (log.isInfoEnabled()) {
				log.info("서브리포트 변경 사항 없음: " + Name);
			}
		}
		BuildSubsubreport();
	}

	/**
	 * 서브서브리포트가 있는 경우 서브서브리포트 컴파일
	 * 
	 * @throws RXException
	 */
	@SuppressWarnings("static-access")
	private synchronized void BuildSubsubreport() throws RXException {
		NodeList nodeList = null;
		File file = null;
		try {
			file = new File(FullName);

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			Element root = document.getDocumentElement();
			nodeList = root.getElementsByTagName("subreport");
		} catch (Exception e) {
			throw new RXException(e);
		}

		int len = nodeList.getLength();
		if (len < 1) {
			if (log.isInfoEnabled()) {
				log.info("서브서브리포트 없음.");
			}
			Subsubreports.clear();
		} else {
			if (log.isInfoEnabled()) {
				log.info(len + "개의 서브서브리포트");
			}
			Subsubreports.clear();
			SubsubreportName = new String[len];

			for (int i = 0; i < len; i++) {
				Element element = (Element) nodeList.item(i);
				String subsubreportName = element.getElementsByTagName("subreportExpression").item(0).getFirstChild().getNodeValue();
				subsubreportName = replaceAll(subsubreportName, "\"", "");
				subsubreportName = replaceAll(subsubreportName, "$P{SUBREPORT_DIR} + ", "");
				subsubreportName = subsubreportName.substring(0, subsubreportName.lastIndexOf("."));
				if (log.isInfoEnabled()) {
					log.info("서브서브리포트:" + SubsubreportName[i]);
				}
				SubsubreportName[i] = ReportPath + separator + subsubreportName + ".rxxml";
				String creport = ReportPath + separator + subsubreportName + ".report";
				Subreports sub = Subsubreports.get(SubsubreportName[i]);

				if (sub == null || sub.getCompiled() == false || FileExist(creport) == false || file.lastModified() != sub.getVersionTimes()) {
					log.debug("서브서브리포트 컴파일");
					sub = new Subreports(ReportPath, subsubreportName);
					sub.setDepth(Depth++);
					sub.setFullName(SubsubreportName[i]);
					sub.setCompiled(true);
					sub.setName(subsubreportName);
					sub.setReportPath(ReportPath);
					sub.setVersionTimes(file.lastModified());
					Subsubreports.put(SubsubreportName[i], sub);
				} else {
					if (log.isInfoEnabled()) {
						log.debug("이미 컴파일된 서브서브리포트:" + SubsubreportName[i]);
					}
				}
			}
		}

	}

	private boolean FileExist(final String fn) {
		try {
			File file = new File(fn);
			if (file.exists() == false) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static String getReportPath() {
		return ReportPath;
	}

	public static void setReportPath(String reportPath) {
		ReportPath = reportPath;
	}

	public static String getFullName() {
		return FullName;
	}

	public static void setFullName(String fullName) {
		FullName = fullName;
	}

	public static String getName() {
		return Name;
	}

	public static void setName(String name) {
		Name = name;
	}

	public static Long getVersionTimes() {
		return VersionTimes;
	}

	public static void setVersionTimes(Long versionTimes) {
		VersionTimes = versionTimes;
	}

	public static boolean getCompiled() {
		return IsCompiled;
	}

	public static void setCompiled(boolean isCompiled) {
		IsCompiled = isCompiled;
	}

	public static int getDepth() {
		return Depth;
	}

	public static void setDepth(int depth) {
		Depth = depth;
	}

	private static String replaceAll(String str, String pattern, String replace) {
		int s = 0;
		int e = 0;

		StringBuilder result = new StringBuilder();

		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

}
