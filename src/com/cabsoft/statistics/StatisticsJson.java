package com.cabsoft.statistics;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.cabsoft.org.json.JSONException;
import com.cabsoft.org.json.JSONObject;

import com.cabsoft.utils.StackTrace;

public class StatisticsJson {
	public static JSONObject toJson(){
		JSONObject json = new JSONObject();
		Statistics stat = Statistics.getInstance();
		
		try{
        	long heapMax		= 0L;
        	long heapUsed		= 0L;
        	long nonHeapMax		= 0L;
        	long nonHeapUsed	= 0L;
        	
            /**
             * 메모리 통계
             */
        	MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            for (Method method : memoryMXBean.getClass().getDeclaredMethods()) {
                method.setAccessible(true);
                if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
                    Object value;
                    try {
                        value = method.invoke(memoryMXBean);
                    } catch (Exception e) {
                        value = e;
                        e.printStackTrace();
                    }
                    if("java.lang.management.MemoryUsage".equals(value.getClass().getCanonicalName())){
                        MemoryUsage mu = (MemoryUsage)value;
                        if("getHeapMemoryUsage".equals(method.getName())){
                        	heapMax		= mu.getMax();
                        	heapUsed	= mu.getUsed();
                        }else if("getNonHeapMemoryUsage".equals(method.getName())){
                        	nonHeapMax		= mu.getMax();
                        	nonHeapUsed		= mu.getUsed();
                        }
                    }
                }
            }
            json.put("HeapMax",				heapMax);
            json.put("HeapUsed",			heapUsed);
            json.put("NonHeapMax",			nonHeapMax);
            json.put("NonHeapUsed",			nonHeapUsed);
            
            json.put("reportCount", stat.getReportCount());
            json.put("excelCount", stat.getExcelCount());
            json.put("pdfCount", stat.getPdfCount());
            json.put("signCount", stat.getSngnCount());
            
            json.put("reportError", stat.getReportError());
            json.put("excelError", stat.getExcelError());
            json.put("pdfError", stat.getPdfError());
            json.put("signError", stat.getSignError());
           
            json.put("reportRunMin", stat.getReportRunMin());
            json.put("reportRunMax", stat.getReportRunMax());
            json.put("reportAvg", stat.getReportAvg());
            
            json.put("excelRunMin", stat.getExcelRunMin());
            json.put("excelRunMax", stat.getExcelRunMax());
            json.put("excelAvg", stat.getExcelAvg());
            
            json.put("pdfRunMin", stat.getPdfRunMin());
            json.put("pdfRunMax", stat.getPdfRunMax());
            json.put("pdfAvg", stat.getPdfAvg());
            
            json.put("signRunMin", stat.getSignRunMin());
            json.put("signRunMax", stat.getSignRunMax());
            json.put("signAvg", stat.getSignAvg());
            
            json.put("totalMin", stat.getTotalMin());
            json.put("totalMax", stat.getTotalMax());
            json.put("totalAvg", stat.getTotalAvg());
            
            
		}catch(Exception e){
			try {
				json.put("error", StackTrace.getStackTrace(e));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		return json;
	}
}
