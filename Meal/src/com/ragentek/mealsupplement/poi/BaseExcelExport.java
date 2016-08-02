package com.ragentek.mealsupplement.poi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

public abstract class BaseExcelExport<T> {
	private List<T> data;
	private String format = PoiUtil.FORMAT_XLSX; //默认为xlsx
	
	public final void setData(List<T> data) {
		this.data = data;
	}

	public final void setFormat(String format) {
		this.format = format;
	}
	
	protected InputStream getTemplateInputStream() {
		InputStream is = getClass().getClassLoader().getResourceAsStream(getClassFilePath(getClass().getPackage().getName(), getClass().getSimpleName()+".template."+PoiUtil.FORMAT_XLS));
		return is;
	}

	private String getClassFilePath(String packageName, String fileName) {
		if(packageName==null || fileName==null) {
			return null;
		}
		return packageName.replace('.', '/')+"/"+fileName;
	}

	public final void exportExcel(OutputStream os) throws IOException {
		Workbook workbook = null;
		InputStream templateInputStream = getTemplateInputStream();
		if(templateInputStream != null) {
			workbook = PoiUtil.createWorkbook(format, templateInputStream);
		} else {
			workbook = PoiUtil.createWorkbook(format);
		}
		dataToExcel(workbook, data);
		workbook.write(os);
		os.flush();
		os.close();
		System.out.println("写入 。，。。 os"+os);
	}
	
	protected abstract void dataToExcel(Workbook workbook, List<T> data);
	
	
}
