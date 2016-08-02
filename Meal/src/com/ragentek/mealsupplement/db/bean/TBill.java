package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TBill implements SqlBean {
@Override
public String getTableName() {
	return "t_bill";
}
@Override
public String getPrimaryKeyColumnName() {
	return "_id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"_id",
"bill_name",
"start_time",
"end_time",
"start_time_str",
"end_time_str",
"total_hours",
"name",
"depart",
"number",
"descs",
"bill_type"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("Id=");	
sb.append(Id);	
sb.append(",billName=");	
sb.append(billName);	
sb.append(",startTime=");	
sb.append(startTime);	
sb.append(",endTime=");	
sb.append(endTime);	
sb.append(",startTimeStr=");	
sb.append(startTimeStr);	
sb.append(",endTimeStr=");	
sb.append(endTimeStr);	
sb.append(",totalHours=");	
sb.append(totalHours);	
sb.append(",name=");	
sb.append(name);	
sb.append(",depart=");	
sb.append(depart);	
sb.append(",number=");	
sb.append(number);	
sb.append(",descs=");	
sb.append(descs);	
sb.append(",billType=");	
sb.append(billType);
	return sb.toString();
}
	private Long Id;
	private String billName;
	private java.sql.Timestamp startTime;
	private java.sql.Timestamp endTime;
	private String startTimeStr;
	private String endTimeStr;
	private Integer totalHours;
	private String name;
	private String depart;
	private String number;
	private String descs;
	private Integer billType;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}
	
	public String getBillName() {
		return billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}
	
	public java.sql.Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(java.sql.Timestamp startTime) {
		this.startTime = startTime;
	}
	
	public java.sql.Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(java.sql.Timestamp endTime) {
		this.endTime = endTime;
	}
	
	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}
	
	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}
	
	public Integer getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(Integer totalHours) {
		this.totalHours = totalHours;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	
	public Integer getBillType() {
		return billType;
	}

	public void setBillType(Integer billType) {
		this.billType = billType;
	}
}
