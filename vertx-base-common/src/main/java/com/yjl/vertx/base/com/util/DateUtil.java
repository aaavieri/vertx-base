package com.yjl.vertx.base.com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    
    public static final String YYYY_MM = "yyyyMM";
    
    public static final String DD = "dd";
    
    public static final String YYYYMMDD = "yyyyMMdd";
    
    public static final String YYYYMMDD_CHINESE_CHAR = "yyyy年MM月dd日";
    
    public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    
    public static String formatDate(final String pattern, final Date date) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    
    public static Date parseDate(final String pattern, final String strDate) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }
}
