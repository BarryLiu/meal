package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TUserPermission implements SqlBean {
@Override
public String getTableName() {
	return "t_user_permission";
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
"permissionid"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("Id=");	
sb.append(Id);	
sb.append(",userid=");	
sb.append(userid);	
sb.append(",permissionid=");	
sb.append(permissionid);
	return sb.toString();
}
	private Long Id;
	private Long userid;
	private Long permissionid;
	
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
	
	public Long getPermissionid() {
		return permissionid;
	}

	public void setPermissionid(Long permissionid) {
		this.permissionid = permissionid;
	}
}
