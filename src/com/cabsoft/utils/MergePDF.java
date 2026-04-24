package com.cabsoft.utils;

import com.cabsoft.text.pdf.PdfCopyFields;
import com.cabsoft.text.pdf.PdfReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("deprecation")
public class MergePDF {
    private final static Log log = LogFactory.getLog(MergePDF.class);
    public static void main(String[] args) {
        try {
            String input = "input/";
            String out = "out/";

            List<InputStream> pdfs = new ArrayList<InputStream>();
            pdfs.add(new FileInputStream(input + "001.pdf"));
            pdfs.add(new FileInputStream(input + "002.pdf"));
            pdfs.add(new FileInputStream(input + "003.pdf"));

            OutputStream output = new FileOutputStream(out + "merge.pdf");
            MergePDF.concatPDFs(pdfs, output);
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param streamOfPDFFiles
     * @param outputStream
     * @throws Exception 
     */
    @SuppressWarnings("unused")
	public static void concatPDFs(List<InputStream> streamOfPDFFiles, OutputStream outputStream) throws Exception {
        try {
            PdfCopyFields copy = new PdfCopyFields(outputStream);
            
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            while (iteratorPDFs.hasNext()) {
            	System.out.println("AAAA");
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf, "com/cabsoft".getBytes());
                copy.addDocument(pdfReader);
            }
            copy.close();
        } catch (Exception e) {
            log.error(e.toString());
            throw new Exception(e);
        } finally {
            try {
                if (outputStream != null) {
                	outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ioe) {
                log.debug(ioe.toString());
            }
        }
    }
}
