package com.cabsoft.pdf.form.utils;

import com.cabsoft.text.BaseColor;
import com.cabsoft.text.Document;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("javadoc")
public class Attachments {
    private final String FormName = "SignerInfo";

    Map<String, String> filelist = null;
    private String SignerInfo;

    /**
     *
     */
    public Attachments(){
        SignerInfo = "";
    }

    /**
     *
     * @param SignerInfo
     */
    public void setSignerInfo(String SignerInfo){
        this.SignerInfo = SignerInfo;
    }

    /**
     *
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
     *
     * @param dest
     * @param attachments
     * @param description
     * @throws FileNotFoundException
     * @throws DocumentException
     * @throws IOException
     * @throws Exception
     */
    public void create(String dest, String[] attachments, String[] description) throws FileNotFoundException, DocumentException, IOException, Exception {
        FileOutputStream os = new FileOutputStream(dest);
        os.write(createPdf(attachments, description));
        os.flush();
        os.close();
    }

    /**
     *
     * @param src
     * @param dest
     * @param attachFileName
     * @param description
     * @throws IOException
     * @throws DocumentException
     */
    public void addAttachments(String src, String dest, String[] attachFileName, String[] description) throws IOException, DocumentException {
        FileInputStream in = new FileInputStream(new File(src));
        FileOutputStream out = new FileOutputStream(new File(dest));
        addAttachments(in, out, attachFileName, description);
    }
    
    /**
     * 
     * @param src
     * @param dest
     * @param attachFileName
     * @param description
     * @throws IOException
     * @throws DocumentException 
     */
    public void addAttachments(InputStream src, OutputStream dest, String[] attachFileName, String[] description) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src, "com/cabsoft".getBytes());
        PdfStamper stamper = new PdfStamper(reader, dest);
        for (int i = 0; i < attachFileName.length; i++) {
            addAttachment(stamper.getWriter(), new File(attachFileName[i]), description[i]);
        }
        if(SignerInfo!=null && !SignerInfo.equalsIgnoreCase("")){
            addSignerInfo(stamper);
        }
        stamper.close();
    }
    
    /**
     * 
     * @param src
     * @param dest
     * @param attachFileName
     * @param description
     * @throws IOException
     * @throws DocumentException 
     */
    public void addAttachments(ByteArrayOutputStream src, OutputStream dest, String[] attachFileName, String[] description) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src.toByteArray(), "com/cabsoft".getBytes());
        PdfStamper stamper = new PdfStamper(reader, dest);
        for (int i = 0; i < attachFileName.length; i++) {
            addAttachment(stamper.getWriter(), new File(attachFileName[i]), description[i]);
        }
        if(SignerInfo!=null && !SignerInfo.equalsIgnoreCase("")){
            addSignerInfo(stamper);
        }
        stamper.close();
    }

    /**
     *
     * @param writer
     * @param src
     * @param desc
     * @throws IOException
     */
    protected void addAttachment(PdfWriter writer, File src, String desc) throws IOException {
        PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(writer, src.getAbsolutePath(), src.getName(), null);
        writer.addFileAttachment(desc, fs);
    }

    private void addSignerInfo(PdfStamper stamper) throws DocumentException, IOException{
        Rectangle rect1 = new Rectangle(40, 710, 200, 726);
        TextField textfield = new TextField(stamper.getWriter(), rect1, FormName);
        textfield.setText(SignerInfo);
        textfield.setFieldName(FormName);
        stamper.addAnnotation(textfield.getTextField(), 1);
    }

    /**
     *
     * @param src
     * @param dir
     * @return
     * @throws IOException
     */
    public Map<String, String> extractAttachments(String src, String dir) throws IOException {
        FileInputStream in = new FileInputStream(new File(src));
        return extractAttachments(in, dir);
    }
    
    public Map<String, String> extractAttachments(InputStream src, String dir) throws IOException {
        File folder = new File(dir);
        folder.mkdirs();
        PdfReader reader = new PdfReader(src, "com/cabsoft".getBytes());
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
    
    public Map<String, String> extractAttachments(ByteArrayOutputStream src, String dir) throws IOException {
        File folder = new File(dir);
        folder.mkdirs();
        PdfReader reader = new PdfReader(src.toByteArray(), "com/cabsoft".getBytes());
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

    private String getFileName(String fs) {
        File f = new File(fs);
        return f.getName();
    }
}
