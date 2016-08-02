package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TGroup;
import com.ragentek.mealsupplement.db.bean.TPermission;
import com.ragentek.mealsupplement.db.bean.TUser;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/14.
 */
public class Permission extends JsonBean {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_FIRST_LEAVE = 1;
    public static final int TYPE_SECOND_LEAVE = 2;
    private Long id;
    private String perName;
    private Integer perValue;
    private Integer parentValue;
    private Integer perType;
    private String url;
    private String perDesc;
    private Integer isexpand;

    private List<TUser> users; //拥有该权限的所有员工
    private List<TGroup> groups; //拥有该权限的所有部门

    public Permission() {}
    public Permission(TPermission tPermission) {
        id = tPermission.getId();
        perName = tPermission.getPerName();
        perValue = tPermission.getPerValue();
        parentValue = tPermission.getParentValue();
        perType = tPermission.getPerType();
        url = tPermission.getUrl();
        perDesc = tPermission.getPerDesc();
        isexpand = tPermission.getIsexpand();
    }

    @Override
    public TPermission toSqlBean() {
        TPermission tPermission = new TPermission();
        tPermission.setId(id);
        tPermission.setPerName(perName);
        tPermission.setPerValue(perValue);
        tPermission.setParentValue(parentValue);
        tPermission.setPerType(perType);
        tPermission.setUrl(url);
        tPermission.setPerDesc(perDesc);
        tPermission.setIsexpand(isexpand);
        return tPermission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPerName() {
        return perName;
    }

    public void setPerName(String perName) {
        this.perName = perName;
    }

    public Integer getPerValue() {
        return perValue;
    }

    public void setPerValue(Integer perValue) {
        this.perValue = perValue;
    }

    public Integer getParentValue() {
        return parentValue;
    }

    public void setParentValue(Integer parentValue) {
        this.parentValue = parentValue;
    }

    public Integer getPerType() {
        return perType;
    }

    public void setPerType(Integer perType) {
        this.perType = perType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPerDesc() {
        return perDesc;
    }

    public void setPerDesc(String perDesc) {
        this.perDesc = perDesc;
    }

    public Integer getIsexpand() {
        return isexpand;
    }

    public void setIsexpand(Integer isexpand) {
        this.isexpand = isexpand;
    }

    public boolean isExpand() {
        return isexpand!=null&&isexpand==1?true:false;
    }

    public List<TUser> getUsers() {
        return users;
    }

    public void setUsers(List<TUser> users) {
        this.users = users;
    }

    public List<TGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<TGroup> groups) {
        this.groups = groups;
    }
}
