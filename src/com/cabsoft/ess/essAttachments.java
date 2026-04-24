package com.cabsoft.ess;

import com.cabsoft.text.BaseColor;
import com.cabsoft.text.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.cabsoft.text.DocumentException;
import com.cabsoft.text.Font;
import com.cabsoft.text.FontFactory;
import com.cabsoft.text.Paragraph;
import com.cabsoft.text.Rectangle;
import com.cabsoft.text.pdf.AcroFields;
import com.cabsoft.text.pdf.BaseFont;
import com.cabsoft.text.pdf.PRStream;
import com.cabsoft.text.pdf.PdfArray;
import com.cabsoft.text.pdf.PdfDictionary;
import com.cabsoft.text.pdf.PdfFileSpecification;
import com.cabsoft.text.pdf.PdfName;
import com.cabsoft.text.pdf.PdfReader;
import com.cabsoft.text.pdf.PdfStamper;
import com.cabsoft.text.pdf.PdfString;
import com.cabsoft.text.pdf.PdfWriter;
import com.cabsoft.text.pdf.TextField;
import com.cabsoft.text.pdf.collection.PdfCollection;
import com.cabsoft.text.pdf.collection.PdfCollectionField;
import com.cabsoft.text.pdf.collection.PdfCollectionItem;
import com.cabsoft.text.pdf.collection.PdfCollectionSchema;
import com.cabsoft.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class essAttachments {
	private static final Log log = LogFactory.getLog(essAttachments.class);
    private final String FormName = "SignerInfo";

    private Map<String, String> filelist = null;
    private String SignerInfo;

    /**
     * 생성자
     */
    public essAttachments(){
        SignerInfo = "";
    }

    /**
     * 추가 전자서명 설정
     * 추가 전자서명이 여러 개인 경우 |를 구분자로 하여 구분한다.
     * 즉, SignerInfo = "signed data1" + "|" + "signed data2";
     * @param SignerInfo
     */
    public void setSignerInfo(String SignerInfo){
        this.SignerInfo = SignerInfo;
    }

    /**
     * |를 구분자로 입력된 SignerInfo를 문자열 배열로 반환한다.
     * @return
     */
    public String[] getSignerInfo(){
        if(SignerInfo!=null && !SignerInfo.equalsIgnoreCase("")){
            return StringUtils.split(SignerInfo, "|");
        }else{
            return null;
        }
    }

    /**
     * PDF에 파일 첨부
     * @param dest 첨부파일이 추가하여 저장할 PDF 파일명
     * @param attachments 첨부파일 이름(전체 경로) 배열
     * @param description 첨부파일 설명 배열
     * @throws FileNotFoundException
     * @throws DocumentException
     * @throws IOException
     * @throws Exception
     */
    public void create(String dest, String[] attachments, String[] description) throws FileNotFoundException, DocumentException, IOException, Exception {
    	log.debug("create: " + dest);
    	for(int i=0; i<attachments.length; i++){
    		log.debug(attachments[i] + " : " + description[i]);
    	}
        FileOutputStream os = new FileOutputStream(dest);
        os.write(createPdf(attachments, description));
        os.flush();
        os.close();
    }

    /**
     * PDF에 파일 첨부
     * @param src 파일이 첨부될 PDF Byte Array Output Stream
     * @param dest  파일이 첨부된 PDF Output Stream
     * @param attachFileName 첨부파일 이름(전체 경로) 배열
     * @param description 첨부파일 설명 배열
     * @throws IOException
     * @throws DocumentException
     */
    public void addAttachments(ByteArrayOutputStream src, OutputStream dest, String[] attachFileName, String[] description) throws IOException, DocumentException {
    	log.debug("addAttachments");

        PdfReader reader = new PdfReader(src.toByteArray(), "com/cabsoft".getBytes());
        PdfStamper stamper = new PdfStamper(reader, dest);
        
        if(attachFileName!=null && attachFileName!=null){
	        for (int i = 0; i < attachFileName.length; i++) {
	        	log.debug(attachFileName[i] + " : " + description[i]);
	            addAttachment(stamper.getWriter(), new File(attachFileName[i]), description[i]);
	        }
        }
        if(SignerInfo!=null && !SignerInfo.equalsIgnoreCase("")){
            addSignerInfo(stamper);
        }
        stamper.close();
    }
    
    /**
     * PDF에 파일 첨부     
     * @param src 파일이 첨부될 PDF 파일 이름
     * @param dest 파일이 첨부된 PDF 파일 이름
     * @param attachFileName 첨부파일 이름(전체 경로) 배열
     * @param description 첨부파일 설명 배열
     * @throws IOException
     * @throws DocumentException
     */
    public void addAttachments(String src, String dest, String[] attachFileName, String[] description) throws IOException, DocumentException {
    	log.debug("addAttachments: " + src + "," + dest);
    	for(int i=0; i<attachFileName.length; i++){
    		log.debug(attachFileName[i] + " : " + description[i]);
    	}
        PdfReader reader = new PdfReader(src, "com/cabsoft".getBytes());
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        for (int i = 0; i < attachFileName.length; i++) {
            addAttachment(stamper.getWriter(), new File(attachFileName[i]), description[i]);
        }
        if(SignerInfo!=null && !SignerInfo.equalsIgnoreCase("")){
            addSignerInfo(stamper);
        }
        stamper.close();
    }

    /**
     * PDF에 파일 첨부
     * @param writer 파일이 첨부될 PdfWriter
     * @param src 첨부 파일 이름
     * @param desc 첨부파일 설명
     * @throws IOException
     */
    protected void addAttachment(PdfWriter writer, File src, String desc) throws IOException {
    	log.debug("addAttachments: " + desc);
        PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(writer, src.getAbsolutePath(), src.getName(), null);
        writer.addFileAttachment(desc, fs);
    }

    /**
     * 추가 서명 정보를 PDF(PdfStamper)에 입력 
     * @param stamper
     * @throws DocumentException
     * @throws IOException
     */
    private void addSignerInfo(PdfStamper stamper) throws DocumentException, IOException{
        Rectangle rect1 = new Rectangle(0, 0, 1, 1);
        TextField textfield = new TextField(stamper.getWriter(), rect1, FormName);
        textfield.setText(SignerInfo);
        textfield.setFieldName(FormName);
        textfield.setVisibility(TextField.HIDDEN);
        stamper.addAnnotation(textfield.getTextField(), 1);
    }
    
    /**
     * PDF(ByteArrayOutputStream)에 추가 전자서명(배열), 첨부파일(배열) 및 첨부파일 설명(배열)이 추가된 PDF를 생성하여
     * OutputStream으로 반환한다.
     * @param src 소스 PDF ByteArrayOutputStream
     * @param out 타겟 PDF OutputStream
     * @param attFs 첨부파일(배열)
     * @param attDesc 첨부파일 설명(배열)
     * @param sdData 추가 전자서명 데이터(배열)
     * @throws Exception
     */
	public void buildAttach(ByteArrayOutputStream src, OutputStream out, String[] attFs, String[] attDesc, String[] sdData) throws Exception{
		log.debug("추가 서명 정보 입력");
		
		String sd = "";
		log.debug("추가 전자서명 횟수: " + sdData.length);
		for(int i=0; i<sdData.length; i++){
			sd += sdData[i] + "|";
		}
		
		setSignerInfo(sd);
		addAttachments(src, out, attFs, attDesc);

		try{
			for(int i=0; i<attFs.length; i++){
				File f = new File(attFs[i]);
				if(f.exists()){
					f.delete();
				}
			}
		}catch(Exception e){
			
		}
	}

    /**
     * PDF(src)에서 첨부 파일을 추출하여 dir에 저장
     * @param src PDF 파일
     * @param dir 추출된 첨부 파일이 저장될 경로
     * @return 추출 결과 Map
     * @throws IOException
     */
    public Map<String, String> extractAttachments(String src, String dir) throws IOException {
        File folder = new File(dir);
        folder.mkdirs();
        PdfReader reader = new PdfReader(src);
        PdfDictionary root = reader.getCatalog();
        PdfDictionary names = root.getAsDict(PdfName.NAMES);
        PdfDictionary embedded = names.getAsDict(PdfName.EMBEDDEDFILES);
        PdfArray filespecs = embedded.getAsArray(PdfName.NAMES);
        filelist = new HashMap<String, String>();
        for (int i = 0; i<filespecs.size();) {
            extractAttachment(reader, folder, filespecs.getAsString(i++), filespecs.getAsDict(i++));
        }

        AcroFields form = reader.getAcroFields();
        SignerInfo = form.getField(FormName);
        return filelist;
    }

    /**
     * 
     * @param reader
     * @param dir
     * @param name
     * @param filespec
     * @throws IOException
     */
    protected void extractAttachment(PdfReader reader, File dir, PdfString name, PdfDictionary filespec) throws IOException {
        PRStream stream;
        FileOutputStream fos;
        String filename;
        PdfDictionary refs = filespec.getAsDict(PdfName.EF);

        String desc = filespec.getAsString(PdfName.DESC).toUnicodeString();

        for (Object okey : refs.getKeys()) {
            PdfName key = (PdfName) okey;
            filename = filespec.getAsString(key).toString();
            if(filelist.containsKey(filename)==false){
                filelist.put(filename, desc);
                stream = (PRStream) PdfReader.getPdfObject(refs.getAsIndirectObject(key));
                fos = new FileOutputStream(new File(dir, filename));
                fos.write(PdfReader.getStreamBytes(stream));
                fos.flush();
                fos.close();
            }
            
        }
    }

    /**
     * 
     * @return
     */
    private PdfCollectionSchema getCollectionSchema() {
        PdfCollectionSchema schema = new PdfCollectionSchema();

        PdfCollectionField size = new PdfCollectionField("파일 크기", PdfCollectionField.SIZE);
        schema.addField("SIZE", size);

        PdfCollectionField filename = new PdfCollectionField("파일 이름", PdfCollectionField.FILENAME);
        filename.setVisible(false);
        schema.addField("FILE", filename);

        PdfCollectionField title = new PdfCollectionField("설 명", PdfCollectionField.TEXT);
        schema.addField("DESC", title);
        return schema;
    }

    /**
     * 
     * @param filename
     * @param desc
     * @return
     * @throws DocumentException
     * @throws IOException
     * @throws Exception
     */
    private byte[] createPdf(String[] filename, String[] desc) throws DocumentException, IOException, Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        Font font = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED, 20);
        font.setColor(BaseColor.BLUE);
        Paragraph pg = new Paragraph("This document contains a collection of PDFs\nPlease wait to load completely.", font);
        pg.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(pg);

        PdfCollection collection = new PdfCollection(PdfCollection.DETAILS);
        PdfCollectionSchema schema = getCollectionSchema();
        collection.setSchema(schema);
        writer.setCollection(collection);

        PdfFileSpecification fs;
        PdfCollectionItem item;
        for (int i = 0; i < filename.length; i++) {
            byte[] b = readFile(filename[i]);
            fs = PdfFileSpecification.fileEmbedded(writer, null, getFileName(filename[i]), b);
            fs.addDescription(desc[i], false);
            item = new PdfCollectionItem(schema);
            item.addItem("DESC", desc[i]);
            fs.addCollectionItem(item);
            writer.addFileAttachment(desc[i], fs);
        }
        document.close();
        return baos.toByteArray();
    }

    /**
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    private byte[] readFile(String fileName) throws Exception {
        byte[] data = null;
        int size = 0;
        File file = null;
        FileInputStream fis = null;
        try {
            file = new File(fileName);
            fis = new FileInputStream(file);
            size = fis.available();
            data = new byte[size];
            fis.read(data);
            fis.close();
        } catch (Exception e) {
            System.out.println(fileName);
            throw new Exception(e);
        }
        return data;
    }

    /**
     * 
     * @param fs
     * @return
     */
    private String getFileName(String fs) {
        File f = new File(fs);
        return f.getName();
    }
}
