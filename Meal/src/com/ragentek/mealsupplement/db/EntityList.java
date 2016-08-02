package com.ragentek.mealsupplement.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/11.
 */
public class EntityList {
    private StringBuilder sqls;
    private List<Object> lst;

    public EntityList(String sql) {
        sqls = new StringBuilder(sql);
        lst = new ArrayList<Object>();
    }

    public void append(String sql) {
        sqls.append(sql);
    }

    public String getSql() {
        return sqls.toString();
    }

    public int size() {
        return lst.size();
    }

    public Object get(int i) {
        return lst.get(i);
    }

    public void add(Object object) {
        lst.add(object);
    }

    public List<Object> getList() {
        return lst;
    }
}
