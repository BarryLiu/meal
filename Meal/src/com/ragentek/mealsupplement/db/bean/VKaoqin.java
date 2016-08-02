package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class VKaoqin implements SqlBean {
@Override
public String getTableName() {
	return "v_kaoqin";
}
@Override
public String getPrimaryKeyColumnName() {
	return "userid";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"userid",
"names",
"day",
"shangban",
"xiaban"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("userid=");	
sb.append(userid);	
sb.append(",names=");	
sb.append(names);	
sb.append(",day=");	
sb.append(day);	
sb.append(",shangban=");	
sb.append(shangban);	
sb.append(",xiaban=");	
sb.append(xiaban);
	return sb.toString();
}
	private String userid;
	private String names;
	private String day;
	private String shangban;
	private String xiaban;
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}
	
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public String getShangban() {
		return shangban;
	}

	public void setShangban(String shangban) {
		this.shangban = shangban;
	}
	
	public String getXiaban() {
		return xiaban;
	}

	public void setXiaban(String xiaban) {
		this.xiaban = xiaban;
	}
}
