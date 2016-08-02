package com.ragentek.mealsupplement.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kui.li on 2015/1/12.
 */
public class TestJava {
    public static void main(String []args) {
//        String day = "2014-12-08";
////        System.out.println(compareDataStr(day,"2014-12-08"));
////        printAllHiloday(2015);
//
//
//        // dotimes [2015-4-13 9:15:02] to [2015-4-13 09:15:02]
//        String dotimes = "2015-04-13 9:5:2";
//        Date date = DateTools.formatStringToDate(dotimes,DateTools.FORSTR_YYYYHHMMHHMMSS);
//        dotimes = DateTools.formatDateToString(DateTools.formatStringToDate(dotimes,DateTools.FORSTR_YYYYHHMMHHMMSS),DateTools.FORSTR_YYYYHHMMHHMMSS);
//        System.out.println(dotimes);
//        System.out.println("==" + "2015-04-13 23:59:59".substring(0,10));
//        String message = "The following SELinux domains were found to be in permissive mode:";
//        if (message == null) {
//            System.out.println("++++++++=");
//        }else if(message.contains("The following SELinux domains were found to be in permissive mode:")) {
//            System.out.println("===");
//        } else {
//            System.out.println("-------");
//        }

        String []arr = new String[]{"dd","dds"};
        for(String item : arr) {
            System.out.println("cmdArray=" + arr.toString());
        }

        ppp("1","2","3");
    }

    private static void ppp(String ... ss) {
        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(ss));
        for(String item : lst)
            System.out.println("ss=" + item);
    }

    private static void printAllHiloday(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2015);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.DAY_OF_MONTH,1);

        while(cal.get(Calendar.YEAR)<2016) {
//            System.out.println("cal.get(Calendar.DAY_OF_WEEK)=" + cal.get(Calendar.DAY_OF_WEEK));
            if(cal.get(Calendar.DAY_OF_WEEK)==7 || cal.get(Calendar.DAY_OF_WEEK)==1) {
                System.out.println(formatDateToString(cal.getTime(),"yyyy-MM-dd"));
            }
            cal.add(Calendar.DAY_OF_MONTH,1);
        }

    }
    public static String formatDateToString(Date date,String pattern) {
        SimpleDateFormat sFormat = new SimpleDateFormat(pattern);
        return sFormat.format(date);
    }
    public static Date formatStringToDate(String dateStr, String pattern) {
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

    /**
     *
     * @param source
     * @param target
     * @return  -1:前者小，0:相等, 1:前者大
     */
    private static int compareDataStr(String source, String target) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long start = sdf.parse(source).getTime();
            long end = sdf.parse(target).getTime();
            if(start-end > 0) {
                return 1;
            }else if(start-end ==0) {
                return 0;
            } else {
                return -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
