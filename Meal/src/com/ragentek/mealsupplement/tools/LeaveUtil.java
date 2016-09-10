package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TBill;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Bill;
import com.ragentek.mealsupplement.json.Fees;
import com.ragentek.mealsupplement.json.Kaoqin;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.poi.Attachment;
import com.ragentek.mealsupplement.poi.LeaveExport;
import com.ragentek.mealsupplement.service.BillService;
import com.ragentek.mealsupplement.service.FeeService;
import com.ragentek.mealsupplement.service.LeaveService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.servlet.BillServlet;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/24.
 */
public class LeaveUtil {
    private static Logger logger = Logger.getLogger(LeaveUtil.class);
    public static final String TIME_AM_START_1 = "09:06:00"; //上午开始上班时间，正常时间  ---  异常开始时间
    public static final String TIME_AM_START_2 = "09:31:00"; //上午上班时间，前天如果9点后下班，这次是9点半  ---  异常开始时间
    public static final String START_MS1 = "05:59"; //开始上班的分秒时间，前一天晚上9点前下班
    public static final String START_MS2 = "30:59"; //开始上班的分秒时间，前一天晚上9点后下班
    public static final int START_TYPE_1 = 1;
    public static final int START_TYPE_2 = 2;
    public static final String TIME_AM_END = "11:59:59"; //上午结束上班时间
    public static final String TIME_PM_START = "12:46:00"; //下午开始上班时间
    public static final String TIME_PM_END = "17:44:59"; //下午结束上班时间  ---  异常结束时间

    private static final String DEF_BILL_NAME = ""; //普通员工默认表单名称
    private static final int DEF_MANAGER_BILL_TYPE = Bill.TYPE_0; //0-调休
    private static final String DEF_MANAGER_BILL_NAME = "调休（正常）"; //调休正常

    public static final String DEF_ABSENCE_TIME = "06:59:59"; //为旷工的员工自动加入一条t_fees,默认的开始和结束时间

    public static  Integer  cishu =1;                  //  记录 批数  真正邮件一批150为最多 这里以140为最大. 记录发送的人
    /**
     *
     * @param number 工号
     * @param startTimeStr 开始日期，格式可能为yyyy/MM/dd HH:mm:ss或yyyy/MM/dd HH:mm
     * @param endTimeStr 结束日期，格式可能为yyyy/MM/dd HH:mm:ss或yyyy/MM/dd HH:mm
     */
    public static void saveOrUpdateLeave(String number, String startTimeStr, String endTimeStr) {
        //userInfo = tBill.getNumber()+"-"+tBill.getName();
        String startDayStr = getDayStr(startTimeStr);
        String endDayStr = getDayStr(endTimeStr);
        if (startDayStr.compareTo(endDayStr) <= 0) { //开始日期小于等于结束日期才是有效的表单
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(DateTools.formatStringToDate(startDayStr, DateTools.FORSTR_DATE));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(DateTools.formatStringToDate(endDayStr, DateTools.FORSTR_DATE));
            while (startCalendar.getTime().getTime() <= endCalendar.getTime().getTime()) {
                String dayStr = DateTools.formatDateToString(startCalendar.getTime(), DateTools.FORSTR_DATE);
                String sql = "select * from t_fees where user_info like ? and day_str=?";
                EntityList entityList = new EntityList(sql);
                entityList.add(number+"-%");
                entityList.add(dayStr);
                TFees tFees = DBUtils.uniqueBean(entityList, TFees.class); //一天一人最多有一条记录
                if(tFees != null) {
                    saveOrUpdateLeave(tFees);
                }
                startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
    }



    public static void saveOrUpdateLeave(final TFees tFees) {
        String number = tFees.getNumber()==null?(tFees.getUserInfo().split("-")[0]):(tFees.getNumber()); //工号
        final User user = UserCache.getInstance().get(number);
        if(user == null) { //工号在ldap系统中不存在，不录入考勤异常，返回
            System.out.println("Number：" + number + " does not exist in LDAP, return.");
            logger.warn("Number:"+number+" does not exist in LDAP, return.");
            return;
        }
        final List<TLeave> entities = new ArrayList<TLeave>(); //最终需要插入的结果集
        final String dayStr = tFees.getDayStr();
        //String number = tFees.getNumber();
        //String userInfo = tFees.getUserInfo();
        //获得当天的异常：最多2条(早退兼迟到)
        TLeave leave1 = null;
        TLeave leave2 = null;
        String startTime = tFees.getStartTime();
        String endTime = tFees.getEndTime();
        if(KaoQin.isWorkflowToNextDay(endTime)) {
            endTime = KaoQin.add24Hour(endTime);
        }
        String status = "";
        if(!KaoQin.checkWetherHoliday(dayStr, DateTools.FORSTR_DATE)) { //只有工作日才有异常
            status = KaoQin.getStatus(startTime, endTime, KaoQin.getPreXiaban(tFees));
        }
        //System.out.println("saveOrUpdateLeave:startTime="+startTime+",endTime="+endTime+",status="+status);
        if(!"".equalsIgnoreCase(status)) { //没有异常
            int startType = getStartType(tFees);
            String todayStartTime = getStartTime(startType);
            String startMs = getStartMS(startType);
            if("未打卡".equals(status) || "旷工".equals(status)) {
                leave1 = getFromFees(tFees);
                leave1.setStartTime(formatStartTimeStr(todayStartTime));
                leave1.setEndTime(TIME_PM_END);
                leave1.setTotalHours(getTotalHours(leave1.getStartTime(), leave1.getEndTime()));
                leave1.setBillName(DEF_BILL_NAME);
                leave1.setStat(status);
            } else if("迟到".equals(status)) {
                leave1 = getFromFees(tFees);
                leave1.setStartTime(formatStartTimeStr(todayStartTime));
                //modify zhangzixiao 为了解决正式上班9:30,10:30之前来算1个小时异常的情况(后来改为了完全按照分钟来算时间，最终分钟转换为小时，所以此处代码就没必要修改了) start
                leave1.setEndTime(formatEndTimeStr(startTime));
                /*
                if(startTime.compareTo((startTime.split(":")[0]+":"+startMs)) <= 0) {
                    int hour = getHour(startTime);
                    hour--;
                    leave1.setEndTime(formatEndTimeStr(formatTime(hour, 59, 59)));
                } else {
                    leave1.setEndTime(formatEndTimeStr(startTime));
                }*/
                //modify 1110 end
                leave1.setTotalHours(getTotalHours(leave1.getStartTime(), leave1.getEndTime()));
                leave1.setBillName(DEF_BILL_NAME);
                leave1.setStat(status);
            } else if("早退".equals(status)) {
                leave1 = getFromFees(tFees);
                leave1.setStartTime(formatStartTimeStr(endTime));
                leave1.setEndTime(TIME_PM_END);
                leave1.setTotalHours(getTotalHours(leave1.getStartTime(), leave1.getEndTime()));
                leave1.setBillName(DEF_BILL_NAME);
                leave1.setStat(status);
            } else if("迟到|早退".equals(status)) {
                leave1 = getFromFees(tFees);
                leave1.setStartTime(formatStartTimeStr(todayStartTime));
                //modify 1110 start
                leave1.setEndTime(formatEndTimeStr(startTime));
                /*
                if(startTime.compareTo((startTime.split(":")[0]+":"+startMs)) <= 0) {
                    int hour = getHour(startTime);
                    hour--;
                    leave1.setEndTime(formatEndTimeStr(formatTime(hour, 59, 59)));
                } else {
                    leave1.setEndTime(formatEndTimeStr(startTime));
                }
                */
                //modify 1110 end
                leave1.setTotalHours(getTotalHours(leave1.getStartTime(), leave1.getEndTime()));
                leave1.setBillName(DEF_BILL_NAME);
                leave1.setStat("迟到");
                leave2 = getFromFees(tFees);
                leave2.setStartTime(formatStartTimeStr(endTime));
                leave2.setEndTime(TIME_PM_END);
                leave2.setTotalHours(getTotalHours(leave2.getStartTime(), leave2.getEndTime()));
                leave2.setBillName(DEF_BILL_NAME);
                leave2.setStat("早退");
                //如果打卡间隔在一个小时内，如10:20:21-10:22:23，这样算迟到早退，但是迟到早退都包含10点，所以10点算了2次，这样一天相当于异常事件为9小时，这种情况需要处理(目前当天有效上班时间小于一个小时按旷工算，所以这里就不用处理了)
                /*
                int leave1EndHoure = getHour(leave1.getEndTime());
                int leave2StartHour = getHour(leave2.getStartTime());
                if(leave1EndHoure == leave2StartHour) {
                    if(leave1EndHoure == 9) { //按早退处理，早退8个小时
                        leave1 = null;
                    } else if(leave1EndHoure == 17) { //按迟到处理，迟到8个小时
                        leave2 = null;
                    } else { //将早退开始时间推迟1个小时
                        int newLeave2StartHour = leave2StartHour+1;
                        StringBuilder sb = new StringBuilder();
                        if(newLeave2StartHour < 10) {
                            sb.append("0");
                        }
                        sb.append(newLeave2StartHour);
                        sb.append(":00:00");
                    }
                }
                */
            }
        }
        if(leave1 != null) {
            entities.add(leave1);
        }
        if(leave2 != null) {
            entities.add(leave2);
        }
        //add zhangzixiao 20160513 如果是高管类型，那么提前添加一个默认处理的单据（正常调休）。注：如果为旷工则不自动处理，让人事来处理 start
        if(user.getStatus() != null && user.getStatus() == User.STATUS_MANAGER && !("未打卡".equals(status) || "旷工".equals(status))) { //高管
            //System.out.println(user.getUsername()+":"+status);
            List<TBill> billEntities = new ArrayList<TBill>();
            for(TLeave tLeave : entities) {
                Bill bill = new Bill();
                bill.setBillType(DEF_MANAGER_BILL_TYPE);
                bill.setBillName(DEF_MANAGER_BILL_NAME);
                bill.setDepart(user.getDepart());
                bill.setName(tLeave.getName());
                bill.setNumber(tLeave.getNumber());
                /*
                bill.setStartDate(dayStr+" "+tLeave.getStartTime().split(":")[0]+":00"); //yyyy/MM/dd HH:mm
                String tmpEndTime = tLeave.getEndTime();
                int hour = getHour(tmpEndTime);
                if(!(tmpEndTime.split(":")[0]+":00:00").equals(tmpEndTime)) { //判断endTime是否是整点，endTime需要取大于等于endTime且距离最近的一个整点
                    hour++;
                }
                StringBuilder endDay = new StringBuilder();
                endDay.append(dayStr);
                endDay.append(" ");
                if(hour < 10) {
                    endDay.append("0");
                }
                endDay.append(hour);
                endDay.append(":00");
                bill.setEndDate(endDay.toString());
                */
                bill.setStartDate(dayStr+" "+tLeave.getStartTime().substring(0, 5));
                if((tLeave.getEndTime().split(":"))[2].equals("00")) { //秒
                    bill.setEndDate(dayStr+" "+tLeave.getEndTime().substring(0, 5));
                } else {
                    bill.setEndDate(dayStr+" "+addOneMinute(tLeave.getEndTime().substring(0, 5)));
                }
                bill.setDescs("自动生成的"+DEF_MANAGER_BILL_NAME+"表单!");
                TBill tBill = bill.toSqlBean();
                try {
                    calculateBillTotalHours(tBill);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                billEntities.add(tBill);
            }
            if(billEntities.size() > 0) {
                DBUtils.insertAll(billEntities);
            }
        }
        //add zhangzixiao 20160513 end
        //查询该员工当天所有的单据
        BillService billService = ServiceFactory.getService(ServiceConfig.SERVICE_BILL);
        List<TBill> tBillList = billService.getNormalBills(tFees.getNumber(), dayStr);
        if(tBillList.size() > 0) {
            for(TBill tBill : tBillList) {
                Long billId = tBill.getId();
                String billName = tBill.getBillName();
                String startDatetimeStr = tBill.getStartTimeStr(); //yyyy-MM-dd HH:mm
                String endDatetimeStr = tBill.getEndTimeStr(); //yyyy-MM-dd HH:mm
                String startDateStr = getDayStr(startDatetimeStr);
                String endDateStr = getDayStr(endDatetimeStr);
                String billStartTimeStr = null;
                String billEndTimeStr = null;
                if(startDateStr.compareTo(dayStr) < 0) {
                    //billStartTimeStr = "09:00:00";
                    billStartTimeStr = TIME_AM_START_1;
                } else {
                    billStartTimeStr = getTimeStr(startDatetimeStr);
                }
                if(endDateStr.compareTo(dayStr) > 0) {
                    //billEndTimeStr = "18:00:00";
                    billEndTimeStr = TIME_PM_END;
                } else {
                    //modify zhangzixiao 表单精确到分钟，所以默认最大限度的覆盖秒，也就是10:20-10:40，应当做：10:20:00-10:40:59 start
                    //billEndTimeStr = getTimeStr(endDatetimeStr);
                    String  tmpBillEndTimeStr = endDatetimeStr.split(" ")[1];
                    if(tmpBillEndTimeStr.length() == 5) {
                        billEndTimeStr = tmpBillEndTimeStr + ":59";
                    } else {
                        billEndTimeStr = tmpBillEndTimeStr;
                    }
                    //modify zhangzixiao end
                }
                //这里假定一个请假单不会影响2个异常，也就是说同一天迟到早退的情况不会只有一张请假单
                generateTLeaveWithBill(entities, tFees, billId, billName, billStartTimeStr, billEndTimeStr);
                /*
                String leaveStartTimeStr = leave1.getStartTime();
                String leaveEndTimeStr = leave1.getEndTime();
                if(leaveStartTimeStr.compareTo(billStartTimeStr) < 0 && leaveEndTimeStr.compareTo(billStartTimeStr) > 0) {
                    TLeave tLeave1 = getFromFees(tFees);
                    tLeave1.setStartTime(formatStartTimeStr(leaveStartTimeStr));
                    tLeave1.setEndTime(formatEndTimeStr(minusOneSecond(billStartTimeStr)));
                    tLeave1.setTotalHours(getTotalHours(tLeave1.getStartTime(), tLeave1.getEndTime()));
                    entities.add(tLeave1);
                    if(leaveEndTimeStr.compareTo(billEndTimeStr) <= 0) {
                        TLeave tLeave2 = getFromFees(tFees);
                        tLeave2.setStartTime(formatStartTimeStr(billStartTimeStr));
                        tLeave2.setEndTime(formatEndTimeStr(leaveEndTimeStr));
                        tLeave2.setTotalHours(getTotalHours(tLeave2.getStartTime(), tLeave2.getEndTime()));
                        tLeave2.setBillId(billId);
                        entities.add(tLeave2);
                    } else {
                        TLeave tLeave2 = getFromFees(tFees);
                        tLeave2.setStartTime(formatStartTimeStr(billStartTimeStr));
                        tLeave2.setEndTime(formatEndTimeStr(minusOneSecond(billEndTimeStr)));
                        tLeave2.setTotalHours(getTotalHours(tLeave2.getStartTime(), tLeave2.getEndTime()));
                        tLeave2.setBillId(billId);
                        TLeave tLeave3 = getFromFees(tFees);
                        tLeave3.setStartTime(formatStartTimeStr(billEndTimeStr));
                        tLeave3.setEndTime(formatEndTimeStr(leaveEndTimeStr));
                        tLeave3.setTotalHours(getTotalHours(tLeave3.getStartTime(), tLeave3.getEndTime()));
                        entities.add(tLeave2);
                        entities.add(tLeave3);
                    }
                } else if (leaveStartTimeStr.compareTo(billStartTimeStr) >= 0 && leaveEndTimeStr.compareTo(billEndTimeStr) <= 0) {
                    leave1.setBillId(billId);
                    entities.add(leave1);
                } else if (leaveStartTimeStr.compareTo(billEndTimeStr) < 0 && leaveEndTimeStr.compareTo(billEndTimeStr) > 0) {
                    TLeave tLeave1 = getFromFees(tFees);
                    tLeave1.setStartTime(formatStartTimeStr(leaveStartTimeStr));
                    tLeave1.setEndTime(formatEndTimeStr(minusOneSecond(billEndTimeStr)));
                    tLeave1.setTotalHours(getTotalHours(tLeave1.getStartTime(), tLeave1.getEndTime()));
                    tLeave1.setBillId(billId);
                    entities.add(tLeave1);
                    TLeave tLeave2 = getFromFees(tFees);
                    tLeave2.setStartTime(formatStartTimeStr(billEndTimeStr));
                    tLeave2.setEndTime(formatEndTimeStr(leaveEndTimeStr));
                    tLeave2.setTotalHours(getTotalHours(tLeave2.getStartTime(), tLeave2.getEndTime()));
                    entities.add(tLeave2);
                } else {
                    entities.add(leave1);
                }
                */
            }
        }

        /**** 使用统一操作代替，使得删除和插入在同一事务中
        //先删除已经存在的异常
        String deleteLeaveSql = "delete from t_leave where day_str=? and user_info=?";
        EntityList deleteLeaveLst = new EntityList(deleteLeaveSql);
        deleteLeaveLst.add(dayStr);
        deleteLeaveLst.add(tFees.getUserInfo());
        DBUtils.executeUpdate(deleteLeaveLst);
        //再插入新的数据
        if (entities.size() > 0) {
            DBUtils.insertAll(entities);
        }
        */
        //统一操作：先删除在插入
        DBUtils.operator(new DBUtils.DBHandler<Void>() {
            private PreparedStatement pstmt;
            @Override
            public void handle(Connection conn) throws Exception {
                conn.setAutoCommit(false);
                //删除
                String deleteLeaveSql = "delete from t_leave where day_str=? and user_info=?";
                pstmt = conn.prepareStatement(deleteLeaveSql);
                pstmt.setString(1, dayStr);
                pstmt.setString(2, tFees.getUserInfo());
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
                //插入
                if(entities.size() > 0) {
                    TLeave tLeave = entities.get(0);
                    String insertSql = DBUtils.getInsertSql(tLeave);
                    pstmt = conn.prepareStatement(insertSql);
                    for(TLeave bean : entities) {
                        String[] columns = bean.getColumnNames();
                        int index = 1;
                        for(int i=1;i<=columns.length;i++) {
                            String column = columns[i-1];
                            if(!column.equals(bean.getPrimaryKeyColumnName())) {
                                String getMethodName = DBUtils.getGetMethodName(column);
                                Method getMethod = bean.getClass().getMethod(getMethodName);
                                Object obj = getMethod.invoke(bean);
                                pstmt.setObject(index++, obj);
                            }
                        }
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                    pstmt.close();
                    pstmt = null;
                }
                //自动删除默认的表单
                String deleteDefBillSql = "delete from t_bill where start_time_str like '"+dayStr+" %' and bill_type="+Bill.TYPE_INVALID+" and number="+tFees.getNumber();
                pstmt = conn.prepareStatement(deleteDefBillSql);
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
                //自动加入默认的表单
                List<TBill> billEntities = new ArrayList<TBill>();
                for(TLeave tLeave : entities) {
                    if(tLeave.getBillId()==null || tLeave.getBillId()<=0) { //没有表单的异常数据自动生成默认表单
                        //此处不去查用户表了，所以用户的depart信息没有设置，另外也没有计算总时间故total_hours也没有设置
                        Bill bill = new Bill();
                        bill.setBillType(Bill.TYPE_INVALID);
                        bill.setBillName(Bill.BILL_NAME_INVALID);
                        bill.setDepart(user.getDepart());
                        bill.setName(tLeave.getName());
                        bill.setNumber(tLeave.getNumber());
                        /*
                        bill.setStartDate(dayStr+" "+tLeave.getStartTime().split(":")[0]+":00"); //yyyy/MM/dd HH:mm
                        String endTime = tLeave.getEndTime();
                        int hour = getHour(endTime);
                        if(!(endTime.split(":")[0]+":00:00").equals(endTime)) { //判断endTime是否是整点，endTime需要取大于等于endTime且距离最近的一个整点
                            hour++;
                        }
                        StringBuilder endDay = new StringBuilder();
                        endDay.append(dayStr);
                        endDay.append(" ");
                        if(hour < 10) {
                            endDay.append("0");
                        }
                        endDay.append(hour);
                        endDay.append(":00");
                        bill.setEndDate(endDay.toString());*/
                        bill.setStartDate(dayStr+" "+tLeave.getStartTime().substring(0, 5));
                        if((tLeave.getEndTime().split(":"))[2].equals("00")) { //秒
                            bill.setEndDate(dayStr+" "+tLeave.getEndTime().substring(0, 5));
                        } else {
                            bill.setEndDate(dayStr+" "+addOneMinute(tLeave.getEndTime().substring(0, 5)));
                        }
                        bill.setDescs("自动生成的表单!");
                        TBill tBill = bill.toSqlBean();
                        calculateBillTotalHours(tBill);
                        billEntities.add(tBill);
                    }
                }
                if(billEntities.size() > 0) {
                    String insertBillSql = "INSERT INTO t_bill(bill_name, start_time, end_time, start_time_str, end_time_str, name, number, bill_type, total_hours, descs) values(?,?,?,?,?,?,?,?,?,?)";
                    pstmt = conn.prepareStatement(insertBillSql);
                    for(TBill tBill : billEntities) {
                        pstmt.setString(1, tBill.getBillName());
                        pstmt.setTimestamp(2, tBill.getStartTime());
                        pstmt.setTimestamp(3, tBill.getEndTime());
                        pstmt.setString(4, tBill.getStartTimeStr());
                        pstmt.setString(5, tBill.getEndTimeStr());
                        pstmt.setString(6, tBill.getName());
                        pstmt.setString(7, tBill.getNumber());
                        pstmt.setInt(8, tBill.getBillType());
                        pstmt.setInt(9, tBill.getTotalHours());
                        pstmt.setString(10, tBill.getDescs());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                    pstmt.close();
                    pstmt = null;
                }
                conn.commit();
                conn.setAutoCommit(true); //conn.setAutoCommit(true)会导致提交,有了此条语句，正常来说conn.commit()是可以省略的
            }
            @Override
            public void handleFinally() throws Exception {
                if(pstmt != null) pstmt.close();
            }
            @Override
            public void handleException(Connection conn) throws Exception {
                conn.rollback();
                conn.setAutoCommit(true);
            }
            @Override
            public Void getResult() {
                return null;
            }
        });
    }

    /**
     *
     * @param entities 结果集，处理该表单前当天所有的异常时间段，该表单处理后会更新entities结果集
     * @param tFees 只是用于生成结果对象TLeave，TLeave的很多属性来自于tFees
     * @param billId 表单id
     * @param billStartTimeStr 表单开始时间,已经格式化为HH:mm:ss
     * @param billEndTimeStr 表单结束时间,已经格式化为HH:mm:ss
     */
    private static void generateTLeaveWithBill(List<TLeave> entities, TFees tFees, Long billId, String billName, String billStartTimeStr, String billEndTimeStr) {
        //System.out.println("generateTLeaveWithBill,billStartTimeStr="+billStartTimeStr+",billEndTimeStr="+billEndTimeStr);
        //处理下假单不是整小时的情况，因为假单都是以整点为单位的(16:00:00-17:45:00)->(16:00:00-17:45:00) 这是不对的，故以下代码应该删除
        //以下代码主要是处理的异常情况为：请假单为09;00:00-09:40:00，可能出现请假09;00:00-09:40:00共1个小时,旷工09:40:41-17:44:59共8个小时的一天9小时的情况
        //所以以下的处理是有必要的，但不是所有的情况都有必要，而是要分情况
        //1. billStartTimeStr>leaveStartTimeStr  需要对billStartTimeStr进行处理
        //2. billEndTimeStr<leaveEndTimeStr 需要对billEndTimeStr做处理
        /*
        String tmpBillStartTimeStr = billStartTimeStr.split(":")[0]+":00:00"; //16:00:00
        if(billStartTimeStr.compareTo(tmpBillStartTimeStr) > 0) {
            //加上1个小时
            int tmpHour = getHour(billStartTimeStr);
            tmpHour++;
            if(tmpHour < 10) {
                //billStartTimeStr = "0"+tmpHour+":00:00";
                tmpBillStartTimeStr = "0"+tmpHour+":00:00";
            } else {
                //billStartTimeStr = tmpHour+":00:00";
                tmpBillStartTimeStr = "0"+tmpHour+":00:00";
            }
        }
        String tmpBillEndTimeStr = billEndTimeStr.split(":")[0]+":00:00"; //17:45:00
        if(billEndTimeStr.compareTo(tmpBillEndTimeStr) > 0) {
            //billEndTimeStr = tmpBillEndTimeStr;
        }
        */

        List<TLeave> tmpEntities = new ArrayList<TLeave>(); //存放本表单处理后生成的TLeave，不能直接放到entities中，因为entities需要遍历
        for(int i=0;i<entities.size();i++) {
            TLeave leave = entities.get(i);
            String status = leave.getStat();
            String leaveStartTimeStr = leave.getStartTime();
            String leaveEndTimeStr = leave.getEndTime();
            /**
             * 第1种情况，当天的时间会被分成3段，生成3个新TLeave
             *表单        billStartTimeStr--------billEndTimeStr
             *异常    leaveStartTimeStr----------------leaveEndTimeStr
             * [leaveStartTimeStr,billStartTimeStr)  [billStartTimeStr,billEndTimeStr]  (billEndTimeStr, leaveEndTimeStr]
             *    第一种情况还包含一种情况如下，当天的时间被分成2段，生成2个新TLeave
             *    表单        billStartTimeStr--------billEndTimeStr
             *    异常    leaveStartTimeStr--------leaveEndTimeStr
             * [leaveStartTimeStr, billStartTimeStr)  [billStartTimeStr, leaveEndTimeStr]
             * 第2种情况，表单包含当天异常时间，则当天异常时间直接连接到该表单，不需要分割，最终返回原1个TLeave
             *表单        billStartTimeStr-----------billEndTimeStr
             *异常          leaveStartTimeStr----leaveEndTimeStr
             * [leaveStartTimeStr, leaveEndTimeStr]
             * 第3种情况，当天实际被分成2段，生成2个新TLeave
             *表单        billStartTimeStr--------billEndTimeStr
             *异常            leaveStartTimeStr--------leaveEndTimeStr
             * [leaveStartTimeStr,billEndTimeStr]  (billEndTimeStr,leaveEndTimeStr]
             */
            if(leaveStartTimeStr.compareTo(billStartTimeStr) < 0 && leaveEndTimeStr.compareTo(billStartTimeStr) > 0) {
                //billStartTimeStr = tmpBillStartTimeStr;
                TLeave tLeave1 = getFromFees(tFees);
                tLeave1.setStartTime(formatStartTimeStr(leaveStartTimeStr));
                tLeave1.setEndTime(formatEndTimeStr(minusOneSecond(billStartTimeStr)));
                tLeave1.setTotalHours(getTotalHours(tLeave1.getStartTime(), tLeave1.getEndTime()));
                tLeave1.setBillName(DEF_BILL_NAME);
                tLeave1.setStat(status);
                tmpEntities.add(tLeave1);
                if(leaveEndTimeStr.compareTo(billEndTimeStr) <= 0) {
                    TLeave tLeave2 = getFromFees(tFees);
                    tLeave2.setStartTime(formatStartTimeStr(billStartTimeStr));
                    tLeave2.setEndTime(formatEndTimeStr(leaveEndTimeStr));
                    tLeave2.setTotalHours(getTotalHours(tLeave2.getStartTime(), tLeave2.getEndTime()));
                    tLeave2.setBillId(billId);
                    tLeave2.setBillName(billName);
                    tLeave2.setStat(status);
                    tmpEntities.add(tLeave2);
                } else {
                    //billEndTimeStr = tmpBillEndTimeStr;
                    TLeave tLeave2 = getFromFees(tFees);
                    tLeave2.setStartTime(formatStartTimeStr(billStartTimeStr));
                    //tLeave2.setEndTime(formatEndTimeStr(minusOneSecond(billEndTimeStr)));
                    tLeave2.setEndTime(formatEndTimeStr(billEndTimeStr));
                    tLeave2.setTotalHours(getTotalHours(tLeave2.getStartTime(), tLeave2.getEndTime()));
                    tLeave2.setBillId(billId);
                    tLeave2.setBillName(billName);
                    tLeave2.setStat(status);
                    TLeave tLeave3 = getFromFees(tFees);
                    //tLeave3.setStartTime(formatStartTimeStr(billEndTimeStr));
                    tLeave3.setStartTime(formatStartTimeStr(addOneSecond(billEndTimeStr)));
                    tLeave3.setEndTime(formatEndTimeStr(leaveEndTimeStr));
                    tLeave3.setTotalHours(getTotalHours(tLeave3.getStartTime(), tLeave3.getEndTime()));
                    tLeave3.setBillName(DEF_BILL_NAME);
                    tLeave3.setStat(status);
                    tmpEntities.add(tLeave2);
                    tmpEntities.add(tLeave3);
                }
            } else if (leaveStartTimeStr.compareTo(billStartTimeStr) >= 0 && leaveEndTimeStr.compareTo(billEndTimeStr) <= 0) {
                leave.setBillId(billId);
                leave.setBillName(billName);
                tmpEntities.add(leave);
            } else if (leaveStartTimeStr.compareTo(billEndTimeStr) < 0 && leaveEndTimeStr.compareTo(billEndTimeStr) > 0) {
                //billEndTimeStr = tmpBillEndTimeStr;
                TLeave tLeave1 = getFromFees(tFees);
                tLeave1.setStartTime(formatStartTimeStr(leaveStartTimeStr));
                //tLeave1.setEndTime(formatEndTimeStr(minusOneSecond(billEndTimeStr)));
                tLeave1.setEndTime(formatEndTimeStr(billEndTimeStr));
                tLeave1.setTotalHours(getTotalHours(tLeave1.getStartTime(), tLeave1.getEndTime()));
                tLeave1.setBillId(billId);
                tLeave1.setBillName(billName);
                tLeave1.setStat(status);
                tmpEntities.add(tLeave1);
                TLeave tLeave2 = getFromFees(tFees);
                //tLeave2.setStartTime(formatStartTimeStr(billEndTimeStr));
                tLeave2.setStartTime(formatStartTimeStr(addOneSecond(billEndTimeStr)));
                tLeave2.setEndTime(formatEndTimeStr(leaveEndTimeStr));
                tLeave2.setTotalHours(getTotalHours(tLeave2.getStartTime(), tLeave2.getEndTime()));
                tLeave2.setBillName(DEF_BILL_NAME);
                tLeave2.setStat(status);
                tmpEntities.add(tLeave2);
            } else {
                tmpEntities.add(leave);
            }
        }
        if(tmpEntities.size() > 0) {
            entities.clear();
            entities.addAll(tmpEntities);
        }
    }

    //减去1毫秒,timeStr：HH:mm:ss
    public static String minusOneSecond(String timeStr) {
        StringBuilder sb = new StringBuilder();
        String[] split = timeStr.split(":");
        int second = Integer.parseInt(split[2]);
        if(second>0) {
            second = second-1;
            sb.append(timeStr.substring(0, 6));
            if(second < 10) {
                sb.append("0");
            }
            sb.append(second);
        } else {
            int minute = Integer.parseInt(split[1]);
            if(minute > 0) {
                minute = minute-1;
                sb.append(timeStr.substring(0,3));
                if(minute < 10) {
                    sb.append("0");
                }
                sb.append(minute);
                sb.append(":59");
            } else {
                int hour = Integer.parseInt(split[0]);
                if(hour > 0) {
                    hour = hour-1;
                    if(hour < 10) {
                        sb.append("0");
                    }
                    sb.append(hour);
                    sb.append(":59:59");
                }
            }
        }
        return sb.toString();
    }

    //加上1毫秒，timeStr:HH:mm:ss
    public static String addOneSecond(String timeStr) {
        String[] split = timeStr.split(":");
        int hour = Integer.parseInt(split[0]);
        int minute = Integer.parseInt(split[1]);
        int second = Integer.parseInt(split[2]);
        if(second == 59) {
            if(minute == 59) {
                second = 0;
                minute = 0;
                hour ++;
            } else {
                second = 0;
                minute++;
            }
        } else {
            second ++;
        }
        return formatTime(hour, minute, second);
    }

    //加上1分钟，timeStr-> HH:mm
    public static String addOneMinute(String timeStr) {
        String[] split = timeStr.split(":");
        int hour = Integer.parseInt(split[0]);
        int minute = Integer.parseInt(split[1]);
        if(minute == 59) {
            minute = 0;
            hour ++;
        } else {
            minute ++;
        }
        return formatTime(hour, minute);
    }

    public static String formatStartTimeStr(String startTimeStr) {
        if(startTimeStr.compareTo(TIME_AM_END) >= 0 && startTimeStr.compareTo(TIME_PM_START) <= 0) {
            return TIME_PM_START;
        }
        return startTimeStr;
    }

    public static String formatEndTimeStr(String endTimeStr) {
        if(endTimeStr.compareTo(TIME_AM_END) >= 0 && endTimeStr.compareTo(TIME_PM_START) <= 0) {
            return TIME_AM_END;
        }
        return endTimeStr;
    }

    private static String formatTime(int hour, int minute, int second) {
        StringBuilder sb = new StringBuilder(); //HH:mm:ss
        if(hour < 10) {
            sb.append("0");
        }
        sb.append(hour);
        sb.append(":");
        if(minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        sb.append(":");
        if(second < 10) {
            sb.append("0");
        }
        sb.append(second);
        return sb.toString();
    }

    private static String formatTime(int hour, int minute) {
        StringBuilder sb = new StringBuilder(); //HH:mm:ss
        if(hour < 10) {
            sb.append("0");
        }
        sb.append(hour);
        sb.append(":");
        if(minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        return sb.toString();
    }

    public static String getDayStr(String datetimeStr) { //2015/03/03 12:22
        return datetimeStr.split(" ")[0];
    }

    public static String getTimeStr(String datetimeStr) {
        String timeStr = datetimeStr.split(" ")[1];
        if(timeStr.length() == 5) { //HH:mm
            timeStr += ":00";
        }
        return timeStr;
    }

    public static TLeave getFromFees(TFees tFees) {
        TLeave leave = new TLeave();
        leave.setDayStr(tFees.getDayStr());
        leave.setNumber(tFees.getNumber() == null ? (tFees.getUserInfo().split("-")[0]) : (tFees.getNumber()));
        leave.setUserInfo(tFees.getUserInfo());
        leave.setName(tFees.getUserInfo().split("-")[1]);
        return leave;
    }

    public static int getStartType(TFees tFees) {
        int startType = START_TYPE_1;
        String preXiaBan = KaoQin.getPreXiaban(tFees);
        if(KaoQin.isWorkflowToNextDay(preXiaBan)) {
            preXiaBan = KaoQin.add24Hour(preXiaBan);
        }
        if(preXiaBan!=null && preXiaBan.compareTo("20:59:59") > 0) {
            startType = START_TYPE_2;
        } else {
            startType = START_TYPE_1;
        }
        return startType;
    }

    public static String getStartTime(int startType) {
        if(startType == START_TYPE_2) {
            return TIME_AM_START_2;
        } else {
            return TIME_AM_START_1;
        }
    }

    public static String getStartMS(int startType) {
        if(startType == START_TYPE_2) {
            return START_MS2;
        } else {
            return START_MS1;
        }
    }

    //timeStr format -- HH:mm:ss
    public static int getSeconds(String timeStr) {
        String[] split = timeStr.split(":");
        int hour = Integer.parseInt(split[0]);
        int minute = Integer.parseInt(split[1]);
        int second = Integer.parseInt(split[2]);
        return hour*3600 + minute*60 + second;
    }

    public static int getDiffHours(int differSeconds) {
        int differHours = 0;
        if(differSeconds > 0) {
            differHours = differSeconds / 3600;
            if(differSeconds % 3600 != 0) {
                differHours ++;
            }
        }
        return differHours;
    }

    //获得工作时间内的工作时长，单位：毫秒
    public static int getDiffSeconds(String startTime, String endTime) {
        int diffSeconds = 0;
        //去掉中间的45分钟
        if(startTime.compareTo(TIME_AM_END)<=0 && endTime.compareTo(TIME_PM_START)>=0) { //完整包含中午45分钟
            int startSeconds = getSeconds(startTime);
            int amEndSeconds = getSeconds(TIME_AM_END);
            int pmStartSeconds = getSeconds(TIME_PM_START);
            int endSeconds = getSeconds(endTime);
            diffSeconds = (amEndSeconds-startSeconds) + (endSeconds-pmStartSeconds);
        } else if(startTime.compareTo(TIME_PM_START)>=0 || endTime.compareTo(TIME_AM_END)<=0) { //不包含中午45分钟
            int startSeconds = getSeconds(startTime);
            int endSeconds = getSeconds(endTime);
            diffSeconds = endSeconds - startSeconds;
        } else { //部分包含中午45分钟，也就是startTime或者endTime在中午45分钟之内
            if(startTime.compareTo(TIME_PM_START) < 0) {
                startTime = TIME_PM_START;
            }
            if(endTime.compareTo(TIME_AM_END) > 0) {
                endTime = TIME_AM_END;
            }
            int startSeconds = getSeconds(startTime);
            int endSeconds = getSeconds(endTime);
            diffSeconds = endSeconds - startSeconds;
        }
        return diffSeconds;
    }

    //startTime,EndTime --> format:HH:mm:ss
    public static int getTotalHours(String startTime, String endTime) {
        /*
        int total = 0;
        int startHour = getHour(startTime);
        int endHour = getHour(endTime);
        if(endHour < 12 || startHour>=13) { //仅仅上午或仅仅下午
            return endHour-startHour+1;
        } else {
            return  endHour-startHour;
        }
        */
        int totalHours = getDiffHours(getDiffSeconds(startTime, endTime));
        if(totalHours == 0) {
            totalHours = 1;
        }
        return totalHours;
    }

    public static int getHour(String time) {
        int hour = 0;
        String[] split = time.split(":");
        if(split.length == 3) {
            hour = Integer.parseInt(split[0]);
        }
        return hour;
    }

    public static int getMinute(String time) {
        int minute = 0;
        String[] split = time.split(":");
        if(split.length == 3) {
            minute = Integer.parseInt(split[1]);
        }
        return minute;
    }

    public static int getSecond(String time) {
        int second = 0;
        String[] split = time.split(":");
        if(split.length == 3) {
            second = Integer.parseInt(split[2]);
        }
        return second;
    }

    /**
     * 针对2016/02/21 03:00:01 -- 2016/02/22 04:03:21的情况,此处会重新计算2016/02/21和2016/02/22两天，实际应该计算2016/02/20和2016/02/21两天
     * 也就是可能出现少计算1天以及多计算1天的情况
     * 但本系统认为类似这种情况不会出现，而多计算一天也无任何影响，所以目前就这样啦
     * 注：本方法是从餐补表开始重新计算餐补表，如果是删除未打卡记录的，必须从考勤表考试重新计算，使用方法 KaoQin$handleForgetClock()
     * @param number 工号
     * @param startTimeStr 开始日期，格式可能为yyyy/MM/dd HH:mm:ss或yyyy/MM/dd HH:mm
     * @param endTimeStr 结束日期，格式可能为yyyy/MM/dd HH:mm:ss或yyyy/MM/dd HH:mm
     */
    public static void handleForgetClock(String number, String startTimeStr, String endTimeStr) {
        //userInfo = tBill.getNumber()+"-"+tBill.getName();
        String startDayStr = getDayStr(startTimeStr);
        String endDayStr = getDayStr(endTimeStr);
        if (startDayStr.compareTo(endDayStr) <= 0) { //开始日期小于等于结束日期才是有效的表单
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(DateTools.formatStringToDate(startDayStr, DateTools.FORSTR_DATE));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(DateTools.formatStringToDate(endDayStr, DateTools.FORSTR_DATE));
            while (startCalendar.getTime().getTime() <= endCalendar.getTime().getTime()) {
                String dayStr = DateTools.formatDateToString(startCalendar.getTime(), DateTools.FORSTR_DATE);
                String sql = "select * from t_fees where user_info like ? and day_str=?";
                EntityList entityList = new EntityList(sql);
                entityList.add(number+"-%");
                entityList.add(dayStr);
                TFees tFees = DBUtils.uniqueBean(entityList, TFees.class); //一天一人最多有一条记录
                if(tFees != null) {
                    try {
                        KaoQin.totalAndSaveFees(tFees);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        logger.error(e.toString(), e);
                    }
                }
                startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
    }

    /**
     * 处理忘记打卡的情况
     * @param tFees
     */
    public static void handleForgetClock(TFees tFees) {
        String dayStr = tFees.getDayStr();
        String startTime = tFees.getStartTime(); //HH:mm:ss
        String endTime = tFees.getEndTime(); //HH:mm:ss
        String nextDay = addDay(dayStr, 1);
        //本天的考勤是从 dayStr 06:00:59到nextDay 06:00:59，如2016/03/21 06:00:59 -- 2016/03/22 06:00:59为2016/03/21的考勤时间
        String startDatetime = dayStr+" 06:01"; //单据表单时间格式为： yyyy/MM/dd HH:mm
        String endDatetime = nextDay+" 06:00"; //单据表单时间格式为： yyyy/MM/dd HH:mm
        String sql = "select * from t_bill " +
                "where (start_time_str<'"+startDatetime+"' and end_time_str>'"+startDatetime+"' or start_time_str>'"+startDatetime+"' and start_time_str<'"+endDatetime+"')" +
                " and number='"+tFees.getNumber()+"' and bill_type="+ Bill.TYPE_3;
        List<TBill> tBillList = DBUtils.query(sql, TBill.class);
        String startTimeStr = dayStr+" "+startTime; //实际上班时间
        String endTimeStr = null; //实际下班时间
        if(KaoQin.isWorkflowToNextDay(endTime)) {
            endTimeStr = nextDay+" "+endTime;
        } else {
            endTimeStr = dayStr+" "+endTime;
        }
        if(tBillList!=null && tBillList.size()>0) {
            for(TBill tBill : tBillList) {
                String tmpStart = tBill.getStartTimeStr();
                //add zhangzixiao 20160620 start
                if("09:06".equals(tmpStart.substring(11))) { //避免出现06:00-06:00的异常
                    tmpStart = tmpStart.substring(0, 11)+"09:05";
                }
                //add zhangzixiao 20160620 end
                String tmpEnd = tBill.getEndTimeStr();
                if(tmpStart.compareTo(startDatetime) < 0) {
                    tmpStart = startDatetime;
                }
                if(tmpEnd.compareTo(endDatetime) > 0) {
                    tmpEnd = endDatetime;
                }
                if(startTimeStr == null) {
                    startTimeStr = tmpStart;
                } else if(startTimeStr.compareTo(tmpStart) > 0){ //如果实际上班时间晚于未打卡单开始时间，那么将实际上班时间改为未打卡单开始时间
                    startTimeStr = tmpStart;
                }
                if(endTimeStr == null) {
                    endTimeStr = tmpEnd;
                } else if(endTimeStr.compareTo(tmpEnd) < 0) { //如果实际下班时间早于未打卡单结束时间，将实际下班时间改为未打卡单结束时间
                    endTimeStr = tmpEnd;
                }
            }
        }
        tFees.setStartTime(getTimeStr(startTimeStr));
        tFees.setEndTime(getTimeStr(endTimeStr));
    }

    public static String addDay(String dayStr, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTools.formatStringToDate(dayStr, DateTools.FORSTR_DATE));
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
    }

    public static void calculateBillTotalHours(TBill tBill) throws ParseException {
        int totalHour = 0;
        Timestamp startDateTime = tBill.getStartTime();
        Timestamp endDateTime = tBill.getEndTime();
        if(startDateTime != null && endDateTime != null && endDateTime.getTime()>startDateTime.getTime()) {
            String startDay = getDayStr(tBill.getStartTimeStr());
            String endDay = getDayStr(tBill.getEndTimeStr());
            Calendar startDayCalendar = Calendar.getInstance();
            startDayCalendar.setTime(DateTools.formatStringToDate(startDay, DateTools.FORSTR_DATE));
            Calendar endDayCalendar = Calendar.getInstance();
            endDayCalendar.setTime(DateTools.formatStringToDate(endDay, DateTools.FORSTR_DATE));
            int day = 0; //时间包含几天，如2016-06-11到2016-06-11是包含1天，2016-06-11 13:00到2016-06-11 10:11是包含2天
            while (startDayCalendar.compareTo(endDayCalendar) <= 0) {
                String dayStr = DateTools.formatDateToString(startDayCalendar.getTime(), DateTools.FORSTR_DATE);
                if(!KaoQin.checkWetherHoliday(dayStr)) { //非工作日
                    ++day;
                }
                startDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            //System.out.println("totalDay="+day);
            String startTime = getTimeStr(tBill.getStartTimeStr()); //HH:mm:ss
            String endTime = getTimeStr(tBill.getEndTimeStr()); //HH:mm:ss
            //从代码逻辑来说下面的计算中应该考虑下startDay或endDay为非工作日的情况，但实际情况中假单不会以非工作日开头和结尾，暂不考虑
            if(tBill.getBillType() == Bill.TYPE_3) { //考勤补充，全部时间
                if(day > 1) {
                    endTime = KaoQin.add24Hour(endTime);
                    if(day > 2) {
                        totalHour += (day-2)*24;
                    }
                }
                //modify by zhangzixiao 20160620 start
                //totalHour += (getHour(endTime)-getHour(startTime));
                int startSeconds = getSeconds(startTime);
                int endSeconds = getSeconds(endTime);
                int diffSeconds = endSeconds - startSeconds;
                totalHour += getDiffHours(diffSeconds);
                //modify by zhangzixiao 20160620 end
            } else {
                if(startTime.compareTo(TIME_AM_START_1) < 0) {
                    startTime = TIME_AM_START_1;
                }
                if(endTime.compareTo(TIME_PM_END) > 0) {
                    endTime = TIME_PM_END;
                }
                if(day == 1 && startTime.compareTo(endTime) < 0) {
                    totalHour += getTotalHours(formatStartTimeStr(startTime), minusOneSecond(formatEndTimeStr(endTime)));
                } else if(day > 1) {
                    //startTime -- TIME_PM_END  && TIME_AM_START_1 -- endTime
                    if(startTime.compareTo(LeaveUtil.TIME_PM_END) < 0) {
                        totalHour += getTotalHours(formatStartTimeStr(startTime), TIME_PM_END);
                    }
                    if(TIME_AM_START_1.compareTo(endTime) < 0) {
                        totalHour += getTotalHours(TIME_AM_START_1, minusOneSecond(formatEndTimeStr(endTime)));
                    }
                    if(day > 2) {
                        totalHour += (day-2)*8;
                    }
                }
            }
            if(totalHour == 0) {
                totalHour = 1;
            }
        } else {
            totalHour = 0;
        }
        tBill.setTotalHours(totalHour);
    }

    public static int notifyAttendance(String startDay, String endDay) throws IOException {
        return notifyAttendance(0, startDay, endDay);
    }

    public static final int NA_SUCCESS = 0;
    public static final int NA_NO_DATA = -1;
    public static final int NA_NO_EMAIL = -2;
    public static final int NA_SEND_EMAIL_FAIL = -3;
    public static final int NA_EXCEPTION = -4;

    public static final int NUM_OF_ONE = 100 ;// 一封邮件最多发给几个人 // 邮件发送有上限 最多150 
    public static int notifyAttendance(int month, String startDay, String endDay) throws IOException {
        int res = NA_SUCCESS;
        /*
        LeaveService leaveService = ServiceFactory.getService(ServiceConfig.SERVICE_LEAVE);
        List<TLeave> tLeaves = leaveService.getUnhandleLeaves(startDay, endDay);
        List<String> tos = leaveService.getUnhandleUserEmails(startDay, endDay);
        if(tLeaves.size() == 0) {
            res = NA_NO_DATA;
        } else if(tos.size() == 0) {
            res = NA_NO_EMAIL;
        } else if(tLeaves.size() > 0 && tos.size() > 0) {
            StringBuilder txt = new StringBuilder("<b>Dear ALL:</b><br/>");
            if(month > 0) {
                txt.append(month+"月份");
            } else {
                txt.append("<b><i>"+startDay+"</i></b>");
                txt.append("至");
                txt.append("<b><i>"+endDay+"</i></b>");
            }
            txt.append("的考勤异常如下，请及时填补考勤异常或无打卡记录的相关表单，并交予人事行政部留档，谢谢!</b><br/>");
            txt.append("具体异常情况可登陆公司<a href='");
            txt.append(ConfigUtil.getProperty("Mail_url"));
            txt.append("'>考勤系统</a>进行查看！<br/>");
            txt.append("<font color='red'><b>注：开始时间和结束时间指的是异常开始的时间和异常结束的时间，而不是当天的签到时间和签退时间。</b>");
            txt.append("<br/>如签退时间为16:20:00,正常下班时间为17:44:59，那么异常开始时间就是16:20:01,异常结束时间就是17:44:59。</font>");
            txt.append("<table border='1' cellpadding='10' cellspacing='0'><tbody>");
            txt.append("<tr><th style='background-color:#97FFFF;'>工号</th>");
            txt.append("<th style='background-color:#97FFFF;'>姓名</th>");
            txt.append("<th style='background-color:#97FFFF;'>日期</th>");
            txt.append("<th style='background-color:#97FFFF;'>异常开始时间</th>");
            txt.append("<th style='background-color:#97FFFF;'>异常结束时间</th>");
            txt.append("<th style='background-color:#97FFFF;'>异常总时长（小时）</th>");
            txt.append("<th style='background-color:#97FFFF;'>异常类型</th></tr>");
            for(TLeave tLeave : tLeaves) {
                txt.append("<tr>");
                txt.append("<td height='10'>"+tLeave.getNumber()+"</td>");
                txt.append("<td>"+tLeave.getName()+"</td>");
                txt.append("<td>"+tLeave.getDayStr()+"</td>");
                txt.append("<td>"+tLeave.getStartTime()+"</td>");
                txt.append("<td>"+tLeave.getEndTime()+"</td>");
                txt.append("<td>"+tLeave.getTotalHours()+"</td>");
                txt.append("<td>"+tLeave.getStat()+"</td>");
                txt.append("</tr>");
            }
            txt.append("</tbody></table>");
            MailUtils mailUtil = new MailUtils();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            LeaveExport export = new LeaveExport(tLeaves);
            export.exportExcel(bos);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            Attachment attachment = new Attachment("考勤异常.xls", "application/vnd.ms-excel", bis);
            mailUtil.addAttachment(attachment);
            //mailUtil.setAddress("考勤异常", "zixiao.zhang@wheatek.com");
            mailUtil.setSubject("考勤异常");
            mailUtil.setTos(tos);
            String cc = ConfigUtil.getProperty("leaveTip_cc");
            if(cc == null) cc="";
            boolean sendRes = mailUtil.send(txt.toString(), cc, "");
            if(!sendRes) {
                res = NA_SEND_EMAIL_FAIL;
            }
        }
        */
        FeeService feeService = ServiceFactory.getService(ServiceConfig.SERVICE_FEE);
        List<TFees> tFeesList = feeService.getUnhandleLeaves(startDay, endDay);
        List<String> tos = feeService.getUnhandleUserEmails(startDay, endDay);

       //yingjing.liu 20160823 start  邮件发送 接收人有上限 // 这里分批次发送
        int start = cishu*NUM_OF_ONE-NUM_OF_ONE;
        int end = start + NUM_OF_ONE;
        List<String> curr = new ArrayList<String>();
        for(int i= 0 ; i< tos.size() ; i++){
            if(start<i&& i<=end ){
                curr.add(tos.get(i));
            }
        }
        tos = curr;
        //yingjing.liu 20160823 end

        
        if(tFeesList.size() == 0) {
            res = NA_NO_DATA;
        } else if(tos.size() == 0) {
            res = NA_NO_EMAIL;
        } else if(tFeesList.size() > 0 && tos.size() > 0) {
            StringBuilder txt = new StringBuilder("<b>Dear ALL:</b><br/>");
            if(month > 0) {
                txt.append(month+"月份");
            } else {
                txt.append("<b><i>"+startDay+"</i></b>");
                txt.append("至");
                txt.append("<b><i>"+endDay+"</i></b>");
            }
            txt.append("的考勤异常如下，请及时填补考勤异常或无打卡记录的相关表单，并交予人事行政部留档，谢谢!</b><br/>");
            txt.append("具体异常情况可登陆公司<a href='");
            txt.append(ConfigUtil.getProperty("Mail_url"));
            txt.append("'>考勤系统</a>进行查看！<br/>");
            txt.append("<font color='#FF0000'>注：签退时间小于06:00:59表示加班到第二天。</font>");
            txt.append("<table border='1' cellpadding='10' cellspacing='0'><tbody>");
            txt.append("<tr><th style='background-color:#97FFFF;'>工号</th>");
            txt.append("<th style='background-color:#97FFFF;'>姓名</th>");
            txt.append("<th style='background-color:#97FFFF;'>日期</th>");
            txt.append("<th style='background-color:#97FFFF;'>签到时间</th>");
            txt.append("<th style='background-color:#97FFFF;'>签退时间</th>");
            txt.append("<th style='background-color:#97FFFF;'>异常类型</th></tr>");
            for(TFees tFees : tFeesList) {
                txt.append("<tr>");
                txt.append("<td height='10'>"+tFees.getNumber()+"</td>");
                txt.append("<td>"+tFees.getName()+"</td>");
                if(tFees.getStatus().equals("未打卡")) {
                    txt.append("<td style='background-color:#CD2626;'>"+tFees.getDayStr()+"</td>");
                    txt.append("<td>--</td>");
                    txt.append("<td>--</td>");
                } else {
                    txt.append("<td>"+tFees.getDayStr()+"</td>");
                    if(tFees.getStatus().equals("旷工") || tFees.getStatus().equals("迟到")) {
                        txt.append("<td style='background-color:#CD2626;'>"+tFees.getStartTime()+"</td>");
                    } else {
                        txt.append("<td>"+tFees.getStartTime()+"</td>");
                    }
                    if(tFees.getStatus().equals("旷工") || tFees.getStatus().equals("早退")) {
                        txt.append("<td style='background-color:#CD2626;'>"+tFees.getEndTime()+"</td>");
                    } else {
                        txt.append("<td>"+tFees.getEndTime()+"</td>");
                    }
                }
                txt.append("<td>"+tFees.getStatus()+"</td>");
                txt.append("</tr>");
            }
            txt.append("</tbody></table>");
            MailUtils mailUtil = new MailUtils();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            LeaveExport export = new LeaveExport();
            export.settFees(tFeesList);
            export.exportExcel(bos);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            Attachment attachment = new Attachment("考勤异常.xls", "application/vnd.ms-excel", bis);
            mailUtil.addAttachment(attachment);
//            mailUtil.setAddress("考勤异常", "654652424@qq.com");
            mailUtil.setSubject("考勤异常");

//            tos = new ArrayList<String>();
//            tos.add("yingjing.liu@wheatek.com");  //只发给我

            mailUtil.setTos(tos);
            String cc = ConfigUtil.getProperty("leaveTip_cc");
            if(cc == null) cc="";
            boolean sendRes = mailUtil.send(txt.toString(), cc, "");
            if(!sendRes) {
                res = NA_SEND_EMAIL_FAIL;
            }
        }
        return res;
    }

    public static int getFeeType(String dayStr, String startTime, String endTime) throws ParseException {
        int type = Fees.TYPE_NORMAL;
        if(KaoQin.checkWetherHoliday(dayStr)) {
            type = Fees.TYPE_HOLIDAY;
        } else {
            if(startTime.equals(endTime)) {
                type = Fees.TYPE_ABAENCE;
            } else {
                if(KaoQin.isWorkflowToNextDay(endTime)) {
                    endTime = KaoQin.add24Hour(endTime);
                }
                if(startTime.compareTo(LeaveUtil.TIME_PM_END) > 0 || endTime.compareTo(LeaveUtil.TIME_AM_START_1) < 0) {
                    type = Fees.TYPE_ONLY_OVERTIME;
                }
            }
        }
        return type;
    }
    public static int getFeeType(Fees fees) throws ParseException {
        return getFeeType(fees.getDay_str(),fees.getStart_time(),fees.getEnd_time());
    }
    public static int getFeeType(TFees tFees) throws ParseException {
        return getFeeType(tFees.getDayStr(),tFees.getStartTime(),tFees.getEndTime());
    }

    public static void  main(String[] args) throws Exception {
//        Bill bill = new Bill();
//        bill.setStartDate("2016/06/20 13:00");
//        bill.setEndDate("2016/06/21 10:20");
//        bill.setBillType(Bill.TYPE_2);
//        TBill tBill = bill.toSqlBean();
//        calculateBillTotalHours(tBill);
//        System.out.println(tBill.getTotalHours());
        String s = "2016/10/23 21:34";
        System.out.println(s.substring(0, 12));
    }
}
