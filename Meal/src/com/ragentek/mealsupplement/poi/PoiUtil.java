package com.ragentek.mealsupplement.poi;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PoiUtil {
	private static final Logger logger = Logger.getLogger(PoiUtil.class);
	public static final String FORMAT_XLS = "xls";
	public static final String FORMAT_XLSX = "xlsx";
	/**
	 * 根据xls或者xlsx创建Workbook
	 * @param fileName 可以只有xls或xlsx，也可以是xls或xlsx的文件名
	 * @return
	 */
	public static Workbook createWorkbook(String fileName) {
		if(fileName.toLowerCase().endsWith("xls")) {
			return new HSSFWorkbook();
		}
		if(fileName.toLowerCase().endsWith("xlsx")) {
			return new XSSFWorkbook();
		}
		return null;
	}
	
	public static Workbook createWorkbook(String fileName, InputStream is) throws IOException {
		if(fileName.toLowerCase().endsWith("xls")) {
			return new HSSFWorkbook(is);
		}
		if(fileName.toLowerCase().endsWith("xlsx")) {
			return new XSSFWorkbook(is);
		}
		return null;
	}
	
	public static boolean isEmpty(Cell cell) {
		if(cell == null) {
			return true;
		}
		if(cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			return true;
		}
		if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			String str = cell.getStringCellValue();
			if(str.trim().equals("")) {
				return true;
			}
		}
		return false;
	}

	public static Date getCellDateValue(Cell cell) {
		Date date = null;
		if(cell != null) {
			if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				if(DateUtil.isCellDateFormatted(cell)) {
					date = cell.getDateCellValue();
				} else if(cell.getCellStyle().getDataFormat() == 58) { //自定义日期格式：m月d日
					double value = cell.getNumericCellValue();
					date = DateUtil.getJavaDate(value);
				}
			}
		}
		return date;
	}
	
	public static String getCellStringValue(Cell cell) {
		if(cell == null) {
			return null;
		}
		if(cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return "";
		}
		if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		}
		if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			String numericValue = null;
			/* 日期格式此处不单独处理，因为格式很多，而值是不变的，故取出数值就可以
			if(DateUtil.isCellDateFormatted(cell)) {
				System.out.println(cell.getCellStyle().getDataFormatString());
				SimpleDateFormat sdf = null;
				if(cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) { //时间
					sdf = new SimpleDateFormat("HH:mm");
				} else { //日期
					sdf = new SimpleDateFormat("yyyy/MM/dd");
				}
				Date date = cell.getDateCellValue();
				numericValue = sdf.format(date);
			} else if(cell.getCellStyle().getDataFormat() == 58) { //自定义日期格式：m月d日
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				double value = cell.getNumericCellValue();
				Date date = DateUtil.getJavaDate(value);
				numericValue = sdf.format(date);
			} else {
				Double d = cell.getNumericCellValue();
				if(d.longValue() == d.doubleValue()) {
					numericValue = String.valueOf(d.longValue());
				} else {
					numericValue = String.valueOf(d.doubleValue());
				}
			}
			*/
			Double d = cell.getNumericCellValue();
			if(d.longValue() == d.doubleValue()) { //目前无法判断到底是long还是double，如果一律double，long类型的会多个.0，如果一律long，double的小数部分会丢失，目前先假定后面为.0的都是自动加上的，其实为long类型
				numericValue = String.valueOf(d.longValue());
			} else {
				numericValue = String.valueOf(d.doubleValue());
			}
			return numericValue;
		}
		if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue();
		}
		if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			String formulaValue = null;
			try {
				formulaValue = cell.getStringCellValue();
			} catch(Exception e1) {
				try {
					Double d = cell.getNumericCellValue();
					if(d.longValue() == d.doubleValue()) {
						formulaValue = String.valueOf(d.longValue());
					} else {
						formulaValue = String.valueOf(d.doubleValue());
					}
				}catch(Exception e2) {
					try {
						formulaValue = String.valueOf(cell.getBooleanCellValue());
					} catch(Exception e3) {
						formulaValue = cell.getCellFormula();
					}
				}
			}
			return formulaValue;
		}
		throw new RuntimeException("getCellStringValue exception : cellType="+cell.getCellType()+",cell="+cell);
	}
	
	public static Integer getCellIntegerValue(Cell cell) {
		Integer res = null;
		if(cell != null) {
			if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				Double d = cell.getNumericCellValue();
				res = d.intValue();
			} else if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
				String s = cell.getStringCellValue();
				try {
					res = Integer.parseInt(s);
				} catch(Exception e) {
					logger.error("getCellIntegerValue : value="+s);
				}
			}
		}
		return res;
	}
	
	public static boolean isFormulaCell(Cell cell) {
		if(cell != null && cell.getCellType()==Cell.CELL_TYPE_FORMULA) {
			return true;
		}
		return false;
	}
	
	public static List<Row> insertRows(Sheet sheet, int startRow, int rows) {  
		List<Row> rowList = new ArrayList<Row>();
        sheet.shiftRows(startRow, sheet.getLastRowNum(), rows, true, false);   
  
        //----------------只插入，不copy样式
        /*
        for (int i = 0; i < rows; i++) {  
            Row sourceRow = null;//原始位置  
            Row targetRow = null;//移动后位置  
            Cell sourceCell = null;  
            Cell targetCell = null;  
            sourceRow = sheet.createRow(startRow);  
            targetRow = sheet.getRow(startRow + rows);  
            sourceRow.setHeight(targetRow.getHeight());  
  
            for (int m = targetRow.getFirstCellNum(); m < targetRow.getPhysicalNumberOfCells(); m++) {  
                sourceCell = sourceRow.createCell(m);  
                targetCell = targetRow.getCell(m);  
                sourceCell.setCellStyle(targetCell.getCellStyle());  
                sourceCell.setCellType(targetCell.getCellType());  
            }  
            startRow++;  
        }  
        */
        for(int i=startRow; i<rows; i++) {
        	Row row = sheet.getRow(i);
        	rowList.add(row);
        }
        return rowList;
    }
	
	public static Row insertRow(Sheet sheet, int rowIndex) {  
		sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1, true, false);  
		return sheet.getRow(rowIndex);
	}
	
	/**
	 * 将targetRow的样式copy到sourceRow
	 * @param sourceRow
	 * @param targetRow
	 */
	public static void copyStyleForRow(Row sourceRow, Row targetRow) {
		Cell sourceCell = null;  
        Cell targetCell = null;
        sourceRow.setHeight(targetRow.getHeight());
        for (int m = targetRow.getFirstCellNum(); m < targetRow.getPhysicalNumberOfCells(); m++) {  
            sourceCell = sourceRow.createCell(m);  
            targetCell = targetRow.getCell(m);  
            sourceCell.setCellStyle(targetCell.getCellStyle());  
            sourceCell.setCellType(targetCell.getCellType());  
        }
	}
	
	public static void copyRow(Row sourceRow, Row targetRow) {
		Cell sourceCell = null;  
        Cell targetCell = null;
        sourceRow.setHeight(targetRow.getHeight());
        for (int m = targetRow.getFirstCellNum(); m < targetRow.getPhysicalNumberOfCells(); m++) {  
            sourceCell = sourceRow.createCell(m);  
            targetCell = targetRow.getCell(m);  
            sourceCell.setCellStyle(targetCell.getCellStyle());  
            sourceCell.setCellType(targetCell.getCellType());  
            switch(targetCell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
            	break;
            case Cell.CELL_TYPE_BOOLEAN:
            	sourceCell.setCellValue(targetCell.getBooleanCellValue());
            	break;
            case Cell.CELL_TYPE_ERROR:
            	sourceCell.setCellErrorValue(targetCell.getErrorCellValue());
            	break;
            case Cell.CELL_TYPE_FORMULA:
            	sourceCell.setCellFormula(targetCell.getCellFormula());
            	break;
            case Cell.CELL_TYPE_NUMERIC:
            	sourceCell.setCellValue(targetCell.getNumericCellValue());
            	break;
            case Cell.CELL_TYPE_STRING:
            	sourceCell.setCellValue(targetCell.getStringCellValue());
            	break;
            }
        }
	}
	
	public static void main(String[] args) {
		List<String> partNumbers = new ArrayList<String>();
		partNumbers.add("q2221");
		partNumbers.add("R4444");
		System.out.println(partNumbers.contains("q2221"));
	}
	
	
}
