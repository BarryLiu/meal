package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TPermission;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/15.
 */
public class FirstDir {
    private String text;
    private Boolean isexpand;
    private List<SecondDir> children;
    private Integer tabid; //为了让子权限定位而已
    public FirstDir() {}
    public FirstDir(TPermission tPermission) {
        text = tPermission.getPerName();
        isexpand = tPermission.getIsexpand()!=null&&tPermission.getIsexpand()==1?true:false;
        tabid = tPermission.getPerValue();
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Boolean getIsexpand() {
        return isexpand;
    }
    public void setIsexpand(Boolean isexpand) {
        this.isexpand = isexpand;
    }
    public List<SecondDir> getChildren() {
        return children;
    }
    public void setChildren(List<SecondDir> children) {
        this.children = children;
    }
    public Integer getTabid() {
        return tabid;
    }
    public void setTabid(Integer tabid) {
        this.tabid = tabid;
    }
}
