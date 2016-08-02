package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Fees;
import com.ragentek.mealsupplement.json.Leave;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.service.RestService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.TextUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yingjing.liu on 2016/7/18.
 */
public class RestServlet extends BaseServlet {
    RestService restService;
    UserService userService;
    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String startDay = req.getParameter("sDate");
        String endDay = req.getParameter("eDate");

        String [] groupIds = req.getParameterValues("");
        String number = req.getParameter("number");
        String dayStr = req.getParameter("dayStr");

        List<TFees> tFeeses = null;
        tFeeses = restService.getRestFeeses(startDay,endDay,null,number,dayStr);

        Rows row = new Rows();
        List<Fees> fees = row.getRowsAsList();
        
        for(TFees tf : tFeeses) { 
            // 根据 这天时间 得到他这天是哪个假   或者周末
            String hobili = getHoliday(tf.getDayStr());
            tf.setStatus(hobili);
            fees.add(new Fees(tf));
        }
        if(!TextUtil.isNullOrEmpty(number)){
            req.setAttribute("number",number);
        }
        String data = row.toJson();
        List<TUser> tUsers = userService.getNormalUsers();
        req.setAttribute("tUsers", tUsers);

        req.setAttribute("data",data);
        req.setAttribute("tFeeses",tFeeses);
        req.getRequestDispatcher("/rest/restList.jsp").forward(req,resp);
    }

   private  SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 传入时间 得到星期几    周一到周五都是返回 “法定假期”
     * @param dayStr
     * @return
     */
    private String getHoliday(String dayStr){
        String result = "";
        try {
        Date date  = sdf.parse(dayStr);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            switch(c.get(Calendar.DAY_OF_WEEK)-1){
                case 0:
                    result = "星期日";
                    break;
                case 6:
                    result = "星期六";
                    break;
                default :   // 周一到周五
                 result ="法定假期";
             }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
