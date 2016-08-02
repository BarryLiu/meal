package com.ragentek.mealsupplement.listener;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Fees;
import com.ragentek.mealsupplement.json.Kaoqin;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.ldap.LDAPControl;
import com.ragentek.mealsupplement.service.FeeService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by zixiao.zhang on 2016/4/12.
 */
public class UserTimer {
    private static final Logger logger = Logger.getLogger(UserTimer.class);
    private static UserTimer instance;
    private static final long PERIOD = 86400000l; //24*60*60*1000
    private Timer userTimer;
    private TimerTask userTimerTask = new TimerTask() {
        @Override
        public void run() {
            try {
                sync(); //同步用户
                //未打卡的情况在每次导入的时候处理，此处就不做处理了，见KaoQin.saveData()方法(调用handle(startDay,endDay)) -- （导入的可能是多条的数据，但人员是按当天算的，可能导致最近入职的一些人员出现为入职前的旷工，故舍弃这种做法）
                /*
                String today = DateTools.formatDateToString(new Date(), DateTools.FORSTR_DATE); //yyyy/MM/dd'
                System.out.println("UserTime run : today="+today+",holiday="+KaoQin.checkWetherHoliday(today));
                if(!KaoQin.checkWetherHoliday(today)) { //如果今天是非工作日，那么也没人上传打卡记录，今天就不用统计缺勤了
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(new Date());
                    endCalendar.add(Calendar.DAY_OF_MONTH, -1); //减去一天，今天的考勤还没有上传，统计到昨天为止
                    String endDay = DateTools.formatDateToString(endCalendar.getTime(), DateTools.FORSTR_DATE); //yyyy/MM/dd
                    endCalendar.setTime(DateTools.formatStringToDate(endDay, DateTools.FORSTR_DATE));
                    String startDay = SerializeUtil.getStartDay();
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(DateTools.formatStringToDate(startDay, DateTools.FORSTR_DATE));
                    while (startCalendar.getTime().getTime()<=endCalendar.getTime().getTime()) {
                        String dayStr = DateTools.formatDateToString(startCalendar.getTime(), DateTools.FORSTR_DATE);
                        handle(dayStr);
                        startCalendar.add(Calendar.DAY_OF_MONTH, 1); //加上一天
                    }
                    SerializeUtil.setStartDay(today); //下次从今天开始统计
                }*/
                //防止出现员工未入职已经旷工的情况出现，所以当天只同步当天，若那天因为服务器异常导致没有统计，那就手动执行吧 -- 目前系统维护了实际入职日期，不会出现未入职已旷工的情况，故还是导入数据是处理整天未打卡情况
                /*
                String today = DateTools.formatDateToString(new Date(), DateTools.FORSTR_DATE);
                handle(today);*/
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.toString(), e);
            }
        }
    };

    private UserTimer(){}
    public static UserTimer getInstance() {
        if(instance == null) {
            instance = new UserTimer();
        }
        return instance;
    }

    public synchronized void startTimer() {
        if(userTimer == null) {
            userTimer = new Timer();
            userTimer.schedule(userTimerTask, getDelayMillisecond(), PERIOD);
        }
    }

    public synchronized void stopTimer() {
        if(userTimer != null) {
            userTimer.cancel();
            userTimer = null;
        }
    }

    /**
     * 获取当前时间距离最近的晚上11点的时间间隔
     * @return
     */
    private long getDelayMillisecond() {
        long delay = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long curTime = System.currentTimeMillis();
        if(calendar.getTimeInMillis() > curTime) {
            delay = calendar.getTimeInMillis() - curTime;
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            delay = calendar.getTimeInMillis() - curTime;
        }
        return delay;
    }

    /*public static void main(String[] args) throws Exception {
        UserTimer.getInstance().handle();
    }*/

    public synchronized static void sync() throws Exception {
        //同步所有的Ldap用户
        List<TUser> ldapUsers = LDAPControl.getInstance().getAllLadpUser(); //所有ldap user
        for(TUser ldapUser : ldapUsers) {
            String number = ldapUser.getNumber();
            if(!TextUtil.isNullOrEmpty(number)) {
                UserUtil.saveOrUpdateLdapUser(ldapUser);
            }
        }
        UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
        List<TUser> users = userService.getNormalUsers();
        //判断用户是否离职了，如果客户未离职而被标为了离职在上面步骤(UserUtil.saveOrUpdateLdapUser)中已经处理，所以这里只用判断哪些已经离职但没有被标为离职的就行了
        for(int i=0;i<users.size();i++) {
            TUser tUser = users.get(i);
            boolean exist = false;
            String tUserNumber = tUser.getNumber();
            if(tUserNumber != null) { //工号非空
                for(TUser ldapUser : ldapUsers) {
                    if(tUserNumber.equals(ldapUser.getNumber())) {
                        exist = true;
                        break;
                    }
                }
            }
            if(!exist) { //不存在更新
                // yingjing.liu  add  start 20160629  对于 正常考勤 的员工  ldap系统中没有录入其number 也会变为离职，：
                // 这里对其进行一次过滤 ：对于 标为离职前从 LDAP 系统中根据登录账号判断其 用户存不存在，如果不存在这条数据 表示离职 、存在代表刚进来还没有对其录入number
                if(LDAPControl.getInstance().getLadpUser(tUser.getLoginCount())!=null) {  //ldap系统中有他
                    continue;
                }

                tUser.setLeaveDate(DateTools.formatDateToString(new Date(),DateTools.FORSTR_DATE));  //标为离职时间
                tUser.setStat(User.STATUS_LEAVE);
                DBUtils.update(tUser);
                users.remove(i);
                i--;
            }
        }
    }

    /**
     *
     * @param startDay format:yyyy/MM/dd
     * @param endDay format:yyyy/MM/dd
     * @throws Exception
     */
    public synchronized static void handle(String startDay, String endDay) throws Exception {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(DateTools.formatStringToDate(startDay, DateTools.FORSTR_DATE));
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(DateTools.formatStringToDate(endDay, DateTools.FORSTR_DATE));
        while (startCalendar.getTime().getTime()<=endCalendar.getTime().getTime()) {
            String dayStr = DateTools.formatDateToString(startCalendar.getTime(), DateTools.FORSTR_DATE);
            handle(dayStr);
            startCalendar.add(Calendar.DAY_OF_MONTH, 1); //加上一天
        }
    }

    // yingjing.liu 20160628 start 添加对单个用户添加的方法
    /**
     *
     * @param startDay format:yyyy/MM/dd
     * @throws Exception
     */
    public synchronized static void handleOne(String startDay,String number ) throws Exception {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(DateTools.formatStringToDate(startDay, DateTools.FORSTR_DATE));
        Calendar endCalendar = Calendar.getInstance(); 
        while (startCalendar.getTime().getTime()<=endCalendar.getTime().getTime()) {
            String dayStr = DateTools.formatDateToString(startCalendar.getTime(), DateTools.FORSTR_DATE);
            handle(dayStr);
            startCalendar.add(Calendar.DAY_OF_MONTH, 1); //加上一天
        }
    }
    // yingjing.liu 20160628 end


    public synchronized static void unhandle(String startDay, String endDay) throws Exception {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(DateTools.formatStringToDate(startDay, DateTools.FORSTR_DATE));
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(DateTools.formatStringToDate(endDay, DateTools.FORSTR_DATE));
        while (startCalendar.getTime().getTime()<=endCalendar.getTime().getTime()) {
            String dayStr = DateTools.formatDateToString(startCalendar.getTime(), DateTools.FORSTR_DATE);
            unhandle(dayStr);
            startCalendar.add(Calendar.DAY_OF_MONTH, 1); //加上一天
        }
    }

    public synchronized static void unhandle(String dayStr) throws Exception {
        System.out.println("unhandle: " + dayStr);
        if(KaoQin.checkWetherHoliday(dayStr)) {
            return; //非工作日返回
        }
        //存在于fees中但是不存在于kaoqin中的t_fees需要删除，同时需要删除对应的t_leave
        //TODO...
    }

    /**
     *
     * @param dayStr format:yyyy/MM/dd
     * @throws Exception
     */
    public synchronized static void handle(String dayStr) throws Exception {
        //System.out.println("handle: " + dayStr);
        if(KaoQin.checkWetherHoliday(dayStr)) {
            return; //非工作日返回
        }
        /** 为所有用户插入一条记录,以处理当天一次也不打卡的情况，默认打一次卡，打卡时间：08:59:59 **/
        UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
        FeeService feeService = ServiceFactory.getService(ServiceConfig.SERVICE_FEE);
        //List<TUser> users = userService.getNormalUsers();
        List<TUser> users = userService.getAttendanceUsers(dayStr); //获得dayStr时已入职且当前参与考勤的所有员工（不参与考勤的包括已离职的，就不管了）
        for(TUser tUser : users) {
            //System.out.println(tUser.getDisplayName());
            if(!feeService.existFee(tUser.getNumber(), dayStr)) { //当天如果已经有记录不再插入
                TFees tFees = new TFees();
                tFees.setDayStr(dayStr);
                tFees.setUserInfo(tUser.getNumber() + "-" + tUser.getName());
                tFees.setNumber(tUser.getNumber());
                tFees.setName(tUser.getName());
                tFees.setStartTime("06:59:59");
                tFees.setEndTime("06:59:59");
                KaoQin.totalAndSaveFees(tFees);
            }
        }
    }

}
