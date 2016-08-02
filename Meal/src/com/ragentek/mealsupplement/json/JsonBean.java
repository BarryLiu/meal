package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.SqlBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public abstract class JsonBean {
    public abstract  <T>  T toSqlBean();

    public static  <T> List<T> toSqlBeanList(List<JsonBean> beans) {
        List<T> lst = new ArrayList<T>();
        for(JsonBean bean : beans) {
            T t = bean.toSqlBean();
            lst.add(t);
        }
        return lst;
    }
}
