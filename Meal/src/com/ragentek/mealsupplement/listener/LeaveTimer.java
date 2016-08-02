package com.ragentek.mealsupplement.listener;

import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.poi.Attachment;
import com.ragentek.mealsupplement.poi.LeaveExport;
import com.ragentek.mealsupplement.service.LeaveService;
import com.ragentek.mealsupplement.tools.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zixiao.zhang on 2016/5/10.
 */
public class LeaveTimer extends BaseTimer {
    private static final Logger logger = Logger.getLogger(UserTimer.class);
    private static LeaveTimer instance;
    private LeaveTimer() {}
    public static LeaveTimer getInstance() {
        if(instance == null) {
            instance = new LeaveTimer();
        }
        return instance;
    }

    @Override
    protected void handle() throws Exception {
        Calendar calendar = Calendar.getInstance();
        String today = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE); //当天
        String secondWorkDay = getSecondWorkDay(); //获得本月第二个工作日
        int month = 0;
        String startDay = null;
        String endDay = null;
        if(today.equals(secondWorkDay)) { //今天是本月第二个工作日，发邮件提醒上个月的考勤异常
            calendar.set(Calendar.DAY_OF_WEEK, 1); //设为本月第一天
            calendar.add(Calendar.DAY_OF_WEEK, -1); //获得上月最后一天：本月第一天减去一天就是上个月最后一天
            month = calendar.get(Calendar.MONTH)+1; //日期从0开始
            endDay = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
            calendar.set(Calendar.DAY_OF_WEEK, 1); //上个月第一天
            startDay = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
        } else if(today.compareTo(secondWorkDay) > 0) { //本月第二个工作日后才会提醒本月的考勤异常
            if(isMonday(today)) { //每个周一发邮件提醒
                month = calendar.get(Calendar.MONTH)+1; //日期从0开始
                calendar.add(Calendar.DAY_OF_MONTH, -1); //前一天
                endDay = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
                calendar.set(Calendar.DAY_OF_MONTH, 1); //本月第一天，因为今天至少是本月第二个工作日之后了，所以前一天肯定还是本月，不可能到达了上个月
                startDay = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
            }
        }
        if(month > 0 && startDay != null && endDay != null && startDay.compareTo(endDay) <= 0) {
            LeaveUtil.notifyAttendance(month, startDay, endDay);
        }
    }

    /**
     * 1：星期日，2：星期一，3：星期二，4：星期三，5：星期四，6：星期五，7：星期六
     * @return
     */
    private static boolean isMonday(String day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTools.formatStringToDate(day, DateTools.FORSTR_DATE));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 2;
    }

    //获得本月第二个工作日
    private static String getSecondWorkDay() throws ParseException {
        String secondWorkDay = null;
        int workDayCount = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); //设为本月1号
        int curMonth = calendar.get(Calendar.MONTH); //当前月
        while(true) {
            String day = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
            if(!KaoQin.checkWetherHoliday(day)) { //非休息日，也就是工作日
                workDayCount ++;
            }
            if(workDayCount >= 2) {
                secondWorkDay = day;
                break;
            }
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            int month = calendar.get(Calendar.MONTH);
            if(month != curMonth) { //到了下个月了
                break;
            }
        }
        return secondWorkDay;
    }

//    public static void main(String[] args) throws Exception {
        /*
        String[] weeks = new String[] {
                "星期日","星期一","星期二","星期三","星期四","星期五","星期六"
        };
        Calendar calendar = Calendar.getInstance();
        for(int i=0;i<30;i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            System.out.println(DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE));
            System.out.println(weeks[calendar.get(Calendar.DAY_OF_WEEK)-1]+":"+calendar.get(Calendar.WEEK_OF_MONTH)+":"+calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
            System.out.println(calendar.get(Calendar.MONTH));
        }*/
        /*
        int month = 5;
        String startDay = "2016/01/04";
        String endDay = "2016/01/05";
        LeaveUtil.notifyAttendance(month, startDay, endDay);
        */
//    }

    @Override
    protected int getTriggerHour() {
        return 2; //每天凌晨2点触发
    }
}
