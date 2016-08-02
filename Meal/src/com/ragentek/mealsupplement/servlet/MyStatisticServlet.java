package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.json.Statistic;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.StatisticService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.TextUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/13.
 */
public class MyStatisticServlet extends BaseServlet {
    private StatisticService statisticService;
    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String startDate = getParameter(req, "startDate");
            String endDate = getParameter(req, "endDate");
            if(!TextUtil.isNullOrEmpty(startDate)) {
                req.setAttribute("startDate", startDate);
            }
            if(!TextUtil.isNullOrEmpty(endDate)) {
                req.setAttribute("endDate", endDate);
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
            
            User user = (User) req.getSession().getAttribute("userInfo");
            String number = user.getNumber();
            List<Statistic> lst = statisticService.getBeans(number, startDate, endDate);
            Rows rows = new Rows(lst);
            String data = rows.toJson();
            req.setAttribute(DATA, data);
            req.getRequestDispatcher("bill/mystatisticList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LeaveServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }
}
