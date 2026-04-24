package com.cabsoft.utils;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.cabsoft.rx.engine.util.RXXmlUtils;

public final class RXDomDocument {

	/**
	 * 입력된 XML 문자열을 XML 도큐먼트로 파싱한다.
	 * 
	 * @param xml
	 *            입력 문자열
	 * @return 파싱된 XML 도큐먼트
	 * @throws Exception
	 */
	public static Document parse(String xml) throws Exception {
		InputSource is = new InputSource(new StringReader(xml));
		return RXXmlUtils.parse(is);
	}

	/**
	 * 파일을 XML 도큐먼트로 파싱한다.
	 * 
	 * @param file
	 *            XML 파일
	 * @return 파싱된 XML 도큐먼트
	 * @throws Exception
	 */
	public static Document parse(File file) throws Exception {
		return RXXmlUtils.parse(file);
	}

	/**
	 * URL 스트림을 XML 도큐먼트로 파싱한다.
	 * 
	 * @param url
	 *            URL
	 * @return 파싱된 XML 도큐먼트
	 * @throws Exception
	 */
	public static Document parse(URL url) throws Exception {
		return RXXmlUtils.parse(url);
	}

	/**
	 * 입력 소스를 XML 도큐먼트로 파싱한다.
	 * 
	 * @param is
	 *            입력 소스
	 * @return 파싱된 XML 도큐먼트
	 * @throws Exception
	 */
	public static Document parse(InputSource is) throws Exception {
		return RXXmlUtils.parse(is);
	}

	/**
	 * 입력 스트림을 XML 도큐먼트로 파싱한다.
	 * 
	 * @param is
	 *            입력스트림
	 * @return 파싱된 XML 도큐먼트
	 * @throws Exception
	 */
	public static Document parse(InputStream is) throws Exception {
		return RXXmlUtils.parse(is);
	}
}
