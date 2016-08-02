package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.bean.TGroup;
import com.ragentek.mealsupplement.service.GroupService;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public class GroupServiceImpl implements GroupService {

    @Override
    public TGroup getByGroupName(String groupName) {
        String sql = "select * from t_group where group_name = ?";
        EntityList list = new EntityList(sql);
        list.add(groupName);
        return DBUtils.uniqueBean(list, TGroup.class);
    }

    @Override
    public List<TGroup> getAll() {
        String sql = "select * from t_group";
        return DBUtils.query(sql, TGroup.class);
    }
}
