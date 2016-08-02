package com.ragentek.mealsupplement.json;

import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.tools.DateTools;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class User extends JsonBean {
    public static final int STATUS_NORMAL = 0; //正常，参与考勤
    public static final int STATUS_NO_ATTENDANCE = 1; //不参与考勤
    public static final int STATUS_LEAVE = 2; //离职
    public static final int STATUS_MANAGER = 3; //高管
    private Long id;
    private String username;
    private String password;
    private String name;
    private String depart;
    private String number;
    private Integer status;
    private String lastLogin;
    private Integer type;
    private String enterDay; //入职日期，yyyy/MM/dd
    private String email;
    // yingjing.liu 20160629 start add  添加离职日期
    private String leaveDay; //离职日期，yyyy/MM/dd
    // yingjing.liu 20160629 start add  添加离职日期
    // yingjing.liu 20160725 start add  添加离职日期
    private Long dept1;
    private String deptName1;
    private Long dept2;
    private String deptName2;
    // yingjing.liu 20160725 start add  添加离职日期

    public User() {}
    public User(TUser tUser) {
        id = tUser.getId();
        username = tUser.getLoginCount();
        password = tUser.getLoginPwd();
        name = tUser.getName();
        depart = tUser.getDepart();
        number = tUser.getNumber();
        status = tUser.getStat();
        Timestamp lastLoginTime = tUser.getLastLoginTime();
        if(lastLoginTime != null) {
            lastLogin = DateTools.formatDateToString(new Date(lastLoginTime.getTime()), DateTools.FORSTR_DATETIME);
        }
        type = tUser.getUserType();
        enterDay = tUser.getEnterDate();
        email = tUser.getEmail();
        //yingjing.liu add 20160629 add start
        leaveDay = tUser.getLeaveDate();
        //yingjing.liu add 20160629 add start
        dept1 = tUser.getDept1();
        deptName1 = tUser.getDeptName1();
        dept2 = tUser.getDept2();
        deptName2 = tUser.getDeptName2();
    }

    @Override
    public TUser toSqlBean() {
        TUser tUser = new TUser();
        tUser.setId(id);
        tUser.setLoginCount(username);
        tUser.setLoginPwd(password);
        tUser.setName(name);
        tUser.setDepart(depart);
        tUser.setNumber(number);
        tUser.setStat(status);
        if(lastLogin != null) {
            tUser.setLastLoginTime(new Timestamp(DateTools.formatStringToDate(lastLogin, DateTools.FORSTR_DATETIME).getTime()));
        }
        tUser.setUserType(type);
        tUser.setEnterDate(enterDay);
        tUser.setEmail(email);

        //yingjing.liu add 20160629 add start
        tUser.setLeaveDate(leaveDay);
        //yingjing.liu add 20160629 add start
        tUser.setDept1(dept1);
        tUser.setDeptName1(deptName1);
        tUser.setDept2(dept2);
        tUser.setDeptName2(deptName2);
        return tUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isLeave() {
        return status != null && status.intValue() == 1;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isAdmin() {
        return type!=null && type==1;
    }

    public String getEnterDay() {
        return enterDay;
    }

    public void setEnterDay(String enterDay) {
        this.enterDay = enterDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //yingjing.liu add 20160629 add start

    public String getLeaveDay() {
        return leaveDay;
    }

    public void setLeaveDay(String leaveDay) {
        this.leaveDay = leaveDay;
    }

    public Long getDept1() {
        return dept1;
    }

    public void setDept1(Long dept1) {
        this.dept1 = dept1;
    }

    public String getDeptName1() {
        return deptName1;
    }

    public void setDeptName1(String deptName1) {
        this.deptName1 = deptName1;
    }

    public Long getDept2() {
        return dept2;
    }

    public void setDept2(Long dept2) {
        this.dept2 = dept2;
    }

    public String getDeptName2() {
        return deptName2;
    }

    public void setDeptName2(String deptName2) {
        this.deptName2 = deptName2;
    }
    //yingjing.liu add 20160629 add start
}
