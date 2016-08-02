package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TLeave;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/12.
 */
public interface FeeService {
    public boolean existFee(String number, String dayStr);
    public List<TFees> getUnhandleLeaves(String startDate, String endDate);
    public List<String> getUnhandleUserEmails(String startDate, String endDate);
    public List<TFees> getFees(String number, String startDay, String endDay);
    public TFees getFee(String number, String dayStr);
}
