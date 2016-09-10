package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Dept;
import com.ragentek.mealsupplement.json.Leave;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.json.Statistic;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.GroupService;
import com.ragentek.mealsupplement.service.StatisticService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.TextUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by zixiao.zhang on 2016/4/13.
 */
public class StatisticServlet extends BaseServlet {
    private StatisticService statisticService;
    private UserService userService;
    private DeptService deptService;
    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String startDate = getParameter(req, "startDate");
            String endDate = getParameter(req, "endDate");
            String number = getParameter(req, "number");
            if(!TextUtil.isNullOrEmpty(startDate)) {
                req.setAttribute("startDate", startDate);
            }
            if(!TextUtil.isNullOrEmpty(endDate)) {
                req.setAttribute("endDate", endDate);
            }
            if(!TextUtil.isNullOrEmpty(number)) {
                req.setAttribute("number", number);
            }

            //yingjing.liu 20160726 add start 添加对时间的过滤  查询
            if(startDate == null) {
                Calendar cal = Calendar.getInstance();
                if(cal.get(Calendar.DAY_OF_MONTH)<=10){ // 判断   如果是 10号之前看的,就用上个月的一号   如果是10号之后就用本月一号
                    cal.add(Calendar.MONTH,-1);
                }
                cal.set(Calendar.DAY_OF_MONTH,1);
                startDate = DateTools.formatDateToString(cal.getTime(),"yyyy/MM/dd");
            }
            //yingjing.liu 20160726 add end
            req.setAttribute("startDate",startDate);

            List<Statistic> lst = statisticService.getBeans(number, startDate, endDate);

            injectDept(lst); //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
            filetareByDept(lst,req);// 根据前面传过来的一级部门与二级部门查询    ： 首先数据库查询全部 ,在这里根据条件remove list数据
            filetareByUsers(lst,req);// 主要是根据自己是不是部门负责人来查询的   如果是负责人才进去过滤 ,不过滤代表不是负责人


            Rows rows = new Rows(lst);
            String data = rows.toJson();
            req.setAttribute(DATA, data);

            //yingjing.liu 20160726 start 根据部门会显
            List<TDept> depts = deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
            req.setAttribute("depts", depts);
            //yingjing.liu 20160726 end

            /** 取出所有参与考勤的用户供选择 **/
            List<TUser> tUsers = userService.getNormalUsers();
            req.setAttribute("tUsers", tUsers);
            req.getRequestDispatcher("bill/statisticList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LeaveServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }
    private void filetareByUsers(List<Statistic> fees, HttpServletRequest req) {
        Map<String,TUser> userMap = (Map<String, TUser>) req.getSession().getAttribute("manageMentUser");
        if(userMap==null) //return了代表不是部门负责人
            return;

        Iterator<Statistic> it = fees.iterator();
        while (it.hasNext()){
            Statistic f = it.next();
            if(!userMap.containsKey(f.getNumber())){
                it.remove();
            }
        }
    }

    //给json数据的用户注入 一级,二级部门信息
    public void injectDept( List<Statistic> stcs ){
        List<TUser> users = userService.getAllUsers();
        Map<String,TUser> userMap = new HashMap<String, TUser>();
        for(int i=0 ; i<users.size(); i++){ //将系统中的数据放到map容器中去
            TUser u = users.get(i);
            userMap.put(u.getNumber(),u);
        }

        for(int i=0 ; i<stcs.size(); i++){ //将系统中的数据放到map容器中去
            Statistic le = stcs.get(i);
            if(userMap.containsKey(le.getNumber())){
                TUser u =userMap.get(le.getNumber());
                le.setDeptName1(u.getDeptName1());
                le.setDeptName2(u.getDeptName2());
            }
        }
    }
    private void filetareByDept(List<Statistic> leaves, HttpServletRequest req) {

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

        Iterator<Statistic> it = leaves.iterator();
        while(it.hasNext()){
            Statistic le = it.next();
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
