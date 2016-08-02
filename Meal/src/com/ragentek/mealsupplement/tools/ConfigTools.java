package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.ldap.LDAPControl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigTools {

	public ConfigTools() {
		// TODO Auto-generated constructor stub
	}

	public void initConfig(){
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ApplicationConfig.properties");
		Properties p = new Properties();
		try {
			p.load(inStream);
			DBUtils.DRIVER_CLASS = p.getProperty("DB_DRIVER_CLASS");
			DBUtils.URL = p.getProperty("DB_URL");
			DBUtils.USER = p.getProperty("DB_USER");
			DBUtils.PASSWORD = p.getProperty("DB_PASSWORD");

            LDAPControl.URL=p.getProperty("LDAP_SERVER_ADDR");
            LDAPControl.BASEDN="CN=Group,DC=ragent,DC=cn";
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
