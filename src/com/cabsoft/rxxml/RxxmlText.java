package com.cabsoft.rxxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.cabsoft.utils.UUIDGenerator;

public class RxxmlText {

	private RxxmlText() {
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFile(String fileName) throws Exception {
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
			// log.debug(fileName);
			throw new Exception(e);
		}
		return data;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String sreadFile(String fileName) throws Exception {
		String ret = "";
		ret = new String(readFile(fileName));
		return ret;
	}

	// 바이너리 데이터(data)를 fileName으로 저장
	/**
	 * 
	 * @param data
	 * @param fileName
	 * @throws Exception
	 */
	public static void writeFile(byte[] data, String fileName) throws Exception {
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(data);
		fos.close();
	}

	// 텍스트 데이터(data)를 fileName으로 저장
	/**
	 * 
	 * @param data
	 * @param fileName
	 * @throws Exception
	 */
	public static void swriteFile(String data, String fileName) throws Exception {
		writeFile(data.getBytes(), fileName);
	}

	// 파일 삭제
	/**
	 * 
	 * @param fs
	 */
	public static void deleteFile(String fs) {
		File f = new File(fs);
		try {
			if (f.delete()) {
				// log.debug("파일 " + fs + "가(이) 삭제되었습니다.");
			} else {
				// log.debug("파일 " + fs + " 파일 삭제 실패");
			}
		} catch (Exception e) {
			// log.debug(fs + " 삭제 오류: " + e.toString());
		}
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static String[] FileList(String filePath) {
		String files[] = null;
		try {
			File path = new File(filePath);
			files = path.list();
		} catch (Exception e) {
			// log.debug("FileList: " + e);
		}
		return files;
	}

	public static String chkImageFile(String src, String rep) {
		String imgPath = "";
		File f = new File(src);
		if (f.exists()) {
			imgPath = src;
		} else {
			imgPath = rep;
		}

		return imgPath;
	}
	
	  @SuppressWarnings("unused")
	public static void main (String[] args) throws Exception{

	  // 변환용 txt파일명
	  String fileName2  = "aaa.txt";

		  if (args.length == 0){
			  System.err.println("Please specify a file.");
			  System.out.println("\nSet file :" +fileName2 );
		      
	    } else {
	    	fileName2 = args[0];
			  System.out.println("\ninput file :" +fileName2 );
	    }
//		  String fileName  = "aaa.txt";
//		  String data = "￭ 금융거래관계의 설정•유지•이행•관리\n";
//			FileOutputStream fos = new FileOutputStream(fileName);
//			fos.write(data.getBytes());
//			fos.write(data.getBytes());
//			fos.write(data.getBytes());
//			fos.close();

		  String fileName  = "default.rxxml";
		  		  
//			byte[] data = null;
//			int size = 0;
//			File file = null;
//			FileInputStream fis = null;
//			try {
//				file = new File(fileName);
//				fis = new FileInputStream(file);
//				size = fis.available();
//				data = new byte[size];
//				fis.read(data);
//				fis.close();
//			} catch (Exception e) {
//				// log.debug(fileName);
//				throw new Exception(e);
//			}
			
		  String string_rxxml = "";
		  String string_data = "";
		  StringBuffer string_ctls = new StringBuffer();
		  String string_b, string_a;
		  string_rxxml = sreadFile(fileName);
		  string_b = string_rxxml.substring(0, string_rxxml.indexOf("<detail>")+"<detail>".length());
		  string_a = string_rxxml.substring( string_rxxml.indexOf("</detail>"));
		
		  string_data = sreadFile(fileName2);
		  // 구분자 셋팅
		  String[] data = string_data.split("\n");
		  System.out.println("input line Count : "+data.length);
		  
		  
		  
		  string_ctls.setLength(0);
		  string_ctls.append("\n");
		  string_ctls.append("\n<band height=\""+data.length*20+"\" splitType=\"Stretch\">\n");

			String s = "";
			for (int i = 0; i < data.length; i++) {
				if (!"".equals(data[i])) {
					 string_ctls.append(GetstaticText (data[i],i));
				}
			}

			string_ctls.append("</band>\n");
		  
			//System.out.println(string_b + string_ctls.toString()+ string_a);
		  
			swriteFile(string_b + string_ctls.toString()+ string_a, fileName2.replace(".txt", ".rxxml"));
			System.out.println(fileName2.replace(".txt", ".rxxml")+"...........[Ok]");
	  }
	  
		public static String GenerateID() {
			UUIDGenerator gen = UUIDGenerator.getInstance();
			return gen.generateRandomBasedUUID().toString();
		}	  
		public static String GetstaticText(String aaa, long idx) {
			 StringBuffer strctl = new StringBuffer();
			 long X=0;
			 long Y=idx*20;
			 long W=480;
			 long H = 20;
			 
			 strctl.append("			<staticText>\n");
			 strctl.append("				<reportElement uuid=\""+GenerateID()+"\" x=\""+X+"\" y=\""+Y+"\" width=\""+W+"\" height=\""+H+"\"/>\n");
			 strctl.append("				<textElement/>\n");
			 strctl.append("				<text><![CDATA["+aaa+"]]></text>\n");
			 strctl.append("			</staticText>\n");
			  
			  return strctl.toString();
		}	  
		public static String GettextField(String aaa) {
			 StringBuffer strctl = new StringBuffer();
//			  string_ctls.append("			<textField>\n");
//			  string_ctls.append("				<reportElement uuid=\""+GenerateID()+"\" x=\"6\" y=\"12\" width=\"435\" height=\"20\"/>\n");
//			  string_ctls.append("				<textElement/>\n");
//			  string_ctls.append("				<textFieldExpression><![CDATA[$F{field}]]></textFieldExpression>\n");
//			  string_ctls.append("			</textField>\n");
			  
			  return strctl.toString();
		}	  
}
