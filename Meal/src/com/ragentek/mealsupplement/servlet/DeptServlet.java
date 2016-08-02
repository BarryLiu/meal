package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Dept;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.TextUtil;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by yingjing.liu on 2016/7/22.
 */
public class DeptServlet extends BaseServlet{

    private DeptService deptService;
    private UserService userService;

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



        List<TUser> users = userService.getAllUsers();

        System.out.println("users.size();"+users.size());

        req.setAttribute("tUsers",users);
        List<TDept> tFirstDepts = deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
        req.setAttribute("datas", getDeptJsonTree(tFirstDepts));
        req.setAttribute("depts",tFirstDepts);
        req.getRequestDispatcher("/user/deptList.jsp").forward(req,resp);
    }

    private String getDeptJsonTree(  List<TDept> tdepts){


        Rows row = new Rows();
        List<Dept> depts = row.getRowsAsList();
        List<TDept> childs =null;
        for (TDept td:tdepts) {
            Dept d = new Dept(td);
            childs= deptService.getDeptsByType(Dept.TYPE_LEVEL_SECOND,td.getValue());
            d.setChildren(childs);
            depts.add(d);
        }
        String data = row.toJson(); 
        return  data;
    }

    @Override
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name  = req.getParameter("name");
        String comment = req.getParameter("comment");
        String duid1 = req.getParameter("duid1");
        String duid2 = req.getParameter("duid2");
        String duid3 = req.getParameter("duid3");
        String dtype = req.getParameter("type");
        TDept tdept = new TDept() ;
        tdept.setName(name);
        tdept.setComment(comment);

        if(!TextUtil.isNullOrEmpty(duid1)){
           TUser u1=  userService.getById(duid1);
            tdept.setDuid1(u1.getId().intValue());
            tdept.setDuname1(u1.getName());
        }

        if(!TextUtil.isNullOrEmpty(duid2)){
            TUser u2=  userService.getById(duid2);
            tdept.setDuid2(u2.getId().intValue());
            tdept.setDuname2(u2.getName());
        }

        if(!TextUtil.isNullOrEmpty(duid3)){
            TUser u3=  userService.getById(duid3);
            tdept.setDuid3(u3.getId().intValue());
            tdept.setDuname3(u3.getName());
        }

        String parentId = null;
        int type =Integer.parseInt(dtype);
        tdept.setDtype(type);
         if(type == Dept.TYPE_LEVEL_FIRST){ //一级部门
            tdept.setParentValue("-1");
             tdept.setParentId(-1);
             tdept.setDtype(Dept.TYPE_LEVEL_FIRST);
           //查询部门下的值
        }else if(type == Dept.TYPE_LEVEL_SECOND){//二级部门
             tdept.setDtype(Dept.TYPE_LEVEL_SECOND);
              parentId = req.getParameter("parentId");
            TDept tDept = deptService.selectById(parentId);
             tdept.setParentId(tDept.getId());
            tdept.setParentValue(tDept.getValue());
        }
        try {
            String nextValue = deptService.selectNextValue(parentId,tdept.getParentValue());
            tdept.setValue(nextValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        User user = (User) req.getSession().getAttribute("userInfo");
       //新增部门
        tdept.setCreateDate(new Timestamp(date.getTime()));
        tdept.setCreateBy(user.getName());
        deptService.inset(tdept);

        query(req,resp);
    }

    @Override
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name  = req.getParameter("name");                //value 的事情  赋值问题       如果改变部门, 就改变，没有就不改
        String comment = req.getParameter("comment"); 
        String duid1 = req.getParameter("duid1");
        String duid2 = req.getParameter("duid2");
        String duid3 = req.getParameter("duid3");

        TDept tdept = deptService.selectById(id);

        tdept.setName(name);
        tdept.setComment(comment);
        if(!TextUtil.isNullOrEmpty(duid1)){
            TUser u1=  userService.getById(duid1);
            tdept.setDuid1(u1.getId().intValue());
            tdept.setDuname1(u1.getName());
        }

        if(!TextUtil.isNullOrEmpty(duid2)){
            TUser u2=  userService.getById(duid2);
            tdept.setDuid2(u2.getId().intValue());
            tdept.setDuname2(u2.getName());
        }

        if(!TextUtil.isNullOrEmpty(duid3)){
            TUser u3=  userService.getById(duid3);
            tdept.setDuid3(u3.getId().intValue());
            tdept.setDuname3(u3.getName());
        }


        String dtype = req.getParameter("type");


        int type =Integer.parseInt(dtype);
        tdept.setDtype(type);
        if(type == Dept.TYPE_LEVEL_FIRST){ //一级部门
            tdept.setParentId(-1);
            tdept.setParentValue("-1");
        }else if(type == Dept.TYPE_LEVEL_SECOND){//二级部门
            try {
            String parentId = req.getParameter("parentId");
         //       deptService.selectByValue();
            //if(tdept.getParentId()!=Integer.parseInt(parentId)){    //判断有改变才去重新设置 parentId parentValue与 value的值
                TDept tDept = deptService.selectById(parentId);
                tdept.setParentId(tDept.getId());
                tdept.setParentValue(tDept.getValue());
                String nextValue = deptService.selectNextValue(parentId,tdept.getParentValue());

                List<TUser> users = userService.selectByDeptSecond(tdept.getId());  // 查询出在哪个部门下, 将要改的二级部门下的所有员工的 一级部门改变 名称和 id 指向
                for(int i=0 ; i<users.size();i++){
                    TUser u = users.get(i);
                    u.setDept1(tDept.getId().longValue());
                    u.setDeptName1(tDept.getName());
                    u.setDeptName2(tdept.getName());//修改了密码员工哪里也要修改
                    DBUtils.update(u);
                }

                tdept.setValue(nextValue);


     //       }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Date date = new Date();
        User user = (User) req.getSession().getAttribute("userInfo");

        tdept.setId(Integer.valueOf(id));
        tdept.setUpdateDate(new Timestamp(date.getTime()));
        tdept.setUpdateBy(user.getName());

        deptService.update(tdept);
        query(req,resp);//继续到查询页面
    }

    @Override
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String deptId = req.getParameter("deptId");
        deptService.deleteById(deptId);
        query(req,resp);
    }

    @Override
    protected void other(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String fdeptid = req.getParameter("fdeptid");
        TDept tdept = deptService.selectById(fdeptid);
        List<TDept> depts = deptService.getDeptsByType(Dept.TYPE_LEVEL_SECOND,tdept.getValue());
        StringBuffer sb = new StringBuffer("[");
        for (TDept td:depts) {
            sb.append("{ 'name' : '"+td.getName()+"','id' : '"+td.getId() +"' ,'value': '"+td.getValue()+"' } ,");
        }
        if(sb.length()>3) {
            sb = new StringBuffer(sb.substring(0, sb.toString().length() - 1));
        }
        sb.append("]");
        System.out.println(sb.toString());
       resp.getOutputStream().print(sb.toString());
    }
}
