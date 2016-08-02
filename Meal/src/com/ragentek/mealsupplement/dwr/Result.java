package com.ragentek.mealsupplement.dwr;

import net.sf.json.JSONObject;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class Result {
    private boolean res;
    private String msg;
    private Object data;

    public Result(boolean res) {
        this.res = res;
    }
    public Result(boolean res, String msg) {
        this.res = res;
        this.msg = msg;
    }
    public Result(boolean res, Object data) {
        this.res = res;
        this.data = data;
    }
    public Result(boolean res, String msg, Object data) {
        this.res = res;
        this.msg = msg;
        this.data = data;
    }

    public static String success() {
        return success(null);
    }
    public static String success(String msg) {
        Result res = new Result(true, msg);
        return res.toString();
    }
    public static String success(Object data) {
        Result res = new Result(true, data);
        return res.toString();
    }
    public static String error() {
        return error(null);
    }
    public static String error(String msg) {
        Result res = new Result(false, msg);
        return res.toString();
    }
    @Override
    public String toString() {
        JSONObject jsonObject = JSONObject.fromObject(this);
        return jsonObject.toString();
    }

    public boolean isRes() {
        return res;
    }

    public void setRes(boolean res) {
        this.res = res;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
