package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.json.Statistic;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/13.
 */
public interface StatisticService {
    /**
     *
     * @param number 学号
     * @param startDay 开始日期，format:yyyy/MM/dd
     * @param endDay 结束日期，format:yyyy/MM/dd
     * @return
     */
    public List<Statistic> getBeans(String number, String startDay, String endDay);
}
