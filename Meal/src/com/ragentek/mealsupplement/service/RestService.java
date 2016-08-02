package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TFees;

import java.util.List;

/**
 * Created by yingjing.liu on 2016/7/18.
 */
public interface RestService {

    public List<TFees> getRestFeeses(String startDay , String endDay , Integer []groupIds,String number ,String dayStr );


}
