package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TUser implements SqlBean {
@Override
public String getTableName() {
	return "t_user";
}
@Override
public String getPrimaryKeyColumnName() {
	return "id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"id",
"login_count",
"login_pwd",
"name",
"depart",
"last_login_time",
"number",
"stat",
"display_name",
"user_type",
"enter_date",
"email",
"leave_date",
"dept1",
"dept_name1",
"dept2",
"dept_name2"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("id=");	
sb.append(id);	
sb.append(",loginCount=");	
sb.append(loginCount);	
sb.append(",loginPwd=");	
sb.append(loginPwd);	
sb.append(",name=");	
sb.append(name);	
sb.append(",depart=");	
sb.append(depart);	
sb.append(",lastLoginTime=");	
sb.append(lastLoginTime);	
sb.append(",number=");	
sb.append(number);	
sb.append(",stat=");	
sb.append(stat);	
sb.append(",displayName=");	
sb.append(displayName);	
sb.append(",userType=");	
sb.append(userType);	
sb.append(",enterDate=");	
sb.append(enterDate);	
sb.append(",email=");	
sb.append(email);	
sb.append(",leaveDate=");	
sb.append(leaveDate);	
sb.append(",dept1=");	
sb.append(dept1);	
sb.append(",deptName1=");	
sb.append(deptName1);	
sb.append(",dept2=");	
sb.append(dept2);	
sb.append(",deptName2=");	
sb.append(deptName2);
	return sb.toString();
}
	private Long id;
	private String loginCount;
	private String loginPwd;
	private String name;
	private String depart;
	private java.sql.Timestamp lastLoginTime;
	private String number;
	private Integer stat;
	private String displayName;
	private Integer userType;
	private String enterDate;
	private String email;
	private String leaveDate;
	private Long dept1;
	private String deptName1;
	private Long dept2;
	private String deptName2;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(String loginCount) {
		this.loginCount = loginCount;
	}
	
	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
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
	
	public java.sql.Timestamp getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(java.sql.Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	
	public String getEnterDate() {
		return enterDate;
	}

	public void setEnterDate(String enterDate) {
		this.enterDate = enterDate;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}
	
	public Long getDept1() {
		return dept1;
	}

	public void setDept1(Long dept1) {
		this.dept1 = dept1;
	}
	
	public String getDeptName1() {
		return deptName1;
	}

	public void setDeptName1(String deptName1) {
		this.deptName1 = deptName1;
	}
	
	public Long getDept2() {
		return dept2;
	}

	public void setDept2(Long dept2) {
		this.dept2 = dept2;
	}
	
	public String getDeptName2() {
		return deptName2;
	}

	public void setDeptName2(String deptName2) {
		this.deptName2 = deptName2;
	}
}
