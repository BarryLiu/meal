package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.LeaveService;
import com.ragentek.mealsupplement.tools.TextUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/28.
 */
public class LeaveServiceImpl implements LeaveService {
    @Override
    public List<TLeave> getBeans(String number, String startDate, String endDate) {
        String sql = "select * from t_leave where 1=1";
        EntityList list = new EntityList(sql);
        if(!TextUtil.isNullOrEmpty(number)) {
            list.append(" and number=?");
            list.add(number);
        }
        if(!TextUtil.isNullOrEmpty(startDate)) {
            list.append(" and day_str>=?");
            list.add(startDate);
        }
        if(!TextUtil.isNullOrEmpty(endDate)) {
            list.append(" and day_str<=?");
            list.add(endDate);
        }
        list.append(" order by day_str DESC");
        return DBUtils.query(list, TLeave.class);
    }

    @Override
    public List<TLeave> getUnhandleLeaves(String startDate, String endDate) {
        String sql = "select l.* from t_leave l,t_user u where l.number=u.number and u.stat=? and l.bill_id is NULL and l.day_str>=? and l.day_str<=? order by l.user_info ASC,l.day_str ASC";
        EntityList list = new EntityList(sql);
        list.add(User.STATUS_NORMAL);
        list.add(startDate);
        list.add(endDate);
        return DBUtils.query(list, TLeave.class);
    }

    @Override
    public List<String> getUnhandleUserEmails(String startDate, String endDate) {
        final  String sql = "select distinct u.email from t_leave l,t_user u where l.number=u.number and u.stat="+ User.STATUS_NORMAL+" and l.bill_id is NULL and l.day_str>='"+startDate+"' and l.day_str<='"+endDate+"'";
        return DBUtils.operator(new DBUtils.DBHandler<List<String>>() {
            private List<String> emails = new ArrayList<String>();
            private Statement stmt = null;
            private ResultSet rs = null;
            @Override
            public void handle(Connection conn) throws Exception {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String email = rs.getString("email");
                    emails.add(email);
                }
            }

            @Override
            public List<String> getResult() {
                return emails;
            }

            @Override
            public void handleFinally() throws Exception {
                if(rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(stmt != null) {
                    stmt.close();
                }
            }
        });
    }
}
