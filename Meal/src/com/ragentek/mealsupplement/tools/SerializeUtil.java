package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.listener.ConfigListener;

import java.io.*;

/**
 * Created by zixiao.zhang on 2016/4/20.
 */
public class SerializeUtil {
    public static final String DIR = "serialize";
    public static File getSerializeRootFile() {
        String fileRootDir = ConfigListener.getFileRootDir();
        if(fileRootDir == null) {
            fileRootDir = "D:\\meal\\";
        }
        File root = new File(fileRootDir, DIR);
        if(!root.exists()) {
            root.mkdirs();
        }
        return root;
    }

    public final synchronized static boolean serialize(Serializable bean) {
        boolean res = false;
        if(bean != null) {
            String className = bean.getClass().getName();
            ObjectOutputStream oo = null;
            try {
                oo = new ObjectOutputStream(new FileOutputStream(new File(getSerializeRootFile(), className)));
                oo.writeObject(bean);
                res = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(oo != null) {
                    try {
                        oo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return res;
    }

    public final synchronized static <T extends Serializable> T deserialize(Class<T> tClass) {
        T t = null;
        String className = tClass.getName();
        File f = new File(getSerializeRootFile(), className);
        if(f.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(f));
                t = (T) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public static boolean setStartDay(String startDay) {
        ConfigBean bean = deserialize(ConfigBean.class);
        if(bean == null) {
            bean = new ConfigBean();
        }
        bean.setStartDay(startDay);
        return serialize(bean);
    }

    public static String  getStartDay() {
        String startDay = null;
        ConfigBean bean = deserialize(ConfigBean.class);
        if(bean != null) {
            startDay = bean.getStartDay();
        }
        if(startDay == null) {
            startDay = ConfigListener.getStartDay();
        }
        if(startDay == null) {
            startDay = "2016/01/01";
        }
        return startDay;
    }

    public static boolean setMailPwd(String mailPwd) {
        ConfigBean bean = deserialize(ConfigBean.class);
        if(bean == null) {
            bean = new ConfigBean();
        }
        bean.setMailPwd(mailPwd);
        return serialize(bean);
    }

    public static String getMailPwd() {
        String mailPwd = null;
        ConfigBean bean = deserialize(ConfigBean.class);
        if(bean != null) {
            mailPwd = bean.getMailPwd();
        }
        return mailPwd;
    }

    public static void main(String[] args) {
        ConfigBean configBean = new ConfigBean();
        configBean.setStartDay("2016/10/27");
        serialize(configBean);
        ConfigBean bean = deserialize(ConfigBean.class);
        System.out.println(bean);
        if(bean != null) {
            System.out.print(bean.getStartDay());
        }
    }
}
