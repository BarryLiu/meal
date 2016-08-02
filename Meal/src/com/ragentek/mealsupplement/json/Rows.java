package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TUser;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/2/29.
 */
public class Rows {
    private Object Rows;
    public Rows() {}
    public Rows(Object rows) {
        Rows = rows;
    }

    /*
    public Object getRows() {
        return Rows;
    }*/

    public void setRows(Object rows) {
        Rows = rows;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"Rows\":");
        if(Rows == null) {
            json.append("[]");
        } else {
            if(JSONUtils.isArray(Rows)) {
                JSONArray arry = JSONArray.fromObject(Rows);
                json.append(arry.toString());
            } else if(JSONUtils.isObject(Rows)) {
                JSONObject jobj = JSONObject.fromObject(Rows);
                json.append(jobj.toString());
            } else {
                json.append("[]");
            }
        }
        json.append("}");
        return json.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }

    public static String getEmpty() {
        return "{\"Rows\":[]}";
    }

    public <T> List<T> getRowsAsList() {
        List<T> list = new ArrayList<T>();
        Rows = list;
        return list;
    }


//    public static void main(String[] args) {
//        Fees fees = new Fees();
//        fees.setUser_info("zzx");
//
//        Fees fees1 = new Fees();
//        fees1.setUser_info("xx");
//        fees1.setDay_str("2015-10-15");
//
//        Rows rows = new Rows();
//        List<Fees> list = rows.getRowsAsList();
//        list.add(fees);
//        list.add(fees1);
//
//        System.out.println(rows);
//        TUser tUser = new TUser();
//        tUser.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
//        tUser.setLoginCount("zhangzixiao");
//        JSONObject jsonObject = JSONObject.fromObject(tUser);
//        System.out.println(jsonObject.toString());
//    }

}
