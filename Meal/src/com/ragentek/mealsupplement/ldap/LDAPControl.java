package com.ragentek.mealsupplement.ldap;

import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.User;
import org.apache.log4j.Logger;
import org.apache.poi.util.SystemOutLogger;

import java.util.*;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * Created by kui.li on 2014/9/2.
 */
public class LDAPControl {
    private static LDAPControl instance;
    private static final Logger logger = Logger.getLogger(LDAPControl.class);
    public static final String BASEUSERS = "OU=wheatek,DC=wheatek,DC=com";
    public static String URL = "ldap://192.168.1.4:389/";
    public static String BASEDN = "CN=Group,DC=wheatek,DC=com";//root
    private String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private LdapContext ctx = null;
    private Hashtable env = null;
    private Control[] connCtls = null;
    public static String userDN;

    //Ldap中冗余的 数据的loginAccount 号
    private static String [] noNunberNoViewLoginCount = new String []{
            "dccback","gongzhonghao","u8admin","dcc1","admin","apple","finance","hr","it","vip4","vip3","vip2","vip1","dcc","wheatek"
    };

    public static LDAPControl getInstance() {
        if(instance == null) {
            instance = new LDAPControl();
        }
        return instance;
    }

    private void connect() {
        env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
        env.put(Context.PROVIDER_URL, URL);//LDAP server
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "jiraadmin@wheatek.com");
        env.put(Context.SECURITY_CREDENTIALS, "J2ira@wtk");

        try {
            ctx = new InitialLdapContext(env, connCtls);
        } catch (AuthenticationException e) {
            System.out.println("Authentication faild: " + e.toString());
        } catch (Exception e) {
            System.out.println("Something wrong while authenticating: " + e.toString());
        }
    }

    /**
     * connect the LDAP server，该方法目前是有效的
     *
     * @param username
     * @return
     */
    private String getUserDN(String username) {
        String userDN = "";
        connect();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration en = ctx.search("DC=wheatek,DC=com", "sAMAccountName=" + username, constraints); //The UID you are going to query,* means all nodes
            if (en == null) {
                System.out.println("Have no NamingEnumeration.");
            }
            if (!en.hasMoreElements()) {
                System.out.println("Have no element.");
            } else {
                while (en != null && en.hasMoreElements()) {//maybe more than one element
                    Object obj = en.nextElement();
                    if (obj instanceof SearchResult) {
                        SearchResult si = (SearchResult) obj;
                        userDN += si.getNameInNamespace();
                    } else {
                        System.out.println(obj);
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in search():" + e);
        }
        return userDN;
    }

    /**
     * check the username * password
     *
     * @param userName
     * @param password
     * @return
     */
    public boolean authorCheck(String userName, String password) {
        boolean valide = false;
        userDN = getUserDN(userName);
        //System.out.println(userName+" "+password);
        /*if(userDN.split(",")[0].equals("CN=韦鹏飞") || userDN.split(",")[0].equals("CN=刘佳丽")||
        userDN.split(",")[0].equals("CN=蒋海焦")||userDN.split(",")[0].equals("CN=王晨")||userDN.split(",")[0].equals("CN=张子枭")){*/
            try {
                if (!"".equals(userDN)) {
                    ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
                    ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
                    ctx.reconnect(connCtls);

                    valide = true;
                }
            } catch (AuthenticationException e) {
                System.out.println(userDN + " is not authenticated");
                System.out.println(e.toString());
                valide = false;
            } catch (NamingException e) {
                System.out.println(userDN + " is not authenticated");
                valide = false;
            }
        /*}
        else{
            valide = false;
        }*/

        return valide;
    }

    /**
     * 获取所有用户，该方法目前只是用于测试
     * @return
     * @throws Exception
     */
    public void getAllUsersInfo() {
        String userDN = "";
        connect();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration en = ctx.search("DC=wheatek,DC=com", "objectClass=User",constraints); //The UID you are going to query,* means all nodes
            if (en == null) {
                System.out.println("Have no NamingEnumeration.");
            }
            if (!en.hasMoreElements()) {
                System.out.println("Have no element.");
            } else {
                while (en != null && en.hasMoreElements()) {//maybe more than one element
                    Object obj = en.nextElement();
                    if (obj instanceof SearchResult) {
                        SearchResult si = (SearchResult) obj;
                        userDN += si.getNameInNamespace();
                        System.out.println("======" + userDN);
                    } else {
                        System.out.println(obj);
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in search():" + e);
        }
    }

    /*
     , memberof=memberOf: CN=B HDC 产品项目部,OU=第一研发中心,OU=ODM业务ODBG,OU=锐嘉科群组,OU=锐嘉科集团,DC=wheatek,DC=com, CN=A HDC 第一研发中心,
    OU=第一研发中心,OU=ODM业务ODBG,OU=锐嘉科群组,OU=锐嘉科集团,DC=wheatek,DC=com, msexchmailboxguid=msExchMailboxGuid: }�X��ѾO�Q`�;��,
        instancetype=instanceType: 4, msexchpoliciesincluded=msExchPoliciesIncluded: dda2a76e-d7f2-4ebb-8daa-9c61bcdb12b6,
        {26491cfc-9e50-4857-861b-0cb8df22b5d7}, objectsid=objectSid:         �\C'�d�6��:  , badpasswordtime=badPasswordTime: 130573909144218750,
        proxyaddresses=proxyAddresses: smtp:yiting.fang@wheatek.cn, X400:C=CN;A= ;P=wheatek;O=Exchange;S=yiting.fang;, SMTP:yiting.fang@wheatekek.com,
        dscorepropagationdata=dSCorePropagationData: 16010101000000.0Z, objectclass=objectClass: top, person, organizationalPerson, user,
        msexchwhenmailboxcreated=msExchWhenMailboxCreated: 20140918035401.0Z, name=name: 方依婷, description=description: HDC PPM 产品项目部,
        sn=sn: 方, useraccountcontrol=userAccountControl: 512, primarygroupid=primaryGroupID: 513, lastlogon=lastLogon: 130577300889531250,
        accountexpires=accountExpires: 0, lastlogoff=lastLogoff: 0, usnchanged=uSNChanged: 28540056, physicaldeliveryofficename=physicalDeliveryOfficeName:
        HDC PPM 产品项目部, msexchrbacpolicylink=msExchRBACPolicyLink: CN=Default Role Assignment Policy,CN=Policies,CN=RBAC,CN=第一个组织,
        CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=wheatek,DC=com, cn=cn: 方依婷, textencodedoraddress=textEncodedORAddress:
        X400:C=CN;A= ;P=wheatek;O=Exchange;S=yiting.fang;, msexchversion=msExchVersion: 44220983382016, msexchtextmessagingstate=msExchTextMessagingState:
        302120705, 16842751, logoncount=logonCount: 72, msexchhomeservername=msExchHomeServerName: /o=wheatek/ou=Exchange Administrative Group
        (FYDIBOHF23SPDLT)/cn=Configuration/cn=Servers/cn=MAIL01, homemta=homeMTA: CN=Microsoft MTA,CN=MAIL01,CN=Servers,CN=Exchange Administrative Group (FYDIBOHF23SPDLT)
        ,CN=Administrative Groups,CN=第一个组织,CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=wheatek,DC=com, samaccounttype=sAMAccountType: 805306368,
        msexchrecipienttypedetails=msExchRecipientTypeDetails: 1, legacyexchangedn=legacyExchangeDN: /o=wheatek/ou=Exchange Administrative Group (FYDIBOHF23SPDLT)/cn=Recipients/cn
        =user1a3d6838, givenname=givenName: 依婷, usncreated=uSNCreated: 27989985, displayname=displayName: Yiting Fang(方依婷), userprincipalname=userPrincipalName: yiting.fang@rage
nt.cn, pwdlastset=pwdLastSet: 130558365273281250, whenchanged=whenChanged: 20141011051925.0Z, lastlogontimestamp=lastLogonTimestamp: 130574783501594162, countrycode=countryCode: 0,
        mailnickname=mailNickname: yiting.fang, distinguishedname=distinguishedName: CN=方依婷,OU=产品项目部,OU=第一研发中心,OU=ODM业务ODBG,OU=锐嘉科集团,DC=wheatek,DC=com, msexchrecipientdisplaytype
        =msExchRecipientDisplayType: 1073741824, homemdb=homeMDB: CN=db2010,CN=Databases,CN=Exchange Administrative Group (FYDIBOHF23SPDLT),CN=Administrative Groups,CN=第一个组织,
        CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=wheatek,DC=com, msexchumdtmfmap=msExchUMDtmfMap: emailAddress:9484643264, lastNameFirstName:, firstNameLastName:
        showinaddressbook=showInAddressBook: CN=所有用户,CN=All Address Lists,CN=Address Lists Container,CN=第一个组织,CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=wheatek,DC=com,
        CN=默认全局地址列表,CN=All Global Address Lists,CN=Address Lists Container,CN=第一个组织,CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=wheatek,DC=com, CN=wheatekeknew,CN=All
        Address Lists,CN=Address Lists Container,CN=第一个组织,CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=wheatek,DC=com, logonhours=logonHours: �������������������
        ��, samaccountname=sAMAccountName: yiting.fang}
        {objectcategory=objectCategory: CN=Person,CN=Schema,CN=Configuration,DC=wheatek,DC=com, whencreated=whenCreated: 20140922093801.0Z, badpwdcount=badPwdCount: 0,
        mdbusedefaults=mDBUseDefaults: TRUE, codepage=codePage: 0, scriptpath=scriptPath: BU2SW.bat, mail=mail: cuixia.liu@wheatekek.com, objectguid=objectGUID: �86vQO�^� �
        , msexchuseraccountcontrol=msExchUserAccountControl: 0, msexchmailboxsecuritydescriptor=msExchMailboxSecurityDescriptor:  �           ,        
                */

    public Map<String,String> getUsers(){
        Map <String,String>map = new HashMap<String,String>();
        connect();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration en = ctx.search("DC=wheatek,DC=com","objectClass=User", constraints); //The UID you are going to query,* means all nodes
            if (en == null) {
                System.out.println("Have no NamingEnumeration.");
            }
            if (!en.hasMoreElements()) {
                System.out.println("Have no element.");
            } else {
                while (en != null && en.hasMoreElements()) {//maybe more than one element
                    Object obj=en.next();
                    while (obj!=null) {
                        if (obj instanceof SearchResult) {
                            SearchResult si = (SearchResult) obj;
                           // userInfos.add(si.getAttributes().get("name").toString());
                            map.put(si.getAttributes().get("samaccountname").get().toString(),
                                    si.getAttributes().get("name").get().toString() + ":" + si.getAttributes().get("description").get().toString());
                           //System.out.println(si.getAttributes().get("description").get().toString());
                            obj=en.next();
                        } else {
                            System.out.println(obj);
                            obj=en.next();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in search():" + e);
        }
        return map;
    }

    /**
     * 所有的有number的用户
     * @return
     */
    public List<TUser> getAllLadpUser(){
        return getAllLadpUser(true);//默认查询有工号的用户
    }
    /**
     * 得到所有的用户
     * @param flag  true: 表示所有有 工号  number的 ，false 表示 包括没有工号的用户
     * @return
     */
    public List<TUser> getAllLadpUser(boolean flag) {
        List<TUser> users = new ArrayList<TUser>();
        connect();
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] returningAttrs = null;
            if(flag){
                returningAttrs = new String[] {
                        "sAMAccountName", "name", "memberof", "description", "displayName","mail"
                };
            }else{
                returningAttrs = new String[] {
                        "sAMAccountName", "name", "memberof",  "displayName","mail"
                };
            }

            controls.setReturningAttributes(returningAttrs);
            NamingEnumeration<SearchResult> en = ctx.search(BASEUSERS, "objectClass=user", controls);
            if(en == null) {
                System.out.println("Have no NamingEnumeration.");
                logger.info("Have no NamingEnumeration.");
            }
            if(!en.hasMoreElements()) {
                System.out.println("Have no element.");
                logger.info("Have no element.");
            } else {
                while (en.hasMoreElements()) {
                    SearchResult sr = en.nextElement();
                    try {
                        TUser tUser = new TUser();
                        String username = sr.getAttributes().get("sAMAccountName").get().toString(); //账号：zixiao.zhang
                        String displayName = sr.getAttributes().get("displayName").get().toString(); //显示名称：Zixiao Zhang(张子枭)
                        String name = sr.getAttributes().get("name").get().toString(); //姓名：张子枭

                        String email = sr.getAttributes().get("mail").get().toString(); //邮箱号码：zixiao.zhang@wheatek.com
                        String groupNames = ""; //部门，可能有多个部门，多个部门间用逗号分隔：A AA 麦穗科技全员,B HDC 软件部,A HDC 研发中心
                        Attribute memberof = sr.getAttributes().get("memberof");
                        if(memberof != null) {
                            for(int i=0;i<memberof.size();i++) {
                                groupNames += (memberof.get(i).toString().split(","))[0].split("=")[1]+",";
                            }
                        }
                        if(groupNames.length() > 0) {
                            groupNames = groupNames.substring(0, groupNames.length()-1);
                        }

                        if(flag){  //在要查有工号的进去，没工号的不进去
                            String number = sr.getAttributes().get("description").get().toString(); //工号：100123
                            tUser.setNumber(number);
                        }

                        tUser.setLoginCount(username);
                        tUser.setLoginPwd("111111"); //该字段设计暂时没有意义

                        tUser.setStat(User.STATUS_NORMAL);
                        tUser.setDepart(groupNames);
                        tUser.setName(name);
                        tUser.setDisplayName(displayName);
                        tUser.setEmail(email);
                        users.add(tUser);
                    } catch (Exception e) {
                        logger.info("Ignore error:"+e.toString());
                    }
                }
            }
        } catch(Exception e) {
            logger.error(e.toString(), e);
        }
        return users;
    }

    /**
     * 所有的用户
     * @return
     */
    public Map<String,TUser> getLdapUsers(){

        return null ;
    }
    /**
     * 得到Ldap中的所有的没有 number 的用户
     * @return
     */
    public  Map<String,TUser> getAllLdapUserNoNumber(){
        List<TUser> hasNumberUser =   getInstance().getAllLadpUser(true); //所有 有number 的员工     number 有值

        List<TUser> allNumberUser = getInstance().getAllLadpUser(false); //所有 的员工      包括有number 的 ， 但容器中没有值

       List<TUser> noNumberUser= new ArrayList<TUser>();
        Map<String,TUser> mapNoNumberUser = new HashMap<String, TUser>();
       for(TUser tUser : allNumberUser){                            //所有保存在 map容器中  loginCount键
           mapNoNumberUser.put(tUser.getLoginCount(),tUser);
       }
        for(TUser tUser : hasNumberUser){                           //根据键移除有的用户   // 20160824有一个员工在ldap中有 工号了添加新员工没有他的信息  怕放开了会将ldap 中有工号已经离职的人也查出来,放开了发现并没有,也许近期离职的都被ldap清除了吧
            mapNoNumberUser.remove(tUser.getLoginCount());
        }
        for(String key : mapNoNumberUser.keySet()){                             //剩下的保存到没有用户的容器中
            noNumberUser.add(mapNoNumberUser.get(key));
        }

        removeImpurty(mapNoNumberUser);
        //  return noNumberUser;   //map
        return  mapNoNumberUser;
    }


    public List<TUser> removeImpurty(Map<String, TUser> users) {
        //1 移除多余的用户
        for(String loginCount : noNunberNoViewLoginCount){
            if(loginCount !=null && !"".equals(loginCount)){
                users.remove(loginCount);
            }
        }
        // 2.得到所有的没有number 的用户
        List<TUser> tUsers = new ArrayList<TUser>();
        for(String key :users.keySet()){
            tUsers.add(users.get(key));
        }

        return tUsers;
    }

    public TUser getLadpUser(String loginCount) {
        TUser tUser = null;
        connect();
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] returningAttrs = new String[] {
                    "sAMAccountName", "name", "memberof", "description", "displayName","mail"
            };
            controls.setReturningAttributes(returningAttrs);
            NamingEnumeration<SearchResult> en = ctx.search(BASEUSERS, "sAMAccountName="+loginCount, controls);
            if(en == null) {
                System.out.println("Have no NamingEnumeration.");
                logger.info("Have no NamingEnumeration.");
            }
            if(!en.hasMoreElements()) {
                System.out.println("Have no element.");
                logger.info("Have no element.");
            } else {
                SearchResult sr = en.nextElement();
                try {
                    tUser = new TUser();
                    String username = sr.getAttributes().get("sAMAccountName").get().toString(); //账号：zixiao.zhang
                    String displayName = sr.getAttributes().get("displayName").get().toString(); //显示名称：Zixiao Zhang(张子枭)
                    String name = sr.getAttributes().get("name").get().toString(); //姓名：张子枭
                    String number = sr.getAttributes().get("description").get().toString(); //工号：100123
                    String email = sr.getAttributes().get("mail").get().toString(); //邮箱号码：zixiao.zhang@wheatek.com
                    String groupNames = ""; //部门，可能有多个部门，多个部门间用逗号分隔：A AA 麦穗科技全员,B HDC 软件部,A HDC 研发中心
                    Attribute memberof = sr.getAttributes().get("memberof");
                    if(memberof != null) {
                        for(int i=0;i<memberof.size();i++) {
                            System.out.println(memberof.get(i).toString());
                            groupNames += (memberof.get(i).toString().split(","))[0].split("=")[1]+",";
                        }
                    }
                    if(groupNames.length() > 0) {
                        groupNames = groupNames.substring(0, groupNames.length()-1);
                    }
                    tUser.setLoginCount(username);
                    tUser.setLoginPwd("111111"); //该字段设计暂时没有意义
                    tUser.setNumber(number);
                    tUser.setStat(User.STATUS_NORMAL);
                    tUser.setDepart(groupNames);
                    tUser.setName(name);
                    tUser.setDisplayName(displayName);
                    tUser.setEmail(email);
                } catch (Exception e) {
                    logger.info("Ignore error:"+e.toString());
                }
            }
        } catch(Exception e) {
            logger.error(e.toString(), e);
        }
        return tUser;
    }



    public static void main(String[] args) {
//        boolean b = new LDAPControl().authorCheck("scm", "G5D#jdd73@#5");
//        new LDAPControl().getAllUsersInfo();//.authorCheck("yu.han", "HANyu!@#123");
//
//        new LDAPControl().getUsers();
//
//        System.out.println("userDN===" + userDN);
//        if (b) System.out.println("(验证成功！)");
//        else System.out.println("(用户名或密码错误！)");

        Map<String,String> map = LDAPControl.getInstance().getUsers();

      /*  for (String key :map.keySet()) {
            System.out.println("key:  "+key + "  data: " + map.get(key));
        }*/

        System.out.println("getusers:");


        List<TUser> users = LDAPControl.getInstance().getAllLadpUser();

       // users = LDAPControl.getInstance().getAllLadpUser(false);
       /* for(int i=0;i<users.size();i++){
            System.out.println(users.get(i));
        }*/
        System.out.println("users:"+users.size());
        List<TUser> usersall  = LDAPControl.getInstance().getAllLadpUser(false);
        System.out.println("usersall: "+usersall.size());

        System.out.println(users.get(5));
        System.out.println("===========");

        System.out.println("hao.wen"+getInstance().getLadpUser("hao.wen"));
       //List<TUser> noNumberUser = getInstance().getAllLdapUserNoNumber();

        for(int i= 0 ; i<users.size(); i++){
            System.out.println(users.get(i));

        }

       /* Map<String,TUser> mUsers = getInstance().getAllLdapUserNoNumber(); // 得到所有没number的数据
         for (String key : mUsers.keySet()){
            TUser u = mUsers.get(key);
            System.out.println(u);
        }*/
  /*  System.out.println("mU"+mUsers.size());
        List<TUser> us = getInstance().removeImpurty(mUsers);
        for (TUser u:us) {
            System.out.println(u);
        }
        System.out.println("us:"+us.size());
*/
    //    LDAPControl.getInstance().getAllUsersInfo();
    }
	
}
