package com.cabsoft.ess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
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
import com.cabsoft.rx.engine.RXRenderable;
import com.cabsoft.rx.engine.RXRuntimeException;
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
import com.cabsoft.rx.engine.util.Pair;
import com.cabsoft.rx.engine.util.RXColorUtil;
import com.cabsoft.rx.engine.util.RXFontUtil;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.rx.engine.util.RXStringUtil;
import com.cabsoft.rx.engine.util.RXStyledText;
import com.cabsoft.rx.hbarcode.AttributeBuilder;
import com.cabsoft.rx.hbarcode.sec;
import com.cabsoft.rx.hbarcode.sysUtils;
import com.cabsoft.utils.Base64Util;
import com.cabsoft.utils.StringUtils;

/**
 * ReportExpress 문서를 HTML 포맷으로 내보낸다.
 * 캐릭터 출력 타입으로 그리드 기반의 레이아웃으로 문서를 내보낸다.
 *
 * @author Cabsoft(support@cabsoftware.com)
 * @version $Id: RXEssExporter.java 0001 2010-07-30 $
 */
@SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
public class ExportSigningData extends RXAbstractExporter {

    private static final Log log = LogFactory.getLog(ExportSigningData.class);
    private static final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
    /**
     * {@link GenericElementHandlerEnviroment#getHandler(com.cabsoft.rx.engine.RXGenericElementType, String)} 에서 사용되는 내보내기 키.
     */
    public static final String HTML_EXPORTER_KEY = RXProperties.PROPERTY_PREFIX + "html";
    /**
     * @deprecated {@link RXHtmlExporterParameter#PROPERTY_FRAMES_AS_NESTED_TABLES}로 대체됨.
     */
    public static final String PROPERTY_FRAMES_AS_NESTED_TABLES = RXHtmlExporterParameter.PROPERTY_FRAMES_AS_NESTED_TABLES;
    /**
     *
     */
    public static final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
    public static final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";
    /*
     * html element의 alt 속성 설정
     */
    public static final String PROPERTY_HTML_ALT = HTML_EXPORTER_PROPERTIES_PREFIX + "alt";
    
    /*
     * html element의 설명을 위한 title 속성 설정 
     */
    public static final String PROPERTY_HTML_TITLE = HTML_EXPORTER_PROPERTIES_PREFIX + "title";
    
    /*
     * html 문서에 이미지를 base64로 인코딩하여 추가하기 위한 설정
     */
    public static final String PROPERTY_HTML_EMBEDEDIMAGE = HTML_EXPORTER_PROPERTIES_PREFIX + "embededimage";
    
    /**
     * 접근 가능한 HTML을 생성하기 위한 내보내기를 결정하는 설정 속성
     */
    public static final String PROPERTY_ACCESSIBLE = HTML_EXPORTER_PROPERTIES_PREFIX + "accessible";
    /**
     *
     */
    protected static final String RX_PAGE_ANCHOR_PREFIX = "RX_PAGE_ANCHOR_";
    protected static final float DEFAULT_ZOOM = 96f / 72f;
    /**
     *
     */
    protected static final String CSS_TEXT_ALIGN_LEFT = "left";
    protected static final String CSS_TEXT_ALIGN_RIGHT = "right";
    protected static final String CSS_TEXT_ALIGN_CENTER = "center";
    protected static final String CSS_TEXT_ALIGN_JUSTIFY = "justify";
    /**
     *
     */
    protected static final String HTML_VERTICAL_ALIGN_TOP = "top";
    protected static final String HTML_VERTICAL_ALIGN_MIDDLE = "middle";
    protected static final String HTML_VERTICAL_ALIGN_BOTTOM = "bottom";
    public static final String IMAGE_NAME_PREFIX = "img_";
    protected static final int IMAGE_NAME_PREFIX_LEGTH = IMAGE_NAME_PREFIX.length();

    protected class ExporterContext extends BaseExporterContext implements RXHtmlExporterContext {

        public String getExportPropertiesPrefix() {
            return HTML_EXPORTER_PROPERTIES_PREFIX;
        }

        public String getHyperlinkURL(RXPrintHyperlink link) {
            return ExportSigningData.this.getHyperlinkURL(link);
        }
    }
    /**
     *
     */
    protected Writer writer;
    protected RXExportProgressMonitor progressMonitor;
    protected int reportIndex;
    protected int pageIndex;
    
    protected boolean isPxImageLoaded;
    protected Map imageNameToImageDataMap;
    protected Map rendererToImagePathMap;
    protected Map imageMaps;
    protected List imagesToProcess;
    
    protected String imagesURI;
    
    /**
     *
     */
    protected boolean isRemoveEmptySpace;
    protected boolean isWhitePageBackground;
    protected String encoding;
    protected String sizeUnit;
    protected float zoom = DEFAULT_ZOOM;
    protected boolean isUsingImagesToAlign;
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
    protected RXHtmlExporterContext exporterContext = new ExporterContext();
    
    public ExportSigningData() {
        backcolorStack = new LinkedList();
        backcolor = null;
    }

    /**
     *
     */
    public void exportReport() throws RXException {
        progressMonitor = (RXExportProgressMonitor) parameters.get(RXExporterParameter.PROGRESS_MONITOR);

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

            isRemoveEmptySpace =
                    getBooleanParameter(
                    RXHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                    RXHtmlExporterParameter.PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                    false);

            isWhitePageBackground =
                    getBooleanParameter(
                    RXHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND,
                    RXHtmlExporterParameter.PROPERTY_WHITE_PAGE_BACKGROUND,
                    true);

            encoding =
                    getStringParameterOrDefault(
                    RXExporterParameter.CHARACTER_ENCODING,
                    RXExporterParameter.PROPERTY_CHARACTER_ENCODING);
            
            rendererToImagePathMap = new HashMap();
            imageMaps = new HashMap();
            imagesToProcess = new ArrayList();
            isPxImageLoaded = false;

            //IMAGE_MAP 매개변수와 하위 호환성을 유지 목적
            imageNameToImageDataMap = (Map) parameters.get(RXHtmlExporterParameter.IMAGES_MAP);
            //		if (imageNameToImageDataMap == null)
            //		{
            //			imageNameToImageDataMap = new HashMap();
            //		}
            //END - backward compatibility with the IMAGE_MAP parameter

            isWrapBreakWord =
                    getBooleanParameter(
                    RXHtmlExporterParameter.IS_WRAP_BREAK_WORD,
                    RXHtmlExporterParameter.PROPERTY_WRAP_BREAK_WORD,
                    false);

            sizeUnit =
                    getStringParameterOrDefault(
                    RXHtmlExporterParameter.SIZE_UNIT,
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
            
            isUsingImagesToAlign =
                    getBooleanParameter(
                    RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,
                    RXHtmlExporterParameter.PROPERTY_USING_IMAGES_TO_ALIGN,
                    true);

            emptyCellStringProvider =
	                new StringProvider() {
	                    public String getStringForCollapsedTD(Object value, String style, int width, int height) {
	                        return "><div style=\"width: " + toSizeUnit(width) + "; height: " + toSizeUnit(height) + "; " + style + "\"></div>";
	                    }
	
	                    public String getStringForEmptyTD(Object value) {
	                        return "";
	                    }
	
	                    public String getReportTableStyle() {
	                        return null;
	                    }
	
	                    public String getStringForCollapsedTD(Object value, int width, int height) {
	                        return "><div style=\"width: " + toSizeUnit(width) + "; height: " + toSizeUnit(height) + ";\"></div>";
	                    }
	            };

            isIgnorePageMargins =
                    getBooleanParameter(
                    RXExporterParameter.IGNORE_PAGE_MARGINS,
                    RXExporterParameter.PROPERTY_IGNORE_PAGE_MARGINS,
                    false);

            accessibleHtml =
                    RXProperties.getBooleanProperty(
                    reportexpressPrint,
                    PROPERTY_ACCESSIBLE,
                    false);

            fontMap = (Map) parameters.get(RXExporterParameter.FONT_MAP);

            setHyperlinkProducerFactory();

            //FIXMENOW check all exporter properties that are supposed to work at report level
            boolean deepGrid =
                    !getBooleanParameter(
                    RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES,
                    RXHtmlExporterParameter.PROPERTY_FRAMES_AS_NESTED_TABLES,
                    true);

            nature = new RXHtmlExporterNature(filter, deepGrid, isIgnorePageMargins);

            flushOutput = getBooleanParameter(RXHtmlExporterParameter.FLUSH_OUTPUT,
                    RXHtmlExporterParameter.PROPERTY_FLUSH_OUTPUT,
                    true);

            StringBuffer sb = (StringBuffer) parameters.get(RXExporterParameter.OUTPUT_STRING_BUFFER);
            if (sb != null) {
                try {
                    writer = new StringWriter();
                    exportReportToWriter();
                    sb.append(writer.toString());
                } catch (IOException e) {
                    throw new RXException("Error writing to StringBuffer writer : " + reportexpressPrint.getName(), e);
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
                        throw new RXException("Error writing to writer : " + reportexpressPrint.getName(), e);
                    }
                } else {
                    OutputStream os = (OutputStream) parameters.get(RXExporterParameter.OUTPUT_STREAM);
                    if (os != null) {
                        try {
                            writer = new OutputStreamWriter(os, encoding);
                            exportReportToWriter();
                        } catch (IOException e) {
                            throw new RXException("Error writing to OutputStream writer : " + reportexpressPrint.getName(), e);
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

                        try {
                            exportReportToWriter();
                        } catch (IOException e) {
                            throw new RXException("Error writing to file writer : " + reportexpressPrint.getName(), e);
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
        } finally {
            resetExportContext();
        }
    }

    public static RXPrintImage getImage(List reportexpressPrintList, String imageName) {
        return getImage(reportexpressPrintList, getPrintElementIndex(imageName));
    }

    public static RXPrintImage getImage(List reportexpressPrintList, RXPrintElementIndex imageIndex) {
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
     *
     */
    protected void exportReportToWriter() throws RXException, IOException {
        int reportSize = rxPrintList.size();
        int max_pages = 0;

        for (int i = 0; i < reportSize; i++) {
            List pages = reportexpressPrint.getPages();
            if (pages != null && pages.size() > 0) {
                max_pages += pages.size();
            }
        }

        if (htmlHeader == null) {
            writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            writer.write("<head>\n");
            writer.write("<title>sCert 원본 대조 시스템</title>\n");
            writer.write("<meta http-equiv=\"content-type\" content=\"text/html; charset=" + encoding + "\"/>\n");
            writer.write("<meta name=\"reportexpress\" content=\"notranslate\"/>\n");
            writer.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8;IE=9;\"/>\n");
            writer.write("<style type=\"text/css\">\n");
            writer.write("html,body{\n");
            writer.write("	height:100%;\n");
            writer.write("	margin: 0\n");
            writer.write("}\n");
            writer.write("P.breakhere {\n");
            writer.write("	page-break-before: always\n");
            writer.write("}\n");
            writer.write(".rxe-default-page{\n");
            writer.write("	background:#ebebeb\n");
            writer.write("}\n");
            writer.write(".rxe-page-paginated{\n");
            writer.write("	border:1px solid #cacaca;\n");
            writer.write("	box-shadow:0 0 4px rgba(0,0,0,0.1);\n");
            writer.write("	-moz-box-shadow:0 0 4px rgba(0,0,0,0.1);\n");
            writer.write("	-webkit-box-shadow:0 0 4px rgba(0,0,0,0.1)\n");
            writer.write("}\n");
            writer.write(".rxe-page{\n");
            writer.write("	overflow:hidden;\n");
            writer.write("	position:relative;\n");
            writer.write("	white-space:normal;\n");
            writer.write("	-webkit-tap-highlight-color:initial\n");
            writer.write("}\n");
            writer.write(".rxe-paginateddocumentplugin{\n");
            writer.write("	position:relative\n");
            writer.write("}\n");
            writer.write("</style>\n");
            writer.write(ExportSigningDataHelper.setMaxPages(max_pages,
                    toScaledSize(reportexpressPrint.getPageWidth()),
                    toScaledSize(reportexpressPrint.getPageHeight() - 20)));
            writer.write("<script type=\"text/javascript\">\n");
            writer.write("window.onload = function(){\n");
            writer.write("  try{\n");
            writer.write("      var doc_left;\n");
            writer.write("      var scr_width = window.document.body.offsetWidth;\n");
            writer.write("	var scr_height = window.document.body.offsetHeight;\n");
            writer.write("	doc_left = (scr_width-pageWidth)/2; doc_left = (doc_left<0) ? 6 : doc_left;\n");
            writer.write("	document.getElementById(\"canvas\").style.height = (document.documentElement.clientHeight-5) + \"px\";\n");
            writer.write("	document.getElementById(\"doc\").style.left = doc_left + \"px\";\n");
            writer.write("	document.getElementById(\"canvas\").style.overflowY  = \"visible\";\n");
            writer.write("  }catch(e){\n");
            writer.write("	alert(e.description);\n");
            writer.write("  }\n");
            writer.write("}\n");
            writer.write("window.onerror = function(msg){\n");
            writer.write("  alert(msg + \"\\n\\n원본 대조 모드에서는 지원하지 않는 기능입니다.\");\n");
            writer.write("  return true;\n");
            writer.write("}\n");
            writer.write("</script>\n");
            writer.write("</head>\n");
            writer.write("<body id=\"htmlviewer\" bgcolor=\"#eeeeee\">\n");
        } else {
            writer.write(htmlHeader);

            /*
             * 페이지 수와 페이지의 폭 및 높이를 자바스크립트로 설정한다.
             */
            writer.write(ExportSigningDataHelper.setMaxPages(max_pages,
                    toScaledSize(reportexpressPrint.getPageWidth()),
                    toScaledSize(reportexpressPrint.getPageHeight() - 20)));
        }

        writer.write(ExportSigningDataHelper.getDocStart(
                toScaledSize(reportexpressPrint.getPageWidth()),
                toScaledSize(reportexpressPrint.getPageHeight() - 20),
                toScaledSize(reportexpressPrint.getLeftMargin()),
                toScaledSize(reportexpressPrint.getRightMargin())));

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
                        throw new RXException("Current thread interrupted.");
                    }

                    page = (RXPrintPage) pages.get(pageIndex);

                    writer.write(ExportSigningDataHelper.getPageStart(
                            toScaledSize(reportexpressPrint.getPageWidth()),
                            toScaledSize(reportexpressPrint.getPageHeight() - 20)));

                    writer.write("<a name=\"" + RX_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (pageIndex + 1) + "\"></a>\n");

                    /*   */
                    exportPage(page);

                    writer.write(ExportSigningDataHelper.getPageEnd());

                    if (reportIndex < rxPrintList.size() - 1 || pageIndex < endPageIndex) {
                        if (betweenPagesHtml == null) {
                            writer.write("<br/>\n<br/>\n");
                        } else {
                            writer.write(betweenPagesHtml);
                        }
                    }

                    writer.write("\n");
                }
            }
        }

        if (htmlFooter == null) {
            writer.write("</div></div></div></div></div></div></body></html>");
        } else {
            writer.write(ExportSigningDataHelper.getDocEnd());
            writer.write(htmlFooter);
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

		RXGridLayout layout =
                new RXGridLayout(
                nature,
                elements,
                reportexpressPrint.getPageWidth(),
                reportexpressPrint.getPageHeight(),
                globalOffsetX,
                globalOffsetY,
                null //address
                );

        exportGrid(layout, isWhitePageBackground);

        if (progressMonitor != null) {
            progressMonitor.afterPageExport(reportIndex, pageIndex);
        }
    }

    /**
     *
     */
    protected void exportGrid(RXGridLayout gridLayout, boolean whitePageBackground) throws IOException, RXException {
        CutsInfo xCuts = gridLayout.getXCuts();
        RXExporterGridCell[][] grid = gridLayout.getGrid();

        String tableStyle = "width: " + toSizeUnit(gridLayout.getWidth()) + "; border-collapse: collapse";
        String additionalTableStyle = emptyCellStringProvider.getReportTableStyle();
        if (additionalTableStyle != null) {
            tableStyle += "; " + additionalTableStyle;
        }

        writer.write("<table style=\"" + tableStyle + "\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"");
        if (whitePageBackground) {
            writer.write(" bgcolor=\"white\"");
        }
        writer.write(">\n");

        if (whitePageBackground) {
            setBackcolor(Color.white);
        }

        writer.write("<tr>\n");
        int width = 0;
        for (int i = 1; i < xCuts.size(); i++) {
            width = xCuts.getCut(i) - xCuts.getCut(i - 1);
            writer.write("  <td" + emptyCellStringProvider.getStringForCollapsedTD("", width, 1) + "</td>\n");
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
                }
                writer.write(">\n");

                for (int x = 0; x < gridRow.length; x++) {
                    RXExporterGridCell gridCell = gridRow[x];
                    if (gridCell.getWrapper() == null) {
                        writeEmptyCell(gridCell, rowHeight);
                    } else {

                        RXPrintElement element = gridCell.getWrapper().getElement();

                        String thTag = null;

                        if (element != null
                                && element.hasProperties()) {
                            thTag = element.getPropertiesMap().getProperty(RXPdfExporterTagHelper.PROPERTY_TAG_TH);
                        }

                        if (thTag != null && (RXPdfExporterTagHelper.TAG_START.equals(thTag) || RXPdfExporterTagHelper.TAG_FULL.equals(thTag))) {
                            thDepth++;
                        }

                        if (element instanceof RXPrintLine) {
                            exportLine((RXPrintLine) element, gridCell);
                        } else if (element instanceof RXPrintRectangle) {
                            exportRectangle((RXPrintRectangle) element, gridCell);
                        } else if (element instanceof RXPrintEllipse) {
                            exportRectangle((RXPrintEllipse) element, gridCell);
                        } else if (element instanceof RXPrintImage) {
                        	RXPrintImage image = (RXPrintImage) element;
                        	String embeded = RXProperties.getProperty(image, PROPERTY_HTML_EMBEDEDIMAGE);
                        	if(embeded!=null && embeded.equalsIgnoreCase("true")){
                        			exportImage(image, gridCell);
                        	}else{
                        		exportImageToDiv(image, gridCell);
                        	}

                        } else if (element instanceof RXPrintText) {
                            exportText((RXPrintText) element, gridCell);
                        } else if (element instanceof RXPrintFrame) {
                            exportFrame((RXPrintFrame) element, gridCell);
                        } else if (element instanceof RXGenericPrintElement) {
                            exportGenericElement((RXGenericPrintElement) element, gridCell, rowHeight);
                        }

                        if (thTag != null && (RXPdfExporterTagHelper.TAG_END.equals(thTag) || RXPdfExporterTagHelper.TAG_FULL.equals(thTag))) {
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

        if (styleBuffer.length() > 0) {
            writer.write(" style=\"");
            writer.write(styleBuffer.toString());
            writer.write("\"");
        }

        writer.write(emptyCellStringProvider.getStringForCollapsedTD("", cell.getWidth(), rowHeight));
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

        appendPen(
                styleBuffer,
                line.getLinePen(),
                side);

        if (styleBuffer.length() > 0) {
            writer.write(" style=\"");
            writer.write(styleBuffer.toString());
            writer.write("\"");
        }

        writer.write(">");

        writer.write(emptyCellStringProvider.getStringForEmptyTD(null));

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
                return "th"; //FIXMEHTML th tags have center alignment by default
            } else {
                ElementWrapper wrapper = gridCell.getWrapper();

                OccupiedGridCell occupiedCell = gridCell instanceof OccupiedGridCell ? (OccupiedGridCell) gridCell : null;
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

    /**
     *
     */
    protected void exportRectangle(RXPrintGraphicElement element, RXExporterGridCell gridCell) throws IOException {
        writeCellStart(gridCell);

        StringBuffer styleBuffer = new StringBuffer();

        appendBackcolorStyle(gridCell, styleBuffer);

        appendPen(
                styleBuffer,
                element.getLinePen(),
                null);

        if (styleBuffer.length() > 0) {
            writer.write(" style=\"");
            writer.write(styleBuffer.toString());
            writer.write("\"");
        }

        writer.write(">");

        writer.write(emptyCellStringProvider.getStringForEmptyTD(null));

        writeCellEnd(gridCell);
    }

    /**
     *
     */
    protected void exportStyledText(RXStyledText styledText, Locale locale, String id, String width) throws IOException {
        exportStyledText(styledText, null, locale, id, width);
    }

    /**
     *
     */
    protected void exportStyledText(RXStyledText styledText, String tooltip, Locale locale, String id, String width) throws IOException {
        String text = styledText.getText();

        int runLimit = 0;

        AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();

        boolean first = true;
        boolean startedSpan = false;
        while (runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length()) {
            //if there are several text runs, write the tooltip into a parent <span>
            if (first && runLimit < styledText.length()) {
                startedSpan = true;
                if (tooltip != null) {
                    writer.write("<span title=\"");
                    writer.write(RXStringUtil.xmlEncode(tooltip));
                    writer.write("\">");
                } else {
                    writer.write("<span title=\"" + id + "\">");
                }
                //reset the tooltip so that inner <span>s to not use it
                tooltip = null;
            }
            first = false;

            exportStyledTextRun(
                    iterator.getAttributes(),
                    text.substring(iterator.getIndex(), runLimit),
                    tooltip,
                    locale,
                    id,
                    width);

            iterator.setIndex(runLimit);
        }

        if (startedSpan) {
            writer.write("</span>");
        }
    }

    /**
     *
     */
    protected void exportStyledTextRun(Map attributes, String text, Locale locale, String id, String width) throws IOException {
        exportStyledTextRun(attributes, text, null, locale, id, width);
    }

    /**
     *
     */
    protected void exportStyledTextRun(
            Map attributes,
            String text,
            String tooltip,
            Locale locale,
            String id,
            String width) throws IOException {
        String fontFamilyAttr = (String) attributes.get(TextAttribute.FAMILY);
        String fontFamily = fontFamilyAttr;

        if (fontMap != null && fontMap.containsKey(fontFamilyAttr)) {
            fontFamily = (String) fontMap.get(fontFamilyAttr);
        } else {
            FontInfo fontInfo = RXFontUtil.getFontInfo(fontFamilyAttr, locale);
            if (fontInfo != null) {
                //fontName found in font extensions
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
            style += "color: #" + RXColorUtil.getColorHexa(forecolor) + "; ";
        }

        Color runBackcolor = (Color) attributes.get(TextAttribute.BACKGROUND);
        if (runBackcolor != null) {
            style += "background-color: #" + RXColorUtil.getColorHexa(runBackcolor) + "; ";
        }

        style += "font-size: " + toSizeUnit(((Float)attributes.get(TextAttribute.SIZE)).intValue()) + "; ";

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

        writer.write("<span style=\"" + style + "\">");
        text = text.trim();
        if (text == null || text.equalsIgnoreCase("")) {
            writer.write(RXStringUtil.htmlEncode(" "));
        } else {
            String s = RXStringUtil.htmlEncode(text);
            writer.write(fixNbsp(s));
        }
        writer.write("</span>");
    }

    private String fixNbsp(String text){
        char c = 160;
        String ss = Character.toString(c);
        String s = text;
        if(text!=null){
            if(text.indexOf(ss)>=0){
                try{
                    s = StringUtils.replaceAll(text, ss, "&nbsp;");
                }catch(Exception e){
                    s = text;
                }
            }
        }
        return s;
    }
    
    /**
     *
     */
    protected void exportText(RXPrintText text, RXExporterGridCell gridCell) throws IOException {
        String s = text.getFullText();
        /* 테스트가 null이거나 공백인 경우 HTML에서 &nbsp로 바꾸기 위함 */
        if (s == null || s.equalsIgnoreCase("")) {
            text.setText(" ");
        }

        String width = toSizeUnit(text.getWidth());

        RXStyledText styledText = getStyledText(text);

        int textLength = 0;

        if (styledText != null) {
            textLength = styledText.length();
        }

        writeCellStart(gridCell);//FIXME why dealing with cell style if no text to print (textLength == 0)?

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

            if ((text.getRunDirectionValue() == RunDirectionEnum.LTR
                    && !horizontalAlignment.equals(CSS_TEXT_ALIGN_LEFT))
                    || (text.getRunDirectionValue() == RunDirectionEnum.RTL
                    && !horizontalAlignment.equals(CSS_TEXT_ALIGN_RIGHT))) {
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
        
		switch (lineSpacing)
		{
			case SINGLE:
			default:
			{
				//styleBuffer.append(" line-height: 1; *line-height: normal;");
				break;
			}
			case ONE_AND_HALF:
			{
				styleBuffer.append(" line-height: 1.5;");
				break;
			}
			case DOUBLE:
			{
				styleBuffer.append(" line-height: 2.0;");
				break;
			}
			case PROPORTIONAL:
			{
				if (lineSpacingSize != null) {
					styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + ";");
				}
				break;
			}
			case AT_LEAST:
			case FIXED:
			{
				if (lineSpacingSize != null) {
					styleBuffer.append(" line-height: " + lineSpacingSize.floatValue() + "px;");
				}
				break;
			}
		}

        if (text.getLineBreakOffsets() != null) {
            //if we have line breaks saved in the text, set nowrap so that
            //the text only wraps at the explicit positions
            styleBuffer.append("white-space: nowrap; ");
        }

        if (styleBuffer.length() > 0) {
            writer.write(" style=\"");
            writer.write(styleBuffer.toString());
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
            //only use text tooltip when no hyperlink present
            String textTooltip = hyperlinkStarted ? null : text.getHyperlinkTooltip();
            String id = RXProperties.getProperty(text, PROPERTY_HTML_ID);
//            String tagValue = RXProperties.getProperty(text, PROPERTY_HTML_TAGVALUE);
//            String script = RXProperties.getProperty(text, PROPERTY_HTML_SCRIPT);
//            String radiovalue = RXProperties.getProperty(text, PROPERTY_HTML_RADIOVALUE);

            exportStyledText(styledText, textTooltip, getTextLocale(text), id, width);
        } else {
            writer.write(emptyCellStringProvider.getStringForEmptyTD(null));
        }

        endHyperlink();

        writeCellEnd(gridCell);
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
                    List parameters = link.getHyperlinkParameters() == null ? null : link.getHyperlinkParameters().getParameters();
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
                    if (link.getHyperlinkReference() != null
                            && link.getHyperlinkAnchor() != null) {
                        href = link.getHyperlinkReference() + "#" + link.getHyperlinkAnchor();
                    }
                    break;
                }
                case REMOTE_PAGE: {
                    if (link.getHyperlinkReference() != null
                            && link.getHyperlinkPage() != null) {
                        href = link.getHyperlinkReference() + "#" + RX_PAGE_ANCHOR_PREFIX + "0_" + link.getHyperlinkPage().toString();
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
            addedToStyle |= appendPen(
                    styleBuffer,
                    box.getTopPen(),
                    "top");
            addedToStyle |= appendPadding(
                    styleBuffer,
                    box.getTopPadding(),
                    "top");
            addedToStyle |= appendPen(
                    styleBuffer,
                    box.getLeftPen(),
                    "left");
            addedToStyle |= appendPadding(
                    styleBuffer,
                    box.getLeftPadding(),
                    "left");
            addedToStyle |= appendPen(
                    styleBuffer,
                    box.getBottomPen(),
                    "bottom");
            addedToStyle |= appendPadding(
                    styleBuffer,
                    box.getBottomPadding(),
                    "bottom");
            addedToStyle |= appendPen(
                    styleBuffer,
                    box.getRightPen(),
                    "right");
            addedToStyle |= appendPadding(
                    styleBuffer,
                    box.getRightPadding(),
                    "right");
        }

        return addedToStyle;
    }

    protected Color appendBackcolorStyle(RXExporterGridCell gridCell, StringBuffer styleBuffer) {
        Color cellBackcolor = gridCell.getCellBackcolor();
        if (cellBackcolor != null && (backcolor == null || cellBackcolor.getRGB() != backcolor.getRGB())) {
            styleBuffer.append("background-color: #");
            styleBuffer.append(RXColorUtil.getColorHexa(cellBackcolor));
            styleBuffer.append("; ");

            return cellBackcolor;
        }

        return null;
    }

    /**
    *
    */
   protected void exportImage(RXPrintImage image, RXExporterGridCell gridCell) throws RXException, IOException {
       java.awt.Image tmpimg = null;
       writeCellStart(gridCell);

       ScaleImageEnum scaleImage = image.getScaleImageValue();
       
       String id = RXProperties.getProperty(image, PROPERTY_HTML_ID);
System.out.println("id = " + id);
       String alt = RXProperties.getProperty(image, PROPERTY_HTML_ALT);
       String title = RXProperties.getProperty(image, PROPERTY_HTML_TITLE);
       alt = alt==null ? "" : alt;
       
       if(title==null || title.equalsIgnoreCase("")){
       	title = RXStringUtil.xmlEncode(image.getHyperlinkTooltip());
       }
       title = title==null ? "" : title;

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
           appendPen(
                   styleBuffer,
                   image.getLinePen(),
                   null);
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
       boolean imageMapRenderer = renderer != null
               && renderer instanceof RXImageMapRenderer
               && ((RXImageMapRenderer) renderer).hasImageAreaHyperlinks();

       boolean hasHyperlinks = false;

       if (renderer != null || isUsingImagesToAlign) {
           if (imageMapRenderer) {
               hasHyperlinks = true;
               hyperlinkStarted = false;
           } else {
               hasHyperlinks = startHyperlink(image);
           }

           String imagePath = null;
           String imageMapName = null;
           List imageMapAreas = null;
           byte[] imageData = null;

           if (renderer != null) {
               tmpimg = RXImageRenderer.getInstance(renderer.getImageData()).getImage();
               imageData = renderer.getImageData();
               
               if (renderer.getType() == RXRenderable.TYPE_IMAGE && rendererToImagePathMap.containsKey(renderer.getId())) {
                   imagePath = (String) rendererToImagePathMap.get(renderer.getId());
               } else {
                   if (image.isLazy()) {
                       imagePath = ((RXImageRenderer) renderer).getImageLocation();
                   } else {
                       RXPrintElementIndex imageIndex = getElementIndex(gridCell);
                       imagesToProcess.add(imageIndex);

                       String imageName = getImageName(imageIndex);
                       imagePath = imagesURI + imageName;

                       //backward compatibility with the IMAGE_MAP parameter
                       if (imageNameToImageDataMap != null) {
                           if (renderer.getType() == RXRenderable.TYPE_SVG) {
                               renderer =
                                       new RXWrappingSvgRenderer(
                                       renderer,
                                       new Dimension(image.getWidth(), image.getHeight()),
                                       ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor() : null);
                           }
                           imageNameToImageDataMap.put(imageName, renderer.getImageData());
                       }
                       //END - backward compatibility with the IMAGE_MAP parameter
                   }

                   rendererToImagePathMap.put(renderer.getId(), imagePath);
               }

               if (imageMapRenderer) {
                   Rectangle renderingArea = new Rectangle(image.getWidth(), image.getHeight());

                   if (renderer.getType() == RXRenderable.TYPE_IMAGE) {
                       imageMapName = (String) imageMaps.get(new Pair(renderer.getId(), renderingArea));
                   }

                   if (imageMapName == null) {
                       imageMapName = "map_" + getElementIndex(gridCell).toString();
                       imageMapAreas = ((RXImageMapRenderer) originalRenderer).getImageAreaHyperlinks(renderingArea);//FIXMECHART

                       if (renderer.getType() == RXRenderable.TYPE_IMAGE) {
                           imageMaps.put(new Pair(renderer.getId(), renderingArea), imageMapName);
                       }
                   }
               }
           } else // ie: 	if(isUsingImagesToAlign)
           {
               loadPxImage();
               imagePath = imagesURI + "px";
               scaleImage = ScaleImageEnum.FILL_FRAME;
           }

           int imageWidth = image.getWidth() - image.getLineBox().getLeftPadding().intValue() - image.getLineBox().getRightPadding().intValue();
           if (imageWidth < 0) {
               imageWidth = 0;
           }

           int imageHeight = image.getHeight() - image.getLineBox().getTopPadding().intValue() - image.getLineBox().getBottomPadding().intValue();
           if (imageHeight < 0) {
               imageHeight = 0;
           }

           if (scaleImage == ScaleImageEnum.HTML_INPUT) {
               try {
                   // java.awt.Image를 BufferedImage로 변환
                   BufferedImage bi = new BufferedImage(
                           tmpimg.getWidth(null),
                           tmpimg.getHeight(null),
                           BufferedImage.TYPE_INT_ARGB);
                   Graphics g = bi.getGraphics();
                   g.drawImage(tmpimg, 0, 0, null);
                   g.dispose();

                   //BufferedImage에서 Steganography 데이터를 추출하여 AttributeBuilder로 반환
                   AttributeBuilder builder = sec.decodeHtmlInput(bi);

                   writer.write("<div");
                   if(id != null){
                       writer.write(" id=\"" + id + "\"");
                   }
                   writer.write(" style=\"cursor:pointer;cursor:hand;\"");
                   writer.write(" onclick=\"doRun(");
                   writer.write("this,");
                   writer.write("'" + sysUtils.replaceAll(builder.getImagePath(), "\\", "/") + "',");
                   writer.write("'" + builder.getImageExt() + "',");
                   writer.write("'" + String.valueOf(builder.getAttribute()) + "',");

                   double normalWidth = imageWidth;
                   double normalHeight = imageHeight;

                   try{
	                   if (!image.isLazy()) {
	                       // Image load might fail.
	                       RXRenderable tmpRenderer =
	                               RXImageRenderer.getOnErrorRendererForDimension(renderer, image.getOnErrorTypeValue());
	                       Dimension2D dimension = tmpRenderer == null ? null : tmpRenderer.getDimension();
	                       // If renderer was replaced, ignore image dimension.
	                       if (tmpRenderer == renderer && dimension != null) {
	                           normalWidth = dimension.getWidth();
	                           normalHeight = dimension.getHeight();
	                       }
	                   }
                   }catch(Exception e){}

                   if (imageHeight > 0) {
                       double ratio = normalWidth / normalHeight;

                       if (ratio > (double) imageWidth / (double) imageHeight) {
                           writer.write("'style=\\\'width: ");
                           writer.write(toSizeUnit(imageWidth));
                           writer.write("\\\'");
                       } else {
                           writer.write("'style=\\\'height: ");
                           writer.write(toSizeUnit(imageHeight));
                           writer.write("\\\'");
                       }
                   }

                   /*
                    * 이미지의 경우 alt 속성을 반드시 추가해 주어야 웹표준에 통과한다.
                    */
                   if(!alt.equalsIgnoreCase("")){
                       writer.write(" alt=\\\'" + alt + "\\\'");
                   }else{
                   	writer.write(" alt=\\\'\\\'");
                   }
                   if(!title.equalsIgnoreCase("")){
                       writer.write(" title=\\\'" + title + "\\\'");
                   }
                   
                   writer.write("')\"");
                   writer.write(">");
               } catch (Exception e) {
                   throw new RXException(e);
               }
           }
           writer.write(" <img");

           writer.write(" src=\"");
           if(imageData!=null){
        	   writer.write("data:image/png;base64,");
        	   writer.write(new String(Base64Util.encode(imageData, false)));
           }else if (imagePath != null) {
               writer.write(imagePath);
           }
           writer.write("\"");

           switch (scaleImage) {
               case FILL_FRAME: {
                   writer.write(" style=\"width: ");
                   writer.write(toSizeUnit(imageWidth));
                   writer.write("; height: ");
                   writer.write(toSizeUnit(imageHeight));
                   writer.write("\"");

                   break;
               }
               case CLIP: //FIXMEIMAGE image clip could be achieved by cutting the image and preserving the image type
               case RETAIN_SHAPE:
               default: {
                   double normalWidth = imageWidth;
                   double normalHeight = imageHeight;
                   
                   try{
	                   if (!image.isLazy()) {
	                       // Image load might fail.
	                       RXRenderable tmpRenderer =
	                               RXImageRenderer.getOnErrorRendererForDimension(renderer, image.getOnErrorTypeValue());
	                       Dimension2D dimension = tmpRenderer == null ? null : tmpRenderer.getDimension();
	                       // If renderer was replaced, ignore image dimension.
	                       if (tmpRenderer == renderer && dimension != null) {
	                           normalWidth = dimension.getWidth();
	                           normalHeight = dimension.getHeight();
	                       }
	                   }
                   }catch(Exception e){}

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
            * 이미지의 경우 alt 속성을 반드시 추가해야 웹표준에 통과한다.
            */
           if(!alt.equalsIgnoreCase("")){
               writer.write(" alt=\"" + alt + "\"");
           }else{
           	writer.write(" alt=\"\"");
           }
           
           if(!title.equalsIgnoreCase("")){
           	writer.write(" title=\"" + title + "\"");
           }

           if (hasHyperlinks) {
               writer.write(" border=\"0\"");
           }

           if (!title.equalsIgnoreCase("")) {
               writer.write(" title=\"");
               writer.write(title);
               writer.write("\"");
           }

           writer.write("/>");
           if (scaleImage == ScaleImageEnum.HTML_INPUT) {
               writer.write("</div>");
           }

           endHyperlink();

           if (imageMapAreas != null) {
               writer.write("\n");
               writeImageMap(imageMapName, image, imageMapAreas);
           }
       }
       writeCellEnd(gridCell);
   }
    
    /**
     *
     */
    protected void exportImageToDiv(RXPrintImage image, RXExporterGridCell gridCell) throws RXException, IOException {
        writeCellStart(gridCell);

        ScaleImageEnum scaleImage = image.getScaleImageValue();
        //String id = RXProperties.getProperty(image, PROPERTY_HTML_ID);

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
            appendPen(
                    styleBuffer,
                    image.getLinePen(),
                    null);
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

        if (renderer != null) {
            int imageWidth = image.getWidth() - image.getLineBox().getLeftPadding().intValue() - image.getLineBox().getRightPadding().intValue();
            if (imageWidth < 0) {
                imageWidth = 0;
            }

            int imageHeight = image.getHeight() - image.getLineBox().getTopPadding().intValue() - image.getLineBox().getBottomPadding().intValue();
            if (imageHeight < 0) {
                imageHeight = 0;
            }

            writer.write(" <span");

            switch (scaleImage) {
                case FILL_FRAME: {
                    writer.write(" style=\"width: ");
                    writer.write(toSizeUnit(imageWidth));
                    writer.write("; height: ");
                    writer.write(toSizeUnit(imageHeight));
                    writer.write("\"");

                    break;
                }
                case CLIP: //FIXMEIMAGE image clip could be achieved by cutting the image and preserving the image type
                case RETAIN_SHAPE:
                default: {
                    double normalWidth = imageWidth;
                    double normalHeight = imageHeight;

                    try{
	                    if (!image.isLazy()) {
	                        // Image load might fail.
	                        RXRenderable tmpRenderer =
	                                RXImageRenderer.getOnErrorRendererForDimension(renderer, image.getOnErrorTypeValue());
	                        Dimension2D dimension = tmpRenderer == null ? null : tmpRenderer.getDimension();
	                        // If renderer was replaced, ignore image dimension.
	                        if (tmpRenderer == renderer && dimension != null) {
	                            normalWidth = dimension.getWidth();
	                            normalHeight = dimension.getHeight();
	                        }
	                    }
                    }catch(Exception e){}

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
            writer.write(">[이미지]</span>");
        }

        writeCellEnd(gridCell);
    }

    protected RXPrintElementIndex getElementIndex(RXExporterGridCell gridCell) {
        RXPrintElementIndex imageIndex =
                new RXPrintElementIndex(
                reportIndex,
                pageIndex,
                gridCell.getWrapper().getAddress());
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
            writeImageAreaCoordinates(new int[]{0, 0, image.getWidth(), image.getHeight()});//for IE
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
       //backward compatibility with the IMAGE_MAP parameter
       if (imageNameToImageDataMap != null && !imageNameToImageDataMap.containsKey("px")) {
           RXRenderable pxRenderer =
                   RXImageRenderer.getInstance("com/cabsoft/rx/engine/images/pixel.GIF");
           rendererToImagePathMap.put(pxRenderer.getId(), imagesURI + "px");
           imageNameToImageDataMap.put("px", pxRenderer.getImageData());
       }
       //END - backward compatibility with the IMAGE_MAP parameter
   }

    /**
     *
     */
    protected static interface StringProvider {
        /**
        *
        */
       public String getStringForCollapsedTD(Object value, String style, int width, int height);
       

        /**
         *
         */
        public String getStringForCollapsedTD(Object value, int width, int height);

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
    public static String getImageName(RXPrintElementIndex printElementIndex) {
        return IMAGE_NAME_PREFIX + printElementIndex.toString();
    }

    /**
     *
     */
    public static RXPrintElementIndex getPrintElementIndex(String imageName) {
        if (!imageName.startsWith(IMAGE_NAME_PREFIX)) {
            throw new RXRuntimeException("Invalid image name: " + imageName);
        }

        return RXPrintElementIndex.parsePrintElementIndex(imageName.substring(IMAGE_NAME_PREFIX_LEGTH));
    }

    protected void exportFrame(RXPrintFrame frame, RXExporterGridCell gridCell) throws IOException, RXException {
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
            exportGrid(gridCell.getLayout(), false);
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

    protected void exportGenericElement(RXGenericPrintElement element,
            RXExporterGridCell gridCell, int rowHeight) throws IOException {
        GenericElementHtmlHandler handler = (GenericElementHtmlHandler) GenericElementHandlerEnviroment.getHandler(
                element.getGenericType(), HTML_EXPORTER_KEY);

        if (handler == null) {
            if (log.isDebugEnabled()) {
                log.debug("No HTML generic element handler for "
                        + element.getGenericType());
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

            String htmlFragment = handler.getHtmlFragment((RXHtmlExporterContext) exporterContext, element);
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
        return String.valueOf(toZoom(size)) + sizeUnit;
    }

    public int toScaledSize(int size) {
        //return Math.round((100*size*zoom)/100);
        return (int) (size * zoom);
    }

    public int toZoom(int size) {
        return (int) (zoom * size);
    }

    protected RXStyledText getStyledText(RXPrintText textElement,
            boolean setBackcolor) {
        RXStyledText styledText = super.getStyledText(textElement, setBackcolor);

        if (styledText != null) {
            short[] lineBreakOffsets = textElement.getLineBreakOffsets();
            if (lineBreakOffsets != null && lineBreakOffsets.length > 0) {
                //insert new lines at the line break positions saved at fill time
                //cloning the text first
                styledText = styledText.cloneText();
                styledText.insert("\n", lineBreakOffsets);
            }
        }

        return styledText;
    }
    
    protected String getBlankImage() throws Exception{
    	byte[] imageData = null;
    	String ret = "";
		try
		{
			RXRenderable pxRenderer = RXImageRenderer.getInstance("com/cabsoft/rx/engine/images/pixel.GIF");
			imageData = pxRenderer.getImageData();
			ret = "data:image/png;base64," + new String(Base64Util.encode(imageData, false));
		}
		catch (RXException e)
		{
			log.error(e);
			throw new Exception(e);
		}
		
		return ret;
    }
}
