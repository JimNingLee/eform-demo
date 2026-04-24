package com.cabsoft.ess;

import com.cabsoft.GlobalParams;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXImageRenderer;
import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintFrame;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintPage;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXRenderable;
import com.cabsoft.rx.engine.RXStyle;
import com.cabsoft.rx.engine.RXWrappingSvgRenderer;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.base.RXBasePrintImage;
import com.cabsoft.rx.engine.type.ModeEnum;
import com.cabsoft.rx.engine.type.OnErrorTypeEnum;
import com.cabsoft.rx.engine.type.ScaleImageEnum;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.rxe.Stamp;
import com.cabsoft.utils.Base64Util;
import com.cabsoft.utils.StringUtils;
import com.cabsoft.utils.SystemUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings({"deprecation", "unchecked", "rawtypes", "unused"})
public class ModifyReportExpressPrint {
	private  final Log log = LogFactory.getLog(ModifyReportExpressPrint.class);

	private final String scheck ="iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAALNJREFUeNpiYGBg2A/E/ynBLAwQ8ACIFzCQBxoYoC7Zz0A++M9EpsZ4IK6HcVjIMADkagEgdiDXkPlAbADFH8kxBOSFBCAOAOKH2JxHKGDlgfg9EK+nJGAnQOkCbJLEGGIP9cIEbN4g1hCQ7R+QXEPQEFDg3YcmZ1A46UNdsQE5NnDF+34kp8dDA/E/Eq2PL8Xiih19NIPISvYXkcJgArHJGFs64YdighkQlmINKMnJAAEGAC3TO4o3vgWZAAAAAElFTkSuQmCC";
	private final String suncheck = "iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAACXBIWXMAABcSAAAXEgFnn9JSAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAGNJREFUeNrs2LENwCAMRNFLxABsRDZnhYySDRwKkFzR2kj/pJNw92Q6F0mmRLmVLMW9v9E3yPH4wWZ74GKWwdJ9GSBAgAABAgQIECBAgAABAgTobJA/x9TRFg26xAVtn1+AAQDDAwpTRQw3wwAAAABJRU5ErkJggg==";
	
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	public final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";
	public final String PROPERTY_HTML_RADIOVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "radiovalue";
	public final String PROPERTY_HTML_TAG_TYPE = HTML_EXPORTER_PROPERTIES_PREFIX + "type";
	public final String PROPERTY_HTML_REMOVE = HTML_EXPORTER_PROPERTIES_PREFIX + "remove";
    
    ReportExpressPrint print = null;
    Map<String, String> mParams = new HashMap<String, String>();
    
    private HashMap<String,Object> exptmap = null; 

    public ModifyReportExpressPrint(ReportExpressPrint print) {
        this.print = print;
        this.mParams = null;
    }

    /**
     * 프레임 클래스인 경우 재귀 호출에 의해 수정한다.
     * @param frame
     * @param params
     * @throws Exception 
     */
    private void exportFrame(RXPrintFrame frame, Map<String, String> params) throws Exception{
    	List<RXPrintElement> elements = frame.getElements();
    	for(int j=0; j<elements.size(); j++){
    		RXPrintElement element = ((RXPrintElement) elements.get(j));
    		if (element instanceof RXPrintFrame) {
    			exportFrame((RXPrintFrame)element, params);
    		}else{
            	ProcessElements(elements,element,params,j);
            }
    	}
    }
    
	public ReportExpressPrint getReportExpressPrint(Map<String, String> params,HashMap<String, Object> Hmexpt) throws Exception {
        mParams = new HashMap<String, String>();
        this.exptmap = Hmexpt;
		
        Iterator<String> it = params.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            mParams.put(key, params.get(key));
        }

		List pages = print.getPages();
        int pageSize = print.getPages().size();

        log.debug("Page  Size: " + pageSize);

        for (int i = 0; i < pageSize; i++) {
            log.debug("Page: " + (i+1) + "/"+pageSize);
            RXPrintPage page = (RXPrintPage) pages.get(i);
            List elements = page.getElements();

            for (int j = 0; j < elements.size(); j++) {
//                log.debug("			elements: " + (j+1) + "/"+elements.size());
                RXPrintElement element = ((RXPrintElement) elements.get(j));
                if (element instanceof RXPrintFrame) {
                	exportFrame((RXPrintFrame)element, params);
                }else{
                	ProcessElements(elements,element,params,j);
                }
            }
            page.setElements(elements);

        }
        return print;
    }

	private void ProcessElements(List elements, RXPrintElement element, Map<String, String> params, Integer j ) throws Exception {
        String html_id = RXProperties.getProperty(element, PROPERTY_HTML_ID);
        String html_remove = RXProperties.getProperty(element, PROPERTY_HTML_REMOVE);
        String html_name = RXProperties.getProperty(element, PROPERTY_HTML_NAME);
        String tag_type = RXProperties.getProperty(element, PROPERTY_HTML_TAG_TYPE); 
		HashMap<String, Object>  Hmconfirm = new HashMap<String, Object>();
        
        if ( ! StringUtils.isNull(html_id) ) {
            log.debug("[ReportExpressPrint]	html_id: " + html_id  + ",html_name: " + html_name + ", tag_type:"+tag_type +
            		",html_remove: " + html_remove);
            
            if(html_remove!=null && "1".equals(html_remove)){
				log.debug("html_remove");
				if (element instanceof RXPrintText) {
	            	RXPrintText text = (RXPrintText) element;
	            	text.setText(" ");
	            	elements.remove(j);
	            	elements.add(j, text);
	            	text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
	            	text.getPropertiesMap().setProperty(PROPERTY_HTML_REMOVE, null);
            	}else{
            		elements.remove(j);
            		element.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
            		element.getPropertiesMap().setProperty(PROPERTY_HTML_REMOVE, null);
            	}
            }else if (params.containsKey(html_id)) {		//수정값에 있는 경우
				String value = (String) params.get(html_id);
				if (element instanceof RXPrintImage) {
					log.debug("RXPrintImage");
					/*
					 * Html 태그 입력에서 정의된 이미지로 교체한다. 교체할 이미지 파일은
					 * HashMap에 com.cabsoft.rx.export.html.id를 키로
					 * 정의되어 있다.
					 */
					if ( !StringUtils.isNull(value)) {
						try {
							if("hwsign".equals(tag_type)){
								RXPrintImage image = (RXPrintImage) element;
								image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								
								value = value.substring(value.indexOf(",")+1);
								
								byte[] b = Base64Util.decode(value.getBytes());
								ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(b));
								BufferedImage simg = ImageIO.read(stream);
								RXRenderable renderer = RXImageRenderer.getInstance(simg, OnErrorTypeEnum.BLANK);
								image.setRenderer(renderer);
								elements.remove(j);
								elements.add(j, image);
							}else{
								RXPrintImage image = (RXPrintImage) element;
								RXRenderable renderer;
								renderer = RXImageRenderer.getInstance((String) params.get(html_id));
								image.setRenderer(renderer);
								image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
								elements.remove(j);
								elements.add(j, image);
	
							}
						} catch (RXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else if (element instanceof RXPrintText) {
					log.debug("RXPrintText, html_id:" + html_id);
					RXPrintText text = (RXPrintText) element;
					if (html_id.equalsIgnoreCase("boldText") && value != null
							&& value.equalsIgnoreCase("1")) {

						RXStyle style = text.getStyle();
						if (style != null) {
							style.setForecolor(Color.BLACK);
							style.setBold(true);
							text.setStyle(style);
						}
						mParams.remove(html_id);
						elements.remove(j);
						elements.add(j, text);
						text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
					} else if ("textconfirm".equalsIgnoreCase(tag_type)) {
						String s = StringUtils.nvl( (String)exptmap.get(html_id), "");
						String req_s = StringUtils.nvl((String) params.get(html_id), "");
						log.debug("textconfirm 텍스트: " + s);
						if ( s.equals(req_s) ) {
							text.setText(s);
							mParams.remove(html_id);
							text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
							elements.remove(j);
							elements.add(j, text);
						} else {
							// 에러 처리 (올라온값과 기존값이 다른경우)
							throw new RXException( "(110)서버와의 연결(세션)이 종료되었습니다.");
						}
					} else if ("readconfirm".equalsIgnoreCase(tag_type)) {
						if ( "1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
							log.debug("HTML ID가 readconfirm으로 시작하므로 이 콘트롤의 스타일을 검정색 굵게로 변경");
							RXStyle style = text.getStyle();
							if (style != null) {
								style.setForecolor(Color.BLACK);
								style.setBold(true);
							}
							mParams.remove(html_id);
							elements.remove(j);
							elements.add(j, text);
							text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
						}
					} else if ("writeconfirm".equalsIgnoreCase(tag_type)) {
						if ( "1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
							log.debug("HTML ID가 writeconfirm 이므로 이 콘트롤의 내용을 [내용을 확인하였습니다.]로 변경");
							mParams.remove(html_id);
							text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
							if (!value.equalsIgnoreCase("")) {
								text.setText(value);
							} else {
								text.setText("내용을 확인하였습니다.");
							}
							elements.remove(j);
							elements.add(j, text);
						}
					} else if ("checkbox".equalsIgnoreCase(tag_type)) {
						log.debug("checkbox, html_id:" + html_id);
						if ( "1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
							text.setText("[v]"); //☑☑☐  Dejavu Sans
							// text.setText("■"); //□ ■ □ ■ 특수문자
						} else {
							text.setText("[ ]");
						}
						mParams.remove(html_id);
						text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
						elements.remove(j);
						elements.add(j, text);

					} else if ("radio".equalsIgnoreCase(tag_type)) {
						String attValue = text.getPropertiesMap().getProperty(PROPERTY_HTML_RADIOVALUE);
						log.debug("Radio Button: value = " + value + ", attValue = " + attValue);
						if (!StringUtils.isNull(attValue) && !StringUtils.isNull(value)
								&& value.equalsIgnoreCase(attValue)) {
							text.setText("●");
						} else {
							text.setText("○");
						}
						log.debug("html_id: " + html_id + "\tradio: " + text.getText());
						
						mParams.remove(html_id);
						text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
						
						elements.remove(j);
						elements.add(j, text);
					} else if ("text".equalsIgnoreCase(tag_type)) {
						String s = StringUtils.nvl( (String) params.get(html_id),"");
						log.debug("text: " + s);
						text.setText(s);
						mParams.remove(html_id);
						text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
						elements.remove(j);
						elements.add(j, text);
					} else {
						String s = (String) params.get(html_id);
						log.debug("일반 텍스트: " + s);
						if (!StringUtils.isNull(s)) {
							text.setText(s);
							mParams.remove(html_id);
							text.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
							elements.remove(j);
							elements.add(j, text);
						}
					}
				}
			}else{
				log.debug("etc....params.containsKey(html_id)");
				// 기타의 경우
				// 이미지에 날짜를 찍어 이미지 교체하기(2013.12.12)			
				if (element instanceof RXPrintImage) {
					
					if ("replaceimage".equals(tag_type)){
				
						String mid = html_id.substring(html_id.indexOf("_")+1);
						String value = params.get(mid);
						
						log.debug("Image ID = " + html_id);
						log.debug("Parameter ID = " + mid);
						log.debug("Parameter Value = " + params.get(mid));
						
						value = StringUtils.replaceAll(value, " ", "|");
						try{
							String fontpath = GlobalParams.getInstance().getCabsoftPath() + "fonts" + SystemUtils.FILE_SEPARATOR;
							RXPrintImage image = (RXPrintImage) element;
							
							RXRenderable renderer = image.getRenderer();
							if (renderer.getType() == RXRenderable.TYPE_SVG) {
								renderer = new RXWrappingSvgRenderer(renderer, new Dimension(image.getWidth(), image.getHeight()), ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor() : null);
							}
							byte[] ib = renderer.getImageData();
							ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(ib));
							BufferedImage m = ImageIO.read(stream);
							String sfontsize = RXProperties.getProperty(image, HTML_EXPORTER_PROPERTIES_PREFIX + "fontsize");
							String sfontcolor = RXProperties.getProperty(image, HTML_EXPORTER_PROPERTIES_PREFIX + "fontcolor");
							
							int fontsize = sfontsize==null || "".equals(sfontsize) ? 40 : Integer.parseInt(sfontsize.trim());
							sfontcolor = sfontcolor==null ? "" : sfontcolor.trim();
							Color fontcolor = Color.BLACK;
							if(!"".equals(sfontcolor)){
								String[] scolor = StringUtils.split(sfontcolor, ",");
								if(scolor.length==3){
									fontcolor = new Color(
											Integer.parseInt(scolor[0]), 
											Integer.parseInt(scolor[1]), 
											Integer.parseInt(scolor[2])
									);
								}
							}
							
						    /**
						     *
						     * @param java.awt.BufferedImage
						     * @param text - 기록할 글자로 개행은 "|"로 구분한다.
						     * @param fontpath - 글꼴이 저장된 전체 경로
						     * @param fontname - 글꼴 파일 이름(ttf만 지원)
						     * @param fontsize - 글꼴 크기
						     * @param top - 글자를 넣을 시작 위치
						     * @param gap - 줄간 간격
						     * @param zoom - 확대 축소 비율(%)
						     * @param fontcolor - 글자색
						     * @return 이미지
						     * @throws Exception
						     */
							BufferedImage img = Stamp.Image(m, value, fontpath, "malgunbd.ttf", fontsize, 130, 50, 100, fontcolor);
							renderer = RXImageRenderer.getInstance(img, OnErrorTypeEnum.BLANK);

							image.setRenderer(renderer);
							
							image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);

							elements.remove(j);
							elements.add(j, image);
						}catch(Exception e){
							log.error(e);
						}
					}else if("signimage".equals(tag_type)){
						String value = (String) params.get(html_id.replaceFirst("signimg_", ""));
						log.debug("signimage:"+value);
						if (StringUtils.isNull(value)) {
							RXPrintImage image = (RXPrintImage) element;
							setBlankImage(image);
							image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
							elements.remove(j);
							elements.add(j, image);
						}
					}else if("hiddenimage".equals(tag_type)){
						RXPrintImage image = (RXPrintImage) element;
						setBlankImage(image);
						image.getPropertiesMap().setProperty(PROPERTY_HTML_ID, null);
						elements.remove(j);
						elements.add(j, image);
					}
				}
			}
		}
    }
    
    public Map<String, String> getResultParams() {
        return mParams;
    }
    
    private void setBlankImage(RXPrintImage image) throws RXException{
		RXRenderable renderer = image.getRenderer();
		if (renderer.getType() == RXRenderable.TYPE_SVG) {
			renderer = new RXWrappingSvgRenderer(renderer, new Dimension(image.getWidth(), image.getHeight()), ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor() : null);
		}
    	BufferedImage timg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    	renderer = RXImageRenderer.getInstance(timg, OnErrorTypeEnum.BLANK);
    	image.setRenderer(renderer);
    }
    
    private RXPrintImage getCheckBox(boolean checked) throws Exception{
    	RXPrintImage image = null;

		byte[] b = null;
		if (checked) {
			b = Base64Util.decode(scheck.getBytes());
		} else {
			b = Base64Util.decode(suncheck.getBytes());
		}
		ImageInputStream stream = ImageIO
				.createImageInputStream(new ByteArrayInputStream(b));
		BufferedImage check = ImageIO.read(stream);
		RXRenderable renderer = RXImageRenderer.getInstance(check,
				OnErrorTypeEnum.BLANK);
		image = new RXBasePrintImage(null);
		image.setOnErrorType(OnErrorTypeEnum.BLANK);
		image.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
		image.setRenderer(renderer);
    	return image;
    }
}
