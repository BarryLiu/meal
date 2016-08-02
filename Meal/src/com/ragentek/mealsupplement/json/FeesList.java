package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TFees;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/2/29.
 */
public class FeesList {
    private List<Fees> list = null;
    private Fees totalFees = null;
    String sDateStr = "";
    String eDateStr = "";
    private int tFee1=0;
    private int tFee2=0;
    private int tFee3=0;
    private int tFee4=0;
    private int tFee5=0;
    private int tOvertime = 0;
    private String status;
    public FeesList() {
        list = new ArrayList<Fees>();
        totalFees = new Fees();
        totalFees.setDay_str("合计");
        totalFees.setEnd_time("");
        totalFees.setUser_info("");
        totalFees.setStart_time(sDateStr + "~" + eDateStr);
        totalFees.setWorkovertime(String.valueOf(tOvertime));
        totalFees.setFee1(String.valueOf(tFee1));
        totalFees.setFee2(String.valueOf(tFee2));
        totalFees.setFee3(String.valueOf(tFee3));
        totalFees.setFee4(String.valueOf(tFee4));
        totalFees.setStatus("");
        list.add(totalFees);
    }
    public List<Fees> getList() {
        return list;
    }
    public void add(TFees fees) {
        //list.add(list.size()-1, fees);
        String dayStr = fees.getDayStr();
        if("".equals(sDateStr)) {
            sDateStr = dayStr;
        } else if(sDateStr.compareTo(dayStr) > 0) {
            sDateStr = dayStr;
        }
        if("".equals(eDateStr)) {
            eDateStr = dayStr;
        } else if(eDateStr.compareTo(dayStr) < 0) {
            eDateStr = dayStr;
        }
        totalFees.setStart_time(sDateStr + "~" + eDateStr);
        tFee1 = tFee1+(fees.getFee1()==null?0:fees.getFee1());
        tFee2 = tFee2+(fees.getFee2()==null?0:fees.getFee2());
        tFee3 = tFee3+(fees.getFee3()==null?0:fees.getFee3());
        tFee4 = tFee4+(fees.getFee4()==null?0:fees.getFee4());
        tOvertime = tOvertime+(fees.getWorkovertime()==null?0:fees.getWorkovertime());
        totalFees.setWorkovertime(String.valueOf(tOvertime));
        totalFees.setFee1(String.valueOf(tFee1));
        totalFees.setFee2(String.valueOf(tFee2));
        totalFees.setFee3(String.valueOf(tFee3));
        totalFees.setFee4(String.valueOf(tFee4));
        totalFees.setStatus(fees.getStatus());
        list.add(list.size()-1, new Fees(fees));
    }

    public void addAll(List<TFees> feesList) {
        for(TFees fees : feesList) {
            add(fees);
        }
    }
}
