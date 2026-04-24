package com.cabsoft.smartcert.mobile;

import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXImageRenderer;
import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintFrame;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintPage;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXRenderable;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.type.OnErrorTypeEnum;
import com.cabsoft.rx.engine.util.RXProperties;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("deprecation")
public class ApplySmartCode {
	private static final Log log = LogFactory.getLog(ApplySmartCode.class);

	private static final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public static final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	public static final String PROPERTY_HTML_RADIOVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "radiovalue";

	ReportExpressPrint print = null;
	Map<String, Object> mParams = new HashMap<String, Object>();

	public ApplySmartCode(ReportExpressPrint print) {
		this.print = print;
		this.mParams = null;
	}

	/**
	 * 프레임 클래스인 경우 재귀 호출에 의해 수정한다.
	 * 
	 * @param frame
	 * @param params
	 * @throws RXException
	 */
	private void exportFrame(RXPrintFrame frame, Map<String, Object> params) throws RXException {
		List<RXPrintElement> elements = frame.getElements();
		for (int i = 0; i < elements.size(); i++) {
			RXPrintElement element = ((RXPrintElement) elements.get(i));
			if (element instanceof RXPrintFrame) {
				exportFrame((RXPrintFrame) element, params);
			} else {
				String html_id = RXProperties.getProperty(element, PROPERTY_HTML_ID);
				if (html_id != null && !html_id.equalsIgnoreCase("") && params.containsKey(html_id)) {
					if (element instanceof RXPrintImage) {
						/*
						 * Html 태그 입력에서 정의된 이미지로 교체한다. 교체할 이미지 파일은 HashMap에
						 * com.cabsoft.rx.export.html.id를 키로 정의되어 있다.
						 */
						if (html_id.equalsIgnoreCase("SmartCode")) {
							log.debug("Smart Code 교체");
							BufferedImage img = (BufferedImage) params.get(html_id);
							RXPrintImage image = (RXPrintImage) element;
							RXRenderable renderer = RXImageRenderer.getInstance(img, OnErrorTypeEnum.BLANK);
							image.setRenderer(renderer);
							image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
							mParams.remove(html_id);
							elements.remove(i);
							elements.add(i, image);
						}
					} else if (element instanceof RXPrintText) {
						if (html_id.equalsIgnoreCase("IssuerID")) {
							log.debug("발급번호 교체");
							RXPrintText text = (RXPrintText) element;
							String s = (String) params.get(html_id);
							if (s != null && !s.equalsIgnoreCase("")) {
								text.setText(s);
								mParams.remove(html_id);
								text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								elements.remove(i);
								elements.add(i, text);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReportExpressPrint getReportExpressPrint(Map<String, Object> params) throws RXException {
		mParams = new HashMap<String, Object>();

		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			mParams.put(key, params.get(key));
		}

		List pages = print.getPages();
		int pageSize = print.getPages().size();

		log.debug("Page  Size: " + pageSize);

		for (int i = 0; i < pageSize; i++) {
			RXPrintPage page = (RXPrintPage) pages.get(i);
			List elements = page.getElements();

			for (int j = 0; j < elements.size(); j++) {
				RXPrintElement element = ((RXPrintElement) elements.get(j));
				if (element instanceof RXPrintFrame) {
					exportFrame((RXPrintFrame) element, params);
				} else {
					String html_id = RXProperties.getProperty(element, PROPERTY_HTML_ID);
					if (html_id != null && !html_id.equalsIgnoreCase("") && params.containsKey(html_id)) {
						if (element instanceof RXPrintImage) {
							/*
							 * Html 태그 입력에서 정의된 이미지로 교체한다. 교체할 이미지 파일은 HashMap에
							 * com.cabsoft.rx.export.html.id를 키로 정의되어 있다.
							 */
							if (html_id.equalsIgnoreCase("SmartCode")) {
								log.debug("Smart Code 교체");
								BufferedImage img = (BufferedImage) params.get(html_id);
								RXPrintImage image = (RXPrintImage) element;
								RXRenderable renderer = RXImageRenderer.getInstance(img, OnErrorTypeEnum.BLANK);
								image.setRenderer(renderer);
								image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								mParams.remove(html_id);
								elements.remove(j);
								elements.add(j, image);
							}
						} else if (element instanceof RXPrintText) {
							if (html_id.equalsIgnoreCase("IssuerID")) {
								log.debug("발급번호 교체");
								RXPrintText text = (RXPrintText) element;
								String s = (String) params.get(html_id);
								if (s != null && !s.equalsIgnoreCase("")) {
									text.setText(s);
									mParams.remove(html_id);
									text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
									elements.remove(j);
									elements.add(j, text);
								}
							}
						}
					}
				}
			}
			page.setElements(elements);

		}
		return print;
	}

	public Map<String, Object> getResultParams() {
		return mParams;
	}
}
