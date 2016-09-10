package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.KaoQingDao;
import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.KaoqinDto;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TKaoqin;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.poi.UserAttendanceExport;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.LeaveUtil;
import com.ragentek.mealsupplement.tools.TextUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.Text;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by yingjing.liu on 2016/5/26.
 */
public class ExportAtteServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        exportPoi(request,response);

        //request.getRequestDispatcher("upload.jsp").forward(request,response);
        System.out.print("返回");
     //   response.getWriter().print("success");
    }

    /**
     * POI 导出考勤信息

     */
    public String exportPoi(HttpServletRequest req, HttpServletResponse resp){
        String  startDay=req.getParameter("startDay");
        String endDay =req.getParameter("endDay") ;
        int res = 0;
        try{
            //执行POI导出 操作
            System.out.println("is to export report for  POI to bill...");

            KaoQingDao dao = new KaoQingDao();
            List<KaoqinDto> dtos = dao.queryByDate(startDay,endDay);

            //yingjing.liu add start 20160909  对两个守夜的师傅 计算上班时间,  他们一个月多少天他们自己轮流来守夜  他们的计算规则: 他们白天不上班  更加中午12点第打卡为分界计算    2016、09、09
            dtos= compareWorkDay(dtos,startDay,endDay,new String[]{"100041","100155"});//许威麟 //何伟忠
            //yingjing.liu add end 20160909  对两个守夜的师傅 计算上班时间,  他们一个月多少天他们自己轮流来守夜
            Long star = System.currentTimeMillis();
          /*  List<KaoqinDto> allDetails = new ArrayList<KaoqinDto>();
            List<KaoqinDto> one = new ArrayList<KaoqinDto>();
            int msgCount = 15; //一次性查询的信息条数  18,19 的 值最标准
            for(int i = 0 ; i<dtos.size();i++){
                one.add(dtos.get(i));
                if(i%msgCount==0|| i == (dtos.size()-1)){ // 分段查询，到最后一条数据也要查询一次
                    List<KaoqinDto> oneDetails = dao.queryByDateDetails(one,startDay,endDay);
                    if(dtos.size()-1==i){

                        System.out.println("abd");
                    }
                    Collections.sort(oneDetails, new Comparator<KaoqinDto>() {
                        @Override
                        public int compare(KaoqinDto o1, KaoqinDto o2) {

                            return 0;
                        }
                    });

                    allDetails.addAll(oneDetails);
                    one = new ArrayList<KaoqinDto>();
                }
            }*/
            List<KaoqinDto> allDetails  = dao.queryByDateAllDetails(dtos,startDay,endDay);//查询用户详情
            Long end =System.currentTimeMillis();
            System.out.println(" 两条语句子查询分段查询 一共耗时： "+((end-star)/(1000))+" s, 数据量："+allDetails.size());

            System.out.println("读取数据完成");




            // 添加一个部门导出      思路：  1.导出所有的员工配置参数。2.根据部门查询符合的员工 3.根据符合的员工 移除多余的员工
//            Map<String,TUser> usersMap = getGroupsUsers(req);  //原来根据部门导出   LDAP 部门

            Map<String,TUser> usersMap = getDeptsUsers(req);
            showLog(dtos,usersMap); //显示忽略查询的用户 
            dtos = removeMeal(dtos , usersMap);
            allDetails = removeMeal(allDetails,usersMap);
            String deptName = getByDeptName(req);
            String fileName = "麦穗科技考勤数据"+deptName+startDay.replaceAll("/","")+"-"+endDay.replaceAll("/","")+".xlsx";
            resp.setContentType("application/x-octetstream;charset=ISO-8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename="+new String(fileName.getBytes(), "ISO-8859-1"));


            System.out.println("dtos.size():"+dtos.size());
            System.out.println("allDetails.size();"+allDetails.size());


            UserAttendanceExport uae = new UserAttendanceExport(startDay,endDay);
            uae.setData(dtos);
            uae.setDtoDetails(allDetails);

            uae.exportExcel(resp.getOutputStream());

        }catch(Exception e){
            e.printStackTrace();
            res= LeaveUtil.NA_EXCEPTION;
        }
        if(res == LeaveUtil.NA_SUCCESS){
            return Result.success();
        }else{
            Result result = new Result(false,res);
            return result.toString();
        }
    }

    /**
     * 针对夜班员工 添加打卡规则
     * 计算打卡规则: 2016/07/19 12:00:01  与  2016/07/20 12:00:00 区间打卡都是  2016/07/19  区间前为前一天 后为后一天
     *
     * 计算规则:  以下午打卡为准 ，两个人的打卡总数是这个月的天数为准如果 多了 代表那天下午都来了   那么把他们第二天早上打卡信息 拿来比较 没有来的-1
     * @param dtos
     * @param startDay
     * @param endDay
     * @param numbers
     * @return
     */
    private List<KaoqinDto> compareWorkDay(List<KaoqinDto> dtos, String startDay, String endDay, String[] numbers) throws ParseException, SQLException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy/MM");

        String sql = "select\n" +
                "SUBSTRING(dotimes , 0 , 11) as day ,count(*) as num from t_kaoqin \n" +
                "where userid=? and SUBSTRING(dotimes , 0 , 11)>= ? and SUBSTRING(dotimes,0,11)<= ? and SUBSTRING(dotimes,12,8) > '12:00:00'\n" +
                "group by \n" +
                "SUBSTRING(dotimes , 0 , 11)";

       Connection conn = DBUtils.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1,numbers[0]);
        ps.setString(2,startDay);
        ps.setString(3,endDay);
        ResultSet rs = ps.executeQuery();
        List<String[]> number41 = new ArrayList<String[]>();
        while(rs.next()){
            String dakas[] =new String[2];
            String day =rs.getString("day");
            String num =rs.getString("num");
            dakas[0]=day;
            dakas[1]=num;
            number41.add(dakas);
        }

        ps.setString(1,numbers[1]);
        ps.setString(2,startDay);
        ps.setString(3,endDay);
          rs = ps.executeQuery();
        List<String[]> number155 = new ArrayList<String[]>();
        while(rs.next()){
            String dakas[] =new String[2];
            String day =rs.getString("day");
            String num =rs.getString("num");
            dakas[0]=day;
            dakas[1]=num;
            number155.add(dakas);
        }


        //以下午打卡为准 ，两个人的打卡总数是这个月的天数为准如果 多了 代表那天下午都来了   那么把他们第二天早上打卡信息 拿来比较 没有来的-1
        int monthofday = 0;
        switch (Calendar.getInstance().get(Calendar.MONTH)+1){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                monthofday = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                monthofday = 30;
                break;
            case 2:
                int year = Calendar.getInstance().get(Calendar.YEAR);
                if(year%4==0&&year%100==0||year/400==0){
                    monthofday=29;
                }else{
                    monthofday=28;
                }
                break;
        }
        int numCount41 = number41.size();
        int numCount155=number155.size();

        if(number41.size()+number155.size()!=monthofday){ //不等于 如果有多来的可以计算, 没有多来的不可以计算
            List<String> dayList= new ArrayList<String>();//多来的天

            for(int i=0 ; i<number41.size();i++){
                String [] num41=number41.get(i);
                for(int j = 0; j<number155.size();j++){
                    String [] num155 = number155.get(j);
                    if(num155[0].equals(num41[0])){ //这天都来了
                        dayList.add(num41[0]);
                    }
                }
            }
            //查询第二天上午的情况
            String queNexSql = "select * from t_kaoqin where   SUBSTRING(dotimes , 0 , 11)= ?  and userid in ("+numbers[0]+","+numbers[1]+")  and SUBSTRING(dotimes,12,8) < '12:00:00' ";
            SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy/MM/dd");

            for(int i=0 ; i<dayList.size();i++){
                boolean hasNum41=false;
                boolean hasNum155=false;
                System.out.println(dayList.get(i));
                Date nextDay = sdf2.parse(dayList.get(i));
                nextDay.setTime(nextDay.getTime()+( 60*60 * 1000 * 24));
                String nextDayStr =sdf2.format(nextDay);
                List<TKaoqin> kaoqins = DBUtils.query(queNexSql,new String[]{nextDayStr },TKaoqin.class);
                //
                for(int j=0 ;j<kaoqins.size()-1;j++){
                    
                    if(kaoqins.get(j).getUserid().equals(numbers[0])){
                        hasNum41=true;
                    }
                    if(kaoqins.get(j).getUserid().equals(numbers[1])){
                        hasNum155 =true;
                    }
                }
                if(hasNum41){
                    numCount41--;
                }
                if(hasNum155){
                    numCount155--;
                }
            }
        }

        for(int i=0 ; i<dtos.size();i++){
            KaoqinDto dto = dtos.get(i);
            if(dto.getNumber().equals(numbers[0])){
                dto.setYingdao(numCount41);
            }
            if(dto.getNumber().equals(numbers[1])){
                dto.setYingdao(numCount155);
            }
        }

      /*  for(int i=0 ; i<dtos.size();i++){
            KaoqinDto dto = dtos.get(i);
            for(int j=0 ; j<numbers.length;j++){
                if(dto.getNumber().equals(numbers[j])){
                    System.out.println("计算："+numbers[j]);
                    String sql ="select * from t_kaoqin where userid=? and SUBSTRING(dotimes , 0 , 11)>= ? and SUBSTRING(dotimes,0,11)<= ?";
                    List<TKaoqin> kaoqins = DBUtils.query(sql, new String[]{numbers[j], startDay, endDay}, TKaoqin.class); 
                    int num = 0;
                    Map<String,List<TKaoqin>> hisKaoqin = new HashMap<String, List<TKaoqin>>();
                    for(int a=0 ; a<kaoqins.size();a++){
                        TKaoqin tkq = kaoqins.get(a);
                        String dotimes = tkq.getDotimes();
                        String[] todot = dotimes.split(" ");
                        
                        if(todot[1].compareTo("12:00:00")>0){ // 为正  属于这天工作,    为负,是前一天工作   为0  12:00:00 打卡  不可能这么准时
                            if(hisKaoqin.containsKey(todot[0])){
                                hisKaoqin.get(todot[0]).add(tkq);
                            }else{
                                List<TKaoqin> kqList = new ArrayList<TKaoqin>();
                                kqList.add(tkq);
                                hisKaoqin.put(todot[0],kqList);
                            }
                        }else{  //前一天
                            Date parse = sdf.parse(todot[0]);
                            parse.setTime(parse.getTime()-(24*60*1000)); //属于昨天的
                            if(startDay.equals(todot[0])){
                                System.out.print("cont,");
                                continue;
                            }
                            todot[0] = sdf.format(parse);
                            if(hisKaoqin.containsKey(todot[0])){
                                hisKaoqin.get(todot[0]).add(tkq);
                            }else{
                                List<TKaoqin> kqList = new ArrayList<TKaoqin>();
                                kqList.add(tkq);
                                hisKaoqin.put(todot[0],kqList);
                            }
                        }
                    }
                    System.out.print(" 计算他的天数: "+hisKaoqin.size());
                    // 计算如果他没有打卡一次是不正常的,上班 下班
                    Set<String> set = hisKaoqin.keySet();
                    Iterator<String> it = set.iterator();
                    while(it.hasNext()){
                        String key = it.next();
                        System.out.print(key+" "+hisKaoqin.get(key).size()+" \t,");
                        if(hisKaoqin.get(key).size()==1){ //一天只打一次卡 计算为没有没打卡
                            System.out.print(",删除"+hisKaoqin.get(key).size());
                            it.remove();
                        }
                    }
                    dto.setYingdao(hisKaoqin.size()); //他打卡的次数
                }
            }
        }*/
        return dtos;
    }

    /**
     * 得到根据的部门导出的名字   根据一级部门导出得到 （一级部门名） 二级部门:(一级部门名_二级部门名)  全部:  (全部成员)
     * @param req
     * @return
     */
    private String getByDeptName(HttpServletRequest req) {
        String deptName = "(全部成员)";
        String fdeptId = req.getParameter("parentId");
        String sdeptId = req.getParameter("secondDept");
        if(!TextUtil.isNullOrEmpty(fdeptId)&&TextUtil.isNullOrEmpty(sdeptId)){
            TDept firstDept =  DBUtils.uniqueBean("select * from t_dept where _id="+fdeptId,TDept.class);
            if(firstDept != null){
                deptName = "("+firstDept.getName()+")";
            }
        }
        if(!TextUtil.isNullOrEmpty(sdeptId)){
            TDept firstDept =  DBUtils.uniqueBean("select * from t_dept where _id="+fdeptId,TDept.class);
            TDept secondDept =  DBUtils.uniqueBean("select * from t_dept where _id="+sdeptId,TDept.class);
            if(firstDept != null&& secondDept!=null){
                deptName = "("+firstDept.getName()+"_"+secondDept.getName()+")";
            }
        }
        return deptName ;
    }


    /**
     * 有二级部门 就根据二级部门 导出， 没有二级部门就根据一级部门导出
     * 根据系统中的部门得到其需要导出excel的用户放置
     * @param req
     * @return
     */
    private Map<String,TUser> getDeptsUsers(HttpServletRequest req) {
        String fdeptId = req.getParameter("parentId");
        String sdeptId = req.getParameter("secondDept");
        
        String sql = "select * from t_user where 1=1 " ;
        boolean isAll = true;
        if(!TextUtil.isNullOrEmpty(fdeptId)&&TextUtil.isNullOrEmpty(sdeptId)){
            sql += " and dept1 = "+fdeptId ;
            isAll = false;
        }
        if(!TextUtil.isNullOrEmpty(sdeptId)){
            sql += " and dept2 = "+sdeptId;
            isAll = false;
        }
        List<TUser> users = DBUtils.query(sql,TUser.class);
        System.out.println(sql);
        Map<String,TUser> map = new HashMap<String, TUser>();
        if(isAll){ //保洁人员与 保安 如果查询全部就加入  
            map.put("100155",new TUser());
            map.put("100040",new TUser());
        }

        for (TUser tu:users) {
            map.put(tu.getNumber(),tu);
        }
        return  map;
    }

    private void showLog(List<KaoqinDto> dtos, Map<String, TUser> usersMap) {

        for(int i=0 ; i<dtos.size(); i++){
            KaoqinDto kd = dtos.get(i);
            if(!usersMap.containsKey(kd.getNumber())){//
                System.out.println("没有查询的用户："+kd);

            }
        }

    }

    /**
     * 根据 部门组 得到权限
     * @param req
     * @return
     */
    private Map<String,TUser> getGroupsUsers(HttpServletRequest req) {
        String groupIds[] = req.getParameterValues("dept");
        String sql = " select * from t_user " ;
        if(groupIds!=null){
            sql +=" where id  in(  select userid from t_user_group where ";
            String gidStr = " groupid in (";
            StringBuffer sb = new StringBuffer(gidStr);
            for(int i=0 ; i<groupIds.length;i++){
                sb.append( groupIds[i]+" ,");
            }
            sb = new StringBuffer(sb.substring(0,sb.length()-1)+")");
            if(sb.length()>gidStr.length()){
                sql += sb.toString();
            }else{
                sql += " groupid in (3) ";
            }
            sql+=" )";
        }
        //拼接成的字符串 :   eg:  select * from t_user where id  in( select userid from t_user_group where groupid in (6))

        List<TUser> users = DBUtils.query(sql, TUser.class);

        Map<String,TUser> userMaps = new HashMap<String, TUser>();
        for(int  i=0 ; i<users.size();i++){
            userMaps.put(users.get(i).getNumber(),userMaps.get(i));
        }
        System.out.println("查询所有人数:"+users.size()+".去重后人数:"+userMaps.size()+"根据部门查:"+sql);
        return userMaps;
    }

    //根据部门派出多余的
    private List<KaoqinDto> removeMeal(List<KaoqinDto> dtos,Map<String,TUser> usersMap) {
        List<KaoqinDto> fitDtos = new ArrayList<KaoqinDto>();

        for(int i=0 ; i<dtos.size();i++){
            if(usersMap.containsKey(dtos.get(i).getNumber())){
                fitDtos.add(dtos.get(i));
            }
        }
        return  fitDtos;
    }

}
