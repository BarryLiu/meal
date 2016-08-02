package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TBill;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.json.Dept;
import com.ragentek.mealsupplement.json.Leave;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.LeaveService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.TextUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by zixiao.zhang on 2016/3/28.
 */
public class LeaveServlet extends BaseServlet {
    private LeaveService leaveService;
    private UserService userService;
    private DeptService deptService;

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        try {
            String startDate = getParameter(req, "startDate");
            String endDate = getParameter(req, "endDate");
            String number = getParameter(req, "number");

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
            if(!TextUtil.isNullOrEmpty(startDate)) {
                req.setAttribute("startDate", startDate);
            }
            if(!TextUtil.isNullOrEmpty(endDate)) {
                req.setAttribute("endDate", endDate);
            }
            if(!TextUtil.isNullOrEmpty(number)) {
                req.setAttribute("number", number);
            }
            List<TLeave> tLeaves = leaveService.getBeans(number, startDate, endDate);
            Rows row = new Rows();
            List<Leave> leaves = row.getRowsAsList();
            for(TLeave tLeave : tLeaves) {
                leaves.add(new Leave(tLeave));
            }
            injectDept(leaves); //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
            filetareByDept(leaves,req);// 根据前面传过来的一级部门与二级部门查询    ： 首先数据库查询全部 ,在这里根据条件remove list数据
            filetareByUsers(leaves,req);// 主要是根据自己是不是部门负责人来查询的   如果是负责人才进去过滤 ,不过滤代表不是负责人

            String data = row.toJson();
            req.setAttribute(DATA, data);
            /** 取出所有参与考勤的用户供选择 **/
            List<TUser> tUsers = userService.getNormalUsers();
            req.setAttribute("tUsers", tUsers);
            //yingjing.liu 20160726 start 根据部门会显
            List<TDept> depts = deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
            req.setAttribute("depts", depts);
            //yingjing.liu 20160726 end
            //System.out.println("data==="+data);
            req.getRequestDispatcher("bill/leaveList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LeaveServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }
    private void filetareByUsers(List<Leave> fees, HttpServletRequest req) {
        Map<String,TUser> userMap = (Map<String, TUser>) req.getSession().getAttribute("manageMentUser");
        if(userMap==null) //return了代表不是部门负责人
            return;

        Iterator<Leave> it = fees.iterator();
        while (it.hasNext()){
            Leave f = it.next();
            if(!userMap.containsKey(f.getNumber())){
                it.remove();
            }
        }
    }
    private void filetareByDept(List<Leave> leaves, HttpServletRequest req) {
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

        Iterator<Leave> it = leaves.iterator();
        while(it.hasNext()){
            Leave le = it.next();
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

    //给json数据的用户注入 一级,二级部门信息
    public void injectDept( List<Leave> leaves ){
        List<TUser> users = userService.getAllUsers();
        Map<String,TUser> userMap = new HashMap<String, TUser>();
        for(int i=0 ; i<users.size(); i++){ //将系统中的数据放到map容器中去
            TUser u = users.get(i);
            userMap.put(u.getNumber(),u);
        }

        for(int i=0 ; i<leaves.size(); i++){ //将系统中的数据放到map容器中去
            Leave le = leaves.get(i);
           if(userMap.containsKey(le.getNumber())){
               TUser u =userMap.get(le.getNumber());
               le.setDeptName1(u.getDeptName1());
               le.setDeptName2(u.getDeptName2());
           }
        }
    }


    /**
     * 刪除 比打卡记录 大 的数据   删除
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       /* delete from t_fees  where  day_str > (select MAX(dotimes) from t_kaoqin );
        delete  from t_bill where  start_time > (select MAX(dotimes) from t_kaoqin );
        delete  from t_leave where  day_str > (select MAX(dotimes) from t_kaoqin );*/

        DBUtils.executeUpdate(" delete from t_fees  where  day_str > (select MAX(dotimes) from t_kaoqin ) ");
        DBUtils.executeUpdate(" delete  from t_bill where  start_time > (select MAX(dotimes) from t_kaoqin ) ");
        DBUtils.executeUpdate(" delete  from t_leave where  day_str > (select MAX(dotimes) from t_kaoqin ) ");

        String json = Result.success();
        System.out.println("result: "+json);
        resp.getOutputStream().write(json.getBytes("UTF-8"));
    }
}
