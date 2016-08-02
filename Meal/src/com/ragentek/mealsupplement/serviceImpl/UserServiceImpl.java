package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.EntityMap;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.TextUtil;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class UserServiceImpl implements UserService {
    @Override
    public List<TUser> queryUserList(User pms) {
        String sql = "select * from t_user";
        EntityList list = new EntityList(sql);
        if(pms != null) {
            list.append(" where 1=1");
            String username = pms.getUsername();
            String depart = pms.getDepart();
            if(!TextUtil.isNullOrEmpty(username)) {
                list.append(" and login_count like ?");
                list.add("%"+username+"%");
            }
            if(!TextUtil.isNullOrEmpty(depart)) {
                list.append(" and depart like ?");
                list.add("%"+depart+"%");
            }
            // 根据  用户名 姓名  工号模糊查询  yingjing .liu  add start  用于UserList.jsp 页面的查询  注意这里的列对应
            if(!TextUtil.isNullOrEmpty(pms.getName())) {
                list.append(" and ( login_count like ?");
                list.add("%"+pms.getName()+"%");

                list.append(" or number like ?");
                list.add("%"+pms.getName()+"%");

                list.append(" or name like ? )");
                list.add("%"+pms.getName()+"%");
            }

            // 根据  用户名 姓名  工号模糊查询  add end
            //20160726 yingjing.liu add 添加部门查询的过滤
            if(pms.getDept1()!=null&&pms.getDept1()!=0){
                list.append(" and dept1 = "+pms.getDept1());
            }
            if(pms.getDept2()!=null&&pms.getDept2()!=0){
                list.append(" and dept2 = "+pms.getDept2());
            }
        }
        System.out.println("queryUserList: sql=" + list.getSql());
        return DBUtils.query(list, TUser.class);
    }

    @Override
    public TUser getByNumber(String number) {
        String sql = "select * from t_user where number=?";
        EntityList list = new EntityList(sql);
        list.add(number);
        return DBUtils.uniqueBean(list, TUser.class);
    }

    @Override
    public TUser getById(String id) {
        String sql = "select * from t_user where id=?";
        EntityList list = new EntityList(sql);
        list.add(id);
        return DBUtils.uniqueBean(list, TUser.class);
    }

    @Override
    public List<TUser> getNormalUsers() {
        //String sql = "select * from t_user where stat is null or stat="+User.STATUS_NORMAL+" or stat="+User.STATUS_MANAGER;
        String sql = "select * from t_user where stat !="+User.STATUS_NO_ATTENDANCE;
        return DBUtils.query(sql, TUser.class);
    }

    @Override
    public List<TUser> getAllUsers() {
        String sql = "select * from t_user";
        return DBUtils.query(sql, TUser.class);
    }

    @Override
    public List<TUser> getAttendanceUsers(String startDay) { //是否自动生成未打卡记录异常
        String sql = "select * from t_user where (stat is null or stat="+User.STATUS_NORMAL+" or stat="+User.STATUS_MANAGER+")";
        EntityList lst = new EntityList(sql);
        if(!TextUtil.isNullOrEmpty(startDay)) {
            lst.append(" and enter_date<=?");
            lst.add(startDay);
        }
        return DBUtils.query(lst, TUser.class);
    }

    @Override
    public List<TUser> selectByDeptSecond(Integer secondDeptId) {
        String sql = "select * from t_user where dept2 = "+secondDeptId;
        return DBUtils.query(sql, TUser.class);
    }

    /*
    @Override
    public TUser getbyLoginCount(String loginCount) {
        String sql = "select * from t_user where login_count=?";
        EntityList list = new EntityList(sql);
        list.add(loginCount);
        return DBUtils.uniqueBean(list, TUser.class);
    }
    */


}
