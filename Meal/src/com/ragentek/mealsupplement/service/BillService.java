package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TBill;
import com.ragentek.mealsupplement.db.bean.TBillType;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public interface BillService {
    public List<TBill> getAll();
    public List<TBill> getBeans(String number, String billName,String sDate);
    public int deleteMulti(Long[] ids);
    public List<TBill> getByIds(Long[] ids);
    public TBill getById(Long id);
    //查询员工当天所有的单据,不包括未打卡记录单和默认表单
    public List<TBill> getNormalBills(String number,String dayStr);
}
