package com.ragentek.mealsupplement.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

public abstract class BaseExcelImport<T> {
	private File file;
	private String fileName;
	protected List<T> data;
	
	public final void setFile(File file) {
		this.file = file;
		if(this.fileName == null) {
			this.fileName = file.getName();
		}
	}

	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}

//	public final List<T> getData() {
//		return data;
//	}
	
	public final List<T> importExcel() throws IOException {
		InputStream is = new FileInputStream(file);
		Workbook workbook = PoiUtil.createWorkbook(fileName, is);
		data = dataFromExcel(workbook);
		if(is != null) {
			is.close();
		}
		return data;
	}
	
	protected abstract List<T> dataFromExcel(Workbook workbook);
}
