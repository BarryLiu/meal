package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TLeave implements SqlBean {
@Override
public String getTableName() {
	return "t_leave";
}
@Override
public String getPrimaryKeyColumnName() {
	return "_id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"_id",
"bill_id",
"bill_name",
"user_info",
"name",
"number",
"day_str",
"start_time",
"end_time",
"total_hours",
"stat"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("Id=");	
sb.append(Id);	
sb.append(",billId=");	
sb.append(billId);	
sb.append(",billName=");	
sb.append(billName);	
sb.append(",userInfo=");	
sb.append(userInfo);	
sb.append(",name=");	
sb.append(name);	
sb.append(",number=");	
sb.append(number);	
sb.append(",dayStr=");	
sb.append(dayStr);	
sb.append(",startTime=");	
sb.append(startTime);	
sb.append(",endTime=");	
sb.append(endTime);	
sb.append(",totalHours=");	
sb.append(totalHours);	
sb.append(",stat=");	
sb.append(stat);
	return sb.toString();
}
	private Long Id;
	private Long billId;
	private String billName;
	private String userInfo;
	private String name;
	private String number;
	private String dayStr;
	private String startTime;
	private String endTime;
	private Integer totalHours;
	private String stat;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}
	
	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}
	
	public String getBillName() {
		return billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}
	
	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getDayStr() {
		return dayStr;
	}

	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public Integer getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(Integer totalHours) {
		this.totalHours = totalHours;
	}
	
	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
}
