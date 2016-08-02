package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TKaoqin implements SqlBean {
@Override
public String getTableName() {
	return "t_kaoqin";
}
@Override
public String getPrimaryKeyColumnName() {
	return "id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"id",
"nos",
"cardno",
"userid",
"names",
"depart",
"dotimes",
"addresss",
"status",
"descs"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("id=");	
sb.append(id);	
sb.append(",nos=");	
sb.append(nos);	
sb.append(",cardno=");	
sb.append(cardno);	
sb.append(",userid=");	
sb.append(userid);	
sb.append(",names=");	
sb.append(names);	
sb.append(",depart=");	
sb.append(depart);	
sb.append(",dotimes=");	
sb.append(dotimes);	
sb.append(",addresss=");	
sb.append(addresss);	
sb.append(",status=");	
sb.append(status);	
sb.append(",descs=");	
sb.append(descs);
	return sb.toString();
}
	private Long id;
	private String nos;
	private String cardno;
	private String userid;
	private String names;
	private String depart;
	private String dotimes;
	private String addresss;
	private String status;
	private String descs;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNos() {
		return nos;
	}

	public void setNos(String nos) {
		this.nos = nos;
	}
	
	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	
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
	
	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}
	
	public String getDotimes() {
		return dotimes;
	}

	public void setDotimes(String dotimes) {
		this.dotimes = dotimes;
	}
	
	public String getAddresss() {
		return addresss;
	}

	public void setAddresss(String addresss) {
		this.addresss = addresss;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
}
