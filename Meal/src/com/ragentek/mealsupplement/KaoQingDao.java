package com.ragentek.mealsupplement;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.KaoqinDto;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.json.Fees;
import com.ragentek.mealsupplement.service.FeeService;
import org.apache.poi.util.SystemOutLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingjing.liu on 2016/5/24.
 */
public class KaoQingDao {

    public static void main(String[] args) throws SQLException {
        String startDay = "2016/05/01";
        String endDay =  "2016/06/01";

        //查询汇总信息
        KaoQingDao dao = new KaoQingDao();
        List<KaoqinDto> dtos =   dao.queryByDate(startDay,endDay);
        System.out.println("dtos.size:"+dtos.size());


        //方式1： 查询详细信息   约140s   一个月的信息
      /*  Long star = System.currentTimeMillis();
        List<KaoqinDto> daos = dao.queryByDetails(dtos);
        System.out.println("daos.size:"+daos.size());
        Long end =System.currentTimeMillis();
        System.out.println(" 发8条语句 一共耗时： "+((end-star)/(1000)));*/


        //方式2： 以一条语句查询  约160s  一个月的信息
     /*   Long star = System.currentTimeMillis();
        List<KaoqinDto> dtoDetails = dao.queryByDateDetails(dtos,startDay,endDay);
        Long end =System.currentTimeMillis();
        System.out.println(" 两条语句 子查询 一共耗时： "+((end-star)/(1000))+" s, 数据量："+dtoDetails.size());
*/
        // 方式3：以方式2 分解成信息多次发送   约13s  一个月的信息
        Long star = System.currentTimeMillis();
        List<KaoqinDto> allDetails = new ArrayList<KaoqinDto>();
        List<KaoqinDto> one = new ArrayList<KaoqinDto>();
        for(int i = 0 ; i<dtos.size();i++){
            one.add(dtos.get(i));
            if(i%15==0|| i == dtos.size()-1){
                List<KaoqinDto> oneDetails = dao.queryByDateDetails(one,startDay,endDay);

                //System.out.println("oneDetails.size()=" +oneDetails.size());
//                for(int  m=0;m<oneDetails.size();m++){
//                    System.out.println(oneDetails.get(m));
//                }

                allDetails.addAll(oneDetails);
                one = new ArrayList<KaoqinDto>();

            }
        }
        Long end =System.currentTimeMillis();
        System.out.println(" 两条语句 子查询 一共耗时： "+((end-star)/(1000))+" s, 数据量："+allDetails.size());

        // 方式4：以方式2 3 分解成信息多次发送   约10s  一个月的信息 希望在 方式3上面提高，然而并没有
       /* Long star = System.currentTimeMillis();
        List<KaoqinDto> allDetails = new ArrayList<KaoqinDto>();
        List<KaoqinDto> one = new ArrayList<KaoqinDto>();

        String oneDetailSql = dao.getDetailSql(startDay,endDay," ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        //System.out.println("oneDetailSql:"+oneDetailSql);
        Connection conn = DBUtils.openConnection();
        PreparedStatement ps = conn.prepareStatement(oneDetailSql);
       for(int i = 0 ; i<dtos.size();i++){
            one.add(dtos.get(i));
            if(i!=0 && i%15==0|| i == dtos.size()-1){
                StringBuffer sb = new StringBuffer("(");
                for(int m=0 ; m<one.size();m++){
                    KaoqinDto dto = one.get(m);
                    sb.append(dto.getNumber()+", ");
                    ps.setString(m+1,dto.getNumber());
                }
                String numberStr = sb.toString().substring(0,sb.length()-2)+")";
                System.out.println("numberStr: "+numberStr);
               //  ps.setString(1,numberStr);
               ResultSet rs =  ps.executeQuery();
                List<KaoqinDto> oneDetails = dao.queryDetails(rs);
                rs.close();

                allDetails.addAll(oneDetails);
                one = new ArrayList<KaoqinDto>();
            }
        }
        ps.close();
        conn.close();
        Long end =System.currentTimeMillis();
        System.out.println(" 第四种方式查询 子查询 一共耗时： "+((end-star)/(1000))+" s, 数据量："+allDetails.size());*/
    }
    /**
     * 第五中查詢方式
     * @param dtos
     * @param startDay
     * @param endDay
     * @return
     */
    public List<KaoqinDto> queryByDateAllDetails(List<KaoqinDto> dtos, String startDay, String endDay) throws SQLException {

        String sqlDetail = getDetailSql(startDay,endDay);
        System.out.println(sqlDetail);

        Connection conn = DBUtils.openConnection();
        PreparedStatement pss = conn.prepareStatement(sqlDetail);
        ResultSet rs = pss.executeQuery();
        List<KaoqinDto> dtoDetails = queryDetails(rs);
        return dtoDetails;
    }
    /**
     * 根据时间查询所有的用户详情
     * @param startDay
     * @param endDay
     * @return
     */
    private String getDetailSql(String startDay ,String endDay ) {
        // 修改查询语句    对  公出 ， 事假 ，病假 ， 等 加个  sum  ： 原因 : 针对一个人 一天 公出，事假，病假 等 一天只会有一次表单  ， 出现了 一天多次公出/ 病假/ 事假等，  原本是查的单个的时间  ，现求出一天 这些情况的和
        String sqlDetail = "select * " +
              /*  ",(" +
                "select count(*) from t_leave where  number= f.number  and day_str = f.day_str and stat in('迟到','早退')\n" +
                ") as 'ztcd' "+*/
                ",(" +
                " select count(*) from t_bill \n" +
                " where number = f.number  and bill_name like '%未打卡%'  and  day(cast(f.day_str as datetime) ) = DAY(start_time) " +
                "\n) as 'bstatus'" +
                " ,( " +
                " select sum(total_hours) from t_bill " +
                " where number =  f.number  and bill_name like '%公出%' " +
                "  and convert(char(10),start_time,111) = f.day_str " +
                " ) as 'gongchu' , \n" +
                " ( " +
                " select count(*) from t_bill " +
                "where number = f.number  and bill_name like '%出差%' " +
                "  and convert(char(10),start_time,111) <= f.day_str " +
                " and  convert(char(10),end_time,111) >= f.day_str" +
                ") as 'chucai', \n" +
                "(" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%事假%'" +
                " and day_str = f.day_str " +
                ") as 'shijia',\n" +
                "(" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%病假%'" +
                " and day_str = f.day_str " +
                ") as 'bingjia',\n" +
                " (" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%年假%'" +
                " and day_str = f.day_str " +
                " ) as 'nianjia',\n" +
                // yingjing.liu add   2016/07/11 start  添加 丧假 等假期
                "( select sum(total_hours) from t_leave where number =f.number  and bill_name like '%丧假%' and day_str = f.day_str  ) as 'sanjia',\n" +
                "  ( select sum(total_hours) from t_leave where number =f.number  and bill_name like '%婚假%' and day_str = f.day_str  ) as 'hunjia',\n" +
                "  ( select sum(total_hours) from t_leave where number =f.number  and bill_name like '%产假（陪产假）%' and day_str = f.day_str  ) as 'chanjia',\n" +
                "  ( select sum(total_hours) from t_bill where number =f.number  and bill_name like '%交通故障%'  and convert(char(10),start_time,111) <= f.day_str  and  convert(char(10),end_time,111) >= f.day_str  ) as 'jiaotong',"+
                // yingjing.liu add   2016/07/11 end
                " (" +
                " select sum(total_hours) from t_bill where number =f.number  and bill_name like '%其他假期%'" +
                "   and   convert(char(10),start_time,111) <= f.day_str  and  convert(char(10),end_time,111) >= f.day_str " +
                " ) as 'qitajia',\n" +
                "(" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%调休（正常）%' " +
                " and day_str = f.day_str  " +
                ") as 'tiaoxiuz', \n" +
                " (" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%调休（非正常）%' " +
                " and day_str = f.day_str " +
                ") as 'tiaoxiuf' \n " +
                "  from t_fees f where  cast('" + startDay + "' as datetime) <=  cast(day_str as datetime)  and   cast(day_str as datetime) <= cast('" + endDay + "' as datetime) \n";
        return sqlDetail;
    }

    public List<KaoqinDto> queryByDetails(List<KaoqinDto> dtos) throws SQLException {
        Connection conn = DBUtils.openConnection();

        String oneDetailSql = null;// getOneDetailSql(startDay,endDay);
        System.out.println(oneDetailSql);
        oneDetailSql = "select * from t_fees where number = ?  and  cast('2016/05/01' as datetime) <=  cast(day_str as datetime)  and   cast(day_str as datetime) <= cast('2016/06/01' as datetime)";
        PreparedStatement pss = conn.prepareStatement(oneDetailSql);

        List<KaoqinDto> daos = new ArrayList<KaoqinDto>();
        for(int i=0 ; i<dtos.size();i++){
            //if(i == 5) break;
            long t1 = System.currentTimeMillis();
            KaoqinDto d =  dtos.get(i);
            pss.setString(1,d.getNumber());

            ResultSet rss = pss.executeQuery();
             /*List<KaoqinDto> oneDetails = queryDetails(rss);
             d.setDtos(oneDetails);*/
           /* if("100185".equals(dto.getNumber())){ debug 这个有病假等信息
                System.out.println(" one"+oneDetails);
            }*/

            while(rss.next()) {
                KaoqinDto dao = new KaoqinDto();
                dao.setName(rss.getString("name"));
                dao.setNumber(rss.getString("number"));
                dao.setDayStr(rss.getString("day_str"));
                dao.setZao(rss.getInt("fee1"));
                dao.setZhong(rss.getInt("fee2"));
                dao.setWan(rss.getInt("fee3"));
                dao.setXiao(rss.getInt("fee4"));
                dao.setStatus(rss.getString("status"));
                dao.setJiaban(rss.getInt("workovertime"));
                dao.setStartTime(rss.getString("start_time"));//签到时间
                dao.setEndTime(rss.getString("end_time")); //签退时间


                daos.add(dao);
            }

            rss.close();
            long t2 = System.currentTimeMillis();
            System.out.println(d.getNumber()+" time:"+(t2-t1));

        }
        pss.close();
        pss = conn.prepareStatement("select total_hours from t_bill  where number =  ?  and bill_name like '%公出%'   and convert(char(10),start_time,111) = ?");
        long t1 = System.currentTimeMillis();
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());
            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setGongchu(rss.getInt("total_hours"));
            }
            rss.close();
        }
        pss.close();
        long t2 = System.currentTimeMillis();
        System.out.println("所有公出 time:"+(t2-t1));

        pss = conn.prepareStatement("select  count(total_hours) from t_bill  where number =  ?  and bill_name like '%出差%'   and convert(char(10),start_time,111) = ?");
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());
            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setChucai(rss.getInt(1));
            }
            rss.close();
        }
        pss.close();

        pss = conn.prepareStatement("select total_hours from t_leave where number =?  and bill_name like '%事假%' and day_str = ? ");
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());

            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setGongchu(rss.getInt("total_hours"));
            }
            rss.close();
        }
        pss.close();

        pss = conn.prepareStatement("select total_hours from t_leave where number =?  and bill_name like '%病假%' and day_str = ?");
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());
            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setGongchu(rss.getInt("total_hours"));
            }
            rss.close();
        }
        pss.close();

        pss = conn.prepareStatement("select total_hours from t_leave where number =?  and bill_name like '%年假%' and day_str = ?");
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());
            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setGongchu(rss.getInt("total_hours"));
            }
            rss.close();
        }
        pss.close();

        pss = conn.prepareStatement("select total_hours from t_leave where number =?  and bill_name like '%其它假期%' and day_str = ?");
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());
            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setGongchu(rss.getInt("total_hours"));
            }
            rss.close();
        }
        pss.close();

        pss = conn.prepareStatement("select sum(total_hours) from t_leave where number =?  and bill_name like '%调休（正常）%'  and day_str = ?");
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());
            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setGongchu(rss.getInt(1));
            }
            rss.close();
        }
        pss.close();

        pss = conn.prepareStatement(" select sum(total_hours) from t_leave where number =?  and bill_name like '%调休（非正常）%'  and day_str = ?");
        for(KaoqinDto dao : daos) {
            pss.setString(1, dao.getNumber());
            pss.setString(2, dao.getDayStr());
            ResultSet rss = pss.executeQuery();
            if(rss.next()) {
                dao.setGongchu(rss.getInt(1));
            }
            rss.close();
        }
        pss.close();

        for(KaoqinDto dao : daos) {
            System.out.println(dao.getNumber()+":"+dao.getDayStr()+":"+dao.getGongchu());
        }

        conn.close();
        return daos;
    }

    /**
     * 查询考勤汇总信息
     * @param startDay
     * @param endDay
     * @return
     * @throws SQLException
     */
    public synchronized List<KaoqinDto> queryByDate(String startDay, String endDay) throws SQLException {
        List<KaoqinDto> dtos = new ArrayList<KaoqinDto>();

        String sql = getQuerySql(startDay, endDay);
        System.out.println("查询汇总："+sql);
        Connection conn = DBUtils.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();


        while (rs.next()) {
            KaoqinDto dto = new KaoqinDto(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getInt(4),
                    rs.getInt(5),
                    rs.getInt(6),
                    rs.getInt(7),
                    rs.getInt(8),
                    rs.getInt(9),
                    rs.getInt(10),
                    rs.getInt(11),
                    rs.getInt(12),
                    rs.getInt(13),
                    rs.getInt(14),
                    rs.getInt(15),
                    rs.getInt(16),
                    rs.getInt(17),
                    rs.getInt(18),
                    rs.getInt(19),
                    rs.getInt(20)
            );

            dto.setSangjia(rs.getInt("sangjia")+"");
            dto.setHunjia(rs.getInt("hunjia")+"");
            dto.setChanjia(rs.getInt("cangjia")+"");
           dto.setJiaotong(rs.getInt("jiaotong")+"");

          //  System.out.println("sql: " + sql);

           // List<KaoqinDto> oneDetails =  queryKqDetailByNumber(dto.getNumber(),startDay,endDay); //查询一个人的信息
          //  System.out.println(" number："+dto.getNumber());
            dtos.add(dto);
        }
        rs.close();
        ps.close();
        System.out.println("查完汇总了~~");
     //   System.out.println(dtos.size());
        return dtos;
    }


    private String getQuerySql(String startDay, String endDay) {
        String sql = "select name ,number," +
                 "count(*)" +         //本来记的是员工所有上班次数

                " as 'shidao' " +
                ",\n" +
                "\t(\t\n" +
              /*  "\t\tselect count(*) from t_fees \n" +
                "\t\t\twhere number = f.number and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime) and status is not null and status <> ''\n" + */
                    "select count(*) from t_leave \n" +
                "\t\twhere number = f.number and cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime) and stat in('迟到','早退')"+
                "\t ) as 'ztcd',  \n" +
                "\t (select count(*) from t_bill \n" +
                "\t\twhere number = f.number  and bill_name like '%未打卡%'  and  cast('"+startDay+"' as datetime) <=  start_time  and  end_time <= cast('"+endDay+"' as datetime) \n" +
                "\t\t) as 'weidaka',\n" +
                "\t (select sum(total_hours) from t_bill \n" +
                "\t\twhere number = f.number  and bill_name like '%公出%'  and  cast('"+startDay+"' as datetime) <=  start_time  and  end_time <= cast('"+endDay+"' as datetime) \t\n" +
                "\t\t) as 'gongchu',\n" +
                "\t (select count(*) from t_bill\n" +
                "\t\t where number = f.number  and bill_name like '%出差%'   and  cast('"+startDay+"' as datetime) <=  start_time  and  end_time <= cast('"+endDay+"' as datetime) \n" +
                "\t ) as 'chucai',\n" +
                "\t (\n" +
                "\t\tselect sum(total_hours)  from t_leave where number = f.number  and  bill_name like '%事假%' \n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'shijia',\n" +
                "\t (\n" +
                "\t\tselect sum(total_hours) from t_leave where number = f.number  and  bill_name like '%病假%' \n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'bingjia',\n" +
                "\t (\n" +
                "\t\tselect sum(total_hours) from t_leave where number = f.number  and  bill_name like '%年假%' \n" +

                "\t ) as 'nianjia', \n" +
                "\t (\n" +
                "\t\tselect sum(total_hours) from t_leave where number = f.number and  bill_name  like '%其他假期%' \n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'qitajia',\n" +
                "\t sum(workovertime) as 'jiaban',\n" +
                "\t (\n" +
                "\t\tselect sum(total_hours) from t_leave\n" +
                "\t\t where number = f.number  and bill_name like '%调休（正常）%'\n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'tiaoxiuz', --调休正常\n" +
                "\t(\n" +
                "\t\tselect sum(total_hours) from t_leave\n" +
                "\t\t where number = f.number  and bill_name like '%调休（非正常）%'\n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'tiaoxiuf', --调休非正常\n" +
                "\t(\n" +
                "\t\t-1\t\n" +
                "\t ) as 'jbjieyu', --加班结余\n" +
                "\n" +
                "\t sum(fee1) as 'zao',\n" +
                "\t sum(fee2) as 'zhong',\n" +
                "\t sum(fee3) as 'wan',\n" +
                "\t sum(fee4) as 'xiao',\n" +

                "  (select count(*) from t_fees \n" +
                " where number = f.number and cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  \n" +
                " and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime )\n" +
                " and fee_type != 3 ) ,\n"+ //本来记的是员工除了工作日和节假日 上班次数

                " (\n" +
                "\t\tselect  sum(total_hours) from t_leave where number = f.number  and  bill_name like '%丧假%' \n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'sangjia',  \n" +
                "\t (\n" +
                "\t\tselect  sum(total_hours) from t_leave where number = f.number  and  bill_name like '%婚假%' \n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'hunjia',(\n" +
                "\t\tselect  sum(total_hours) from t_leave where number = f.number  and  bill_name like '%产假（陪产假）%' \n" +
                "\t\t and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t ) as 'cangjia',(\n" +
                "\t\tselect  sum(total_hours) from t_bill where number = f.number  and  bill_name like '%交通故障%' \n" + 
                "\t\t and  cast('"+startDay+"' as datetime) <=  start_time  and  end_time <= cast('"+endDay+"' as datetime) \t\n" +
                "\t ) as 'jiaotong'"+

                "\tfrom t_fees f \n" +
                "\twhere cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and  cast(day_str as datetime) <= cast('"+endDay+"' as datetime)\n" +
                "\t group by name ,number ";

        return sql;
    }

    /**
     * 根据员工的工号 ，开结束时间查询出他的 打卡记录   用它携带数据不能带所有的 所以不用他了   queryDetails 方法替代
     * @param number
     * @param startDay
     * @param endDay
     * @return
     */
    public synchronized List<KaoqinDto>  queryKqDetailByNumber(String number ,String startDay,String endDay)  {

        String sql = "select * from t_fees where number = "+number+"   and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and   cast(day_str as datetime) < cast('"+endDay+"' as datetime);";

        List<KaoqinDto> dtos = new ArrayList<KaoqinDto>(); //总的考情详细信息


        List<TFees> feesList =  DBUtils.query(sql, TFees.class); //每一个人的考勤详细信息

        for(int  i=0 ; i<feesList.size();i++){
            TFees f = feesList.get(i);
            KaoqinDto dto = new KaoqinDto() ;

            dto.setName(f.getName());   //姓名
            dto.setNumber(f.getNumber());//工号
            dto.setDayStr(f.getDayStr());//日期
            dto.setStartTime(f.getStartTime());//签到时间
            dto.setEndTime(f.getEndTime()); //签退时间
            dto.setZao(f.getFee1());    //早
            dto.setZhong(f.getFee2());  //中
            dto.setWan(f.getFee3());    //晚
            dto.setXiao(f.getFee4());   //宵
            dto.setJiaban(f.getWorkovertime()); //工作时间

            // 实到	迟到/早退	未打卡	公出	出差	事假	病假	年假	其它假期 调休（正常	非正常）    数据没算
            dto.setStatus(f.getStatus());

            dtos.add(dto);
        }

        // System.out.println( "number : "+number +"的信息数：" +dtos.size()+" sql:"+sql);
        return dtos;
    }


    /**
     * 根据员工的工号 ，开结束时间查询出他的 打卡记录      (事假	病假	年假	其它假期 )

     * @return
     */
    public  List<KaoqinDto>  queryDetails(ResultSet  rs ) {
        List<KaoqinDto> dtoDetails = new ArrayList<KaoqinDto>();

    //   System.out.println("detailsql: "+sqlDetail);
        try{
           // conn = DBUtils.openConnection();


            while(rs.next()){
                KaoqinDto dto = new KaoqinDto();

                dto.setName(rs.getString("name"));   //姓名
                dto.setNumber(rs.getString("number"));//工号
                dto.setDayStr(rs.getString("day_str"));//日期
                dto.setStartTime(rs.getString("start_time"));//签到时间
                dto.setEndTime(rs.getString("end_time")); //签退时间
                dto.setZao(rs.getInt("fee1"));    //早
                dto.setZhong(rs.getInt("fee2"));  //中
                dto.setWan(rs.getInt("fee3"));    //晚
                dto.setXiao(rs.getInt("fee4"));   //宵
                dto.setJiaban(rs.getInt("workovertime")); //工作时间

                // 实到	迟到/早退	未打卡	公出	出差	事假	病假	年假	其它假期 调休（正常	非正常）    数据没算
                dto.setGongchu(rs.getInt("gongchu"));
//                dto.setChucai(rs.getInt("chucai"));
               // dto.setZtcd(rs.getInt("ztcd"));
                dto.setChucai(parseInt(rs.getString("chucai")));
//                dto.setShijia(rs.getInt("shijia"));
                dto.setShijia(parseInt(rs.getString("shijia")));
                dto.setBingjia(parseInt(rs.getString("bingjia")));
                dto.setNianjia(parseInt(rs.getString("nianjia")));
                dto.setQitajia(parseInt(rs.getString("qitajia")));
                dto.setTiaoxiuz(rs.getInt("tiaoxiuz"));
                dto.setTiaoxiuf(parseInt(rs.getString("tiaoxiuf")));

                //yingjing.liu add start 2016/07/11  添加产假等假期
                dto.setSangjia(rs.getString("sanjia"));
                dto.setHunjia(rs.getString("hunjia"));
                dto.setChanjia(rs.getString("chanjia"));
                dto.setJiaotong(rs.getString("jiaotong"));
                //yingjing.liu add end 2016/07/11

                dto.setStatus(rs.getString("status"));
                dtoDetails.add(dto);
            }
          
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return dtoDetails;
    }

    private String getOneDetailSql(String startDay,String endDay){
        String sqlDetail = "select * " +
                " ,( " +
                " select total_hours from t_bill " +
                " where number =  f.number  and bill_name like '%公出%' " +
                "  and convert(char(10),start_time,111) = f.day_str " +
                " ) as 'gongchu' , \n" +
                " ( " +
                " select count(total_hours) from t_bill " +
                "where number = f.number  and bill_name like '%出差%' " +
                "  and convert(char(10),start_time,111) = f.day_str " +
                ") as 'chucai', \n" +
                "(" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%事假%'" +
                " and day_str = f.day_str " +
                ") as 'shijia',\n" +
                "(" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%病假%'" +
                " and day_str = f.day_str " +
                ") as 'bingjia',\n" +
                " (" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%年假%'" +
                " and day_str = f.day_str " +
                " ) as 'nianjia',\n" +
                " (" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%其他假期%'" +
                " and day_str = f.day_str " +
                " ) as 'qitajia',\n" +
                "(" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%调休（正常）%' " +
                " and day_str = f.day_str  " +
                ") as 'tiaoxiuz', \n" +
                " (" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%调休（非正常）%' " +
                " and day_str = f.day_str " +
                ") as 'tiaoxiuf' \n " +
                "  from t_fees f where number = ?  and  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and   cast(day_str as datetime) <= cast('"+endDay+"' as datetime) ";
        return sqlDetail;
    }

    private Integer parseInt(String str){
        Integer res = 0 ;
        if(str == null ||"".equals(str)){
            return  res;
        }
        return Integer.valueOf(str);
    }

    /**
     * 根据 dtos中的 number 来查询所有的用户信息 发送一条语句 30天 约 4500 条数据 约140s
     * @param dtos
     * @param startDay
     * @param endDay
     * @return
     */
    public List<KaoqinDto> queryByDateDetails(List<KaoqinDto> dtos, String startDay, String endDay) {

        StringBuffer sb = new StringBuffer("(");
        for(int i=0 ; i<dtos.size();i++){
            KaoqinDto dto = dtos.get(i);
            sb.append(dto.getNumber()+", ");
        }
        String numberStr = sb.toString().substring(0,sb.length()-2)+")";

 
        List<KaoqinDto> oneDetails = null;
        try{
        String oneDetailSql = getDetailSql(startDay,endDay,numberStr);

            //System.out.println("oneDetailSql:"+oneDetailSql);
        Connection conn = DBUtils.openConnection();
        PreparedStatement ps = conn.prepareStatement(oneDetailSql);
        ResultSet rs = ps.executeQuery();
        oneDetails = queryDetails(rs );
            System.out.println("oneDetails:"+oneDetails.size());
            rs.close();
            ps.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  oneDetails;
    }

    private String getDetailSql(String startDay ,String endDay , String numberStr) {

        String sqlDetail = "select * " +
                " ,( " +
                " select total_hours from t_bill " +
                " where number =  f.number  and bill_name like '%公出%' " +
                "  and convert(char(10),start_time,111) = f.day_str " +
                " ) as 'gongchu' , \n" +
                " ( " +
                " select count(total_hours) from t_bill " +
                "where number = f.number  and bill_name like '%出差%' " +
                "  and convert(char(10),start_time,111) = f.day_str " +
                ") as 'chucai', \n" +
                "(" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%事假%'" +
                " and day_str = f.day_str " +
                ") as 'shijia',\n" +
                "(" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%病假%'" +
                " and day_str = f.day_str " +
                ") as 'bingjia',\n" +
                " (" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%年假%'" +
                " and day_str = f.day_str " +
                " ) as 'nianjia',\n" +
                " (" +
                " select total_hours from t_leave where number =f.number  and bill_name like '%其他假期%'" +
                " and day_str = f.day_str " +
                " ) as 'qitajia',\n" +
                "(" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%调休（正常）%' " +
                " and day_str = f.day_str  " +
                ") as 'tiaoxiuz', \n" +
                " (" +
                " select sum(total_hours) from t_leave where number =f.number  and bill_name like '%调休（非正常）%' " +
                " and day_str = f.day_str " +
                ") as 'tiaoxiuf' \n " +
                "  from t_fees f where  cast('"+startDay+"' as datetime) <=  cast(day_str as datetime)  and   cast(day_str as datetime) <= cast('"+endDay+"' as datetime) \n"+
                " and number in "+numberStr;

        return  sqlDetail;
    }


}
