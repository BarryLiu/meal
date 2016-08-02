package com.ragentek.mealsupplement.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kui.li on 2015/4/16.
 */
public class DateTools {
    //public static final String FORSTR_YYYYHHMMHHMMSS="yyyy-MM-dd HH:mm:ss";
    public static final String FORSTR_DATETIME = "yyyy/MM/dd HH:mm:ss";
    public static final String FORSTR_DATE="yyyy/MM/dd";
    public static final String FORSTR_TIME = "HH:mm:ss";
    public static final String FORSTR_YYYYHHMMHHMMSS="yyyy/MM/dd HH:mm:ss";
    public static final String FORSTR_YYYYHHMMHHMM = "yyyy/MM/dd HH:mm";

    public static String convertDateSeparator(String date) {
        String str="";
        if(date!=null && !date.equals(""))
            str = date.replace('-', '/');  // eg: 2015-8-5 -> 2015/8/5
        else
            str = date;
        return str;
    }


    /**
     * 格式化日期时间
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDateToString(Date date,String pattern) {
        SimpleDateFormat sFormat = new SimpleDateFormat(pattern);
        return sFormat.format(date);
    }

    /**
     * 字符串转日期
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date formatStringToDate(String dateStr, String pattern) {
        if(dateStr != null && dateStr.length()<11) {
            pattern = "yyyy/MM/dd";
        }
        SimpleDateFormat sFormat = new SimpleDateFormat(pattern);
        try {
            return sFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                Date date = new Date();
                date.setTime(Long.parseLong(dateStr));
                return date;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
