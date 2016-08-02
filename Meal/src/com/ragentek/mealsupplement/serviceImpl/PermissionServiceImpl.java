package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.bean.*;
import com.ragentek.mealsupplement.json.Permission;
import com.ragentek.mealsupplement.service.PermissionService;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/14.
 */
public class PermissionServiceImpl implements PermissionService {
    @Override
    public List<TPermission> getAll() {
        String sql = "select * from t_permission  order by per_value";
        return DBUtils.query(sql, TPermission.class);
    }

    @Override
    public List<TPermission> getNormalAll() {
        String sql = "select * from t_permission where per_type="+ Permission.TYPE_NORMAL;
        return DBUtils.query(sql, TPermission.class);
    }

    @Override
    public List<TPermission> getDirectoryAll() {
        String sql = "select * from t_permission where per_type>"+Permission.TYPE_NORMAL;
        return DBUtils.query(sql, TPermission.class);
    }

    //获得拥有权限permissionId的所有员工
    @Override
    public List<TUser> getPermissionUsers(Long permissionId) {
        String sql = "select u.* from t_user_permission up,t_user u where up.userid=u.id and up.permissionid="+permissionId;
        return DBUtils.query(sql, TUser.class);
    }

    //获得拥有权限permissionId的所有部门
    @Override
    public List<TGroup> getPsermissionGroups(Long permissionId) {
        String sql = "select g.* from t_group_permission gp,t_group g where gp.groupid=g._id and gp.permissionid="+permissionId;
        return DBUtils.query(sql, TGroup.class);
    }

    //获得员工userId拥有的所有权限
    @Override
    public List<TPermission> getUserPermissions(Long userId) {
        String sql = "select p.* from t_user_permission up,t_user u,t_permission p where up.userid=u.id and up.permissionid=p._id and u.id="+userId;
        return DBUtils.query(sql, TPermission.class);
    }

    //获得部门groupId拥有的所有权限
    @Override
    public List<TPermission> getGroupPermissions(Long groupId) {
        String sql = "select p.* from t_group_permission gp,t_group g,t_permission p where gp.groupid=g._id and gp.permissionid=p._id and g._id="+groupId;
        return DBUtils.query(sql, TPermission.class);
    }

    //先删除再插入
    @Override
    public void assignPermissionUsers(final Long permissionId, final Long[] userIds) {
        DBUtils.operator(new DBUtils.DBHandler<Void>() {
            private PreparedStatement pstmt;
            @Override
            public void handle(Connection conn) throws Exception {
                conn.setAutoCommit(false);
                String deleteSql = "delete from t_user_permission where permissionid="+permissionId;
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
                if(userIds != null && userIds.length > 0) {
                    String insertSql = "INSERT INTO t_user_permission(userid, permissionid) VALUES(?,?)";
                    pstmt = conn.prepareStatement(insertSql);
                    for(Long userId : userIds) {
                        pstmt.setObject(1, userId);
                        pstmt.setObject(2, permissionId);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
                conn.commit();
                conn.setAutoCommit(true);
            }
            @Override
            public Void getResult() {
                return null;
            }
        });
    }

    @Override
    public void assignPermissionGroups(final Long permissionId, final Long[] groupIds) {
        DBUtils.operator(new DBUtils.DBHandler<Void>() {
            private PreparedStatement pstmt;
            @Override
            public void handle(Connection conn) throws Exception {
                conn.setAutoCommit(false);
                String deleteSql = "delete from t_group_permission where permissionid="+permissionId;
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
                if(groupIds != null && groupIds.length > 0) {
                    String insertSql = "INSERT INTO t_group_permission(groupid, permissionid) VALUES(?,?)";
                    pstmt = conn.prepareStatement(insertSql);
                    for(Long groupId : groupIds) {
                        pstmt.setObject(1, groupId);
                        pstmt.setObject(2, permissionId);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
                conn.commit();
                conn.setAutoCommit(true);
            }
            @Override
            public Void getResult() {
                return null;
            }
        });
    }

    @Override
    public List<TPermission> getAllPermissionsForUser(Long userid) {
        EntityList lst = new EntityList("select p.* from t_user_permission up, t_permission p where up.permissionid=p._id and up.userid="+userid);
        lst.append(" union ");
        lst.append("select p.* from t_group_permission gp, t_permission p where gp.permissionid=p._id and gp.groupid in (select groupid from t_user_group where userid="+userid+")");
        lst.append(" order by per_value");
        return DBUtils.query(lst, TPermission.class);
    }
}
