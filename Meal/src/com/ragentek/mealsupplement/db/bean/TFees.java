package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TFees implements SqlBean {
@Override
public String getTableName() {
	return "t_fees";
}
@Override
public String getPrimaryKeyColumnName() {
	return "id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"id",
"user_info",
"day_str",
"fee1",
"fee2",
"fee3",
"fee4",
"fee5",
"start_time",
"end_time",
"workovertime",
"status",
"name",
"number",
"fee_type",
"deal_status"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("id=");	
sb.append(id);	
sb.append(",userInfo=");	
sb.append(userInfo);	
sb.append(",dayStr=");	
sb.append(dayStr);	
sb.append(",fee1=");	
sb.append(fee1);	
sb.append(",fee2=");	
sb.append(fee2);	
sb.append(",fee3=");	
sb.append(fee3);	
sb.append(",fee4=");	
sb.append(fee4);	
sb.append(",fee5=");	
sb.append(fee5);	
sb.append(",startTime=");	
sb.append(startTime);	
sb.append(",endTime=");	
sb.append(endTime);	
sb.append(",workovertime=");	
sb.append(workovertime);	
sb.append(",status=");	
sb.append(status);	
sb.append(",name=");	
sb.append(name);	
sb.append(",number=");	
sb.append(number);	
sb.append(",feeType=");	
sb.append(feeType);	
sb.append(",dealStatus=");	
sb.append(dealStatus);
	return sb.toString();
}
	private Long id;
	private String userInfo;
	private String dayStr;
	private Integer fee1;
	private Integer fee2;
	private Integer fee3;
	private Integer fee4;
	private Integer fee5;
	private String startTime;
	private String endTime;
	private Integer workovertime;
	private String status;
	private String name;
	private String number;
	private Integer feeType;
	private String dealStatus;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	
	public String getDayStr() {
		return dayStr;
	}

	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}
	
	public Integer getFee1() {
		return fee1;
	}

	public void setFee1(Integer fee1) {
		this.fee1 = fee1;
	}
	
	public Integer getFee2() {
		return fee2;
	}

	public void setFee2(Integer fee2) {
		this.fee2 = fee2;
	}
	
	public Integer getFee3() {
		return fee3;
	}

	public void setFee3(Integer fee3) {
		this.fee3 = fee3;
	}
	
	public Integer getFee4() {
		return fee4;
	}

	public void setFee4(Integer fee4) {
		this.fee4 = fee4;
	}
	
	public Integer getFee5() {
		return fee5;
	}

	public void setFee5(Integer fee5) {
		this.fee5 = fee5;
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
	
	public Integer getWorkovertime() {
		return workovertime;
	}

	public void setWorkovertime(Integer workovertime) {
		this.workovertime = workovertime;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	
	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}
	
	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}
}
