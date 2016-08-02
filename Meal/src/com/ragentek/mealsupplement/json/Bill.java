package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TBill;
import com.ragentek.mealsupplement.tools.DateTools;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zixiao.zhang on 2016/3/18.
 */
public class Bill extends JsonBean {
    public static final int TYPE_0 = 0; //调休
    public static final int TYPE_1 = 1; //不调休
    public static final int TYPE_2 = 2; //扣薪
    public static final int TYPE_3 = 3; //考勤补充
    public static final int TYPE_INVALID = -1; //自动生成的未生效表单
    public static final String BILL_NAME_INVALID = "未处理"; //未生效表单的名称
    private Long id;
    private String billName;
//    private java.sql.Timestamp startTime;
//    private java.sql.Timestamp endTime;
//    private String startTimeStr;
//    private String endTimeStr;
    private String startDate; //yyyy/MM/dd HH:mm
    private String endDate; //yyyy/MM/dd HH:mm
    private Integer totalHours;
    private String name;
    private String depart;
    private String number;
    private String descs;
    private Integer billType;
    //yingjing.liu 20160726 add   //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
    private String deptName1;
    private String deptName2;
    //yingjing.liu 20160726 add end
    public Bill(){}
    public Bill(TBill tBill) {
        id = tBill.getId();
        billName = tBill.getBillName();
        Timestamp startTime = tBill.getStartTime();
        if(startTime != null) {
            startDate = DateTools.formatDateToString(new Date(startTime.getTime()), DateTools.FORSTR_YYYYHHMMHHMM);
        }
        Timestamp endTime = tBill.getEndTime();
        if(endTime != null) {
            endDate = DateTools.formatDateToString(new Date(endTime.getTime()), DateTools.FORSTR_YYYYHHMMHHMM);
        }
        totalHours = tBill.getTotalHours();
        name = tBill.getName();
        depart = tBill.getDepart();
        number = tBill.getNumber();
        descs = tBill.getDescs();
        billType = tBill.getBillType();
    }

    @Override
    public TBill toSqlBean() {
        TBill tBill = new TBill();
        tBill.setId(id);
        tBill.setBillName(billName);
        if(startDate != null) {
            Date date = DateTools.formatStringToDate(startDate, DateTools.FORSTR_YYYYHHMMHHMM);
            tBill.setStartTime(new Timestamp(date.getTime()));
            tBill.setStartTimeStr(startDate);
        }
        if(endDate != null) {
            Date date = DateTools.formatStringToDate(endDate, DateTools.FORSTR_YYYYHHMMHHMM);
            tBill.setEndTime(new Timestamp(date.getTime()));
            tBill.setEndTimeStr(endDate);
        }
        tBill.setTotalHours(totalHours);
        tBill.setName(name);
        tBill.setDepart(depart);
        tBill.setNumber(number);
        tBill.setDescs(descs);
        tBill.setBillType(billType);
        return tBill;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public String getDeptName1() {
        return deptName1;
    }

    public void setDeptName1(String deptName1) {
        this.deptName1 = deptName1;
    }

    public String getDeptName2() {
        return deptName2;
    }

    public void setDeptName2(String deptName2) {
        this.deptName2 = deptName2;
    }

    //是否影响考勤
    public static boolean isKaoQinBill(Integer billType) {
        return billType!=null && billType.intValue() == TYPE_3;
    }
    //是否为默认表单，默认表单是自动生成的尚未生效
    public static boolean isInvalid(Integer billType) {
        return billType!=null && billType.intValue() == TYPE_INVALID;
    }
}
