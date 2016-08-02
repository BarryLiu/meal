package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Dept;
import com.ragentek.mealsupplement.json.Fees;
import com.ragentek.mealsupplement.json.FeesList;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.DateTools;
import com.ragentek.mealsupplement.tools.TextUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by kui.li on 2014/9/2.
 */
public class OutputServlet extends BaseServlet {
    private DeptService deptService;
    private ServletContext sc;
    public void init(ServletConfig config) {
        sc = config.getServletContext();
    }

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name"); // 员工姓名或工号
        String sDate = req.getParameter("sDate"); // 查询开始日期
        String eDate = req.getParameter("eDate"); // 查询结束日期
        String outTag = req.getParameter("outTag"); // 导出数据flag

        // wpf add
        System.out.println("WPF_3");
        sDate = DateTools.convertDateSeparator(sDate);
        eDate = DateTools.convertDateSeparator(eDate);

        System.out.println("name=" + name + ";sDate=" + sDate + ";eDate=" + eDate + ";outTag=" + outTag);

        req.setAttribute("name", name);
        req.setAttribute("sDate", sDate);
        req.setAttribute("eDate", eDate);
//        String type = req.getParameter("type"); // 餐补类型，1，2，3，4，5分别代表早，中，晚，宵，午夜

        // 开始查询
//        String sql = "SELECT * FROM t_fees WHERE 1=1";
        String sql = " select id,user_info,day_str,fee1,fee2,fee3,fee4,fee5,start_time, end_time,workovertime,status,name,number,fee_type\n" +
                "\t ,(select top 1 bill_name from t_bill where convert(char(10),start_time,111) = f.day_str and number = f.number  ) as deal_status \n" +
                "\t from t_fees f where 1 =1 "; //20160729 yingjing .liu  换成这条语句才能查询出他的处理结果    deal_status
        if(name != null && !"".equals(name)) {
            sql += " AND user_info like '%" + name + "%'";
        }
        if(sDate != null && !"".equals(sDate)) {
            sql += " AND day_str >= '" + sDate + "'";
        }
        if(eDate != null && !"".equals(eDate)) {
            sql += " AND day_str <= '" + eDate + "'";
        }
        sql += " ORDER BY day_str,user_info";
        System.out.println("In query,sql=" + sql);
        List<TFees> lstFees = DBUtils.query(sql,TFees.class);

        FeesList list = new FeesList();
        list.addAll(lstFees);
        List<Fees> fees = list.getList();
        injectDept(fees); //新加的这个用于 数据的展示 ,在   String data = row.toJson(); 前面调整数据的时候 set 进去
        filetareByDept(fees,req);// 根据前面传过来的一级部门与二级部门查询    ： 首先数据库查询全部 ,在这里根据条件remove list数据
        filetareByUsers(fees,req);// 主要是根据自己是不是部门负责人来查询的   如果是负责人才进去过滤 ,不过滤代表不是负责人

        if(outTag == null || !"1".equals(outTag)) { //查询
           /* StringBuilder sb = new StringBuilder();
            sb.append("{Rows:[");
            if(lstFees != null && lstFees.size()>0) {
                if("1".equals(outTag)) {
                    req.setAttribute("download", "1"); //有数据，可以下载
                }
                String sDateStr = "";
                String eDateStr = "";
                int tFee1=0,tFee2=0,tFee3=0,tFee4=0,tFee5=0;
                // int  tWorktime=0;
                for(TFees item : lstFees) {
                    if("".equals(sDateStr))
                        sDateStr = item.getDayStr();
                    eDateStr = item.getDayStr();
                    tFee1 += item.getFee1();
                    tFee2 += item.getFee2();
                    tFee3 += item.getFee3();
                    tFee4 += item.getFee4();
                    //tWorktime += item.getWorkovertime();
                    //tFee5 += item.getFee5();
                    sb.append("{user_info:'");
                    sb.append(item.getUserInfo());
                    sb.append("',day_str:'");
                    sb.append(item.getDayStr());
                    sb.append("',start_time:'");
                    sb.append(item.getStartTime());
                    sb.append("',end_time:'");
                    sb.append(item.getEndTime());
                    sb.append("',workovertime:'");
                    sb.append(item.getWorkovertime());
                    sb.append("',fee1:'");
                    sb.append(item.getFee1());
                    sb.append("',fee2:'");
                    sb.append(item.getFee2());
                    sb.append("',fee3:'");
                    sb.append(item.getFee3());
                    sb.append("',fee4:'");
                    sb.append(item.getFee4());
                    //sb.append("',fee5:'");
                    //sb.append(item.getFee5());
                    sb.append("',total:'");
                    //int total = item.getFee1() + item.getFee2() + item.getFee3() + item.getFee4() + item.getFee5();
                    int total = item.getFee1() + item.getFee2() + item.getFee3() + item.getFee4();
                    sb.append(total);
                    sb.append("',status:'");
                    sb.append(item.getStatus());
                    sb.append("'},");
                }
                sb.append("{user_info:'");
                sb.append("合计");
                sb.append("',day_str:'");
                sb.append(sDateStr + "~" + eDateStr);
                sb.append("',start_time:'");
                sb.append("");
                sb.append("',end_time:'");
                sb.append("");
                sb.append("',workovertime:'");
                sb.append("");
                sb.append("',fee1:'");
                sb.append(tFee1);
                sb.append("',fee2:'");
                sb.append(tFee2);
                sb.append("',fee3:'");
                sb.append(tFee3);
                sb.append("',fee4:'");
                sb.append(tFee4);
                sb.append("',fee5:'");
                sb.append(tFee5);
                sb.append("',total:'");
                //int total = tFee1 + tFee2 + tFee3 + tFee4 + tFee5;
                int total = tFee1 + tFee2 + tFee3 + tFee4;
                sb.append(total);
                sb.append("',status:'");
                sb.append("");
                sb.append("'}");
            }
            sb.append("]}");
            req.setAttribute("data", sb.toString());*/

            Rows rows = new Rows(fees);
            req.setAttribute("data", rows.toJson()); 
            //yingjing.liu 20160725 add dept start
            List<TDept> depts= deptService.getDeptsByType(Dept.TYPE_LEVEL_FIRST,null);
            req.setAttribute("depts",depts);
            //yingjing.liu 20160725 add dept end
            req.getRequestDispatcher("search.jsp").forward(req, resp);
        } else { //下载
            // 导出EXCEL
            HSSFWorkbook hssfworkbook = new HSSFWorkbook(); // 工作簿
            HSSFSheet hssfsheet = hssfworkbook.createSheet(); //创建sheet页
            hssfworkbook.setSheetName(0,"统计明细");//sheet名称乱码处理
            int rowNum = 0;
            addRow(hssfsheet,rowNum,"工号-姓名","日期","上班时间","下班时间","加班时间","早餐","午餐","晚餐","宵夜1","合计");
            if(lstFees != null && lstFees.size()>0) {
                // 汇总用map
                Map<String, Integer> map = new TreeMap<String, Integer>(new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        //升序排序
                        return obj1.compareTo(obj2);
                    }
                });
                // 汇总用map
                Map<String, Integer> map2 = new TreeMap<String, Integer>(new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        //升序排序
                        return obj1.compareTo(obj2);
                    }
                });

                String sDateStr = "";
                String eDateStr = "";
                int tFee1=0,tFee2=0,tFee3=0,tFee4=0,tFee5=0;
                // int  tWorktime=0;
                for(TFees item : lstFees) {
                    if("".equals(sDateStr))
                        sDateStr = item.getDayStr();
                    eDateStr = item.getDayStr();
                    tFee1 += item.getFee1();
                    tFee2 += item.getFee2();
                    tFee3 += item.getFee3();
                    tFee4 += item.getFee4();
                    int total = item.getFee1() + item.getFee2() + item.getFee3() + item.getFee4();
                    rowNum++;
                    addRow(hssfsheet, rowNum, item.getUserInfo(), item.getDayStr(), item.getStartTime(), item.getEndTime(),item.getWorkovertime(),
                            item.getFee1(), item.getFee2(), item.getFee3(), item.getFee4(),  total);//item.getFee5(),

                    if (map.containsKey(item.getUserInfo())) { // 已经存在
                        Integer iFee = map.get(item.getUserInfo());
                        map.remove(item.getUserInfo());
                        map.put(item.getUserInfo(), iFee.intValue() + total);
                    } else {
                        map.put(item.getUserInfo(), total);
                    }

                    if (map2.containsKey(item.getUserInfo())) { // 已经存在
                        Integer iWorktime = map2.get(item.getUserInfo());
                        map2.remove(item.getUserInfo());
                        //System.out.println("WPF_7:"+item.getUserInfo()+","+item.getWorkovertime());
                        map2.put(item.getUserInfo(), iWorktime.intValue()+item.getWorkovertime());
                    } else {
                        // System.out.println("WPF_8:"+item.getUserInfo()+","+item.getWorkovertime());
                        map2.put(item.getUserInfo(), item.getWorkovertime());
                    }
                }
                int total = tFee1 + tFee2 + tFee3 + tFee4;

                rowNum++;
                addRow(hssfsheet, rowNum, "合计", sDateStr + "~" + eDateStr, "", "",0,
                        tFee1, tFee2, tFee3, tFee4,  total); //tFee5,

                // 查询每人每餐的汇总数据，不计算天
                sql = "SELECT user_info,sum(fee1) as fee1,sum(fee2) as fee2,sum(fee3) as fee3,sum(fee4) as fee4,sum(workovertime) as workovertime FROM t_fees WHERE 1=1";
                if(name != null && !"".equals(name)) {
                    sql += " AND user_info like '%" + name + "%'";
                }
                if(sDate != null && !"".equals(sDate)) {
                    sql += " AND day_str >= '" + sDate + "'";
                }
                if(eDate != null && !"".equals(eDate)) {
                    sql += " AND day_str <= '" + eDate + "'";
                }
                sql += " GROUP BY user_info";
                List<TFees> lstTotal = DBUtils.query(sql,TFees.class);

                if(lstTotal != null && lstTotal.size()>0) {
                    // 创建sheet 页
                    HSSFSheet hssfsheet2 = hssfworkbook.createSheet();
                    hssfworkbook.setSheetName(1, "汇总结果1");
                    rowNum = 0;
                    addRow(hssfsheet2, rowNum, "工号|姓名","加班时间","早餐","午餐","晚餐","宵夜1","合计", "", "", "");//"宵夜2",

                    tFee1=0;tFee2=0;tFee3=0;tFee4=0;tFee5=0;
                    //tWorktime=0;
                    for(TFees item : lstTotal) {
                        rowNum++;

                        tFee1 += item.getFee1();
                        tFee2 += item.getFee2();
                        tFee3 += item.getFee3();
                        tFee4 += item.getFee4();
                        //tFee5 += item.getFee5();
                        //tWorktime += item.getWorkovertime();
                        //System.out.println("WPF_5:"+item.getUserInfo()+","+item.getWorkovertime()+","+item.getDayStr());
                        addRow(hssfsheet2, rowNum, item.getUserInfo(), item.getWorkovertime(), item.getFee1(), item.getFee2(), item.getFee3(), item.getFee4(),
                                item.getFee1() + item.getFee2() + item.getFee3() + item.getFee4()); //item.getFee5(), +item.getFee5()
                    }
                    rowNum++;
                    addRow(hssfsheet2, rowNum, "合计",0,
                            tFee1, tFee2, tFee3, tFee4,  tFee1 + tFee2 + tFee3 + tFee4); // tFee5, + tFee5
                }

                // 第三个sheet，统计汇总数据
                //创建汇总sheet页
                HSSFSheet hssfsheet3 = hssfworkbook.createSheet();
                hssfworkbook.setSheetName(2, "汇总结果2");
                addRow(hssfsheet3, 0, "工号-姓名","加班时间（单位：分钟）", "补助总额（单位：元）");
                // 添加余下的所有数据
                Set<String> keySet = map.keySet();
                Iterator<String> iterator = keySet.iterator();
                int num = 0;
                int sum = 0;
                while (iterator.hasNext()) {
                    num++;
                    String key = iterator.next();
                    //long time=map2.get(key);
                    //System.out.println("WPF_6:"+key+","+map2.get(key)+","+map.get(key));
                    addRow(hssfsheet3, num, key, map2.get(key),map.get(key));
                    sum += map.get(key);
                }
                num++;
                addRow(hssfsheet3, num, "合计",0, sum);
            }
            //输出
            String fileName = System.currentTimeMillis() + ".xls";
            resp.setContentType("application/x-octetstream;charset=ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename="+new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            hssfworkbook.write(resp.getOutputStream());
        }

    }
    private void filetareByUsers(List<Fees> fees, HttpServletRequest req) {
        Map<String,TUser> userMap = (Map<String, TUser>) req.getSession().getAttribute("manageMentUser");
        if(userMap==null) //return了代表不是部门负责人
            return;

        Iterator<Fees> it = fees.iterator();
        while (it.hasNext()){
            Fees f = it.next();
            if(!userMap.containsKey(f.getUser_info().split("-")[0])){
                it.remove();
            }
        }
    }
    private void injectDept(List<Fees> billList) {
          UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
        List<TUser> users = userService.getAllUsers();
        Map<String,TUser> userMap = new HashMap<String, TUser>();
        for(int i=0 ; i<users.size(); i++){ //将系统中的数据放到map容器中去
            TUser u = users.get(i);
            userMap.put(u.getNumber(),u);
        }
        for(int i=0 ; i<billList.size(); i++){ //将系统中的数据放到map容器中去
            Fees le = billList.get(i);
            String number = le.getUser_info().split("-")[0];
            if(userMap.containsKey(number)){
                TUser u =userMap.get(number);
                le.setDeptName1(u.getDeptName1());
                le.setDeptName2(u.getDeptName2());
            }
        }
    }
    private void filetareByDept(List<Fees> leaves, HttpServletRequest req) {
        String dept1Id = req.getParameter("dept1");
        String dept2Id = req.getParameter("dept2");
        req.setAttribute("dept1",dept1Id);
        req.setAttribute("dept2",dept1Id);

        TDept dept1 = null;
        TDept dept2 = null;
        if(!TextUtil.isNullOrEmpty(dept1Id)){
            dept1 = deptService.selectById(dept1Id);
            List<TDept> seconds =  deptService.getDeptsByType(Dept.TYPE_LEVEL_SECOND,dept1.getValue());
            req.setAttribute("deptSeconds",seconds);
        }

        if(!TextUtil.isNullOrEmpty(dept2Id))
            dept2 = deptService.selectById(dept2Id);

        Iterator<Fees> it = leaves.iterator();
        while(it.hasNext()){
            Fees le = it.next();
            if(dept1!=null && dept1.getName()!=null){
                if(!dept1.getName().equals(le.getDeptName1())){
                    it.remove();
                    continue;
                }
            }
            if(dept2!=null && dept2.getName()!=null){
                if(!dept2.getName().equals(le.getDeptName2())){
                    it.remove();
                    continue;
                }
            }
        }
    }
    private static void addRow(HSSFSheet hssfsheet, int rowNum, String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short)0);
        hssfcell0.setCellValue(value1);
        HSSFCell hssfcell1 = hssfrow.createCell((short)1);
        hssfcell1.setCellValue(value2);
        HSSFCell hssfcell2 = hssfrow.createCell((short)2);
        hssfcell2.setCellValue(value3);
        HSSFCell hssfcell3 = hssfrow.createCell((short)3);
        hssfcell3.setCellValue(value4);
        HSSFCell hssfcell4 = hssfrow.createCell((short)4);
        hssfcell4.setCellValue(value5);
        HSSFCell hssfcell5 = hssfrow.createCell((short)5);
        hssfcell5.setCellValue(value6);
        HSSFCell hssfcell6 = hssfrow.createCell((short)6);
        hssfcell6.setCellValue(value7);
        HSSFCell hssfcell7 = hssfrow.createCell((short)7);
        hssfcell7.setCellValue(value8);
        HSSFCell hssfcell8 = hssfrow.createCell((short)8);
        hssfcell8.setCellValue(value9);
        HSSFCell hssfcell9 = hssfrow.createCell((short)9);
        hssfcell9.setCellValue(value10);
    }
    private static void addRow(HSSFSheet hssfsheet, int rowNum, String value1, String value2, String value3, String value4, Integer value5, Integer value6, Integer value7, Integer value8, Integer value9, Integer value10) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short)0);
        hssfcell0.setCellValue(value1);
        HSSFCell hssfcell1 = hssfrow.createCell((short)1);
        hssfcell1.setCellValue(value2);
        HSSFCell hssfcell2 = hssfrow.createCell((short)2);
        hssfcell2.setCellValue(value3);
        HSSFCell hssfcell3 = hssfrow.createCell((short)3);
        hssfcell3.setCellValue(value4);
        HSSFCell hssfcell4 = hssfrow.createCell((short)4);
        hssfcell4.setCellValue(value5);
        HSSFCell hssfcell5 = hssfrow.createCell((short)5);
        hssfcell5.setCellValue(value6);
        HSSFCell hssfcell6 = hssfrow.createCell((short)6);
        hssfcell6.setCellValue(value7);
        HSSFCell hssfcell7 = hssfrow.createCell((short)7);
        hssfcell7.setCellValue(value8);
        HSSFCell hssfcell8 = hssfrow.createCell((short)8);
        hssfcell8.setCellValue(value9);
        HSSFCell hssfcell9 = hssfrow.createCell((short)9);
        hssfcell9.setCellValue(value10);
    }
    private static void addRow(HSSFSheet hssfsheet, int rowNum, String value1, Integer value2, Integer value3, Integer value4, Integer value5, Integer value6, Integer value7) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short)0);
        hssfcell0.setCellValue(value1);
        HSSFCell hssfcell1 = hssfrow.createCell((short)1);
        hssfcell1.setCellValue(value2);
        HSSFCell hssfcell2 = hssfrow.createCell((short)2);
        hssfcell2.setCellValue(value3);
        HSSFCell hssfcell3 = hssfrow.createCell((short)3);
        hssfcell3.setCellValue(value4);
        HSSFCell hssfcell4 = hssfrow.createCell((short)4);
        hssfcell4.setCellValue(value5);
        HSSFCell hssfcell5 = hssfrow.createCell((short)5);
        hssfcell5.setCellValue(value6);
        HSSFCell hssfcell6 = hssfrow.createCell((short)6);
        hssfcell6.setCellValue(value7);
    }
    private static void addRow(HSSFSheet hssfsheet, int rowNum, String key, String values) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short)0);
        hssfcell0.setCellValue(key);
        HSSFCell hssfcell1 = hssfrow.createCell((short)1);
        hssfcell1.setCellValue(values);
    }

    private static void addRow(HSSFSheet hssfsheet, int rowNum, String key, String value1,String value2) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short)0);
        hssfcell0.setCellValue(key);
        HSSFCell hssfcell1 = hssfrow.createCell((short)1);
        hssfcell1.setCellValue(value1);
        HSSFCell hssfcell2 = hssfrow.createCell((short)2);
        hssfcell2.setCellValue(value2);
    }
    private static void addRow(HSSFSheet hssfsheet, int rowNum, String key, Integer values) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short)0);
        hssfcell0.setCellValue(key);
        HSSFCell hssfcell1 = hssfrow.createCell((short)1);
        hssfcell1.setCellValue(values);
    }

    private static void addRow(HSSFSheet hssfsheet, int rowNum, String key, Integer value1,Integer value2) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short)0);
        hssfcell0.setCellValue(key);
        HSSFCell hssfcell1 = hssfrow.createCell((short)1);
        hssfcell1.setCellValue(value1);
        HSSFCell hssfcell2 = hssfrow.createCell((short)2);
        hssfcell2.setCellValue(value2);
    }
}