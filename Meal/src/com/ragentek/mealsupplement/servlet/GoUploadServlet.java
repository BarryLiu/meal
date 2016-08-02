package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TGroup;
import com.ragentek.mealsupplement.json.Dept;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by yingjing.liu on 2016/7/13.
 */
public class GoUploadServlet extends BaseServlet {

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       /* String sql = " select * from t_group ";
        List<TGroup> groups = DBUtils.query(sql, TGroup.class);
        for (int i=0 ; i<groups.size();i++){
            System.out.println(groups.get(i));
        }
        req.setAttribute("groups",groups);*/  // 原来根据部门导出报表取消换新部门

        String sql = " select * from t_dept where dtype = 1";
        List<TDept> depts = DBUtils.query(sql, TDept.class);
        req.setAttribute("depts",depts);
        req.getRequestDispatcher("/upload.jsp").forward(req,resp);
    }
}
