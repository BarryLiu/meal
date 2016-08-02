package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Rows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/9.
 */
public class TestServlet extends BaseServlet {
    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            List<TUser> lst = new ArrayList<TUser>();
            String sql = "select * from t_user";
            lst = DBUtils.query(sql, TUser.class);
            Rows rows = new Rows();
            rows.setRows(lst);
            System.out.println(rows.toJson());
            req.setAttribute("data", rows.toJson());
            req.getRequestDispatcher("user/userList.jsp").forward(req,resp);
        } catch(Exception e) {
            e.printStackTrace();
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }
}
