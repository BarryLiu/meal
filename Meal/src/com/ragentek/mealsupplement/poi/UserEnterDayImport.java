package com.ragentek.mealsupplement.poi;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.listener.UserTimer;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.tools.DateTools;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/4/21.
 */
public class UserEnterDayImport extends BaseExcelImport {
    public UserEnterDayImport(File f) {
        setFile(f);
    }
    @Override
    protected List dataFromExcel(Workbook workbook) {
        Sheet sheet = workbook.getSheet("sheet1");
        int rowIndex = 1; //从第几行开始读取（从0开始），1表示第二行
        UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
        try {
            for(int i=rowIndex;i<=sheet.getLastRowNum();i++) {
                Row row = sheet.getRow(i);
                if(row == null) {
                    break;
                }
                //工号
                Cell numberCell = row.getCell(1);
                String number = PoiUtil.getCellStringValue(numberCell);
                //姓名
                Cell namesCell = row.getCell(2);
                String name = PoiUtil.getCellStringValue(namesCell);
                //入职日期
                Cell entryDayCell = row.getCell(3);
                Date date = PoiUtil.getCellDateValue(entryDayCell);
                //String entryDay = PoiUtil.getCellStringValue(entryDayCell);
                String entryDay = DateTools.formatDateToString(date, DateTools.FORSTR_DATE);
                System.out.println(number+":"+name+":"+entryDay);
                TUser tUser = userService.getByNumber(number);
                if(tUser != null) {
                    tUser.setEnterDate(entryDay);
                    DBUtils.update(tUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        /*File f = new File("D:\\入职时间.xlsx");
        UserEnterDayImport imp = new UserEnterDayImport(f);
        imp.importExcel();*/
        UserTimer.handle("2015/08/01","2016/03/31");
//        UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
//        List<TUser> tUsers = userService.getAttendanceUsers("2015/08/01");
//        for(TUser tUser : tUsers) {
//            System.out.println(tUser.getDisplayName());
//        }
    }
}
