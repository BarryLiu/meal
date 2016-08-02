package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.*;
import com.ragentek.mealsupplement.json.Permission;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/14.
 */
public interface PermissionService {
    public List<TPermission> getAll();
    public List<TPermission> getNormalAll();
    public List<TPermission> getDirectoryAll();
    //public List<TGroupPermission> getAllGroupPermission();
    //public List<TUserPermission> getAllUserPermission();

    public List<TUser> getPermissionUsers(Long permissionId); //获得拥有该权限所有员工
    public List<TGroup> getPsermissionGroups(Long permissionId); //获得拥有该权限所有部门
    public List<TPermission> getUserPermissions(Long userId); //获得该员工拥有的所有权限
    public List<TPermission> getGroupPermissions(Long groupId); //获得该部门拥有的所有权限

    public void assignPermissionUsers(Long permissionId, Long[] userIds); //分配权限permissionId给员工userIds：先删除再插入
    public void assignPermissionGroups(Long permissionId, Long[] groupIds); //分配权限permissionId给部门groupIds：先删除再插入

    public List<TPermission> getAllPermissionsForUser(Long userid); //获得该用户的所有权限（包括用户所在的部门拥有的权限）
}
