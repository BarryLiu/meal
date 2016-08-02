package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TGroup;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public interface GroupService {
    public TGroup getByGroupName(String groupName);
    public List<TGroup> getAll();
}
