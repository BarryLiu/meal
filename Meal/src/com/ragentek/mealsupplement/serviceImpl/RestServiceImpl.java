package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.RestService;

import java.util.List;

/**
 * Created by yingjing.liu on 2016/7/18.
 */
public class RestServiceImpl implements RestService {

    @Override
    public List<TFees> getRestFeeses(String startDay, String endDay, Integer[] groupIds, String number, String dayStr) {

        StringBuffer sb = new StringBuffer(" select * from t_fees where fee_type = 3 and workovertime >0 ");
        if(dayStr !=null && !"".equals(dayStr.trim()))
            sb.append(  " and day_str = '"+dayStr+"'  -- 根据时间\n");
        if(number !=null && !"".equals(number.trim()))
            sb.append(" and number = '"+number+"'  -- 根据工号\n" );
         if(startDay != null && !"".equals(startDay.trim()))
             sb.append(" and '"+startDay+"' < day_str  -- 根据开始或结束时间");
        if(endDay !=null && !"".equals(endDay.trim()))
            sb.append(" and day_str < '"+endDay+"' " );

        if(groupIds != null && groupIds.length>0){ //根据部门查询     暂时 不做
            //" 查询部门 _ 根据部门id 充 t_user 表中找到对应的员工  再  模糊查询  eg：and number in (100219,100181)  或 and number in (select number from t_user where id in( select userid from t_user_group where groupid  in(27) )) "
         /*   "\t\tand number in (select number from t_user where id in(\n" +
                    "\t\t\tselect userid from t_user_group where groupid  in(27) \n" +
                    "\t\t)) "*/
        }
        sb.append(" order by day_str desc");

        String  sql = sb.toString();
        EntityList list = new EntityList(sql);
//        list.add(User.STATUS_NORMAL);
//        list.add(startDay);
//        list.add(endDay);
        return DBUtils.query(list, TFees.class);
    }
}
