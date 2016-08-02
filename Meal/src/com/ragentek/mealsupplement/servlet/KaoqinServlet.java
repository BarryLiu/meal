package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TKaoqin;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Kaoqin;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.tools.DateTools;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by kui.li on 2014/9/2.
 */
public class KaoqinServlet extends BaseServlet {

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
                String sql = "";

                String sUserName = req.getParameter("sUserName"); // 查询姓名
                if(sUserName == null || "".equals(sUserName)) {
                    // 根据session查询当前用户的考勤记录
                    /*
                    sql = "select top 1 * from t_fees where user_info like '%-" + user.getName() + "'";
                    List<TFees> lstFees = DBUtils.query(sql,TFees.class);
                    if(lstFees != null && lstFees.size()>0) {
                        sUserName = lstFees.get(0).getUserInfo();
                        sql = "SELECT * FROM t_kaoqin WHERE names = '" + sUserName.split("-")[1] + "' and userid = '" + sUserName.split("-")[0] + "' ";
                    } else {
                        sql = "SELECT * FROM t_kaoqin WHERE names like '%" + user.getName() + "%'";
                    }*/
                    sql = "SELECT * FROM t_kaoqin WHERE names = '" + user.getName() + "' and userid = '" + user.getNumber() + "' ";
                } else {
                    sUserName = URLDecoder.decode(sUserName,"utf-8");
                    sql = "SELECT * FROM t_kaoqin WHERE names = '" + sUserName.split("-")[1] + "' and userid = '" + sUserName.split("-")[0] + "' ";
                }
                String sDate = req.getParameter("sDate"); // 查询开始日期
                String eDate = req.getParameter("eDate"); // 查询结束日期

                // wpf add
                System.out.println("WPF_1");
                sDate = DateTools.convertDateSeparator(sDate);
                eDate = DateTools.convertDateSeparator(eDate);

                System.out.println("sUserName:" + sUserName + ";sDate=" + sDate + ";eDate=" + eDate);
                req.setAttribute("sDate", (sDate==null || sDate.length()<10)?sDate:sDate.substring(0,10));
                req.setAttribute("eDate", (eDate==null || eDate.length()<10)?eDate:eDate.substring(0,10));
                req.setAttribute("sUserName", sUserName);


                if(sDate != null && !"".equals(sDate)) {
                    sql += " AND dotimes >= '" + sDate + " 00:00:00'";
                }
                if(eDate != null && !"".equals(eDate)) {
                    sql += " AND dotimes <= '" + eDate + " 23:59:59'";
                }
                //sql += " ORDER BY dotimes DESC";
                sql += " ORDER BY SUBSTRING(dotimes,1,10) DESC,SUBSTRING(dotimes, 12, 8) ASC";

                List<TKaoqin> lstKaoqin = DBUtils.query(sql,TKaoqin.class);
                if(lstKaoqin != null && lstKaoqin.size()>0) {
                    /*
                    StringBuilder sb = new StringBuilder();
                    sb.append("{Rows:[");
                    for(TKaoqin item : lstKaoqin) {
                        sb.append("{userid:'");
                        sb.append(item.getUserid());
                        sb.append("',names:'");
                        sb.append(item.getNames());
                        sb.append("',depart:'");
                        sb.append(item.getDepart());
                        sb.append("',dotimes:'");
                        sb.append(item.getDotimes());
                       // sb.append("',addresss:'");
                       // sb.append(item.getAddresss());
                        sb.append("',status:'");
                        sb.append(item.getStatus());
//                       if("1".equals(item.getStatus()))
//                            sb.append("通过");
//                        else
//                            sb.append("未通过");
                       // sb.append("',descs:'");
                       // sb.append(item.getDescs());
                        sb.append("'},");
                    }
                    sb.append("]}");
                    req.setAttribute("data", sb.toString());*/
                    Rows rows = new Rows();
                    List<Kaoqin> list = rows.getRowsAsList();
                    for(TKaoqin item : lstKaoqin) {
                        list.add(new Kaoqin(item));
                    }
                    req.setAttribute("data", rows.toJson());
                } else {
                    /*
                    StringBuilder sb = new StringBuilder();
                    sb.append("{Rows:[");
                    sb.append("]}");
                    req.setAttribute("data", sb.toString());*/
                    req.setAttribute("data", Rows.getEmpty());
                }
                req.getRequestDispatcher("kaoqin.jsp").forward(req, resp);
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