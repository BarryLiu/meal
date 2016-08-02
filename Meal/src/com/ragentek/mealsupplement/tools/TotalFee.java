/**
 *
 */
package com.ragentek.mealsupplement.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TFees;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author kui.li
 *         普通人员补助：
 *         保洁补助：27元/天，非工作日普通员工算，涂茂银，李忠霞，欧连凤
 *         司机补助：商务车司机 33元/天
 *         保安补助：27元/天，
 */
public class TotalFee {

    private static Map<String, String> mapHoliday = new HashMap<String, String>();

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
    public TotalFee() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     * @throws
     */
    public static void main(String[] args) {
        //rootPath = "D://kaoqin";//System.getProperty("user.dir");//TotalFee.class.getClass().getResource("/").getPath();

        System.out.println("当前路径：" + rootPath);
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
                    System.out.println("开始统计：" + item.getName());
//                    try {
//                        getData(item, 0);
//                    } catch (Exception e) {
//                        System.out.println("    " + item.getName() + "文件有异常");
//                    }
                }
            }

            // 迭代结果，并输出到excel中
            try {
                wirteFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void saveData(File file, int ignoreRows)

            throws FileNotFoundException, IOException {

        List<String[]> result = new ArrayList<String[]>();

        int rowSize = 0;

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(

                file));

        // 打开HSSFWorkbook
        POIFSFileSystem fs = new POIFSFileSystem(in);

        HSSFWorkbook wb = new HSSFWorkbook(fs);

        HSSFCell cell = null;

        int codeIndex = 0, nameIndex = 0, dayIndex = 0, beginTimeIndex = 0, endTimeIndex = 0;
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {

            HSSFSheet st = wb.getSheetAt(sheetIndex);
            // 第一行为标题，不取
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {

                HSSFRow row = st.getRow(rowIndex);

                if (row == null) {

                    continue;

                }

                int tempRowSize = row.getLastCellNum() + 1;

                if (tempRowSize > rowSize) {

                    rowSize = tempRowSize;

                }

                String[] values = new String[rowSize];

                Arrays.fill(values, "");

                boolean hasValue = false;

                String code = "", name = "", day = "", beginTime = "", endTime = "";
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
                    if (rowIndex == 0) {
                        if ("考勤号码".equals(tmpValues.trim())) {  // 工号
                            codeIndex = columnIndex;
                            System.out.println("WPF_MEAL:  工号 index: "+columnIndex);
                        } else if ("姓名".equals(tmpValues.trim())) {  // 姓名
                            nameIndex = columnIndex;
                            System.out.println("WPF_MEAL:  姓名 index: "+columnIndex);
                        } else if ("日期时间".equals(tmpValues.trim())) {  // 日期
                            dayIndex = columnIndex;
                            System.out.println("WPF_MEAL:  日期 index: "+columnIndex);
                        } else if (tmpValues.startsWith("上班时间")) {  // 上班时间
                            beginTimeIndex = columnIndex;
                        } else if (tmpValues.startsWith("下班时间")) {  // 下班时间
                            endTimeIndex = columnIndex;
                        }
                        continue;
                    }

                    if (columnIndex == codeIndex) {// 工号
                        code = tmpValues;

                    } else if (columnIndex == nameIndex) // 姓名
                        name = tmpValues;
                    else if (columnIndex == dayIndex) // 日期
                        day = tmpValues;
                    else if (columnIndex == beginTimeIndex) // 上班时间
                        beginTime = tmpValues;
                    else if (columnIndex == endTimeIndex) { // 下玉时间
                        endTime = tmpValues;
                        // 开始计算用户时间
                        countFee(code, name, day, beginTime, endTime);
                        code = "";
                        name = "";
                        day = "";
                        beginTime = "";
                        endTime = "";
                    }

                    hasValue = true;
                }


                if (hasValue) {

                    result.add(values);

                }

            }

        }


        codeIndex = 0;
        nameIndex = 0;
        dayIndex = 0;
        beginTimeIndex = 0;
        endTimeIndex = 0;

        in.close();
    }

    private static void countFee(String code, String name, String day, String beginTime,
                                 String endTime) {
        //if(!"计锋英".equals(name))
        //if(!name.contains("计锋英"))
        //	return;
        //System.out.println( "code:" + code + ",name:" + name + ",day=" + day + ",begintTime:" + beginTime + ",endTime" + endTime);
        try {
            day = day.substring(0, 10);
            String userInfo = code + "-" + name;
            long gTime = Long.valueOf(beginTime.replaceAll(":", ""));
            long eTime = Long.valueOf(endTime.replaceAll(":", ""));
            if (eTime < 90000)
                eTime += 240000;

            int fee = 0;
            TFees fees = new TFees();
            fees.setUserInfo(userInfo);
            fees.setDayStr(day);
            fees.setStartTime(beginTime);
            fees.setEndTime(endTime);
            if (gTime <= 92000 && eTime > 92000) { // 早餐
                fee += 3;
                fees.setFee1(3);
            } else {
                fees.setFee1(0);
            }
            if (gTime < 130000 && eTime >= 120000) { // 午餐
                // 判定是 否为双休日，若为又休日，则标准变为15元
                if (checkWetherHoliday(day)) { // 非工作日
                    fee += 15;
                    fees.setFee2(15);
                } else { // 工作日
                    fee += 12;
                    fees.setFee2(12);
                }
            } else {
                fees.setFee2(0);
            }
            if (gTime < 200000 && eTime >= 200000) { // 晚餐
                fee += 15;
                fees.setFee3(15);
            } else {
                fees.setFee3(0);
            }
            // 从2014-12-08起，晚餐时间调整为晚上9：50起补助
            if(compareDataStr(day,"2014-12-08") >= 0) {
                if (gTime < 215059 && eTime >= 215059) { // 夜宵
                    fee += 15;
                    fees.setFee4(15);
                } else {
                    fees.setFee4(0);
                }
            } else {
                if (gTime < 220000 && eTime >= 220000) { // 夜宵
                    fee += 15;
                    fees.setFee4(15);
                } else {
                    fees.setFee4(0);
                }
            }
            if (gTime < 240000 && eTime >= 240000) { // 午夜
                fee += 15;
                fees.setFee5(15);
            } else {
                fees.setFee5(0);
            }

            // check数据是否存在，若存在则更新，否则插入新数据
            String sql = "SELECT * FROM t_fees WHERE user_info=? AND day_str=?";
            List<TFees> lstFees = DBUtils.query(sql, new String[]{userInfo, day}, TFees.class);
            if (lstFees != null && lstFees.size() > 0) { // 更新
                fees.setId(lstFees.get(0).getId());
                System.out.println("update = Fees:" + fees.toString());
                long rtn = DBUtils.update(fees);
                System.out.println("update rtn=" + rtn + " Fees:" + fees.toString());
            } else { // 插入
                long rtn = DBUtils.insert(fees);
                System.out.println("insert = rtn=" + rtn + " Fees:" + fees.toString());
            }
        } catch (Exception e) {
            //System.out.println(code + "-" + name + "数据异常");
            //e.printStackTrace();
        }
    }

    /**
     *
     * @param source
     * @param target
     * @return  -1:前者小，0:相等, 1:前者大
     */
    private static int compareDataStr(String source, String target) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

    /**
     * 判定此天是否为非工作日
     *
     * @param day
     * @return
     * @throws ParseException
     */
    private static boolean checkWetherHoliday(String day) throws ParseException {
        if (mapHoliday.containsKey(day))
            return true;
        return false;
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

    private static void wirteFile() throws IOException {
        //工作簿
        HSSFWorkbook hssfworkbook = new HSSFWorkbook();
        //创建sheet页
        HSSFSheet hssfsheet = hssfworkbook.createSheet();
        //sheet名称乱码处理
        hssfworkbook.setSheetName(0, "统计结果");

        // 添加第一行
        addRow(hssfsheet, 0, "工号-姓名", "补助总额（单位：元）");

        // 添加余下的所有数据
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            num++;
            String key = iterator.next();
            addRow(hssfsheet, num, key, map.get(key));
        }

        //输出
        FileOutputStream fileoutputstream = new FileOutputStream(rootPath + "//result.xls");
        hssfworkbook.write(fileoutputstream);
        fileoutputstream.close();
        System.out.println("处理完成，结果已输出到：result.xls");
    }

    private static void addRow(HSSFSheet hssfsheet, int rowNum, String key, String values) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short) 0);
        hssfcell0.setCellValue(key);
        HSSFCell hssfcell1 = hssfrow.createCell((short) 1);
        hssfcell1.setCellValue(values);
    }

    private static void addRow(HSSFSheet hssfsheet, int rowNum, String key, Integer values) {
        HSSFRow hssfrow = hssfsheet.createRow(rowNum);
        HSSFCell hssfcell0 = hssfrow.createCell((short) 0);
        hssfcell0.setCellValue(key);
        HSSFCell hssfcell1 = hssfrow.createCell((short) 1);
        hssfcell1.setCellValue(values);
    }

    static {
        // 2015年假日
        mapHoliday.put("2015-01-01", "");
        mapHoliday.put("2015-01-02", "");
        mapHoliday.put("2015-01-03", "");
        mapHoliday.put("2015-01-10", "");
        mapHoliday.put("2015-01-11", "");
        mapHoliday.put("2015-01-17", "");
        mapHoliday.put("2015-01-18", "");
        mapHoliday.put("2015-01-24", "");
        mapHoliday.put("2015-01-25", "");
        mapHoliday.put("2015-01-31", "");
        mapHoliday.put("2015-02-01", "");
        mapHoliday.put("2015-02-07", "");
        mapHoliday.put("2015-02-08", "");
        mapHoliday.put("2015-02-16", "");
        mapHoliday.put("2015-02-17", "");
        mapHoliday.put("2015-02-18", "");
        mapHoliday.put("2015-02-19", "");
        mapHoliday.put("2015-02-20", "");
        mapHoliday.put("2015-02-21", "");
        mapHoliday.put("2015-02-22", "");
        mapHoliday.put("2015-02-23", "");
        mapHoliday.put("2015-02-24", "");
        mapHoliday.put("2015-03-01", "");
        mapHoliday.put("2015-03-07", "");
        mapHoliday.put("2015-03-08", "");
        mapHoliday.put("2015-03-14", "");
        mapHoliday.put("2015-03-15", "");
        mapHoliday.put("2015-03-21", "");
        mapHoliday.put("2015-03-22", "");
        mapHoliday.put("2015-03-28", "");
        mapHoliday.put("2015-03-29", "");
        mapHoliday.put("2015-04-04", "");
        mapHoliday.put("2015-04-05", "");
        mapHoliday.put("2015-04-06", "");
        mapHoliday.put("2015-04-11", "");
        mapHoliday.put("2015-04-12", "");
        mapHoliday.put("2015-04-18", "");
        mapHoliday.put("2015-04-19", "");
        mapHoliday.put("2015-04-25", "");
        mapHoliday.put("2015-04-26", "");
        mapHoliday.put("2015-05-01", "");
        mapHoliday.put("2015-05-02", "");
        mapHoliday.put("2015-05-03", "");
        mapHoliday.put("2015-05-09", "");
        mapHoliday.put("2015-05-10", "");
        mapHoliday.put("2015-05-16", "");
        mapHoliday.put("2015-05-17", "");
        mapHoliday.put("2015-05-23", "");
        mapHoliday.put("2015-05-24", "");
        mapHoliday.put("2015-05-30", "");
        mapHoliday.put("2015-05-31", "");
        mapHoliday.put("2015-06-06", "");
        mapHoliday.put("2015-06-07", "");
        mapHoliday.put("2015-06-13", "");
        mapHoliday.put("2015-06-14", "");
        mapHoliday.put("2015-06-20", "");
        mapHoliday.put("2015-06-21", "");
        mapHoliday.put("2015-06-22", "");
        mapHoliday.put("2015-06-27", "");
        mapHoliday.put("2015-06-28", "");
        mapHoliday.put("2015-07-04", "");
        mapHoliday.put("2015-07-05", "");
        mapHoliday.put("2015-07-11", "");
        mapHoliday.put("2015-07-12", "");
        mapHoliday.put("2015-07-18", "");
        mapHoliday.put("2015-07-19", "");
        mapHoliday.put("2015-07-25", "");
        mapHoliday.put("2015-07-26", "");
        mapHoliday.put("2015-08-01", "");
        mapHoliday.put("2015-08-02", "");
        mapHoliday.put("2015-08-08", "");
        mapHoliday.put("2015-08-09", "");
        mapHoliday.put("2015-08-15", "");
        mapHoliday.put("2015-08-16", "");
        mapHoliday.put("2015-08-22", "");
        mapHoliday.put("2015-08-23", "");
        mapHoliday.put("2015-08-29", "");
        mapHoliday.put("2015-08-30", "");
        mapHoliday.put("2015-09-05", "");
        mapHoliday.put("2015-09-06", "");
        mapHoliday.put("2015-09-12", "");
        mapHoliday.put("2015-09-13", "");
        mapHoliday.put("2015-09-19", "");
        mapHoliday.put("2015-09-20", "");
        mapHoliday.put("2015-09-26", "");
        mapHoliday.put("2015-09-27", "");
        mapHoliday.put("2015-10-01", "");
        mapHoliday.put("2015-10-02", "");
        mapHoliday.put("2015-10-03", "");
        mapHoliday.put("2015-10-04", "");
        mapHoliday.put("2015-10-05", "");
        mapHoliday.put("2015-10-06", "");
        mapHoliday.put("2015-10-07", "");
        mapHoliday.put("2015-10-11", "");
        mapHoliday.put("2015-10-17", "");
        mapHoliday.put("2015-10-18", "");
        mapHoliday.put("2015-10-24", "");
        mapHoliday.put("2015-10-25", "");
        mapHoliday.put("2015-10-31", "");
        mapHoliday.put("2015-11-01", "");
        mapHoliday.put("2015-11-07", "");
        mapHoliday.put("2015-11-08", "");
        mapHoliday.put("2015-11-14", "");
        mapHoliday.put("2015-11-15", "");
        mapHoliday.put("2015-11-21", "");
        mapHoliday.put("2015-11-22", "");
        mapHoliday.put("2015-11-28", "");
        mapHoliday.put("2015-11-29", "");
        mapHoliday.put("2015-12-05", "");
        mapHoliday.put("2015-12-06", "");
        mapHoliday.put("2015-12-12", "");
        mapHoliday.put("2015-12-13", "");
        mapHoliday.put("2015-12-19", "");
        mapHoliday.put("2015-12-20", "");
        mapHoliday.put("2015-12-26", "");
        mapHoliday.put("2015-12-27", "");
        // 2014年假日mapHoliday.put("2014-01-01", "");
        mapHoliday.put("2014-01-04", "");
        mapHoliday.put("2014-01-05", "");
        mapHoliday.put("2014-01-11", "");
        mapHoliday.put("2014-01-12", "");
        mapHoliday.put("2014-01-18", "");
        mapHoliday.put("2014-01-19", "");
        mapHoliday.put("2014-01-25", "");
        mapHoliday.put("2014-01-26", "");
        mapHoliday.put("2014-01-31", "");
        mapHoliday.put("2014-02-01", "");
        mapHoliday.put("2014-02-02", "");
        mapHoliday.put("2014-02-03", "");
        mapHoliday.put("2014-02-04", "");
        mapHoliday.put("2014-02-05", "");
        mapHoliday.put("2014-02-06", "");
        mapHoliday.put("2014-02-09", "");
        mapHoliday.put("2014-02-15", "");
        mapHoliday.put("2014-02-16", "");
        mapHoliday.put("2014-02-22", "");
        mapHoliday.put("2014-02-23", "");
        mapHoliday.put("2014-03-01", "");
        mapHoliday.put("2014-03-02", "");
        mapHoliday.put("2014-03-08", "");
        mapHoliday.put("2014-03-09", "");
        mapHoliday.put("2014-03-15", "");
        mapHoliday.put("2014-03-16", "");
        mapHoliday.put("2014-03-22", "");
        mapHoliday.put("2014-03-23", "");
        mapHoliday.put("2014-03-29", "");
        mapHoliday.put("2014-03-30", "");
        mapHoliday.put("2014-04-05", "");
        mapHoliday.put("2014-04-06", "");
        mapHoliday.put("2014-04-07", "");
        mapHoliday.put("2014-04-12", "");
        mapHoliday.put("2014-04-13", "");
        mapHoliday.put("2014-04-19", "");
        mapHoliday.put("2014-04-20", "");
        mapHoliday.put("2014-04-26", "");
        mapHoliday.put("2014-04-27", "");
        mapHoliday.put("2014-05-01", "");
        mapHoliday.put("2014-05-02", "");
        mapHoliday.put("2014-05-03", "");
        mapHoliday.put("2014-05-10", "");
        mapHoliday.put("2014-05-11", "");
        mapHoliday.put("2014-05-17", "");
        mapHoliday.put("2014-05-18", "");
        mapHoliday.put("2014-05-24", "");
        mapHoliday.put("2014-05-25", "");
        mapHoliday.put("2014-05-31", "");
        mapHoliday.put("2014-06-01", "");
        mapHoliday.put("2014-06-02", "");
        mapHoliday.put("2014-06-07", "");
        mapHoliday.put("2014-06-08", "");
        mapHoliday.put("2014-06-14", "");
        mapHoliday.put("2014-06-15", "");
        mapHoliday.put("2014-06-21", "");
        mapHoliday.put("2014-06-22", "");
        mapHoliday.put("2014-06-28", "");
        mapHoliday.put("2014-06-29", "");
        mapHoliday.put("2014-07-05", "");
        mapHoliday.put("2014-07-06", "");
        mapHoliday.put("2014-07-12", "");
        mapHoliday.put("2014-07-13", "");
        mapHoliday.put("2014-07-19", "");
        mapHoliday.put("2014-07-20", "");
        mapHoliday.put("2014-07-26", "");
        mapHoliday.put("2014-07-27", "");
        mapHoliday.put("2014-08-02", "");
        mapHoliday.put("2014-08-03", "");
        mapHoliday.put("2014-08-09", "");
        mapHoliday.put("2014-08-10", "");
        mapHoliday.put("2014-08-16", "");
        mapHoliday.put("2014-08-17", "");
        mapHoliday.put("2014-08-23", "");
        mapHoliday.put("2014-08-24", "");
        mapHoliday.put("2014-08-30", "");
        mapHoliday.put("2014-08-31", "");
        mapHoliday.put("2014-09-06", "");
        mapHoliday.put("2014-09-07", "");
        mapHoliday.put("2014-09-08", "");
        mapHoliday.put("2014-09-13", "");
        mapHoliday.put("2014-09-14", "");
        mapHoliday.put("2014-09-20", "");
        mapHoliday.put("2014-09-21", "");
        mapHoliday.put("2014-09-27", "");
        mapHoliday.put("2014-09-28", "");
        mapHoliday.put("2014-10-01", "");
        mapHoliday.put("2014-10-02", "");
        mapHoliday.put("2014-10-03", "");
        mapHoliday.put("2014-10-04", "");
        mapHoliday.put("2014-10-05", "");
        mapHoliday.put("2014-10-06", "");
        mapHoliday.put("2014-10-07", "");
        mapHoliday.put("2014-10-12", "");
        mapHoliday.put("2014-10-18", "");
        mapHoliday.put("2014-10-19", "");
        mapHoliday.put("2014-10-25", "");
        mapHoliday.put("2014-10-26", "");
        mapHoliday.put("2014-11-01", "");
        mapHoliday.put("2014-11-02", "");
        mapHoliday.put("2014-11-08", "");
        mapHoliday.put("2014-11-09", "");
        mapHoliday.put("2014-11-15", "");
        mapHoliday.put("2014-11-16", "");
        mapHoliday.put("2014-11-22", "");
        mapHoliday.put("2014-11-23", "");
        mapHoliday.put("2014-11-29", "");
        mapHoliday.put("2014-11-30", "");
        mapHoliday.put("2014-12-06", "");
        mapHoliday.put("2014-12-07", "");
        mapHoliday.put("2014-12-13", "");
        mapHoliday.put("2014-12-14", "");
        mapHoliday.put("2014-12-20", "");
        mapHoliday.put("2014-12-21", "");
        mapHoliday.put("2014-12-27", "");
        mapHoliday.put("2014-12-28", "");
        // 2013年假日
        mapHoliday.put("2013-01-02", "");
        mapHoliday.put("2013-01-03", "");
        mapHoliday.put("2013-01-12", "");
        mapHoliday.put("2013-01-13", "");
        mapHoliday.put("2013-01-19", "");
        mapHoliday.put("2013-01-20", "");
        mapHoliday.put("2013-01-26", "");
        mapHoliday.put("2013-01-27", "");
        mapHoliday.put("2013-02-02", "");
        mapHoliday.put("2013-02-03", "");
        mapHoliday.put("2013-02-09", "");
        mapHoliday.put("2013-02-10", "");
        mapHoliday.put("2013-02-11", "");
        mapHoliday.put("2013-02-12", "");
        mapHoliday.put("2013-02-13", "");
        mapHoliday.put("2013-02-14", "");
        mapHoliday.put("2013-02-15", "");
        mapHoliday.put("2013-02-23", "");
        mapHoliday.put("2013-02-24", "");
        mapHoliday.put("2013-03-02", "");
        mapHoliday.put("2013-03-03", "");
        mapHoliday.put("2013-03-09", "");
        mapHoliday.put("2013-03-10", "");
        mapHoliday.put("2013-03-16", "");
        mapHoliday.put("2013-03-17", "");
        mapHoliday.put("2013-03-23", "");
        mapHoliday.put("2013-03-24", "");
        mapHoliday.put("2013-03-30", "");
        mapHoliday.put("2013-03-31", "");
        mapHoliday.put("2013-04-04", "");
        mapHoliday.put("2013-04-05", "");
        mapHoliday.put("2013-04-06", "");
        mapHoliday.put("2013-04-13", "");
        mapHoliday.put("2013-04-14", "");
        mapHoliday.put("2013-04-20", "");
        mapHoliday.put("2013-04-21", "");
        mapHoliday.put("2013-04-29", "");
        mapHoliday.put("2013-04-30", "");
        mapHoliday.put("2013-05-01", "");
        mapHoliday.put("2013-05-04", "");
        mapHoliday.put("2013-05-05", "");
        mapHoliday.put("2013-05-11", "");
        mapHoliday.put("2013-05-12", "");
        mapHoliday.put("2013-05-18", "");
        mapHoliday.put("2013-05-19", "");
        mapHoliday.put("2013-05-25", "");
        mapHoliday.put("2013-05-26", "");
        mapHoliday.put("2013-06-01", "");
        mapHoliday.put("2013-06-02", "");
        mapHoliday.put("2013-06-10", "");
        mapHoliday.put("2013-06-11", "");
        mapHoliday.put("2013-06-12", "");
        mapHoliday.put("2013-06-15", "");
        mapHoliday.put("2013-06-16", "");
        mapHoliday.put("2013-06-22", "");
        mapHoliday.put("2013-06-23", "");
        mapHoliday.put("2013-06-29", "");
        mapHoliday.put("2013-06-30", "");
        mapHoliday.put("2013-07-06", "");
        mapHoliday.put("2013-07-07", "");
        mapHoliday.put("2013-07-13", "");
        mapHoliday.put("2013-07-14", "");
        mapHoliday.put("2013-07-20", "");
        mapHoliday.put("2013-07-21", "");
        mapHoliday.put("2013-07-27", "");
        mapHoliday.put("2013-07-28", "");
        mapHoliday.put("2013-08-03", "");
        mapHoliday.put("2013-08-04", "");
        mapHoliday.put("2013-08-10", "");
        mapHoliday.put("2013-08-11", "");
        mapHoliday.put("2013-08-17", "");
        mapHoliday.put("2013-08-18", "");
        mapHoliday.put("2013-08-24", "");
        mapHoliday.put("2013-08-25", "");
        mapHoliday.put("2013-08-31", "");
        mapHoliday.put("2013-09-01", "");
        mapHoliday.put("2013-09-07", "");
        mapHoliday.put("2013-09-08", "");
        mapHoliday.put("2013-09-14", "");
        mapHoliday.put("2013-09-15", "");
        mapHoliday.put("2013-09-19", "");
        mapHoliday.put("2013-09-20", "");
        mapHoliday.put("2013-09-21", "");
        mapHoliday.put("2013-09-28", "");
        mapHoliday.put("2013-10-01", "");
        mapHoliday.put("2013-10-02", "");
        mapHoliday.put("2013-10-03", "");
        mapHoliday.put("2013-10-04", "");
        mapHoliday.put("2013-10-05", "");
        mapHoliday.put("2013-10-06", "");
        mapHoliday.put("2013-10-07", "");
        mapHoliday.put("2013-10-13", "");
        mapHoliday.put("2013-10-19", "");
        mapHoliday.put("2013-10-20", "");
        mapHoliday.put("2013-10-26", "");
        mapHoliday.put("2013-10-27", "");
        mapHoliday.put("2013-11-02", "");
        mapHoliday.put("2013-11-03", "");
        mapHoliday.put("2013-11-09", "");
        mapHoliday.put("2013-11-10", "");
        mapHoliday.put("2013-11-16", "");
        mapHoliday.put("2013-11-17", "");
        mapHoliday.put("2013-11-23", "");
        mapHoliday.put("2013-11-24", "");
        mapHoliday.put("2013-11-30", "");
        mapHoliday.put("2013-12-01", "");
        mapHoliday.put("2013-12-07", "");
        mapHoliday.put("2013-12-08", "");
        mapHoliday.put("2013-12-14", "");
        mapHoliday.put("2013-12-15", "");
        mapHoliday.put("2013-12-21", "");
        mapHoliday.put("2013-12-22", "");
        mapHoliday.put("2013-12-28", "");
        mapHoliday.put("2013-12-29", "");
        // 2012年假日
        mapHoliday.put("2012-01-02", "");
        mapHoliday.put("2012-01-03", "");
        mapHoliday.put("2012-01-07", "");
        mapHoliday.put("2012-01-08", "");
        mapHoliday.put("2012-01-14", "");
        mapHoliday.put("2012-01-15", "");
        mapHoliday.put("2012-01-22", "");
        mapHoliday.put("2012-01-23", "");
        mapHoliday.put("2012-01-24", "");
        mapHoliday.put("2012-01-25", "");
        mapHoliday.put("2012-01-26", "");
        mapHoliday.put("2012-01-27", "");
        mapHoliday.put("2012-01-28", "");
        mapHoliday.put("2012-02-04", "");
        mapHoliday.put("2012-02-05", "");
        mapHoliday.put("2012-02-11", "");
        mapHoliday.put("2012-02-12", "");
        mapHoliday.put("2012-02-18", "");
        mapHoliday.put("2012-02-19", "");
        mapHoliday.put("2012-02-25", "");
        mapHoliday.put("2012-02-26", "");
        mapHoliday.put("2012-03-03", "");
        mapHoliday.put("2012-03-04", "");
        mapHoliday.put("2012-03-10", "");
        mapHoliday.put("2012-03-11", "");
        mapHoliday.put("2012-03-17", "");
        mapHoliday.put("2012-03-18", "");
        mapHoliday.put("2012-03-24", "");
        mapHoliday.put("2012-03-25", "");
        mapHoliday.put("2012-04-02", "");
        mapHoliday.put("2012-04-03", "");
        mapHoliday.put("2012-04-04", "");
        mapHoliday.put("2012-04-07", "");
        mapHoliday.put("2012-04-08", "");
        mapHoliday.put("2012-04-14", "");
        mapHoliday.put("2012-04-15", "");
        mapHoliday.put("2012-04-21", "");
        mapHoliday.put("2012-04-22", "");
        mapHoliday.put("2012-04-29", "");
        mapHoliday.put("2012-04-30", "");
        mapHoliday.put("2012-05-01", "");
        mapHoliday.put("2012-05-05", "");
        mapHoliday.put("2012-05-06", "");
        mapHoliday.put("2012-05-12", "");
        mapHoliday.put("2012-05-13", "");
        mapHoliday.put("2012-05-19", "");
        mapHoliday.put("2012-05-20", "");
        mapHoliday.put("2012-05-26", "");
        mapHoliday.put("2012-05-27", "");
        mapHoliday.put("2012-06-02", "");
        mapHoliday.put("2012-06-03", "");
        mapHoliday.put("2012-06-09", "");
        mapHoliday.put("2012-06-10", "");
        mapHoliday.put("2012-06-16", "");
        mapHoliday.put("2012-06-17", "");
        mapHoliday.put("2012-06-22", "");
        mapHoliday.put("2012-06-23", "");
        mapHoliday.put("2012-06-24", "");
        mapHoliday.put("2012-06-30", "");
        mapHoliday.put("2012-07-01", "");
        mapHoliday.put("2012-07-07", "");
        mapHoliday.put("2012-07-08", "");
        mapHoliday.put("2012-07-14", "");
        mapHoliday.put("2012-07-15", "");
        mapHoliday.put("2012-07-21", "");
        mapHoliday.put("2012-07-22", "");
        mapHoliday.put("2012-07-28", "");
        mapHoliday.put("2012-07-29", "");
        mapHoliday.put("2012-08-04", "");
        mapHoliday.put("2012-08-05", "");
        mapHoliday.put("2012-08-11", "");
        mapHoliday.put("2012-08-12", "");
        mapHoliday.put("2012-08-18", "");
        mapHoliday.put("2012-08-19", "");
        mapHoliday.put("2012-08-25", "");
        mapHoliday.put("2012-08-26", "");
        mapHoliday.put("2012-09-01", "");
        mapHoliday.put("2012-09-02", "");
        mapHoliday.put("2012-09-08", "");
        mapHoliday.put("2012-09-09", "");
        mapHoliday.put("2012-09-15", "");
        mapHoliday.put("2012-09-16", "");
        mapHoliday.put("2012-09-22", "");
        mapHoliday.put("2012-09-23", "");
        mapHoliday.put("2012-09-30", "");
        mapHoliday.put("2012-10-01", "");
        mapHoliday.put("2012-10-02", "");
        mapHoliday.put("2012-10-03", "");
        mapHoliday.put("2012-10-04", "");
        mapHoliday.put("2012-10-05", "");
        mapHoliday.put("2012-10-06", "");
        mapHoliday.put("2012-10-07", "");
        mapHoliday.put("2012-10-13", "");
        mapHoliday.put("2012-10-14", "");
        mapHoliday.put("2012-10-20", "");
        mapHoliday.put("2012-10-21", "");
        mapHoliday.put("2012-10-27", "");
        mapHoliday.put("2012-10-28", "");
        mapHoliday.put("2012-11-03", "");
        mapHoliday.put("2012-11-04", "");
        mapHoliday.put("2012-11-10", "");
        mapHoliday.put("2012-11-11", "");
        mapHoliday.put("2012-11-17", "");
        mapHoliday.put("2012-11-18", "");
        mapHoliday.put("2012-11-24", "");
        mapHoliday.put("2012-11-25", "");
        mapHoliday.put("2012-12-01", "");
        mapHoliday.put("2012-12-02", "");
        mapHoliday.put("2012-12-08", "");
        mapHoliday.put("2012-12-09", "");
        mapHoliday.put("2012-12-15", "");
        mapHoliday.put("2012-12-16", "");
        mapHoliday.put("2012-12-22", "");
        mapHoliday.put("2012-12-23", "");
        mapHoliday.put("2012-12-29", "");
        mapHoliday.put("2012-12-30", "");
    }
}