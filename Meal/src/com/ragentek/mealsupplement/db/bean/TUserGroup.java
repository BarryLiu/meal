package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TUserGroup implements SqlBean {
@Override
public String getTableName() {
	return "t_user_group";
}
@Override
public String getPrimaryKeyColumnName() {
	return "_id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"_id",
"userid",
"groupid"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("Id=");	
sb.append(Id);	
sb.append(",userid=");	
sb.append(userid);	
sb.append(",groupid=");	
sb.append(groupid);
	return sb.toString();
}
	private Long Id;
	private Long userid;
	private Long groupid;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}
	
	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}
	
	public Long getGroupid() {
		return groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
}
