package com.cabsoft.pdf.form;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.rx.engine.DefaultRXReportsContext;
import com.cabsoft.rx.engine.RXAbstractExporter;
import com.cabsoft.rx.engine.RXAnchor;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXExporterParameter;
import com.cabsoft.rx.engine.RXFont;
import com.cabsoft.rx.engine.RXGenericPrintElement;
import com.cabsoft.rx.engine.RXImageRenderer;
import com.cabsoft.rx.engine.RXLineBox;
import com.cabsoft.rx.engine.RXPen;
import com.cabsoft.rx.engine.RXPrintAnchor;
import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintEllipse;
import com.cabsoft.rx.engine.RXPrintFrame;
import com.cabsoft.rx.engine.RXPrintHyperlink;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintLine;
import com.cabsoft.rx.engine.RXPrintPage;
import com.cabsoft.rx.engine.RXPrintRectangle;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXPropertiesUtil;
import com.cabsoft.rx.engine.RXRuntimeException;
import com.cabsoft.rx.engine.RXReportsContext;
import com.cabsoft.rx.engine.Renderable;
import com.cabsoft.rx.engine.RenderableUtil;
import com.cabsoft.rx.engine.RXPropertiesUtil.PropertySuffix;
import com.cabsoft.rx.engine.base.RXBaseFont;
import com.cabsoft.rx.engine.base.RXBasePrintText;
import com.cabsoft.rx.engine.export.FontKey;
import com.cabsoft.rx.engine.export.GenericElementHandlerEnviroment;
import com.cabsoft.rx.engine.export.GenericElementPdfHandler;
import com.cabsoft.rx.engine.export.PdfFont;
import com.cabsoft.rx.engine.export.RXExportProgressMonitor;
import com.cabsoft.rx.engine.export.RXPdfExporterContext;
import com.cabsoft.rx.engine.export.RXPdfExporterParameter;
import com.cabsoft.rx.engine.export.ValueComparator;
import com.cabsoft.rx.engine.export.legacy.BorderOffset;
import com.cabsoft.rx.engine.fonts.FontFace;
import com.cabsoft.rx.engine.fonts.FontFamily;
import com.cabsoft.rx.engine.fonts.FontInfo;
import com.cabsoft.rx.engine.fonts.FontUtil;
import com.cabsoft.rx.engine.type.HyperlinkTypeEnum;
import com.cabsoft.rx.engine.type.LineDirectionEnum;
import com.cabsoft.rx.engine.type.LineStyleEnum;
import com.cabsoft.rx.engine.type.ModeEnum;
import com.cabsoft.rx.engine.type.RenderableTypeEnum;
import com.cabsoft.rx.engine.util.BreakIteratorSplitCharacter;
import com.cabsoft.rx.engine.util.RXLoader;
import com.cabsoft.rx.engine.util.RXPdfaIccProfileNotFoundException;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.rx.engine.util.RXStyledText;
import com.cabsoft.rx.engine.util.RXTextAttribute;
import com.cabsoft.rx.hbarcode.MessageBuilder;
import com.cabsoft.rx.hbarcode.sec;
import com.cabsoft.rx.repo.RepositoryUtil;

import com.cabsoft.text.BaseColor;
import com.cabsoft.text.Chunk;
import com.cabsoft.text.Document;
import com.cabsoft.text.DocumentException;
import com.cabsoft.text.Element;
import com.cabsoft.text.Font;
import com.cabsoft.text.FontFactory;
import com.cabsoft.text.Image;
import com.cabsoft.text.Phrase;
import com.cabsoft.text.Rectangle;
import com.cabsoft.text.SplitCharacter;
import com.cabsoft.text.pdf.BaseFont;
import com.cabsoft.text.pdf.ColumnText;
import com.cabsoft.awt.FontMapper;
import com.cabsoft.text.pdf.ICC_Profile;
import com.cabsoft.text.pdf.PdfAction;
import com.cabsoft.text.pdf.PdfArray;
import com.cabsoft.text.pdf.PdfContentByte;
import com.cabsoft.text.pdf.PdfDestination;
import com.cabsoft.text.pdf.PdfDictionary;
import com.cabsoft.text.pdf.PdfFormField;
import com.cabsoft.text.pdf.PdfICCBased;
import com.cabsoft.text.pdf.PdfName;
import com.cabsoft.text.pdf.PdfOutline;
import com.cabsoft.text.pdf.PdfString;
import com.cabsoft.text.pdf.PdfTemplate;
import com.cabsoft.text.pdf.PdfWriter;
import com.cabsoft.utils.StringUtils;

/**
 * Exports a ReportExpress document to PDF format. It has binary output type and
 * exports the document to a free-form layout.
 */
@SuppressWarnings({ "deprecation", "unchecked", "unused" })
public class PdfFormPdfExporter extends RXAbstractExporter {

	private final Log log = LogFactory.getLog(PdfFormPdfExporter.class);

	public final String EFORM_EXPORTER_PROPERTIES_PREFIX = RXPropertiesUtil.PROPERTY_PREFIX + "export.html.";
	
	/**
	 * Prefix of properties that specify font files for the PDF exporter.
	 */
	public final String EFORM_FONT_FILES_PREFIX = EFORM_EXPORTER_PROPERTIES_PREFIX
			+ "font.";

	/**
	 * Prefix of properties that specify font directories for the PDF exporter.
	 */
	public final String EFORM_FONT_DIRS_PREFIX = EFORM_EXPORTER_PROPERTIES_PREFIX
			+ "fontdir.";

	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getHandler(com.cabsoft.rx.engine.RXGenericElementType, String)}
	 * .
	 */
	public final String EFORM_EXPORTER_KEY = RXPropertiesUtil.PROPERTY_PREFIX + "html";

	private final String EMPTY_BOOKMARK_TITLE = "";

	private final String EFORM_FORM_PROPERTIES_PREFIX = RXPropertiesUtil.PROPERTY_PREFIX + "export.html.";

	/*
	 * PDF Form의 ID를 설정하며 Text Field나 Text의 경우 Text 입력 필드로 설정된다.
	 */
	public final String PROPERTY_EFORM_ID = EFORM_FORM_PROPERTIES_PREFIX + "id";
	
	/*
	 * RADIO BUTTON의 경우 name으로 그룹이 만들어 진다.
	 */
	public final String PROPERTY_EFORM_NAME = EFORM_FORM_PROPERTIES_PREFIX + "name";
	
	public final String PROPERTY_EFORM_SIGN = EFORM_FORM_PROPERTIES_PREFIX + "sign";

	/*
	 * PDF Form의 Tag Type를 설정하며 Text Field나 Text의 경우 Text 입력 필드로 설정된다.
	 */
	public final String PROPERTY_EFORM_TYPE = EFORM_FORM_PROPERTIES_PREFIX + "type";
	
	
	/*
	 * 콤보박스 또는 체크 박스를 설정한다.
	 * 체크박스의 경우 <checkbox>|<기본 체크 여부>의 형태로 설정하며
	 * 콤보박스의 경우 <combo>|<목록 1>^<목록 2>^<목록 3>|<기본 선택 인덱스>의 형태로 설정
	 */
	public final String PROPERTY_EFORM_TAGVALUE = EFORM_FORM_PROPERTIES_PREFIX + "tagValue";
	
	/*
	 * 라디오 버튼의 값을 설정한다.
	 * 값의 설정은 <기본 선택여부>|<선택된 경우의 리턴 값>의 형식으로 설정
	 */
	public final String PROPERTY_EFORM_RADIOVALUE = EFORM_FORM_PROPERTIES_PREFIX + "radiovalue";
	
	/*
	 * PDF 내보내기시 제외된다.
	 */
	public final String PROPERTY_EFORM_REMOVE = EFORM_FORM_PROPERTIES_PREFIX + "remove";
	
	/*
	 * PDF Text Field의 Padding
	 */
	public final String PROPERTY_EFORM_TEXTFIELD_PADDING = EFORM_FORM_PROPERTIES_PREFIX + "padding";
	
	public final String PROPERTY_EFORM_ZINDEX = EFORM_FORM_PROPERTIES_PREFIX + "zindex";

	/**
	 *
	 */
	protected final String RX_PAGE_ANCHOR_PREFIX = "RX_PAGE_ANCHOR_";

	protected boolean fontsRegistered;

	protected class ExporterContext extends BaseExporterContext implements
			RXPdfExporterContext {
		public String getExportPropertiesPrefix() {
			return EFORM_EXPORTER_PROPERTIES_PREFIX;
		}

		public PdfWriter getPdfWriter() {
			return pdfWriter;
		}
	}

	/**
	 *
	 */
	protected Document document;
	protected PdfContentByte pdfContentByte;
	protected PdfWriter pdfWriter;

	protected Document imageTesterDocument;
	protected PdfContentByte imageTesterPdfContentByte;

	protected PdfFormPdfExporterTagHelper tagHelper = new PdfFormPdfExporterTagHelper(
			this);

	protected RXExportProgressMonitor progressMonitor;

	protected int reportIndex;

	/**
	 *
	 */
	protected boolean forceSvgShapes;
	protected boolean isCreatingBatchModeBookmarks;
	protected boolean isCompressed;
	protected boolean isEncrypted;
	protected boolean is128BitKey;
	protected String userPassword;
	protected String ownerPassword;
	protected int permissions;
	protected Character pdfVersion;
	protected String pdfJavaScript;
	protected String printScaling;
	
	private SignPages signPages = null;

	private boolean collapseMissingBookmarkLevels;

	protected String pdfformat;
	protected int pdf_format;
	protected ICC_Profile icc;
	
	private RadioButtonFactory radiobuttons = new RadioButtonFactory();
	private int frameTop = 0;
	private int frameLeft = 0;

	/**
	 *
	 */
	protected Map<Renderable, Image> loadedImagesMap;
	protected Image pxImage;

	private BookmarkStack bookmarkStack;

	private Map<FontKey, PdfFont> pdfFontMap;

	private SplitCharacter splitCharacter;

	protected RXPdfExporterContext exporterContext = new ExporterContext();

	/**
	 * @see #RXPdfExporter(RXReportsContext)
	 */
	public PdfFormPdfExporter() {
		this(DefaultRXReportsContext.getInstance());
	}

	/**
	 *
	 */
	public PdfFormPdfExporter(RXReportsContext rXReportsContext) {
		super(rXReportsContext);
	}

	/**
	 *
	 */
	protected Image getPxImage() {
		if (pxImage == null) {
			try {
				pxImage = Image
						.getInstance(RXLoader
								.loadBytesFromResource("com/cabsoft/rx/engine/images/pixel.GIF"));
			} catch (Exception e) {
				throw new RXRuntimeException(e);
			}
		}

		return pxImage;
	}
	
	public SignPages getSignPages(){
		return signPages;
	}

	/**
	 *
	 */
	public void exportReport() throws RXException {
		registerFonts();

		progressMonitor = (RXExportProgressMonitor) parameters
				.get(RXExporterParameter.PROGRESS_MONITOR);

		/*   */
		setOffset();

		try {
			/*   */
			setExportContext();

			/*   */
			setInput();

			if (!parameters.containsKey(RXExporterParameter.FILTER)) {
				filter = createFilter(EFORM_EXPORTER_PROPERTIES_PREFIX);
			}

			/*   */
			if (!isModeBatch) {
				setPageRange();
			}

			isCreatingBatchModeBookmarks = getBooleanParameter(
					RXPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS,
					RXPdfExporterParameter.PROPERTY_CREATE_BATCH_MODE_BOOKMARKS,
					false);

			forceSvgShapes = // FIXME certain properties need to be read from
								// each individual document in batch mode; check
								// all exporters and all props
			getBooleanParameter(RXPdfExporterParameter.FORCE_SVG_SHAPES,
					RXPdfExporterParameter.PROPERTY_FORCE_SVG_SHAPES, false);

			isCompressed = getBooleanParameter(
					RXPdfExporterParameter.IS_COMPRESSED,
					RXPdfExporterParameter.PROPERTY_COMPRESSED, false);

			isEncrypted = getBooleanParameter(
					RXPdfExporterParameter.IS_ENCRYPTED,
					RXPdfExporterParameter.PROPERTY_ENCRYPTED, false);

			is128BitKey = getBooleanParameter(
					RXPdfExporterParameter.IS_128_BIT_KEY,
					RXPdfExporterParameter.PROPERTY_128_BIT_KEY, false);

			userPassword = getStringParameter(
					RXPdfExporterParameter.USER_PASSWORD,
					RXPdfExporterParameter.PROPERTY_USER_PASSWORD);

			ownerPassword = getStringParameter(
					RXPdfExporterParameter.OWNER_PASSWORD,
					RXPdfExporterParameter.PROPERTY_OWNER_PASSWORD);

			pdfformat = getStringParameter(RXPdfExporterParameter.PDF_FORMAT,
					RXPdfExporterParameter.PROPERTY_PDF_FORMAT);

			if (pdfformat.equalsIgnoreCase("PDFA1A")) {
				pdf_format = PdfWriter.PDFA1A;
			} else if (pdfformat.equalsIgnoreCase("PDFA1B")) {
				pdf_format = PdfWriter.PDFA1B;
				// }else if(pdfformat.equalsIgnoreCase("PDFX1A2001")){
				// pdf_format = PdfWriter.PDFX1A2001;
				// }else if(pdfformat.equalsIgnoreCase("PDFX32002")){
				// pdf_format = PdfWriter.PDFX32002;
			} else {
				pdf_format = PdfWriter.PDFXNONE;
			}

			Integer permissionsParameter = (Integer) parameters
					.get(RXPdfExporterParameter.PERMISSIONS);
			if (permissionsParameter != null) {
				permissions = permissionsParameter.intValue();
			}

			pdfVersion = getCharacterParameter(
					RXPdfExporterParameter.PDF_VERSION,
					RXPdfExporterParameter.PROPERTY_PDF_VERSION);

			setFontMap();
			setSplitCharacter();
			setHyperlinkProducerFactory();

			pdfJavaScript = getStringParameter(
					RXPdfExporterParameter.PDF_JAVASCRIPT,
					RXPdfExporterParameter.PROPERTY_PDF_JAVASCRIPT);

			printScaling = getStringParameter(
					RXPdfExporterParameter.PRINT_SCALING,
					RXPdfExporterParameter.PROPERTY_PRINT_SCALING);

			tagHelper.setTagged(getBooleanParameter(
					RXPdfExporterParameter.IS_TAGGED, RXPdfExporterParameter.PROPERTY_TAGGED, false));

			// no export param for this
			collapseMissingBookmarkLevels = RXPropertiesUtil
					.getInstance(rxReportsContext)
					.getBooleanProperty(
							reportexpressPrint,
							RXPdfExporterParameter.PROPERTY_COLLAPSE_MISSING_BOOKMARK_LEVELS, false);

			OutputStream os = (OutputStream) parameters
					.get(RXExporterParameter.OUTPUT_STREAM);
			if (os != null) {
				exportReportToStream(os);
			} else {
				File destFile = (File) parameters
						.get(RXExporterParameter.OUTPUT_FILE);
				if (destFile == null) {
					String fileName = (String) parameters
							.get(RXExporterParameter.OUTPUT_FILE_NAME);
					if (fileName != null) {
						destFile = new File(fileName);
					} else {
						throw new RXException(
								"No output specified for the exporter.");
					}
				}

				try {
					os = new FileOutputStream(destFile);
					exportReportToStream(os);
					os.flush();
				} catch (IOException e) {
					throw new RXException("Error trying to export to file : "
							+ destFile, e);
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
						}
					}
				}
			}
		} finally {
			resetExportContext();
		}
	}

	protected void setFontMap() {
		pdfFontMap = (Map<FontKey, PdfFont>) parameters
				.get(RXExporterParameter.FONT_MAP);
	}

	protected void setSplitCharacter() {
		boolean useFillSplitCharacter;
		Boolean useFillSplitCharacterParam = (Boolean) parameters
				.get(RXPdfExporterParameter.FORCE_LINEBREAK_POLICY);
		if (useFillSplitCharacterParam == null) {
			useFillSplitCharacter = RXPropertiesUtil.getInstance(
					rxReportsContext).getBooleanProperty(
					reportexpressPrint.getPropertiesMap(),
					RXPdfExporterParameter.PROPERTY_FORCE_LINEBREAK_POLICY,
					false);
		} else {
			useFillSplitCharacter = useFillSplitCharacterParam.booleanValue();
		}

		if (useFillSplitCharacter) {
			splitCharacter = new BreakIteratorSplitCharacter();
		}
	}

	/**
	 *
	 */
	protected void exportReportToStream(OutputStream os) throws RXException {
		signPages = new SignPages();

		document = new Document(new Rectangle(
				reportexpressPrint.getPageWidth(),
				reportexpressPrint.getPageHeight()));

		imageTesterDocument = new Document(new Rectangle(10, // reportexpressPrint.getPageWidth(),
				10 // reportexpressPrint.getPageHeight()
				));

		boolean closeDocuments = true;
		try {
			pdfWriter = PdfWriter.getInstance(document, os);
			pdfWriter.setCloseStream(false);

			tagHelper.setPdfWriter(pdfWriter);

			if (pdfVersion != null) {
				pdfWriter.setPdfVersion(pdfVersion.charValue());
			}
			if (isCompressed) {
				pdfWriter.setFullCompression();
			}
			if (isEncrypted) {
				pdfWriter.setEncryption(is128BitKey, userPassword,
						ownerPassword, permissions);
			}

			if (printScaling != null) {
				if (RXPdfExporterParameter.PRINT_SCALING_DEFAULT
						.equals(printScaling)) {
					pdfWriter.addViewerPreference(PdfName.PRINTSCALING,
							PdfName.APPDEFAULT);
				} else if (RXPdfExporterParameter.PRINT_SCALING_NONE
						.equals(printScaling)) {
					pdfWriter.addViewerPreference(PdfName.PRINTSCALING,
							PdfName.NONE);
				}
			}

			// Add meta-data parameters to generated PDF document
			// mtclough@users.sourceforge.net 2005-12-05
			String title = (String) parameters
					.get(RXPdfExporterParameter.METADATA_TITLE);
			if (title != null) {
				document.addTitle(title);
			}
			String author = (String) parameters
					.get(RXPdfExporterParameter.METADATA_AUTHOR);
			if (author != null) {
				document.addAuthor(author);
			}
			String subject = (String) parameters
					.get(RXPdfExporterParameter.METADATA_SUBJECT);
			if (subject != null) {
				document.addSubject(subject);
			}
			String keywords = (String) parameters
					.get(RXPdfExporterParameter.METADATA_KEYWORDS);
			if (keywords != null) {
				document.addKeywords(keywords);
			}
			String creator = (String) parameters
					.get(RXPdfExporterParameter.METADATA_CREATOR);
			if (creator != null) {
				document.addCreator(creator);
			} else {
				document.addCreator("ReportExpress ("
						+ reportexpressPrint.getName() + ")");
			}

			// BEGIN: PDF/A support
			String pdfaConformance = getStringParameter(
					RXPdfExporterParameter.PDFA_CONFORMANCE,
					RXPdfExporterParameter.PROPERTY_PDFA_CONFORMANCE);
			boolean gotPdfa = false;
			if (pdfaConformance != null
					&& !RXPdfExporterParameter.PDFA_CONFORMANCE_NONE
							.equalsIgnoreCase(pdfaConformance)) {
				if (RXPdfExporterParameter.PDFA_CONFORMANCE_1A
						.equalsIgnoreCase(pdfaConformance)) {
					pdfWriter.setPDFXConformance(PdfWriter.PDFA1A);
					gotPdfa = true;
				} else if (RXPdfExporterParameter.PDFA_CONFORMANCE_1B
						.equalsIgnoreCase(pdfaConformance)) {
					pdfWriter.setPDFXConformance(PdfWriter.PDFA1B);
					gotPdfa = true;
				}
			}

			if (gotPdfa) {
				pdfWriter.createXmpMetadata();
			} else {
				pdfWriter.setRgbTransparencyBlending(true);
			}
			// END: PDF/A support

			document.open();

			// BEGIN: PDF/A support
			if (gotPdfa) {
				String iccProfilePath = getStringParameter(
						RXPdfExporterParameter.PDFA_ICC_PROFILE_PATH,
						RXPdfExporterParameter.PROPERTY_PDFA_ICC_PROFILE_PATH);
				if (iccProfilePath != null) {
					PdfDictionary pdfDictionary = new PdfDictionary(
							PdfName.OUTPUTINTENT);
					pdfDictionary.put(PdfName.OUTPUTCONDITIONIDENTIFIER,
							new PdfString("sRGB IEC61966-2.1"));
					pdfDictionary.put(PdfName.INFO, new PdfString(
							"sRGB IEC61966-2.1"));
					pdfDictionary.put(PdfName.S, PdfName.GTS_PDFA1);

					InputStream iccIs = RepositoryUtil.getInstance(
							rxReportsContext).getInputStreamFromLocation(
							iccProfilePath);
					PdfICCBased pdfICCBased = new PdfICCBased(
							ICC_Profile.getInstance(iccIs));
					pdfICCBased.remove(PdfName.ALTERNATE);
					pdfDictionary.put(PdfName.DESTOUTPUTPROFILE, pdfWriter
							.addToBody(pdfICCBased).getIndirectReference());

					pdfWriter.getExtraCatalog().put(PdfName.OUTPUTINTENTS,
							new PdfArray(pdfDictionary));
				} else {
					throw new RXPdfaIccProfileNotFoundException();
				}
			}
			// END: PDF/A support

			if (pdfJavaScript != null) {
				pdfWriter.addJavaScript(pdfJavaScript);
			}

			pdfContentByte = pdfWriter.getDirectContent();

			tagHelper.init(pdfContentByte);

			initBookmarks();

			PdfWriter imageTesterPdfWriter = PdfWriter.getInstance(
					imageTesterDocument, new NullOutputStream() // discard the
																// output
					);
			imageTesterDocument.open();
			imageTesterDocument.newPage();
			imageTesterPdfContentByte = imageTesterPdfWriter.getDirectContent();
			imageTesterPdfContentByte.setLiteral("\n");

			for (reportIndex = 0; reportIndex < rxPrintList.size(); reportIndex++) {
				setReportExpressPrint(rxPrintList.get(reportIndex));
				loadedImagesMap = new HashMap<Renderable, Image>();

				setPageSize(null);

				BorderOffset.setLegacy(RXPropertiesUtil.getInstance(
						rxReportsContext).getBooleanProperty(
						reportexpressPrint,
						BorderOffset.PROPERTY_LEGACY_BORDER_OFFSET, false));

				boolean sizePageToContent = RXPropertiesUtil.getInstance(
						rxReportsContext).getBooleanProperty(
						reportexpressPrint,
						RXPdfExporterParameter.PROPERTY_SIZE_PAGE_TO_CONTENT,
						false);

				List<RXPrintPage> pages = reportexpressPrint.getPages();
				if (pages != null && pages.size() > 0) {
					if (isModeBatch) {
						document.newPage();

						if (isCreatingBatchModeBookmarks) {
							// add a new level to our outline for this report
							addBookmark(0, reportexpressPrint.getName(), 0, 0);
						}

						startPageIndex = 0;
						endPageIndex = pages.size() - 1;
					}

					for (int pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++) {
						if (Thread.interrupted()) {
							throw new RXException("Current thread interrupted.");
						}
						
						radiobuttons = new RadioButtonFactory();

						RXPrintPage page = pages.get(pageIndex);

						if (sizePageToContent) {
							setPageSize(page);
						}

						document.newPage();

						pdfContentByte = pdfWriter.getDirectContent();

						pdfContentByte.setLineCap(2);// PdfContentByte.LINE_CAP_PROJECTING_SQUARE
														// since iText 1.02b

						writePageAnchor(pageIndex);

						/*
						 * 현재 페이지에 대한 자필서명 위치 초기화
						 */
						signPages.newSignPosition();
						
						/*   */
						exportPage(page);
						
						HashMap<String, RadioButtons> factory = radiobuttons.getButtonFactory();
						Iterator<String> it = factory.keySet().iterator();
						while(it.hasNext()){
							String group = it.next();
							RadioButtons buttons = factory.get(group);
							PdfFormField radiogroup = PdfFormField.createRadioButton(pdfWriter, false);
							radiogroup.setFieldName(group);
							ArrayList<RadioButton> list = buttons.getRadioButtonList();
							for(int i=0; i<list.size(); i++){
								RadioButton button = list.get(i);
								PdfFormBuilder.addRadio(rxReportsContext, pdfWriter, 
										reportexpressPrint.getPageWidth(), reportexpressPrint.getPageHeight(), radiogroup, button);
							}
							pdfWriter.addAnnotation(radiogroup);
						}
						
						/*
						 * 현재 페이지에 대한 자필서명 위치 저장
						 */
						signPages.addSignPages();
						
						
					}
				} else {
					document.newPage();
					pdfContentByte = pdfWriter.getDirectContent();
					pdfContentByte.setLiteral("\n");
				}
			}
			
//			HashMap<String, RadioButtons> factory = radiobuttons.getButtonFactory();
//			Iterator<String> it = factory.keySet().iterator();
//			while(it.hasNext()){
//				String group = it.next();
//				RadioButtons buttons = factory.get(group);
//				PdfFormField radiogroup = PdfFormField.createRadioButton(pdfWriter, false);
//				radiogroup.setFieldName(group);
//				ArrayList<RadioButton> list = buttons.getRadioButtonList();
//				for(int i=0; i<list.size(); i++){
//					RadioButton button = list.get(i);
//					EFormBuilder.addRadio(rxReportsContext, pdfWriter, 
//							reportexpressPrint.getPageWidth(), reportexpressPrint.getPageHeight(), radiogroup, button);
//				}
//				pdfWriter.addAnnotation(radiogroup);
//			}

			closeDocuments = false;
			document.close();
			imageTesterDocument.close();
		} catch (DocumentException e) {
			throw new RXException("PDF Document error : "
					+ reportexpressPrint.getName(), e);
		} catch (IOException e) {
			throw new RXException("Error generating PDF report : "
					+ reportexpressPrint.getName(), e);
		} finally {
			radiobuttons.FactoryClear();
			
			if (closeDocuments) // only on exception
			{
				try {
					document.close();
				} catch (Exception e) {
					// ignore, let the original exception propagate
				}

				try {
					imageTesterDocument.close();
				} catch (Exception e) {
					// ignore, let the original exception propagate
				}
			}
		}

		// return os.toByteArray();
	}

	protected void writePageAnchor(int pageIndex) throws DocumentException {
		Map<Attribute, Object> attributes = new HashMap<Attribute, Object>();
		FontUtil.getInstance(rxReportsContext).getAttributesWithoutAwtFont(
				attributes,
				new RXBasePrintText(reportexpressPrint
						.getDefaultStyleProvider()));
		Font pdfFont = getFont(attributes, getLocale(), false);
		Chunk chunk = new Chunk(" ", pdfFont);

		chunk.setLocalDestination(RX_PAGE_ANCHOR_PREFIX + reportIndex + "_"
				+ (pageIndex + 1));

		tagHelper.startPageAnchor();

		ColumnText colText = new ColumnText(pdfContentByte);
		colText.setSimpleColumn(new Phrase(chunk), 0,
				reportexpressPrint.getPageHeight(), 1, 1, 0, Element.ALIGN_LEFT);

		colText.go();

		tagHelper.endPageAnchor();
	}

	/**
	 *
	 */
	protected void setPageSize(RXPrintPage page) throws RXException,
			DocumentException, IOException {
		int pageWidth = reportexpressPrint.getPageWidth();
		int pageHeight = reportexpressPrint.getPageHeight();

		if (page != null) {
			Collection<RXPrintElement> elements = page.getElements();
			for (RXPrintElement element : elements) {
				int elementRight = element.getX() + element.getWidth();
				int elementBottom = element.getY() + element.getHeight();
				pageWidth = pageWidth < elementRight ? elementRight : pageWidth;
				pageHeight = pageHeight < elementBottom ? elementBottom
						: pageHeight;
			}

			pageWidth += reportexpressPrint.getRightMargin();
			pageHeight += reportexpressPrint.getBottomMargin();
		}

		Rectangle pageSize;
		switch (reportexpressPrint.getOrientationValue()) {
		case LANDSCAPE:
			// using rotate to indicate landscape page
			pageSize = new Rectangle(pageHeight, pageWidth).rotate();
			break;
		default:
			pageSize = new Rectangle(pageWidth, pageHeight);
			break;
		}
		
		document.setPageSize(pageSize);
	}

	/**
	 *
	 */
	protected void exportPage(RXPrintPage page) throws RXException,
			DocumentException, IOException {
		tagHelper.startPage();

		Collection<RXPrintElement> elements = page.getElements();
		
		HashMap<String, Integer> list = new HashMap<String, Integer>();
		HashMap<String, RXPrintElement> zmap = new HashMap<String, RXPrintElement>();
		List<RXPrintElement> els = new ArrayList<RXPrintElement>();
		
		if (elements != null && elements.size() > 0)
		{
			int idx = 0;
			for(Iterator<RXPrintElement> it = elements.iterator(); it.hasNext();)
			{
				RXPrintElement element = it.next();
				String zindex = RXProperties.getProperty(element, PROPERTY_EFORM_ZINDEX);
				if(zindex==null){
					els.add(element);
				}else{
					zindex = zindex.trim();
					list.put("idx_" + String.valueOf(idx), Integer.valueOf(zindex));
					zmap.put("idx_" + String.valueOf(idx), element);
				}
				idx++;
			}
		}

		List<RXPrintElement> zlist = new ArrayList<RXPrintElement>();

		log.debug("list = " + list.toString());
		
		ValueComparator bvc =  new ValueComparator(list);
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(bvc);
		treeMap.putAll(list);
		
		log.debug("treeMap = " + treeMap.toString());
		
		Iterator<String> it = treeMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			zlist.add(zmap.get(key));
		}
		
		exportElements(zlist);
		exportElements(els);
		
//		exportElements(elements);

		tagHelper.endPage();

		if (progressMonitor != null) {
			progressMonitor.afterPageExport();
		}
	}

	protected void exportElements(Collection<RXPrintElement> elements)
			throws DocumentException, IOException, RXException {
		if (elements != null && elements.size() > 0) {
			for (Iterator<RXPrintElement> it = elements.iterator(); it
					.hasNext();) {
				RXPrintElement element = it.next();

				if (filter == null || filter.isToExport(element)) {
					tagHelper.startElement(element);

					if (element instanceof RXPrintLine) {
						exportLine((RXPrintLine) element);
					} else if (element instanceof RXPrintRectangle) {
						exportRectangle((RXPrintRectangle) element);
					} else if (element instanceof RXPrintEllipse) {
						exportEllipse((RXPrintEllipse) element);
					} else if (element instanceof RXPrintImage) {
						exportImage((RXPrintImage) element);
					} else if (element instanceof RXPrintText) {
						exportText((RXPrintText) element);
					} else if (element instanceof RXPrintFrame) {
						exportFrame((RXPrintFrame) element);
					} else if (element instanceof RXGenericPrintElement) {
						exportGenericElement((RXGenericPrintElement) element);
					}

					tagHelper.endElement(element);
				}
			}
		}
	}

	/**
	 *
	 */
	protected void exportLine(RXPrintLine line) {
		float lineWidth = line.getLinePen().getLineWidth().floatValue();
		if (lineWidth > 0f) {
			preparePen(pdfContentByte, line.getLinePen(),
					PdfContentByte.LINE_CAP_BUTT);

			if (line.getWidth() == 1) {
				if (line.getHeight() != 1) {
					// Vertical line
					if (line.getLinePen().getLineStyleValue() == LineStyleEnum.DOUBLE) {
						pdfContentByte.moveTo(
								line.getX() + getOffsetX() + 0.5f - lineWidth
										/ 3,
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY());
						pdfContentByte.lineTo(
								line.getX() + getOffsetX() + 0.5f - lineWidth
										/ 3,
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY()
										- line.getHeight());

						pdfContentByte.stroke();

						pdfContentByte.moveTo(
								line.getX() + getOffsetX() + 0.5f + lineWidth
										/ 3,
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY());
						pdfContentByte.lineTo(
								line.getX() + getOffsetX() + 0.5f + lineWidth
										/ 3,
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY()
										- line.getHeight());
					} else {
						pdfContentByte.moveTo(
								line.getX() + getOffsetX() + 0.5f,
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY());
						pdfContentByte.lineTo(
								line.getX() + getOffsetX() + 0.5f,
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY()
										- line.getHeight());
					}
				}
			} else {
				if (line.getHeight() == 1) {
					// Horizontal line
					if (line.getLinePen().getLineStyleValue() == LineStyleEnum.DOUBLE) {
						pdfContentByte.moveTo(
								line.getX() + getOffsetX(),
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY() - 0.5f
										+ lineWidth / 3);
						pdfContentByte.lineTo(
								line.getX() + getOffsetX() + line.getWidth(),
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY() - 0.5f
										+ lineWidth / 3);

						pdfContentByte.stroke();

						pdfContentByte.moveTo(
								line.getX() + getOffsetX(),
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY() - 0.5f
										- lineWidth / 3);
						pdfContentByte.lineTo(
								line.getX() + getOffsetX() + line.getWidth(),
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY() - 0.5f
										- lineWidth / 3);
					} else {
						pdfContentByte.moveTo(
								line.getX() + getOffsetX(),
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY() - 0.5f);
						pdfContentByte.lineTo(
								line.getX() + getOffsetX() + line.getWidth(),
								reportexpressPrint.getPageHeight()
										- line.getY() - getOffsetY() - 0.5f);
					}
				} else {
					// Oblique line
					if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN) {
						if (line.getLinePen().getLineStyleValue() == LineStyleEnum.DOUBLE) {
							double xtrans = lineWidth
									/ (3 * Math.sqrt(1
											+ Math.pow(line.getWidth(), 2)
											/ Math.pow(line.getHeight(), 2)));
							double ytrans = lineWidth
									/ (3 * Math.sqrt(1
											+ Math.pow(line.getHeight(), 2)
											/ Math.pow(line.getWidth(), 2)));

							pdfContentByte
									.moveTo(line.getX() + getOffsetX()
											+ (float) xtrans,
											reportexpressPrint.getPageHeight()
													- line.getY()
													- getOffsetY()
													+ (float) ytrans);
							pdfContentByte
									.lineTo(line.getX() + getOffsetX()
											+ line.getWidth() + (float) xtrans,
											reportexpressPrint.getPageHeight()
													- line.getY()
													- getOffsetY()
													- line.getHeight()
													+ (float) ytrans);

							pdfContentByte.stroke();

							pdfContentByte
									.moveTo(line.getX() + getOffsetX()
											- (float) xtrans,
											reportexpressPrint.getPageHeight()
													- line.getY()
													- getOffsetY()
													- (float) ytrans);
							pdfContentByte
									.lineTo(line.getX() + getOffsetX()
											+ line.getWidth() - (float) xtrans,
											reportexpressPrint.getPageHeight()
													- line.getY()
													- getOffsetY()
													- line.getHeight()
													- (float) ytrans);
						} else {
							pdfContentByte.moveTo(
									line.getX() + getOffsetX(),
									reportexpressPrint.getPageHeight()
											- line.getY() - getOffsetY());
							pdfContentByte.lineTo(
									line.getX() + getOffsetX()
											+ line.getWidth(),
									reportexpressPrint.getPageHeight()
											- line.getY() - getOffsetY()
											- line.getHeight());
						}
					} else {
						if (line.getLinePen().getLineStyleValue() == LineStyleEnum.DOUBLE) {
							double xtrans = lineWidth
									/ (3 * Math.sqrt(1
											+ Math.pow(line.getWidth(), 2)
											/ Math.pow(line.getHeight(), 2)));
							double ytrans = lineWidth
									/ (3 * Math.sqrt(1
											+ Math.pow(line.getHeight(), 2)
											/ Math.pow(line.getWidth(), 2)));

							pdfContentByte
									.moveTo(line.getX() + getOffsetX()
											+ (float) xtrans,
											reportexpressPrint.getPageHeight()
													- line.getY()
													- getOffsetY()
													- line.getHeight()
													- (float) ytrans);
							pdfContentByte.lineTo(
									line.getX() + getOffsetX()
											+ line.getWidth() + (float) xtrans,
									reportexpressPrint.getPageHeight()
											- line.getY() - getOffsetY()
											- (float) ytrans);

							pdfContentByte.stroke();

							pdfContentByte
									.moveTo(line.getX() + getOffsetX()
											- (float) xtrans,
											reportexpressPrint.getPageHeight()
													- line.getY()
													- getOffsetY()
													- line.getHeight()
													+ (float) ytrans);
							pdfContentByte.lineTo(
									line.getX() + getOffsetX()
											+ line.getWidth() - (float) xtrans,
									reportexpressPrint.getPageHeight()
											- line.getY() - getOffsetY()
											+ (float) ytrans);
						} else {
							pdfContentByte.moveTo(
									line.getX() + getOffsetX(),
									reportexpressPrint.getPageHeight()
											- line.getY() - getOffsetY()
											- line.getHeight());
							pdfContentByte.lineTo(
									line.getX() + getOffsetX()
											+ line.getWidth(),
									reportexpressPrint.getPageHeight()
											- line.getY() - getOffsetY());
						}
					}
				}
			}

			pdfContentByte.stroke();

			pdfContentByte.setLineDash(0f);
			pdfContentByte
					.setLineCap(PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
		}
	}

	/**
	 *
	 */
	protected void exportRectangle(RXPrintRectangle rectangle) {
		pdfContentByte.setRGBColorFill(rectangle.getBackcolor().getRed(),
				rectangle.getBackcolor().getGreen(), rectangle.getBackcolor()
						.getBlue());

		preparePen(pdfContentByte, rectangle.getLinePen(),
				PdfContentByte.LINE_CAP_PROJECTING_SQUARE);

		float lineWidth = rectangle.getLinePen().getLineWidth().floatValue();
		float lineOffset = BorderOffset.getOffset(rectangle.getLinePen());

		if (rectangle.getModeValue() == ModeEnum.OPAQUE) {
			pdfContentByte.roundRectangle(rectangle.getX() + getOffsetX(),
					reportexpressPrint.getPageHeight() - rectangle.getY()
							- getOffsetY() - rectangle.getHeight(),
					rectangle.getWidth(), rectangle.getHeight(),
					rectangle.getRadius());

			pdfContentByte.fill();
		}

		if (lineWidth > 0f) {
			if (rectangle.getLinePen().getLineStyleValue() == LineStyleEnum.DOUBLE) {
				pdfContentByte.roundRectangle(rectangle.getX() + getOffsetX()
						- lineWidth / 3,
						reportexpressPrint.getPageHeight() - rectangle.getY()
								- getOffsetY() - rectangle.getHeight()
								- lineWidth / 3, rectangle.getWidth() + 2
								* lineWidth / 3, rectangle.getHeight() + 2
								* lineWidth / 3, rectangle.getRadius());

				pdfContentByte.stroke();

				pdfContentByte.roundRectangle(rectangle.getX() + getOffsetX()
						+ lineWidth / 3,
						reportexpressPrint.getPageHeight() - rectangle.getY()
								- getOffsetY() - rectangle.getHeight()
								+ lineWidth / 3, rectangle.getWidth() - 2
								* lineWidth / 3, rectangle.getHeight() - 2
								* lineWidth / 3, rectangle.getRadius());

				pdfContentByte.stroke();
			} else {
				pdfContentByte.roundRectangle(rectangle.getX() + getOffsetX()
						+ lineOffset,
						reportexpressPrint.getPageHeight() - rectangle.getY()
								- getOffsetY() - rectangle.getHeight()
								+ lineOffset, rectangle.getWidth() - 2
								* lineOffset, rectangle.getHeight() - 2
								* lineOffset, rectangle.getRadius());

				pdfContentByte.stroke();
			}
		}

		pdfContentByte.setLineDash(0f);
	}

	/**
	 *
	 */
	protected void exportEllipse(RXPrintEllipse ellipse) {
		pdfContentByte.setRGBColorFill(ellipse.getBackcolor().getRed(), ellipse
				.getBackcolor().getGreen(), ellipse.getBackcolor().getBlue());

		preparePen(pdfContentByte, ellipse.getLinePen(),
				PdfContentByte.LINE_CAP_PROJECTING_SQUARE);

		float lineWidth = ellipse.getLinePen().getLineWidth().floatValue();
		float lineOffset = BorderOffset.getOffset(ellipse.getLinePen());

		if (ellipse.getModeValue() == ModeEnum.OPAQUE) {
			pdfContentByte.ellipse(ellipse.getX() + getOffsetX(),
					reportexpressPrint.getPageHeight() - ellipse.getY()
							- getOffsetY() - ellipse.getHeight(),
					ellipse.getX() + getOffsetX() + ellipse.getWidth(),
					reportexpressPrint.getPageHeight() - ellipse.getY()
							- getOffsetY());

			pdfContentByte.fill();
		}

		if (lineWidth > 0f) {
			if (ellipse.getLinePen().getLineStyleValue() == LineStyleEnum.DOUBLE) {
				pdfContentByte.ellipse(ellipse.getX() + getOffsetX()
						- lineWidth / 3, reportexpressPrint.getPageHeight()
						- ellipse.getY() - getOffsetY() - ellipse.getHeight()
						- lineWidth / 3, ellipse.getX() + getOffsetX()
						+ ellipse.getWidth() + lineWidth / 3,
						reportexpressPrint.getPageHeight() - ellipse.getY()
								- getOffsetY() + lineWidth / 3);

				pdfContentByte.stroke();

				pdfContentByte.ellipse(ellipse.getX() + getOffsetX()
						+ lineWidth / 3, reportexpressPrint.getPageHeight()
						- ellipse.getY() - getOffsetY() - ellipse.getHeight()
						+ lineWidth / 3, ellipse.getX() + getOffsetX()
						+ ellipse.getWidth() - lineWidth / 3,
						reportexpressPrint.getPageHeight() - ellipse.getY()
								- getOffsetY() - lineWidth / 3);

				pdfContentByte.stroke();
			} else {
				pdfContentByte.ellipse(ellipse.getX() + getOffsetX()
						+ lineOffset, reportexpressPrint.getPageHeight()
						- ellipse.getY() - getOffsetY() - ellipse.getHeight()
						+ lineOffset,
						ellipse.getX() + getOffsetX() + ellipse.getWidth()
								- lineOffset,
						reportexpressPrint.getPageHeight() - ellipse.getY()
								- getOffsetY() - lineOffset);

				pdfContentByte.stroke();
			}
		}

		pdfContentByte.setLineDash(0f);
	}

	/**
	 *
	 */
	public void exportImage(RXPrintImage printImage) throws DocumentException,
			IOException, RXException {
		boolean isHDBarcode = false;
		boolean deleteAfter = false;
		String barcodefs = "";
		String imgPath = "";

		if (printImage.getModeValue() == ModeEnum.OPAQUE
				|| !pdfformat.equalsIgnoreCase("PDFXNONE")) {
			pdfContentByte.setRGBColorFill(printImage.getBackcolor().getRed(),
					printImage.getBackcolor().getGreen(), printImage
							.getBackcolor().getBlue());
			pdfContentByte.rectangle(printImage.getX() + getOffsetX(),
					reportexpressPrint.getPageHeight() - printImage.getY()
							- getOffsetY(), printImage.getWidth(),
					-printImage.getHeight());
			pdfContentByte.fill();
		}

		int topPadding = printImage.getLineBox().getTopPadding().intValue();
		int leftPadding = printImage.getLineBox().getLeftPadding().intValue();
		int bottomPadding = printImage.getLineBox().getBottomPadding()
				.intValue();
		int rightPadding = printImage.getLineBox().getRightPadding().intValue();

		int availableImageWidth = printImage.getWidth() - leftPadding
				- rightPadding;
		availableImageWidth = (availableImageWidth < 0) ? 0
				: availableImageWidth;

		int availableImageHeight = printImage.getHeight() - topPadding
				- bottomPadding;
		availableImageHeight = (availableImageHeight < 0) ? 0
				: availableImageHeight;

		Renderable renderer = printImage.getRenderable();

		if (renderer != null && availableImageWidth > 0
				&& availableImageHeight > 0) {
			if (renderer.getTypeValue() == RenderableTypeEnum.IMAGE) {
				// Image renderers are all asked for their image data at some
				// point.
				// Better to test and replace the renderer now, in case of lazy
				// load error.
				renderer = RenderableUtil.getInstance(rxReportsContext)
						.getOnErrorRendererForImageData(renderer,
								printImage.getOnErrorTypeValue());
			}
		} else {
			renderer = null;
		}

		if (renderer != null) {
			int xoffset = 0;
			int yoffset = 0;

			Chunk chunk = null;

			float scaledWidth = availableImageWidth;
			float scaledHeight = availableImageHeight;

			if (renderer.getTypeValue() == RenderableTypeEnum.IMAGE) {
				Image image = null;

				float xalignFactor = getXAlignFactor(printImage);
				float yalignFactor = getYAlignFactor(printImage);

				switch (printImage.getScaleImageValue()) {
				case CLIP: {
					// Image load might fail, from given image data.
					// Better to test and replace the renderer now, in case of
					// lazy load error.
					renderer = RenderableUtil.getInstance(rxReportsContext)
							.getOnErrorRendererForDimension(renderer,
									printImage.getOnErrorTypeValue());
					if (renderer == null) {
						break;
					}

					int normalWidth = availableImageWidth;
					int normalHeight = availableImageHeight;

					Dimension2D dimension = renderer
							.getDimension(rxReportsContext);
					if (dimension != null) {
						normalWidth = (int) dimension.getWidth();
						normalHeight = (int) dimension.getHeight();
					}

					xoffset = (int) (xalignFactor * (availableImageWidth - normalWidth));
					yoffset = (int) (yalignFactor * (availableImageHeight - normalHeight));

					int minWidth = Math.min(normalWidth, availableImageWidth);
					int minHeight = Math
							.min(normalHeight, availableImageHeight);

					BufferedImage bi = new BufferedImage(minWidth, minHeight,
							BufferedImage.TYPE_INT_ARGB);

					Graphics2D g = bi.createGraphics();
					if (printImage.getModeValue() == ModeEnum.OPAQUE
							|| !pdfformat.equalsIgnoreCase("PDFXNONE")) {
						g.setColor(printImage.getBackcolor());
						g.fillRect(0, 0, minWidth, minHeight);
					}
					renderer.render(rxReportsContext, g,
							new java.awt.Rectangle((xoffset > 0 ? 0 : xoffset),
									(yoffset > 0 ? 0 : yoffset), normalWidth,
									normalHeight));
					g.dispose();

					xoffset = (xoffset < 0 ? 0 : xoffset);
					yoffset = (yoffset < 0 ? 0 : yoffset);

					// awtImage = bi.getSubimage(0, 0, minWidth, minHeight);

					// image = com.lowagie.text.Image.getInstance(awtImage,
					// printImage.getBackcolor());
					image = Image.getInstance(bi, null);

					break;
				}
				case FILL_FRAME: {
					if (printImage.isUsingCache()
							&& loadedImagesMap.containsKey(renderer)) {
						image = loadedImagesMap.get(renderer);
					} else {
						try {
							image = Image.getInstance(renderer
									.getImageData(rxReportsContext));
							if (!pdfformat.equalsIgnoreCase("PDFXNONE")) {
								image.setSmask(false);
							}
							imageTesterPdfContentByte.addImage(image, 10, 0, 0,
									10, 0, 0);
						} catch (Exception e) {
							RXImageRenderer tmpRenderer = RXImageRenderer
									.getOnErrorRendererForImage(
											rxReportsContext,
											RXImageRenderer.getInstance(renderer
													.getImageData(rxReportsContext)),
											printImage.getOnErrorTypeValue());
							if (tmpRenderer == null) {
								break;
							}
							java.awt.Image awtImage = tmpRenderer
									.getImage(rxReportsContext);
							image = Image.getInstance(
									awtImage, null);
						}

						if (printImage.isUsingCache()) {
							loadedImagesMap.put(renderer, image);
						}
					}

					image.scaleAbsolute(availableImageWidth,
							availableImageHeight);
					break;
				}
				/*
				 * 보안 코드 입력부분으로 고밀도 바코드 이미지로 대체한다. 보안 코드 이미지는 항상 모양 유지로 삽입한다.
				 */
				case HD_BARCODE: {
					// 보안 코드 이미지는 캐시된 데이터를 사용하지 않는다.
					isHDBarcode = true;
					java.awt.Image tmpimg = RXImageRenderer.getInstance(
							renderer.getImageData()).getImage();
					try {
						// java.awt.Image를 BufferedImage로 변환
						BufferedImage bi = new BufferedImage(
								tmpimg.getWidth(null), tmpimg.getHeight(null),
								BufferedImage.TYPE_INT_ARGB);
						Graphics g = bi.getGraphics();
						g.drawImage(tmpimg, 0, 0, null);
						g.dispose();

						// BufferedImage에서 Steganography 데이터를 추출하여
						// MessageBuilder로 반환
						MessageBuilder builder = sec.decodeBarcode(bi);

						// 파일로부터 이미지를 읽어 기존 이미지를 대체한다.
						barcodefs = builder.getPath() + builder.getImgFile();
						image = Image.getInstance(barcodefs);
						if (builder.getMessage()
								.equalsIgnoreCase("deleteafter")) {
							deleteAfter = true;
						} else {
							deleteAfter = false;
						}

						if (!pdfformat.equalsIgnoreCase("PDFXNONE")) {
							image.setSmask(false);
						}
						imageTesterPdfContentByte.addImage(image, 10, 0, 0, 10,
								0, 0);
					} catch (Exception e) {
						isHDBarcode = false;
						RXImageRenderer tmpRenderer = RXImageRenderer
								.getOnErrorRendererForImage(RXImageRenderer
										.getInstance(renderer.getImageData()),
										printImage.getOnErrorTypeValue());
						if (tmpRenderer == null) {
							break;
						}
						java.awt.Image awtImage = tmpRenderer.getImage();
						image = Image.getInstance(awtImage,
								null);
					}

					image.scaleToFit(availableImageWidth, availableImageHeight);

					xoffset = (int) (xalignFactor * (availableImageWidth - image
							.getPlainWidth()));
					yoffset = (int) (yalignFactor * (availableImageHeight - image
							.getPlainHeight()));

					xoffset = (xoffset < 0 ? 0 : xoffset);
					yoffset = (yoffset < 0 ? 0 : yoffset);

					break;
				}
				/*
				 * Html 입력부분으로 설정된 이미지로 대체한다. 이미지는 항상 모양 유지로 삽입한다.
				 */
				case HTML_INPUT:
					/*
					 * { //이미지는 캐시된 데이터를 사용하지 않는다. java.awt.Image tmpimg =
					 * RXImageRenderer
					 * .getInstance(renderer.getImageData()).getImage(); try{ //
					 * java.awt.Image를 BufferedImage로 변환 BufferedImage bi = new
					 * BufferedImage( tmpimg.getWidth(null),
					 * tmpimg.getHeight(null), BufferedImage.TYPE_INT_ARGB );
					 * Graphics g = bi.getGraphics(); g.drawImage(tmpimg, 0, 0,
					 * null); g.dispose();
					 * 
					 * //BufferedImage에서 Steganography 데이터를 추출하여
					 * AttributeBuilder로 반환 AttributeBuilder builder =
					 * sec.decodeHtmlInput(bi);
					 * 
					 * //파일로부터 이미지를 읽어 기존 이미지를 대체한다. if(HtmlInputInfo==null){
					 * imgPath = builder.getImageFileName(); }else
					 * if(HtmlInputInfo.containsKey(builder.getTagID())){
					 * imgPath = (String)HtmlInputInfo.get(builder.getTagID());
					 * } image = com.cabsoft.text.Image.getInstance(imgPath);
					 * 
					 * if (!pdfformat.equalsIgnoreCase("PDFXNONE")){
					 * image.setSmask(false); }
					 * imageTesterPdfContentByte.addImage(image, 10, 0, 0, 10,
					 * 0, 0); } catch(Exception e) { isHDBarcode = false;
					 * RXImageRenderer tmpRenderer =
					 * RXImageRenderer.getOnErrorRendererForImage(
					 * RXImageRenderer.getInstance(renderer.getImageData()),
					 * printImage.getOnErrorTypeValue() ); if (tmpRenderer ==
					 * null) { break; } java.awt.Image awtImage =
					 * tmpRenderer.getImage(); image =
					 * com.cabsoft.text.Image.getInstance(awtImage, null); }
					 * 
					 * image.scaleToFit(availableImageWidth,
					 * availableImageHeight);
					 * 
					 * xoffset = (int)(xalignFactor * (availableImageWidth -
					 * image.getPlainWidth())); yoffset = (int)(yalignFactor *
					 * (availableImageHeight - image.getPlainHeight()));
					 * 
					 * xoffset = (xoffset < 0 ? 0 : xoffset); yoffset = (yoffset
					 * < 0 ? 0 : yoffset);
					 * 
					 * break; }
					 */
				case RETAIN_SHAPE:
				default: {
					if (printImage.isUsingCache()
							&& loadedImagesMap.containsKey(renderer)) {
						image = loadedImagesMap.get(renderer);
					} else {
						try {
							image = Image.getInstance(renderer
									.getImageData(rxReportsContext));
							if (!pdfformat.equalsIgnoreCase("PDFXNONE")) {
								image.setSmask(false);
							}
							imageTesterPdfContentByte.addImage(image, 10, 0, 0,
									10, 0, 0);
						} catch (Exception e) {
							RXImageRenderer tmpRenderer = RXImageRenderer
									.getOnErrorRendererForImage(
											rxReportsContext,
											RXImageRenderer.getInstance(renderer
													.getImageData(rxReportsContext)),
											printImage.getOnErrorTypeValue());
							if (tmpRenderer == null) {
								break;
							}
							java.awt.Image awtImage = tmpRenderer
									.getImage(rxReportsContext);
							image = Image.getInstance(
									awtImage, null);
						}

						if (printImage.isUsingCache()) {
							loadedImagesMap.put(renderer, image);
						}
					}

					image.scaleToFit(availableImageWidth, availableImageHeight);

					xoffset = (int) (xalignFactor * (availableImageWidth - image
							.getPlainWidth()));
					yoffset = (int) (yalignFactor * (availableImageHeight - image
							.getPlainHeight()));

					xoffset = (xoffset < 0 ? 0 : xoffset);
					yoffset = (yoffset < 0 ? 0 : yoffset);

					break;
				}
				}

				if (image != null) {
					chunk = new Chunk(image, 0, 0);

					scaledWidth = image.getScaledWidth();
					scaledHeight = image.getScaledHeight();
				}
			} else {
				double normalWidth = availableImageWidth;
				double normalHeight = availableImageHeight;

				double displayWidth = availableImageWidth;
				double displayHeight = availableImageHeight;

				double ratioX = 1f;
				double ratioY = 1f;

				Rectangle2D clip = null;

				Dimension2D dimension = renderer.getDimension(rxReportsContext);
				if (dimension != null) {
					normalWidth = dimension.getWidth();
					normalHeight = dimension.getHeight();
					displayWidth = normalWidth;
					displayHeight = normalHeight;

					float xalignFactor = getXAlignFactor(printImage);
					float yalignFactor = getYAlignFactor(printImage);

					switch (printImage.getScaleImageValue()) {
					case CLIP: {
						xoffset = (int) (xalignFactor * (availableImageWidth - normalWidth));
						yoffset = (int) (yalignFactor * (availableImageHeight - normalHeight));
						clip = new Rectangle2D.Double(-xoffset, -yoffset,
								availableImageWidth, availableImageHeight);
						break;
					}
					case FILL_FRAME: {
						ratioX = availableImageWidth / normalWidth;
						ratioY = availableImageHeight / normalHeight;
						normalWidth *= ratioX;
						normalHeight *= ratioY;
						xoffset = 0;
						yoffset = 0;
						break;
					}
					case RETAIN_SHAPE:
					default: {
						ratioX = availableImageWidth / normalWidth;
						ratioY = availableImageHeight / normalHeight;
						ratioX = ratioX < ratioY ? ratioX : ratioY;
						ratioY = ratioX;
						normalWidth *= ratioX;
						normalHeight *= ratioY;
						xoffset = (int) (xalignFactor * (availableImageWidth - normalWidth));
						yoffset = (int) (yalignFactor * (availableImageHeight - normalHeight));
						break;
					}
					}
				}

				PdfTemplate template = pdfContentByte.createTemplate(
						(float) displayWidth, (float) displayHeight);

				Graphics2D g = forceSvgShapes ? template.createGraphicsShapes(
						(float) displayWidth, (float) displayHeight) : template
						.createGraphics(availableImageWidth,
								availableImageHeight, new LocalFontMapper());

				if (clip != null) {
					g.setClip(clip);
				}

				if (printImage.getModeValue() == ModeEnum.OPAQUE
						|| !pdfformat.equalsIgnoreCase("PDFXNONE")) {
					g.setColor(printImage.getBackcolor());
					g.fillRect(0, 0, (int) displayWidth, (int) displayHeight);
				}

				if (printImage.getModeValue() == ModeEnum.OPAQUE) {
					g.setColor(printImage.getBackcolor());
					g.fillRect(0, 0, (int) displayWidth, (int) displayHeight);
				}

				Rectangle2D rectangle = new Rectangle2D.Double(0, 0,
						displayWidth, displayHeight);

				renderer.render(rxReportsContext, g, rectangle);
				g.dispose();

				pdfContentByte.saveState();
				pdfContentByte.addTemplate(template, (float) ratioX, 0f, 0f,
						(float) ratioY, printImage.getX() + getOffsetX()
								+ xoffset, reportexpressPrint.getPageHeight()
								- printImage.getY() - getOffsetY()
								- (int) normalHeight - yoffset);
				pdfContentByte.restoreState();

				Image image = getPxImage();
				image.scaleAbsolute(availableImageWidth, availableImageHeight);
				chunk = new Chunk(image, 0, 0);
			}

			/*
			 * image.setAbsolutePosition( printImage.getX() + offsetX +
			 * borderOffset, reportexpressPrint.getPageHeight() -
			 * printImage.getY() - offsetY - image.scaledHeight() - borderOffset
			 * );
			 * 
			 * pdfContentByte.addImage(image);
			 */

			if (chunk != null) {
				setAnchor(chunk, printImage, printImage);
				setHyperlinkInfo(chunk, printImage);

				tagHelper.startImage(printImage);

				ColumnText colText = new ColumnText(pdfContentByte);
				int upperY = reportexpressPrint.getPageHeight()
						- printImage.getY() - topPadding - getOffsetY()
						- yoffset;
				int lowerX = printImage.getX() + leftPadding + getOffsetX()
						+ xoffset;
				colText.setSimpleColumn(new Phrase(chunk), lowerX, upperY
						- scaledHeight, lowerX + scaledWidth, upperY,
						scaledHeight, Element.ALIGN_LEFT);

				colText.go();

				tagHelper.endImage();
			}
		}

		if (printImage.getLineBox().getTopPen().getLineWidth().floatValue() <= 0f
				&& printImage.getLineBox().getLeftPen().getLineWidth()
						.floatValue() <= 0f
				&& printImage.getLineBox().getBottomPen().getLineWidth()
						.floatValue() <= 0f
				&& printImage.getLineBox().getRightPen().getLineWidth()
						.floatValue() <= 0f) {
			if (printImage.getLinePen().getLineWidth().floatValue() > 0f) {
				exportPen(printImage.getLinePen(), printImage);
			}
		} else {
			/*   */
			exportBox(printImage.getLineBox(), printImage);
		}
	}

	private float getXAlignFactor(RXPrintImage printImage) {
		float xalignFactor = 0f;
		switch (printImage.getHorizontalAlignmentValue()) {
		case RIGHT: {
			xalignFactor = 1f;
			break;
		}
		case CENTER: {
			xalignFactor = 0.5f;
			break;
		}
		case LEFT:
		default: {
			xalignFactor = 0f;
			break;
		}
		}
		return xalignFactor;
	}

	private float getYAlignFactor(RXPrintImage printImage) {
		float yalignFactor = 0f;
		switch (printImage.getVerticalAlignmentValue()) {
		case BOTTOM: {
			yalignFactor = 1f;
			break;
		}
		case MIDDLE: {
			yalignFactor = 0.5f;
			break;
		}
		case TOP:
		default: {
			yalignFactor = 0f;
			break;
		}
		}
		return yalignFactor;
	}

	/**
	 *
	 */
	protected void setHyperlinkInfo(Chunk chunk, RXPrintHyperlink link) {
		if (link != null) {
			switch (link.getHyperlinkTypeValue()) {
			case REFERENCE: {
				if (link.getHyperlinkReference() != null) {
					switch (link.getHyperlinkTargetValue()) {
					case BLANK: {
						chunk.setAction(PdfAction.javaScript(
								"if (app.viewerVersion < 7)"
										+ "{this.getURL(\""
										+ link.getHyperlinkReference()
										+ "\");}" + "else {app.launchURL(\""
										+ link.getHyperlinkReference()
										+ "\", true);};", pdfWriter));
						break;
					}
					case SELF:
					default: {
						chunk.setAnchor(link.getHyperlinkReference());
						break;
					}
					}
				}
				break;
			}
			case LOCAL_ANCHOR: {
				if (link.getHyperlinkAnchor() != null) {
					chunk.setLocalGoto(link.getHyperlinkAnchor());
				}
				break;
			}
			case LOCAL_PAGE: {
				if (link.getHyperlinkPage() != null) {
					chunk.setLocalGoto(RX_PAGE_ANCHOR_PREFIX + reportIndex
							+ "_" + link.getHyperlinkPage().toString());
				}
				break;
			}
			case REMOTE_ANCHOR: {
				if (link.getHyperlinkReference() != null
						&& link.getHyperlinkAnchor() != null) {
					chunk.setRemoteGoto(link.getHyperlinkReference(),
							link.getHyperlinkAnchor());
				}
				break;
			}
			case REMOTE_PAGE: {
				if (link.getHyperlinkReference() != null
						&& link.getHyperlinkPage() != null) {
					chunk.setRemoteGoto(link.getHyperlinkReference(), link
							.getHyperlinkPage().intValue());
				}
				break;
			}
			case CUSTOM: {
				if (hyperlinkProducerFactory != null) {
					String hyperlink = hyperlinkProducerFactory
							.produceHyperlink(link);
					if (hyperlink != null) {
						switch (link.getHyperlinkTargetValue()) {
						case BLANK: {
							chunk.setAction(PdfAction.javaScript(
									"if (app.viewerVersion < 7)"
											+ "{this.getURL(\"" + hyperlink
											+ "\");}"
											+ "else {app.launchURL(\""
											+ hyperlink + "\", true);};",
									pdfWriter));
							break;
						}
						case SELF:
						default: {
							chunk.setAnchor(hyperlink);
							break;
						}
						}
					}
				}
			}
			case NONE:
			default: {
				break;
			}
			}
		}
	}

	/**
	 *
	 */
	protected Phrase getPhrase(AttributedString as, String text,
			RXPrintText textElement) {
		Phrase phrase = new Phrase();
		int runLimit = 0;

		AttributedCharacterIterator iterator = as.getIterator();
		Locale locale = getTextLocale(textElement);

		boolean firstChunk = true;
		while (runLimit < text.length()
				&& (runLimit = iterator.getRunLimit()) <= text.length()) {
			Map<Attribute, Object> attributes = iterator.getAttributes();
			Chunk chunk = getChunk(attributes,
					text.substring(iterator.getIndex(), runLimit), locale);

			if (firstChunk) {
				// only set anchor + bookmark for the first chunk in the text
				setAnchor(chunk, textElement, textElement);
			}

			RXPrintHyperlink hyperlink = textElement;
			if (hyperlink.getHyperlinkTypeValue() == HyperlinkTypeEnum.NONE) {
				hyperlink = (RXPrintHyperlink) attributes
						.get(RXTextAttribute.HYPERLINK);
			}

			setHyperlinkInfo(chunk, hyperlink);
			phrase.add(chunk);

			iterator.setIndex(runLimit);
			firstChunk = false;
		}

		return phrase;
	}

	/**
	 *
	 */
	protected Chunk getChunk(Map<Attribute, Object> attributes, String text,
			Locale locale) {
		// underline and strikethrough are set on the chunk below
		Font font = getFont(attributes, locale, false);

		Chunk chunk = new Chunk(text, font);

		if (hasUnderline(attributes)) {
			// using the same values as sun.font.Fond2D
			chunk.setUnderline(null, 0, 1f / 18, 0, -1f / 12, 0);
		}

		if (hasStrikethrough(attributes)) {
			// using the same thickness as sun.font.Fond2D.
			// the position is calculated in Fond2D based on the ascent,
			// defaulting
			// to iText default position which depends on the font size
			chunk.setUnderline(null, 0, 1f / 18, 0, 1f / 3, 0);
		}

		Color backcolor = (Color) attributes.get(TextAttribute.BACKGROUND);
		if (backcolor != null) {
			chunk.setBackground(new BaseColor(backcolor.getRed(), backcolor
					.getGreen(), backcolor.getBlue(), backcolor.getAlpha()));
		}

		Object script = attributes.get(TextAttribute.SUPERSCRIPT);
		if (script != null) {
			if (TextAttribute.SUPERSCRIPT_SUPER.equals(script)) {
				chunk.setTextRise(font.getCalculatedLeading(1f) / 2);
			} else if (TextAttribute.SUPERSCRIPT_SUB.equals(script)) {
				chunk.setTextRise(-font.getCalculatedLeading(1f) / 2);
			}
		}

		if (splitCharacter != null) {
			// TODO use line break offsets if available?
			chunk.setSplitCharacter(splitCharacter);
		}

		return chunk;
	}

	protected boolean hasUnderline(Map<Attribute, Object> textAttributes) {
		Integer underline = (Integer) textAttributes
				.get(TextAttribute.UNDERLINE);
		return TextAttribute.UNDERLINE_ON.equals(underline);
	}

	protected boolean hasStrikethrough(Map<Attribute, Object> textAttributes) {
		Boolean strike = (Boolean) textAttributes
				.get(TextAttribute.STRIKETHROUGH);
		return TextAttribute.STRIKETHROUGH_ON.equals(strike);
	}

	/**
	 * Creates a PDF font.
	 * 
	 * @param attributes
	 *            the text attributes of the font
	 * @param locale
	 *            the locale for which to create the font
	 * @param setFontLines
	 *            whether to set underline and strikethrough as font style
	 * @return the PDF font for the specified attributes
	 */

	protected Font getFont(Map<Attribute, Object> attributes, Locale locale,
			boolean setFontLines) {
		RXFont rxFont = new RXBaseFont(attributes);

		Exception initialException = null;

		Color forecolor = (Color) attributes.get(TextAttribute.FOREGROUND);

		// use the same font scale ratio as in
		// RXStyledText.getAwtAttributedString
		float fontSizeScale = 1f;
		Integer scriptStyle = (Integer) attributes
				.get(TextAttribute.SUPERSCRIPT);
		if (scriptStyle != null
				&& (TextAttribute.SUPERSCRIPT_SUB.equals(scriptStyle) || TextAttribute.SUPERSCRIPT_SUPER
						.equals(scriptStyle))) {
			fontSizeScale = 2f / 3;
		}

		Font font = null;
		PdfFont pdfFont = null;
		FontKey key = new FontKey(rxFont.getFontName(), rxFont.isBold(),
				rxFont.isItalic());

		if (fontMap != null && fontMap.containsKey(key)) {
			pdfFont = pdfFontMap.get(key);
		} else {
			FontInfo fontInfo = FontUtil.getInstance(rxReportsContext)
					.getFontInfo(rxFont.getFontName(), locale);
			if (fontInfo == null) {
				// fontName NOT found in font extensions
				pdfFont = new PdfFont(rxFont.getPdfFontName(),
						rxFont.getPdfEncoding(), rxFont.isPdfEmbedded());
			} else {
				// fontName found in font extensions
				FontFamily family = fontInfo.getFontFamily();
				FontFace face = fontInfo.getFontFace();
				int faceStyle = java.awt.Font.PLAIN;

				if (face == null) {
					// fontName matches family name in font extension
					if (rxFont.isBold() && rxFont.isItalic()) {
						face = family.getBoldItalicFace();
						faceStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
					}

					if (face == null && rxFont.isBold()) {
						face = family.getBoldFace();
						faceStyle = java.awt.Font.BOLD;
					}

					if (face == null && rxFont.isItalic()) {
						face = family.getItalicFace();
						faceStyle = java.awt.Font.ITALIC;
					}

					if (face == null) {
						face = family.getNormalFace();
						faceStyle = java.awt.Font.PLAIN;
					}

					// if (face == null)
					// {
					// throw new RXRuntimeException("Font family '" +
					// family.getName() +
					// "' does not have the normal font face.");
					// }
				} else {
					// fontName matches face name in font extension; not family
					// name
					faceStyle = fontInfo.getStyle();
				}

				String pdfFontName = null;
				int pdfFontStyle = java.awt.Font.PLAIN;
				if (rxFont.isBold() && rxFont.isItalic()) {
					pdfFontName = family.getBoldItalicPdfFont();
					pdfFontStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
				}

				if (pdfFontName == null && rxFont.isBold()) {
					pdfFontName = family.getBoldPdfFont();
					pdfFontStyle = java.awt.Font.BOLD;
				}

				if (pdfFontName == null && rxFont.isItalic()) {
					pdfFontName = family.getItalicPdfFont();
					pdfFontStyle = java.awt.Font.ITALIC;
				}

				if (pdfFontName == null) {
					pdfFontName = family.getNormalPdfFont();
					pdfFontStyle = java.awt.Font.PLAIN;
				}

				if (pdfFontName == null) {
					// in theory, face file cannot be null here
					pdfFontName = (face == null || face.getFile() == null ? rxFont
							.getPdfFontName() : face.getFile());
					pdfFontStyle = faceStyle;// FIXMEFONT not sure this is
												// correct, in case we inherit
												// pdfFontName from default
												// properties
				}

				// String ttf = face.getFile();
				// if (ttf == null)
				// {
				// throw new RXRuntimeException("The '" + face.getName() +
				// "' font face in family '" + family.getName() +
				// "' returns a null file.");
				// }

				pdfFont = new PdfFont(pdfFontName,
						family.getPdfEncoding() == null ? rxFont
								.getPdfEncoding() : family.getPdfEncoding(),
						family.isPdfEmbedded() == null ? rxFont.isPdfEmbedded()
								: family.isPdfEmbedded().booleanValue(),
						rxFont.isBold()
								&& ((pdfFontStyle & java.awt.Font.BOLD) == 0),
						rxFont.isItalic()
								&& ((pdfFontStyle & java.awt.Font.ITALIC) == 0));
			}
		}

		int pdfFontStyle = (pdfFont.isPdfSimulatedBold() ? Font.BOLD : 0)
				| (pdfFont.isPdfSimulatedItalic() ? Font.ITALIC : 0);
		if (setFontLines) {
			pdfFontStyle |= (rxFont.isUnderline() ? Font.UNDERLINE : 0)
					| (rxFont.isStrikeThrough() ? Font.STRIKETHRU : 0);
		}

		try {
			if (forecolor != null) {
				font = FontFactory.getFont(pdfFont.getPdfFontName(), pdfFont
						.getPdfEncoding(), pdfFont.isPdfEmbedded(),
						rxFont.getFontSize() * fontSizeScale, pdfFontStyle,
						new BaseColor(forecolor.getRed(), forecolor.getGreen(),
								forecolor.getBlue(), forecolor.getAlpha()));
			} else {
				font = FontFactory.getFont(pdfFont.getPdfFontName(),
						pdfFont.getPdfEncoding(), pdfFont.isPdfEmbedded(),
						rxFont.getFontSize() * fontSizeScale, pdfFontStyle);
			}

			// check if FontFactory didn't find the font
			if (font.getBaseFont() == null
					&& font.getFamily().equals(Font.UNDEFINED)) {
				font = null;
			}
		} catch (Exception e) {
			initialException = e;
		}

		if (font == null) {
			byte[] bytes = null;

			try {
				bytes = RepositoryUtil.getInstance(rxReportsContext)
						.getBytesFromLocation(pdfFont.getPdfFontName());
			} catch (RXException e) {
				throw // NOPMD
				new RXRuntimeException("Could not load the following font : "
						+ "\npdfFontName   : " + pdfFont.getPdfFontName()
						+ "\npdfEncoding   : " + pdfFont.getPdfEncoding()
						+ "\nisPdfEmbedded : " + pdfFont.isPdfEmbedded(),
						initialException);
			}

			BaseFont baseFont = null;

			try {
				baseFont = BaseFont.createFont(pdfFont.getPdfFontName(),
						pdfFont.getPdfEncoding(), pdfFont.isPdfEmbedded(),
						true, bytes, null);
			} catch (DocumentException e) {
				throw new RXRuntimeException(e);
			} catch (IOException e) {
				throw new RXRuntimeException(e);
			}

			if (forecolor != null) {
				font = new Font(baseFont, rxFont.getFontSize() * fontSizeScale,
						pdfFontStyle, new BaseColor(forecolor.getRed(),
								forecolor.getGreen(), forecolor.getBlue(),
								forecolor.getAlpha()));
			} else {
				font = new Font(baseFont, rxFont.getFontSize() * fontSizeScale,
						pdfFontStyle);
			}
		}

		return font;
	}
	
	/**
	 * @throws IOException 
	 *
	 */
	private void defaultExportText(RXPrintText text) throws DocumentException
	{
		String s = text.getFullText();
		if(s==null || "null".equals(s)){
			text.setText("");
		}
		text.setMode(ModeEnum.TRANSPARENT);
		PdfFormAbstractPdfTextRenderer textRenderer = text.getLeadingOffset() == 0 ? new PdfFormPdfTextRenderer(
				rxReportsContext, getPropertiesUtil().getBooleanProperty(
						RXStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT))
				: new EFormSimplePdfTextRenderer(rxReportsContext,
						getPropertiesUtil().getBooleanProperty(
								RXStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT)// FIXMECONTEXT
																				// replace
																				// with
																				// getPropertiesUtil
																				// in
																				// all
																				// exporters
				);// FIXMETAB optimize this

		textRenderer.initialize(this, pdfContentByte, text, getOffsetX(),
				getOffsetY());

		RXStyledText styledText = textRenderer.getStyledText();

		if (styledText == null) {
			return;
		}

		double angle = 0;

		switch (text.getRotationValue()) {
		case LEFT: {
			angle = Math.PI / 2;
			break;
		}
		case RIGHT: {
			angle = -Math.PI / 2;
			break;
		}
		case UPSIDE_DOWN: {
			angle = Math.PI;
			break;
		}
		case NONE:
		default: {
		}
		}

		AffineTransform atrans = new AffineTransform();
		atrans.rotate(angle, textRenderer.getX(),
				reportexpressPrint.getPageHeight() - textRenderer.getY());
		pdfContentByte.transform(atrans);

		if (text.getModeValue() == ModeEnum.OPAQUE
				|| !pdfformat.equalsIgnoreCase("PDFXNONE")) {
			Color backcolor = text.getBackcolor();
			pdfContentByte.setRGBColorFill(backcolor.getRed(),
					backcolor.getGreen(), backcolor.getBlue());
			pdfContentByte.rectangle(textRenderer.getX(),
					reportexpressPrint.getPageHeight() - textRenderer.getY(),
					textRenderer.getWidth(), -textRenderer.getHeight());
			pdfContentByte.fill();
		}

		if (styledText.length() > 0) {
			tagHelper.startText();

			/*   */
			textRenderer.render();

			tagHelper.endText();
		}

		atrans = new AffineTransform();
		atrans.rotate(-angle, textRenderer.getX(),
				reportexpressPrint.getPageHeight() - textRenderer.getY());
		pdfContentByte.transform(atrans);

		/*   */
		exportBox(text.getLineBox(), text);
	}

	/**
	 * @throws IOException 
	 *
	 */
	public void exportText(RXPrintText text) throws DocumentException
	{
		String id = RXProperties.getProperty(text, PROPERTY_EFORM_ID);
		id = id==null ? "": id.trim();
		
		String name = RXProperties.getProperty(text, PROPERTY_EFORM_NAME);
		
		String sign = RXProperties.getProperty(text, PROPERTY_EFORM_SIGN);
		sign = sign==null ? "": sign.trim();
		
		String tag_type = RXProperties.getProperty(text, PROPERTY_EFORM_TYPE);
		tag_type = tag_type==null ? "": tag_type.trim();
		
		String tagValues = RXProperties.getProperty(text, PROPERTY_EFORM_TAGVALUE);
		String radioValue = RXProperties.getProperty(text, PROPERTY_EFORM_RADIOVALUE);
		String remove = RXProperties.getProperty(text, PROPERTY_EFORM_REMOVE);
		remove = remove==null ? "0" : "1";
		
		int padding = RXProperties.getIntegerProperty(text, PROPERTY_EFORM_TEXTFIELD_PADDING, 1);
		
		PdfFormAbstractPdfTextRenderer textRenderer = 
				text.getLeadingOffset() == 0 
				? new PdfFormPdfTextRenderer(
					rxReportsContext,
					getPropertiesUtil().getBooleanProperty(RXStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT)
					) 
				: new EFormSimplePdfTextRenderer(
					rxReportsContext,
					getPropertiesUtil().getBooleanProperty(RXStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT)//FIXMECONTEXT replace with getPropertiesUtil in all exporters
					);//FIXMETAB optimize this
			
		textRenderer.initialize(this, pdfContentByte, text, getOffsetX(), getOffsetY());
			
		RXStyledText styledText = textRenderer.getStyledText();

		if(id.equalsIgnoreCase("")){
			/**
			 * 자필 서명
			 */
			if(!"".equals(sign)){
				try{
					int x1 = text.getX();
					int y1 = text.getY();
					int x2 = text.getWidth();
					int y2 = text.getHeight();
					SignPosition pos = new SignPosition(sign, x1, y1, x2, y2);
					signPages.addSignPosition(pos);
				}catch(Exception e){
					throw new DocumentException(e);
				}
				
			}else if (styledText == null)
			{
				return;
			}
	
			double angle = 0;
	
			switch (text.getRotationValue())
			{
				case LEFT :
				{
					angle = Math.PI / 2;
					break;
				}
				case RIGHT :
				{
					angle = - Math.PI / 2;
					break;
				}
				case UPSIDE_DOWN :
				{
					angle = Math.PI;
					break;
				}
				case NONE :
				default :
				{
				}
			}
	
			AffineTransform atrans = new AffineTransform();
			atrans.rotate(angle, textRenderer.getX(), reportexpressPrint.getPageHeight() - textRenderer.getY());
			pdfContentByte.transform(atrans);
	
			if (text.getModeValue() == ModeEnum.OPAQUE || !pdfformat.equalsIgnoreCase("PDFXNONE"))
			{
				Color backcolor = text.getBackcolor();
				pdfContentByte.setRGBColorFill(
					backcolor.getRed(),
					backcolor.getGreen(),
					backcolor.getBlue()
					);
				pdfContentByte.rectangle(
					textRenderer.getX(),
					reportexpressPrint.getPageHeight() - textRenderer.getY(),
					textRenderer.getWidth(),
					- textRenderer.getHeight()
					);
				pdfContentByte.fill();
			}
	
			if (styledText.length() > 0)
			{
				tagHelper.startText();
				
				/*   */
				textRenderer.render();
	
				tagHelper.endText();
			}
	
			atrans = new AffineTransform();
			atrans.rotate(-angle, textRenderer.getX(), reportexpressPrint.getPageHeight() - textRenderer.getY());
			pdfContentByte.transform(atrans);

			/*   */
			exportBox(
				text.getLineBox(),
				text
				);
			
		/**
		 * com.cabsoft.rx.export.html.remove 속성이 1이 아닌 경우만 PDF로 내보낸다.
		 * 추후에 수정될 수 있음.
		 */
		}else if(!"1".equals(remove)){
			
			if(tagValues!=null){
				String[] vv = StringUtils.split(tagValues, "|");
				String tagVal = "0";
				String type = vv[0];
				type = (tag_type!=null && !"".equals(tag_type)) ? tag_type : type;
				if(type.equalsIgnoreCase("checkbox")){
					if(vv.length==2){
						tagVal = vv[1];
					}else{
						tagVal = vv[0];
					}
					PdfFormBuilder.addCheckbox(rxReportsContext, pdfWriter, 
							reportexpressPrint.getPageWidth(), reportexpressPrint.getPageHeight(), id, tagVal, text,
						frameLeft, frameTop);
				}else if(type.equalsIgnoreCase("select")){
					try{
						defaultExportText(text);
						
						SelectBox select = new SelectBox(tagValues, "//options/option");
						int defIndex = select.getSelectedIndex();
						List<String> list = select.getOptions();
						String[] options = new String[list.size()];
						for(int i=0; i<list.size(); i++){
							options[i] = list.get(i);
						}
						defIndex = defIndex>0 ? defIndex-1 : defIndex;
						PdfFormBuilder.addCombo(rxReportsContext, pdfWriter, 
								reportexpressPrint.getPageWidth(), reportexpressPrint.getPageHeight(), id, text, options, defIndex,
								frameLeft, frameTop);
					}catch(Exception e){
						log.error(e.toString());
					}
				}else if(type.equalsIgnoreCase("radio")){
					String[] v = StringUtils.split(radioValue, "|");
					boolean checked = v[0].equalsIgnoreCase("1") ? true : false;
					String onValue = id;
					if(v.length==2){
						onValue = v[1];
					}
					String radioid = name==null ? id : name;
					RadioButtons buttons = radiobuttons.getRadioButtons(radioid);
					buttons.addRadioButton(text, onValue, checked, frameLeft, frameTop);
					radiobuttons.addRadioButtons(radioid, buttons);
				}
			}else if(radioValue!=null){
				String[] v = StringUtils.split(radioValue, "|");
				boolean checked = v[0].equalsIgnoreCase("1") ? true : false;
				String onValue = id;
				if(v.length==2){
					onValue = v[1];
				}
				String radioid = name==null ? id : name;
				RadioButtons buttons = radiobuttons.getRadioButtons(radioid);
				buttons.addRadioButton(text, onValue, checked, frameLeft, frameTop);
				radiobuttons.addRadioButtons(radioid, buttons);
			}else{
				if(!"textconfirm".equals(tag_type)) defaultExportText(text);
				if(!"readconfirm".equals(tag_type)){
					PdfFormBuilder.addTextField(rxReportsContext, pdfWriter, 
							reportexpressPrint.getPageWidth(), reportexpressPrint.getPageHeight(), id, text, padding, frameLeft, frameTop);
				}
			}
			
			exportBox(
					text.getLineBox(),
					text
			);
		}
	}
	
	/**
	 *
	 */
	protected void exportBox(RXLineBox box, RXPrintElement element) {
		exportTopPen(box.getTopPen(), box.getLeftPen(), box.getRightPen(),
				element);
		exportLeftPen(box.getTopPen(), box.getLeftPen(), box.getBottomPen(),
				element);
		exportBottomPen(box.getLeftPen(), box.getBottomPen(),
				box.getRightPen(), element);
		exportRightPen(box.getTopPen(), box.getBottomPen(), box.getRightPen(),
				element);

		pdfContentByte.setLineDash(0f);
		pdfContentByte.setLineCap(PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
	}

	/**
	 *
	 */
	protected void exportPen(RXPen pen, RXPrintElement element) {
		exportTopPen(pen, pen, pen, element);
		exportLeftPen(pen, pen, pen, element);
		exportBottomPen(pen, pen, pen, element);
		exportRightPen(pen, pen, pen, element);

		pdfContentByte.setLineDash(0f);
		pdfContentByte.setLineCap(PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
	}

	/**
	 *
	 */
	protected void exportTopPen(RXPen topPen, RXPen leftPen, RXPen rightPen,
			RXPrintElement element) {
		if (topPen.getLineWidth().floatValue() > 0f) {
			float leftOffset = leftPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(leftPen);
			float rightOffset = rightPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(rightPen);

			preparePen(pdfContentByte, topPen, PdfContentByte.LINE_CAP_BUTT);

			if (topPen.getLineStyleValue() == LineStyleEnum.DOUBLE) {
				float topOffset = topPen.getLineWidth().floatValue();

				pdfContentByte.moveTo(element.getX() + getOffsetX()
						- leftOffset, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() + topOffset / 3);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								+ rightOffset,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() + topOffset / 3);
				pdfContentByte.stroke();

				pdfContentByte.moveTo(element.getX() + getOffsetX()
						+ leftOffset / 3, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - topOffset / 3);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								- rightOffset / 3,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - topOffset / 3);
				pdfContentByte.stroke();
			} else {
				float topOffset = BorderOffset.getOffset(topPen);
				pdfContentByte.moveTo(element.getX() + getOffsetX()
						- leftOffset, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - topOffset);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								+ rightOffset,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - topOffset);
				pdfContentByte.stroke();
			}
		}
	}

	/**
	 *
	 */
	protected void exportLeftPen(RXPen topPen, RXPen leftPen, RXPen bottomPen,
			RXPrintElement element) {
		if (leftPen.getLineWidth().floatValue() > 0f) {
			float topOffset = topPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(topPen);
			float bottomOffset = bottomPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(bottomPen);

			preparePen(pdfContentByte, leftPen, PdfContentByte.LINE_CAP_BUTT);

			if (leftPen.getLineStyleValue() == LineStyleEnum.DOUBLE) {
				float leftOffset = leftPen.getLineWidth().floatValue();

				pdfContentByte.moveTo(element.getX() + getOffsetX()
						- leftOffset / 3, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() + topOffset);
				pdfContentByte.lineTo(element.getX() + getOffsetX()
						- leftOffset / 3, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - element.getHeight()
						- bottomOffset);
				pdfContentByte.stroke();

				pdfContentByte.moveTo(element.getX() + getOffsetX()
						+ leftOffset / 3, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - topOffset / 3);
				pdfContentByte.lineTo(element.getX() + getOffsetX()
						+ leftOffset / 3, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - element.getHeight()
						+ bottomOffset / 3);
				pdfContentByte.stroke();
			} else {
				float leftOffset = BorderOffset.getOffset(leftPen);
				pdfContentByte.moveTo(element.getX() + getOffsetX()
						+ leftOffset, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() + topOffset);
				pdfContentByte.lineTo(element.getX() + getOffsetX()
						+ leftOffset, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - element.getHeight()
						- bottomOffset);
				pdfContentByte.stroke();
			}
		}
	}

	/**
	 *
	 */
	protected void exportBottomPen(RXPen leftPen, RXPen bottomPen,
			RXPen rightPen, RXPrintElement element) {
		if (bottomPen.getLineWidth().floatValue() > 0f) {
			float leftOffset = leftPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(leftPen);
			float rightOffset = rightPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(rightPen);

			preparePen(pdfContentByte, bottomPen, PdfContentByte.LINE_CAP_BUTT);

			if (bottomPen.getLineStyleValue() == LineStyleEnum.DOUBLE) {
				float bottomOffset = bottomPen.getLineWidth().floatValue();

				pdfContentByte.moveTo(element.getX() + getOffsetX()
						- leftOffset, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - element.getHeight()
						- bottomOffset / 3);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								+ rightOffset,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - element.getHeight()
								- bottomOffset / 3);
				pdfContentByte.stroke();

				pdfContentByte.moveTo(element.getX() + getOffsetX()
						+ leftOffset / 3, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - element.getHeight()
						+ bottomOffset / 3);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								- rightOffset / 3,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - element.getHeight()
								+ bottomOffset / 3);
				pdfContentByte.stroke();
			} else {
				float bottomOffset = BorderOffset.getOffset(bottomPen);
				pdfContentByte.moveTo(element.getX() + getOffsetX()
						- leftOffset, reportexpressPrint.getPageHeight()
						- element.getY() - getOffsetY() - element.getHeight()
						+ bottomOffset);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								+ rightOffset,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - element.getHeight()
								+ bottomOffset);
				pdfContentByte.stroke();
			}
		}
	}

	/**
	 *
	 */
	protected void exportRightPen(RXPen topPen, RXPen bottomPen,
			RXPen rightPen, RXPrintElement element) {
		if (rightPen.getLineWidth().floatValue() > 0f) {
			float topOffset = topPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(topPen);
			float bottomOffset = bottomPen.getLineWidth().floatValue() / 2
					- BorderOffset.getOffset(bottomPen);

			preparePen(pdfContentByte, rightPen, PdfContentByte.LINE_CAP_BUTT);

			if (rightPen.getLineStyleValue() == LineStyleEnum.DOUBLE) {
				float rightOffset = rightPen.getLineWidth().floatValue();

				pdfContentByte.moveTo(
						element.getX() + getOffsetX() + element.getWidth()
								+ rightOffset / 3,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() + topOffset);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								+ rightOffset / 3,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - element.getHeight()
								- bottomOffset);
				pdfContentByte.stroke();

				pdfContentByte.moveTo(
						element.getX() + getOffsetX() + element.getWidth()
								- rightOffset / 3,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - topOffset / 3);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								- rightOffset / 3,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - element.getHeight()
								+ bottomOffset / 3);
				pdfContentByte.stroke();
			} else {
				float rightOffset = BorderOffset.getOffset(rightPen);
				pdfContentByte.moveTo(
						element.getX() + getOffsetX() + element.getWidth()
								- rightOffset,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() + topOffset);
				pdfContentByte.lineTo(
						element.getX() + getOffsetX() + element.getWidth()
								- rightOffset,
						reportexpressPrint.getPageHeight() - element.getY()
								- getOffsetY() - element.getHeight()
								- bottomOffset);
				pdfContentByte.stroke();
			}
		}
	}

	/**
	 *
	 */
	private void preparePen(PdfContentByte pdfContentByte, RXPen pen,
			int lineCap) {
		float lineWidth = pen.getLineWidth().floatValue();

		if (lineWidth <= 0) {
			return;
		}

		pdfContentByte.setLineWidth(lineWidth);
		pdfContentByte.setLineCap(lineCap);

		Color color = pen.getLineColor();
		pdfContentByte.setRGBColorStroke(color.getRed(), color.getGreen(),
				color.getBlue());

		switch (pen.getLineStyleValue()) {
		case DOUBLE: {
			pdfContentByte.setLineWidth(lineWidth / 3);
			pdfContentByte.setLineDash(0f);
			break;
		}
		case DOTTED: {
			switch (lineCap) {
			case PdfContentByte.LINE_CAP_BUTT: {
				pdfContentByte.setLineDash(lineWidth, lineWidth, 0f);
				break;
			}
			case PdfContentByte.LINE_CAP_PROJECTING_SQUARE: {
				pdfContentByte.setLineDash(0, 2 * lineWidth, 0f);
				break;
			}
			}
			break;
		}
		case DASHED: {
			switch (lineCap) {
			case PdfContentByte.LINE_CAP_BUTT: {
				pdfContentByte.setLineDash(5 * lineWidth, 3 * lineWidth, 0f);
				break;
			}
			case PdfContentByte.LINE_CAP_PROJECTING_SQUARE: {
				pdfContentByte.setLineDash(4 * lineWidth, 4 * lineWidth, 0f);
				break;
			}
			}
			break;
		}
		case SOLID:
		default: {
			pdfContentByte.setLineDash(0f);
			break;
		}
		}
	}

	protected synchronized void registerFonts() {
		if (!fontsRegistered) {
			List<PropertySuffix> fontFiles = RXPropertiesUtil.getInstance(
					DefaultRXReportsContext.getInstance()).getProperties(
							EFORM_FONT_FILES_PREFIX);// FIXMECONTEXT no default here and
											// below
			if (!fontFiles.isEmpty()) {
				for (Iterator<PropertySuffix> i = fontFiles.iterator(); i
						.hasNext();) {
					PropertySuffix font = i.next();
					String file = font.getValue();
					if (file.toLowerCase().endsWith(".ttc")) {
						FontFactory.register(file);
					} else {
						String alias = font.getSuffix();
						FontFactory.register(file, alias);
					}
				}
			}

			List<PropertySuffix> fontDirs = RXPropertiesUtil.getInstance(
					DefaultRXReportsContext.getInstance()).getProperties(
							EFORM_FONT_DIRS_PREFIX);
			if (!fontDirs.isEmpty()) {
				for (Iterator<PropertySuffix> i = fontDirs.iterator(); i
						.hasNext();) {
					PropertySuffix dir = i.next();
					FontFactory.registerDirectory(dir.getValue());
				}
			}

			fontsRegistered = true;
		}
	}

	protected class Bookmark {
		final PdfOutline pdfOutline;
		final int level;

		Bookmark(Bookmark parent, int x, int top, String title) {
			this(parent, new PdfDestination(PdfDestination.XYZ, x, top, 0),
					title);
		}

		Bookmark(Bookmark parent, PdfDestination destination, String title) {
			this.pdfOutline = new PdfOutline(parent.pdfOutline, destination,
					title, false);
			this.level = parent.level + 1;
		}

		Bookmark(PdfOutline pdfOutline, int level) {
			this.pdfOutline = pdfOutline;
			this.level = level;
		}
	}

	protected class BookmarkStack {
		LinkedList<Bookmark> stack;

		BookmarkStack() {
			stack = new LinkedList<Bookmark>();
		}

		void push(Bookmark bookmark) {
			stack.add(bookmark);
		}

		Bookmark pop() {
			return stack.removeLast();
		}

		Bookmark peek() {
			return stack.getLast();
		}
	}

	protected void initBookmarks() {
		bookmarkStack = new BookmarkStack();

		int rootLevel = isModeBatch && isCreatingBatchModeBookmarks ? -1 : 0;
		Bookmark bookmark = new Bookmark(pdfContentByte.getRootOutline(),
				rootLevel);
		bookmarkStack.push(bookmark);
	}

	protected void addBookmark(int level, String title, int x, int y) {
		Bookmark parent = bookmarkStack.peek();
		// searching for parent
		while (parent.level >= level) {
			bookmarkStack.pop();
			parent = bookmarkStack.peek();
		}

		if (!collapseMissingBookmarkLevels) {
			// creating empty bookmarks in order to preserve the bookmark level
			for (int i = parent.level + 1; i < level; ++i) {
				Bookmark emptyBookmark = new Bookmark(parent,
						parent.pdfOutline.getPdfDestination(),
						EMPTY_BOOKMARK_TITLE);
				bookmarkStack.push(emptyBookmark);
				parent = emptyBookmark;
			}
		}

		Bookmark bookmark = new Bookmark(parent, x,
				reportexpressPrint.getPageHeight() - y, title);
		bookmarkStack.push(bookmark);
	}

	protected void setAnchor(Chunk chunk, RXPrintAnchor anchor,
			RXPrintElement element) {
		String anchorName = anchor.getAnchorName();
		if (anchorName != null) {
			chunk.setLocalDestination(anchorName);

			if (anchor.getBookmarkLevel() != RXAnchor.NO_BOOKMARK) {
				addBookmark(anchor.getBookmarkLevel(), anchor.getAnchorName(),
						element.getX(), element.getY());
			}
		}
	}

	protected void exportFrame(RXPrintFrame frame) throws DocumentException,
			IOException, RXException {
		if (frame.getModeValue() == ModeEnum.OPAQUE) {
			int x = frame.getX() + getOffsetX();
			int y = frame.getY() + getOffsetY();

			Color backcolor = frame.getBackcolor();
			pdfContentByte.setRGBColorFill(backcolor.getRed(),
					backcolor.getGreen(), backcolor.getBlue());
			pdfContentByte.rectangle(x, reportexpressPrint.getPageHeight() - y,
					frame.getWidth(), -frame.getHeight());
			pdfContentByte.fill();
		}

		setFrameElementsOffset(frame, false);
		try {
			frameTop = frame.getY();
			frameLeft = frame.getX();
			
			exportElements(frame.getElements());
			
			frameTop = 0;
			frameLeft = 0;
		} finally {
			restoreElementOffsets();
		}

		exportBox(frame.getLineBox(), frame);
	}

	/**
	 * Output stream implementation that discards all the data.
	 */
	public class NullOutputStream extends OutputStream {
		public NullOutputStream() {
		}

		public void write(int b) {
			// discard the data
		}

		public void write(byte[] b, int off, int len) {
			// discard the data
		}

		public void write(byte[] b) {
			// discard the data
		}
	}

	/**
	 *
	 */
	class LocalFontMapper implements FontMapper {
		public LocalFontMapper() {
		}

		public BaseFont awtToPdf(java.awt.Font font) {
			// not setting underline and strikethrough as we only need the base
			// font.
			// underline and strikethrough will not work here because
			// PdfGraphics2D
			// doesn't check the font attributes.
			Map<Attribute, Object> atts = new HashMap<Attribute, Object>();
			atts.putAll(font.getAttributes());
			return getFont(atts, null, false).getBaseFont();
		}

		public java.awt.Font pdfToAwt(BaseFont font, int size) {
			return null;
		}
	}

	/**
	 *
	 */
	protected void exportGenericElement(RXGenericPrintElement element) {
		GenericElementPdfHandler handler = (GenericElementPdfHandler) GenericElementHandlerEnviroment
				.getInstance(getRXReportsContext()).getElementHandler(
						element.getGenericType(), EFORM_EXPORTER_KEY);

		if (handler != null) {
			handler.exportElement(exporterContext, element);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("No PDF generic element handler for "
						+ element.getGenericType());
			}
		}
	}

	/**
	 *
	 */
	protected String getExporterKey() {
		return EFORM_EXPORTER_KEY;
	}
}
