package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.bean.TBillType;
import com.ragentek.mealsupplement.service.BillTypeService;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public class BillTypeServiceImpl implements BillTypeService {
    @Override
    public List<TBillType> getAll() {
        String sql = "select * from t_bill_type";
        List<TBillType>  tbts= DBUtils.query(sql, TBillType.class);
        tbts =sortBillType(tbts); // 为单据类型排序 规则写死,以用到次数大到小排序  如果不用这规则，直接注释掉
        return tbts;
    }

    //排序在这里写死规则 如果对单据的数量有改动，规则打破，重新以默认排序
    private   List<TBillType>  sortBillType(List<TBillType> tbts) {

       if(tbts!=null && tbts.size()==13){
           List<TBillType> tbs = new ArrayList<TBillType>();
           tbs.add(tbts.get(6));//调休正常
           tbs.add(tbts.get(10));//调休非正常
           tbs.add(tbts.get(9));// 出差
           tbs.add(tbts.get(7));// 补未打卡考勤
           tbs.add(tbts.get(8));//公出
           tbs.add(tbts.get(5));//事假
           tbs.add(tbts.get(12));//交通故障

           tbs.add(tbts.get(4));//产假（陪产假）
           tbs.add(tbts.get(1));//病假
           tbs.add(tbts.get(11));//其他假期
           tbs.add(tbts.get(0));//年假
           tbs.add(tbts.get(3));//婚假
           tbs.add(tbts.get(2));//丧假
           return tbs;
       }
        return tbts;
    }

    @Override
    public boolean existBillName(String billName) {
        String sql = "select count(*) from t_bill_type where bill_name=?";
        EntityList lst = new EntityList(sql);
        lst.add(billName);
        Integer count = DBUtils.uniqueResult(lst);
        return count != null && count > 0;
    }

    @Override
    public int deleteMulti(Long[] ids) {
        String sql = "delete from t_bill_type where _id"+DBUtils.getInHql(ids);
        System.out.println("deleteMulti:sql="+sql);
        return DBUtils.executeUpdate(sql);
    }

    @Override
    public TBillType getByBillName(String billName) {
        String sql = "select * from t_bill_type where bill_name=?";
        EntityList lst = new EntityList(sql);
        lst.add(billName);
        return DBUtils.uniqueBean(lst, TBillType.class);
    }
}
