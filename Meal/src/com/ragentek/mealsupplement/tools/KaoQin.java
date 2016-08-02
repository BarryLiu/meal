/**
 *
 */
package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.EntityList;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TKaoqin;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.db.bean.VKaoqin;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.listener.UserTimer;
import com.ragentek.mealsupplement.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kui.li
 *         普通人员补助：
 *         保洁补助：27元/天，非工作日普通员工算，涂茂银，李忠霞，欧连凤
 *         司机补助：商务车司机 33元/天
 *         保安补助：27元/天，
 */
public class KaoQin {
    private static Map<String, String> mapHoliday = new HashMap<String, String>();
    private static Map<String, String> mapWorkday = new HashMap<String, String>();

    private static Map<String, Integer> map = new TreeMap<String, Integer>(new Comparator<String>() {
        public int compare(String obj1, String obj2) {
            //升序排序
            return obj1.compareTo(obj2);
        }
    });

    private static String rootPath = "D://kaoqin";

    /**
     *
     */
    public KaoQin() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     * @throws
     */
    public static void main(String[] args) throws Exception {
        //rootPath = "D://kaoqin";//System.getProperty("user.dir");//TotalFee.class.getClass().getResource("/").getPath();

        /*System.out.println("当前路径：" + rootPath);
        File folder = new File(rootPath + "//files");
        File details = new File(rootPath + "//details");
        details.mkdirs();
        System.out.println("文件路径：" + folder.getPath());
        folder.mkdirs();
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("未找到对应excel文件，或文件放置位置错误！");
        } else {
            File[] files = folder.listFiles();
            if (files == null || files.length <= 0) {
                System.out.println("未放置任何统计文件，请将需要统计的文件放置到 files目录！");
                return;
            }
            map.clear();
            for (File item : files) {
                if (item.getName().endsWith(".xls")) {
                    System.out.println("开始统计：" + item.getName());*/
//                    try {
//                        getData(item, 0);
//                    } catch (Exception e) {
//                        System.out.println("    " + item.getName() + "文件有异常");
//                    }
                /*}
            }
        }*/
        /*
        String start = "2016/01/01";
        String end = "2016/03/04";
        try {
            KaoQin.reCheckWorkTime(start, end);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        //updateLeaves("2016/02/25", "2016/02/26");
        //countKaoQinForFees("100123","2015/02/26 12:11", "2016/06/26 13:11");
        //countKaoQinForFees(0);
        //countKaoQinForFees("2015/08/01","2016/03/31");
        /*File f = new File("E:\\8.22-8.27.xls");
        saveData(f, 0);*/
        /*String startDayStr = "2015/08/01";
        String endDayStr = "2016/05/01";
        try {
            countKaoQinForFees(startDayStr, endDayStr); //处理打卡的情况
            UserTimer.handle(startDayStr, endDayStr); //处理未打卡的情况
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //updateFeeTyps("2016/05/17", "2016/05/17");
        String startDayStr = "2016/06/14";
        String endDayStr = "2016/06/19";
        try {
            //可以先删除t_fees表中开始和结束都是 06:00:59 的数据，因为countKaoQinForFees(startDayStr, endDayStr)是从kaoqin表里面读取的，而UserTimer.handle对于已经存在于t_fees表中的则不处理了
            //或者更干脆，直接删除t_fees，t_leave，t_bill表中的相关数据
            countKaoQinForFees(startDayStr, endDayStr); //处理打卡的情况
            UserTimer.handle(startDayStr, endDayStr); //处理未打卡的情况
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法仅手动调用，设置t_fees表的fee_type字段
     * @param startDayStr
     * @param endDayStr
     */
    private static void updateFeeTyps(String startDayStr, String endDayStr) throws Exception {
        String sql = "select * from t_fees where 1=1";
        EntityList lst = new EntityList(sql);
        if(startDayStr != null) {
            lst.append(" and day_str >= ?");
            lst.add(startDayStr);
        }
        if(endDayStr != null) {
            lst.append(" and day_str <= ?");
            lst.add(endDayStr);
        }
        List<TFees> tFeesList = DBUtils.query(lst, TFees.class);
        for(TFees tFees : tFeesList) {
            //System.out.println("Handing... "+tFees.getDayStr()+":"+tFees.getUserInfo());
            int feeType = LeaveUtil.getFeeType(tFees);
            tFees.setFeeType(feeType);
            DBUtils.update(tFees);
        }
    }

    /**
     * 该方法仅手动调用，用于重新计算一段时间内的异常记录（t_leave表）
     * @param startDayStr 开始时间，格式：yyyy/MM/dd
     * @param endDayStr 结束时间，格式：yyyy/MM/dd
     */
    private static void updateLeaves(String startDayStr, String endDayStr) {
        String sql = "select * from t_fees where 1=1";
        EntityList lst = new EntityList(sql);
        if(startDayStr != null) {
            lst.append(" and day_str >= ?");
            lst.add(startDayStr);
        }
        if(endDayStr != null) {
            lst.append(" and day_str <= ?");
            lst.add(endDayStr);
        }
        List<TFees> tFeesList = DBUtils.query(lst, TFees.class);
        for(TFees tFees : tFeesList) {
            //System.out.println("Handing... "+tFees.getDayStr()+":"+tFees.getUserInfo());
            LeaveUtil.saveOrUpdateLeave(tFees);
        }
    }

    public static long saveData(File file, int ignoreRows)
            throws FileNotFoundException, IOException {
        return saveData(file, ignoreRows, null, null);
    }

    public static long saveData(File file, int ignoreRows, String sDate, String eDate)

            throws FileNotFoundException, IOException {
        //System.out.println("saveData : file="+file.getAbsolutePath()+",ignoreRows="+ignoreRows+",sDate="+sDate+",eDate="+eDate);
        String origsDate = sDate; //此次录入最小日期
        String origeDate = eDate; //此次录入最大日期
        if(!TextUtil.isNullOrEmpty(sDate)) {
            sDate = sDate+" 06:01:00"; //此次录入最小时间，用于和打卡记录比较
        }
        if(!TextUtil.isNullOrEmpty(eDate)) {
            Calendar eDateCalendar = Calendar.getInstance();
            eDateCalendar.setTime(DateTools.formatStringToDate(eDate, DateTools.FORSTR_DATE));
            eDateCalendar.add(Calendar.DAY_OF_MONTH, 1); //加上一天,后一天的06:00:59
            eDateCalendar.set(Calendar.HOUR_OF_DAY, 6); //hour_of_day是24小时制，hour是12小时制
            eDateCalendar.set(Calendar.MINUTE, 0);
            eDateCalendar.set(Calendar.SECOND, 59);
            eDateCalendar.set(Calendar.MILLISECOND, 0);
            eDate = DateTools.formatDateToString(eDateCalendar.getTime(), DateTools.FORSTR_YYYYHHMMHHMMSS); //此次录入最大时间，用于和打卡记录比较
        }
        //sDate,eDate是限定了此次录入的最大时间范围，startDayStr和endDayStr是实际录入的时间范围
        String startDayStr = null; //实际录入最小日期
        String endDayStr = null; //时间录入最大日期

        long rtnFirstRowIndex = 0;

        List<String[]> result = new ArrayList<String[]>();

        int rowSize = 0;

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

        // 打开HSSFWorkbook
        POIFSFileSystem fs = new POIFSFileSystem(in);

        HSSFWorkbook wb = new HSSFWorkbook(fs);

        HSSFCell cell = null;

        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            System.out.println("sheetName=="+st.getSheetName());
            // 第一行为标题，不取
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {

                HSSFRow row = st.getRow(rowIndex);

                if (row == null) {
                    System.out.println("row "+rowIndex+" is null!");
                    continue;

                }

                int tempRowSize = row.getLastCellNum() + 1;

                if (tempRowSize > rowSize) {

                    rowSize = tempRowSize;

                }

                String[] values = new String[rowSize];

                Arrays.fill(values, "");

                boolean hasValue = false;

                String nos = "", cardno = "", userid = "", names = "", depart = "", dotimes = "", addresss = "", status = "", descs = "";
                for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {

                    String value = "";

                    cell = row.getCell(columnIndex);

                    if (cell != null) {


                        //cell.set.setEncoding(HSSFCell.ENCODING_UTF_16);

                        switch (cell.getCellType()) {

                            case HSSFCell.CELL_TYPE_STRING:

                                value = cell.getStringCellValue();

                                break;

                            case HSSFCell.CELL_TYPE_NUMERIC:

                                if (HSSFDateUtil.isCellDateFormatted(cell)) {

                                    Date date = cell.getDateCellValue();

                                    if (date != null) {

                                        value = new SimpleDateFormat("HH:mm:ss")

                                                .format(date);

                                    } else {

                                        value = "";

                                    }

                                } else {

                                    value = new DecimalFormat("0").format(cell

                                            .getNumericCellValue());

                                }

                                break;

                            case HSSFCell.CELL_TYPE_FORMULA:

                                // 导入时如果为公式生成的数据则无值
                                cell.setCellType(cell.CELL_TYPE_STRING);
                                if (!cell.getStringCellValue().equals("")) {

                                    value = cell.getStringCellValue();

                                } else {

                                    value = cell.getNumericCellValue() + "";

                                }

                                break;

                            case HSSFCell.CELL_TYPE_BLANK:

                                break;

                            case HSSFCell.CELL_TYPE_ERROR:

                                value = "";

                                break;

                            case HSSFCell.CELL_TYPE_BOOLEAN:

                                value = (cell.getBooleanCellValue() == true ? "Y"

                                        : "N");

                                break;

                            default:

                                value = "";

                        }

                    }

                    //if (columnIndex == 0 && value.trim().equals("")) {
                    if (value.trim().equals("")) {
                        //break;

                    }

                    String tmpValues = rightTrim(value);

                    //  部门  姓名  考勤号码  日期时间  机器号  编号  比对方式  卡号
                    switch (columnIndex) {
                        case 0:
                            nos = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"部门".equals(tmpValues))
                                    return -1;
                            }
                            break;
                        case 1:
                            names = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"姓名".equals(tmpValues))
                                    return -1;
                            }
                            break;
                        case 2:
                            userid = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"考勤号码".equals(tmpValues))
                                    return -1;
                            }
                            break;
                        case 3:
                            dotimes = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"日期时间".equals(tmpValues))
                                    return -1;
                            }
                            break;
                        case 4:
                            depart = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"机器号".equals(tmpValues))
                                    return -1;
                            }
                            break;
                        case 5:
                            addresss = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"编号".equals(tmpValues))
                                    return -1;
                            }
                            break;
                        case 6:
                            status = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"比对方式".equals(tmpValues))
                                    return -1;
                            }
                            break;
                        case 7:
                            descs = tmpValues;
                            if(rowIndex == 0) { // 第1行
                                if(!"卡号".equals(tmpValues))
                                    return -1;
                            }
                            break;
                    }
                    hasValue = true;
                }
                if(!hasValue) return -2;
                if(rowIndex ==0)
                    continue;

               // System.out.println("WPF format:" + DateTools.FORSTR_YYYYHHMMHHMMSS );

                // save rowdata
                TKaoqin rowdata = new TKaoqin();
                rowdata.setNos(nos);
                // rowdata.setCardno(cardno);
                rowdata.setUserid(userid);
                rowdata.setNames(names);
                rowdata.setDepart(depart);
                //System.out.println("WPF time1:" + dotimes );
                if(!dotimes.equals("")) {
                    rowdata.setDotimes(DateTools.formatDateToString(DateTools.formatStringToDate(dotimes, DateTools.FORSTR_YYYYHHMMHHMMSS), DateTools.FORSTR_YYYYHHMMHHMMSS));
                } else {
                   // System.out.println("WPF:" + rowdata.toString() );
                    continue;
                }
                //System.out.println("WPF time2:" + dotimes );
                rowdata.setAddresss(addresss);
                rowdata.setStatus(status);
                rowdata.setDescs(descs);

                //add zhangzixiao 20160421 start
                if(!TextUtil.isValidNumber(userid)) { //学号有效性检查，禁止录入无效的数据
                    System.out.println("Invalid number : "+userid);
                    continue;
                }
                User user = UserCache.getInstance().get(userid);
                if(user != null && user.getStatus() != null && user.getStatus() == User.STATUS_NO_ATTENDANCE) {
                    continue; //标为不参与考勤的不录入考勤系统
                }
                if(TextUtil.isNullOrEmpty(names)) { //名称不能为空
                    continue;
                }
                //add zhangzixiao 20160421 end

                //System.out.println("WPF_REEF:" + dotimes.split(" ")[0]);
                if(compareDataStr( dotimes.split(" ")[0],"2015/08/01") < 0) {  // 2015-04-01 100007 100006 10000010 100039
                    if(rowdata.getUserid().equals("68") || rowdata.getUserid().equals("100043") || rowdata.getUserid().equals("100016")
                            || rowdata.getUserid().equals("100039" )|| rowdata.getUserid().equals("1000010" )|| rowdata.getUserid().equals("100006" )
                            || rowdata.getUserid().equals("100007" )){
                        System.out.println("RowData:" + rowdata.getUserid() + " 不入库！");
                        continue;
                    }
                }
                //add zhangzixiao 20160505 start
                //限制此次录入的考勤范围
                if(!TextUtil.isNullOrEmpty(sDate) && rowdata.getDotimes().compareTo(sDate) < 0) { //sDate : 06:01:00
                    continue;
                }
                if(!TextUtil.isNullOrEmpty(eDate) && rowdata.getDotimes().compareTo(eDate) > 0) { //eDate : 06:00:59
                    continue;
                }
                //add zhangzixiao 20160505 end
                // check 考勤时间是否存在
                String sql = "select * from t_kaoqin where userid=? and names=? and dotimes=?";
                List<TKaoqin> lstEx = DBUtils.query(sql,new String[]{rowdata.getUserid(),rowdata.getNames(),rowdata.getDotimes()},TKaoqin.class);
                if(lstEx != null && lstEx.size()>0) {
                    //System.out.println("RowData:" + rowdata.toString() + " 已经存在！！！");
                    continue;
                }

                long rtn = DBUtils.insert(rowdata);
               // System.out.println("RowData:" + rowdata.toString() + ";入库结果=" + rtn);
                if(rtnFirstRowIndex == 0) {
                    rtnFirstRowIndex = rtn;
                }
                if (hasValue) {
                    result.add(values);
                }

                String dayStr = rowdata.getDotimes().split(" ")[0];
                if(startDayStr == null) {
                    startDayStr = dayStr;
                    endDayStr = dayStr;
                } else {
                    if(startDayStr.compareTo(dayStr) > 0) {
                        startDayStr = dayStr;
                    } else if(endDayStr.compareTo(dayStr) < 0) {
                        endDayStr = dayStr;
                    }
                }
            }
        }
        in.close();
        //例如：限制读到5月5号，但实际会读到5月6号凌晨6点之前，所以endDayStr会是5月6号，但5月6号实际的数据(凌晨6点之后的数据)一点都没有读入，会为每个参与考勤的人生成一个旷工，这种情况应避免。
        if(endDayStr != null && !TextUtil.isNullOrEmpty(origeDate) && endDayStr.compareTo(origeDate) > 0) {
            endDayStr = origeDate;
        }
        if(startDayStr != null && endDayStr != null && startDayStr.compareTo(endDayStr) <= 0) {
            //System.out.println("saveData over : startDayStr="+startDayStr+",endDayStr="+endDayStr);
            try {
                countKaoQinForFees(startDayStr, endDayStr); //处理打卡的情况
                UserTimer.handle(startDayStr, endDayStr); //处理未打卡的情况
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rtnFirstRowIndex;
    }

    public static String rightTrim(String str) {

        if (str == null) {

            return "";

        }

        int length = str.length();

        for (int i = length - 1; i >= 0; i--) {

            if (str.charAt(i) != 0x20) {

                break;

            }

            length--;

        }

        return str.substring(0, length);

    }

    /**
     * 计算本次导入数据的餐费
     * @param firstRow 起始行
     */
    public static void countKaoQinForFees(long firstRow) throws ParseException {
        /*String sql = "select userid,names,SUBSTRING(dotimes,1,10) as day,min(dotimes) as shangban,max(dotimes) as xiaban\n" +
                "from t_kaoqin \n" +
                "where id>= " + firstRow + " \n" +
                "group by userid,names,SUBSTRING(dotimes,1,10) " ;*/

        String sqlday = "select top 1 * \n" +
                "from t_kaoqin \n" +
                "where id>= " + firstRow + " \n" +
                 "order by dotimes asc";
        String startday = "2015/08/01";
        List<TKaoqin>  lstday = DBUtils.query(sqlday, TKaoqin.class);
        if(lstday !=null && lstday.size()>0) {
            for (TKaoqin tmpItem : lstday) {
                Calendar cal = Calendar.getInstance();
                //System.out.println("WPF update begin day11:" + tmpItem.getDotimes().substring(0, 10)+"  startday  "+startday );
                cal.setTime(DateTools.formatStringToDate(tmpItem.getDotimes().substring(0,10), "yyyy/MM/dd")); // yyyy-MM-dd
                cal.add(Calendar.DAY_OF_MONTH, -1); // 前一天

                startday=DateTools.formatDateToString(cal.getTime(),"yyyy/MM/dd");

                //System.out.println("WPF update begin day:" + tmpItem.getDotimes()+"  startday  "+startday );
            }
        }
        String sql = "select userid,names,SUBSTRING(dotimes,1,10) as day,min(dotimes) as shangban,max(dotimes) as xiaban\n" +
                "from t_kaoqin \n" +
                "where SUBSTRING(dotimes,1,10) >= '"+ startday + "'  \n" +
                "group by userid,names,SUBSTRING(dotimes,1,10) " ;
        List<VKaoqin> lst = DBUtils.query(sql, VKaoqin.class);

        countKaoQinForFees(lst);
    }

    public static void countKaoQinForFees(String startDateTime, String endDateTime) throws ParseException {
        String startday = LeaveUtil.getDayStr(startDateTime);
        String endday = LeaveUtil.getDayStr(endDateTime);
        String sql = "select userid,names,SUBSTRING(dotimes,1,10) as day,min(dotimes) as shangban,max(dotimes) as xiaban" +
                " from t_kaoqin" +
                " where SUBSTRING(dotimes,1,10) >= '"+ startday + "' and SUBSTRING(dotimes,1,10) <= '"+endday+"'" +
                " group by userid,names,SUBSTRING(dotimes,1,10) " ;
        List<VKaoqin> lst = DBUtils.query(sql, VKaoqin.class);

        countKaoQinForFees(lst);
    }

    public static void countKaoQinForFees(String number, String startDateTime, String endDateTime) throws ParseException {
        String startday = LeaveUtil.getDayStr(startDateTime);
        String endday = LeaveUtil.getDayStr(endDateTime);
        String sql = "select userid,names,SUBSTRING(dotimes,1,10) as day,min(dotimes) as shangban,max(dotimes) as xiaban" +
                " from t_kaoqin" +
                " where SUBSTRING(dotimes,1,10) >= '"+ startday + "' and SUBSTRING(dotimes,1,10) <= '"+endday+"' and userid='"+number+"'" +
                " group by userid,names,SUBSTRING(dotimes,1,10) " ;
        List<VKaoqin> lst = DBUtils.query(sql, VKaoqin.class);
        countKaoQinForFees(lst);
    }

    // yingjing.liu add  20160628  start add  对于插入单个用户后 单独计算他的异常等信息
    public static void countKaoQinForFees2(String number, String startday) throws ParseException {

        String sql = "select userid,names,SUBSTRING(dotimes,1,10) as day,min(dotimes) as shangban,max(dotimes) as xiaban" +
                " from t_kaoqin" +
                " where SUBSTRING(dotimes,1,10) >= '"+ startday + "' and  userid='"+number+"'" +
                " group by userid,names,SUBSTRING(dotimes,1,10) " ;
        List<VKaoqin> lst = DBUtils.query(sql, VKaoqin.class);
        countKaoQinForFees(lst);
    }
    //  yingjing.liu add  20160628  end add

    private static void countKaoQinForFees(List<VKaoqin> lst) throws ParseException {
        if(lst != null && lst.size()>0) {
            //System.out.println("WPF update fees,list size:" + lst.size() );
            for(VKaoqin item : lst) {
                TFees fees = new TFees();
                fees.setUserInfo(item.getUserid() + "-" + item.getNames());
                fees.setNumber(item.getUserid());
                fees.setName(item.getNames());
                fees.setDayStr(item.getDay());

                if(compareDataStr(item.getDay(),"2015/04/01") < 0) {  // 2015-04-01
                    continue;
                }

                // start time by day => shang ban
                //System.out.println("WPF shangban:" + item.getShangban() );
                //System.out.println("WPF"+item.getNames()+item.getDay()+" shangban:" +  item.getShangban().substring(11) +"xiaban:"+ item.getXiaban().substring(11));
                String shangBan = item.getShangban().substring(11);
                if(shangBan.compareTo("06:00:59") <=0 ) { // 上班时间小于早上6点，视为前天加班，需要将此时间统计到上一天
                    // 查询当天所有考勤数据，找到离早上6点最近的时间点作为前天的下班时间
                    String tmpSql = "select top 1 * from t_kaoqin where userid=? and names=? and SUBSTRING(dotimes,1,10)=? and dotimes <= '" + item.getDay()+" 06:00:59" + "' order by dotimes desc";
                    List<TKaoqin> tmpLst = DBUtils.query(tmpSql,new String[]{item.getUserid(),item.getNames(),item.getDay()},TKaoqin.class);
                    //System.out.println("WPF update lastday xiaban:" +item.getUserid() + item.getNames()+ item.getDay() );
                    for(TKaoqin tmpItem : tmpLst) {
                        // 重新计算上一天的餐补
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(DateTools.formatStringToDate(item.getDay(),"yyyy/MM/dd")); // yyyy-MM-dd
                        cal.add(Calendar.DAY_OF_MONTH,-1); // 前一天

                        // System.out.println("WPF update lastday xiaban:" +tmpItem.getUserid() + tmpItem.getNames()+ tmpItem.getDotimes() );
                        // 从数据库中查询前一天数据，并更新
                        tmpSql = "select * from t_fees where user_info=? and day_str=?";
                        List<TFees> lstFees = DBUtils.query(tmpSql,new String[]{fees.getUserInfo(),DateTools.formatDateToString(cal.getTime(),"yyyy/MM/dd")},TFees.class);  // yyyy-MM-dd
                        if(lstFees != null && lstFees.size() > 0) {
                            for(TFees tItem : lstFees) {
                                //System.out.println("WPF update lastday xiaban:" + tItem.getEndTime()+"change to:"+tmpItem.getDotimes().substring(11) );
                                totalAndSaveFees(tItem.getDayStr(),tItem.getStartTime(),tmpItem.getDotimes().substring(11),tItem, false);
                            }
                        }
                    }

                    // 获取最新的上班时间，即早上6点（06:00:59）以后的上班时间
                    tmpSql = "select top 1 * from t_kaoqin where userid=? and names=? and SUBSTRING(dotimes,1,10)=? and dotimes > '" + item.getDay()+" 06:00:59" + "' order by dotimes asc";
                    tmpLst = DBUtils.query(tmpSql,new String[]{item.getUserid(),item.getNames(),item.getDay()},TKaoqin.class);
                    //System.out.println("WPF update shangban:" +item.getUserid() + item.getNames()+ item.getDay() );
                    if(tmpLst != null && tmpLst.size()>0) {
                        for(TKaoqin tKaoqin : tmpLst) {
                            //System.out.println("WPF shangban :" + tKaoqin.getNames()+tKaoqin.getDotimes() );
                            shangBan = tKaoqin.getDotimes().substring(11);
                        }
                    } else { //今天没有06:00:59之后的打卡记录，认为今天未上班
                        continue;
                    }
                }
                // end time by day => xia ban
                String xiaBan = item.getXiaban().substring(11);
                // System.out.println("WPF update xiaban:" +item.getUserid() + item.getNames()+ item.getXiaban() );
                //System.out.println("WPF"+item.getNames()+item.getDay()+" shangban:" + item.getShangban() +"xiaban:"+item.getXiaban());
               /* if(compareDataStr(item.getDay(),"2015/08/19") == 0) {  // 2015-04-01
                    System.out.println("WPF shangban:" + item.getShangban() +"xiaban:"+item.getXiaban());

                }
                if(shangBan.compareTo(item.getXiaban())==0){
                    System.out.println("WPF"+item.getDay()+" shangban:" + item.getShangban() +"xiaban:"+item.getXiaban());
                }*/

                // 开始计算当前的餐补
                totalAndSaveFees(item.getDay(),shangBan,xiaBan,fees,true);
            }
        }
    }

    private static TFees getPreDayFees(TFees fees) {
        TFees preDayFees = null;
        String dayStr = fees.getDayStr();
        Date date = DateTools.formatStringToDate(dayStr, DateTools.FORSTR_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String preDayStr = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
        String sql = "SELECT * FROM t_fees WHERE user_info=? AND day_str=?";
        List<TFees> lst = DBUtils.query(sql, new String[]{fees.getUserInfo(), preDayStr}, TFees.class);
        if(lst.size() > 0) {
            preDayFees = lst.get(0);
        }
        return preDayFees;
    }

    //获得前一天下班时间，判断是否为9点后下班，从而判断第二个是否迟到了
    public static String getPreXiaban(TFees fees) {
        String preXiaban = null;
        TFees preDayFees = getPreDayFees(fees);
        if(preDayFees != null) {
            preXiaban = preDayFees.getEndTime();
        }
        return preXiaban;
    }

    //如果工作到了第二天，那么久加上24小时
    public static String add24Hour(String timeStr) {
        int hour = Integer.parseInt(timeStr.split(":")[0]) + 24;
        return hour + timeStr.substring(2);
    }

    //是否加班到了第二天凌晨后
    public static boolean isWorkflowToNextDay(String xiaBan) {
        return xiaBan!=null && xiaBan.compareTo("06:00:59") <= 0;
    }

    //特别注意：调用该方法前应该先判断下当天是否为工作日，如果非工作日直接赋值为“”即可，非工作日是没有异常的
    public static String getStatus(String shangBan, String xiaBan, String preXiaban) {
        if(isWorkflowToNextDay(xiaBan)) {
            xiaBan = add24Hour(xiaBan);
        }
        if(shangBan.equals(xiaBan)) {
            if(LeaveUtil.DEF_ABSENCE_TIME.equals(shangBan)) {
                return "旷工";
            } else {
                return "未打卡";
            }
        }
        if(isWorkflowToNextDay(preXiaban)) {
            preXiaban = add24Hour(preXiaban);
        }
        if(preXiaban!=null && preXiaban.compareTo("20:59:59") > 0) {
            if(xiaBan.compareTo("09:30:59") <= 0) {
                return "旷工";
            }
            if (shangBan.compareTo("09:30:59") > 0 && shangBan.compareTo("17:45:00")<0) {
                if(xiaBan.compareTo("17:45:00") < 0) {
                    //modify zhangzixiao 如果上班总时长小于1个小时，按旷工算 start
                    //return "迟到|早退";
                    int workTotalSeconds = LeaveUtil.getDiffSeconds(shangBan, xiaBan);
                    if(workTotalSeconds < 3600) {
                        return "旷工";
                    } else {
                        return "迟到|早退";
                    }
                    //modify zhangzixiao end
                } else {
                    return "迟到";
                }
            }
        } else {
            if(xiaBan.compareTo("09:05:59") <= 0) {
                return "旷工";
            }
            if(shangBan.compareTo("09:05:59") > 0 && shangBan.compareTo("17:45:00")<0) {
                if(xiaBan.compareTo("17:45:00") < 0) {
                    //modify zhangzixiao 如果上班总时长小于1个小时，按旷工算 start
                    //return "迟到|早退";
                    int workTotalSeconds = LeaveUtil.getDiffSeconds(shangBan, xiaBan);
                    if(workTotalSeconds < 3600) {
                        return "旷工";
                    } else {
                        return "迟到|早退";
                    }
                    //modify zhangzixiao end
                } else {
                    return "迟到";
                }
            }
        }
        if(xiaBan.compareTo("17:45:00") < 0) {
            return "早退";
        }
        if(shangBan.compareTo("17:45:00") >= 0) {
            return "旷工";
        }
        return "";
    }

    //特定员工number重新进行某一天dayStr的数据：dayStr format：yyyy/MM/dd
    public static void totalAndSaveFees(String dayStr, String number, String name) throws ParseException {
        //首先删除这一天相关的数据：如果为周末且包含未打卡，如果不先删除，此时去掉未打卡处理会比较麻烦。索性先删除好了
        //这里只用删除t_fees就可以了，t_leave会根据t_fees自动更新，t_bill如果没有人工操作也会自动更新
        String sql = "delete t_fees where number=? and day_str=?";
        EntityList lst = new EntityList(sql);
        lst.add(number);
        lst.add(dayStr);
        DBUtils.executeUpdate(lst);
        //查询当天的数据是否存在，如果当天06:01-23:59无打卡记录认为没有打开
        sql = "select userid,names,SUBSTRING(dotimes,1,10) as day,min(dotimes) as shangban,max(dotimes) as xiaban" +
                " from t_kaoqin" +
                " where dotimes > '"+ dayStr + " 06:00:59' and SUBSTRING(dotimes,1,10) <= '"+dayStr+"' and userid='"+number+"'" +
                " group by userid,names,SUBSTRING(dotimes,1,10) " ;
        List<VKaoqin> kaoqinList = DBUtils.query(sql, VKaoqin.class);
        if(kaoqinList != null && kaoqinList.size() > 0) { //当天06:01-23:59有打卡记录
            countKaoQinForFees(kaoqinList);
            //查看第二天06:01之前是否有打卡记录
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateTools.formatStringToDate(dayStr, DateTools.FORSTR_DATE));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String nextDay = DateTools.formatDateToString(calendar.getTime(), DateTools.FORSTR_DATE);
            sql = "select userid,names,SUBSTRING(dotimes,1,10) as day,min(dotimes) as shangban,max(dotimes) as xiaban" +
                    " from t_kaoqin" +
                    " where SUBSTRING(dotimes,1,10) >= '"+ nextDay + "' and dotimes <= '"+nextDay+" 06:00:59' and userid='"+number+"'" +
                    " group by userid,names,SUBSTRING(dotimes,1,10) " ;
            List<VKaoqin> nextKaoqinList = DBUtils.query(sql, VKaoqin.class);
            if(nextKaoqinList != null && nextKaoqinList.size() > 0) {
                sql = "select * from t_fees where number=? and day_str=?"; //获得今天插入后新的数据
                List<TFees> lstFees = DBUtils.query(sql,new String[]{number,dayStr},TFees.class);  // yyyy-MM-dd
                if(lstFees != null && lstFees.size() > 0) {
                    for(TFees tItem : lstFees) {
                        //System.out.println("WPF update lastday xiaban:" + tItem.getEndTime()+"change to:"+tmpItem.getDotimes().substring(11) );
                        totalAndSaveFees(tItem.getDayStr(),tItem.getStartTime(),nextKaoqinList.get(0).getXiaban().substring(11),tItem, false);
                    }
                }
            }
        } else { //当天06:01-23:59无打开记录
            if(!checkWetherHoliday(dayStr)) { //非休息日则加入一条未打卡记录
                TFees tFees = new TFees();
                tFees.setDayStr(dayStr);
                tFees.setUserInfo(number + "-" + name);
                tFees.setNumber(number);
                tFees.setName(name);
                tFees.setStartTime("06:59:59");
                tFees.setEndTime("06:59:59");
                KaoQin.totalAndSaveFees(tFees);
            }
        }
    }

    public static void totalAndSaveFees(TFees tFees) throws ParseException {
        String day = tFees.getDayStr();
        String startTime = tFees.getStartTime();
        String endTime = tFees.getEndTime();
        boolean isToday = !isWorkflowToNextDay(endTime);
        totalAndSaveFees(day, startTime, endTime, tFees, isToday);
    }

    /**
     *
     * @param day
     * @param shangBan
     * @param xiaBan
     * @param fees
     * @param isToday
     * @throws ParseException
     */
    private static void totalAndSaveFees(String day ,String shangBan, String xiaBan, TFees fees, boolean isToday) throws ParseException {
        //add zhangzixiao 20160330 start
        fees.setDayStr(day);
        fees.setStartTime(shangBan);
        fees.setEndTime(xiaBan);
        LeaveUtil.handleForgetClock(fees); //处理了未打卡记录后可能改变shangBan,xiaBan和isToday的值，故这些值应该重新判断
        shangBan = fees.getStartTime();
        xiaBan = fees.getEndTime();
        isToday = !isWorkflowToNextDay(xiaBan);
        //System.out.println("totalAndSaveFees: shangBan="+shangBan+",xiaBan="+xiaBan+",isToday="+isToday);
        //add zhangzixiao 20160330 end
        String realXiaBan = xiaBan;

        if(!isToday) { // 若是昨天的统计将时间改成24+制，如04:01:02替换成：28:01:02
            int hour = Integer.parseInt(xiaBan.split(":")[0]) + 24; // 加上24小时
            xiaBan = hour + xiaBan.substring(2);
        }

        int fee = 0; // 全天总计餐补
        // 早餐 要求9点00前打卡都 给3元的补助
        if(shangBan.compareTo("09:00:00") <= 0 && xiaBan.compareTo("09:00:00")>0) {
            fee += 3;
            fees.setFee1(3);
        } else {
            fees.setFee1(0);
        }
        // 午餐
        if((shangBan.compareTo("10:30:00") <= 0 && xiaBan.compareTo("11:45:59")>0) || (shangBan.compareTo("12:45:00") <= 0 && xiaBan.compareTo("14:00:59")>0)) {
            // 判定是 否为双休日，若为又休日，则标准变为15元
            if (checkWetherHoliday(day)) { // 非工作日
                fee += 20;
                fees.setFee2(20);
            } else { // 工作日
                fee += 20;
                fees.setFee2(20);
            }
        } else {
            fees.setFee2(0);
        }
        // 晚餐
       /* if(compareDataStr(day,"2015/05/01") >= 0) {  // 2015-05-01
            if (shangBan.compareTo("19:09:59") <= 0 && xiaBan.compareTo("19:49:59") > 0) {
                fee += 3;
                fees.setFee3(15);
            } else {
                fees.setFee3(0);
            }
        } else */{
            if (shangBan.compareTo("17:45:00") <= 0 && xiaBan.compareTo("19:59:59") > 0) {
                fee += 15;
                fees.setFee3(15);
            } else {
                fees.setFee3(0);
            }
        }

        // 夜宵
        // 从2014-12-08起，晚餐时间调整为晚上9：50起补助
        /*if(compareDataStr(day,"2014/12/08") >= 0) {  // 2014-12-08
            if(shangBan.compareTo("21:09:59") <= 0 && xiaBan.compareTo("21:49:59")>0) {
                fee += 15;
                fees.setFee4(15);
            } else {
                fees.setFee4(0);
            }
        } else */ {
            if (shangBan.compareTo("17:45:00") <= 0 && xiaBan.compareTo("21:59:59")>0) { // 夜宵
                fee += 15;
                fees.setFee4(15);
            } else {
                fees.setFee4(0);
            }
        }
       // 午夜
       /*
        if (shangBan.compareTo("23:19:59") <= 0 && xiaBan.compareTo("24:00:59")>0 && xiaBan.compareTo("31:00:59")<0) {
            fee += 15;
            fees.setFee5(15);
        } else {
            fees.setFee5(0);
        }
      */
        fees.setFee5(0);


        // 设置上下班结束时间
        fees.setStartTime(shangBan);
        fees.setEndTime(realXiaBan);
        String tmpstatus="";

        if (!checkWetherHoliday(day)) { // 非工作日
            tmpstatus = getStatus(shangBan, xiaBan, getPreXiaban(fees));
        /*if(shangBan.compareTo(xiaBan)==0){
            if("".equals(tmpstatus)) {
                tmpstatus+="未打卡";
            }
            else
            {
                tmpstatus+="|未打卡";
            }
        } else {
            if (shangBan.compareTo("09:05:59") > 0) {
                //tmpstatus+="迟到";
                if ("".equals(tmpstatus)) {
                    tmpstatus += "迟到";
                } else {
                    tmpstatus += "|迟到";
                }
            }
            if (xiaBan.compareTo("17:45:00") < 0) {
                //tmpstatus+="早退";
                if ("".equals(tmpstatus)) {
                    tmpstatus += "早退";
                } else {
                    tmpstatus += "|早退";
                }
            }
        }*///else 未打卡
        }
        fees.setStatus(tmpstatus);

        //计算加班时间
        // 判定是 否为双休日，若为又休日，则标准变为15元
        if (checkWetherHoliday(day)) { // 非工作日
            int time = (Integer.parseInt(xiaBan.split(":")[0])- Integer.parseInt(shangBan.split(":")[0])  )*60  +  Integer.parseInt(xiaBan.split(":")[1]) - Integer.parseInt(shangBan.split(":")[1]); //
            fees.setWorkovertime(time);
        } else { // 工作日
            if( Integer.parseInt(xiaBan.split(":")[0]) >= 19 ){
                int time = (Integer.parseInt(xiaBan.split(":")[0])- 19  )*60  +  Integer.parseInt(xiaBan.split(":")[1]); //
                fees.setWorkovertime(time);
           } else{
                fees.setWorkovertime(0);
            }
        }
        //System.out.println("WPF 加班:" + fees.getWorkovertime());

        fees.setFeeType(LeaveUtil.getFeeType(fees)); //add zhangzixiao 20160514

        // check数据是否存在，若存在则更新，否则插入新数据
        String sql = "SELECT * FROM t_fees WHERE user_info=? AND day_str=?";
        List<TFees> lstFees = DBUtils.query(sql, new String[]{fees.getUserInfo(), fees.getDayStr()}, TFees.class);
        if (lstFees != null && lstFees.size() > 0) { // 更新
            TFees origFees = lstFees.get(0);
            //if(!(origFees.getStartTime().equals(fees.getStartTime()) && origFees.getEndTime().equals(fees.getEndTime()))) { //开始时间和结束时间均相同则不进行更新
                fees.setId(lstFees.get(0).getId());
                //System.out.println("update = Fees:" + fees.toString());
                long rtn = DBUtils.update(fees);
                //System.out.println("update rtn=" + rtn + " Fees:" + fees.toString());
                LeaveUtil.saveOrUpdateLeave(fees); //add 20160328
            //}
        } else { // 插入
            long rtn = DBUtils.insert(fees);
            //System.out.println("insert = rtn=" + rtn + " Fees:" + fees.toString());
            //add zhangzixiao 20160328 start
            fees.setId(rtn);
            LeaveUtil.saveOrUpdateLeave(fees);
            //add zhangzixiao 20160328 end
        }
    }

    //重新计算>=startDayStr && <=endDayStr 期间内的加班和工作时间， startDayStr和endDayStr的格式为：yyyy/MM/dd
    //startDayStr和endDayStr对应数据库表t_kaoqin的字段day_str
    //目前该方法只是用于手动调整数据库数据，供main方法调用，不供系统使用
    private static void reCheckWorkTime(String startDayStr, String endDayStr) throws ParseException {
        //一天天的进行处理，也可一并进行处理
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(sdf.parse(endDayStr));
        Calendar sCal = Calendar.getInstance();
        sCal.setTime(sdf.parse(startDayStr));
        while(!sCal.after(endCal)) {
            String dayStr = sdf.format(sCal.getTime());
            /*** 更新休息日 ***/
            /*
            if(checkWetherHoliday(dayStr)) { //是休息日
                String sql = "select * from t_fees where day_str='"+dayStr+"'";
                List<TFees> lst = DBUtils.query(sql, TFees.class);
                System.out.print(sql+":"+lst.size());
                for(TFees fee : lst) {
                    String shangBan = fee.getStartTime();
                    String xiaBan = fee.getEndTime();
                    if(shangBan.compareTo("06:00:59") > 0) { //上班时间小于06:00:59的认为是无效记录，后续不会出现
                        if(xiaBan.compareTo("06:00:59") <= 0) { //加班到第二天凌晨后
                            int hour = Integer.parseInt(xiaBan.split(":")[0]) + 24; // 加上24小时
                            xiaBan = hour + xiaBan.substring(2);
                        }
                        int time = (Integer.parseInt(xiaBan.split(":")[0])- Integer.parseInt(shangBan.split(":")[0])  )*60  +  Integer.parseInt(xiaBan.split(":")[1]) - Integer.parseInt(shangBan.split(":")[1]);
                        fee.setWorkovertime(time);
                        fee.setStatus("");
                        DBUtils.update(fee);
                    }
                }
            }
             */
            /*** 更新工作日 ***/
            if(!checkWetherHoliday(dayStr)) { //是工作日
                String sql = "select * from t_fees where day_str='"+dayStr+"'";
                List<TFees> lst = DBUtils.query(sql, TFees.class);
                for(TFees fee : lst) {
                    String shangBan = fee.getStartTime();
                    String xiaBan = fee.getEndTime();
                    if(shangBan.compareTo("06:00:59") > 0) { //上班时间小于06:00:59的认为是无效记录，后续不会出现
                        if(isWorkflowToNextDay(xiaBan)) { //加班到第二天凌晨后
                            xiaBan = add24Hour(xiaBan);
                        }
                        String status = getStatus(shangBan, xiaBan, getPreXiaban(fee));
                        if(!fee.getStatus().equals(status)) {
                            fee.setStatus(status);
                            DBUtils.update(fee);
                        }
                    }
                }
            }
            sCal.add(Calendar.DAY_OF_MONTH, 1); //增加一天，开始下一天的处理
        }

        //sCal.add(Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * 判定此天是否为非工作日,day format:yyyy/MM/dd
     *
     * @param day format:yyyy/MM/dd
     * @return
     * @throws ParseException
     */
    public static boolean checkWetherHoliday(String day) throws ParseException {
        if (mapHoliday.containsKey(day)) { //法定假日
            return true;
        } else {
            if(day.compareTo("2016/01/01")>=0) { //2016年后的情况
                if(mapWorkday.containsKey(day)) { //法定工作日
                    return false;
                } else { //其他情况按是否为星期天来判断
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdf.parse(day));
                    if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { //周六，周日
                        return true;
                    } else {
                        return false;
                    }
                }
            } else { //2016年之前配置了所有的休息日，包括星期天，故只有没有配置为休息日的就是工作日
                return false;
            }
        }
    }

    public static boolean checkWetherHoliday(String day, String format) {
        boolean holiday = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(day);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
            String daystr = sdf2.format(date);
            holiday = checkWetherHoliday(daystr);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return holiday;
    }

    /**
     *
     * @param source
     * @param target
     * @return  -1:前者小，0:相等, 1:前者大
     */
    private static int compareDataStr(String source, String target) {
        //System.out.println("WPF_MEAL ,source = "+ source);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // "yyyy-MM-dd"
        try {
            long start = sdf.parse(source).getTime();
            long end = sdf.parse(target).getTime();
            if(start-end > 0) {
                return 1;
            }else if(start-end ==0) {
                return 0;
            } else {
                return -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static {
        /***** 2016年节假日和调休配置 Start *****/
        //元旦
        mapHoliday.put("2016/01/01", "");
        mapHoliday.put("2016/01/02", "");
        mapHoliday.put("2016/01/03", "");
        //春节，2月6日和2月14日上班，2月6日和年会调整了，所以最终情况2月6日也不上班
        mapHoliday.put("2016/02/06", "");
        mapHoliday.put("2016/02/07", "");
        mapHoliday.put("2016/02/08", "");
        mapHoliday.put("2016/02/09", "");
        mapHoliday.put("2016/02/10", "");
        mapHoliday.put("2016/02/11", "");
        mapHoliday.put("2016/02/12", "");
        mapHoliday.put("2016/02/13", "");
        mapWorkday.put("2016/02/14", "");
        //清明节
        mapHoliday.put("2016/04/02", "");
        mapHoliday.put("2016/04/03", "");
        mapHoliday.put("2016/04/04", "");
        //劳动节
        mapHoliday.put("2016/04/30", "");
        mapHoliday.put("2016/05/01", "");
        mapHoliday.put("2016/05/02", "");
        //端午节，6月12号周日上班
        mapHoliday.put("2016/06/09", "");
        mapHoliday.put("2016/06/10", "");
        mapHoliday.put("2016/06/11", "");
        mapWorkday.put("2016/06/12", "");
        //中秋节，9月18号周日上班
        mapHoliday.put("2016/09/15", "");
        mapHoliday.put("2016/09/16", "");
        mapHoliday.put("2016/09/17", "");
        mapWorkday.put("2016/09/18", "");
        //国庆节,10月8号，10月9号上班
        mapHoliday.put("2016/10/01", "");
        mapHoliday.put("2016/10/02", "");
        mapHoliday.put("2016/10/03", "");
        mapHoliday.put("2016/10/04", "");
        mapHoliday.put("2016/10/05", "");
        mapHoliday.put("2016/10/06", "");
        mapHoliday.put("2016/10/07", "");
        mapWorkday.put("2016/10/08", "");
        mapWorkday.put("2016/10/09", "");
        /***** 2016年节假日和调休配置 End *****/
        // 2015年假日
        mapHoliday.put("2015/01/01", "");
        mapHoliday.put("2015/01/02", "");
        mapHoliday.put("2015/01/03", "");
        mapHoliday.put("2015/01/10", "");
        mapHoliday.put("2015/01/11", "");
        mapHoliday.put("2015/01/17", "");
        mapHoliday.put("2015/01/18", "");
        mapHoliday.put("2015/01/24", "");
        mapHoliday.put("2015/01/25", "");
        mapHoliday.put("2015/01/31", "");
        mapHoliday.put("2015/02/01", "");
        mapHoliday.put("2015/02/07", "");
        mapHoliday.put("2015/02/08", "");
        mapHoliday.put("2015/02/16", "");
        mapHoliday.put("2015/02/17", "");
        mapHoliday.put("2015/02/18", "");
        mapHoliday.put("2015/02/19", "");
        mapHoliday.put("2015/02/20", "");
        mapHoliday.put("2015/02/21", "");
        mapHoliday.put("2015/02/22", "");
        mapHoliday.put("2015/02/23", "");
        mapHoliday.put("2015/02/24", "");
        mapHoliday.put("2015/03/01", "");
        mapHoliday.put("2015/03/07", "");
        mapHoliday.put("2015/03/08", "");
        mapHoliday.put("2015/03/14", "");
        mapHoliday.put("2015/03/15", "");
        mapHoliday.put("2015/03/21", "");
        mapHoliday.put("2015/03/22", "");
        mapHoliday.put("2015/03/28", "");
        mapHoliday.put("2015/03/29", "");
        mapHoliday.put("2015/04/04", "");
        mapHoliday.put("2015/04/05", "");
        mapHoliday.put("2015/04/06", "");
        mapHoliday.put("2015/04/11", "");
        mapHoliday.put("2015/04/12", "");
        mapHoliday.put("2015/04/18", "");
        mapHoliday.put("2015/04/19", "");
        mapHoliday.put("2015/04/25", "");
        mapHoliday.put("2015/04/26", "");
        mapHoliday.put("2015/05/01", "");
        mapHoliday.put("2015/05/02", "");
        mapHoliday.put("2015/05/03", "");
        mapHoliday.put("2015/05/09", "");
        mapHoliday.put("2015/05/10", "");
        mapHoliday.put("2015/05/16", "");
        mapHoliday.put("2015/05/17", "");
        mapHoliday.put("2015/05/23", "");
        mapHoliday.put("2015/05/24", "");
        mapHoliday.put("2015/05/30", "");
        mapHoliday.put("2015/05/31", "");
        mapHoliday.put("2015/06/06", "");
        mapHoliday.put("2015/06/07", "");
        mapHoliday.put("2015/06/13", "");
        mapHoliday.put("2015/06/14", "");
        mapHoliday.put("2015/06/20", "");
        mapHoliday.put("2015/06/21", "");
        mapHoliday.put("2015/06/22", "");
        mapHoliday.put("2015/06/27", "");
        mapHoliday.put("2015/06/28", "");
        mapHoliday.put("2015/07/04", "");
        mapHoliday.put("2015/07/05", "");
        mapHoliday.put("2015/07/11", "");
        mapHoliday.put("2015/07/12", "");
        mapHoliday.put("2015/07/18", "");
        mapHoliday.put("2015/07/19", "");
        mapHoliday.put("2015/07/25", "");
        mapHoliday.put("2015/07/26", "");
        mapHoliday.put("2015/08/01", "");
        mapHoliday.put("2015/08/02", "");
        mapHoliday.put("2015/08/08", "");
        mapHoliday.put("2015/08/09", "");
        mapHoliday.put("2015/08/15", "");
        mapHoliday.put("2015/08/16", "");
        mapHoliday.put("2015/08/22", "");
        mapHoliday.put("2015/08/23", "");
        mapHoliday.put("2015/08/29", "");
        mapHoliday.put("2015/08/30", "");
        mapHoliday.put("2015/09/05", "");
        mapHoliday.put("2015/09/06", "");
        mapHoliday.put("2015/09/12", "");
        mapHoliday.put("2015/09/13", "");
        mapHoliday.put("2015/09/19", "");
        mapHoliday.put("2015/09/20", "");
        mapHoliday.put("2015/09/26", "");
        mapHoliday.put("2015/09/27", "");
        mapHoliday.put("2015/10/01", "");
        mapHoliday.put("2015/10/02", "");
        mapHoliday.put("2015/10/03", "");
        mapHoliday.put("2015/10/04", "");
        mapHoliday.put("2015/10/05", "");
        mapHoliday.put("2015/10/06", "");
        mapHoliday.put("2015/10/07", "");
        mapHoliday.put("2015/10/11", "");
        mapHoliday.put("2015/10/17", "");
        mapHoliday.put("2015/10/18", "");
        mapHoliday.put("2015/10/24", "");
        mapHoliday.put("2015/10/25", "");
        mapHoliday.put("2015/10/31", "");
        mapHoliday.put("2015/11/01", "");
        mapHoliday.put("2015/11/07", "");
        mapHoliday.put("2015/11/08", "");
        mapHoliday.put("2015/11/14", "");
        mapHoliday.put("2015/11/15", "");
        mapHoliday.put("2015/11/21", "");
        mapHoliday.put("2015/11/22", "");
        mapHoliday.put("2015/11/28", "");
        mapHoliday.put("2015/11/29", "");
        mapHoliday.put("2015/12/05", "");
        mapHoliday.put("2015/12/06", "");
        mapHoliday.put("2015/12/12", "");
        mapHoliday.put("2015/12/13", "");
        mapHoliday.put("2015/12/19", "");
        mapHoliday.put("2015/12/20", "");
        mapHoliday.put("2015/12/26", "");
        mapHoliday.put("2015/12/27", "");
        // 2014年假日mapHoliday.put("2014/01/01", "");
        mapHoliday.put("2014/01/04", "");
        mapHoliday.put("2014/01/05", "");
        mapHoliday.put("2014/01/11", "");
        mapHoliday.put("2014/01/12", "");
        mapHoliday.put("2014/01/18", "");
        mapHoliday.put("2014/01/19", "");
        mapHoliday.put("2014/01/25", "");
        mapHoliday.put("2014/01/26", "");
        mapHoliday.put("2014/01/31", "");
        mapHoliday.put("2014/02/01", "");
        mapHoliday.put("2014/02/02", "");
        mapHoliday.put("2014/02/03", "");
        mapHoliday.put("2014/02/04", "");
        mapHoliday.put("2014/02/05", "");
        mapHoliday.put("2014/02/06", "");
        mapHoliday.put("2014/02/09", "");
        mapHoliday.put("2014/02/15", "");
        mapHoliday.put("2014/02/16", "");
        mapHoliday.put("2014/02/22", "");
        mapHoliday.put("2014/02/23", "");
        mapHoliday.put("2014/03/01", "");
        mapHoliday.put("2014/03/02", "");
        mapHoliday.put("2014/03/08", "");
        mapHoliday.put("2014/03/09", "");
        mapHoliday.put("2014/03/15", "");
        mapHoliday.put("2014/03/16", "");
        mapHoliday.put("2014/03/22", "");
        mapHoliday.put("2014/03/23", "");
        mapHoliday.put("2014/03/29", "");
        mapHoliday.put("2014/03/30", "");
        mapHoliday.put("2014/04/05", "");
        mapHoliday.put("2014/04/06", "");
        mapHoliday.put("2014/04/07", "");
        mapHoliday.put("2014/04/12", "");
        mapHoliday.put("2014/04/13", "");
        mapHoliday.put("2014/04/19", "");
        mapHoliday.put("2014/04/20", "");
        mapHoliday.put("2014/04/26", "");
        mapHoliday.put("2014/04/27", "");
        mapHoliday.put("2014/05/01", "");
        mapHoliday.put("2014/05/02", "");
        mapHoliday.put("2014/05/03", "");
        mapHoliday.put("2014/05/10", "");
        mapHoliday.put("2014/05/11", "");
        mapHoliday.put("2014/05/17", "");
        mapHoliday.put("2014/05/18", "");
        mapHoliday.put("2014/05/24", "");
        mapHoliday.put("2014/05/25", "");
        mapHoliday.put("2014/05/31", "");
        mapHoliday.put("2014/06/01", "");
        mapHoliday.put("2014/06/02", "");
        mapHoliday.put("2014/06/07", "");
        mapHoliday.put("2014/06/08", "");
        mapHoliday.put("2014/06/14", "");
        mapHoliday.put("2014/06/15", "");
        mapHoliday.put("2014/06/21", "");
        mapHoliday.put("2014/06/22", "");
        mapHoliday.put("2014/06/28", "");
        mapHoliday.put("2014/06/29", "");
        mapHoliday.put("2014/07/05", "");
        mapHoliday.put("2014/07/06", "");
        mapHoliday.put("2014/07/12", "");
        mapHoliday.put("2014/07/13", "");
        mapHoliday.put("2014/07/19", "");
        mapHoliday.put("2014/07/20", "");
        mapHoliday.put("2014/07/26", "");
        mapHoliday.put("2014/07/27", "");
        mapHoliday.put("2014/08/02", "");
        mapHoliday.put("2014/08/03", "");
        mapHoliday.put("2014/08/09", "");
        mapHoliday.put("2014/08/10", "");
        mapHoliday.put("2014/08/16", "");
        mapHoliday.put("2014/08/17", "");
        mapHoliday.put("2014/08/23", "");
        mapHoliday.put("2014/08/24", "");
        mapHoliday.put("2014/08/30", "");
        mapHoliday.put("2014/08/31", "");
        mapHoliday.put("2014/09/06", "");
        mapHoliday.put("2014/09/07", "");
        mapHoliday.put("2014/09/08", "");
        mapHoliday.put("2014/09/13", "");
        mapHoliday.put("2014/09/14", "");
        mapHoliday.put("2014/09/20", "");
        mapHoliday.put("2014/09/21", "");
        mapHoliday.put("2014/09/27", "");
        mapHoliday.put("2014/09/28", "");
        mapHoliday.put("2014/10/01", "");
        mapHoliday.put("2014/10/02", "");
        mapHoliday.put("2014/10/03", "");
        mapHoliday.put("2014/10/04", "");
        mapHoliday.put("2014/10/05", "");
        mapHoliday.put("2014/10/06", "");
        mapHoliday.put("2014/10/07", "");
        mapHoliday.put("2014/10/12", "");
        mapHoliday.put("2014/10/18", "");
        mapHoliday.put("2014/10/19", "");
        mapHoliday.put("2014/10/25", "");
        mapHoliday.put("2014/10/26", "");
        mapHoliday.put("2014/11/01", "");
        mapHoliday.put("2014/11/02", "");
        mapHoliday.put("2014/11/08", "");
        mapHoliday.put("2014/11/09", "");
        mapHoliday.put("2014/11/15", "");
        mapHoliday.put("2014/11/16", "");
        mapHoliday.put("2014/11/22", "");
        mapHoliday.put("2014/11/23", "");
        mapHoliday.put("2014/11/29", "");
        mapHoliday.put("2014/11/30", "");
        mapHoliday.put("2014/12/06", "");
        mapHoliday.put("2014/12/07", "");
        mapHoliday.put("2014/12/13", "");
        mapHoliday.put("2014/12/14", "");
        mapHoliday.put("2014/12/20", "");
        mapHoliday.put("2014/12/21", "");
        mapHoliday.put("2014/12/27", "");
        mapHoliday.put("2014/12/28", "");
        // 2013年假日
        mapHoliday.put("2013/01/02", "");
        mapHoliday.put("2013/01/03", "");
        mapHoliday.put("2013/01/12", "");
        mapHoliday.put("2013/01/13", "");
        mapHoliday.put("2013/01/19", "");
        mapHoliday.put("2013/01/20", "");
        mapHoliday.put("2013/01/26", "");
        mapHoliday.put("2013/01/27", "");
        mapHoliday.put("2013/02/02", "");
        mapHoliday.put("2013/02/03", "");
        mapHoliday.put("2013/02/09", "");
        mapHoliday.put("2013/02/10", "");
        mapHoliday.put("2013/02/11", "");
        mapHoliday.put("2013/02/12", "");
        mapHoliday.put("2013/02/13", "");
        mapHoliday.put("2013/02/14", "");
        mapHoliday.put("2013/02/15", "");
        mapHoliday.put("2013/02/23", "");
        mapHoliday.put("2013/02/24", "");
        mapHoliday.put("2013/03/02", "");
        mapHoliday.put("2013/03/03", "");
        mapHoliday.put("2013/03/09", "");
        mapHoliday.put("2013/03/10", "");
        mapHoliday.put("2013/03/16", "");
        mapHoliday.put("2013/03/17", "");
        mapHoliday.put("2013/03/23", "");
        mapHoliday.put("2013/03/24", "");
        mapHoliday.put("2013/03/30", "");
        mapHoliday.put("2013/03/31", "");
        mapHoliday.put("2013/04/04", "");
        mapHoliday.put("2013/04/05", "");
        mapHoliday.put("2013/04/06", "");
        mapHoliday.put("2013/04/13", "");
        mapHoliday.put("2013/04/14", "");
        mapHoliday.put("2013/04/20", "");
        mapHoliday.put("2013/04/21", "");
        mapHoliday.put("2013/04/29", "");
        mapHoliday.put("2013/04/30", "");
        mapHoliday.put("2013/05/01", "");
        mapHoliday.put("2013/05/04", "");
        mapHoliday.put("2013/05/05", "");
        mapHoliday.put("2013/05/11", "");
        mapHoliday.put("2013/05/12", "");
        mapHoliday.put("2013/05/18", "");
        mapHoliday.put("2013/05/19", "");
        mapHoliday.put("2013/05/25", "");
        mapHoliday.put("2013/05/26", "");
        mapHoliday.put("2013/06/01", "");
        mapHoliday.put("2013/06/02", "");
        mapHoliday.put("2013/06/10", "");
        mapHoliday.put("2013/06/11", "");
        mapHoliday.put("2013/06/12", "");
        mapHoliday.put("2013/06/15", "");
        mapHoliday.put("2013/06/16", "");
        mapHoliday.put("2013/06/22", "");
        mapHoliday.put("2013/06/23", "");
        mapHoliday.put("2013/06/29", "");
        mapHoliday.put("2013/06/30", "");
        mapHoliday.put("2013/07/06", "");
        mapHoliday.put("2013/07/07", "");
        mapHoliday.put("2013/07/13", "");
        mapHoliday.put("2013/07/14", "");
        mapHoliday.put("2013/07/20", "");
        mapHoliday.put("2013/07/21", "");
        mapHoliday.put("2013/07/27", "");
        mapHoliday.put("2013/07/28", "");
        mapHoliday.put("2013/08/03", "");
        mapHoliday.put("2013/08/04", "");
        mapHoliday.put("2013/08/10", "");
        mapHoliday.put("2013/08/11", "");
        mapHoliday.put("2013/08/17", "");
        mapHoliday.put("2013/08/18", "");
        mapHoliday.put("2013/08/24", "");
        mapHoliday.put("2013/08/25", "");
        mapHoliday.put("2013/08/31", "");
        mapHoliday.put("2013/09/01", "");
        mapHoliday.put("2013/09/07", "");
        mapHoliday.put("2013/09/08", "");
        mapHoliday.put("2013/09/14", "");
        mapHoliday.put("2013/09/15", "");
        mapHoliday.put("2013/09/19", "");
        mapHoliday.put("2013/09/20", "");
        mapHoliday.put("2013/09/21", "");
        mapHoliday.put("2013/09/28", "");
        mapHoliday.put("2013/10/01", "");
        mapHoliday.put("2013/10/02", "");
        mapHoliday.put("2013/10/03", "");
        mapHoliday.put("2013/10/04", "");
        mapHoliday.put("2013/10/05", "");
        mapHoliday.put("2013/10/06", "");
        mapHoliday.put("2013/10/07", "");
        mapHoliday.put("2013/10/13", "");
        mapHoliday.put("2013/10/19", "");
        mapHoliday.put("2013/10/20", "");
        mapHoliday.put("2013/10/26", "");
        mapHoliday.put("2013/10/27", "");
        mapHoliday.put("2013/11/02", "");
        mapHoliday.put("2013/11/03", "");
        mapHoliday.put("2013/11/09", "");
        mapHoliday.put("2013/11/10", "");
        mapHoliday.put("2013/11/16", "");
        mapHoliday.put("2013/11/17", "");
        mapHoliday.put("2013/11/23", "");
        mapHoliday.put("2013/11/24", "");
        mapHoliday.put("2013/11/30", "");
        mapHoliday.put("2013/12/01", "");
        mapHoliday.put("2013/12/07", "");
        mapHoliday.put("2013/12/08", "");
        mapHoliday.put("2013/12/14", "");
        mapHoliday.put("2013/12/15", "");
        mapHoliday.put("2013/12/21", "");
        mapHoliday.put("2013/12/22", "");
        mapHoliday.put("2013/12/28", "");
        mapHoliday.put("2013/12/29", "");
        // 2012年假日
        mapHoliday.put("2012/01/02", "");
        mapHoliday.put("2012/01/03", "");
        mapHoliday.put("2012/01/07", "");
        mapHoliday.put("2012/01/08", "");
        mapHoliday.put("2012/01/14", "");
        mapHoliday.put("2012/01/15", "");
        mapHoliday.put("2012/01/22", "");
        mapHoliday.put("2012/01/23", "");
        mapHoliday.put("2012/01/24", "");
        mapHoliday.put("2012/01/25", "");
        mapHoliday.put("2012/01/26", "");
        mapHoliday.put("2012/01/27", "");
        mapHoliday.put("2012/01/28", "");
        mapHoliday.put("2012/02/04", "");
        mapHoliday.put("2012/02/05", "");
        mapHoliday.put("2012/02/11", "");
        mapHoliday.put("2012/02/12", "");
        mapHoliday.put("2012/02/18", "");
        mapHoliday.put("2012/02/19", "");
        mapHoliday.put("2012/02/25", "");
        mapHoliday.put("2012/02/26", "");
        mapHoliday.put("2012/03/03", "");
        mapHoliday.put("2012/03/04", "");
        mapHoliday.put("2012/03/10", "");
        mapHoliday.put("2012/03/11", "");
        mapHoliday.put("2012/03/17", "");
        mapHoliday.put("2012/03/18", "");
        mapHoliday.put("2012/03/24", "");
        mapHoliday.put("2012/03/25", "");
        mapHoliday.put("2012/04/02", "");
        mapHoliday.put("2012/04/03", "");
        mapHoliday.put("2012/04/04", "");
        mapHoliday.put("2012/04/07", "");
        mapHoliday.put("2012/04/08", "");
        mapHoliday.put("2012/04/14", "");
        mapHoliday.put("2012/04/15", "");
        mapHoliday.put("2012/04/21", "");
        mapHoliday.put("2012/04/22", "");
        mapHoliday.put("2012/04/29", "");
        mapHoliday.put("2012/04/30", "");
        mapHoliday.put("2012/05/01", "");
        mapHoliday.put("2012/05/05", "");
        mapHoliday.put("2012/05/06", "");
        mapHoliday.put("2012/05/12", "");
        mapHoliday.put("2012/05/13", "");
        mapHoliday.put("2012/05/19", "");
        mapHoliday.put("2012/05/20", "");
        mapHoliday.put("2012/05/26", "");
        mapHoliday.put("2012/05/27", "");
        mapHoliday.put("2012/06/02", "");
        mapHoliday.put("2012/06/03", "");
        mapHoliday.put("2012/06/09", "");
        mapHoliday.put("2012/06/10", "");
        mapHoliday.put("2012/06/16", "");
        mapHoliday.put("2012/06/17", "");
        mapHoliday.put("2012/06/22", "");
        mapHoliday.put("2012/06/23", "");
        mapHoliday.put("2012/06/24", "");
        mapHoliday.put("2012/06/30", "");
        mapHoliday.put("2012/07/01", "");
        mapHoliday.put("2012/07/07", "");
        mapHoliday.put("2012/07/08", "");
        mapHoliday.put("2012/07/14", "");
        mapHoliday.put("2012/07/15", "");
        mapHoliday.put("2012/07/21", "");
        mapHoliday.put("2012/07/22", "");
        mapHoliday.put("2012/07/28", "");
        mapHoliday.put("2012/07/29", "");
        mapHoliday.put("2012/08/04", "");
        mapHoliday.put("2012/08/05", "");
        mapHoliday.put("2012/08/11", "");
        mapHoliday.put("2012/08/12", "");
        mapHoliday.put("2012/08/18", "");
        mapHoliday.put("2012/08/19", "");
        mapHoliday.put("2012/08/25", "");
        mapHoliday.put("2012/08/26", "");
        mapHoliday.put("2012/09/01", "");
        mapHoliday.put("2012/09/02", "");
        mapHoliday.put("2012/09/08", "");
        mapHoliday.put("2012/09/09", "");
        mapHoliday.put("2012/09/15", "");
        mapHoliday.put("2012/09/16", "");
        mapHoliday.put("2012/09/22", "");
        mapHoliday.put("2012/09/23", "");
        mapHoliday.put("2012/09/30", "");
        mapHoliday.put("2012/10/01", "");
        mapHoliday.put("2012/10/02", "");
        mapHoliday.put("2012/10/03", "");
        mapHoliday.put("2012/10/04", "");
        mapHoliday.put("2012/10/05", "");
        mapHoliday.put("2012/10/06", "");
        mapHoliday.put("2012/10/07", "");
        mapHoliday.put("2012/10/13", "");
        mapHoliday.put("2012/10/14", "");
        mapHoliday.put("2012/10/20", "");
        mapHoliday.put("2012/10/21", "");
        mapHoliday.put("2012/10/27", "");
        mapHoliday.put("2012/10/28", "");
        mapHoliday.put("2012/11/03", "");
        mapHoliday.put("2012/11/04", "");
        mapHoliday.put("2012/11/10", "");
        mapHoliday.put("2012/11/11", "");
        mapHoliday.put("2012/11/17", "");
        mapHoliday.put("2012/11/18", "");
        mapHoliday.put("2012/11/24", "");
        mapHoliday.put("2012/11/25", "");
        mapHoliday.put("2012/12/01", "");
        mapHoliday.put("2012/12/02", "");
        mapHoliday.put("2012/12/08", "");
        mapHoliday.put("2012/12/09", "");
        mapHoliday.put("2012/12/15", "");
        mapHoliday.put("2012/12/16", "");
        mapHoliday.put("2012/12/22", "");
        mapHoliday.put("2012/12/23", "");
        mapHoliday.put("2012/12/29", "");
        mapHoliday.put("2012/12/30", "");
    }
}