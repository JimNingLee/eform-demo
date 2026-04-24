package com.cabsoft.site.kfda;

import com.cabsoft.Barcode;
import com.cabsoft.RXHDBarCode;
import com.cabsoft.text.Image;
import com.cabsoft.text.pdf.BaseFont;
import com.cabsoft.text.pdf.PdfContentByte;
import com.cabsoft.text.pdf.PdfLayer;
import com.cabsoft.text.pdf.PdfReader;
import com.cabsoft.text.pdf.PdfStamper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RXApplyRXCode {
    private final static Log log = LogFactory.getLog(RXApplyRXCode.class);
    
    IRXCodeInfo rxcodeinfo;
    
    public RXApplyRXCode(IRXCodeInfo rxcodeinfo){
        this.rxcodeinfo = rxcodeinfo;
    }
    
    public void doProcess(OutputStream out, PdfReader reader) throws Exception {
        try {
            Image qrcode = null;
            if(rxcodeinfo.isInsertBarcode()){
                log.debug("Make QRCode");
                log.debug("QRCode Data: " + rxcodeinfo.getQRData());

//                BufferedImage logo = ImageIO.read(new File("logo.png"));
//                logo = ImageResize.resizeImage(logo, 100, .5f);                
                
                ByteArrayOutputStream qbaos = new ByteArrayOutputStream();
                
                ImageIO.write(Barcode.createQRCode(rxcodeinfo.getQRData(), "UTF-8", 200, 1, null), "png",qbaos);
                qbaos.flush();
                qbaos.close();
                qrcode = Image.getInstance(qbaos.toByteArray());
            }
            
            List<BufferedImage> barcodelist = new ArrayList<BufferedImage>();
            if(rxcodeinfo.isInsertBarcode()){
                log.debug("Make RXCode");
                log.debug("RXCode Data: " + rxcodeinfo.getBarcodeData());
                RXHDBarCode barcode = new RXHDBarCode();
                barcodelist = barcode.makeBuffer(rxcodeinfo.getBarcodeData(), 0, 0, rxcodeinfo.getPages_Num());
            }
            
            float[] pos = {0.0f, 0.0f};

            int n = reader.getNumberOfPages();
            PdfStamper stamper = new PdfStamper(reader, out);
            PdfLayer wmLayer = new PdfLayer("RXCODE_LAYER", stamper.getWriter());
            wmLayer.setOnPanel(true);
            wmLayer.setPrint("print", true);
            wmLayer.setOn(true);
            wmLayer.setView(true);
            
            int i = 1;
            PdfContentByte over;
            while (i <= n) {
                over = stamper.getOverContent(i);
                
                over.beginLayer(wmLayer);

                /**
                 * 고밀도 바코드
                 */
                if(rxcodeinfo.isInsertBarcode()){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(barcodelist.get(i-1), "png", baos);
                    baos.flush();
                    baos.close();
                    Image img = Image.getInstance(baos.toByteArray());
                    img.scalePercent(rxcodeinfo.getBarcode_size_percent());
                    
                    // 세로 용지
                    if (reader.getPageRotation(1) == 0) {
                        pos[0] = rxcodeinfo.getBarcode_x();
                        pos[1] = rxcodeinfo.getBarcode_y();
                    // 가로 용지
                    } else {
                        pos[1] = rxcodeinfo.getBarcode_x();
                        pos[0] = rxcodeinfo.getBarcode_y();
                    }
                    img.setAbsolutePosition(pos[0], pos[1]);
                    over.addImage(img);
                }
                
                if(i==1 && rxcodeinfo.isInsertID()){
                    log.debug("발급 번호: " + rxcodeinfo.getIssuerID());
                    over.beginText();
                    BaseFont bf = BaseFont.createFont(rxcodeinfo.getFontFile(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true);
                    over.setFontAndSize(bf, rxcodeinfo.getFont_size());
                    over.setTextMatrix(rxcodeinfo.getId_x(), rxcodeinfo.getId_y());
                    over.showText(rxcodeinfo.getIssuerIDString());
                    over.endText();
                }
                if(rxcodeinfo.isInsertQRCode()){
                    // 세로 용지
                    if (reader.getPageRotation(1) == 0) {
                        pos[0] = rxcodeinfo.getQr_x();
                        pos[1] = rxcodeinfo.getQr_y();
                    // 가로 용지
                    }else{
                        pos[1] = rxcodeinfo.getQr_x();
                        pos[0] = rxcodeinfo.getQr_y();
                    }
                    if(!rxcodeinfo.isLastPageOnly()){
                        qrcode.setAbsolutePosition(pos[0], pos[1]);
                        qrcode.scalePercent(rxcodeinfo.getQr_size_percent());
                        over.addImage(qrcode);
                    }else if(i==n){
                        qrcode.setAbsolutePosition(pos[0], pos[1]);
                        qrcode.scalePercent(rxcodeinfo.getQr_size_percent());
                        over.addImage(qrcode);
                    }
                }
                
                over.endLayer();
                i++;
            }
            stamper.close();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
