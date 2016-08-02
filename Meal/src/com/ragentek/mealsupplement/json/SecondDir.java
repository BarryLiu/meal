package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TPermission;

/**
 * Created by zixiao.zhang on 2016/4/15.
 */
public class SecondDir {
    private String text;
    private String url;
    private Integer tabid;
    public SecondDir(){}
    public SecondDir(TPermission tPermission) {
        text = tPermission.getPerName();
        url = tPermission.getUrl();
        tabid = tPermission.getPerValue();
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Integer getTabid() {
        return tabid;
    }
    public void setTabid(Integer tabid) {
        this.tabid = tabid;
    }
}
