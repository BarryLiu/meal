package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.FeeService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/12.
 */
public class FeeServiceImpl implements FeeService {

    @Override
    public boolean existFee(String number, String dayStr) {
        String sql = "select count(*) from t_fees where day_str='"+dayStr+"' and user_info like '"+number+"-%'";
        Integer count = DBUtils.uniqueResult(sql);
        return count != null && count > 0;
    }

    @Override
    public List<TFees> getUnhandleLeaves(String startDate, String endDate) {
        String sql = "select f.* from t_fees f,t_user u where f.number=u.number and u.stat=? and f.day_str>=? and f.day_str<=? and (f.status != '' or f.status is not null)" +
                " and exists(select _id from t_leave where number=f.number and day_str=f.day_str and bill_id is null) order by f.number asc,f.day_str asc";
        EntityList list = new EntityList(sql);
        list.add(User.STATUS_NORMAL);
        list.add(startDate);
        list.add(endDate);
        return DBUtils.query(list, TFees.class);
    }

    @Override
    public List<String> getUnhandleUserEmails(String startDate, String endDate) {
        final String sql = "select distinct u.email from t_fees f,t_user u where f.number=u.number and u.stat="+User.STATUS_NORMAL+" and f.day_str>='"+startDate+"' and f.day_str<='"+endDate+"' and (f.status != '' or f.status is not null)" +
                " and exists(select _id from t_leave where number=f.number and day_str=f.day_str and bill_id is null)";
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

    @Override
    public List<TFees> getFees(String number, String startDay, String endDay) {
        String sql = "select * from t_fees where number='"+number+"' and day_str>='"+startDay+"' and day_str<='"+endDay+"'";
        return DBUtils.query(sql, TFees.class);
    }

    @Override
    public TFees getFee(String number, String dayStr) {
        String sql = "select * from t_fees where number='"+number+"' and day_str='"+dayStr+"'";
        return DBUtils.uniqueBean(sql, TFees.class);
    }
}
