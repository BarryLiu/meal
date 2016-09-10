package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.*;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.json.*;
import com.ragentek.mealsupplement.service.*;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.KaoQin;
import com.ragentek.mealsupplement.tools.LeaveUtil;
import com.ragentek.mealsupplement.tools.TextUtil;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public class BillServlet extends BaseServlet {
    private BillService billService;
    private BillTypeService billTypeService;
    private UserService userService;
    private DeptService deptService;
    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        try {
            String number = getParameter(req, "number");
            if(!TextUtil.isNullOrEmpty(number)) {
                req.setAttribute("number", number);
            }
            String billName = getParameter(req, "billName");
            if(!TextUtil.isNullOrEmpty(billName)) {
                req.setAttribute("billName", billName);
            }

            String sDate = req.getParameter("sDate"); // 查询开始日期
            
            //yingjing.liu 20160726 add start 添加对时间的过滤  查询
            if(sDate == null) {
                Calendar cal = Calendar.getInstance();
                if(cal.get(Calendar.DAY_OF_MONTH)<=10){ // 判断   如果是 10号之前看的,就用上个月的一号   如果是10号之后就用本月一号
                    cal.add(Calendar.MONTH,-1);
                }
                cal.set(Calendar.DAY_OF_MONTH,1);
                sDate = DateTools.formatDateToString(cal.getTime(),"yyyy/MM/dd");
            }
            //yingjing.liu 20160726 add end
            req.setAttribute("sDate", sDate);
            //List<TBill> tBillList = billService.getAll();
            List<TBill> tBillList = billService.getBeans(number, billName,sDate);
            Rows row = new Rows();
            List<Bill> billList = row.getRowsAsList();
            for(TBill tBill : tBillList) {
                billList.add(new Bill(tBill));
            }
            injectDept(billList); //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
            filetareByDept(billList,req);// 根据前面传过来的一级部门与二级部门查询    ： 首先数据库查询全部 ,在这里根据条件remove list数据

            String data = row.toJson();
            req.setAttribute(DATA, data);
            /** 取出所有的类型供选择 **/
            List<TBillType> tBillTypes = billTypeService.getAll();
            req.setAttribute("tBillTypes", tBillTypes);
            JSONArray jsonArray = JSONArray.fromObject(tBillTypes);
            String jsonBillTypes = jsonArray.toString();
            req.setAttribute("billNameData", jsonBillTypes.replace("\"","'"));
            /** 取出所有参与考勤的用户供选择 **/
            List<TUser> tUsers = userService.getNormalUsers();
            req.setAttribute("tUsers", tUsers);
            //yingjing.liu 20160726 start 根据部门会显
            List<TDept> depts = deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
            req.setAttribute("depts", depts);
            //yingjing.liu 20160726 end
            //System.out.println("data==="+data);
            req.getRequestDispatcher("bill/billList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }

    private void injectDept(List<Bill> billList) {
        List<TUser> users = userService.getAllUsers();
        Map<String,TUser> userMap = new HashMap<String, TUser>();
        for(int i=0 ; i<users.size(); i++){ //将系统中的数据放到map容器中去
            TUser u = users.get(i);
            userMap.put(u.getNumber(),u);
        }
        for(int i=0 ; i<billList.size(); i++){ //将系统中的数据放到map容器中去
            Bill le = billList.get(i);
            if(userMap.containsKey(le.getNumber())){
                TUser u =userMap.get(le.getNumber());
                le.setDeptName1(u.getDeptName1());
                le.setDeptName2(u.getDeptName2());
            }
        }
    }
    private void filetareByDept(List<Bill> leaves, HttpServletRequest req) {
        String dept1Id = req.getParameter("dept1");
        String dept2Id = req.getParameter("dept2");
        req.setAttribute("dept1",dept1Id);
        req.setAttribute("dept2",dept1Id);

        TDept dept1 = null;
        TDept dept2 = null;
        if(!TextUtil.isNullOrEmpty(dept1Id)){
            dept1 = deptService.selectById(dept1Id);
            List<TDept> seconds =  deptService.getDeptsByType(Dept.TYPE_LEVEL_SECOND,dept1.getValue());
            req.setAttribute("deptSeconds",seconds);
        }

        if(!TextUtil.isNullOrEmpty(dept2Id))
            dept2 = deptService.selectById(dept2Id);

        Iterator<Bill> it = leaves.iterator();
        while(it.hasNext()){
            Bill le = it.next();
            if(dept1!=null && dept1.getName()!=null){
                if(!dept1.getName().equals(le.getDeptName1())){
                    it.remove();
                    continue;
                }
            }
            if(dept2!=null && dept2.getName()!=null){
                if(!dept2.getName().equals(le.getDeptName2())){
                    it.remove();
                    continue;
                }
            }
        }
    }

    @Override
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = null;
        Bill bill = getBeanFromParam(req, PMS, Bill.class);
        TBillType billType = billTypeService.getByBillName(bill.getBillName());
        bill.setBillType(billType.getBillType());
        TUser tUser = userService.getByNumber(bill.getNumber());
        bill.setDepart(tUser.getDepart());
        bill.setName(tUser.getName());
        //bill.setTotalHours(100);
        TBill tBill = bill.toSqlBean();
        try {
            LeaveUtil.calculateBillTotalHours(tBill);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }
        long id = DBUtils.insert(tBill);
        if(id > 0) {
            tBill.setId(id);
            if(!Bill.isKaoQinBill(tBill.getBillType())) {
                LeaveUtil.saveOrUpdateLeave(tBill.getNumber(), tBill.getStartTimeStr(), tBill.getEndTimeStr());
            } else {
                LeaveUtil.handleForgetClock(tBill.getNumber(), tBill.getStartTimeStr(), tBill.getEndTimeStr()); //新增可以从餐补表开始计算
            }
            json = Result.success(new Bill(tBill));
        } else {
            json = Result.error("Failed to add!");
            System.out.println("BillType add Failed: billName=" + bill.getBillName() + ",name=" + bill.getName() + ",number=" + bill.getNumber() + ",startDate=" + bill.getStartDate() + ",endDate=" + bill.getEndDate() + ",id=" + id);
        }
        resp.getOutputStream().write(json.getBytes("UTF-8"));
    }

    /*
    public static void main(String[] args) throws Exception {
        Bill bill = new Bill();
        bill.setStartDate("2016/04/05 11:40");
        bill.setEndDate("2016/04/14 11:40");
        bill.setBillType(Bill.TYPE_0);
        TBill tBill = bill.toSqlBean();
        calculateBillTotalHours(tBill);
        System.out.println(tBill.getTotalHours());
    }
    */

    @Override
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long[] ids = getParam(req, "ids", Long[].class);
        List<TBill> tBillList = billService.getByIds(ids);
        int count = billService.deleteMulti(ids);
        for(TBill tBill : tBillList) {
            reCheck(tBill, true);
        }
        if(count != ids.length) {
            logger.warn("BillTypeServlet:count="+count+"ids.length="+ids.length);
        }
        resp.getOutputStream().write(Result.success().getBytes("UTF-8"));
    }

    //考虑情况：1. 删除考勤补充 2.由非考勤补充改为考勤补充 3.由考勤补充变为非考勤补充
    //其中1,3种情况是减少考勤，情况2是增加考勤
    //目前对第二种情况可以应对，但是对1,3种情况无法应对
    private static void reCheck(TBill tBill, boolean decrease) {
        if(!Bill.isInvalid(tBill.getBillType())) { //自动生成的未生效表单可以直接删除不用更新
            if(!Bill.isKaoQinBill(tBill.getBillType())) {
                LeaveUtil.saveOrUpdateLeave(tBill.getNumber(), tBill.getStartTimeStr(), tBill.getEndTimeStr());
            } else {
                try {
                    //modify zhangzixiao 20160530 可能当天无打开记录，如果无打卡记录不会进行任何更新 start
                    KaoQin.countKaoQinForFees(tBill.getNumber(), tBill.getStartTimeStr(), tBill.getEndTimeStr()); //重新从考勤表来计算考勤
                    String startDay = LeaveUtil.getDayStr(tBill.getStartTimeStr());
                    String endDay = LeaveUtil.getDayStr(tBill.getEndTimeStr());
                    if(!decrease) {
                        FeeService feeService = ServiceFactory.getService(ServiceConfig.SERVICE_FEE);
                        List<TFees> tFeesList = feeService.getFees(tBill.getNumber(), startDay, endDay);
                        //System.out.println("number="+tBill.getNumber()+",startDay="+startDay+",endDay="+endDay+",count="+tFeesList.size());
                        for(TFees tFees : tFeesList) {
                            KaoQin.totalAndSaveFees(tFees);
                        }
                    } else {
                        Calendar start = Calendar.getInstance();
                        start.setTime(DateTools.formatStringToDate(startDay, DateTools.FORSTR_DATE));
                        Calendar end = Calendar.getInstance();
                        end.setTime(DateTools.formatStringToDate(endDay, DateTools.FORSTR_DATE));
                        while (start.getTime().getTime() <= end.getTime().getTime()) {
                            if (!(start.getTime().getTime() == end.getTime().getTime() && LeaveUtil.getTimeStr(tBill.getEndTimeStr()).compareTo("06:00:59") <= 0)) {
                                KaoQin.totalAndSaveFees(DateTools.formatDateToString(start.getTime(), DateTools.FORSTR_DATE), tBill.getNumber(), tBill.getName());
                            }
                            start.add(Calendar.DAY_OF_MONTH, 1);
                        }
                    }
                    //modify zhangzixiao 20160530 end
                } catch (ParseException e) {
                    e.printStackTrace();
                    //logger.error(e.toString(), e);
                }
            }
        }
    }

    @Override
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = null;
        Long id = getParam(req, "id", Long.class);
        if(id == null) {
            json = Result.error();
        } else {
            TBill tBill = billService.getById(id);
            if(Bill.isInvalid(tBill.getBillType())) { //原来是无效的，需要添加用户部门并去掉说明
                tBill.setDescs("");
                /* 后面改动，导致目前部门在生成未生效表单的时候就已经添加了,如果要打开注释，用户的获取可考虑从UserCache获得
                String number = tBill.getNumber();
                TUser tUser = userService.getByNumber(number);
                if(tUser != null) {
                    tBill.setDepart(tUser.getDepart());
                }
                */
            }
            int preBillType = tBill.getBillType();
            String billName = getParameter(req, "billName");
            TBillType billType = billTypeService.getByBillName(billName);
            tBill.setBillType(billType.getBillType());
            tBill.setBillName(billName);
            int curBillType = tBill.getBillType();
            try {
                LeaveUtil.calculateBillTotalHours(tBill); //重新计算总时长，因为可能变成了考勤补充
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DBUtils.update(tBill);
            changeFees(billName,tBill); // 修改了表单处理结果，对餐补 t_fees 中有一个status 表示excel导入的状态 在这里根据 t_bill(表单)的信息修改t_fees 的 deal_status 处理为状态
            if(preBillType==Bill.TYPE_3 && curBillType!=Bill.TYPE_3) { //由考勤补充变为了非考勤补充
                reCheck(tBill, true);
            } else {
                reCheck(tBill, false);
            }
            json = Result.success();
        }
        resp.getOutputStream().write(json.getBytes("UTF-8"));
    }

    private void changeFees(String billName, TBill tBill) {
        

    }

    private static void setDefaultTo(String startDay, String endDay, String billName) {
        /*
        BillTypeService billTypeService = ServiceFactory.getService(ServiceConfig.SERVICE_BILL_TYPE);
        BillService billService = ServiceFactory.getService(ServiceConfig.SERVICE_BILL);
        UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
        TBillType iBillType = billTypeService.getByBillName(billName);
        if(iBillType != null) {
            int billType = iBillType.getBillType();
            //获得所有的默认表单(这种方式目前是不准确的，因为一天有2条异常的情况下，更新第一条的时候，会把第二条删除再新增，所以若真要这种方式，需要不断的查询数据库，处理一条查询一次)
            String sql = "select * from t_bill where bill_type=-1 and start_time_str>'"+startDay+"' and end_time_str<'"+endDay+"'";
            List<TBill> tBillList = DBUtils.query(sql, TBill.class);
            for(TBill tBill : tBillList) {
                tBill.setDescs("");
                String number = tBill.getNumber();
                TUser tUser = userService.getByNumber(number);
                if(tUser != null) {
                    tBill.setDepart(tUser.getDepart());
                } else {
                    tBill.setDepart("已离职");
                }
                tBill.setBillType(billType);
                tBill.setBillName(billName);
                DBUtils.update(tBill);
                reCheck(tBill);
            }
        } else {
            System.out.println(billName+": 不存在！");
        }*/
        //将t_leave和默认bill关联起来
        EntityList lst = new EntityList("update t_leave set bill_id =");
        lst.append("(");
        lst.append("select b._id from t_bill b where b.number=t_leave.number and SUBSTRING(b.start_time_str, 1, 10)=t_leave.day_str");
        lst.append(" and (SUBSTRING(b.start_time_str, 12, 5)+':00')<=t_leave.start_time and (SUBSTRING(b.end_time_str, 12, 5)+':00')>=t_leave.end_time");
        lst.append(" and b.bill_type=-1");
        lst.append(") where day_str<='2016/04/01' and day_str>='2016/04/01'");
        //然后将t_leave的billName更新为‘调休’
        String updateTLeave = "update t_leave set bill_name='调休' where bill_id is not null and (bill_name is null or bill_name='') and day_str<='2016/04/01' and day_str>='2016/04/01'";
        //然后更新t_bill,将descs变为空，将bill_type变为0（调休）,将bill_name改为调休,如此修改后还有depart没有更新
        String updateTBill = "update t_bill set descs='',bill_type=0,bill_name='调休' where substring(start_time_str, 1, 10)<='2016/04/01' and substring(start_time_str, 1, 10)>='2016/04/01' and bill_type=-1";
        //TODO... ... 略，以上语句都可以在图形界面执行，此处建议在图像界面一条条执行，故不再完成该方法
    }

    public static void main(String[] args) {
        setDefaultTo("2015/08/01", "2016/04/01", "调休");
    }

}
