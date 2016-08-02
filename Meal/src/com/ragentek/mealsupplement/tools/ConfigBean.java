package com.ragentek.mealsupplement.tools;

import java.io.Serializable;

/**
 * Created by zixiao.zhang on 2016/4/20.
 */
public class ConfigBean implements Serializable {
    private static final long serialVersionUID = -232132138423432L;
    private String startDay;
    private String mailPwd; //发送邮件的密码，设置为公司某位员工的邮箱为发送邮箱，那么密码就是该员工的电脑密码，电脑密码是会变的，故在该用户每次登陆的时候记录下该密码

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getMailPwd() {
        return mailPwd;
    }

    public void setMailPwd(String mailPwd) {
        this.mailPwd = mailPwd;
    }
}
