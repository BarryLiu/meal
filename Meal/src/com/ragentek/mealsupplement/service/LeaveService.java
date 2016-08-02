package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TLeave;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/28.
 */
public interface LeaveService {
    public List<TLeave> getBeans(String number, String startDate, String endDate);
    public List<TLeave> getUnhandleLeaves(String startDate, String endDate);
    public List<String> getUnhandleUserEmails(String startDate, String endDate);
}
