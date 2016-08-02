package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.KaoQingDao;
import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.KaoqinDto;
import com.ragentek.mealsupplement.db.bean.TDept;
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

        if(!TextUtil.isNullOrEmpty(fdeptId)&&TextUtil.isNullOrEmpty(sdeptId)){
            sql += " and dept1 = "+fdeptId ;
        }
        if(!TextUtil.isNullOrEmpty(sdeptId)){
            sql += " and dept2 = "+sdeptId;
        }
        List<TUser> users = DBUtils.query(sql,TUser.class);
        System.out.println(sql);
        Map<String,TUser> map = new HashMap<String, TUser>();

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
