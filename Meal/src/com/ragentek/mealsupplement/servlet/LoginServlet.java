package com.ragentek.mealsupplement.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TPermission;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.*;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.PermissionService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.ConfigUtil;
import com.ragentek.mealsupplement.tools.SerializeUtil;
import com.ragentek.mealsupplement.tools.TextUtil;
import com.ragentek.mealsupplement.tools.UserUtil;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.ldap.LDAPControl;

/**
 * Created by kui.li on 2014/9/2.
 */
public class LoginServlet extends BaseServlet {

//    private static final Logger logger = Logger.getLogger(LoginServlet.class);
    private PermissionService permissionService;
    private UserService userService;
    private DeptService deptService;

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        String passWord = req.getParameter("passWord");

        if(userName == null || passWord == null || "".equals(userName) || "".equals(passWord)) {
            req.setAttribute("error_msg","用户名或密码不能为空！");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        LDAPControl ldap = new LDAPControl();
        TUser tUser = ldap.getLadpUser(userName);
        if(tUser != null) { //存在于Ldap中
            if(ldap.authorCheck(userName, passWord)) { //用户名密码正确

                // yingjing.liu 20160624 add  start 没有 number 的用户也可以登录
                //对于ldap 中没有number的用户到 考勤系统看一下有没有 工号，不然登不上去    注：  如果ldap中的user没有 number 那么查出来的所有值都为 属性 null 这样的话 我用数据库中的信息
                  tUser =  checkNumber(tUser,userName,passWord);
                // yingjing.liu 20160624 add  end


                String number = tUser.getNumber();
                if(TextUtil.isNullOrEmpty(number)) {
                    req.setAttribute("error_msg", "非法用户：工号为空！");
                    req.getRequestDispatcher("/index.jsp").forward(req, resp);
                } else {
                    tUser.setLastLoginTime(new Timestamp(System.currentTimeMillis())); //更新最近登录时间
                    TUser entity = UserUtil.saveOrUpdateLdapUser(tUser);
                    User user = new User(entity);
                    req.getSession().setAttribute("userInfo", user);
                    //判断当前登陆用户是否为发邮件用户，如果为发邮件用户，保存其密码用于发邮件
                    if(userName.equals(ConfigUtil.getProperty("Mail_user"))) {
                        String mailPwd = SerializeUtil.getMailPwd();
                        if(!passWord.equals(mailPwd)) {
                            boolean res = SerializeUtil.setMailPwd(passWord);
                            if(!res) {
                                logger.error("save MailPwd Error : userName="+userName+",passWord="+passWord);
                            }
                        }
                    }
                    List<TPermission> permissions = null;
                    System.out.println(user.getUsername()+":isAdmin="+user.isAdmin());
                    if(user.isAdmin()) {
                        permissions = permissionService.getAll();
                    } else {
                        permissions = permissionService.getAllPermissionsForUser(entity.getId());
                    }
                    // 20160727 yingjing.liu add start 部门负责人的权限     部门负责人： 其权限低于正式权限  负责此部门，就得只能看到自己部门下的员工的情况, 如果有权限了就得去掉部门负责人身份  便于在查询的时候在根据部门负责人过滤
                    responsiblePremission(entity,permissions,req);
                    // 20160727 yingjing.liu add end
                    String treeData = permissionToJson(permissions);
                    System.out.println("treeData===="+treeData);
                    //treeData ="heihei密码错误\"sss\"";
                    treeData = treeData.replace("\"","'");
                    req.getSession().setAttribute("treeData", treeData);
                    req.getRequestDispatcher("/main.jsp").forward(req, resp);
                }
            } else {
                req.setAttribute("error_msg","密码错误！");
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("error_msg","用户名错误或该用户已离职！");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }

        /*
        if (ldap.authorCheck(userName, passWord)) {
            String name = ldap.userDN.split(",")[0].split("=")[1];
            System.out.println("acount=" + userName + ",password=" + passWord + ",name=" + name + " 登陆成功！");

            String sql = "SELECT * FROM t_user WHERE login_count=?";
            List<TUser> lstUser = DBUtils.query(sql,new String[]{userName},TUser.class);
            System.out.println(sql+":"+lstUser.size());
            TUser user = null;
            if(lstUser != null && lstUser.size()>0) {
                // update userinfos
                user = lstUser.get(0);
                System.out.println(user.getId()+":"+user.getName()+":"+user.getLastLoginTime());
                user.setName(name);
                user.setLoginPwd(passWord);
                user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
                DBUtils.update(user);
            } else {
                user = new TUser();
                user.setLoginCount(userName);
                user.setName(name);
                user.setLoginPwd(passWord);
                user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
                long rtn = DBUtils.insert(user);
                System.out.println("rtn=" + rtn);
            }
            session.setAttribute("userInfo",user);
            req.getRequestDispatcher("/main.jsp").forward(req, resp);
        } else {
        	req.setAttribute("error_msg","用户名或密码错误！");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }
        */
    }

    /**
     * 查询所有的部门负责人,看自己是否在其中 
     * @param permissions
     * @param req
     */
    private void responsiblePremission(TUser user,List<TPermission> permissions, HttpServletRequest req) {
        TUser tuser = userService.getByNumber(user.getNumber());

        String sql = "select * from t_dept where duid1 = "+tuser.getId()+" or duid2 = "+tuser.getId()+" or duid3 =  "+tuser.getId();
        List<TDept> depts = DBUtils.query(sql, TDept.class);
        if(depts.size()>0){//进去的才是部门负责人
            sql = "select * from t_permission where per_value in (100300,100301,100302,100303)"; //查询负责人权限
            List<TPermission> respPre = DBUtils.query(sql,TPermission.class);



            for (TPermission tp:respPre) {
                boolean flag = true;
                for (TPermission utp: permissions) {
                    if(tp.getId().equals(utp.getId())){
                        flag =false;
                    }
                }
                if(flag){ //没有这个权限
                    permissions.add(tp);
                }else {
                    return;//既然有了权限   就不用以部门负责人身份了
                }
            }
            //整理排序
            Collections.sort(permissions, new Comparator<TPermission>() {
                @Override
                public int compare(TPermission o1, TPermission o2) {
                    if(o1.getPerValue()>o2.getPerValue())
                        return 1;
                    else
                        return -1;
                }
            });


            //得到他管理的所有人  放入缓存中
           Map<String,TUser> userMap =new HashMap<String, TUser>();
            for (TDept td:depts) {
                User pms = new User();
                if(td.getDtype()== Dept.TYPE_LEVEL_FIRST)
                    pms.setDept1(td.getId().longValue());
                else if(td.getDtype()==Dept.TYPE_LEVEL_SECOND)
                    pms.setDept2(td.getId().longValue());

                List<TUser> users = userService.queryUserList(pms);
                for (TUser u:users) {
                    userMap.put(u.getNumber(),u);
                }
            }
            req.getSession().setAttribute("manageMentUser",userMap);
        }
    }

    private TUser checkNumber(TUser tUser, String userName, String passWord) {

        if(TextUtil.isNullOrEmpty(tUser.getNumber())){
            String sql = "select * from t_user where login_count = '"+userName+"'";
            List<TUser> users= DBUtils.query(sql,TUser.class);
            if(users.size()>0){     //  正常情况下 只会有一条数据
                tUser=users.get(0);
            }
        }
         return tUser;
    }

    /**
     * permissions是有序的
     * @param permissions
     * @return
     */
    private String permissionToJson(List<TPermission> permissions) {
        List<FirstDir> fds = new ArrayList<FirstDir>();
        for(TPermission permission : permissions) {
            if(permission.getPerType() == Permission.TYPE_FIRST_LEAVE) {
                FirstDir fd = new FirstDir(permission);
                fds.add(fd);
            } else if(permission.getPerType() == Permission.TYPE_SECOND_LEAVE) {
                Integer parentValue = permission.getParentValue();
                //获得父权限,这里认定权限是有序的
                FirstDir parent = fds.size()>0?fds.get(fds.size()-1):null;
                if(parent != null) {
                    List<SecondDir> children = parent.getChildren();
                    if(children == null) {
                        children = new ArrayList<SecondDir>();
                        parent.setChildren(children);
                    }
                    children.add(new SecondDir(permission));
                }
            }
        }
        JSONArray jsonArray = JSONArray.fromObject(fds);
        return jsonArray.toString();
    }

    @Override
    protected void other(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().removeAttribute("userInfo");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

}