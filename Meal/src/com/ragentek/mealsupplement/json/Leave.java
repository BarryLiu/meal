package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TLeave;

/**
 * Created by zixiao.zhang on 2016/3/25.
 */
public class Leave extends JsonBean {
    private Long id;
    private String userInfo;
    private String name;
    private String number;
    private String dayStr;
    private String startTime;
    private String endTime;
    private Integer totalHours;
    private Long billId;
    private String billName;
    private String status;
    //yingjing.liu 20160726 add   //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
    private String deptName1;
    private String deptName2;
    //yingjing.liu 20160726 add end

    public Leave() {}
    public Leave(TLeave tLeave) {
        id = tLeave.getId();
        userInfo = tLeave.getUserInfo();
        name = tLeave.getName();
        number = tLeave.getNumber();
        dayStr = tLeave.getDayStr();
        startTime = tLeave.getStartTime();
        endTime = tLeave.getEndTime();
        totalHours = tLeave.getTotalHours();
        billId = tLeave.getBillId();
        billName = tLeave.getBillName();
        status = tLeave.getStat();


    }

    @Override
    public String toString() {
        return "Leave{" +
                "id=" + id +
                ", userInfo='" + userInfo + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", dayStr='" + dayStr + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalHours=" + totalHours +
                ", billId=" + billId +
                ", billName='" + billName + '\'' +
                ", status='" + status + '\'' +
                ", deptName1='" + deptName1 + '\'' +
                ", deptName2='" + deptName2 + '\'' +
                '}';
    }

    @Override
    public TLeave toSqlBean() {
        TLeave tLeave = new TLeave();
        tLeave.setId(id);
        tLeave.setUserInfo(userInfo);
        tLeave.setName(name);
        tLeave.setNumber(number);
        tLeave.setDayStr(dayStr);
        tLeave.setStartTime(startTime);
        tLeave.setEndTime(endTime);
        tLeave.setTotalHours(totalHours);
        tLeave.setBillId(billId);
        tLeave.setBillName(billName);
        tLeave.setStat(status);
        return tLeave;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        id = id;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeptName1(String deptName1) {
        this.deptName1 = deptName1;
    }

    public String getDeptName1() {
        return deptName1;
    }

    public void setDeptName2(String deptName2) {
        this.deptName2 = deptName2;
    }

    public String getDeptName2() {
        return deptName2;
    }
}
