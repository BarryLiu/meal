package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TBillType;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public interface BillTypeService {
    public List<TBillType> getAll();
    public boolean existBillName(String billName);
    public int deleteMulti(Long[] ids);
    public TBillType getByBillName(String billName);
}
