package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TGroup;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.db.bean.TUserGroup;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.json.Dept;
import com.ragentek.mealsupplement.json.Kaoqin;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.ldap.LDAPControl;
import com.ragentek.mealsupplement.listener.UserTimer;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.KaoQin;
import com.ragentek.mealsupplement.tools.TextUtil;
import com.ragentek.mealsupplement.tools.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.Text;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class UserServlet extends BaseServlet {
//    private String data;
//    private User pms;
    private UserService userService;

    private DeptService deptService;
    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User pms = getBeanFromParam(req, "pms", User.class);
            List<TUser> tUsers = userService.queryUserList(pms);
            Rows rows = new Rows();
            List<User> users = rows.getRowsAsList();
            for(TUser tUser : tUsers) {
                users.add(new User(tUser));
            }
            String data = rows.toJson();
//            System.out.println(rows);
            req.setAttribute("data", rows.toJson());
            req.setAttribute("pms", pms);
            //injectAttrs(req);
            //yingjing.liu 20160725 add dept start
            List<TDept> depts= deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
            req.setAttribute("depts",depts);
            //yingjing.liu 20160725 add dept end

            req.getRequestDispatcher("user/userList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }

    /**
     * 该方法目前没有用，由Dwr.syncLdapUser代替了
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void syncWtkUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
        try {

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserServlet.syncWtkUser", e);
        }
        */
    }

    /*
    public String getData() {
        return data;
    }

    public User getPms() {
        return pms;
    }

    public void setPms(User pms) {
        this.pms = pms;
    }
    */

    //跳到add 界面, 拿到数据
    @Override
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String todo = req.getParameter("todo");
        if(todo == null){                       //跳到添加页面
            Map<String,TUser> users = LDAPControl.getInstance().getAllLdapUserNoNumber();
            List<TUser> sysUser=userService.getAllUsers();
            for(TUser u: sysUser){  //  如果没有的用户在数据库中已经存在就不需要     界面下拉框显示了
                users.remove(u.getLoginCount());
            }
            List<TUser> tUsers =  LDAPControl.getInstance().removeImpurty(users);//移除掉不需要的用户数据

            req.getSession().setAttribute("mldapUsers",users);
            req.setAttribute("ldapUsers",tUsers);

            //yingjing.liu 20160725 add dept start
            List<TDept> depts= deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
            req.setAttribute("depts",depts);
            //yingjing.liu 20160725 add dept end
            
            req.getRequestDispatcher("user/addUser.jsp").forward(req,resp);
        }else{ // 执行添加操作
            //1 插入用户
           Map<String,TUser> tUsers = (Map<String, TUser>) req.getSession().getAttribute("mldapUsers");
            String loginAccount =req.getParameter("loginAccount");
            String enterDate = req.getParameter("enterDate");
            String number =req.getParameter("number");

            String fid = req.getParameter("fid");
            String sid = req.getParameter("sid");



            enterDate =  enterDate.replaceAll("-","/");
            TUser tuser = tUsers.get(loginAccount);

            if(!TextUtil.isNullOrEmpty(fid)){
                TDept fd = deptService.selectById(fid);;
                tuser.setDept1(fd.getId().longValue());
                tuser.setDeptName1(fd.getName());
            }
            if(!TextUtil.isNullOrEmpty(sid)){
                TDept fd = deptService.selectById(sid);;
                tuser.setDept2(fd.getId().longValue());
                tuser.setDeptName2(fd.getName());
            }

            tuser.setNumber(number);
            tuser.setLoginCount(loginAccount);
            tuser.setEnterDate(enterDate);
            Long id = DBUtils.insert(tuser);
            System.out.println(tuser);
            tuser.setId(id);
            System.out.println("loginAccount: "+loginAccount+" "+enterDate+"   "+number +" id:"+id);

            // 2 计算它的异常和餐补    ，如果插入计算有错误还是对次步骤忽略吧

            try {
                KaoQin.countKaoQinForFees2(number,enterDate);
                UserTimer.handleOne(enterDate, number); //处理未打卡的情况

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            // 3 对其分组和权限的操作         : 1解析员工部门,  2 查出 数据库中对应部门id    3.对他们的关系表  t_user_group生成并 添加      (其中对于ldap中有的部门而数据库中没有 暂时没有将ldap部门 插入数据库 ，因为这样的情况应该很少)
            String deptart = tuser.getDepart();
            if(deptart!=null){
                String[] groups = deptart.split(",");
                String sql = "select * from t_group where group_name in ";
                StringBuffer sb = new StringBuffer("(");
                for(int i=0 ; i<groups.length; i++){
                    sb.append("'"+groups[i]+"',");
                }
                String inStr = sb.substring(0,sb.length()-1)+")";
                sql += inStr;
                System.out.println("queryGroup sql : "+sql);
                List<TGroup> ugs =  DBUtils.query(sql, TGroup.class);

                List<TUserGroup> tugs = new ArrayList<TUserGroup>();
                for(int i=0 ; i< ugs.size();i++){
                    TUserGroup tug = new TUserGroup();
                    tug.setUserid(tuser.getId());
                    tug.setGroupid(ugs.get(i).getId());
                    tugs.add(tug);
                }
                DBUtils.insertAll(tugs);


            }
            resp.getOutputStream().write(Result.success().getBytes("UTF-8"));
        }

    }

    @Override
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userId = req.getParameter("userId");
        String firstId = req.getParameter("firstId");
        String sencondId = req.getParameter("sencondId");

        TUser user = userService.getById(userId);

        if(!TextUtil.isNullOrEmpty(firstId)){
            TDept td = deptService.selectById(firstId);
            user.setDept1(td.getId().longValue());
            user.setDeptName1(td.getName());
        }else{
            user.setDept1(null);
            user.setDeptName1(null);
        }
        if(!TextUtil.isNullOrEmpty(sencondId)){
            TDept td = deptService.selectById(sencondId);
            user.setDept2(td.getId().longValue());
            user.setDeptName2(td.getName());
        }else{
            user.setDept2(null);
            user.setDeptName2(null);
        }
        DBUtils.update(user);
        if(req.getParameter("toPage")!=null){//指定跳到哪里?  true  1部门,显示部门里面为其分部门来 false用户里面分部门

            other(req,resp);
        }else{
            query(req,resp);
        }

    }

    @Override
    protected void other(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User pms = getBeanFromParam(req, "pms", User.class);
            String valueStr = req.getParameter("value");
            if(req.getParameter("toPage")!=null){            //第一次过来将其放到 session 是因为 ： 点击部门下的显示用户 后再部门下的分配权限后用于本页面的刷新,早知道就用异步的啦 这里用到两次
                pms = (User) req.getSession().getAttribute("cachePms");
                valueStr = (String) req.getSession().getAttribute("valueStr");
            }else{
                req.getSession().setAttribute("cachePms",pms);
                req.getSession().setAttribute("valueStr",valueStr);
            }

            Long deptId = pms.getDept1();
            if(deptId!=null){
                Integer value = Integer.parseInt(valueStr);
                //判断是一级部门还是二级部门
                System.out.println("value%100:"+deptId%100==0+" value/100:"+deptId%100);
                TDept dept = deptService.selectById(deptId+"");
                System.out.println("dept: "+dept);
                req.setAttribute("dept",dept);
                if(value%100==0){//一级部门
                    pms.setDept1(deptId);
                }else{          //二级部门
                    pms.setDept2(deptId);
                    pms.setDept1(null);
                }
            }
            List<TUser> tUsers = userService.queryUserList(pms);
            Rows rows = new Rows();
            List<User> users = rows.getRowsAsList();
            for(TUser tUser : tUsers) {
                users.add(new User(tUser));
            }
            String data = rows.toJson();
//            System.out.println(rows);
            req.setAttribute("data", rows.toJson());
            req.setAttribute("pms", pms);
            //injectAttrs(req);
            //yingjing.liu 20160725 add dept start
            List<TDept> depts= deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
            req.setAttribute("depts",depts);
            req.setAttribute("fromOther",1); //qury 和other里面的方法一样的,表示来自other就是部门里面点击 显示用户
            //yingjing.liu 20160725 add dept end

            /** 取出所有参与考勤的用户供选择 **/
            List<TUser> allUsers = userService.getNormalUsers();
            req.setAttribute("tUsers", allUsers);

            req.getRequestDispatcher("user/showDeptUser.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
