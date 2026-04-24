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

@SuppressWarnings("unused")
public class MainReport {
	private static final String separator = System.getProperty("file.separator");

	private final static Object lock = new Object();

	private static ConcurrentHashMap<String, MainReportMap> reports = new ConcurrentHashMap<String, MainReportMap>();

	private static final Log log = LogFactory.getLog(MainReport.class);

	public MainReport() throws RXException {
		throw new RXException("사용할 수 없는 생성자입니다.");
	}

	/**
	 * 메인리포트 처리
	 * 
	 * @param path
	 *            보고서 서식 경로
	 * @param fn
	 *            보고서 서식 파일 이름
	 * @throws RXException
	 */
	public MainReport(String path, String fn) throws RXException {
		MainReportMap reportmap = new MainReportMap();
		reportmap.setFullName(path + separator + fn + ".rxxml");
		reportmap.setIsCompiled(false);
		reportmap.setName(fn);
		reportmap.setReportPath(path);
		File f = new File(path + separator + fn + ".rxxml");
		reportmap.setVersionTimes(f.lastModified());

		if (reports.containsKey(fn) == false) {
			reports.put(fn, reportmap);
		}
		CompileLayout(fn);
	}

	/**
	 * 서식이 수정되었는지 검사한 후 수정된 경우 컴파일
	 * 
	 * @throws RXException
	 */
	public void Modified(String fn) throws RXException {
		try {
			CompileLayout(fn);
		} catch (Exception e) {
			throw new RXException(e);
		}
	}

	/**
	 * 서식 컴파일: 서식이 컴파일 되지 않았거나 수정된 경우 컴파일
	 * 
	 * @throws RXException
	 */
	private void CompileLayout(String fn) throws RXException {
		String fs = getReportMap(fn).getReportPath() + separator + getReportMap(fn).getName() + ".report";
		File file = new File(getReportMap(fn).getFullName());
		File reportFile = null;
		boolean needCompile;

		needCompile = false;
		if (FileExist(fs) == false) {
			needCompile = true;
		} else {
			reportFile = new File(fs);
		}

		if (needCompile == false) {
			if (getReportMap(fn).getIsCompiled() == false) {
				if (FileExist(fs) == false) {
					log.info("컴파일된 서식 없음: " + fn + ".report");
					needCompile = true;
				} else {
					reportFile = new File(fs);
					if (getReportMap(fn).getVersionTimes() > reportFile.lastModified()) {
						log.info("서식이 수정됨: " + fn + ".rxxml");
						needCompile = true;
					}
				}
			} else {
				if (getReportMap(fn).getVersionTimes() != file.lastModified()) {
					log.info("실행 중 서식이 수정됨: " + fn + ".rxxml");
					needCompile = true;
				}
			}
		}

		if (needCompile == true) {
			if (log.isInfoEnabled()) {
				log.info("서식 컴파일: " + fn + ".rxxml");
			}
			ReportExpressCompileManager.compileReportToFile(getReportMap(fn).getFullName(), getReportMap(fn).getReportPath() + separator + getReportMap(fn).getName() + ".report");
			getReportMap(fn).setIsCompiled(true);
			getReportMap(fn).setVersionTimes(file.lastModified());
		} else {
			if (log.isInfoEnabled()) {
				log.info("서식 변경 사항 없음: " + fn + ".rxxml");
			}
			getReportMap(fn).setIsCompiled(true);
		}
		BuildSubreport(getReportMap(fn).getReportPath(), fn);
	}

	/**
	 * 서브리포트가 있는 경우 서브리포트 컴파일
	 * 
	 * @throws RXException
	 */

	private void BuildSubreport(String path, String fn) throws RXException {
		try {
			File file = new File(getReportMap(fn).getFullName());

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			Element root = document.getDocumentElement();

			NodeList nodeList = root.getElementsByTagName("subreport");
			int len = nodeList.getLength();

			if (len < 1) {
				if (log.isInfoEnabled()) {
					log.info("서브리포트 없음");
				}
			} else {
				if (log.isInfoEnabled()) {
					log.info(len + "개의 서브리포트");
				}

				for (int i = 0; i < len; i++) {
					Element element = (Element) nodeList.item(i);
					String sname = element.getElementsByTagName("subreportExpression").item(0).getFirstChild().getNodeValue();

					sname = replaceAll(sname, "\"", "");
					sname = replaceAll(sname, "$P{SUBREPORT_DIR} + ", "");
					if (sname.indexOf("$P{") == -1) {
						sname = sname.substring(0, sname.lastIndexOf("."));
						CreateSubreport(path, sname);
					} else {
					}

				}
			}
		} catch (Exception e) {
			throw new RXException(e);
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

	private MainReportMap getReportMap(String key) {
		if (reports.containsKey(key) == true) {
			return reports.get(key);
		} else {
			return null;
		}
	}

	private void CreateSubreport(String path, String fn) throws RXException {
		MainReportMap reportmap = new MainReportMap();
		reportmap.setFullName(path + separator + fn + ".rxxml");
		reportmap.setIsCompiled(false);
		reportmap.setName(fn);
		reportmap.setReportPath(path);
		File f = new File(path + separator + fn + ".rxxml");
		reportmap.setVersionTimes(f.lastModified());

		if (reports.containsKey(fn) == false) {
			reports.put(fn, reportmap);
		}

		CompileLayout(fn);
	}

	private String replaceAll(String str, String pattern, String replace) {
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
