package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.SqlBean;

/**
 * Created by zixiao.zhang on 2016/5/16.
 */
public class Stats implements SqlBean {
    private String number; //考勤号码
    private String name; //姓名
    private String date; //日期
    private String startTime; //开始时间
    private String endTime; //结束时间
    private String workDays; //实到
    private String lateAndEarly; //迟到/早退
    private String noPunch; //未打卡
    private String barracks; //公出
    private String businessTravel; //出差
    private String personalLeave; //事假
    private String sickLeave; //病假
    private String annualLeave; //年假
    private String otherLeave; //其他假期
    private String overtimes; //加班
    private String normalOffs; //调休（正常）
    private String abnormalOffs; //调休（非正常）
    private String remainingOvertime; //加班结余
    private String breakfast; //早餐补助
    private String lunch; //午餐补助
    private String dinner; //晚餐补助
    private String supper; //夜宵补助

    @Override
    public String getTableName() {
        return null;
    }
    @Override
    public String getPrimaryKeyColumnName() {
        return null;
    }
    @Override
    public String[] getColumnNames() {
        return new String[0];
    }
}
