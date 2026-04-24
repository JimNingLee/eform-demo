package com.cabsoft.compile;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.rx.engine.RXException;

public class CompileManager {
	private static final Log log = LogFactory.getLog(CompileManager.class);

	private static ConcurrentHashMap<String, MainReport> compilers = new ConcurrentHashMap<String, MainReport>();

	private static CompileManager instance = null;

	private CompileManager() {

	}

	/**
	 * CompileManager 인스턴스 생성
	 * 
	 * @return
	 */
	public static CompileManager getInstance() {
		if (instance == null) {
			if (log.isInfoEnabled()) {
				log.info("Create new CompileManager");
			}
			instance = new CompileManager();
		}
		return instance;
	}

	/**
	 * 라이센스 인스턴스 초기화를 위해 컨텍스트의 리얼경로를 넘겨받아 처리한다.
	 * 
	 * @param fpath
	 * @return
	 */
	public static CompileManager getInstance(String fpath) {
		fpath = replaceSystemPathString(fpath);
		if (instance == null) {
			if (log.isInfoEnabled()) {
				log.info("Create new CompileManager");
			}
			instance = new CompileManager();
		}
		return instance;
	}

	/**
	 * 보고서 서식 컴파일
	 * 
	 * @param path
	 *            보고서 서식 경로
	 * @param fn
	 *            보고서 파일 이름
	 * @throws RXException
	 */
	public void CompileLayout(String path, String fn) throws RXException {
		path = replaceSystemPathString(path);
		String fs = path + System.getProperty("file.separator") + fn + ".rxxml";
		MainReport xmlfile = compilers.get(fs);

		if (xmlfile == null) {
			if (log.isInfoEnabled()) {
				log.info("새로 컴파일");
			}
			xmlfile = new MainReport(path, fn);
			compilers.put(fs, xmlfile);
		} else {
			if (log.isInfoEnabled()) {
				log.info("서식이 수정되었는지 검사");
			}
			xmlfile.Modified(fn);
			compilers.remove(fs);
			compilers.put(fs, xmlfile);
		}
	}

	private static String replaceSystemPathString(String src) {
		String ret = "";
		if (src == null || src.equalsIgnoreCase("")) {
			ret = "";
		} else {
			try {
				String FILE_SEPARATOR = System.getProperty("file.separator");
				ret = replaceAll(src, "\\", FILE_SEPARATOR);
				ret = replaceAll(ret, "/", FILE_SEPARATOR);
			} catch (Exception e) {
			}
		}
		return ret;
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
