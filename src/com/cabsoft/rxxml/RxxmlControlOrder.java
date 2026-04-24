package com.cabsoft.rxxml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RxxmlControlOrder {

	public static void main(String argv[]) throws Exception {

		try {
			//
			String filepath = "D:/WorkSpaceEclipse/Site/eformbank/WebContent/cdoc/eform/reports/shbank/20100012.rxxml";
			String filepathNew = "D:/WorkSpaceEclipse/Site/eformbank/WebContent/cdoc/eform/reports/shbank/20100012New.rxxml";
			
			if (argv.length == 0) {
				System.err.println("Please specify a file.");
			} else {
				filepath = argv[0];
				System.out.println("\ninput file:" + filepath);
			}
			filepathNew = filepath.replaceAll(".rxxml", "_New.rxxml");

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);

			NodeList listBand = doc.getElementsByTagName("band");

			for (int l = 0; l < listBand.getLength(); l++) {
				Node CtlNode = listBand.item(l);
				NodeList list = CtlNode.getChildNodes();

				int x1, y1, x2, y2;
				int orgmax;
				orgmax = list.getLength(); // list.getLength();
				for (int i = 0; i < orgmax; i++) {

					Node node = list.item(i);
					System.out.println("==============(i)" + i + ":" + node.getNodeName());

					if (node.getChildNodes().getLength() > 0) {
						// System.out.println("left:" +
						// node.getChildNodes().item(1).getNodeName());
						if ("reportElement".equals(node.getChildNodes().item(1).getNodeName())) {
							NamedNodeMap attr = node.getChildNodes().item(1).getAttributes();
							// System.out.println("left:"+node.getChildNodes().item(1).getNodeName());
							x1 = Integer.valueOf(attr.getNamedItem("x").getNodeValue());
							y1 = Integer.valueOf(attr.getNamedItem("y").getNodeValue());
							System.out.println("(x1,y1):" + x1 + "," + y1);

							// 적어도 2개의 데이터인경우
							if (i > 0) {
								for (int j = 0; j < i; j++) {
									Node node2 = list.item(j);
									// System.out.println("(j)"+j + ":" +
									// node2.getNodeName());

									if (node2.getChildNodes().getLength() > 0) {
										System.out.println("(j)" + j + ":" + "left:"
												+ node2.getChildNodes().item(1).getNodeName());
										if ("reportElement".equals(node2.getChildNodes().item(1).getNodeName())) {
											NamedNodeMap attr2 = node2.getChildNodes().item(1).getAttributes();
											// System.out.println("left:"+node.getChildNodes().item(1).getNodeName());
											x2 = Integer.valueOf(attr2.getNamedItem("x").getNodeValue());
											y2 = Integer.valueOf(attr2.getNamedItem("y").getNodeValue());
											System.out.println("(x1,y1):" + x1 + "," + y1 + "(x2,y2):" + x2 + "," + y2);

											// 위치가 잘못된것 찾음
											if ((y1 < y2) || (y1 == y2 && x1 < x2)) {

												System.out.println("(위치 찾음):" + j + "/" + i);
												CtlNode.insertBefore(node, node2);

												log_nodelist(list, i);
												break;
											}
										}
									}

								}
							}

						}
					}

				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepathNew));
			transformer.transform(source, result);

			System.out.println("Done");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
	}

	private static void log_nodelist(NodeList _list, Integer maxIndex) {
		int x1, y1;
		for (int k = 0; k < maxIndex; k++) {
			Node node3 = _list.item(k);
			if (node3.getChildNodes().getLength() > 0) {
				if ("reportElement".equals(node3.getChildNodes().item(1).getNodeName())) {
					NamedNodeMap attr3 = node3.getChildNodes().item(1).getAttributes();
					// System.out.println("left:"+node.getChildNodes().item(1).getNodeName());
					x1 = Integer.valueOf(attr3.getNamedItem("x").getNodeValue());
					y1 = Integer.valueOf(attr3.getNamedItem("y").getNodeValue());

					System.out.println(k + "(x1,y1):" + x1 + "," + y1);
				}
			}
		}

	}

}