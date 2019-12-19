package com.innodealing.bpms.unit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dealing076 on 2017/1/17.
 */
public class DateFormat {

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	
    /**
     * 把日期类型格式化成字符串
     * @param date
     * @param format
     * @return
     */
    public static String convert2String(Date date, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            return formater.format(date);
        } catch (Exception e) {
            return null;
        }
    }
    
	/**
     * 把日期字符串格式化成日期类型
     * @param dateStr
     * @param format
     * @return
     */
    public static Date convert2Date(String dateStr, String format) {  
        SimpleDateFormat simple = new SimpleDateFormat(format); 
        try {  
            simple.setLenient(false);  
            return simple.parse(dateStr);  
        } catch (Exception e) {  
            return  null;  
        }  
    }
}
