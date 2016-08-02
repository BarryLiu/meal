package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.SqlBean;

/**
 * Created by zixiao.zhang on 2016/4/13.
 */
public class Statistic implements SqlBean {
    private String userInfo; //用户信息：学号-姓名
    private Integer totalfees; //总餐补
    private Integer totalovertimes; //总加班时间
    private Integer totalleaves; //总调休时间
    private Integer unhandledleaves; //总未处理的时间
    //private Integer actualovertimes; //净加班时间（计算得到）：总加班时间-总调休时间

    //private String number; //学号：来自用户信息
    //private String name; //姓名：来自用户信息

    //yingjing.liu 20160726 add   //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
    private String deptName1;
    private String deptName2;

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
    //yingjing.liu 20160726 add end

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public Integer getTotalfees() {
        return totalfees;
    }

    public void setTotalfees(Integer totalfees) {
        this.totalfees = totalfees;
    }

    public Integer getTotalovertimes() {
        return totalovertimes;
    }

    public void setTotalovertimes(Integer totalovertimes) {
        this.totalovertimes = totalovertimes;
    }

    public Integer getTotalleaves() {
        return totalleaves==null?0:totalleaves*60;
    }

    public void setTotalleaves(Integer totalleaves) {
        this.totalleaves = totalleaves;
    }

    public Integer getUnhandledleaves() {
        return unhandledleaves==null?0:unhandledleaves*60;
    }

    public void setUnhandledleaves(Integer unhandledleaves) {
        this.unhandledleaves = unhandledleaves;
    }

    public Integer getActualovertimes() {
        return (totalovertimes==null?0:totalovertimes) - (totalleaves==null?0:totalleaves*60);
    }

//    public void setActualovertimes(Integer actualovertimes) {
//        this.actualovertimes = actualovertimes;
//    }

    public String getNumber() {
        if(userInfo != null) {
            String[] split = userInfo.split("-");
            return split[0];
        }
        return null;
    }

//    public void setNumber(String number) {
//        this.number = number;
//    }

    public String getName() {
        if(userInfo != null) {
            String[] split = userInfo.split("-");
            if(split.length > 1) {
                return  split[1];
            }
        }
        return null;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

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
