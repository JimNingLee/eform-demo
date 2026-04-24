package com.cabsoft.sign;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.cabsoft.pdf.form.BaseFontUtil;
import com.cabsoft.text.BaseColor;
import com.cabsoft.text.Element;
import com.cabsoft.text.Font;
import com.cabsoft.text.Phrase;
import com.cabsoft.text.Rectangle;
import com.cabsoft.text.pdf.BaseFont;
import com.cabsoft.text.pdf.ColumnText;
import com.cabsoft.text.pdf.PdfContentByte;
import com.cabsoft.text.pdf.PdfLayer;
import com.cabsoft.text.pdf.PdfReader;
import com.cabsoft.text.pdf.PdfStamper;

public class AdditionStamp {
	
	/**
	 * 압축된 전자서명 데이터를 검증하여 인증서 정보를 추출한 후
	 * 이 인증서 정보를 PDF에 표시한다.
	 * @param src 소스 OutputStream
	 * @param dest 타겟 OutputStream
	 * @param signedData 압축된 전자서명 데이터
	 * @param fontsize 폰트 크기
	 * @param textcolor 텍스트 컬러
	 * @param bordercolor 보더 컬러
	 * @param left	X 좌표
	 * @param top Y 좌표
	 * @param width 폭
	 * @param height 높이
	 * @throws Exception
	 */
	public static void stamp(OutputStream src, OutputStream dest, String signedData, float fontsize,
			BaseColor textcolor, BaseColor bordercolor,
			int left, int top, int width, int height) throws Exception{
		
		stamp(src, dest, signedData, fontsize,  textcolor, bordercolor, left, top, width, height, 0.5f);
	}
	
	/**
	 * 압축된 전자서명 데이터를 검증하여 인증서 정보를 추출한 후
	 * 이 인증서 정보를 PDF에 표시한다.
	 * @param src 소스 OutputStream
	 * @param dest 타겟 OutputStream
	 * @param signedData 압축된 전자서명 데이터
	 * @param fontsize 폰트 크기
	 * @param textcolor 텍스트 컬러
	 * @param bordercolor 보더 컬러
	 * @param left	X 좌표
	 * @param top Y 좌표
	 * @param width 폭
	 * @param height 높이
	 * @param borderWidth 보더 굵기
	 * @throws Exception
	 */
	public static void stamp(OutputStream src, OutputStream dest, String signedData, float fontsize,
			BaseColor textcolor, BaseColor bordercolor,
			int left, int top, int width, int height, float borderWidth) throws Exception{
		
		RXDigitalSign sign = new RXDigitalSign();
		sign.verifyCompressedSignedData(signedData);
		
		String text = "고객이름: [" + sign.getSubjectName() + "]\n"
				    + "발급대상: [" + sign.getSubject("CN") + "]\n"
				    + "발급자: [" + sign.getIssuer("CN") + "]\n"
				    + "서명일자: [" + sign.getSigningDateTime() + "]\n"
				    + "인증서 지문: [" + sign.getCertFingerPrint() + "]";
		
		ByteArrayOutputStream baos = (ByteArrayOutputStream)src;
		PdfReader reader = new PdfReader(baos.toByteArray(), "com/cabsoft".getBytes());
		Rectangle rect = reader.getPageSize(1);
		PdfStamper stamper = new PdfStamper(reader, dest);
		PdfContentByte canvas = stamper.getOverContent(1);

		PdfLayer layer = new PdfLayer("Additional Info", stamper.getWriter());
		//layer.setOnPanel(false);
		layer.setOn(true);
		layer.setView(true);
		layer.setPrint("Print", true);
		
		canvas.beginLayer(layer);
		BaseFont bf = BaseFontUtil.getBaseFont("malgun.ttf");
		ColumnText ct = new ColumnText(canvas);
		Font font = new Font(bf, fontsize);
		font.setStyle(Font.BOLD);
		font.setColor(textcolor);
		Phrase myText = new Phrase(text, font);
		
		ct.setSimpleColumn(myText, left, rect.getHeight() - top, left+width, height, fontsize*1.2f, Element.ALIGN_LEFT);

		ct.go();
		canvas.endLayer();
		
		canvas.setLineWidth(borderWidth);
		canvas.setColorStroke(bordercolor);
		canvas.roundRectangle(left-5, rect.getHeight() - (top-5), 
				width+10, -height, 
				5
		);

		canvas.stroke();

		stamper.close();
		reader.close();
		dest.flush();
		dest.close();
	}
}
