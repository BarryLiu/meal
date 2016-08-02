package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.bean.TGroup;
import com.ragentek.mealsupplement.db.bean.TPermission;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.json.Permission;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.service.GroupService;
import com.ragentek.mealsupplement.service.PermissionService;
import com.ragentek.mealsupplement.service.UserGroupService;
import com.ragentek.mealsupplement.service.UserService;
import net.sf.json.JSON;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/14.
 */
public class PermissionServlet extends BaseServlet {
    private PermissionService permissionService;
    private UserService userService;
    private GroupService groupService;

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<TPermission> tPermissions = permissionService.getAll();
            Rows rows = new Rows();
            List<Permission> permissions = rows.getRowsAsList();
            for(TPermission tPermission : tPermissions) {
                Permission permission = new Permission(tPermission);
                List<TUser> users = permissionService.getPermissionUsers(permission.getId());
                permission.setUsers(users);
                List<TGroup> groups = permissionService.getPsermissionGroups(permission.getId());
                permission.setGroups(groups);
                permissions.add(permission);
            }
            String data = rows.toJson();
            req.setAttribute(DATA, data);
            //req.setAttribute("permissions", permissions);
            //所有的用户
            List<TUser> allUsers = userService.getNormalUsers();
            JSONArray userArray = JSONArray.fromObject(allUsers);
            req.setAttribute("allUsers", userArray.toString());
            //所有的部门
            List<TGroup> allDepartments = groupService.getAll();
            JSONArray departArray = JSONArray.fromObject(allDepartments);
            req.setAttribute("allDepartments", departArray.toString());
            req.getRequestDispatcher("user/permissionList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LeaveServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }

    public void assign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = null;
        try {
            Long[] userIds = getParam(req, "userIds", Long[].class);
            Long[] departmentIds = getParam(req, "departmentIds", Long[].class);
            Long permissionId = getParam(req, "permissionId", Long.class);
            permissionService.assignPermissionUsers(permissionId, userIds);
            permissionService.assignPermissionGroups(permissionId, departmentIds);
            json = Result.success("权限分配成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
            json = Result.error("发生后台错误，权限分配失败！");
        }
        resp.getOutputStream().write(json.getBytes("UTF-8"));
    }
}
