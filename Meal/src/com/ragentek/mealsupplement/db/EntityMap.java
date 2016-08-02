package com.ragentek.mealsupplement.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class EntityMap {
    private StringBuilder sqls;
    private Map<String, Object> map;

    public EntityMap(String sql) {
        sqls = new StringBuilder(sql);
        map = new HashMap<String, Object>();
    }

    public void append(String sql) {
        sqls.append(sql);
    }

    public String getSql() {
        return sqls.toString();
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Object get(String key) {
        return map.get(key);
    }
}
