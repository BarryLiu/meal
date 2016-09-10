package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TFees;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/2/27.
 */
public class Fees {
    public static final int TYPE_NORMAL = 0; //当天正常工作时间内来上班了
    public static final int TYPE_ONLY_OVERTIME = 1; //当天正常工作时间未来上班，但当天有打卡记录，全部为加班
    public static final int TYPE_ABAENCE = 2; //当天无打卡记录，为旷工
    public static final int TYPE_HOLIDAY = 3; //节假日（节假日如果来全部为加班，无旷工迟到等异常）
    private String day_str;
    private String user_info;
    private String start_time;
    private String end_time;
    private String workovertime;
    private String fee1;
    private String fee2;
    private String fee3;
    private String fee4;
    private String total;
    private String status;
    private int feeType;
    private String dealStatus;

    //yingjing.liu 20160726 add   //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
    private String deptName1;
    private String deptName2;
    //yingjing.liu 20160726 end


    public Fees() {
    }

    public Fees(TFees tFees) {
        day_str = tFees.getDayStr();
        user_info = tFees.getUserInfo();
        start_time = tFees.getStartTime();
        end_time = tFees.getEndTime();
        workovertime = String.valueOf(tFees.getWorkovertime());
        Integer tfee1 = tFees.getFee1()==null?0:tFees.getFee1();
        Integer tfee2 = tFees.getFee2()==null?0:tFees.getFee2();
        Integer tfee3 = tFees.getFee3()==null?0:tFees.getFee3();
        Integer tfee4 = tFees.getFee4()==null?0:tFees.getFee4();
        fee1 = String.valueOf(tfee1);
        fee2 = String.valueOf(tfee2);
        fee3 = String.valueOf(tfee3);
        fee4 = String.valueOf(tfee4);
        total = String.valueOf(tfee1+tfee2+tfee3+tfee4);
        status = tFees.getStatus();
        feeType = tFees.getFeeType();
        dealStatus = tFees.getDealStatus();
    }

    //yingjing.liu 20160726 add   //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
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


    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getDay_str() {
        return day_str;
    }

    public void setDay_str(String day_str) {
        this.day_str = day_str;
    }

    public String getUser_info() {
        return user_info;
    }

    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getWorkovertime() {
        return workovertime;
    }

    public void setWorkovertime(String workovertime) {
        this.workovertime = workovertime;
    }

    public String getFee1() {
        return fee1;
    }

    public void setFee1(String fee1) {
        this.fee1 = fee1;
    }

    public String getFee2() {
        return fee2;
    }

    public void setFee2(String fee2) {
        this.fee2 = fee2;
    }

    public String getFee3() {
        return fee3;
    }

    public void setFee3(String fee3) {
        this.fee3 = fee3;
    }

    public String getFee4() {
        return fee4;
    }

    public void setFee4(String fee4) {
        this.fee4 = fee4;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFeeType() {
        return feeType;
    }

    public void setFeeType(int feeType) {
        this.feeType = feeType;
    }
}
