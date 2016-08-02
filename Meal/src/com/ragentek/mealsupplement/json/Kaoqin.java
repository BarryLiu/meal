package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TKaoqin;

/**
 * Created by zixiao.zhang on 2016/2/29.
 */
public class Kaoqin {
    private String userid;
    private String names;
    private String depart;
    private String dotimes;
    private String status;

    public Kaoqin() {}
    public Kaoqin(TKaoqin tKaoqin) {
        userid = tKaoqin.getUserid();
        names = tKaoqin.getNames();
        depart = tKaoqin.getDepart();
        dotimes = tKaoqin.getDotimes();
        status = tKaoqin.getStatus();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getDotimes() {
        return dotimes;
    }

    public void setDotimes(String dotimes) {
        this.dotimes = dotimes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
