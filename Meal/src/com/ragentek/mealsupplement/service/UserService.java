package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.User;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public interface UserService {
    public List<TUser> queryUserList(User pms);
    //public TUser getbyLoginCount(String loginCount);
    public TUser getByNumber(String number);
    public TUser getById(String id );
    public List<TUser> getNormalUsers();

    public List<TUser> getAllUsers();

    public List<TUser> getAttendanceUsers(String startDay); //获得startDay（yyyy/MM/dd）时已入职且当前仍参与考勤的所有员工

    public List<TUser> selectByDeptSecond(Integer secondDeptId);
}
