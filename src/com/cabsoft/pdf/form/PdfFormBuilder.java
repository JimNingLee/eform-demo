package com.cabsoft.pdf.form;

/**
 * eForm PDF Form을 생성하기 위한 클래스
 */
import com.cabsoft.pdf.form.images.Images;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXLineBox;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXPropertiesUtil;
import com.cabsoft.rx.engine.RXReportsContext;
import com.cabsoft.rx.engine.type.ModeEnum;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.text.BaseColor;
import com.cabsoft.text.DocumentException;
import com.cabsoft.text.Image;
import com.cabsoft.text.Rectangle;
import com.cabsoft.text.pdf.BaseFont;
import com.cabsoft.text.pdf.PdfAnnotation;
import com.cabsoft.text.pdf.PdfAppearance;
import com.cabsoft.text.pdf.PdfContentByte;
import com.cabsoft.text.pdf.PdfFormField;
import com.cabsoft.text.pdf.PdfWriter;
import com.cabsoft.text.pdf.RadioCheckField;
import com.cabsoft.text.pdf.TextField;
import com.cabsoft.utils.StringUtils;

import java.awt.Color;

@SuppressWarnings({"unused", "deprecation"})
public class PdfFormBuilder {
    private final static String EFORM_FORM_PROPERTIES_PREFIX = RXPropertiesUtil.PROPERTY_PREFIX + "export.html.";
    private final static String PROPERTY_EFORM_MULTILINE = EFORM_FORM_PROPERTIES_PREFIX + "multiline";
    private final static String PROPERTY_EFORM_PASSWORD = EFORM_FORM_PROPERTIES_PREFIX + "password";
    private final static String PROPERTY_EFORM_BORDER_COLOR = EFORM_FORM_PROPERTIES_PREFIX + "bordercolor";
    private final static String PROPERTY_EFORM_BORDER_WIDTH = EFORM_FORM_PROPERTIES_PREFIX + "borderwidth";
    private final static String PROPERTY_EFORM_BORDER_STYLE = EFORM_FORM_PROPERTIES_PREFIX + "borderstyle";
    private final static String PROPERTY_EFORM_BORDER_TYPE = EFORM_FORM_PROPERTIES_PREFIX + "bordertype";
    private final static String PROPERTY_EFORM_BACKGROUND_COLOR = EFORM_FORM_PROPERTIES_PREFIX + "backcolor";
    
    private final static String PROPERTY_EFORM_RADIO_CHECK_TYPE = EFORM_FORM_PROPERTIES_PREFIX + "checktype";
    
    private final static String PROPERTY_EFORM_CHECK_IMAGE= EFORM_FORM_PROPERTIES_PREFIX + "checkimage";
    
    /*
     * 체크박스 이미지
     */
    private final static String checkedImage = "iVBORw0KGgoAAAANSUhEUgAAAFQAAABXCAYAAACa/2JvAAAChElEQVR42u1cC42FQAxcB0hAAhKQgAQkIAEJSEACEpCABCQg4R2X7eUIuXvw6PDZ7UxSA5NtO/2tc8SnyGerZqtn68VeYj3peY9ktnK2drZhQdw7I1ZI5QXuJXBpE+n7RbFy3yNkZqTRx8RRQeSP5aTSuQZA5EvirHm0IDIrUokjsyWVODI7UunFOILMQTSqaZQgMkeS6SUNgkxqTSFgAhFqXmsmINFOrSnoQWQ2pBJXBVFrAjM65REwCU0kE5eEKI8EHTM6DhWIzJpUevdkRn9Y3GRGB8ZNZnRw3GRGB8ZNZnRg3GQSEiDGGANp9CiYhHBIQXU6k5AA0d9kEgJKJCYhoERiJbTA4PRJKCWNHojlhII0eiDm6RywAash7rwvoJ1aUryDXZ0bxUBX5xgD6OqMm0BXZ9xcQevqjJtAAc+4CazV2SwG1uqs01fQtuVYpy+g7cCzv7mCpgPPa4wVtMO2LGS3/I5znbyoyelbYonS1YOUSOVG9tUkA015OYRI5J6K5WiZpykvg5JI2QE9eOS0WaM5q5Be5ZGYNl5YXgbTRdJm272xNLXSRdJ2ePbejffOSDWE2LHcej2VM3Tof/bSqqa8DLJhjDrhM+/qSEL/KwVrZ/BPD9Q9efuHrjU5G+pOeKXaWj3oHmcNJHSU0lJTDQU/Bs6BhCJ234MfZyQPIjSab8+GB5AZ1eSyeQChUV1mFDeTGeWSwl1kRjts624iNNq5enUDmVF/GZk6as6g5ZOJbbmr3H50RnCV25tajO1PJtPcDnx5MqHmdjm1vUyuH15U25u+0DgjOZk/Kuj4OrFAdvK5Bw+UUPw0ZYGMZD6jHO0d/0baJHVrIWISnWn63vILC+kivwZTNwYAAAAASUVORK5CYII=";

    private static float MinLineWidth(RXLineBox box) {
        float min = 100000.0f;
        float lw = box.getLeftPen().getLineWidth();

        min = lw < min ? lw : min;
        lw = box.getRightPen().getLineWidth();
        min = lw < min ? lw : min;
        lw = box.getTopPen().getLineWidth();
        min = lw < min ? lw : min;
        lw = box.getBottomPen().getLineWidth();
        min = lw < min ? lw : min;
        return min;
    }

    /**
     * 테스트 입력
     *
     * @param rxReportsContext RXReportsContext
     * @param writer PdfWriter
     * @param width 페이지의 너비
     * @param height 페이지의 높이
     * @param id Form의 ID
     * @param text RXPrintText
     * @throws RXException
     */
	public static void addTextField(RXReportsContext rxReportsContext, PdfWriter writer,
            int width, int height, String id, RXPrintText text, int frameLeft, int frameTop) throws DocumentException {

		addTextField(rxReportsContext, writer, width, height, id, text, 1, frameLeft, frameTop);
		
	}
	
    /**
     * 테스트 입력
     *
     * @param rxReportsContext RXReportsContext
     * @param writer PdfWriter
     * @param width 페이지의 너비
     * @param height 페이지의 높이
     * @param id Form의 ID
     * @param text RXPrintText
     * @param padding
     * @throws RXException
     */
	public static void addTextField(RXReportsContext rxReportsContext, PdfWriter writer,
            int width, int height, String id, RXPrintText text, int padding, int frameLeft, int frameTop) throws DocumentException {
        try {
            int x = text.getX() + frameLeft + padding;
            int y = height - text.getY() - frameTop - padding;
            int w = text.getWidth() - 2*padding;
            int h = text.getHeight() - 2*padding;

            String s = text.getFullText();

            RXLineBox box = text.getLineBox();

            /*
             * 텍스트 입력 상자 정의
             */
            Rectangle rect = new Rectangle(x, y, x + w, y - h);
            Color color = text.getForecolor();
            BaseColor textColor = new BaseColor(color.getRed(), color.getGreen(), color.getBlue());

            BaseFont font = getBaseFont(rxReportsContext, text);
            int fontSize = text.getFontSize();

           TextField tf = new TextField(writer, rect, id);
            
            if (text.getModeValue() == ModeEnum.OPAQUE){
            	Color backcolor = text.getBackcolor();
            	tf.setBackgroundColor(getBaseColor(backcolor));
            }
               
            String bc = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_COLOR);
            String bw = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_WIDTH);
            String bt = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_STYLE);
            String ml = RXProperties.getProperty(text, PROPERTY_EFORM_MULTILINE);
            String pw = RXProperties.getProperty(text, PROPERTY_EFORM_PASSWORD);

            float borderWidth = 0.0f;
            if(bw!=null){
                borderWidth = Float.valueOf(bw.trim());
            }
            
            int borderType = 0;
            BaseColor boderColor = null;
            if(bc!=null){
                boderColor = getBaseColor(bc.trim());
            }
            
            if(bt!=null){
                borderType = Integer.valueOf(bt.trim());
                borderType = borderType<0 ? 0 : borderType;
                borderType = borderType>4 ? 4 : borderType;
                tf.setBorderStyle(borderType);
            }
            
            tf.setBorderWidth(borderWidth);
            if(borderType==0 || borderType==1 || borderType==4){
                tf.setBorderColor(boderColor);
            }
            
            if(ml!=null){
                if("1".equals(ml.trim())){
                    tf.setOptions(TextField.MULTILINE | TextField.DO_NOT_SCROLL);
                    
                }
            }else{
            	tf.setMaxCharacterLength(5);
            }
            
            if(pw!=null){
                pw = pw.trim();
                if("1".equals(pw)){
                    tf.setOptions(tf.getOptions() | TextField.PASSWORD);
                    
                }
            }
            
            tf.setFont(font);
            tf.setFontSize(fontSize);
            tf.setText(s);
            tf.setDefaultText(s);
            writer.addAnnotation(tf.getTextField());
        } catch (Exception e) {
        	e.printStackTrace();
            throw new DocumentException(e);
        }
    }

    public static void addCheckbox(RXReportsContext rxReportsContext, PdfWriter writer,
            int width, int height, String id, String tag, RXPrintText text, int frameLeft, int frameTop) throws DocumentException {
        try {
            int size = 0;

            int x = text.getX() + frameLeft;
            int y = height - text.getY() - frameTop;
            int w = text.getWidth();
            int h = text.getHeight();

            Rectangle rect = new Rectangle(x, y, x + w, y - h);

            if (w > h) {
                size = h;
            } else {
                size = w;
            }

            /*
             * 체크박스 이미지의 크기를 조절하기 위한 요소
             */
            int fact = 3;

            PdfContentByte cb = writer.getDirectContent();
            PdfFormField field = PdfFormField.createCheckBox(writer);

            PdfAppearance tpOff = cb.createAppearance(size, size);
            PdfAppearance tpOn = cb.createAppearance(size, size);

            String c = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_COLOR);
            String bw = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_WIDTH);
            String bt = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_TYPE);
            String bc = RXProperties.getProperty(text, PROPERTY_EFORM_BACKGROUND_COLOR);
            
            String is = RXProperties.getProperty(text, PROPERTY_EFORM_CHECK_IMAGE);
            if(is!=null){
                is = is.trim();
            }else{
                is = "black";
            }
            
            Rectangle offRect = new Rectangle(1, 1, size-1, size-1);
            Rectangle onRect = new Rectangle(1, 1, size-1, size-1);
            if(c!=null){
                String[] t = StringUtils.split(c.trim(), "|");
                BaseColor off_bordercolor = getBaseColor(t[0]);
                BaseColor on_bordercolor = getBaseColor(t[1]);
                
                offRect.setBorderColor(off_bordercolor);
                onRect.setBorderColor(on_bordercolor);
            }
            
            if(bc!=null){
                String[] t = StringUtils.split(bc.trim(), "|");
                BaseColor off_backcolor = getBaseColor(t[0]);
                BaseColor on_backcolor = getBaseColor(t[1]);
                
                offRect.setBackgroundColor(off_backcolor);
                onRect.setBackgroundColor(on_backcolor);
            }
            
            if(bw!=null){
                String[] t = StringUtils.split(bw.trim(), "|");
                float off_borderwidth = Float.valueOf(t[0]);
                float on_borderwidth = Float.valueOf(t[1]);
                
                offRect.setBorderWidth(off_borderwidth);
                onRect.setBorderWidth(on_borderwidth);
            }else{
                float off_borderwidth = 0.1f;
                float on_borderwidth = 0.1f;
                
                offRect.setBorderWidth(off_borderwidth);
                onRect.setBorderWidth(on_borderwidth);
            }
            
            if(bt!=null){
                String[] t = StringUtils.split(bt.trim(), "|");
                int off_bordertype = getBorderType(t[0]);
                int on_bordertype = getBorderType(t[1]);
                
                offRect.setBorder(off_bordertype);
                onRect.setBorder(on_bordertype);
            }else{
                offRect.setBorder(getBorderType("box"));
                onRect.setBorder(getBorderType("box"));
            }

            tpOff.rectangle(offRect);
            tpOff.stroke();
            
            tpOn.rectangle(onRect);

            byte[] b = Images.readData(is + ".png");
            Image img = Image.getInstance(b);
            
            float sf = 2.0f;
            float sz = (float)(size - fact * sf);
            img.scaleToFit(sz, sz);
            img.setAbsolutePosition(fact+1, fact+1);

            tpOn.fillStroke();
            tpOn.stroke();
            tpOn.addImage(img);
            
            field.setWidget(rect, PdfAnnotation.HIGHLIGHT_INVERT);
            field.setFieldName(id);
            if (tag.equalsIgnoreCase("1")) {
                field.setValueAsName("On");
                field.setAppearanceState("On");
            } else {
                field.setValueAsName("Off");
                field.setAppearanceState("Off");
            }
            field.setFlags(PdfAnnotation.FLAGS_PRINT);
            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "On", tpOn);
            writer.addAnnotation(field);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    public static void addCombo(RXReportsContext rxReportsContext, PdfWriter writer,
            int width, int height, String id, RXPrintText text, String[] options, int defIndex, int frameLeft, int frameTop) throws DocumentException {
        try {
            int x = text.getX() + frameLeft + 1;
            int y = height - text.getY() - frameTop - 1;
            int w = text.getWidth() - 2;
            int h = text.getHeight() - 2;

            Rectangle rect = new Rectangle(x, y, x + w, y - h);
            RXLineBox box = text.getLineBox();

            BaseFont font = getBaseFont(rxReportsContext, text);
            int fontSize = text.getFontSize();

            Color txtcolor = text.getForecolor();
            Color backcolor =text.getBackcolor();
            
            TextField tf = new TextField(writer, rect, id);
            tf.setTextColor(getBaseColor(txtcolor));
            tf.setChoices(options);
            tf.setChoiceExports(options);
            
            tf.setBackgroundColor(getBaseColor(backcolor));
            tf.setFont(font);
            tf.setFontSize(fontSize);

            PdfFormField combo = tf.getComboField();

            writer.addAnnotation(combo);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    public static void addRadio(RXReportsContext rxReportsContext, PdfWriter writer,
            int width, int height, PdfFormField radiogroup, RadioButton button) throws DocumentException {
        try {

            RXPrintText text = button.getRXPrintText();
            String onValue = button.getOnValue();
            boolean checked = button.isChecked();

            int x = text.getX() + button.getFrameLeft();
            int y = height - text.getY() - button.getFrameTop();
            int w = text.getWidth();
            int h = text.getHeight();

            int size = 0;
            if (w > h) {
                size = h;
            } else {
                size = w;
            }

            int fact = 1;

            x += (w - size) / 2;
            y += (h - size) / 2;

            size = size - 2 * fact;
            
            int x1 = x + fact;
            int y1 = y - fact;
            int x2 = x + size;
            int y2 = y - size;
            int s1 = x2 - x1;
            int s2 = y2 - y1;
            int sz = s1>s2 ? s1 : s2;

            int ssz = size - sz;
            Rectangle rect = new Rectangle(x1 + fact, y1 - fact, x + sz + fact, y - sz - fact);

            RadioCheckField radio = new RadioCheckField(writer, rect, null, onValue);

            String check_type = RXProperties.getProperty(text, PROPERTY_EFORM_RADIO_CHECK_TYPE);
            String c = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_COLOR);
            String bw = RXProperties.getProperty(text, PROPERTY_EFORM_BORDER_WIDTH);
            String bc = RXProperties.getProperty(text, PROPERTY_EFORM_BACKGROUND_COLOR);
            
            if(c!=null){
                radio.setBorderColor(getBaseColor(c.trim()));
            }else{
            	radio.setBorderColor(getBaseColor(0, 0, 0));
            }
            
            float borderWidth = 0.1f;
            if(bw!=null){
                borderWidth = Float.valueOf(bw.trim());
            }else{
            	borderWidth = 0.1f;
            }
            radio.setBorderWidth(borderWidth);

            if(bc!=null){
                radio.setBackgroundColor(getBaseColor(bc.trim()));
            }

            if(check_type==null){
            	radio.setCheckType(RadioCheckField.TYPE_CIRCLE);
            }else{
            	
            }
            radio.setChecked(checked);
            PdfFormField field = radio.getRadioField();

            radiogroup.addKid(field);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    private static int getRadioCheckType(String type){
    	int ret = 2;
    	
    	// √
    	if(type.equalsIgnoreCase("check")){
    		ret = 1;
    		
    	// ⊙
    	}else if(type.equalsIgnoreCase("circle")){
    		ret = 2;
    		
    	// +
    	}else if(type.equalsIgnoreCase("cross")){
    		ret = 3;
    		
    	// ◆
    	}else if(type.equalsIgnoreCase("diamond")){
    		ret = 4;
    		
    	// ■
    	}else if(type.equalsIgnoreCase("square")){
    		ret = 5;
    		
    	// ★
    	}else if(type.equalsIgnoreCase("star")){
    		ret = 6;
    	}
    	
    	return ret;
    }
    
    private static BaseFont getBaseFont(RXReportsContext rxReportsContext, RXPrintText text) throws DocumentException {
        try {
        	String fs = "MalgunGothic";
        	if(text.isBold()){
        		fs = fs + "Bold.otf";
        	}else{
        		fs = fs + ".otf";
        	}
            return BaseFontUtil.getBaseFont(fs);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }
    
    private static BaseColor getBaseColor(String sc){
        String[] c = StringUtils.split(sc, ",");
        for(int i=0; i<c.length; i++){
            c[i] = c[i].trim();
        }
        int r = Integer.valueOf(c[0]);
        int g = Integer.valueOf(c[1]);
        int b = Integer.valueOf(c[2]);
        r = r<0 ? 0 : r;
        g = g<0 ? 0 : g;
        b = b<0 ? 0 : b;
        r = r>255 ? 255 : r;
        g = g>255 ? 255 : g;
        b = b>255 ? 255 : b;
        
        return new BaseColor(r, g, b);
    }
    
    private static BaseColor getBaseColor(int r, int g, int b){
        r = r<0 ? 0 : r;
        g = g<0 ? 0 : g;
        b = b<0 ? 0 : b;
        r = r>255 ? 255 : r;
        g = g>255 ? 255 : g;
        b = b>255 ? 255 : b;
        
        return new BaseColor(r, g, b);
    }
    
    private static BaseColor getBaseColor(Color c){
        return new BaseColor(c.getRed(), c.getGreen(), c.getBlue());
    }
    
    private static int getBorderType(String s){
        int ret = Rectangle.NO_BORDER;
        if(s.equalsIgnoreCase("TOP")){
            ret = Rectangle.TOP;
        }else if(s.equalsIgnoreCase("BOTTOM")){
            ret = Rectangle.BOTTOM;
        }else if(s.equalsIgnoreCase("LEFT")){
            ret = Rectangle.LEFT;
        }else if(s.equalsIgnoreCase("RIGHT")){
            ret = Rectangle.RIGHT;
        }else if(s.equalsIgnoreCase("BOX")){
            ret = Rectangle.BOX;
        }
        return ret;
    }
}
