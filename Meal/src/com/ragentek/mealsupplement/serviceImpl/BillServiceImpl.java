package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.bean.TBill;
import com.ragentek.mealsupplement.db.bean.TBillType;
import com.ragentek.mealsupplement.json.Bill;
import com.ragentek.mealsupplement.service.BillService;
import com.ragentek.mealsupplement.tools.TextUtil;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public class BillServiceImpl implements BillService {

    @Override
    public List<TBill> getAll() {
        String sql = "select * from t_bill order by start_time_str desc";
        return DBUtils.query(sql, TBill.class);
    }

    @Override
    public List<TBill> getBeans(String number, String billName,String sDate) {
        EntityList lst = new EntityList("select * from t_bill where 1=1");
        if(!TextUtil.isNullOrEmpty(number)) {
            lst.append(" and number=?");
            lst.add(number);
        }
        if(!TextUtil.isNullOrEmpty(billName)) {
            lst.append(" and bill_name=?");
            lst.add(billName);
        }
        if(!TextUtil.isNullOrEmpty(sDate)){
            lst.append(" and end_time_str>?");
            lst.add(sDate);
        }
        lst.append(" order by start_time_str desc");
        return DBUtils.query(lst, TBill.class);
    }

    @Override
    public int deleteMulti(Long[] ids) {
        String sql = "delete from t_bill where _id"+DBUtils.getInHql(ids);
        return DBUtils.executeUpdate(sql);
    }

    @Override
    public List<TBill> getByIds(Long[] ids) {
        String sql = "select * from t_bill where _id"+DBUtils.getInHql(ids);
        return DBUtils.query(sql, TBill.class);
    }

    @Override
    public TBill getById(Long id) {
        String sql = "select * from t_bill where _id="+id;
        return DBUtils.uniqueBean(sql, TBill.class);
    }

    /**
     *
     * @param number
     * @param dayStr format:yyyy/MM/dd
     * @return
     */
    @Override
    public List<TBill> getNormalBills(String number, String dayStr) {
        String billSql = "select * from t_bill where SUBSTRING(start_time_str, 1, 10)<=? and SUBSTRING(end_time_str, 1, 10)>=? and number=? and bill_type != "+ Bill.TYPE_3+" and bill_type != "+Bill.TYPE_INVALID;
        EntityList billSqlList = new EntityList(billSql);
        billSqlList.add(dayStr);
        billSqlList.add(dayStr);
        billSqlList.add(number);
        return DBUtils.query(billSqlList, TBill.class);
    }
}
