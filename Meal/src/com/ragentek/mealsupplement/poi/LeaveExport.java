package com.ragentek.mealsupplement.poi;

import com.ragentek.mealsupplement.db.bean.TFees;
import com.ragentek.mealsupplement.db.bean.TLeave;
import com.ragentek.mealsupplement.json.Fees;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by zixiao.zhang on 2016/5/10.
 */
public class LeaveExport extends BaseExcelExport {
    private static final int DATAT_INVALID = 0;
    private static final int DATATYPE_LEAVE = 1;
    private static final int DATATYPE_FEE = 2;

    private static final String[] TITLES = new String[] {
            "工号","姓名","日期","异常开始时间","异常结束时间","异常总时长（小时）","异常类型"
    };
    private  CellStyle commonStyle;
    private CellStyle centerStyle;
    private List<TLeave> tLeaves;
    public LeaveExport(){}
    public LeaveExport(List<TLeave> tLeaves) {
        this.tLeaves = tLeaves;
    }

    private static final String[] TITLES_FEE = new String[] {
            "工号","姓名","日期","签到时间","签退时间","异常类型"
    };
    private List<TFees> tFeesList;
    public void settFees(List<TFees> tFeesList) {
        this.tFeesList = tFeesList;
    }

    private int getDataType() {
        int dataType = DATAT_INVALID;
        if(tLeaves != null) {
            dataType = DATATYPE_LEAVE;
        } else if(tFeesList != null) {
            dataType = DATATYPE_FEE;
        }
        return dataType;
    }
    private String[] getTitles() {
        int dataType = getDataType();
        if(dataType == DATATYPE_LEAVE) {
            return TITLES;
        } else if(dataType == DATATYPE_FEE) {
            return TITLES_FEE;
        }
        return null;
    }

    @Override
    protected void dataToExcel(Workbook workbook, List data) {
        commonStyle = createCommonStyle(workbook);
        centerStyle = createCenterStyle(workbook);
        int dataType = getDataType();
        int rowIndex = 0;
        Sheet sheet = workbook.createSheet("考勤异常");
        Row title = sheet.createRow(rowIndex++);
        title.setHeight((short) 600);
        for(int i=0;i<getTitles().length;i++) {
            Cell cell = title.createCell(i);
            cell.setCellValue(getTitles()[i]);
            if(dataType == DATATYPE_LEAVE && (i == 3 || i == 4)) {
                CellStyle commonStyle = createCommonStyle(workbook);
                //commonStyle.setFillBackgroundColor(HSSFColor.SKY_BLUE.index);
                commonStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index); //HSSFColor.ORANGE
                commonStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(commonStyle);
            } else {
                CellStyle centerStyle = createCenterStyle(workbook);
                //centerStyle.setFillBackgroundColor(HSSFColor.SKY_BLUE.index);
                centerStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index); //HSSFColor.ORANGE
                centerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(centerStyle);
            }
        }
        if(dataType == DATATYPE_LEAVE) {
            for(TLeave tLeave : tLeaves) {
                Row row = sheet.createRow(rowIndex++);
                row.setHeight((short) 400);
                createCommonCell(row, 0).setCellValue(tLeave.getNumber());
                createCommonCell(row, 1).setCellValue(tLeave.getName());
                createCommonCell(row, 2).setCellValue(tLeave.getDayStr());
                createCommonCell(row, 3).setCellValue(tLeave.getStartTime());
                createCommonCell(row, 4).setCellValue(tLeave.getEndTime());
                createCenterCell(row, 5).setCellValue(tLeave.getTotalHours());
                createCenterCell(row, 6).setCellValue(tLeave.getStat());
            }
        } else if(dataType == DATATYPE_FEE) {
            CellStyle redCenterStyle = createCenterStyle(workbook);
            redCenterStyle.setFillForegroundColor(HSSFColor.RED.index); //HSSFColor.ORANGE
            redCenterStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            for(TFees tFees : tFeesList) {
                Row row = sheet.createRow(rowIndex++);
                row.setHeight((short) 400);
                createCenterCell(row, 0).setCellValue(tFees.getName());
                createCenterCell(row, 1).setCellValue(tFees.getNumber());
                if(tFees.getStatus().equals("未打卡")) {
                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(tFees.getDayStr());
                    cell2.setCellStyle(redCenterStyle);
                    createCenterCell(row, 3).setCellValue("--");
                    createCenterCell(row, 4).setCellValue("--");
                } else {
                    createCenterCell(row, 2).setCellValue(tFees.getDayStr());
                    if(tFees.getStatus().equals("旷工") || tFees.getStatus().equals("迟到")) {
                        Cell cell3 = row.createCell(3);
                        cell3.setCellValue(tFees.getStartTime());
                        cell3.setCellStyle(redCenterStyle);
                    } else {
                        createCenterCell(row, 3).setCellValue(tFees.getStartTime());
                    }
                    if(tFees.getStatus().equals("旷工") || tFees.getStatus().equals("早退")) {
                        Cell cell4 = row.createCell(4);
                        cell4.setCellValue(tFees.getEndTime());
                        cell4.setCellStyle(redCenterStyle);
                    } else {
                        createCenterCell(row, 4).setCellValue(tFees.getEndTime());
                    }
                }
                //createCenterCell(row, 2).setCellValue(tFees.getDayStr());
                //createCenterCell(row, 3).setCellValue(tFees.getStartTime());
                //createCenterCell(row, 4).setCellValue(tFees.getEndTime());
                createCenterCell(row, 5).setCellValue(tFees.getStatus());
            }
        }
        /*** 设置列宽 ***/
        setColumnWidth(sheet);
    }

    private void setColumnWidth(Sheet sheet) {
        int dataType = getDataType();
        if(dataType == DATATYPE_LEAVE) {
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 3000);
            sheet.setColumnWidth(2, 3000);
            sheet.setColumnWidth(3, 2500);
            sheet.setColumnWidth(4, 2500);
            sheet.setColumnWidth(5, 3000);
            sheet.setColumnWidth(6, 3000);
        } else {
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 3000);
            sheet.setColumnWidth(2, 3000);
            sheet.setColumnWidth(3, 2500);
            sheet.setColumnWidth(4, 2500);
            sheet.setColumnWidth(5, 3000);
        }
    }

    private CellStyle createCommonStyle(Workbook workbook) {
        CellStyle commonStyle = workbook.createCellStyle();
        commonStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        commonStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        commonStyle.setTopBorderColor(HSSFColor.BLACK.index);
        commonStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        commonStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        commonStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        commonStyle.setRightBorderColor(HSSFColor.BLACK.index);
        commonStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        commonStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        commonStyle.setWrapText(true);
        Font font = workbook.createFont();
        //font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);
        commonStyle.setFont(font);
        return commonStyle;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle centerStyle = createCommonStyle(workbook);
        centerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        //centerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        return centerStyle;
    }

    private Cell createCommonCell(Row row, int index) {
        Cell cell = row.createCell(index);
        cell.setCellStyle(commonStyle);
        return cell;
    }

    private Cell createCenterCell(Row row, int index) {
        Cell cell = row.createCell(index);
        cell.setCellStyle(centerStyle);
        return cell;
    }

}
