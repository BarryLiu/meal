package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TGroupPermission implements SqlBean {
@Override
public String getTableName() {
	return "t_group_permission";
}
@Override
public String getPrimaryKeyColumnName() {
	return "_id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"_id",
"groupid",
"permissionid"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("Id=");	
sb.append(Id);	
sb.append(",groupid=");	
sb.append(groupid);	
sb.append(",permissionid=");	
sb.append(permissionid);
	return sb.toString();
}
	private Long Id;
	private Long groupid;
	private Long permissionid;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}
	
	public Long getGroupid() {
		return groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	
	public Long getPermissionid() {
		return permissionid;
	}

	public void setPermissionid(Long permissionid) {
		this.permissionid = permissionid;
	}
}
