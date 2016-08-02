package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.FeesList;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.tools.DateTools;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kui.li on 2014/9/2.
 */
public class MySupplementServlet extends BaseServlet {

//    private static final Logger logger = Logger.getLogger(LoginServlet.class);

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        //
        Object obj = req.getSession().getAttribute("userInfo");
        if(obj != null) {
            try {
                //TUser user = (TUser) obj;
                User user = (User) obj;

                String sDate = req.getParameter("sDate"); // 查询开始日期
                String eDate = req.getParameter("eDate"); // 查询结束日期

                // wpf add
                System.out.println("WPF_2");
                sDate = DateTools.convertDateSeparator(sDate);
                eDate = DateTools.convertDateSeparator(eDate);


                //yingjing.liu 20160726 add start 添加对时间的过滤  查询
                if(sDate == null) {
                    Calendar cal = Calendar.getInstance();
                    if(cal.get(Calendar.DAY_OF_MONTH)<=10){ // 判断   如果是 10号之前看的,就用上个月的一号   如果是10号之后就用本月一号
                        cal.add(Calendar.MONTH,-1);
                    }
                    cal.set(Calendar.DAY_OF_MONTH,1);
                    sDate = DateTools.formatDateToString(cal.getTime(),"yyyy/MM/dd");
                }
                //yingjing.liu 20160726 add end
                


                System.out.println("name:" + user.getName() + ";sDate=" + sDate + ";eDate=" + eDate);
                req.setAttribute("sDate", sDate);
                req.setAttribute("eDate", eDate);

                //modify 20160329 zhangzixiao start
                //String sql = "SELECT * FROM t_fees WHERE user_info like '%-" + user.getName() + "'";
                String userInfo = user.getNumber()+"-"+user.getName();
//                String sql = "SELECT * FROM t_fees WHERE user_info='"+userInfo+"'";

                String sql = "SELECT id,user_info,day_str,fee1,fee2,fee3,fee4,fee5,start_time, end_time,workovertime,status,name,number,fee_type," +
                        "(select top 1 bill_name from t_bill where convert(char(10),start_time,111) = f.day_str and number = f.number  ) as deal_status " +
                        " FROM t_fees f WHERE user_info='"+userInfo+"'"; //


                //modify 20160329 zhangzixiao end
                if(sDate != null && !"".equals(sDate)) {
                    sql += " AND day_str >= '" + sDate + "'";
                }
                if(eDate != null && !"".equals(eDate)) {
                    sql += " AND day_str <= '" + eDate + "'";
                }
                sql += " ORDER BY day_str DESC";

                List<TFees> lstFees = DBUtils.query(sql,TFees.class);
                if(lstFees != null && lstFees.size()>0) {
                    /*
                    StringBuilder sb = new StringBuilder();
                    sb.append("{Rows:[");
                    String sDateStr = "";
                    String eDateStr = "";
                    int tFee1=0,tFee2=0,tFee3=0,tFee4=0,tFee5=0;
                    int tOvertime = 0;
                    for(TFees item : lstFees) {
                        if("".equals(sDateStr))
                            sDateStr = item.getDayStr();
                        eDateStr = item.getDayStr();
                        tFee1 += item.getFee1();
                        tFee2 += item.getFee2();
                        tFee3 += item.getFee3();
                        tFee4 += item.getFee4();
                        //tFee5 += item.getFee5();
                        tOvertime += item.getWorkovertime();
                        sb.append("{day_str:'");
                        sb.append(item.getDayStr());
                        sb.append("',user_info:'");
                        sb.append(item.getUserInfo());
                        sb.append("',start_time:'");
                        sb.append(item.getStartTime());
                        sb.append("',end_time:'");
                        sb.append(item.getEndTime());
                        sb.append("',workovertime:'");
                        sb.append(item.getWorkovertime());
                        sb.append("',fee1:'");
                        sb.append(item.getFee1());
                        sb.append("',fee2:'");
                        sb.append(item.getFee2());
                        sb.append("',fee3:'");
                        sb.append(item.getFee3());
                        sb.append("',fee4:'");
                        sb.append(item.getFee4());
                        //sb.append("',fee5:'");
                        //sb.append(item.getFee5());
                        sb.append("',total:'");
                        sb.append(item.getFee1() + item.getFee2() + item.getFee3() + item.getFee4() + item.getFee5());
                        sb.append("'},");
                    }
                    sb.append("{day_str:'");
                    sb.append("合计");
                    sb.append("',start_time:'");
                    sb.append(sDateStr + "~" + eDateStr);
                    sb.append("',end_time:'");
                    sb.append("");
                    sb.append("',workovertime:'");
                    sb.append(tOvertime);
                    sb.append("',fee1:'");
                    sb.append(tFee1);
                    sb.append("',fee2:'");
                    sb.append(tFee2);
                    sb.append("',fee3:'");
                    sb.append(tFee3);
                    sb.append("',fee4:'");
                    sb.append(tFee4);
                    //sb.append("',fee5:'");
                    //sb.append(tFee5);
                    sb.append("',total:'");
                    //sb.append(tFee1 + tFee2 + tFee3 + tFee4 + tFee5);
                    sb.append(tFee1 + tFee2 + tFee3 + tFee4);
                    sb.append("'},");
                    sb.append("]}");
                    req.setAttribute("data", sb.toString());
                    System.out.println(sb.toString());
                    */
                    FeesList list = new FeesList();
                    list.addAll(lstFees);
                    Rows rows = new Rows(list.getList());
                    String data = rows.toJson();
                    req.setAttribute("data", data);
                    //System.out.println(data);
                } else {
                    /*
                    StringBuilder sb = new StringBuilder();
                    sb.append("{Rows:[");
                    sb.append("]}");
                    req.setAttribute("data", sb.toString());
                    */
                    req.setAttribute("data", Rows.getEmpty());
                }
                req.getRequestDispatcher("my_supplement.jsp").forward(req, resp);
            }catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("error_msg", "登陆超时，请重新登陆！");
                //resp.sendRedirect("/index.jsp");
                goToIndexPage(req, resp);
            }
        } else {
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }
}