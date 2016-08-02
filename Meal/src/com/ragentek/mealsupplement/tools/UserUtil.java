package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TGroup;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.db.bean.TUserGroup;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.GroupService;
import com.ragentek.mealsupplement.service.UserService;

import java.sql.Timestamp;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public class UserUtil {
    public static TUser saveOrUpdateLdapUser(TUser ldapUser) {
        TUser entity = null;
        String number = ldapUser.getNumber();
        if(!TextUtil.isNullOrEmpty(number)) {
            UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
            entity = userService.getByNumber(number);
            if(entity == null) {
                entity = new TUser();
                entity.setDisplayName(ldapUser.getDisplayName());
                entity.setName(ldapUser.getName());
                entity.setDepart(ldapUser.getDepart());
                entity.setNumber(ldapUser.getNumber());
                entity.setLoginPwd(ldapUser.getLoginPwd());
                entity.setLoginCount(ldapUser.getLoginCount());
                entity.setStat(ldapUser.getStat());
                entity.setLastLoginTime(ldapUser.getLastLoginTime());
                entity.setEmail(ldapUser.getEmail());
                long userid = DBUtils.insert(entity);
                entity.setId(userid);
                saveUserGroups(userid, entity.getDepart());
            }else if(!sameUser(ldapUser, entity)) { //更新，不更新状态
                if(!Util.sameString(ldapUser.getDepart(), entity.getDepart())) { //部门不相同，需要更新
                    long userid = entity.getId();
                    String clearSql = "delete from t_user_group where userid="+userid;
                    DBUtils.executeUpdate(clearSql); //清空用户-部门关系
                    saveUserGroups(userid, ldapUser.getDepart());
                }
                entity.setLoginCount(ldapUser.getLoginCount());
                entity.setDisplayName(ldapUser.getDisplayName());
                entity.setName(ldapUser.getName());
                entity.setDepart(ldapUser.getDepart());
                entity.setNumber(ldapUser.getNumber());
                entity.setLoginPwd(ldapUser.getLoginPwd());
                entity.setLastLoginTime(ldapUser.getLastLoginTime());
                entity.setEmail(ldapUser.getEmail());
                DBUtils.update(entity);
                UserCache.getInstance().update(new User(entity));
            }

        }
        return entity;
    }
    private static boolean sameUser(TUser ldapUser, TUser entity) {
        boolean same = true;
        if(!Util.sameString(ldapUser.getDisplayName(), entity.getDisplayName())) {
            same = false;
        } else if(!Util.sameString(ldapUser.getName(), entity.getName())) {
            same = false;
        } else if(!Util.sameString(ldapUser.getDepart(), entity.getDepart())) {
            same = false;
        } else if(!Util.sameString(ldapUser.getNumber(), entity.getNumber())) {
            same = false;
        } else if(!Util.sameString(ldapUser.getLoginPwd(), entity.getLoginPwd())) {
            same = false;
        } else if(!Util.sameString(ldapUser.getEmail(), entity.getEmail())) {
            same = false;
        } else if(!Util.sameString(ldapUser.getLoginCount(), entity.getLoginCount())) {
            same = false;
        } else {
            /** 如果ldapUser的时间大于entity的时间需要更新，这种情况一般登录时出现。NULL认为是最小时间。
             * 这种情况主要用于登录，登录时会获得ldap用户，并将当前时间赋值给ladp用户为最近登录时间，所以这里的判断主要是由于登录也会调用该方法 **/
            long ldapTime = ldapUser.getLastLoginTime() == null ? 0L : ldapUser.getLastLoginTime().getTime();
            long entityTime = entity.getLastLoginTime() == null ? 0L : entity.getLastLoginTime().getTime();
            if(ldapTime > entityTime) {
                same = false;
            }
        }
        //如果该用户当前状态是离职，改为非离职,这种情况理论上不会出现，因为理论上离职了就从ldap删除，无论是登录或者同步用户都需要先从ldap中取到用户才能调用到该方法
        //当然还是有可能执行到下面代码的，那就是在系统中被手动标识为离职但是该用户并没有实际离职（也就是并没有从ldap系统中删除）
        if(entity.getStat() != null && entity.getStat() == User.STATUS_LEAVE) {
            same = false;
            entity.setStat(User.STATUS_NORMAL); //这里已经改变了entity的status，所以更新之前不用再设置status了
            // yingjing.liu add start 20160629     正常了 离职时间就要为空
            entity.setLeaveDate("");
            // yingjing.liu add end 20160629
        }
        return same;
    }
    private static void saveUserGroups(long userid, String departStr) {
        GroupService groupService = ServiceFactory.getService(ServiceConfig.SERVICE_GROUP);
        if(!TextUtil.isNullOrEmpty(departStr)) {
            String[] departs = departStr.split(",");
            System.out.print("length == "+departs.length);
            for(String depart : departs) {
                TGroup tGroup = groupService.getByGroupName(depart);
                long groupid = -1;
                if (tGroup == null) {
                    tGroup = new TGroup();
                    tGroup.setGroupName(depart);
                    groupid = DBUtils.insert(tGroup); //插入部门
                } else {
                    groupid = tGroup.getId();
                }
                TUserGroup tUserGroup = new TUserGroup();
                tUserGroup.setGroupid(groupid);
                tUserGroup.setUserid(userid);
                DBUtils.insert(tUserGroup); //插入用户-部门关系
            }
        }
    }
}
