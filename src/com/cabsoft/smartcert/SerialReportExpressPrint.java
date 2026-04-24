package com.cabsoft.smartcert;

import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXGenericPrintElement;
import com.cabsoft.rx.engine.RXImageRenderer;
import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintEllipse;
import com.cabsoft.rx.engine.RXPrintFrame;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintLine;
import com.cabsoft.rx.engine.RXPrintPage;
import com.cabsoft.rx.engine.RXPrintRectangle;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXRenderable;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.utils.IssuerID;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("deprecation")
public class SerialReportExpressPrint {
	private static final Log log = LogFactory.getLog(SerialReportExpressPrint.class);

	private static final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public static final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	public static final String PROPERTY_HTML_RADIOVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "radiovalue";

	ReportExpressPrint print = null;
	Map<String, Object> mParams = new HashMap<String, Object>();

	public SerialReportExpressPrint(ReportExpressPrint print) {
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
				log.debug(String.valueOf(i) + ">>>key:" + element.getKey() + ",html_id:" + html_id);

				if (html_id != null && !html_id.equalsIgnoreCase("") && params.containsKey(html_id)) {
					String value = (String) params.get(html_id);
					log.debug(html_id + " = " + value);
					if (element instanceof RXPrintLine) {
					} else if (element instanceof RXPrintRectangle) {
						log.debug("RXPrintRectangle - 해당 안됨");
					} else if (element instanceof RXPrintEllipse) {
						log.debug("RXPrintEllipse - 해당 안됨");
					} else if (element instanceof RXPrintImage) {
						log.debug("RXPrintImage");
						/*
						 * Html 태그 입력에서 정의된 이미지로 교체한다. 교체할 이미지 파일은 HashMap에
						 * com.cabsoft.rx.export.html.id를 키로 정의되어 있다.
						 */
						if (value != null & !value.equalsIgnoreCase("")) {
							RXPrintImage image = (RXPrintImage) element;
							RXRenderable renderer = RXImageRenderer.getInstance((String) params.get(html_id));
							image.setRenderer(renderer);
							image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
							elements.remove(i);
							elements.add(i, image);
						}
					} else if (element instanceof RXPrintText) {
						log.debug("RXPrintText");
						RXPrintText text = (RXPrintText) element;

						if (html_id.equalsIgnoreCase("IssuerID") && value != null && value.equalsIgnoreCase("")) {
							String s = (String) params.get(html_id);
							log.debug("일반 텍스트: " + s);
							if (s != null) {
								text.setText(s);
								mParams.remove(html_id);
								text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								elements.remove(i);
								elements.add(i, text);
							}
						}
					} else if (element instanceof RXPrintFrame) {
					} else if (element instanceof RXGenericPrintElement) {
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
					log.debug(String.valueOf(i + 1) + " Pages, " + String.valueOf(j) + " >>>key:" + element.getKey() + ",html_id:" + html_id + ",mParams.size():" + String.valueOf(mParams.size()));

					// 더이상 바꿀게 없으면 빠져나오기
					if (mParams.size() == 0)
						break;

					if (html_id != null && !html_id.equalsIgnoreCase("") && params.containsKey(html_id)) {
						String value = (String) params.get(html_id);
						log.debug(html_id + " = " + value);
						if (element instanceof RXPrintLine) {
						} else if (element instanceof RXPrintRectangle) {
							log.debug("RXPrintRectangle - 해당 안됨");
						} else if (element instanceof RXPrintEllipse) {
							log.debug("RXPrintEllipse - 해당 안됨");
						} else if (element instanceof RXPrintImage) {
							log.debug("RXPrintImage");
							/*
							 * Html 태그 입력에서 정의된 이미지로 교체한다. 교체할 이미지 파일은 HashMap에
							 * com.cabsoft.rx.export.html.id를 키로 정의되어 있다.
							 */
							if (value != null & !value.equalsIgnoreCase("")) {
								RXPrintImage image = (RXPrintImage) element;
								RXRenderable renderer = RXImageRenderer.getInstance((String) params.get(html_id));
								image.setRenderer(renderer);
								image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								elements.remove(j);
								elements.add(j, image);
							}
						} else if (element instanceof RXPrintText) {
							log.debug("RXPrintText");
							RXPrintText text = (RXPrintText) element;
							if (html_id.equalsIgnoreCase("boldText") && value != null && value.equalsIgnoreCase("1")) {
								log.debug("현재 HTML ID가 boldText 이고 value=1 이므로 현재의 RXPrintText를 삭제한다.");
								// text.setFontSize(text.getFontSize()+2);
								mParams.remove(html_id);
								elements.remove(j);
							} else if (html_id.equals("readconfirm") && value != null) {
								log.debug("HTML ID가 readconfirm 이므로 이 콘트롤의 내용을 [내용을 확인하였습니다.]로 변경");
								mParams.remove(html_id);
								text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								if (!value.equalsIgnoreCase("")) {
									text.setText(value);
								} else {
									text.setText("내용을 확인하였습니다.");
								}
								elements.remove(j);
								elements.add(j, text);
							} else if (html_id.indexOf("checkbox") > -1) {
								if (value != null) {
									log.debug("CheckBox = " + value);
									if (value.equalsIgnoreCase("1")) {
										text.setText("☑");
									} else {
										text.setText("□");
									}
									log.debug("checkbox: " + text.getText());
									mParams.remove(html_id);
									text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
									elements.remove(j);
									elements.add(j, text);
								}
							} else if (html_id.indexOf("yesno") > -1) {
								String attValue = text.getPropertiesMap().getProperty(PROPERTY_HTML_RADIOVALUE);
								log.debug("Radio Button: value = " + value + ", attValue = " + attValue);
								if (attValue != null && value != null && value.equalsIgnoreCase(attValue)) {
									text.setText("◉");
								} else {
									text.setText("○");
								}
								log.debug("radio: " + text.getText());
								mParams.remove(html_id);
								text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								elements.remove(j);
								elements.add(j, text);
							} else {
								String s = (String) params.get(html_id);
								log.debug("일반 텍스트: " + s);
								if (s != null) {
									text.setText(s);
									mParams.remove(html_id);
									text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
									elements.remove(j);
									elements.add(j, text);
								}
							}
						} else if (element instanceof RXPrintFrame) {
						} else if (element instanceof RXGenericPrintElement) {
						}
					}
				}
			}
			page.setElements(elements);

		}
		return print;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String setReportExpressPrint(Map<String, Object> sParams) throws RXException {

		IssuerID id = new IssuerID();
		String serial = "", serialString = "";
		HttpServletRequest request = (HttpServletRequest) sParams.get("request");
		boolean NewIssuerID = (Boolean) sParams.get("NewIssuerID");
		List pages = null;

		// sParams.remove("request");
		if (NewIssuerID) {
			try {
				serial = id.getIssuerID();
				serialString = id.getIssuerIDString(serial);

				sParams.put("IssuerID", serialString);
				request.getSession().setAttribute("IssueID", serial);

				pages = print.getPages();
				int pageSize = print.getPages().size();

				log.debug("Page  Size: " + pageSize);

				for (int i = 0; i < pageSize; i++) {
					RXPrintPage page = (RXPrintPage) pages.get(i);
					List elements = page.getElements();

					int j = -1;
					RXPrintElement element = null;
					do {
						++j;
						element = ((RXPrintElement) elements.get(j));
						log.debug(String.valueOf(i + 1) + " Pages, " + String.valueOf(j) + " >>>key:" + element.getKey() + ",sParams.size():" + String.valueOf(sParams.size()));
					} while (!("IssuerID".equals(element.getKey())));

					log.debug("Find:" + String.valueOf(i + 1) + " Pages, " + String.valueOf(j) + " >>>key:" + element.getKey() + ",sParams.size():" + String.valueOf(sParams.size()));
					if (element instanceof RXPrintText) {
						log.debug("RXPrintText");
						RXPrintText text = (RXPrintText) element;
						String s = (String) sParams.get(element.getKey());
						if (s != null) {
							log.debug("[before]IssuerID: " + text.getText());
							text.setText(text.getText().replaceAll("(.{4}-.{4}-.{4}-.{4}-.{4})", "") + s);
							log.debug("[After]IssuerID: " + text.getText());
							elements.remove(j);
							elements.add(j, text);
						}
					}
					page.setElements(elements);
				} // for (page)

			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				request.getSession().setAttribute("SessionReportPrint", print);
				pages = null;
			}
		} else {
			serial = (String) request.getSession().getAttribute("IssueID");
		}

		return serial;
	}

	public Map<String, Object> getResultParams() {
		return mParams;
	}
}
