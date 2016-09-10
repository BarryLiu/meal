package com.ragentek.mealsupplement.poi;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.KaoqinDto;
import com.ragentek.mealsupplement.db.bean.TBill;
import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.Bill;
import com.ragentek.mealsupplement.tools.StringTools;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yingjing.liu on 2016/5/23.
 */
public class UserAttendanceExport extends BaseExcelExport {
    private String startDate;
    private String endDate;

    private String fileName;
    private   Map<String,TUser> extUsers;   //高管的人数 .事假计算为负也为 0
    private List<KaoqinDto> dtoDetails;
    public static final String[] columns = new String[]{
            "考勤号码", "姓名",/* "实到(天)",*/"应到", "迟到/早退(次)", "未打卡(次)", "公出(小时)", "出差(天)", "事假(小时)", "病假(小时)", "年假(小时)","丧假","婚假","产假（陪产假）","交通故障", "其它假期(小时)", "加班(小时)",
            "正常(调休)(小时)", "非正常(调休)(小时)", "加班结余(小时)", "早(元)", "中(元)", "晚(元)", "宵(元)"
    };
    public static final String[] columnsDetail = new String[]{
            "考勤号码", "姓名", "日期", "签到时间", "签退时间", "实到", "迟到/早退", "未打卡", "公出", "出差",  "事假(小时)", "病假(小时)", "年假(小时)",
            "丧假","婚假","产假（陪产假）","交通故障","其它假期(小时)", "加班(分钟)", "正常(调休)(小时)", "非正常(调休)(小时)", "早(元)", "中(元)", "晚(元)", "宵(元)"
    };

    public static final String [] columnsAtte=new String[]{
            "考勤号码","姓名","早","中","晚","宵","总计"
    };
    public UserAttendanceExport() {

    }

    public UserAttendanceExport(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<KaoqinDto> getDtoDetails() {
        return dtoDetails;
    }

    public void setDtoDetails(List<KaoqinDto> dtoDetails) {
        this.dtoDetails = dtoDetails;
    }

    @Override
    protected synchronized void dataToExcel(Workbook workbook, List data) {

        //对数据校正，     对不参与考勤的人数校正//
        data = rectifyKaoQin(data);


        tBills = DBUtils.query(" select * from t_bill  ",TBill.class);


        Sheet sheetDetail = (Sheet) workbook.createSheet("考勤明细");
        Sheet sheet1 = (Sheet) workbook.createSheet("考勤汇总");
        Sheet sheetAtta = (Sheet) workbook.createSheet("餐补");

        Row row1 = (Row) sheet1.createRow(0);
        row1.setHeight((short) (25 * 20));
        sheet1.autoSizeColumn(1, true);
        // row1.setHeight(Short.parseShort("20"));
//        row1.setRowStyle(CellStyle.ALIGN_FILL);

        //设置样式
        XSSFCellStyle my_style = (XSSFCellStyle) workbook.createCellStyle();

      // my_style.setFillPattern(XSSFCellStyle.FINE_DOTS );
         my_style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
         my_style.setFillBackgroundColor(IndexedColors.RED.getIndex());
         my_style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
         my_style.setWrapText(true);

       // row1.setRowStyle(my_style);
        for (int j = 0; j < columns.length; j++) {
            Cell cell = row1.createCell(j);
            cell.setCellValue(columns[j]);
            cell.setCellStyle(my_style);
        }
        

        Integer detailsRowNum = 0;
        Row rowDetail = (Row) sheetDetail.createRow(detailsRowNum++);
        row1.setHeight((short) (25 * 20));
        sheet1.autoSizeColumn(1, true);
        for (int j = 0; j < columnsDetail.length; j++) {
            Cell cell = rowDetail.createCell(j);
            cell.setCellValue(columnsDetail[j]);
            cell.setCellStyle(my_style);
        }

        Row rowAtte = (Row) sheetAtta.createRow(0);
        for (int j = 0; j < columnsAtte.length; j++) {
            Cell cell = rowAtte.createCell(j);
            cell.setCellValue(columnsAtte[j]);
            cell.setCellStyle(my_style);
        }




        List<KaoqinDto> kqs = new ArrayList<KaoqinDto>();

        //写入数据
        for (int i = 1; i <= data.size(); i++) {
            KaoqinDto dto = (KaoqinDto) data.get(i - 1);


            Integer atteRow = 0;
            Row row = (Row) sheet1.createRow(i); //汇总
            // 循环写入列数据
            for (int j = 0; j < columns.length; j++) {
                Cell cell = row.createCell(j);
                if(dto.getNumber().equals("100291")){
                    System.out.println("dto:"+dto);
                }
                String result = getValue(dto, j);
                try {
                    cell.setCellValue(Integer.valueOf(result));//   转换成数字类型的 如果不是则到catch中string
                }catch (Exception e){
                    cell.setCellValue(result);
                }
            }

            // 根据 详情汇总拿到的数据写入到详情页中去
         /*    List<KaoqinDto> kds = dto.getDtos();
            for(int n= 1;n<kds.size();n++){
                KaoqinDto kd = kds.get(n-1);
                rowDetail = sheetDetail.createRow(detailsRowNum++);
                for(int m= 0;m<columnsDetail.length;m++){
                    Cell cell = rowDetail.createCell(m);

                    String result = null;
                    try {
                        result = getDetailsValue(kd,m);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    cell.setCellValue(result);
                }
            }*/
        }


        // 根据 详情汇总拿到的数据写入到详情页中去
           if(dtoDetails!=null){
               for(int n= 0;n<dtoDetails.size();n++){
                   KaoqinDto kd = dtoDetails.get(n);
                   rowDetail = sheetDetail.createRow(detailsRowNum++);
                   for(int m= 0;m<columnsDetail.length;m++){
                       Cell cell = rowDetail.createCell(m);

                       String result = null;
                       try {
                           result = getDetailsValue(kd,m);
                            if("".equals(result))
                                result = "0";
                           if(m==1||m==2|| m==3||m==4){  //为0 为1 是String 类型 ， 其他为Integer类型  1,2,3,4,列刚好不是数字类型
                               cell.setCellValue(result);
                           }else{
                               cell.setCellValue(Integer.valueOf(result));
                           }
                       } catch (ParseException e) {
                           e.printStackTrace();
                           cell.setCellValue(result);
                       }

                   }
               }
           }



            Integer zaoSum=0,zhongSum=0,wanSum=0,xiaoSum=0,sum =0; //写入餐补数据
            for(int i=0 ; i<data.size() ;i++){

                Row rowAtt = sheetAtta.createRow(i+1);
                
                KaoqinDto dto = (KaoqinDto) data.get(i);
                rowAtt.createCell(0).setCellValue(Integer.valueOf(dto.getNumber()));
                rowAtt.createCell(1).setCellValue(dto.getName());
                rowAtt.createCell(2).setCellValue(dto.getZao()); 
                rowAtt.createCell(3).setCellValue(dto.getZhong());
                rowAtt.createCell(4).setCellValue(dto.getWan());
                rowAtt.createCell(5).setCellValue(dto.getXiao());

                Integer cSum = dto.getZao()+dto.getZhong()+dto.getWan()+dto.getXiao();
                rowAtt.createCell(6).setCellValue(cSum);

                zaoSum += dto.getZao();
                zhongSum +=dto.getZhong();
                wanSum +=dto.getWan();
                xiaoSum+=dto.getXiao();
                sum += cSum;
        }

       Row atteLast =  sheetAtta.createRow(sheetAtta.getLastRowNum()+1); //不加1 最后一个被覆盖了
        atteLast.createCell(0).setCellValue("总计");
        atteLast.createCell(2).setCellValue(zaoSum);
        atteLast.createCell(3).setCellValue(zhongSum);
        atteLast.createCell(4).setCellValue(wanSum);
        atteLast.createCell(5).setCellValue(xiaoSum);
        atteLast.createCell(6).setCellValue((zaoSum+zhongSum+wanSum+xiaoSum));//zaoSu
        System.out.println("写入成功");
    }

    /**
     * 高管的事假都为0 ，   固定几个人不记录考勤
     *
     * @param data
     * @return
     */
    private List rectifyKaoQin(List data) {
        //移除最后一个多余的
        List<KaoqinDto> allData=new ArrayList<KaoqinDto>(data);

        data.remove(data.size()-1);

        extUsers =new HashMap<String, TUser>();
        String sql = "select * from t_user where stat = "+KaoqinDto.STATUS_MANAGER;//得到高管
        List<TUser> extUsersList = DBUtils.query(sql,TUser.class);
        
        for(int i=0 ; i<extUsersList.size(); i++){
            TUser u = extUsersList.get(i);
            extUsers.put(u.getNumber(),u);
        }

        for(int i=0 ; i<allData.size(); i++){
            KaoqinDto dto = allData.get(i); //如果是高管事假设为0
            if(extUsers.containsKey(dto.getNumber())){
                dto.setShijia(0);           //
            }
        }


        //固定几个人不记录考勤
        sql = "select * from t_user where stat = "+KaoqinDto.STATUS_NO_ATTENDANCE ; //得到不参与考勤
        List<TUser> noMealUsersList = DBUtils.query(sql,TUser.class);
        Map<String,TUser> noMealUserMap = new HashMap<String, TUser>();
        for(int i=0 ; i<noMealUsersList.size();i++){                      //进去添加不参与考勤的人
            TUser noMealUser = noMealUsersList.get(i);
            noMealUserMap.put(noMealUser.getNumber(),noMealUser);
        }
       //强制不参与考勤
        noMealUserMap.put("100040",null);   //黄华云 师傅，不计算
        noMealUserMap.put("100041",null);
        noMealUserMap.put("100155",null);   //不计算
        for(int i=0 ; i<allData.size();i++){
            if (noMealUserMap.containsKey(allData.get(i).getNumber())){
               KaoqinDto u =  allData.get(i);
                u.setZao(0);
                u.setZhong(0);
                u.setWan(0);
                u.setXiao(0);
            }
        }



        return allData;
    }


    private  List<TBill> tBills ;

    private synchronized String getDetailsValue(KaoqinDto dto, int m) throws ParseException {
       /* if("100185".equals(dto.getNumber())){
            System.out.println("100185 : "+dto);
        }*/

        String result = "0";
        switch (m) {
            case 0:
                result = dto.getNumber();
                break;
            case 1:
                result = dto.getName();
                break;
            case 2:
                result = dto.getDayStr();
                break;
            case 3:result = dto.getStartTime();
                break;
            case 4: result = dto.getEndTime();
                break;
            case 5: //实到                
                //result = dto.getGongchu()+"";
                String dayStr = dto.getDayStr();
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                Date date =  sdf.parse(dayStr);
               switch (date.getDay()){ //是星期一到星期五就是实到
                   case 1:
                   case 2:
                   case 3:
                   case 4:
                   case 5:
                       result = "1";
                       break;
                   default:
                       result = "";
                       break;
               }

                break;
            case 6: //迟到早退
               int status =0;
                 if(dto.getStatus()!=null){
                    if(dto.getStatus().contains("迟到")){
                        status++;
                    }
                     if(dto.getStatus().contains("早退")){
                         status++;
                     }
                }
                result = status==0?"":status+"";
                //9点过后来迟到   6点前走迟到
             /*   SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                try {
                    Date startDate = dateFormat.parse(dto.getStartTime());
                    if (startDate.getHours()>9&&startDate.getMinutes()>30){
                        status++;
                    }

                    Date endDate = dateFormat.parse(dto.getEndTime());
                    if(endDate.getHours()<6){
                        status++;
                    }

                    result = status==0?"":status+"";
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
               // result = dto.getZtcd()+"";

                break;
            case 7://未打卡
                //String sql = "select * from t_bill where bill_name like '%未打卡%'  and  cast('"+dto.getDayStr()+" "+dto.getStartTime()+"' as datetime) <=  start_time  and  end_time <= cast('"+ dto.getDayStr()+" "+dto.getEndTime()+"' as datetime) and number = "+dto.getNumber();
                /*tBills = DBUtils.query(sql,TBill.class);
                if(tBills.size()>0){
                    result= "1";
                }*/

                if("未打卡".equals(dto.getStatus())){
                  result = "1";
                }
                break;
            case 8://公出
               /* sql = "select * from t_bill where bill_name like '%未打卡%'  and  cast('"+dto.getDayStr()+" "+dto.getStartTime()+"' as datetime) <=  start_time  and  end_time <= cast('"+dto.getDayStr()+" "+dto.getEndTime()+"' as datetime) and number = "+dto.getNumber();
                tBills = DBUtils.query(sql,TBill.class);
                if(tBills.size()>0){
                    result= tBills.get(0).getTotalHours()+"";
                }*/
                result = StringTools.getString(dto.getGongchu());
                break;
            case 9://出差
              /*  sql = "select * from t_bill where bill_name like '%出差%'  and  cast('"+dto.getDayStr()+" "+dto.getStartTime()+"' as datetime) <=  start_time  and  end_time <= cast('"+dto.getDayStr()+" "+dto.getEndTime()+"' as datetime) and number = "+dto.getNumber();
                tBills = DBUtils.query(sql,TBill.class);
                if(tBills.size()>0){
                    result= "1";
                }
                System.out.println("出差："+tBills.size()+" sql:"+sql);*/
                result = StringTools.getString(dto.getChucai());
                break;
            case 10://事假
             /*   sql = "select * from t_bill where bill_name like '%事假%'  and  cast('"+dto.getDayStr()+" "+dto.getStartTime()+"' as datetime) <=  start_time  and  end_time <= cast('"+dto.getDayStr()+" "+dto.getEndTime()+"' as datetime) and number = "+dto.getNumber();
                tBills = DBUtils.query(sql,TBill.class);
                if(tBills.size()>0){
                    result= tBills.get(0).getTotalHours()+"";
                }
                System.out.println("事假："+tBills.size()+" sql:"+sql);*/
                //result = dto.getQitajia()+"";
                result = StringTools.getString(dto.getShijia());
                break;
            case 11://病假
            /*    sql = "select * from t_bill where bill_name like '%病假%'  and  cast('"+dto.getDayStr()+" "+dto.getStartTime()+"' as datetime) <=  start_time  and  end_time <= cast('"+dto.getDayStr()+" "+dto.getEndTime()+"' as datetime) and number = "+dto.getNumber();
                tBills = DBUtils.query(sql,TBill.class);
                if(tBills.size()>0){
                    result=  tBills.get(0).getTotalHours()+"" ;
                    System.out.println("sss:sql:"+sql);
                }*/
                //result = dto.getJiaban()+"";
                result = StringTools.getString(dto.getBingjia());
                break;
            case 12://年假
              /*  sql = "select * from t_bill where bill_name like '%年假%'  and  cast('"+dto.getDayStr()+" "+dto.getStartTime()+"' as datetime) <=  start_time  and  end_time <= cast('"+dto.getDayStr()+" "+dto.getEndTime()+"' as datetime) and number = "+dto.getNumber();
                tBills = DBUtils.query(sql,TBill.class);
                if(tBills.size()>0){
                    result=  tBills.get(0).getTotalHours()+"" ;
                }*/
                result = StringTools.getString(dto.getNianjia());
                break;
            // yingjing.liu 20160777 start 添加 丧假等假期
            case 13:
                result = StringTools.getString(dto.getSangjia());
                break;
            case 14:
                result=StringTools.getString(dto.getHunjia());
                break;
            case 15:
                result=StringTools.getString(dto.getChanjia());
                break;
            case 16:
                result=StringTools.getString(dto.getJiaotong());
                break;
            case 17://其它假期
               /* sql = "select * from t_bill where bill_name like '%其它假期%'  and  cast('"+dto.getDayStr()+" "+dto.getStartTime()+"' as datetime) <=  start_time  and  end_time <= cast('"+dto.getDayStr()+" "+dto.getEndTime()+"' as datetime) and number = "+dto.getNumber();
                tBills = DBUtils.query(sql,TBill.class);
                if(tBills.size()>0){
                    result=  tBills.get(0).getTotalHours()+"" ;
                }*/
                result = StringTools.getString(dto.getQitajia());
                break;
            case 18: //加班

                result = dto.getJiaban()+"";
                break;
            case 19: //正常                           --------貌似数据不准确

               /* sql = "select * from t_leave" +
                        " where number = "+dto.getNumber()+"  and bill_name like '%调休（正常）%'" +
                        " and  '"+dto.getDayStr()+"'  =  day_str ";
                       List<TLeave> leaves =  DBUtils.query(sql, TLeave.class);
                        if(leaves.size()>0){
                            result = ""+leaves.get(0).getTotalHours();
                        }*/
                result = StringTools.getString(dto.getTiaoxiuz());
                //System.out.println("调休正常 =leaves: "+leaves.size()+" sql: "+sql);
                break;
            case 20://非正常                        --------貌似数据不准确
               /*  sql = "select * from t_leave" +
                        " where number = "+dto.getNumber()+"  and bill_name like '%调休（非正常）%'" +
                        " and  '"+dto.getDayStr()+"' =  day_str ";
                 leaves =  DBUtils.query(sql, TLeave.class);
                if(leaves.size()>0){
                    result = ""+leaves.get(0).getTotalHours();
                }*/
                result = StringTools.getString(dto.getTiaoxiuf());
                break; 
            case 21://早
                result = StringTools.getString(dto.getZao());
                break;
            case 22:
                result = StringTools.getString(dto.getZhong());
                break;
            case 23:result  = StringTools.getString( dto.getWan());
                break;
            case 24:result = StringTools.getString(dto.getXiao());
                break;
            default:
                System.out.println("还有多的 detail ");
                break;
        } 
        return result;
    }

    private String getValue(KaoqinDto dto, int j) {
        String result = "";
        switch (j) {
            case 0:
                result = dto.getNumber();
                break;
            case 1:
                result = dto.getName();
                break;
            case 2:
                result = dto.getYingdao()+"";//应到
                break;
            case 3:result = dto.getZtcd()+"";
                break;
            case 4: result = dto.getWeidaka()+"";
                break;
            case 5:result = dto.getGongchu()+"";
                break;
            case 6:result = dto.getChucai()+"";
                break;
            case 7:
                if(!extUsers.containsKey(dto.getNumber())){
                    result = dto.getShijia()+"";
                }else{      // 高管是没有事假的，就算有也没有
                    result="0";
                }
                break;
            case 8:result = dto.getBingjia()+"";
                break;
            case 9:result = dto.getNianjia()+"";
                break;
            case 10:
                result = dto.getSangjia()+"";
                break;
            case 11:
                result = dto.getHunjia()+"";
                break;
            case 12:
                result = dto.getChanjia()+"";
                break;
            case 13:
                result = dto.getJiaotong()+"";
                break;
            case 14:result = dto.getQitajia()+"";
                break;
            case 15:result = (dto.getJiaban()/60)+"";  // 分钟转换成小时
                break;
            case 16:result = dto.getTiaoxiuz()+"";
                break;
            case 17:
                result = dto.getTiaoxiuf()+"";
                break;
            case 18:
                result = dto.getJbjieyu()+"";
                break;
            case 19:
                result = dto.getZao()+"";
                break;
            case 20:
                result = dto.getZhong()+"";
                break;
            case 21:result  = dto.getWan()+"";
                break;
            case 22:result = dto.getXiao()+"";
                break;

            default:
                System.out.println("还有多的");
                break;
         }

        return result;
    }
}
