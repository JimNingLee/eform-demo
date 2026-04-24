package com.cabsoft.pdf.form;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class SelectBox {
	private final String x_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><options>";
	private final String x_footer = "</options>";
	
	private Document doc = null;
	private List<String> list = null;
	private String xpath = "";
	
	public SelectBox(String xml, String xpath) throws Exception{
		list = new ArrayList<String>();
		this.xpath = xpath;
		String x = x_header + xml + x_footer;
		InputSource is = new InputSource(new StringReader(x));
		
		doc = (Document)DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	}
	
    public int getSelectedIndex() throws XPathExpressionException {
        list = new ArrayList<String>();
        XPath Xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) Xpath.evaluate(xpath, doc, XPathConstants.NODESET);
        int ret = -1;
        
        for (int x = 0; x < nodes.getLength(); x++) {
            if(nodes.item(x).hasAttributes()){
                ret = x;
            }
            
            String s = nodes.item(x).getTextContent();

            s = s.trim();
            list.add(s);
        }
        return ret;
    }
    
    public List<String> getOptions(){
    	return list;
    }
    
}
