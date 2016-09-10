package com.ragentek.mealsupplement.dwr;

import com.ragentek.mealsupplement.KaoQingDao;
import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.*;
import com.ragentek.mealsupplement.json.Dept;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.ldap.LDAPControl;
import com.ragentek.mealsupplement.listener.UserTimer;
import com.ragentek.mealsupplement.poi.UserAttendanceExport;
import com.ragentek.mealsupplement.service.BillTypeService;
import com.ragentek.mealsupplement.service.FeeService;
import com.ragentek.mealsupplement.service.GroupService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class Dwr {
    public String test() {
        return "zhangzixiao";
    }
    public String updateUserStatus(long userId, int status) {
        String sql = "select * from t_user where id="+userId;
        /*
        List<TUser> users = DBUtils.query(sql, TUser.class);
        if(users.size() > 0) {
            TUser user = users.get(0);
            user.setStat(status);
            DBUtils.update(user);
            return Result.success();
        }*/
        TUser user = DBUtils.uniqueBean(sql, TUser.class);
        if(user != null) {
            // yingjing.liu add start 20160629  如果是改为离职  需要修改他的离职时间
            if(status == User.STATUS_LEAVE){
                user.setLeaveDate(DateTools.formatDateToString(new Date(),DateTools.FORSTR_DATE));
            }else{
                user.setLeaveDate(""); // 不是离职离职时间就是空
            }
            // yingjing.liu add end 20160629
            user.setStat(status);
            DBUtils.update(user);
            UserCache.getInstance().update(new User(user));
            return Result.success();
        }
        return Result.error("The user does not exist!");
    }

    public String updateUserEnterDay(long userId, String enterDay) {
        if(TextUtil.isNullOrEmpty(enterDay)) {
            return Result.error("入职日期不能为空！");
        }
        String sql = "select * from t_user where id="+userId;
        TUser tUser = DBUtils.uniqueBean(sql, TUser.class);
        if(tUser != null) {
            tUser.setEnterDate(enterDay);
            DBUtils.update(tUser);
            UserCache.getInstance().update(new User(tUser));
            return Result.success();
        }
        return Result.error("The user does not exist!");
    }
    // yingjing.liu add 20160629 start 录入、修改用户离职时间
    public String updateUserLeaveDay(long userId, String leaveDay) {
        if(TextUtil.isNullOrEmpty(leaveDay)) {
            return Result.error("离职日期不能为空！");
        }
        String sql = "select * from t_user where id="+userId;
        TUser tUser = DBUtils.uniqueBean(sql, TUser.class);
        if(tUser != null) {
            tUser.setLeaveDate(leaveDay);
            DBUtils.update(tUser);
            UserCache.getInstance().update(new User(tUser));
            return Result.success();
        }
        return Result.error("The user does not exist!");
    }
    // yingjing.liu add 20160629 start 录入、修改用户离职时间



    public String syncLdapUser() {
        System.out.println("SyncLdapUser start ... ");
        /*
        List<TUser> tUsers = LDAPControl.getInstance().getAllLadpUser();
        //UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
        //GroupService groupService = ServiceFactory.getService(ServiceConfig.SERVICE_GROUP);
        for(TUser tUser : tUsers) {
            System.out.println(tUser.getLoginCount()+" : "+tUser.getName()+","+tUser.getNumber()+","+tUser.getDepart());
            //ldap中用户名是唯一的，但在该系统中用户名未必是唯一的，因为可能有同名的人离职后再入职。但工号是唯一的，该系统就以工号为唯一标识，无工号不录入
            String number = tUser.getNumber();
            if(!TextUtil.isNullOrEmpty(number)) {
                UserUtil.saveOrUpdateLdapUser(tUser);
            }
        }*/
        try {
            UserTimer.sync();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.toString());
        }
        System.out.println("SyncLdapUser end ... ");
        return Result.success();
    }

    public String updateBillType(Long id, Integer billType) {
        String sql = "select * from t_bill_type where _id="+id;
        TBillType entity = DBUtils.uniqueBean(sql, TBillType.class);
        if(entity == null) {
            return Result.error("The Bill Type does not exist!");
        } else {
            entity.setBillType(billType);
            DBUtils.update(entity);
            return Result.success();
        }
    }

    public String updateBillName(Long id, String billName) {
        String sql = "select * from t_bill_type where _id="+id;
        TBillType entity = DBUtils.uniqueBean(sql, TBillType.class);
        if(TextUtil.isNullOrEmpty(billName)) {
            return Result.error("The Bill Name is empty!");
        } else if(entity == null) {
            return Result.error("The Bill Type does not exist!");
        } else {
            billName = billName.trim();
            if(!entity.getBillName().equals(billName)) {
                BillTypeService billTypeService = ServiceFactory.getService(ServiceConfig.SERVICE_BILL_TYPE);
                boolean existBillName = billTypeService.existBillName(billName);
                if(existBillName) {
                    return Result.error("The Bill Name already exists, please change one!");
                }
            }
            entity.setBillName(billName);
            DBUtils.update(entity);
            return Result.success();
        }
    }

    public String notifyAttendance(String startDay, String endDay) {
        int res = 0;
        try {
//            res = LeaveUtil.notifyAttendance(startDay, endDay);

            //yingjing.liu 20160823 start  邮件发送 接收人有上限 // 这里分批次发送
            LeaveUtil.cishu = 1;
            FeeService feeService = ServiceFactory.getService(ServiceConfig.SERVICE_FEE);
            List<String> tos = feeService.getUnhandleUserEmails(startDay, endDay);
            int num = (tos.size()/LeaveUtil.NUM_OF_ONE)+1;
            System.out.println("发送邮件数量(份)： "+num);
            for(int i =0  ; i<num;i++ ){
                LeaveUtil.cishu = i+1;
                res = LeaveUtil.notifyAttendance(startDay, endDay);
            }
            //yingjing.liu 20160823 end

        } catch (Exception e) {
            e.printStackTrace();
            res = LeaveUtil.NA_EXCEPTION;
        }
        if(res == LeaveUtil.NA_SUCCESS) {
            return Result.success();
        } else {
            Result result = new Result(false, res);
            return result.toString();
        }
    }

    /**
     * POI 导出考勤信息
     * @param startDay
     * @param endDay
     * @return
     */
    public String exportPoi(String  startDay,String endDay){
        int res = 0;
        try{
            //执行POI导出 操作
            System.out.println("is to export report for  POI to bill...");

            KaoQingDao dao = new KaoQingDao();
            List<KaoqinDto> dtos = dao.queryByDate(startDay,endDay);


            OutputStream os = new FileOutputStream(new File("D:\\麦穗考勤系統数据"+startDay.replaceAll("/","")+"-"+endDay.replaceAll("/","")+".xlsx"));
            UserAttendanceExport uae = new UserAttendanceExport(startDay,endDay);
            uae.setData(dtos);

            uae.exportExcel(os);


        }catch(Exception e){
            e.printStackTrace();
            res= LeaveUtil.NA_EXCEPTION;
        }
        if(res == LeaveUtil.NA_SUCCESS){
            return Result.success();
        }else{
            Result result = new Result(false,res);
            return result.toString();
        }
    }

    /**
     * 在添加用户里验证 工号是不是被占用
     * @param number
     * @return
     */
    public String checkNumberUnique(String number ){
        String sql = " select * from t_user where number = "+number ;
        List<TUser> users = DBUtils.query(sql,TUser.class);

        if(users.size()>0){     // 有用户在不合格
            Result result = new Result(false,"工号被占用！");
            return result.toString();
        }else{

            return Result.success();
        }

    }

    public String getDeptSecond(String deptId ){
        TDept tDept = null;
        String sql1 = "select * from t_dept where _id = "+deptId;
        List<TDept> alldepts = DBUtils.query(sql1,TDept.class);
        if(alldepts.size()==0)
              tDept = new TDept();
        else
              tDept = alldepts.get(0);

        String sql = "select * from t_dept " ;
            sql += " where dtype =  "+Dept.TYPE_LEVEL_SECOND +" and parent_value = "+ tDept.getValue();
        List<TDept> depts = DBUtils.query(sql,TDept.class);
        StringBuffer sb = new StringBuffer("[");
        for (TDept td:depts) {
            sb.append("{ 'name' : '"+td.getName()+"','id' : '"+td.getId() +"' ,'value': '"+td.getValue()+"' },");
        }
        if(sb.length()>3){
            sb = new StringBuffer(sb.substring(0,sb.toString().length()-1));
        }

        sb.append("]");
        System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * 判断 给的id 下的部门有没有 员工在这个用户下
     * @param deptId
     * @return
     */
    public Boolean checkCanDelete(String deptId){
        String sql1 = "select * from t_dept where _id = "+deptId;
        List<TDept> alldepts = DBUtils.query(sql1,TDept.class);

        if(alldepts.size()>0){
            TDept t = alldepts.get(0);
            String sql = "";
            if(t.getDtype()==Dept.TYPE_LEVEL_FIRST){
                sql = " select * from t_user where dept1 = "+deptId;
            }else{
                sql = " select * from t_user where dept2 = "+deptId;
            }

            List<TUser> users = DBUtils.query(sql,TUser.class);
            System.out.println("usersNum:"+users.size()+"checkDelete: "+sql);
            if(users.size()==0){            //只有这个部门下面没有人的时候才可以删除 返回true
                return true;
            }
        }
        return false;
    }

    public String addUserToDept(String deptId ,String userId ){
        String result = "" ;
        String sql1 = "select * from t_dept where _id = "+deptId;
        List<TDept> depts = DBUtils.query(sql1,TDept.class);

        String sql2 = "select * from t_user where number = "+userId ;
        List<TUser> users = DBUtils.query(sql2,TUser.class);
        if(depts.size()>0&&users.size()>0){ // 只会有一条记录
            TDept dept = depts.get(0);
            TUser user = users.get(0);
            if(dept.getDtype()==1){
                user.setDept1(dept.getId().longValue());
                user.setDeptName1(dept.getName());
            }else if(dept.getDtype()==2){
                sql1 = "select * from t_dept where _id = "+dept.getParentId();
                TDept dept1 = DBUtils.uniqueBean(sql1,TDept.class);
                if(dept1 != null ){
                    user.setDept1(dept1.getId().longValue());
                    user.setDeptName1(dept1.getName());

                    user.setDept2(dept.getId().longValue());
                    user.setDeptName2(dept.getName());
                }
            }
            DBUtils.update(user);
            result = "添加成功";
        }else {
            result = "添加失败";
        }
        return result;
    }

}
