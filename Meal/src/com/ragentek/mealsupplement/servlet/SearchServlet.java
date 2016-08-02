package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.*;
import com.ragentek.mealsupplement.ldap.LDAPControl;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.TextUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by kui.li on 2014/9/2.
 */
public class SearchServlet extends BaseServlet {

//    private static final Logger logger = Logger.getLogger(LoginServlet.class);
    private DeptService deptService;
    private UserService userService;


    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name"); // 员工姓名或工号
        String sDate = req.getParameter("sDate"); // 查询开始日期
        String eDate = req.getParameter("eDate"); // 查询结束日期
        System.out.println("name=" + name + ";sDate=" + sDate + ";eDate=" + eDate);

        // wpf add
        System.out.println("WPF_4");

        if(sDate == null ) {
            Calendar cal = Calendar.getInstance();
            if(cal.get(Calendar.DAY_OF_MONTH)<=10){ // 判断   如果是 10号之前看的,就用上个月的一号   如果是10号之后就用本月一号
                cal.add(Calendar.MONTH,-1);
            }
            cal.set(Calendar.DAY_OF_MONTH,1);
            sDate = DateTools.formatDateToString(cal.getTime(),"yyyy/MM/dd");
        }

        req.setAttribute("name", name);
        req.setAttribute("sDate", sDate);
        req.setAttribute("eDate", eDate);
//        String type = req.getParameter("type"); // 餐补类型，1，2，3，4，5分别代表早，中，晚，宵，午夜

// wpf add
System.out.println("WPF_MEAL search sDate: "+ sDate +", eDate: "+eDate);

        // 开始查询
//        String sql = "SELECT * FROM t_fees WHERE 1=1";

        String sql = " select id,user_info,day_str,fee1,fee2,fee3,fee4,fee5,start_time, end_time,workovertime,status,name,number,fee_type " +
                "\t ,(select top 1 bill_name from t_bill where convert(char(10),start_time,111) = f.day_str and number = f.number  ) as deal_status " +
                "\t from t_fees f where 1 =1 ";

        if(name != null && !"".equals(name)) {
            sql += " AND user_info like '%" + name + "%'";
        }
        if(sDate != null && !"".equals(sDate)) {
            sql += " AND day_str >= '" + sDate + "'";
        }
        if(eDate != null && !"".equals(eDate)) {
            sql += " AND day_str <= '" + eDate + "'";
        }
        sql += " ORDER BY day_str DESC,user_info";
        System.out.println("查询所有考勤和餐补语句："+sql);
        List<TFees> lstFees = DBUtils.query(sql,TFees.class);
        if(lstFees != null && lstFees.size()>0) {
            /*
            StringBuilder sb = new StringBuilder();
            sb.append("{Rows:[");
            String sDateStr = "";
            String eDateStr = "";
            int tFee1=0,tFee2=0,tFee3=0,tFee4=0,tFee5=0;
            int tOvertime = 0;
            for(TFees item : lstFees) {
                if("".equals(sDateStr))
                    sDateStr = item.getDayStr();
                eDateStr = item.getDayStr();
                tFee1 += item.getFee1();
                tFee2 += item.getFee2();
                tFee3 += item.getFee3();
                tFee4 += item.getFee4();
                //tFee5 += item.getFee5();
                tOvertime += item.getWorkovertime();
                sb.append("{user_info:'");
                sb.append(item.getUserInfo());
                sb.append("',day_str:'");
                sb.append(item.getDayStr());
                sb.append("',start_time:'");
                sb.append(item.getStartTime());
                sb.append("',end_time:'");
                sb.append(item.getEndTime());
                sb.append("',workovertime:'");
                sb.append(item.getWorkovertime());
                sb.append("',fee1:'");
                sb.append(item.getFee1());
                sb.append("',fee2:'");
                sb.append(item.getFee2());
                sb.append("',fee3:'");
                sb.append(item.getFee3());
                sb.append("',fee4:'");
                sb.append(item.getFee4());
                //sb.append("',fee5:'");
                //sb.append(item.getFee5());
                sb.append("',total:'");
                sb.append(item.getFee1() + item.getFee2() + item.getFee3() + item.getFee4() + item.getFee5());
                sb.append("'},");
            }
            sb.append("{user_info:'");
            sb.append("合计");
            sb.append("',day_str:'");
            sb.append(sDateStr + "~" + eDateStr);
            sb.append("',start_time:'");
            sb.append("");
            sb.append("',end_time:'");
            sb.append("");
            sb.append("',workovertime:'");
            sb.append(tOvertime);
            sb.append("',fee1:'");
            sb.append(tFee1);
            sb.append("',fee2:'");
            sb.append(tFee2);
            sb.append("',fee3:'");
            sb.append(tFee3);
            sb.append("',fee4:'");
            sb.append(tFee4);
            //sb.append("',fee5:'");
            //sb.append(tFee5);
            sb.append("',total:'");
            //sb.append(tFee1 + tFee2 + tFee3 + tFee4 + tFee5);
            sb.append(tFee1 + tFee2 + tFee3 + tFee4);
            sb.append("'},");
            sb.append("]}");
            req.setAttribute("data", sb.toString());
            */


            FeesList list = new FeesList();
            list.addAll(lstFees);

            List<Fees> fees = list.getList();
            injectDept(fees); //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
            filetareByDept(fees,req);// 根据前面传过来的一级部门与二级部门查询    ： 首先数据库查询全部 ,在这里根据条件remove list数据
            filetareByUsers(fees,req);// 主要是根据自己是不是部门负责人来查询的   如果是负责人才进去过滤 ,不过滤代表不是负责人

            Rows rows = new Rows(fees);
            req.setAttribute("data", rows.toJson());
        } else {
            /*
            StringBuilder sb = new StringBuilder();
            sb.append("{Rows:[");
            sb.append("]}");
            req.setAttribute("data", sb.toString());*/
            req.setAttribute("data", Rows.getEmpty());
        }
        //yingjing.liu 20160725 add dept start
        List<TDept> depts= deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
        req.setAttribute("depts",depts);
        //yingjing.liu 20160725 add dept end

        req.getRequestDispatcher("search.jsp").forward(req, resp);
    }

    private void filetareByUsers(List<Fees> fees, HttpServletRequest req) {
        Map<String,TUser> userMap = (Map<String, TUser>) req.getSession().getAttribute("manageMentUser");
        if(userMap==null) //return了代表不是部门负责人
            return;

        Iterator<Fees> it = fees.iterator();
        while (it.hasNext()){
            Fees f = it.next();
            if(!userMap.containsKey(f.getUser_info().split("-")[0])){
                it.remove();
            }
        }
    }

    private void injectDept(List<Fees> billList) {
        List<TUser> users = userService.getAllUsers();
        Map<String,TUser> userMap = new HashMap<String, TUser>();
        for(int i=0 ; i<users.size(); i++){ //将系统中的数据放到map容器中去
            TUser u = users.get(i);
            userMap.put(u.getNumber(),u);
        }
        for(int i=0 ; i<billList.size(); i++){ //将系统中的数据放到map容器中去
            Fees le = billList.get(i);
            String number = le.getUser_info().split("-")[0];
            if(userMap.containsKey(number)){
                TUser u =userMap.get(number);
                le.setDeptName1(u.getDeptName1());
                le.setDeptName2(u.getDeptName2());
            }
        }
    }
    private void filetareByDept(List<Fees> leaves, HttpServletRequest req) {
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

        Iterator<Fees> it = leaves.iterator();
        while(it.hasNext()){
            Fees le = it.next();
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

}