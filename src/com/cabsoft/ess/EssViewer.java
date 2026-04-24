/*
 * ReportExpress - Java Reporting Library.
 * Copyright (C) 2003 - 2010 Cabsoft Corporation. All rights reserved.
 * http://www.cabsoftware.com
 */

/******************************************
 * A4 용지: 755px X 1084px
 * 1inch = 25.4mm = 96pixel
 * 1mm = 3.78px
 ******************************************/

package com.cabsoft.ess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.util.StringUtil;

import com.cabsoft.rx.crosstabs.RXCellContents;
import com.cabsoft.rx.engine.RXAbstractExporter;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.RXGenericPrintElement;
import com.cabsoft.rx.engine.RXImageMapRenderer;
import com.cabsoft.rx.engine.RXImageRenderer;
import com.cabsoft.rx.engine.RXLineBox;
import com.cabsoft.rx.engine.RXPen;
import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintElementIndex;
import com.cabsoft.rx.engine.RXPrintEllipse;
import com.cabsoft.rx.engine.RXPrintFrame;
import com.cabsoft.rx.engine.RXPrintGraphicElement;
import com.cabsoft.rx.engine.RXPrintHyperlink;
import com.cabsoft.rx.engine.RXPrintHyperlinkParameter;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintImageArea;
import com.cabsoft.rx.engine.RXPrintImageAreaHyperlink;
import com.cabsoft.rx.engine.RXPrintLine;
import com.cabsoft.rx.engine.RXPrintPage;
import com.cabsoft.rx.engine.RXPrintRectangle;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXPropertiesMap;
import com.cabsoft.rx.engine.RXRenderable;
import com.cabsoft.rx.engine.RXRuntimeException;
import com.cabsoft.rx.engine.RXStyle;
import com.cabsoft.rx.engine.RXWrappingSvgRenderer;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.base.RXBasePrintFrame;
import com.cabsoft.rx.engine.export.CutsInfo;
import com.cabsoft.rx.engine.export.DefaultHyperlinkTargetProducerFactory;
import com.cabsoft.rx.engine.export.ElementWrapper;
import com.cabsoft.rx.engine.export.ExporterNature;
import com.cabsoft.rx.engine.export.GenericElementHandlerEnviroment;
import com.cabsoft.rx.engine.export.GenericElementHtmlHandler;
import com.cabsoft.rx.engine.export.OccupiedGridCell;
import com.cabsoft.rx.engine.export.RXExportProgressMonitor;
import com.cabsoft.rx.engine.export.RXExporterGridCell;
import com.cabsoft.rx.engine.export.RXGridLayout;
import com.cabsoft.rx.engine.export.RXHtmlExporterContext;
import com.cabsoft.rx.engine.export.RXHtmlExporterHelper;
import com.cabsoft.rx.engine.export.RXHtmlExporterNature;
import com.cabsoft.rx.engine.export.RXHtmlExporterParameter;
import com.cabsoft.rx.engine.export.RXHyperlinkProducer;
import com.cabsoft.rx.engine.export.RXHyperlinkTargetProducer;
import com.cabsoft.rx.engine.export.RXHyperlinkTargetProducerFactory;
import com.cabsoft.rx.engine.export.RXPdfExporterTagHelper;
import com.cabsoft.rx.engine.fonts.FontFamily;
import com.cabsoft.rx.engine.fonts.FontInfo;
import com.cabsoft.rx.engine.type.HyperlinkTypeEnum;
import com.cabsoft.rx.engine.type.LineDirectionEnum;
import com.cabsoft.rx.engine.type.LineSpacingEnum;
import com.cabsoft.rx.engine.type.ModeEnum;
import com.cabsoft.rx.engine.type.RunDirectionEnum;
import com.cabsoft.rx.engine.type.ScaleImageEnum;
import com.cabsoft.rx.engine.util.RXColorUtil;
import com.cabsoft.rx.engine.util.RXFontUtil;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.rx.engine.util.RXStringUtil;
import com.cabsoft.rx.engine.util.RXStyledText;
import com.cabsoft.rx.engine.util.Pair;
import com.cabsoft.utils.StringUtils;

/**
 * ReportExpress 문서를 HTML 포맷으로 내보낸다. 캐릭터 출력 타입으로 그리드 기반의 레이아웃으로 문서를 내보낸다.
 * 
 * @author Cabsoft(support@cabsoftware.com)
 * @version $Id: RXHtmlExporter.java 0001 2010-07-30 $
 */
@SuppressWarnings({ "deprecation", "rawtypes", "unchecked", "unused" })
public class EssViewer extends RXAbstractExporter {

	private final Log log = LogFactory.getLog(EssViewer.class);

	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";

	/**
	 * {@link GenericElementHandlerEnviroment#getHandler(com.cabsoft.rx.engine.RXGenericElementType, String)}
	 * 에서 사용되는 내보내기 키.
	 */
	public final String HTML_EXPORTER_KEY = RXProperties.PROPERTY_PREFIX + "html";

	/**
	 * @deprecated {@link RXHtmlExporterParameter#PROPERTY_FRAMES_AS_NESTED_TABLES}
	 *             로 대체됨.
	 */
	public final String PROPERTY_FRAMES_AS_NESTED_TABLES = RXHtmlExporterParameter.PROPERTY_FRAMES_AS_NESTED_TABLES;

	/*
	 * html element의 id
	 */
	public final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	public final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";
	
	/**
	 * Tab Index 설정
	 */
	public final String PROPERTY_HTML_TABINDEX = HTML_EXPORTER_PROPERTIES_PREFIX + "tabindex";

	/*
	 * html element의 alt 속성 설정
	 */
	public final String PROPERTY_HTML_ALT = HTML_EXPORTER_PROPERTIES_PREFIX + "alt";

	/*
	 * html element의 설명을 위한 title 속성 설정
	 */
	public final String PROPERTY_HTML_TITLE = HTML_EXPORTER_PROPERTIES_PREFIX + "title";

	/*
	 * html element의 tag 값 설정 select: <option selected="selected">충북</option>
	 * <option>충남</option> <option>강원</option> checkbox: 0 또는 1, 기본값이 체크인 경우 1을
	 * 설정한다. radio button: 0 또는 1, 기본적으로 선택되는 radio button에만 1을 설정한다.
	 */
	public final String PROPERTY_HTML_TAGVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "tagValue";

	/*
	 * html element의 onclick 등의 이벤트 발생시 호출되는 자바스크립트 설정
	 * 
	 * onclick="doCheck(this)"
	 * 
	 * 이때 doCheck(this) 등의 함수는 이미 정의 되어 있어야 한다.
	 */
	public final String PROPERTY_HTML_SCRIPT = HTML_EXPORTER_PROPERTIES_PREFIX + "script";

	/*
	 * 해당 radio button의 값을 설정한다.
	 */
	public final String PROPERTY_HTML_RADIOVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "radiovalue";

	public final String PROPERTY_HTML_DESCRIPTION = HTML_EXPORTER_PROPERTIES_PREFIX + "desc";

	public final String PROPERTY_HTML_TAG_TYPE = HTML_EXPORTER_PROPERTIES_PREFIX + "type";

	/**
	 * z-index 설정
	 */
	public final String PROPERTY_HTML_ZINDEX = HTML_EXPORTER_PROPERTIES_PREFIX + "zindex";

	/**
	 * CSS Class 설정
	 */
	public final String PROPERTY_HTML_CLASS = HTML_EXPORTER_PROPERTIES_PREFIX + "class";

	/**
	 * 접근 가능한 HTML을 생성하기 위한 내보내기를 결정하는 설정 속성
	 */
	public final String PROPERTY_ACCESSIBLE = HTML_EXPORTER_PROPERTIES_PREFIX + "accessible";

	/**
	 * disable 설저d
	 */
	public final String PROPERTY_HTML_DISABLED = HTML_EXPORTER_PROPERTIES_PREFIX + "disable";

	/**
	 *
	 */
	protected final String RX_PAGE_ANCHOR_PREFIX = "RX_PAGE_ANCHOR_";

	protected final float DEFAULT_ZOOM = 1f;

	/**
	 *
	 */
	protected final String CSS_TEXT_ALIGN_LEFT = "left";
	protected final String CSS_TEXT_ALIGN_RIGHT = "right";
	protected final String CSS_TEXT_ALIGN_CENTER = "center";
	protected final String CSS_TEXT_ALIGN_JUSTIFY = "justify";

	/**
	 *
	 */
	protected final String HTML_VERTICAL_ALIGN_TOP = "top";
	protected final String HTML_VERTICAL_ALIGN_MIDDLE = "middle";
	protected final String HTML_VERTICAL_ALIGN_BOTTOM = "bottom";

	public final String IMAGE_NAME_PREFIX = "img_";
	protected final int IMAGE_NAME_PREFIX_LEGTH = IMAGE_NAME_PREFIX.length();

	protected class ExporterContext extends BaseExporterContext implements RXHtmlExporterContext {
		public String getExportPropertiesPrefix() {
			return HTML_EXPORTER_PROPERTIES_PREFIX;
		}

		public String getHyperlinkURL(RXPrintHyperlink link) {
			return EssViewer.this.getHyperlinkURL(link);
		}
	}

	/**
	 *
	 */
	protected Writer writer;
	protected RXExportProgressMonitor progressMonitor;
	protected Map rendererToImagePathMap;
	protected Map imageMaps;
	protected Map imageNameToImageDataMap;
	protected List imagesToProcess;
	protected boolean isPxImageLoaded;

	protected int reportIndex;
	protected int pageIndex;
	private int current_page = 0; // 현재 페이지 정보

	/**
	 *
	 */
	protected File imagesDir;
	protected String imagesURI;
	protected String imagesURIParam;
	protected boolean isOutputImagesToDir;
	protected boolean isRemoveEmptySpace;
	protected boolean isWhitePageBackground;
	protected String encoding;
	protected String sizeUnit;
	protected float zoom = DEFAULT_ZOOM;
	protected boolean isUsingImagesToAlign;
	private boolean frameTotable = true;
	protected boolean isWrapBreakWord;
	protected boolean isIgnorePageMargins;
	protected boolean accessibleHtml;

	protected boolean flushOutput;

	/**
	 *
	 */
	protected String htmlHeader;
	protected String betweenPagesHtml;
	protected String htmlFooter;

	private String tableSummary = "";
	private String jobID = "";
	private String webUrl = "";
	private boolean usingWebImage = false;
	private String pwd = "";
	private int totalPageView = -1;
	private int curPageView = -1;
	private int totalRecords = -1;
	private int startRecords = -1;
	private int endRecords = -1;
	private boolean elementOperlay = false;
	private String userAgent = "";
	private boolean isIE = true;
	private boolean useOverlap = false;
	private boolean previewOnly = false;
	
	private int tabIndex = 99;

	protected StringProvider emptyCellStringProvider;

	/**
	 * @deprecated
	 */
	protected Map fontMap;

	private LinkedList backcolorStack;
	private Color backcolor;

	protected RXHyperlinkTargetProducerFactory targetProducerFactory = new DefaultHyperlinkTargetProducerFactory();

	protected boolean hyperlinkStarted;
	protected int thDepth;

	protected ExporterNature nature;

	private HashMap<String, Integer> idMap = null;

	/*
	 * 라디오 버튼의 경우 id를 다르게 하고 name을 같게 하기 위한 변수
	 */
	private int radioIndex = 0;

	// 단순 확인 버튼(전송(서명)값에 포함안됨 html.type = "buttonconfirm"
	private int btnconfirmIndex = 0;

	/*
	 * 웹 페이지에서 사용자가 입력할 태그를 저장할 해시맵으로 이 맵에의해 form 태그가 만들어진다.
	 */
	private Map<String, Object> essParameters = new HashMap<String, Object>();

	private List<RXPrintText> formList = new ArrayList<RXPrintText>();
	private List<Integer> formListPage = new ArrayList<Integer>();

	private String customer = "";
	private String product = "";
	private HashMap<String, Object> exptHMap = new HashMap<String, Object>();

	/*
	 * 위의 해시맵에 의해 만들어질 form 태그의 이름을 설정한다.
	 */
	private String FormName = "";

	protected RXHtmlExporterContext exporterContext = new ExporterContext();

	/*
	 * 서석에서 RXStyle: pre_lineblank 처리 (기존 com.cabsoft.rx.export.html.line.blank
	 * 보완)
	 */
	private String lineblankstyle = "";
	private int[] lineParagrap = new int[] { -1, -1 }; // left,right

	public EssViewer() {
		backcolorStack = new LinkedList();
		backcolor = null;
		idMap = new HashMap<String, Integer>();
		tableSummary = "";
	}

	private String getHtmlID(String key) {
		Integer idCount = 0;
		if (idMap.isEmpty() == false && idMap.containsKey(key) == true) {
			idCount = idMap.get(key) + 1;
		}
		idMap.put(key, idCount);
		return key + "_" + String.valueOf(idCount);
	}

	public void setPreviewOnly(boolean previewOnly) {
		this.previewOnly = previewOnly;
	}

	public void setUseOverlap(boolean useOverlap) {
		this.useOverlap = useOverlap;
	}

	public void setTableSummary(String tableSummary) {
		this.tableSummary = tableSummary;
	}

	public void setFormName(String formName) {
		this.FormName = formName;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	/*
	 * 사용자 입력에 의해 변경되어야 할 데이터 맵핑 해시맵 이 해시맵에 의해 ReportExpressPrint 개체가 수정된다.
	 */
	public Map<String, Object> getEssParams() {
		return essParameters;
	}

	public void exportReport(String jobID, String pwd) throws RXException {
		this.jobID = jobID;
		this.pwd = pwd;
		this.usingWebImage = false;
		this.webUrl = "";
		doExportReport();
	}

	public void exportReport(String jobID, String webUrl, String pwd) throws RXException {
		this.jobID = jobID;
		this.usingWebImage = true;
		this.webUrl = webUrl;
		this.pwd = pwd;
		doExportReport();
	}

	private boolean IE(String agent) {
		if (agent.indexOf("msie") > -1 || agent.indexOf("trident") > -1) {
			return true;
		} else {
			return false;
		}
	}

	public HashMap<String, Object> getExptHMap() {
		return exptHMap;
	}

	/**
	 *
	 */
	public void exportReport() throws RXException {
		throw new RXException("본 HTML 뷰어에서는 지원하지 않습니다.");
	}

	private void doExportReport() throws RXException {
		progressMonitor = (RXExportProgressMonitor) parameters.get(RXExporterParameter.PROGRESS_MONITOR);

		/*
		 * style 읽기(line.blank)관련
		 */
		ReportExpressPrint rxprint = (ReportExpressPrint) parameters.get(RXExporterParameter.REPORTEXPRESS_PRINT);

		List<RXStyle> slist = rxprint.getStylesList();

		int c_ctlpos_End = 0;
		for (int i = 0; i < slist.size(); i++) {
			log.debug("RXStyle:" + slist.get(i).getName());
			if (("pre_lineblank").equals(slist.get(i).getName())) {
				lineblankstyle = "s,"; // style에 지정되어 있음
				if (slist.get(i).getLineBox().getTopPen().getLineWidth().floatValue() == 0f)
					lineblankstyle += "t";
				if (slist.get(i).getLineBox().getBottomPen().getLineWidth().floatValue() == 0f)
					lineblankstyle += "b";
				if (slist.get(i).getLineBox().getLeftPen().getLineWidth().floatValue() == 0f) {
					lineblankstyle += "l";
					lineParagrap[0] = rxprint.getLeftMargin() + slist.get(i).getParagraph().getLeftIndent();
				}
				if (slist.get(i).getLineBox().getRightPen().getLineWidth().floatValue() == 0f) {
					lineblankstyle += "r";
					lineParagrap[1] = rxprint.getLeftMargin() + slist.get(i).getParagraph().getRightIndent();
				}
				log.debug("pre_lineblank:" + lineblankstyle);
				break;
			}
		}

		/*   */
		setOffset();

		try {
			/*   */
			setExportContext();

			/*   */
			setInput();

			if (!parameters.containsKey(RXExporterParameter.FILTER)) {
				filter = createFilter(HTML_EXPORTER_PROPERTIES_PREFIX);
			}

			/*   */
			if (!isModeBatch) {
				setPageRange();
			}

			htmlHeader = (String) parameters.get(RXHtmlExporterParameter.HTML_HEADER);
			betweenPagesHtml = (String) parameters.get(RXHtmlExporterParameter.BETWEEN_PAGES_HTML);
			htmlFooter = (String) parameters.get(RXHtmlExporterParameter.HTML_FOOTER);
			userAgent = (String) parameters.get(RXExporterParameter.USER_AGENT);
			isIE = IE(userAgent);

			imagesDir = (File) parameters.get(RXHtmlExporterParameter.IMAGES_DIR);
			if (imagesDir == null) {
				String dir = (String) parameters.get(RXHtmlExporterParameter.IMAGES_DIR_NAME);
				if (dir != null) {
					imagesDir = new File(dir);
				}
			}

			isRemoveEmptySpace = getBooleanParameter(RXHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					RXHtmlExporterParameter.PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, false);

			isWhitePageBackground = getBooleanParameter(RXHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					RXHtmlExporterParameter.PROPERTY_WHITE_PAGE_BACKGROUND, true);

			Boolean isOutputImagesToDirParameter = (Boolean) parameters
					.get(RXHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR);
			if (isOutputImagesToDirParameter != null) {
				isOutputImagesToDir = isOutputImagesToDirParameter.booleanValue();
			}

			String uri = (String) parameters.get(RXHtmlExporterParameter.IMAGES_URI);
			if (uri != null) {
				imagesURI = uri;

				if (StringUtils.contains(imagesURI, "?")) {
					String[] s = StringUtils.split(imagesURI, "?");
					if (s.length == 2) {
						imagesURI = s[0] + "?__p=";
						imagesURIParam = s[1];
					}
				}
			}

			encoding = getStringParameterOrDefault(RXExporterParameter.CHARACTER_ENCODING,
					RXExporterParameter.PROPERTY_CHARACTER_ENCODING);

			rendererToImagePathMap = new HashMap();
			imageMaps = new HashMap();
			imagesToProcess = new ArrayList();
			isPxImageLoaded = false;

			// IMAGE_MAP 매개변수와 하위 호환성을 유지 목적
			imageNameToImageDataMap = (Map) parameters.get(RXHtmlExporterParameter.IMAGES_MAP);
			// if (imageNameToImageDataMap == null)
			// {
			// imageNameToImageDataMap = new HashMap();
			// }
			// END - backward compatibility with the IMAGE_MAP parameter

			isWrapBreakWord = getBooleanParameter(RXHtmlExporterParameter.IS_WRAP_BREAK_WORD,
					RXHtmlExporterParameter.PROPERTY_WRAP_BREAK_WORD, false);

			sizeUnit = getStringParameterOrDefault(RXHtmlExporterParameter.SIZE_UNIT,
					RXHtmlExporterParameter.PROPERTY_SIZE_UNIT);

			Float zoomRatio = (Float) parameters.get(RXHtmlExporterParameter.ZOOM_RATIO);
			if (zoomRatio != null) {
				zoom = zoomRatio.floatValue();
				if (zoom <= 0) {
					throw new RXException("Invalid zoom ratio : " + zoom);
				}
			} else {
				zoom = DEFAULT_ZOOM;
			}

			isUsingImagesToAlign = getBooleanParameter(RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,
					RXHtmlExporterParameter.PROPERTY_USING_IMAGES_TO_ALIGN, false);

			if (isUsingImagesToAlign) {
				emptyCellStringProvider = new StringProvider() {
					public String getStringForCollapsedTD(Object value, String style, int width, int height) {
						if (usingWebImage == true) {
							return "><img alt=\"\" src=\"" + value + "pixel.gif\" style=\"width: " + toSizeUnit(width)
									+ "; height: " + toSizeUnit(height) + "; " + style + "\"/>";
						} else {
							return "><img alt=\"\" src=\"" + value + "px\" style=\"width: " + toSizeUnit(width)
									+ "; height: " + toSizeUnit(height) + "; " + style + "\"/>";
						}
					}

					public String getStringForEmptyTD(Object value) {
						if (usingWebImage == true) {
							return "<img alt=\"\" src=\"" + value + "pixel.gif\" border=\"0\"/>";
						} else {
							return "<img alt=\"\" src=\"" + value + "px\" border=\"0\"/>";
						}
					}

					public String getReportTableStyle() {
						return null;
					}
				};

				if (usingWebImage == false)
					loadPxImage();
			} else {
				emptyCellStringProvider = new StringProvider() {
					public String getStringForCollapsedTD(Object value, String style, int width, int height) {
						return " style=\"width: " + toSizeUnit(width) + "; height: " + toSizeUnit(height) + "; "
								+ style + "\">";
					}

					public String getStringForEmptyTD(Object value) {
						return "";
					}

					public String getReportTableStyle() {
						// required for lines and rectangles, but doesn't work
						// in IE
						// border-collapse: collapse seems to take care of this
						// though
						return "empty-cells: show";
					}
				};
			}

			isIgnorePageMargins = getBooleanParameter(RXExporterParameter.IGNORE_PAGE_MARGINS,
					RXExporterParameter.PROPERTY_IGNORE_PAGE_MARGINS, false);

			accessibleHtml = RXProperties.getBooleanProperty(reportexpressPrint, PROPERTY_ACCESSIBLE, false);

			fontMap = (Map) parameters.get(RXExporterParameter.FONT_MAP);

			setHyperlinkProducerFactory();

			// FIXMENOW check all exporter properties that are supposed to work
			// at report level
			frameTotable = getBooleanParameter(RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES,
					RXHtmlExporterParameter.PROPERTY_FRAMES_AS_NESTED_TABLES, true);
			boolean deepGrid = !frameTotable;

			nature = new RXHtmlExporterNature(filter, deepGrid, isIgnorePageMargins);

			flushOutput = getBooleanParameter(RXHtmlExporterParameter.FLUSH_OUTPUT,
					RXHtmlExporterParameter.PROPERTY_FLUSH_OUTPUT, true);

			StringBuffer sb = (StringBuffer) parameters.get(RXExporterParameter.OUTPUT_STRING_BUFFER);
			if (sb != null) {
				try {
					writer = new StringWriter();
					exportReportToWriter();
					sb.append(writer.toString());
				} catch (IOException e) {
					throw new RXException("Error writing to StringBuffer writer(1) : " + reportexpressPrint.getName(),
							e);
				} catch (Exception e) {
					throw new RXException("Error writing to StringBuffer writer(2) : " + reportexpressPrint.getName(),
							e);
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException e) {
						}
					}
				}
			} else {
				writer = (Writer) parameters.get(RXExporterParameter.OUTPUT_WRITER);
				if (writer != null) {
					try {
						exportReportToWriter();
					} catch (IOException e) {
						throw new RXException("Error writing to writer(1) : " + reportexpressPrint.getName(), e);
					} catch (Exception e) {
						throw new RXException("Error writing to writer(2) : " + reportexpressPrint.getName(), e);
					}
				} else {
					OutputStream os = (OutputStream) parameters.get(RXExporterParameter.OUTPUT_STREAM);
					if (os != null) {
						try {
							writer = new OutputStreamWriter(os, encoding);
							exportReportToWriter();
						} catch (IOException e) {
							throw new RXException("Error writing to OutputStream writer(1) : "
									+ reportexpressPrint.getName(), e);
						} catch (Exception e) {
							throw new RXException("Error writing to OutputStream writer(2) : "
									+ reportexpressPrint.getName(), e);
						}
					} else {
						File destFile = (File) parameters.get(RXExporterParameter.OUTPUT_FILE);
						if (destFile == null) {
							String fileName = (String) parameters.get(RXExporterParameter.OUTPUT_FILE_NAME);
							if (fileName != null) {
								destFile = new File(fileName);
							} else {
								throw new RXException("No output specified for the exporter.");
							}
						}

						try {
							os = new FileOutputStream(destFile);
							writer = new OutputStreamWriter(os, encoding);
						} catch (IOException e) {
							throw new RXException("Error creating to file writer : " + reportexpressPrint.getName(), e);
						}

						if (imagesDir == null) {
							imagesDir = new File(destFile.getParent(), destFile.getName() + "_files");
						}

						if (isOutputImagesToDirParameter == null) {
							isOutputImagesToDir = true;
						}

						if (imagesURI == null) {
							imagesURI = imagesDir.getName() + "/";
						}

						try {
							exportReportToWriter();
						} catch (IOException e) {
							throw new RXException("Error writing to file writer(1) : " + reportexpressPrint.getName(),
									e);
						} catch (Exception e) {
							throw new RXException("Error writing to file writer(2) : " + reportexpressPrint.getName(),
									e);
						} finally {
							if (writer != null) {
								try {
									writer.close();
								} catch (IOException e) {
								}
							}
						}
					}
				}
			}

			if (isOutputImagesToDir) {
				if (imagesDir == null) {
					throw new RXException("The images directory was not specified for the exporter.");
				}

				if (isPxImageLoaded || (imagesToProcess != null && imagesToProcess.size() > 0)) {
					if (!imagesDir.exists()) {
						imagesDir.mkdir();
					}

					if (isPxImageLoaded) {
						RXRenderable pxRenderer = RXImageRenderer.getInstance("com/cabsoft/rx/engine/images/pixel.GIF");
						byte[] imageData = pxRenderer.getImageData();

						File imageFile = new File(imagesDir, "px");
						FileOutputStream fos = null;

						try {
							fos = new FileOutputStream(imageFile);
							fos.write(imageData, 0, imageData.length);
						} catch (IOException e) {
							throw new RXException("Error writing to image file : " + imageFile, e);
						} finally {
							if (fos != null) {
								try {
									fos.close();
								} catch (IOException e) {
								}
							}
						}
					}

					for (Iterator it = imagesToProcess.iterator(); it.hasNext();) {
						RXPrintElementIndex imageIndex = (RXPrintElementIndex) it.next();

						RXPrintImage image = getImage(rxPrintList, imageIndex);
						RXRenderable renderer = image.getRenderer();
						if (renderer.getType() == RXRenderable.TYPE_SVG) {
							renderer = new RXWrappingSvgRenderer(renderer, new Dimension(image.getWidth(),
									image.getHeight()), ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor()
									: null);
						}

						byte[] imageData = renderer.getImageData();

						File imageFile = new File(imagesDir, getImageName(imageIndex));
						FileOutputStream fos = null;

						try {
							fos = new FileOutputStream(imageFile);
							fos.write(imageData, 0, imageData.length);
						} catch (IOException e) {
							throw new RXException("Error writing to image file : " + imageFile, e);
						} finally {
							if (fos != null) {
								try {
									fos.close();
								} catch (IOException e) {
								}
							}
						}
					}
				}
			}
		} finally {
			resetExportContext();
		}
	}

	private RXPrintImage getImage(List reportexpressPrintList, String imageName) {
		return getImage(reportexpressPrintList, getPrintElementIndex(imageName));
	}

	private RXPrintImage getImage(List reportexpressPrintList, RXPrintElementIndex imageIndex) {
		ReportExpressPrint report = (ReportExpressPrint) reportexpressPrintList.get(imageIndex.getReportIndex());
		RXPrintPage page = (RXPrintPage) report.getPages().get(imageIndex.getPageIndex());

		Integer[] elementIndexes = imageIndex.getAddressArray();
		Object element = page.getElements().get(elementIndexes[0].intValue());

		for (int i = 1; i < elementIndexes.length; ++i) {
			RXPrintFrame frame = (RXPrintFrame) element;
			element = frame.getElements().get(elementIndexes[i].intValue());
		}

		return (RXPrintImage) element;
	}

	/**
	 * @throws Exception
	 * 
	 */
	protected void exportReportToWriter() throws Exception {
		int reportSize = rxPrintList.size();
		int max_pages = 0;
		current_page = 0;

		EssViewerHelper helper = new EssViewerHelper(previewOnly);

		for (int i = 0; i < reportSize; i++) {
			List pages = reportexpressPrint.getPages();
			if (pages != null && pages.size() > 0) {
				max_pages += pages.size();
			}
		}

		if (htmlHeader == null) {
			writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
			writer.write("<html>\n");
			writer.write("<head>\n");
			writer.write("  <title></title>\n");
			writer.write("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\"/>\n");
			writer.write("  <style type=\"text/css\">\n");
			writer.write("    a {text-decoration: none}\n");
			writer.write("  </style>\n");
			writer.write("</head>\n");
			writer.write("<body text=\"#000000\" link=\"#000000\" alink=\"#000000\" vlink=\"#000000\">\n");
			writer.write("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
			writer.write("<tr><td width=\"50%\">&nbsp;</td><td align=\"center\">\n");
			writer.write("\n");
		} else {
			writer.write(htmlHeader);

			writer.write(helper.setMaxPages(max_pages, toScaledSize(reportexpressPrint.getPageWidth()),
					toScaledSize(reportexpressPrint.getPageHeight() - 10), jobID, totalPageView, curPageView,
					totalRecords, startRecords, endRecords));

			writer.write(helper.getDocStart(userAgent, toScaledSize(reportexpressPrint.getPageWidth()),
					toScaledSize(reportexpressPrint.getPageHeight() - 10),
					toScaledSize(reportexpressPrint.getLeftMargin()), toScaledSize(reportexpressPrint.getRightMargin())));
		}

		for (reportIndex = 0; reportIndex < rxPrintList.size(); reportIndex++) {
			setReportExpressPrint((ReportExpressPrint) rxPrintList.get(reportIndex));

			List pages = reportexpressPrint.getPages();

			if (progressMonitor != null) {
				progressMonitor.startExport(reportIndex, reportSize, pages.size());
			}

			if (pages != null && pages.size() > 0) {
				if (isModeBatch) {
					startPageIndex = 0;
					endPageIndex = pages.size() - 1;
				}

				RXPrintPage page = null;
				for (pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++) {
					if (Thread.interrupted()) {
						throw new RXException("현재 쓰레드가 인터럽트되었습니다.");
					}

					page = (RXPrintPage) pages.get(pageIndex);

					if (htmlHeader != null) {
						writer.write(helper.getPageStart(toScaledSize(reportexpressPrint.getPageWidth()),
								toScaledSize(reportexpressPrint.getPageHeight())));
					}

					current_page++;
					writer.write("<a name=\"" + RX_PAGE_ANCHOR_PREFIX + String.valueOf(current_page) + "\"></a>\n");

					/************************************************************/
					exportPage(page);
					/************************************************************/

					if (htmlHeader != null) {
						writer.write(helper.getPageEnd());
					}

					if (reportIndex < rxPrintList.size() - 1 || pageIndex < endPageIndex) {
						if (betweenPagesHtml == null) {
							if (htmlHeader != null) {
								writer.write("<p class=\"breakhere\" />\n");
							} else {
								writer.write("<br/>\n<br/>\n");
							}
						} else {
							writer.write(betweenPagesHtml);
						}
					}

					writer.write("\n");
				}
			}
		}

		/*
		 * form 태그가 필요한 경우
		 */
		if (FormName != null & !FormName.equalsIgnoreCase("")) {
			BuildForm builidform = new BuildForm();
			builidform.build(writer, FormName, customer, product, formList, formListPage, exptHMap);
		}

		if (htmlHeader == null) {
			if (htmlFooter == null) {
				writer.write("</td><td width=\"50%\">&nbsp;</td></tr>\n");
				writer.write("</table>\n");
				writer.write("</body>\n");
				writer.write("</html>\n");
			} else {
				writer.write(htmlFooter);
			}
		} else {
			writer.write(helper.getDocEnd(htmlFooter, elementOperlay));
		}

		if (flushOutput) {
			writer.flush();
		}

		if (progressMonitor != null) {
			progressMonitor.endExport();
		}
	}

	/**
	 *
	 */
	protected void exportPage(RXPrintPage page) throws RXException, IOException {
		List elements = null;

		if (accessibleHtml) {
			RXBasePrintFrame frame = new RXBasePrintFrame(reportexpressPrint.getDefaultStyleProvider());

			new RXHtmlExporterHelper(reportexpressPrint).createNestedFrames(page.getElements().listIterator(), frame);

			elements = frame.getElements();
		} else {
			elements = page.getElements();
		}

		/*
		 * zindex가 있는 컨트롤을 추출하기 위해 마지막 변수를 true로 설정
		 */
		RXGridLayout layout = new RXGridLayout(nature, elements, reportexpressPrint.getPageWidth(),
				reportexpressPrint.getPageHeight(), globalOffsetX, globalOffsetY, true);

		if (useOverlap == true) {
			/*
			 * zindex는 없지만 서식에서 겹쳐져 있는 경우
			 */
			List<RXPrintElement> overlap = layout.getOverlapElements();
			List<ElementWrapper> overlapWrappers = layout.getOverlapWrappers();
			exportOverlap(overlap, overlapWrappers);

			/*
			 * 서식에서 겹치지는 않았지만 zindex를 사용한 경우
			 */
			List<RXPrintElement> zindexElements = layout.getZindexElements();
			List<ElementWrapper> zindexWrappers = layout.getZindexWrappers();
			exportOverlap(zindexElements, zindexWrappers);
		}

		exportGrid(layout, isWhitePageBackground, getTableSummary(), false);

		if (progressMonitor != null) {
			progressMonitor.afterPageExport(reportIndex, pageIndex);
		}
	}

	private String getTableSummary() {
		if (tableSummary == null || "".equals(tableSummary)) {
			return "";
		} else {
			String ret = "summary=";
			String s = StringUtils.replaceAll(tableSummary, "$$$", String.valueOf(current_page));
			ret = ret + "\"" + s + "\"";
			return ret;
		}
	}

	// private String getTableCaption() {
	// if (tableSummary == null || "".equals(tableSummary)) {
	// return "<caption></caption>";
	// } else {
	// String s = StringUtils.replaceAll(tableSummary, "$$$",
	// String.valueOf(current_page));
	// return "<caption></caption>";
	// }
	// }

	protected void exportOverlap(List<RXPrintElement> overlapElements, List<ElementWrapper> overlapWrappers)
			throws IOException, RXException {
		elementOperlay = overlapElements.size() > 0 ? true : false;
		// for (int i = 0; i < overlapElements.size(); i++) {
		for (int i = overlapElements.size() - 1; i > -1; i--) {
			RXPrintElement element = (RXPrintElement) overlapElements.get(i);
			ElementWrapper wraper = overlapWrappers.get(i);

			if (element instanceof RXPrintLine) {
				// exportLine((RXPrintLine) element, null);
			} else if (element instanceof RXPrintRectangle) {
				exportRectangle((RXPrintRectangle) element);
			} else if (element instanceof RXPrintEllipse) {
				exportRectangle((RXPrintEllipse) element);
			} else if (element instanceof RXPrintImage) {
				exportImage((RXPrintImage) element, wraper);
			} else if (element instanceof RXPrintText) {
				exportText((RXPrintText) element);
			} else if (element instanceof RXPrintFrame) {
				exportFrame((RXPrintFrame) element, overlapWrappers);
			}
		}
	}

	/**
	 *
	 */
	protected void exportGrid(RXGridLayout gridLayout, boolean whitePageBackground, String summary, boolean isFrame)
			throws IOException, RXException {
		CutsInfo xCuts = gridLayout.getXCuts();
		RXExporterGridCell[][] grid = gridLayout.getGrid();

		String tableStyle = "";
		if (isFrame == false) {
			tableStyle = "width: " + toSizeUnit(gridLayout.getWidth()) + "; border-collapse: collapse";
		} else {
			tableStyle = "width: 100%; border-collapse: collapse";
		}

		String additionalTableStyle = emptyCellStringProvider.getReportTableStyle();
		if (additionalTableStyle != null) {
			tableStyle += "; " + additionalTableStyle;
		}

		writer.write("<table " + summary + " style=\"" + tableStyle
				+ "\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"");

		if (whitePageBackground) {
			writer.write(" bgcolor=\"white\"");
		}
		writer.write(">\n");

		// writer.write(getTableCaption());

		if (whitePageBackground) {
			setBackcolor(Color.white);
		}

		writer.write("<tr>\n");
		int width = 0;
		for (int i = 1; i < xCuts.size(); i++) {
			width = xCuts.getCut(i) - xCuts.getCut(i - 1);
			writer.write("  <td"
					+ emptyCellStringProvider.getStringForCollapsedTD(webUrl.equalsIgnoreCase("") ? imagesURI : webUrl,
							"", width, 1) + "</td>\n");
		}
		writer.write("</tr>\n");

		thDepth = 0;
		for (int y = 0; y < grid.length; y++) {
			if (gridLayout.getYCuts().isCutSpanned(y) || !isRemoveEmptySpace) {
				RXExporterGridCell[] gridRow = grid[y];

				int rowHeight = RXGridLayout.getRowHeight(gridRow);

				boolean hasEmptyCell = hasEmptyCell(gridRow);

				writer.write("<tr valign=\"top\"");
				if (!hasEmptyCell) {
					writer.write(" style=\"height:" + toSizeUnit(rowHeight) + "\"");
					/*
					 * 높이가 0인경우 td 태그 삽입 - 확인 필요
					 */
					if (rowHeight == 0) {
						writer.write("><td colspan=\"" + gridRow.length + "\"></td");
					}
				}
				writer.write(">\n");
				for (int x = 0; x < gridRow.length; x++) {
					RXExporterGridCell gridCell = gridRow[x];
					if (gridCell.getWrapper() == null) {
						writeEmptyCell(gridCell, rowHeight);
					} else {
						RXPrintElement element = gridCell.getWrapper().getElement();

						String thTag = null;

						if (element != null && element.hasProperties()) {
							thTag = element.getPropertiesMap().getProperty(RXPdfExporterTagHelper.PROPERTY_TAG_TH);
						}

						if (thTag != null
								&& (RXPdfExporterTagHelper.TAG_START.equals(thTag) || RXPdfExporterTagHelper.TAG_FULL
										.equals(thTag))) {
							thDepth++;
						}

						if (element instanceof RXPrintLine) {
							RXPrintLine line = (RXPrintLine) element;
							String zindex = RXProperties.getProperty(line, PROPERTY_HTML_ZINDEX);
							zindex = zindex != null ? zindex.trim() : null;
							if (zindex == null || "".equals(zindex)) {
								exportLine(line, gridCell);
							} else {
								writeCellStart(gridCell);
								writer.write(">");
								writeCellEnd(gridCell);
							}
						} else if (element instanceof RXPrintRectangle) {
							RXPrintRectangle rect = (RXPrintRectangle) element;
							String zindex = RXProperties.getProperty(rect, PROPERTY_HTML_ZINDEX);
							zindex = zindex != null ? zindex.trim() : null;
							if (zindex == null || "".equals(zindex)) {
								exportRectangle(rect, gridCell);
							} else {
								writeCellStart(gridCell);
								writer.write(">");
								writeCellEnd(gridCell);
							}
						} else if (element instanceof RXPrintEllipse) {
							RXPrintEllipse ellipse = (RXPrintEllipse) element;
							String zindex = RXProperties.getProperty(ellipse, PROPERTY_HTML_ZINDEX);
							zindex = zindex != null ? zindex.trim() : null;
							if (zindex == null || "".equals(zindex)) {
								exportRectangle(ellipse, gridCell);
							} else {
								writeCellStart(gridCell);
								writer.write(">");
								writeCellEnd(gridCell);
							}
						} else if (element instanceof RXPrintImage) {
							RXPrintImage image = (RXPrintImage) element;
							String zindex = RXProperties.getProperty(image, PROPERTY_HTML_ZINDEX);
							zindex = zindex != null ? zindex.trim() : null;
							if (zindex == null || "".equals(zindex)) {
								exportImage(image, gridCell);
							} else {
								writeCellStart(gridCell);
								writer.write(">");
								writeCellEnd(gridCell);
							}
						} else if (element instanceof RXPrintText) {
							RXPrintText text = (RXPrintText) element;
							String zindex = RXProperties.getProperty(text, PROPERTY_HTML_ZINDEX);
							zindex = zindex != null ? zindex.trim() : null;
							if (zindex == null || "".equals(zindex)) {
								exportText(text, gridCell);
							} else {
								writeCellStart(gridCell);
								writer.write(">");
								writeCellEnd(gridCell);
							}
						} else if (element instanceof RXPrintFrame) {
							RXPrintFrame frame = (RXPrintFrame) element;
							exportFrame(frame, gridCell);
						} else if (element instanceof RXGenericPrintElement) {
							exportGenericElement((RXGenericPrintElement) element, gridCell, rowHeight);
						}

						if (thTag != null
								&& (RXPdfExporterTagHelper.TAG_END.equals(thTag) || RXPdfExporterTagHelper.TAG_FULL
										.equals(thTag))) {
							thDepth--;
						}
					}

					x += gridCell.getColSpan() - 1;
				}
				writer.write("</tr>\n");
			}
		}

		if (whitePageBackground) {
			restoreBackcolor();
		}

		writer.write("</table>\n");
	}

	private boolean hasEmptyCell(RXExporterGridCell[] gridRow) {
		if (gridRow[0].getWrapper() == null) // quick exit
		{
			return true;
		}

		boolean hasEmptyCell = false;
		for (int x = 1; x < gridRow.length; x++) {
			if (gridRow[x].getWrapper() == null) {
				hasEmptyCell = true;
				break;
			}
		}

		return hasEmptyCell;
	}

	protected void writeEmptyCell(RXExporterGridCell cell, int rowHeight) throws IOException {
		String cellTag = getCellTag(cell);

		writer.write("  <" + cellTag);
		if (cell.getColSpan() > 1) {
			writer.write(" colspan=\"" + cell.getColSpan() + "\"");
		}

		StringBuffer styleBuffer = new StringBuffer();
		appendBackcolorStyle(cell, styleBuffer);
		appendBorderStyle(cell.getBox(), styleBuffer);

		String style = "";
		if (styleBuffer.length() > 0) {
			style = styleBuffer.toString();
		}

		writer.write(emptyCellStringProvider.getStringForCollapsedTD(webUrl.equalsIgnoreCase("") ? imagesURI : webUrl,
				style, cell.getWidth(), rowHeight));
		writer.write("</" + cellTag + ">\n");
	}

	/**
	 *
	 */
	protected void exportLine(RXPrintLine line, RXExporterGridCell gridCell) throws IOException {
		writeCellStart(gridCell);

		StringBuffer styleBuffer = new StringBuffer();

		appendBackcolorStyle(gridCell, styleBuffer);

		String side = null;
		float ratio = line.getWidth() / line.getHeight();
		if (ratio > 1) {
			if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN) {
				side = "top";
			} else {
				side = "bottom";
			}
		} else {
			if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN) {
				side = "left";
			} else {
				side = "right";
			}
		}

		appendPen(styleBuffer, line.getLinePen(), side);

		if (styleBuffer.length() > 0) {
			writer.write(" style=\"");
			writer.write(styleBuffer.toString());
			writer.write("\"");
		}

		writer.write(">");

		writer.write(emptyCellStringProvider.getStringForEmptyTD(webUrl.equalsIgnoreCase("") ? imagesURI : webUrl));

		writeCellEnd(gridCell);
	}

	/**
	 *
	 */
	protected void writeCellStart(RXExporterGridCell gridCell) throws IOException {
		writer.write("  <" + getCellTag(gridCell));
		if (gridCell.getColSpan() > 1) {
			writer.write(" colspan=\"" + gridCell.getColSpan() + "\"");
		}
		if (gridCell.getRowSpan() > 1) {
			writer.write(" rowspan=\"" + gridCell.getRowSpan() + "\"");
		}

		// 웝 표준에서 ID 중복 오류로 제거
		// if (gridCell.getWrapper() != null)
		// {
		// RXPrintElement element = gridCell.getWrapper().getElement();
		// if (element != null)
		// {
		// String id = RXProperties.getProperty(element, PROPERTY_HTML_ID);
		// if (id != null)
		// {
		// writer.write(" id=\"" + id +"\"");
		// }
		// }
		// }
	}

	/**
	 *
	 */
	protected void writeCellEnd(RXExporterGridCell gridCell) throws IOException {
		writer.write("</" + getCellTag(gridCell) + ">\n");
	}

	/**
	 *
	 */
	protected String getCellTag(RXExporterGridCell gridCell) {
		if (accessibleHtml) {
			if (thDepth > 0) {
				return "th"; // FIXMEHTML th tags have center alignment by
								// default
			} else {
				ElementWrapper wrapper = gridCell.getWrapper();

				OccupiedGridCell occupiedCell = gridCell instanceof OccupiedGridCell ? (OccupiedGridCell) gridCell
						: null;
				if (occupiedCell != null) {
					wrapper = occupiedCell.getOccupier().getWrapper();
				}

				if (wrapper != null) {
					String cellContentsType = wrapper.getProperty(RXCellContents.PROPERTY_TYPE);
					if (RXCellContents.TYPE_CROSSTAB_HEADER.equals(cellContentsType)
							|| RXCellContents.TYPE_COLUMN_HEADER.equals(cellContentsType)
							|| RXCellContents.TYPE_ROW_HEADER.equals(cellContentsType)) {
						return "th";
					}
				}
			}
		}

		return "td";
	}

	protected void exportRectangle(RXPrintGraphicElement element) throws IOException {
		StringBuffer styleBuffer = new StringBuffer();

		appendBackcolorStyle(element.getBackcolor(), styleBuffer);

		appendPen(styleBuffer, element.getLinePen(), null);

		String w = toSizeUnit(element.getWidth());
		String h = toSizeUnit(element.getHeight());
		String zindex = RXProperties.getProperty(element, PROPERTY_HTML_ZINDEX);
		String cls = "rxe-absolute-position";

		writer.write("<div");
		writer.write(" style=\"left: ");
		writer.write(toSizeUnit(element.getX()) + ";");
		writer.write(" top: ");
		writer.write(toSizeUnit(element.getY()) + ";");
		writer.write(" width:" + w + ";");
		writer.write(" height:" + h + ";");
		if (zindex != null && !"".equals(zindex)) {
			writer.write("z-index:" + zindex + ";");
		}

		int r = 0;
		if (element instanceof RXPrintEllipse) {
			RXPrintEllipse ellipse = (RXPrintEllipse) element;
			int w1 = ellipse.getWidth();
			int h1 = ellipse.getHeight();
			r = w1 > h1 ? h1 : w1;
		} else {
			RXPrintRectangle rect = (RXPrintRectangle) element;
			r = rect.getRadius();
		}

		if (r > 0) {
			String rs = String.valueOf(r);
			writer.write("border-radius: " + rs + "px;");
			writer.write("-webkit-border-radius:" + rs + "px;");
			writer.write("-moz-border-radius:" + rs + "px;");
			if (isIE == true) {
				cls += " rxe-round";
			}
		}

		if (styleBuffer.length() > 0) {
			writer.write(styleBuffer.toString() + "\"");
		}
		writer.write("class=\"" + cls + "\"");
		writer.write(">");
		writer.write("</div>");

	}

	/**
	 *
	 */
	protected void exportRectangle(RXPrintGraphicElement element, RXExporterGridCell gridCell) throws IOException {
		writeCellStart(gridCell);
		writer.write(">");

		StringBuffer styleBuffer = new StringBuffer();
		appendBackcolorStyle(gridCell, styleBuffer);
		appendPen(styleBuffer, element.getLinePen(), null);

		String w = toSizeUnit(element.getWidth());
		String h = toSizeUnit(element.getHeight());
		writer.write("<div style=\"left: ");
		writer.write(" width:" + w + ";");
		writer.write(" height:" + h + ";");
		String cls = "";

		int r = 0;
		if (element instanceof RXPrintEllipse) {
			RXPrintEllipse ellipse = (RXPrintEllipse) element;
			int w1 = ellipse.getWidth();
			int h1 = ellipse.getHeight();
			r = w1 > h1 ? h1 : w1;
		} else {
			RXPrintRectangle rect = (RXPrintRectangle) element;
			r = rect.getRadius();
		}

		if (r > 0) {
			String rs = String.valueOf(r);
			writer.write("border-radius:" + rs + "px;");
			writer.write("-webkit-border-radius:" + rs + "px;");
			writer.write("-moz-border-radius:" + rs + "px;");
			if (isIE == true) {
				cls += " rxe-round";
			}
		}
		if (styleBuffer.length() > 0) {
			writer.write(styleBuffer.toString() + "\"");
		}
		if (!"".equals(cls)) {
			writer.write(" class=\"" + cls + "\"");
		}
		writer.write(">");
		writer.write(emptyCellStringProvider.getStringForEmptyTD(webUrl.equalsIgnoreCase("") ? imagesURI : webUrl));
		writer.write("</div>");
		writeCellEnd(gridCell);
	}

	/**
	 *
	 */
	protected void exportStyledText(RXPrintText rxText, Locale locale, String id, String name) throws IOException {
		exportStyledText(rxText, null, null, null, locale, id, name);
	}

	/**
	 *
	 */
	protected void exportStyledText(RXPrintText printText, String alt, String title, String css_class, Locale locale,
			String id, String name) throws IOException {
		String width = null;
		String tagValue = null;
		String script = null;
		String radiovalue = null;
		String tag_desc = null;
		String tag_type = null;
		String tabindex = null;
		
		// String html_value = null;
		String disabled = null;

		if (id != null && !"".equals(id)) {
			width = toSizeUnit(printText.getWidth());
			tagValue = RXProperties.getProperty(printText, PROPERTY_HTML_TAGVALUE);
			script = RXProperties.getProperty(printText, PROPERTY_HTML_SCRIPT);
			radiovalue = RXProperties.getProperty(printText, PROPERTY_HTML_RADIOVALUE);
			tag_desc = RXProperties.getProperty(printText, PROPERTY_HTML_DESCRIPTION);
			tag_type = RXProperties.getProperty(printText, PROPERTY_HTML_TAG_TYPE);
			disabled = RXProperties.getProperty(printText, PROPERTY_HTML_DISABLED);
			tabindex = RXProperties.getProperty(printText, PROPERTY_HTML_TABINDEX);
		}

		RXStyledText styledText = getStyledText(printText);
		String text = styledText.getText();

		int runLimit = 0;

		AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();

		boolean first = true;
		boolean startedSpan = false;
		while (runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length()) {
			// if there are several text runs, write the tooltip into a parent
			// <span>
			if (first && runLimit < styledText.length()) {
				startedSpan = true;

				String desc = "";
				if (alt != null && !alt.equalsIgnoreCase("")) {
					desc = desc + "alt=\"" + alt + "\" ";
				}
				if (title != null && !title.equalsIgnoreCase("")) {
					desc = desc + "title=\"" + title + "\" ";
				}

				if (id != null && !"".equalsIgnoreCase(id)) {
					writer.write("<span id=\"" + getHtmlID(id) + "\" " + desc + ">");
				} else {
					writer.write("<span " + desc + ">");
				}
				alt = null;
				title = null;
			}
			first = false;

			exportStyledTextRun(iterator.getAttributes(), text.substring(iterator.getIndex(), runLimit), tag_type, alt,
					title, css_class, locale, id, name, tagValue, radiovalue, script, width, disabled, tabindex);

			iterator.setIndex(runLimit);
		}

		if (startedSpan) {
			writer.write("</span>");
		}
	}

	/**
	 *
	 */
	// protected void exportStyledTextRun(Map attributes, String text, Locale
	// locale, String id) throws IOException {
	// exportStyledTextRun(attributes, text, null, null, locale, id);
	// }

	/**
	 *
	 */
	protected void exportStyledTextRun(Map attributes, String text, String tag_type, String alt, String title,
			String css_class, Locale locale, String id, String name, String tagValue, String radiovalue, String script,
			String width, String disabled, String tabindex) throws IOException {

		tabindex = tabindex==null ? "" : tabindex;
		
		if (id != null) {
			log.debug("exportStyledTextRun, id=" + id + ",name:" + name);
			
			/**
			 * tabindex가 정의 되지 않은 경우
			 * 순서에 따라 정의한다.
			 * 단 일부만 tabindex가 정의된 경우 tabindex가 중복될 수 있다.
			 */
			if("".equals(tabindex) && id.indexOf("scrollpoint")<0){
				tabIndex++;
				tabindex = String.valueOf(tabIndex);
			}
			
			if(!"".equals(tabindex)){
				tabindex = " tabindex='" + tabindex + "' ";
			}else{
				tabindex = " ";
			}
		}else{
			tabindex = " ";
		}

		String fontFamilyAttr = (String) attributes.get(TextAttribute.FAMILY);
		String fontFamily = fontFamilyAttr;

		name = (name == null || name == "") ? id : name;

		/*
		 * 스크립트
		 */
		script = (script == null) ? "" : script;

		/*
		 * 태그 값
		 */
		tagValue = (tagValue == null) ? "" : tagValue;

		/*
		 * radio button 값
		 */
		radiovalue = (radiovalue == null) ? "" : radiovalue;

		/*
		 * alt 값
		 */
		alt = (alt == null) ? "" : alt;

		/*
		 * title 값
		 */
		title = (title == null) ? "" : title;

		/*
		 * CSS Class 값
		 */
		css_class = (css_class == null) ? "" : css_class;

		/*
		 * disable 값
		 */
		disabled = (disabled == null) ? "" : disabled;

		String[] tag = null;
		String type = "";
		String value = "";
		if (text.indexOf("|") > 0) {
			tag = StringUtils.split(text, "|");
			type = tag[0];
			value = tag[1];
		} else {
			type = text;
			value = "";
		}

		type = (tag_type != null && !"".equals(tag_type)) ? tag_type : type;
		// "button"으로 표시하지 않을 id 값 입력
		if (id != null && id.startsWith("scrollpoint")) {
			type = "";
		}

		if (fontMap != null && fontMap.containsKey(fontFamilyAttr)) {
			fontFamily = (String) fontMap.get(fontFamilyAttr);
		} else {
			FontInfo fontInfo = RXFontUtil.getFontInfo(fontFamilyAttr, locale);
			if (fontInfo != null) {
				// fontName found in font extensions
				FontFamily family = fontInfo.getFontFamily();
				String exportFont = family.getExportFont(getExporterKey());
				if (exportFont != null) {
					fontFamily = exportFont;
				}
			}
		}

		String style = "";
		style += "font-family: '" + fontFamily + "'; ";

		Color forecolor = (Color) attributes.get(TextAttribute.FOREGROUND);
		if (!hyperlinkStarted || !Color.black.equals(forecolor)) {
			if ("textconfirm".equalsIgnoreCase(type) && !id.endsWith("_input")) {
				// 칼러 표시 (class 명 : textconfirmfiled)
				// style += "color: #" + RXColorUtil.getColorHexa(forecolor) +
				// "; ";
			} else if (!StringUtils.isNull(type)) {
				// css에서 color 수정
				// style += "color: #" + RXColorUtil.getColorHexa(forecolor) +
				// "; ";
			} else {
				log.debug("++++++++++++++++++++++++++" + id);
				style += "color: #" + RXColorUtil.getColorHexa(forecolor) + "; ";
			}
		}

		if ("textconfirm".equalsIgnoreCase(type) && id.endsWith("_input")) {
			text = "";
			style += "background-color: transparent; color:#ff0000;";
			log.debug(id + "," + type + "," + text);
		} else if ("textconfirm".equalsIgnoreCase(type)) {
			// // css에서 backcolor 수정
			// Color runBackcolor = (Color)
			// attributes.get(TextAttribute.BACKGROUND);
			// if (runBackcolor != null) {
			// style += "background-color: #" +
			// RXColorUtil.getColorHexa(runBackcolor) + "; ";
			// }
		} else {
			Color runBackcolor = (Color) attributes.get(TextAttribute.BACKGROUND);
			if (runBackcolor != null) {
				style += "background-color: #" + RXColorUtil.getColorHexa(runBackcolor) + "; ";
			}
		}
		
		style += "font-size: " + toSizeUnit(((Float) attributes.get(TextAttribute.SIZE)).intValue()) + "; ";
		if(forecolor!=null){
			style += "color: #" + RXColorUtil.getColorHexa(forecolor) + "; ";
		}
		
		/*
		 * if (!horizontalAlignment.equals(CSS_TEXT_ALIGN_LEFT)) {
		 * writer.write(" text-align: "); writer.write(horizontalAlignment);
		 * writer.write(";"); }
		 */

		if (TextAttribute.WEIGHT_BOLD.equals(attributes.get(TextAttribute.WEIGHT))) {
			style += "font-weight: bold; ";
		}
		if (TextAttribute.POSTURE_OBLIQUE.equals(attributes.get(TextAttribute.POSTURE))) {
			style += "font-style: italic; ";
		}
		if (TextAttribute.UNDERLINE_ON.equals(attributes.get(TextAttribute.UNDERLINE))) {
			style += "text-decoration: underline; ";
		}
		if (TextAttribute.STRIKETHROUGH_ON.equals(attributes.get(TextAttribute.STRIKETHROUGH))) {
			style += "text-decoration: line-through; ";
		}

		if (TextAttribute.SUPERSCRIPT_SUPER.equals(attributes.get(TextAttribute.SUPERSCRIPT))) {
			style += "vertical-align: super; ";
		} else if (TextAttribute.SUPERSCRIPT_SUB.equals(attributes.get(TextAttribute.SUPERSCRIPT))) {
			style += "vertical-align: sub; ";
		}

		/*
		 * alt와 title 속성 설정
		 */
		String desc = "";
		if (!title.equalsIgnoreCase("")) {
			desc = desc + "title=\"" + title + "\" ";
		} else if (!alt.equalsIgnoreCase("")) {
			desc = desc + "title=\"" + alt + "\" ";
		}

		if (id == null || "".equalsIgnoreCase(id)) {
			// log.debug("id == null || ''.equalsIgnoreCase(id)");
			if (StringUtils.isNull(css_class)) {
				writer.write("<span " + desc + " style=\"" + style + "\">");
			} else {
				writer.write("<span " + desc + " class=\"" + css_class + "\" style=\"" + style + "\">");
			}
			if (text == null || text.equalsIgnoreCase("")) {
				writer.write(RXStringUtil.htmlEncode(" "));
			} else {
				String s = RXStringUtil.htmlEncode(text);
				writer.write(fixNbsp(s));
			}
			writer.write("</span>");
		} else {
			// html disabled
			String disabledValue = "";

			if ("1".equals(disabled)) {
				disabledValue = "disabled=\"disabled\"";
			}

			/*
			 * 일반 테스트인 경우
			 */
			if ("text".equalsIgnoreCase(type)) {
				String sc = script + " onmouseover=\"MouseOver('" + id + "')\" onmouseout=\"MouseOut('" + id + "')\" ";
				writer.write("<span id=\"" + getHtmlID(id) + "\" " + desc + " " + sc + " style=\"" + style + "\">");
				if (width != null) {
					style += " width:" + width + "; ";
				}
				if (StringUtils.isNull(css_class)) {
					writer.write("<input" + tabindex + "class=\"textfiled\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id
							+ "\" " + script + " style=\"" + style + "\" " + disabledValue);
				} else {
					writer.write("<input" + tabindex + "class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\""
							+ id + "\" " + script + " style=\"" + style + "\" " + disabledValue);
				}
				writer.write(" value=\"" + value + "\"/>");
			}
			/*
			 * 확인 텍스트인 경우
			 */
			else if ("textconfirm".equalsIgnoreCase(type)) {
				/**
				 * textconfirm인 경우 전에 정의된 script를 확인 한 후 수정한다.
				 * [buttonconfirm인 경우 참고]
				 * 
				 * System.out.println("확인 텍스트인 경우 textconfirm >>>>>>>>>>>>>>>>>>>>>>> script = [" + script + "]");
				 */
				
				String sc = script + " onmouseover=\"MouseOver('" + id + "')\" onmouseout=\"MouseOut('" + id + "')\"";
				
				writer.write("<span id=\"" + getHtmlID(id) + "\" " + desc + " " + sc + " style=\"" + style + "\">");
				if (width != null) {
					style += " width:" + width + "; ";
				}
				if (StringUtils.isNull(css_class)) {
					if (id.endsWith("_input")) {
						writer.write("<input" + tabindex + "class=\"textfiled\" type=\"text\" name=\"" + name + "\" id=\"" + id
								+ "\" " + script + " style=\"" + style + "\" " + disabledValue);
					} else {
						writer.write("<input" + tabindex + "class=\"textconfirmfiled\" type=\"text\" name=\"" + name + "\" id=\"" + id
								+ "\" " + script + " style=\"" + style + "\" disabled='true'");
					}
				} else {
					if (id.endsWith("_input")) {
						writer.write("<input" + tabindex + "class=\"text" + css_class + "\" name=\"" + name + "\" id=\"" + id + "\" "
								+ script + " style=\"" + style + "\" " + disabledValue);
					} else {
						writer.write("<input" + tabindex + "class=\"confirm" + css_class + "\" name=\"" + name + "\" id=\"" + id
								+ "\" " + script + " style=\"" + style + "\" disabled='true'");
					}
				}
				// writer.write(" value=\"" + text + "\"/>");
				writer.write(" value=\"" + "" + "\"/>");
			}
			/*
			 * 버튼인 경우
			 */
			else if ("buttonconfirm".equalsIgnoreCase(type)) {
				if(script!=null && !"".equals(script)){
					/**
					 * 스크립트가 있는 경우 onclick 이벤트에 onkeypress 이벤트도 추가하여
					 * 키보드로도 입력이 가능하도록함.
					 */
					String spanscript = script;
					spanscript = StringUtil.replaceAll(spanscript, "onclick", "onkeypress");
					if(spanscript.indexOf("doBtnClick")>-1){
						spanscript = StringUtil.replaceAll(spanscript, "doBtnClick", "doBtnKeyPress");
					}
					spanscript = StringUtil.replaceAll(spanscript, "this", "event, this");
					script = script + " " + spanscript;
					
				}
//				System.out.println("버튼인 경우 buttonconfirm >>>>>>>>>>>>>>>>>>>>>>> script = [" + script + "]");

				String sc = script + " onmouseover=\"MouseOver('" + id + "')\" onmouseout=\"MouseOut('" + id + "')\"";
				writer.write("<span id=\"" + getHtmlID(id) + "\" " + desc + " " + sc + " style=\"" + style + "\">");
				if (width != null) {
					style += " width:" + width + "; ";
				}

				if (StringUtils.isNull(css_class)) {
					writer.write("<span" + tabindex + "class=\"buttonconfirm\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id
							+ "\" " + script + " style=\"" + style + "\"");
				} else {
					writer.write("<span" + tabindex + "class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\""
							+ id + "\" " + script + " style=\"" + style + "\"");
				}
				writer.write(">" + text.replaceAll("\n", "<br/>") + "</span>");
			}
			/*
			 * 버튼인 경우
			 */
			else if ("button".equalsIgnoreCase(type)) {
				/**
				 * button인 경우 전에 정의된 script를 확인 한 후 수정한다.
				 * [buttonconfirm인 경우 참고]
				 * 
				 * System.out.println("버튼인 경우 button >>>>>>>>>>>>>>>>>>>>>>> script = [" + script + "]");
				 */
				
				String sc = script + " onmouseover=\"MouseOver('" + id + "')\" onmouseout=\"MouseOut('" + id + "')\"";
				writer.write("<span id=\"" + getHtmlID(id) + "\" " + desc + " " + sc + " style=\"" + style + "\">");
				if (width != null) {
					style += " width:" + width + "; ";
				}
				
				if (StringUtils.isNull(css_class)) {
					writer.write("<input" + tabindex + "class=\"button\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id
							+ "\" " + script + " style=\"" + style + "\" " + disabledValue);
				} else {
					writer.write("<input" + tabindex + "class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\""
							+ id + "\" " + script + " style=\"" + style + "\" " + disabledValue);
				}
				writer.write(" value=\"" + value + "\"/>");
			}
			/*
			 * radio button 인 경우
			 */
			else if ("radio".equalsIgnoreCase(type) || "checkbox".equalsIgnoreCase(type)) {
				String checked = "";
				String spanID = "span_" + id;
				/**
				 * 체크박스와 라디오 버튼인 경우 CSS의 radiobutton를 무시하고
				 * span에 radiocheckspan 클래스만 사용한다(ess.css 참고).
				 */
				String radiocheckspan = "";
				radiocheckspan = " class='radiocheckspan' ";
				String act = " onkeypress=\"doEvent(event,'"+id+"');\" ";

				String sc = script + " onfocus=\"Focus('" + spanID + "')\" onmouseover=\"MouseOver('" + id + "')\" onmouseout=\"MouseOut('" + id + "')\"";
				
				writer.write("<span" + tabindex + " id=\"" + spanID + "\" " + desc + radiocheckspan + "style=\"" + style + "\"" + act + " " + sc + ">");

				/**
				 * 체크박스와 라디오 버튼인 경우 span에 tabindex를 정의하고
				 * 각 요소는 무시한다.
				 */
				tabindex = " tabindex=\"-1000\" ";
					
				/*
				 * checkbox나 radio button의 경우 선택된 요소는 checked="checked"로 성정해야 웹
				 * 표준에 맞음
				 */
				if (tagValue.equalsIgnoreCase("1")) {
					checked = "checked=\"checked\" ";
				}
				
				script = script + " onfocus=\"myRadioCheckFocus(this)\"";

				if ("radio".equalsIgnoreCase(type)) {
					String radioID = id;
					if (StringUtils.isNull(id)) {
						/*
						 * radio button의 경우 id가 중복되면 안되기 때문에 id를 증가시키면서 추가하고
						 * name은 같은 그룹 내에서는 같은 이름으로 설정한다.
						 */
						radioIndex++;
						radioID = "radio" + String.valueOf(radioIndex);
					}
					if (disabledValue.startsWith("disabled")) {
						css_class = " ";
					} else if (StringUtils.isNull(css_class)) {
						css_class = " "; //"radiobutton";
					}

					if (StringUtils.isNull(css_class)) {
						writer.write("<input" + tabindex + "class=\"radiobutton\" type=\"" + type + "\" name=\"" + name + "\" id=\""
								+ radioID + "\" " + script + " value=\"" + radiovalue + "\" " + checked + " "
								+ disabledValue + "/>");
//						writer.write("<label" + tabindex + "class='radiobutton' for='"+radioID+"'></label>");
					} else {
						writer.write("<input" + tabindex + "class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name
								+ "\" id=\"" + radioID + "\" " + script + " value=\"" + radiovalue + "\" " + checked
								+ " " + disabledValue + "/>");
//						writer.write("<label" + tabindex + "class='"+css_class+"' for='"+radioID+"'></label>");
					}
				} else {
					if (disabledValue.startsWith("disabled")) {
						css_class = " ";
					} else if (StringUtils.isNull(css_class)) {
						css_class = " "; //"checkbox";
					}

					if (StringUtils.isNull(css_class)) {
						writer.write("<input" + tabindex + "class=\"checkbox\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id
								+ "\" " + script + " value=\"" + tagValue + "\" " + checked + " " + disabledValue
								+ "/>");
//						writer.write("<label" + tabindex + "class='checkbox' for='"+id+"'></label>");
					} else {
						writer.write("<input" + tabindex + "class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name
								+ "\" id=\"" + id + "\" " + script + " value=\"" + tagValue + "\" " + checked + " "
								+ disabledValue + "/>");
//						writer.write("<label" + tabindex + "class='"+css_class+"' for='"+id+"'></label>");
					}
				}
				if (value != null && !value.equalsIgnoreCase("")) {
					writer.write(RXStringUtil.htmlEncode(value));
				}
			}
			/*
			 * select 태그인 경우
			 */
			else if ("select".equalsIgnoreCase(type)) {
				String sc = script + " onmouseover=\"MouseOver('" + id + "')\" onmouseout=\"MouseOut('" + id + "')\"";
				writer.write("<span id=\"" + getHtmlID(id) + "\" " + desc + " " + sc + " style=\"" + style + "\">");

				if (width != null) {
					style += " width:" + width + "; ";
				}
				if (StringUtils.isNull(css_class)) {
					writer.write("<select" + tabindex + "class=\"selectbox\" name=\"" + name + "\" id=\"" + id + "\" " + script
							+ " style=\"" + style + "\" " + disabledValue + ">");
				} else {
					writer.write("<select" + tabindex + "class=\"" + css_class + "\" name=\"" + name + "\" id=\"" + id + "\" "
							+ script + " style=\"" + style + "\" " + disabledValue + ">");
				}
				writer.write(tagValue);
				writer.write("</select>");
				if (value != null && !value.equalsIgnoreCase("")) {
					writer.write(RXStringUtil.htmlEncode(value));
				}
			}
			/*
			 * 기타
			 */
			else {
				/*
				 * 스크립트가 있는 경우
				 */
				if (script != null && !"".equals(script)) {
					/**
					 * 스크립트가 있는 경우 onclick 이벤트에 onkeypress 이벤트도 추가하여
					 * 키보드로도 입력이 가능하도록함.
					 */
					String spanscript = script;
					spanscript = StringUtil.replaceAll(spanscript, "onclick", "onkeypress");
					if(spanscript.indexOf("doSign")>-1){
						spanscript = StringUtil.replaceAll(spanscript, "doSign", "doSignKeyPress");
					}
					if(spanscript.indexOf("doClick")>-1){
						spanscript = StringUtil.replaceAll(spanscript, "doClick", "doClickKeyPress");
					}
					spanscript = StringUtil.replaceAll(spanscript, "this", "event, this");
					
					script = script + " " + spanscript;
					String sc = script + " onmouseover=\"MouseOver('" + id + "')\" onmouseout=\"MouseOut('" + id + "')\"";
					
					/**
					 * 디버깅 용
					 * 
					 * System.out.println("스크립트가 있는 경우 >>>>>>>>>>>>>>>>>>>>>>> script = [" + script + "]");
					 */
					
					if (css_class == null || "".equals(css_class)) {
						writer.write("<span" + tabindex + "class=\"button\" id=\"" + id + "\" " + desc
								+ " style=\"cursor:pointer;cursor:hand; " + style + "\" " + sc + ">");
					} else {
						writer.write("<span" + tabindex + "class=\"" + css_class + "\" id=\"" + id + "\" " + desc
								+ " style=\"cursor:pointer;cursor:hand; " + style + "\" " + sc + ">");
					}
				} else {
					writer.write("<span" + tabindex + "id=\"" + id + "\" " + desc + " style=\" " + style + "\">");
				}
				if (text == null || "".equalsIgnoreCase(text)) {
					writer.write(RXStringUtil.htmlEncode(" "));
				} else {
					String s = RXStringUtil.htmlEncode(text);
					writer.write(fixNbsp(s));
				}
			}
			writer.write("</span>");
		}
	}

	private String fixNbsp(String text) {
		char c = 160;
		String ss = Character.toString(c);
		String s = text;
		if (text != null) {
			if (text.indexOf(ss) >= 0) {
				try {
					s = StringUtils.replaceAll(text, ss, "&nbsp;");
				} catch (Exception e) {
					s = text;
				}
			}
		}
		return s;
	}

	protected void exportText(RXPrintText text) throws IOException {
		String id = RXProperties.getProperty(text, PROPERTY_HTML_ID);
		String name = RXProperties.getProperty(text, PROPERTY_HTML_NAME);

		if (id != null) {
			log.debug("exportText(RXPrintText text), id=" + id + ",name:" + name);
		} else {
			// log.debug("exportText(RXPrintText text), id="+ id
			// +",name:"+name);
		}

		String s = text.getFullText();

		/* 테스트가 null이거나 공백인 경우 HTML에서 &nbsp로 바꾸기 위함 */
		if (s == null || s.equalsIgnoreCase("")) {
			text.setText(" ");
		}

		/*
		 * alt 속성 가져오기
		 */
		String alt = RXProperties.getProperty(text, PROPERTY_HTML_ALT);

		/*
		 * title 속성 가져오기
		 */
		String title = RXProperties.getProperty(text, PROPERTY_HTML_TITLE);

		/*
		 * CSS Class 속성 가져오기
		 */
		String css_class = RXProperties.getProperty(text, PROPERTY_HTML_CLASS);

		/*
		 * title 속성 가져오기
		 */
		String tag_type = RXProperties.getProperty(text, PROPERTY_HTML_TAG_TYPE);
		RXStyledText styledText = getStyledText(text);

		int textLength = 0;

		if (styledText != null) {
			textLength = styledText.length();
		}

		if (text.getRunDirectionValue() == RunDirectionEnum.RTL) {
			writer.write(" dir=\"rtl\"");
		}

		StringBuffer styleBuffer = new StringBuffer();

		String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;

		switch (text.getVerticalAlignmentValue()) {
		case BOTTOM: {
			verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
			break;
		}
		case MIDDLE: {
			verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
			break;
		}
		case TOP:
		default: {
			verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
		}
		}

		if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP)) {
			styleBuffer.append(" vertical-align: ");
			styleBuffer.append(verticalAlignment);
			styleBuffer.append(";");
		}

		int[] ctlpos = { -1, -1 };
		RXLineBox box = text.getLineBox();
		if (box != null) {
			ctlpos[0] = text.getX();
			ctlpos[1] = text.getWidth();

			// System.out.println("if (box != null)  Control 끝:"
			// +ctlpos_End+","+toSizeUnit(ctlpos_End));

			RXPropertiesMap propertiMape = text.getPropertiesMap();
			appendBorderStyle(box, styleBuffer, propertiMape, ctlpos);
		}

		String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;

		if (textLength > 0) {
			switch (text.getHorizontalAlignmentValue()) {
			case RIGHT: {
				horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
				break;
			}
			case CENTER: {
				horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
				break;
			}
			case JUSTIFIED: {
				horizontalAlignment = CSS_TEXT_ALIGN_JUSTIFY;
				break;
			}
			case LEFT:
			default: {
				horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
			}
			}

			if ((text.getRunDirectionValue() == RunDirectionEnum.LTR && !horizontalAlignment
					.equals(CSS_TEXT_ALIGN_LEFT))
					|| (text.getRunDirectionValue() == RunDirectionEnum.RTL && !horizontalAlignment
							.equals(CSS_TEXT_ALIGN_RIGHT))) {
				styleBuffer.append("text-align: ");
				styleBuffer.append(horizontalAlignment);
				styleBuffer.append(";");
			}
		}

		if (isWrapBreakWord) {
			styleBuffer.append("width: " + toSizeUnit(text.getWidth()) + "; ");
			styleBuffer.append("word-wrap: break-word; ");
		}

		Float lineSpacingSize = text.getParagraph().getLineSpacingSize();
		LineSpacingEnum lineSpacing = text.getParagraph().getLineSpacing();

		switch (lineSpacing) {
		case SINGLE:
		default: {
			// styleBuffer.append(" line-height: 1; *line-height: normal;");
			break;
		}
		case ONE_AND_HALF: {
			styleBuffer.append(" line-height: 1.5;");
			break;
		}
		case DOUBLE: {
			styleBuffer.append(" line-height: 2.0;");
			break;
		}
		case PROPORTIONAL: {
			if (lineSpacingSize != null) {
				styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + ";");
			}
			break;
		}
		case AT_LEAST:
		case FIXED: {
			if (lineSpacingSize != null) {
				styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + "px;");
			}
			break;
		}
		}

		if (text.getLineBreakOffsets() != null) {
			// if we have line breaks saved in the text, set nowrap so that
			// the text only wraps at the explicit positions
			styleBuffer.append("white-space: nowrap; ");
		}

		String w = toSizeUnit(text.getWidth());
		String h = toSizeUnit(text.getHeight());
		String zindex = RXProperties.getProperty(text, PROPERTY_HTML_ZINDEX);
		
		writer.write("<div class=\"rxe-absolute-position\"");

		writer.write(" style=\"left: ");
		writer.write(toSizeUnit(text.getX()) + ";");
		writer.write(" top: ");
		writer.write(toSizeUnit(text.getY()) + ";");
		writer.write(" width:" + w + ";");
		writer.write(" height:" + h + ";");
		// if (verticalAlignment.equals(HTML_VERTICAL_ALIGN_MIDDLE)) {
		// writer.write(" line-height: ");
		// writer.write(h);
		// writer.write(";");
		// }
		if (zindex != null && !"".equals(zindex)) {
			writer.write("z-index:" + zindex + ";");
		}

		if (text.getModeValue() != ModeEnum.TRANSPARENT) {
			Color bgColor = text.getBackcolor();
			appendBackcolorStyle(bgColor, styleBuffer);
		}

		writer.write("display: table;\">");
		writer.write("<div style=\"display: table-cell;");
		if (styleBuffer.length() > 0) {
			writer.write(styleBuffer.toString().trim());
		}
		writer.write("\">");

		if (text.getAnchorName() != null) {
			writer.write("<a name=\"");
			writer.write(text.getAnchorName());
			writer.write("\"/>");
		}

		startHyperlink(text);

		if (textLength > 0) {
			// only use text tooltip when no hyperlink present
			String textTooltip = hyperlinkStarted ? null : text.getHyperlinkTooltip();

			// // buttonconfirm인경우 자동 id 생성
			// if ("buttonconfirm".equalsIgnoreCase(tag_type) && id == null) {
			// btnconfirmIndex++;
			// id = "buttonconfirmautoid" + String.valueOf(btnconfirmIndex);
			// }

			if (id != null && !id.startsWith("scrollpoint")) {
				essParameters.put(id, null);
				log.debug("(1)formList.add(text):" + id + "," + text.getFullText());
				formList.add(text);
				formListPage.add(current_page);
			}

			/*
			 * 하이퍼링크 툴팁과 title이 동시에 설정된 경우 title 속성이 우선한다.
			 */
			textTooltip = (title != null && !title.equalsIgnoreCase("")) ? title : textTooltip;

			exportStyledText(text, alt, title, css_class, getTextLocale(text), id, name);
		} else {
			writer.write(emptyCellStringProvider.getStringForEmptyTD(webUrl.equalsIgnoreCase("") ? imagesURI : webUrl));
		}

		endHyperlink();

		writer.write("</div>");
		writer.write("</div>");

		// log.debug("1+++++++++++++++++++"+tag_type);
		if ("textconfirm".equalsIgnoreCase(tag_type)) {
			exportTextConfirm(text);
		}

	}

	private void exportTextConfirm(RXPrintText text) throws IOException {
		String s = text.getFullText();

		/* 테스트가 null이거나 공백인 경우 HTML에서 &nbsp로 바꾸기 위함 */
		if (s == null || s.equalsIgnoreCase("")) {
			text.setText(" ");
		}
		/*
		 * alt 속성 가져오기
		 */
		String alt = RXProperties.getProperty(text, PROPERTY_HTML_ALT);

		/*
		 * title 속성 가져오기
		 */
		String title = RXProperties.getProperty(text, PROPERTY_HTML_TITLE);

		/*
		 * CSS Class 속성 가져오기
		 */
		String css_class = RXProperties.getProperty(text, PROPERTY_HTML_CLASS);

		RXStyledText styledText = getStyledText(text);

		int textLength = 0;

		if (styledText != null) {
			textLength = styledText.length();
		}

		if (text.getRunDirectionValue() == RunDirectionEnum.RTL) {
			writer.write(" dir=\"rtl\"");
		}

		StringBuffer styleBuffer = new StringBuffer();

		String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;

		switch (text.getVerticalAlignmentValue()) {
		case BOTTOM: {
			verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
			break;
		}
		case MIDDLE: {
			verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
			break;
		}
		case TOP:
		default: {
			verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
		}
		}

		if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP)) {
			styleBuffer.append(" vertical-align: ");
			styleBuffer.append(verticalAlignment);
			styleBuffer.append(";");
		}

		RXLineBox box = text.getLineBox();
		if (box != null) {
			appendBorderStyle(box, styleBuffer);
		}

		String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;

		if (textLength > 0) {
			// switch (text.getHorizontalAlignmentValue()) {
			// case RIGHT: {
			// horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
			// break;
			// }
			// case CENTER: {
			// horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
			// break;
			// }
			// case JUSTIFIED: {
			// horizontalAlignment = CSS_TEXT_ALIGN_JUSTIFY;
			// break;
			// }
			// case LEFT:
			// default: {
			// horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
			// }
			// }

			// 강제적으로 left
			horizontalAlignment = CSS_TEXT_ALIGN_LEFT;

			if ((text.getRunDirectionValue() == RunDirectionEnum.LTR && !horizontalAlignment
					.equals(CSS_TEXT_ALIGN_LEFT))
					|| (text.getRunDirectionValue() == RunDirectionEnum.RTL && !horizontalAlignment
							.equals(CSS_TEXT_ALIGN_RIGHT))) {
				styleBuffer.append("text-align: ");
				styleBuffer.append(horizontalAlignment);
				styleBuffer.append(";");
			}
		}

		if (isWrapBreakWord) {
			styleBuffer.append("width: " + toSizeUnit(text.getWidth()) + "; ");
			styleBuffer.append("word-wrap: break-word; ");
		}

		Float lineSpacingSize = text.getParagraph().getLineSpacingSize();
		LineSpacingEnum lineSpacing = text.getParagraph().getLineSpacing();

		switch (lineSpacing) {
		case SINGLE:
		default: {
			// styleBuffer.append(" line-height: 1; *line-height: normal;");
			break;
		}
		case ONE_AND_HALF: {
			styleBuffer.append(" line-height: 1.5;");
			break;
		}
		case DOUBLE: {
			styleBuffer.append(" line-height: 2.0;");
			break;
		}
		case PROPORTIONAL: {
			if (lineSpacingSize != null) {
				styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + ";");
			}
			break;
		}
		case AT_LEAST:
		case FIXED: {
			if (lineSpacingSize != null) {
				styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + "px;");
			}
			break;
		}
		}

		if (text.getLineBreakOffsets() != null) {
			// if we have line breaks saved in the text, set nowrap so that
			// the text only wraps at the explicit positions
			styleBuffer.append("white-space: nowrap; ");
		}

		String w = toSizeUnit(text.getWidth());
		String h = toSizeUnit(text.getHeight());
		String zindex = RXProperties.getProperty(text, PROPERTY_HTML_ZINDEX);

		writer.write("<div class=\"rxe-absolute-position\"");
		writer.write(" style=\"left: ");
		writer.write(toSizeUnit(text.getX()) + ";");
		writer.write(" top: ");
		writer.write(toSizeUnit(text.getY()) + ";");
		writer.write(" width:" + w + ";");
		writer.write(" height:" + h + ";");
		// if (verticalAlignment.equals(HTML_VERTICAL_ALIGN_MIDDLE)) {
		// writer.write(" line-height: ");
		// writer.write(h);
		// writer.write(";");
		// }
		if (zindex != null && !"".equals(zindex)) {
			writer.write("z-index:" + (Integer.valueOf(zindex) + 1) + ";");
		}

		// if (text.getModeValue() != ModeEnum.TRANSPARENT) {
		// Color bgColor = text.getBackcolor();
		// appendBackcolorStyle(bgColor, styleBuffer);
		// }

		writer.write("display: table;\">");
		writer.write("<div style=\"display: table-cell;");
		if (styleBuffer.length() > 0) {
			writer.write(styleBuffer.toString().trim());
		}
		writer.write("\">");

		if (text.getAnchorName() != null) {
			writer.write("<a name=\"");
			writer.write(text.getAnchorName());
			writer.write("\"/>");
		}

		// startHyperlink(text);
		String id = RXProperties.getProperty(text, PROPERTY_HTML_ID);
		String name = RXProperties.getProperty(text, PROPERTY_HTML_NAME);
		if (textLength > 0) {
			// only use text tooltip when no hyperlink present
			String textTooltip = hyperlinkStarted ? null : text.getHyperlinkTooltip();

			// // textconfirm 인경우 폼 데이터로 안넣음
			// if(id!=null){
			// essParameters.put(id, null);
			// formList.add(text);
			// }

			/*
			 * 하이퍼링크 툴팁과 title이 동시에 설정된 경우 title 속성이 우선한다.
			 */
			textTooltip = (title != null && !title.equalsIgnoreCase("")) ? title : textTooltip;

			// //input text 형태
			// text.setText(" ");

			text.setMode(ModeEnum.TRANSPARENT);

			exportStyledText(text, alt, title, css_class, getTextLocale(text), id + "_input", name);
		} else {
			writer.write(emptyCellStringProvider.getStringForEmptyTD(webUrl.equalsIgnoreCase("") ? imagesURI : webUrl));
		}

		// endHyperlink();

		writer.write("</div>");
		writer.write("</div>");

	}

	/**
	 *
	 */
	protected void exportText(RXPrintText text, RXExporterGridCell gridCell) throws IOException {

		String id = RXProperties.getProperty(text, PROPERTY_HTML_ID);
		String name = RXProperties.getProperty(text, PROPERTY_HTML_NAME);

		if (id != null) {
			log.debug("exportText, id=" + id + ",name:" + name);
		}

		String s = text.getFullText();
		/* 테스트가 null이거나 공백인 경우 HTML에서 &nbsp로 바꾸기 위함 */
		if (s == null || s.equalsIgnoreCase("")) {
			text.setText(" ");
		}

		/*
		 * alt 속성 가져오기
		 */
		String alt = RXProperties.getProperty(text, PROPERTY_HTML_ALT);

		/*
		 * title 속성 가져오기
		 */
		String title = RXProperties.getProperty(text, PROPERTY_HTML_TITLE);

		/*
		 * CSS Class 속성 가져오기
		 */
		String css_class = RXProperties.getProperty(text, PROPERTY_HTML_CLASS);

		RXStyledText styledText = getStyledText(text);

		int textLength = 0;

		if (styledText != null) {
			textLength = styledText.length();
		}

		writeCellStart(gridCell);

		if (text.getRunDirectionValue() == RunDirectionEnum.RTL) {
			writer.write(" dir=\"rtl\"");
		}

		StringBuffer styleBuffer = new StringBuffer();

		String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;

		switch (text.getVerticalAlignmentValue()) {
		case BOTTOM: {
			verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
			break;
		}
		case MIDDLE: {
			verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
			break;
		}
		case TOP:
		default: {
			verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
		}
		}

		if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP)) {
			styleBuffer.append(" vertical-align: ");
			styleBuffer.append(verticalAlignment);
			styleBuffer.append(";");
		}

		appendBackcolorStyle(gridCell, styleBuffer);
		appendBorderStyle(gridCell.getBox(), styleBuffer);

		String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;

		if (textLength > 0) {
			switch (text.getHorizontalAlignmentValue()) {
			case RIGHT: {
				horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
				break;
			}
			case CENTER: {
				horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
				break;
			}
			case JUSTIFIED: {
				horizontalAlignment = CSS_TEXT_ALIGN_JUSTIFY;
				break;
			}
			case LEFT:
			default: {
				horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
			}
			}

			if ((text.getRunDirectionValue() == RunDirectionEnum.LTR && !horizontalAlignment
					.equals(CSS_TEXT_ALIGN_LEFT))
					|| (text.getRunDirectionValue() == RunDirectionEnum.RTL && !horizontalAlignment
							.equals(CSS_TEXT_ALIGN_RIGHT))) {
				styleBuffer.append("text-align: ");
				styleBuffer.append(horizontalAlignment);
				styleBuffer.append(";");
			}
		}

		if (isWrapBreakWord) {
			styleBuffer.append("width: " + toSizeUnit(gridCell.getWidth()) + "; ");
			styleBuffer.append("word-wrap: break-word; ");
		}

		Float lineSpacingSize = text.getParagraph().getLineSpacingSize();
		LineSpacingEnum lineSpacing = text.getParagraph().getLineSpacing();

		switch (lineSpacing) {
		case SINGLE:
		default: {
			// styleBuffer.append(" line-height: 1; *line-height: normal;");
			break;
		}
		case ONE_AND_HALF: {
			styleBuffer.append(" line-height: 1.5;");
			break;
		}
		case DOUBLE: {
			styleBuffer.append(" line-height: 2.0;");
			break;
		}
		case PROPORTIONAL: {
			if (lineSpacingSize != null) {
				styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + ";");
			}
			break;
		}
		case AT_LEAST:
		case FIXED: {
			if (lineSpacingSize != null) {
				styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + "px;");
			}
			break;
		}
		}

		if (text.getLineBreakOffsets() != null) {
			// if we have line breaks saved in the text, set nowrap so that
			// the text only wraps at the explicit positions
			styleBuffer.append("white-space: nowrap; ");
		}

		if (styleBuffer.length() > 0) {
			writer.write(" style=\"");
			writer.write(styleBuffer.toString().trim());
			writer.write("\"");
		}

		writer.write(">");

		if (text.getAnchorName() != null) {
			writer.write("<a name=\"");
			writer.write(text.getAnchorName());
			writer.write("\"/>");
		}

		startHyperlink(text);

		if (textLength > 0) {
			// only use text tooltip when no hyperlink present
			String textTooltip = hyperlinkStarted ? null : text.getHyperlinkTooltip();

			if (id != null && !id.startsWith("scrollpoint")) {
				essParameters.put(id, null);
				formList.add(text);
				formListPage.add(current_page);
			}

			/*
			 * 하이퍼링크 툴팁과 title이 동시에 설정된 경우 title 속성이 우선한다.
			 */
			textTooltip = (title != null && !title.equalsIgnoreCase("")) ? title : textTooltip;

			exportStyledText(text, alt, title, css_class, getTextLocale(text), id, name);
		} else {
			writer.write(emptyCellStringProvider.getStringForEmptyTD(webUrl.equalsIgnoreCase("") ? imagesURI : webUrl));
		}

		endHyperlink();

		writeCellEnd(gridCell);

		// log.debug("2+++++++++++++++++++"+id);
		// if ("textconfirm".equalsIgnoreCase(tag_type) ) {
		// //exportTextConfirm(text);
		// }

	}

	protected boolean startHyperlink(RXPrintHyperlink link) throws IOException {
		String href = getHyperlinkURL(link);

		if (href != null) {
			writer.write("<a href=\"");
			writer.write(href);
			writer.write("\"");

			String target = getHyperlinkTarget(link);
			if (target != null) {
				writer.write(" target=\"");
				writer.write(target);
				writer.write("\"");
			}

			if (link.getHyperlinkTooltip() != null) {
				writer.write(" title=\"");
				writer.write(RXStringUtil.xmlEncode(link.getHyperlinkTooltip()));
				writer.write("\"");
			}

			writer.write(">");
		}

		hyperlinkStarted = href != null;

		return hyperlinkStarted;
	}

	protected String getHyperlinkTarget(RXPrintHyperlink link) {
		String target = null;
		RXHyperlinkTargetProducer producer = targetProducerFactory.getHyperlinkTargetProducer(link.getLinkTarget());
		if (producer == null) {
			switch (link.getHyperlinkTargetValue()) {
			case BLANK: {
				target = "_blank";
				break;
			}
			case PARENT: {
				target = "_parent";
				break;
			}
			case TOP: {
				target = "_top";
				break;
			}
			case CUSTOM: {
				boolean paramFound = false;
				List parameters = link.getHyperlinkParameters() == null ? null : link.getHyperlinkParameters()
						.getParameters();
				if (parameters != null) {
					for (Iterator it = parameters.iterator(); it.hasNext();) {
						RXPrintHyperlinkParameter parameter = (RXPrintHyperlinkParameter) it.next();
						if (link.getLinkTarget().equals(parameter.getName())) {
							target = parameter.getValue() == null ? null : parameter.getValue().toString();
							paramFound = true;
							break;
						}
					}
				}
				if (!paramFound) {
					target = link.getLinkTarget();
				}
				break;
			}
			case SELF:
			default: {
			}
			}
		} else {
			target = producer.getHyperlinkTarget(link);
		}

		return target;
	}

	protected String getHyperlinkURL(RXPrintHyperlink link) {
		String href = null;
		RXHyperlinkProducer customHandler = getCustomHandler(link);
		if (customHandler == null) {
			switch (link.getHyperlinkTypeValue()) {
			case REFERENCE: {
				if (link.getHyperlinkReference() != null) {
					href = link.getHyperlinkReference();
				}
				break;
			}
			case LOCAL_ANCHOR: {
				if (link.getHyperlinkAnchor() != null) {
					href = "#" + link.getHyperlinkAnchor();
				}
				break;
			}
			case LOCAL_PAGE: {
				if (link.getHyperlinkPage() != null) {
					href = "#" + RX_PAGE_ANCHOR_PREFIX + reportIndex + "_" + link.getHyperlinkPage().toString();
				}
				break;
			}
			case REMOTE_ANCHOR: {
				if (link.getHyperlinkReference() != null && link.getHyperlinkAnchor() != null) {
					href = link.getHyperlinkReference() + "#" + link.getHyperlinkAnchor();
				}
				break;
			}
			case REMOTE_PAGE: {
				if (link.getHyperlinkReference() != null && link.getHyperlinkPage() != null) {
					href = link.getHyperlinkReference() + "#" + RX_PAGE_ANCHOR_PREFIX + "0_"
							+ link.getHyperlinkPage().toString();
				}
				break;
			}
			case NONE:
			default: {
				break;
			}
			}
		} else {
			href = customHandler.getHyperlink(link);
		}

		return href;
	}

	protected void endHyperlink() throws IOException {
		if (hyperlinkStarted) {
			writer.write("</a>");
		}
		hyperlinkStarted = false;
	}

	protected boolean appendBorderStyle(RXLineBox box, StringBuffer styleBuffer) {
		boolean addedToStyle = false;

		if (box != null) {
			addedToStyle |= appendPen(styleBuffer, box.getTopPen(), "top");
			addedToStyle |= appendPadding(styleBuffer, box.getTopPadding(), "top");
			addedToStyle |= appendPen(styleBuffer, box.getLeftPen(), "left");
			addedToStyle |= appendPadding(styleBuffer, box.getLeftPadding(), "left");
			addedToStyle |= appendPen(styleBuffer, box.getBottomPen(), "bottom");
			addedToStyle |= appendPadding(styleBuffer, box.getBottomPadding(), "bottom");
			addedToStyle |= appendPen(styleBuffer, box.getRightPen(), "right");
			addedToStyle |= appendPadding(styleBuffer, box.getRightPadding(), "right");
		}

		return addedToStyle;
	}

	protected boolean appendBorderStyle(RXLineBox box, StringBuffer styleBuffer, RXPropertiesMap propertiMape) {
		int[] ctlpos = { -1, -1 };
		return appendBorderStyle(box, styleBuffer, propertiMape, ctlpos);
	}

	protected boolean appendBorderStyle(RXLineBox box, StringBuffer styleBuffer, RXPropertiesMap propertiMape,
			int[] ctlpos) {
		boolean addedToStyle = false;
		boolean lineboxArr[] = { true, true, true, true }; // "top","bottom","left"
															// ,"right"'

		if (box != null) {
			String lineBlank = StringUtils.nvl(propertiMape.getProperty("com.cabsoft.rx.export.html.line.blank"), "");
			// style이 지정이 안된경우
			if ("".equals(lineblankstyle)) {
				if (lineBlank.indexOf("t") > -1)
					lineboxArr[0] = false;
				if (lineBlank.indexOf("b") > -1)
					lineboxArr[1] = false;
				if (lineBlank.indexOf("l") > -1)
					lineboxArr[2] = false;
				if (lineBlank.indexOf("r") > -1)
					lineboxArr[3] = false;
			} else {
				// style에 지정된 경우
				// 무조건 설정되로 보여주기
				if (lineBlank.indexOf("f") > -1) { // fix
					if (lineBlank.indexOf("t") > -1)
						lineboxArr[0] = false;
					if (lineBlank.indexOf("b") > -1)
						lineboxArr[1] = false;
					if (lineBlank.indexOf("l") > -1)
						lineboxArr[2] = false;
					if (lineBlank.indexOf("r") > -1)
						lineboxArr[3] = false;
				} else {
					// 1단계 기본스타일 지정
					if (lineblankstyle.indexOf("t") > -1)
						lineboxArr[0] = false;
					if (lineblankstyle.indexOf("b") > -1)
						lineboxArr[1] = false;
					if (lineblankstyle.indexOf("l") > -1)
						lineboxArr[2] = false;
					if (lineblankstyle.indexOf("r") > -1)
						lineboxArr[3] = false;

					if (lineParagrap[0] > 0 && ctlpos[0] > 0 && ctlpos[0] == lineParagrap[0])
						lineboxArr[2] = true;
					if (lineParagrap[1] > 0 && ctlpos[1] > 0 && (ctlpos[0] + ctlpos[1]) == lineParagrap[1])
						lineboxArr[3] = true;

					if (lineBlank.indexOf("v") > -1) { // visible
						// 추가 보이기 위한 설정
						if (lineBlank.indexOf("t") > -1)
							lineboxArr[0] = true;
						if (lineBlank.indexOf("b") > -1)
							lineboxArr[1] = true;
						if (lineBlank.indexOf("l") > -1)
							lineboxArr[2] = true;
						if (lineBlank.indexOf("r") > -1)
							lineboxArr[3] = true;
					} else if (lineBlank.indexOf("h") > -1) { // hidden
						// 추가 숨기기 위한 설정
						if (lineBlank.indexOf("t") > -1)
							lineboxArr[0] = false;
						if (lineBlank.indexOf("b") > -1)
							lineboxArr[1] = false;
						if (lineBlank.indexOf("l") > -1)
							lineboxArr[2] = false;
						if (lineBlank.indexOf("r") > -1)
							lineboxArr[3] = false;
					}
				}
			}
			// 마지막으로 라인 보이기 설정완료
			if (lineboxArr[0]) {
				addedToStyle |= appendPen(styleBuffer, box.getTopPen(), "top");
				addedToStyle |= appendPadding(styleBuffer, box.getTopPadding(), "top");
			}
			if (lineboxArr[1]) {
				addedToStyle |= appendPen(styleBuffer, box.getBottomPen(), "bottom");
				addedToStyle |= appendPadding(styleBuffer, box.getBottomPadding(), "bottom");
			}
			if (lineboxArr[2]) {
				addedToStyle |= appendPen(styleBuffer, box.getLeftPen(), "left");
				addedToStyle |= appendPadding(styleBuffer, box.getLeftPadding(), "left");
			}
			if (lineboxArr[3]) {
				addedToStyle |= appendPen(styleBuffer, box.getRightPen(), "right");
				addedToStyle |= appendPadding(styleBuffer, box.getRightPadding(), "right");
			}
		}
		return addedToStyle;
	}

	protected Color appendBackcolorStyle(RXExporterGridCell gridCell, StringBuffer styleBuffer) {
		Color cellBackcolor = gridCell.getCellBackcolor();
		return appendBackcolorStyle(cellBackcolor, styleBuffer);
	}

	protected Color appendBackcolorStyle(Color cellBackcolor, StringBuffer styleBuffer) {
		if (cellBackcolor != null && (backcolor == null || cellBackcolor.getRGB() != backcolor.getRGB())) {
			styleBuffer.append("background-color: #");
			styleBuffer.append(RXColorUtil.getColorHexa(cellBackcolor));
			styleBuffer.append("; ");

			return cellBackcolor;
		}

		return null;
	}

	protected void exportImage(RXPrintImage image, ElementWrapper wraper) throws RXException, IOException {
		String alt = RXProperties.getProperty(image, PROPERTY_HTML_ALT);
		String title = RXProperties.getProperty(image, PROPERTY_HTML_TITLE);
		String id = RXProperties.getProperty(image, PROPERTY_HTML_ID);
		String name = RXProperties.getProperty(image, PROPERTY_HTML_NAME);
		String type = RXProperties.getProperty(image, PROPERTY_HTML_TAG_TYPE);
		
		String tabindex = RXProperties.getProperty(image, PROPERTY_HTML_TABINDEX);

		tabindex = tabindex==null ? "" : tabindex.trim();
		
		String strStyle = "";

		// hiddenimage 가 아닌경우만 내보내기
		if (!"hiddenimage".equalsIgnoreCase(type)) {
			alt = StringUtils.nvl(alt, "");

			if (title == null || title.equalsIgnoreCase("")) {
				title = RXStringUtil.xmlEncode(image.getHyperlinkTooltip());
			}
			title = StringUtils.nvl(title, "");

			StringBuffer styleBuffer = new StringBuffer();

			String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;

			switch (image.getHorizontalAlignmentValue()) {
			case RIGHT: {
				horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
				break;
			}
			case CENTER: {
				horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
				break;
			}
			case LEFT:
			default: {
				horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
			}
			}

			if (!horizontalAlignment.equals(CSS_TEXT_ALIGN_LEFT)) {
				styleBuffer.append("text-align: ");
				styleBuffer.append(horizontalAlignment);
				styleBuffer.append(";");
			}

			String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;

			switch (image.getVerticalAlignmentValue()) {
			case BOTTOM: {
				verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
				break;
			}
			case MIDDLE: {
				verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
				break;
			}
			case TOP:
			default: {
				verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
			}
			}

			if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP)) {
				styleBuffer.append(" vertical-align: ");
				styleBuffer.append(verticalAlignment);
				styleBuffer.append(";");
			}

			if (image.getModeValue() != ModeEnum.TRANSPARENT) {
				Color bgColor = image.getBackcolor();
				appendBackcolorStyle(bgColor, styleBuffer);
			}

			RXLineBox box = image.getLineBox();
			boolean addedToStyle = appendBorderStyle(box, styleBuffer);
			if (!addedToStyle) {
				appendPen(styleBuffer, image.getLinePen(), null);
			}

			RXRenderable renderer = image.getRenderer();

			if (renderer != null || isUsingImagesToAlign) {
				String zindex = RXProperties.getProperty(image, PROPERTY_HTML_ZINDEX);
				writer.write("<div class=\"rxe-absolute-position\"");
				writer.write(" style=\"left: ");
				writer.write(toSizeUnit(image.getX()) + ";");
				writer.write(" top: ");
				writer.write(toSizeUnit(image.getY()) + ";");
				if (zindex != null && !"".equals(zindex)) {
					writer.write("z-index:" + zindex + ";");
				}

				writer.write("display: table;\">");

				writer.write("<div style=\"display: table-cell;");
				if (styleBuffer.length() > 0) {
					writer.write(styleBuffer.toString());
				}
				writer.write("\">");

				if (id == null || id == "") {
					writer.write("<img");
					if(!"".equals(tabindex)){
						writer.write(" tabindex='" + tabindex + "'");
					}
				} else {
					writer.write("<img id=\"" + id + "\" ");
					if(!"".equals(tabindex)){
						writer.write(" tabindex='" + tabindex + "' ");
					}
				}

				String imagePath = null;
				String imageMapName = null;
				List imageMapAreas = null;

				ScaleImageEnum scaleImage = image.getScaleImageValue();

				String imgKey = image.getKey();
				imgKey = imgKey == null ? "" : imgKey.trim();

				if (renderer != null) {
					if (renderer.getType() == RXRenderable.TYPE_IMAGE
							&& rendererToImagePathMap.containsKey(renderer.getId())) {
						// 의문시됨
						imagePath = (String) rendererToImagePathMap.get(renderer.getId());
					} else {
						if (image.isLazy()) {
							// 의문시됨
							imagePath = ((RXImageRenderer) renderer).getImageLocation();
						} else {
							if (!imgKey.equalsIgnoreCase("") && !webUrl.equals("")) {
								imagePath = webUrl + imgKey;
							} else {
								RXPrintElementIndex imageIndex = getElementIndex(wraper);
								imagesToProcess.add(imageIndex);

								String imageName = getImageName(imageIndex);
								if (pwd != null && !"".equals(pwd)) {
									imagePath = imagesURI
											+ com.cabsoft.utils.Hex.dumpHex((imagesURIParam + imageName).getBytes());
								} else {
									imagePath = imagesURI + imageName;
								}
								// backward compatibility with the IMAGE_MAP
								// parameter
								if (imageNameToImageDataMap != null) {
									if (renderer.getType() == RXRenderable.TYPE_SVG) {
										renderer = new RXWrappingSvgRenderer(renderer, new Dimension(image.getWidth(),
												image.getHeight()),
												ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor() : null);
									}
									imageNameToImageDataMap.put(imageName, renderer.getImageData());
								}
								// END - backward compatibility with the
								// IMAGE_MAP
								// parameter
							}
						}

						if (imgKey.equalsIgnoreCase("") || webUrl.equals("")) {
							rendererToImagePathMap.put(renderer.getId(), imagePath);
						}
					}
				} else // ie: if(isUsingImagesToAlign)
				{
					if (!imgKey.equalsIgnoreCase("") && !webUrl.equals("")) {
						imagePath = webUrl + "pixel.gif";
					} else {
						loadPxImage();
						if (pwd != null && !"".equals(pwd)) {
							imagePath = imagesURI + com.cabsoft.utils.Hex.dumpHex((imagesURIParam + "px").getBytes());
						} else {
							imagePath = imagesURI + "px";
						}
						scaleImage = ScaleImageEnum.FILL_FRAME;
					}
				}

				writer.write(" src=\"");
				if (imagePath != null) {
					writer.write(imagePath);
				}
				writer.write("\"");

				int imageWidth = image.getWidth() - image.getLineBox().getLeftPadding().intValue()
						- image.getLineBox().getRightPadding().intValue();
				if (imageWidth < 0) {
					imageWidth = 0;
				}

				int imageHeight = image.getHeight() - image.getLineBox().getTopPadding().intValue()
						- image.getLineBox().getBottomPadding().intValue();
				if (imageHeight < 0) {
					imageHeight = 0;
				}

				switch (scaleImage) {
				case FILL_FRAME: {
					strStyle += (" width:");
					strStyle += (toSizeUnit(imageWidth));
					strStyle += ("; height: ");
					strStyle += (toSizeUnit(imageHeight));
					strStyle += (";");

					break;
				}
				case CLIP: // FIXMEIMAGE image clip could be achieved by cutting
							// the
					// image and preserving the image type
				case RETAIN_SHAPE:
				default: {
					double normalWidth = imageWidth;
					double normalHeight = imageHeight;
					try {
						if (!image.isLazy()) {
							// Image load might fail.
							RXRenderable tmpRenderer = RXImageRenderer.getOnErrorRendererForDimension(renderer,
									image.getOnErrorTypeValue());
							Dimension2D dimension = tmpRenderer == null ? null : tmpRenderer.getDimension();
							// If renderer was replaced, ignore image dimension.
							if (tmpRenderer == renderer && dimension != null) {
								normalWidth = dimension.getWidth();
								normalHeight = dimension.getHeight();
							}
						}
					} catch (Exception e) {
					}

					if (imageHeight > 0) {
						double ratio = normalWidth / normalHeight;

						if (ratio > (double) imageWidth / (double) imageHeight) {
							strStyle += (" width:");
							strStyle += (toSizeUnit(imageWidth));
							strStyle += (";");
						} else {
							strStyle += (" height:");
							strStyle += (toSizeUnit(imageHeight));
							strStyle += (";");
						}
					}
				}
				}

				/*
				 * 이미지의 경우 alt 속성을 반드시 추가해야 웹표준에 통과한다. 웹 접근성을 위해 img 태그에 alt와
				 * title 추가
				 */
				String s = "";
				String t = "";
				if (!alt.equalsIgnoreCase("")) {
					s = " alt=\"" + alt + "\"";
				}

				if (!title.equalsIgnoreCase("")) {
					t = " title=\"" + title + "\" ";
					if (s.equalsIgnoreCase("")) {
						s = " alt=\"" + title + "\" ";
					}
				} else if (image.getHyperlinkTooltip() != null) {
					t = " title=\"" + RXStringUtil.xmlEncode(image.getHyperlinkTooltip()) + "\" ";
					if (s.equalsIgnoreCase("")) {
						s = " alt=\"" + RXStringUtil.xmlEncode(image.getHyperlinkTooltip()) + "\" ";
					}
				}
				t = "".equals(t) ? " title=\"이미지2\" " : t;
				s = "".equals(s) ? " alt=\"이미지2\" " : s;

				if ("signimage".equalsIgnoreCase(type)) {
					strStyle += "display:none;";
				}
				writer.write(" style=\"" + strStyle + "\"");
				writer.write(t);
				writer.write(s);
				writer.write("/>");
				writer.write("</div>");
				writer.write("</div>");
			}
		}
	}

	/**
	 *
	 */
	protected void exportImage(RXPrintImage image, RXExporterGridCell gridCell) throws RXException, IOException {
		writeCellStart(gridCell);

		String alt = RXProperties.getProperty(image, PROPERTY_HTML_ALT);
		String title = RXProperties.getProperty(image, PROPERTY_HTML_TITLE);
		String tabindex = RXProperties.getProperty(image, PROPERTY_HTML_TABINDEX);
		
		alt = alt == null ? "" : alt;
		tabindex = tabindex==null ? "" : tabindex.trim();

		if (title == null || title.equalsIgnoreCase("")) {
			title = RXStringUtil.xmlEncode(image.getHyperlinkTooltip());
		}
		title = title == null ? "" : title;

		StringBuffer styleBuffer = new StringBuffer();

		String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;

		switch (image.getHorizontalAlignmentValue()) {
		case RIGHT: {
			horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
			break;
		}
		case CENTER: {
			horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
			break;
		}
		case LEFT:
		default: {
			horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
		}
		}

		if (!horizontalAlignment.equals(CSS_TEXT_ALIGN_LEFT)) {
			styleBuffer.append("text-align: ");
			styleBuffer.append(horizontalAlignment);
			styleBuffer.append(";");
		}

		String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;

		switch (image.getVerticalAlignmentValue()) {
		case BOTTOM: {
			verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
			break;
		}
		case MIDDLE: {
			verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
			break;
		}
		case TOP:
		default: {
			verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
		}
		}

		if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP)) {
			styleBuffer.append(" vertical-align: ");
			styleBuffer.append(verticalAlignment);
			styleBuffer.append(";");
		}

		appendBackcolorStyle(gridCell, styleBuffer);

		boolean addedToStyle = appendBorderStyle(gridCell.getBox(), styleBuffer);
		if (!addedToStyle) {
			appendPen(styleBuffer, image.getLinePen(), null);
		}

		if (styleBuffer.length() > 0) {
			writer.write(" style=\"");
			writer.write(styleBuffer.toString());
			writer.write("\"");
		}

		writer.write(">");

		if (image.getAnchorName() != null) {
			writer.write("<a name=\"");
			writer.write(image.getAnchorName());
			writer.write("\"/>");
		}

		RXRenderable renderer = image.getRenderer();
		RXRenderable originalRenderer = renderer;
		boolean imageMapRenderer = renderer != null && renderer instanceof RXImageMapRenderer
				&& ((RXImageMapRenderer) renderer).hasImageAreaHyperlinks();

		boolean hasHyperlinks = false;

		if (renderer != null || isUsingImagesToAlign) {
			if (imageMapRenderer) {
				hasHyperlinks = true;
				hyperlinkStarted = false;
			} else {
				hasHyperlinks = startHyperlink(image);
			}

			writer.write("<img");
			if(!"".equals(tabindex)){
				writer.write("tabindex='" + tabindex + "'");
			}
			String imagePath = null;
			String imageMapName = null;
			List imageMapAreas = null;

			ScaleImageEnum scaleImage = image.getScaleImageValue();

			String imgKey = image.getKey();
			imgKey = imgKey == null ? "" : imgKey.trim();

			if (renderer != null) {
				if (renderer.getType() == RXRenderable.TYPE_IMAGE
						&& rendererToImagePathMap.containsKey(renderer.getId())) {
					// 의문시됨
					imagePath = (String) rendererToImagePathMap.get(renderer.getId());
				} else {
					if (image.isLazy()) {
						// 의문시됨
						imagePath = ((RXImageRenderer) renderer).getImageLocation();
					} else {
						if (!imgKey.equalsIgnoreCase("") && !webUrl.equals("")) {
							imagePath = webUrl + imgKey;
						} else {
							RXPrintElementIndex imageIndex = getElementIndex(gridCell);
							imagesToProcess.add(imageIndex);

							String imageName = getImageName(imageIndex);
							if (pwd != null && !"".equals(pwd)) {
								imagePath = imagesURI
										+ com.cabsoft.utils.Hex.dumpHex((imagesURIParam + imageName).getBytes());
							} else {
								imagePath = imagesURI + imageName;
							}

							// backward compatibility with the IMAGE_MAP
							// parameter
							if (imageNameToImageDataMap != null) {
								if (renderer.getType() == RXRenderable.TYPE_SVG) {
									renderer = new RXWrappingSvgRenderer(renderer, new Dimension(image.getWidth(),
											image.getHeight()),
											ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor() : null);
								}
								imageNameToImageDataMap.put(imageName, renderer.getImageData());
							}
							// END - backward compatibility with the IMAGE_MAP
							// parameter
						}
					}

					if (imgKey.equalsIgnoreCase("") || webUrl.equals(""))
						rendererToImagePathMap.put(renderer.getId(), imagePath);
				}

				if (imageMapRenderer) {
					Rectangle renderingArea = new Rectangle(image.getWidth(), image.getHeight());

					if (renderer.getType() == RXRenderable.TYPE_IMAGE) {
						imageMapName = (String) imageMaps.get(new Pair(renderer.getId(), renderingArea));
					}

					if (imageMapName == null) {
						imageMapName = "map_" + getElementIndex(gridCell).toString();
						imageMapAreas = ((RXImageMapRenderer) originalRenderer).getImageAreaHyperlinks(renderingArea);// FIXMECHART

						if (renderer.getType() == RXRenderable.TYPE_IMAGE) {
							imageMaps.put(new Pair(renderer.getId(), renderingArea), imageMapName);
						}
					}
				}
			} else // ie: if(isUsingImagesToAlign)
			{
				if (!imgKey.equalsIgnoreCase("") && !webUrl.equals("")) {
					imagePath = webUrl + "pixel.gif";
				} else {
					loadPxImage();
					if (pwd != null && !"".equals(pwd)) {
						imagePath = imagesURI + com.cabsoft.utils.Hex.dumpHex((imagesURIParam + "px").getBytes());
					} else {
						imagePath = imagesURI + "px";
					}
					scaleImage = ScaleImageEnum.FILL_FRAME;
				}
			}

			writer.write(" src=\"");
			if (imagePath != null) {
				writer.write(imagePath);
			}
			writer.write("\"");

			int imageWidth = image.getWidth() - image.getLineBox().getLeftPadding().intValue()
					- image.getLineBox().getRightPadding().intValue();
			if (imageWidth < 0) {
				imageWidth = 0;
			}

			int imageHeight = image.getHeight() - image.getLineBox().getTopPadding().intValue()
					- image.getLineBox().getBottomPadding().intValue();
			if (imageHeight < 0) {
				imageHeight = 0;
			}

			switch (scaleImage) {
			case FILL_FRAME: {
				writer.write(" style=\"width: ");
				writer.write(toSizeUnit(imageWidth));
				writer.write("; height: ");
				writer.write(toSizeUnit(imageHeight));
				writer.write("\"");

				break;
			}
			case CLIP: // FIXMEIMAGE image clip could be achieved by cutting the
						// image and preserving the image type
			case RETAIN_SHAPE:
			default: {
				double normalWidth = imageWidth;
				double normalHeight = imageHeight;

				try {
					if (!image.isLazy()) {
						// Image load might fail.
						RXRenderable tmpRenderer = RXImageRenderer.getOnErrorRendererForDimension(renderer,
								image.getOnErrorTypeValue());
						Dimension2D dimension = tmpRenderer == null ? null : tmpRenderer.getDimension();
						// If renderer was replaced, ignore image dimension.
						if (tmpRenderer == renderer && dimension != null) {
							normalWidth = dimension.getWidth();
							normalHeight = dimension.getHeight();
						}
					}
				} catch (Exception e) {
				}

				if (imageHeight > 0) {
					double ratio = normalWidth / normalHeight;

					if (ratio > (double) imageWidth / (double) imageHeight) {
						writer.write(" style=\"width: ");
						writer.write(toSizeUnit(imageWidth));
						writer.write("\"");
					} else {
						writer.write(" style=\"height: ");
						writer.write(toSizeUnit(imageHeight));
						writer.write("\"");
					}
				}
			}
			}

			if (imageMapName != null) {
				writer.write(" usemap=\"#" + imageMapName + "\"");
			}

			/*
			 * 이미지의 경우 alt 속성을 반드시 추가해야 웹표준에 통과한다. 웹 접근성을 위해 img 태그에 alt와 title
			 * 추가
			 */
			String s = "";
			String t = "";
			if (!alt.equalsIgnoreCase("")) {
				s = " alt=\"" + alt + "\"";
			}

			if (hasHyperlinks) {
				writer.write(" border=\"0\"");
			}

			if (!title.equalsIgnoreCase("")) {
				t = " title=\"" + title + "\" ";
				if (s.equalsIgnoreCase("")) {
					s = " alt=\"" + title + "\" ";
				}
			} else if (image.getHyperlinkTooltip() != null) {
				t = " title=\"" + RXStringUtil.xmlEncode(image.getHyperlinkTooltip()) + "\" ";
				if (s.equalsIgnoreCase("")) {
					s = " alt=\"" + RXStringUtil.xmlEncode(image.getHyperlinkTooltip()) + "\" ";
				}
			}
			t = "".equals(t) ? " title=\"이미지\" " : t;
			s = "".equals(s) ? " alt=\"이미지\" " : s;
			writer.write(t);
			writer.write(s);
			writer.write("/>");

			endHyperlink();

			if (imageMapAreas != null) {
				writer.write("\n");
				writeImageMap(imageMapName, image, imageMapAreas);
			}
		}

		writeCellEnd(gridCell);
	}

	protected RXPrintElementIndex getElementIndex(RXExporterGridCell gridCell) {
		RXPrintElementIndex imageIndex = new RXPrintElementIndex(reportIndex, pageIndex, gridCell.getWrapper()
				.getAddress());
		return imageIndex;
	}

	protected RXPrintElementIndex getElementIndex(ElementWrapper wraper) {
		RXPrintElementIndex imageIndex = new RXPrintElementIndex(reportIndex, pageIndex, wraper.getAddress());
		return imageIndex;
	}

	protected void writeImageMap(String imageMapName, RXPrintImage image, List imageMapAreas) throws IOException {
		writer.write("<map name=\"" + imageMapName + "\">\n");

		for (ListIterator it = imageMapAreas.listIterator(imageMapAreas.size()); it.hasPrevious();) {
			RXPrintImageAreaHyperlink areaHyperlink = (RXPrintImageAreaHyperlink) it.previous();
			RXPrintImageArea area = areaHyperlink.getArea();

			writer.write("  <area shape=\"" + RXPrintImageArea.getHtmlShape(area.getShape()) + "\"");
			writeImageAreaCoordinates(area.getCoordinates());
			writeImageAreaHyperlink(areaHyperlink.getHyperlink());
			writer.write("/>\n");
		}

		if (image.getHyperlinkTypeValue() != HyperlinkTypeEnum.NONE) {
			writer.write("  <area shape=\"default\"");
			writeImageAreaCoordinates(new int[] { 0, 0, image.getWidth(), image.getHeight() });// for
																								// IE
			writeImageAreaHyperlink(image);
			writer.write("/>\n");
		}

		writer.write("</map>\n");
	}

	protected void writeImageAreaCoordinates(int[] coords) throws IOException {
		if (coords != null && coords.length > 0) {
			StringBuffer coordsEnum = new StringBuffer(coords.length * 4);
			coordsEnum.append(toZoom(coords[0]));
			for (int i = 1; i < coords.length; i++) {
				coordsEnum.append(',');
				coordsEnum.append(toZoom(coords[i]));
			}
			writer.write(" coords=\"" + coordsEnum + "\"");
		}
	}

	protected void writeImageAreaHyperlink(RXPrintHyperlink hyperlink) throws IOException {
		String href = getHyperlinkURL(hyperlink);
		if (href == null) {
			writer.write(" nohref=\"nohref\"");
		} else {
			writer.write(" href=\"" + href + "\"");

			String target = getHyperlinkTarget(hyperlink);
			if (target != null) {
				writer.write(" target=\"");
				writer.write(target);
				writer.write("\"");
			}
		}

		if (hyperlink.getHyperlinkTooltip() != null) {
			writer.write(" title=\"");
			writer.write(RXStringUtil.xmlEncode(hyperlink.getHyperlinkTooltip()));
			writer.write("\"");
		}
	}

	/**
	 *
	 */
	protected void loadPxImage() throws RXException {
		isPxImageLoaded = true;
		// backward compatibility with the IMAGE_MAP parameter
		if (imageNameToImageDataMap != null && !imageNameToImageDataMap.containsKey("px")) {
			RXRenderable pxRenderer = RXImageRenderer.getInstance("com/cabsoft/rx/engine/images/pixel.GIF");
			rendererToImagePathMap.put(pxRenderer.getId(), imagesURI + "px");
			imageNameToImageDataMap.put("px", pxRenderer.getImageData());
		}
		// END - backward compatibility with the IMAGE_MAP parameter
	}

	/**
	 *
	 */
	protected interface StringProvider {

		/**
		 *
		 */
		public String getStringForCollapsedTD(Object value, String style, int width, int height);

		/**
		 *
		 */
		public String getStringForEmptyTD(Object value);

		public String getReportTableStyle();
	}

	/**
	 *
	 */
	private boolean appendPadding(StringBuffer sb, Integer padding, String side) {
		boolean addedToStyle = false;

		if (padding.intValue() > 0) {
			sb.append("padding");
			if (side != null) {
				sb.append("-");
				sb.append(side);
			}
			sb.append(": ");
			sb.append(toSizeUnit(padding.intValue()));
			sb.append("; ");

			addedToStyle = true;
		}

		return addedToStyle;
	}

	/**
	 *
	 */
	private boolean appendPen(StringBuffer sb, RXPen pen, String side) {
		boolean addedToStyle = false;

		float borderWidth = pen.getLineWidth().floatValue();
		if (0f < borderWidth && borderWidth < 1f) {
			borderWidth = 1f;
		}

		String borderStyle = null;
		switch (pen.getLineStyleValue()) {
		case DOUBLE: {
			borderStyle = "double";
			break;
		}
		case DOTTED: {
			borderStyle = "dotted";
			break;
		}
		case DASHED: {
			borderStyle = "dashed";
			break;
		}
		case SOLID:
		default: {
			borderStyle = "solid";
			break;
		}
		}

		if (borderWidth > 0f) {
			sb.append("border");
			if (side != null) {
				sb.append("-");
				sb.append(side);
			}
			sb.append("-style: ");
			sb.append(borderStyle);
			sb.append("; ");

			sb.append("border");
			if (side != null) {
				sb.append("-");
				sb.append(side);
			}
			sb.append("-width: ");
			sb.append(toSizeUnit((int) borderWidth));
			sb.append("; ");

			sb.append("border");
			if (side != null) {
				sb.append("-");
				sb.append(side);
			}
			sb.append("-color: #");
			sb.append(RXColorUtil.getColorHexa(pen.getLineColor()));
			sb.append("; ");

			addedToStyle = true;
		}

		return addedToStyle;
	}

	/**
	 *
	 */
	private String getImageName(RXPrintElementIndex printElementIndex) {
		return IMAGE_NAME_PREFIX + printElementIndex.toString();
	}

	/**
	 *
	 */
	private RXPrintElementIndex getPrintElementIndex(String imageName) {
		if (!imageName.startsWith(IMAGE_NAME_PREFIX)) {
			throw new RXRuntimeException("Invalid image name: " + imageName);
		}

		return RXPrintElementIndex.parsePrintElementIndex(imageName.substring(IMAGE_NAME_PREFIX_LEGTH));
	}

	protected void exportFrame(RXPrintFrame frame, List<ElementWrapper> overlapWrappers) throws IOException,
			RXException {
		StringBuffer styleBuffer = new StringBuffer();
		if (frame.getModeValue() != ModeEnum.TRANSPARENT) {
			Color frameBackcolor = frame.getBackcolor();
			if (frameBackcolor != null && (backcolor == null || frameBackcolor.getRGB() != backcolor.getRGB())) {
				styleBuffer.append("background-color: #");
				styleBuffer.append(RXColorUtil.getColorHexa(frameBackcolor));
				styleBuffer.append("; ");
			}
		}

		RXLineBox box = frame.getLineBox();
		if (box != null) {
			appendPen(styleBuffer, box.getTopPen(), "top");
			appendPadding(styleBuffer, box.getTopPadding(), "top");
			appendPen(styleBuffer, box.getLeftPen(), "left");
			appendPadding(styleBuffer, box.getLeftPadding(), "left");
			appendPen(styleBuffer, box.getBottomPen(), "bottom");
			appendPadding(styleBuffer, box.getBottomPadding(), "bottom");
			appendPen(styleBuffer, box.getRightPen(), "right");
			appendPadding(styleBuffer, box.getRightPadding(), "right");
		}

		String w = toSizeUnit(frame.getWidth());
		String h = toSizeUnit(frame.getHeight());
		String zindex = RXProperties.getProperty(frame, PROPERTY_HTML_ZINDEX);
		String title = RXProperties.getProperty(frame, PROPERTY_HTML_TITLE);
		String alt = RXProperties.getProperty(frame, PROPERTY_HTML_ALT);

		title = (title == null || "".equals(title)) ? "" : title.trim();
		alt = (alt == null || "".equals(alt)) ? "" : alt.trim();
		title = "".equals(title) ? ("".equals(alt) ? "" : alt) : title;

		writer.write("<div title=\"" + title + "\" class=\"rxe-absolute-position\"");
		writer.write(" style=\"left: ");
		writer.write(toSizeUnit(frame.getX()) + ";");
		writer.write(" top: ");
		writer.write(toSizeUnit(frame.getY()) + ";");
		writer.write(" width:" + w + ";");
		writer.write(" height:" + h + ";");
		if (zindex != null && !"".equals(zindex)) {
			writer.write("z-index:" + zindex + ";");
		}

		if (styleBuffer.length() > 0) {
			writer.write(styleBuffer.toString());
			writer.write("\"");
		}
		writer.write(">\n");
		exportOverlap(frame.getElements(), overlapWrappers);
		writer.write("</div>\n");
	}

	protected void exportFrame(RXPrintFrame frame, RXExporterGridCell gridCell) throws IOException, RXException {
		String title = RXProperties.getProperty(frame, PROPERTY_HTML_TITLE);
		title = title == null ? "" : title;
		title = "".equals(title) ? (frame.getKey() == null ? "" : frame.getKey()) : title;

		writeCellStart(gridCell);

		StringBuffer styleBuffer = new StringBuffer();
		Color frameBackcolor = appendBackcolorStyle(gridCell, styleBuffer);
		appendBorderStyle(gridCell.getBox(), styleBuffer);

		if (styleBuffer.length() > 0) {
			writer.write(" style=\"");
			writer.write(styleBuffer.toString());
			writer.write("\"");
		}

		writer.write(">\n");

		if (frameBackcolor != null) {
			setBackcolor(frameBackcolor);
		}
		try {
			if (frameTotable) {
				exportGrid(gridCell.getLayout(), false, "summary='" + title + "'", true);
			} else {
				exportGrid(gridCell.getLayout(), false, "", true);
			}
		} finally {
			if (frameBackcolor != null) {
				restoreBackcolor();
			}
		}

		writeCellEnd(gridCell);
	}

	protected void setBackcolor(Color color) {
		backcolorStack.addLast(backcolor);

		backcolor = color;
	}

	protected void restoreBackcolor() {
		backcolor = (Color) backcolorStack.removeLast();
	}

	protected void exportGenericElement(RXGenericPrintElement element, RXExporterGridCell gridCell, int rowHeight)
			throws IOException {
		GenericElementHtmlHandler handler = (GenericElementHtmlHandler) GenericElementHandlerEnviroment.getHandler(
				element.getGenericType(), HTML_EXPORTER_KEY);

		if (handler == null) {
			if (log.isDebugEnabled()) {
				log.debug("No HTML generic element handler for " + element.getGenericType());
			}

			writeEmptyCell(gridCell, rowHeight);
		} else {
			writeCellStart(gridCell);

			StringBuffer styleBuffer = new StringBuffer();
			appendBackcolorStyle(gridCell, styleBuffer);
			appendBorderStyle(gridCell.getBox(), styleBuffer);
			if (styleBuffer.length() > 0) {
				writer.write(" style=\"");
				writer.write(styleBuffer.toString());
				writer.write("\"");
			}

			writer.write(">");

			String htmlFragment = handler.getHtmlFragment(exporterContext, element);
			if (htmlFragment != null) {
				writer.write(htmlFragment);
			}

			writeCellEnd(gridCell);
		}
	}

	public Map getExportParameters() {
		return parameters;
	}

	public String getExportPropertiesPrefix() {
		return HTML_EXPORTER_PROPERTIES_PREFIX;
	}

	protected String getExporterKey() {
		return HTML_EXPORTER_KEY;
	}

	public ReportExpressPrint getExportedReport() {
		return reportexpressPrint;
	}

	public String toSizeUnit(int size) {
		return String.valueOf((int) toScaledSize(size)) + sizeUnit;
	}

	public int toScaledSize(int size) {
		// return Math.round((100*size*zoom)/100);
		return (int) (size * zoom);
	}

	public int toZoom(int size) {
		return (int) (zoom * size);
	}

	protected RXStyledText getStyledText(RXPrintText textElement, boolean setBackcolor) {
		RXStyledText styledText = super.getStyledText(textElement, setBackcolor);

		if (styledText != null) {
			short[] lineBreakOffsets = textElement.getLineBreakOffsets();
			if (lineBreakOffsets != null && lineBreakOffsets.length > 0) {
				// insert new lines at the line break positions saved at fill
				// time
				// cloning the text first
				styledText = styledText.cloneText();
				styledText.insert("\n", lineBreakOffsets);
			}
		}

		return styledText;
	}

}
