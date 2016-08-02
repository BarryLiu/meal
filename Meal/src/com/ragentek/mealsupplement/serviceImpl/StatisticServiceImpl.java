package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.json.Statistic;
import com.ragentek.mealsupplement.service.StatisticService;
import com.ragentek.mealsupplement.tools.TextUtil;

import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/13.
 */
public class StatisticServiceImpl implements StatisticService {
    @Override
    public List<Statistic> getBeans(String number, String startDay, String endDay) {
        EntityList lst = new EntityList("select t1.user_info,isnull(totalfees, 0) totalfees,isnull(totalovertimes, 0) totalovertimes,isnull(totalleaves, 0) totalleaves,isnull(unhandledleaves, 0) unhandledleaves from");
        /** t_fees 获得用户信息，总餐补，总加班时间 start **/
        lst.append(" (select user_info,(sum(fee1)+sum(fee2)+sum(fee3)+sum(fee4)+sum(fee5)) totalfees,sum(workovertime) totalovertimes from t_fees where 1=1");
        if(!TextUtil.isNullOrEmpty(number)) {
            lst.append(" and user_info like '"+number+"-%'");
        }
        if(!TextUtil.isNullOrEmpty(startDay)) {
            lst.append(" and day_str>='"+startDay+"'");
        }
        if(!TextUtil.isNullOrEmpty(endDay)) {
            lst.append(" and day_str<='"+endDay+"'");
        }
        lst.append(" group by user_info) t1");
        /** end **/
        lst.append(" full join");
        /** t_leave 获得总调休时间 start **/
        /*
        lst.append(" (select l.user_info,sum(l.total_hours) totalleaves from t_leave l left join t_bill b on l.bill_id=b._id where (b.bill_type=0 or b.bill_type=2 or b.bill_type is null)");
        if(!TextUtil.isNullOrEmpty(number)) {
            lst.append(" and l.user_info like '"+number+"-%'");
        }
        if(!TextUtil.isNullOrEmpty(startDay)) {
            lst.append(" and l.day_str>='"+startDay+"'");
        }
        if(!TextUtil.isNullOrEmpty(endDay)) {
            lst.append(" and l.day_str<='"+endDay+"'");
        }
        lst.append(" group by l.user_info) t2");
        */
        /** end **/
        /** t_leave 获得总调休时间和总共的未处理时间 start **/
        /*
        String t = "select a1.totalleaves,a2.unhandledleaves,isnull(a1.user_info, a2.user_info) user_info from \n" +
                "(select l.user_info,sum(l.total_hours) totalleaves from t_leave l left join t_bill b on l.bill_id=b._id where b.bill_type=0 group by l.user_info) a1\n" +
                "full join\n" +
                "(select l.user_info,sum(l.total_hours) unhandledleaves from t_leave l where bill_id is null group by l.user_info) a2\n" +
                "on a1.user_info=a2.user_info";*/
        lst.append(" (");
        lst.append("select a1.totalleaves,a2.unhandledleaves,isnull(a1.user_info, a2.user_info) user_info from");
        lst.append(" (select l.user_info,sum(l.total_hours) totalleaves from t_leave l left join t_bill b on l.bill_id=b._id where b.bill_type=0");
        if(!TextUtil.isNullOrEmpty(number)) {
            lst.append(" and l.user_info like '"+number+"-%'");
        }
        if(!TextUtil.isNullOrEmpty(startDay)) {
            lst.append(" and l.day_str>='"+startDay+"'");
        }
        if(!TextUtil.isNullOrEmpty(endDay)) {
            lst.append(" and l.day_str<='"+endDay+"'");
        }
        lst.append(" group by l.user_info) a1");
        lst.append(" full join");
        lst.append(" (select l.user_info,sum(l.total_hours) unhandledleaves from t_leave l where bill_id is null");
        if(!TextUtil.isNullOrEmpty(number)) {
            lst.append(" and l.user_info like '"+number+"-%'");
        }
        if(!TextUtil.isNullOrEmpty(startDay)) {
            lst.append(" and l.day_str>='"+startDay+"'");
        }
        if(!TextUtil.isNullOrEmpty(endDay)) {
            lst.append(" and l.day_str<='"+endDay+"'");
        }
        lst.append(" group by l.user_info) a2");
        lst.append(" on a1.user_info=a2.user_info");
        lst.append(") t2");
        /** end **/
        lst.append(" on t1.user_info=t2.user_info");
        System.out.println(lst.getSql());
        return DBUtils.query(lst, Statistic.class);
    }
}
