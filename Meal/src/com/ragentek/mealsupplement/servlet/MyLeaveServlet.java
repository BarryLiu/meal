package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.json.Leave;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.LeaveService;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.TextUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/29.
 */
public class MyLeaveServlet extends BaseServlet {
    private LeaveService leaveService;

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        try {
            String startDate = getParameter(req, "startDate");
            String endDate = getParameter(req, "endDate");

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
            User user = (User) req.getSession().getAttribute("userInfo");
            String number = user.getNumber();
            List<TLeave> tLeaves = leaveService.getBeans(number, startDate, endDate);
            System.out.println(startDate+":"+endDate+":"+number+":"+tLeaves.size());
            Rows row = new Rows();
            List<Leave> leaves = row.getRowsAsList();
            for(TLeave tLeave : tLeaves) {
                leaves.add(new Leave(tLeave));
            }
            String data = row.toJson();
            req.setAttribute(DATA, data);
            req.getRequestDispatcher("bill/myLeaveList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LeaveServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }
}
