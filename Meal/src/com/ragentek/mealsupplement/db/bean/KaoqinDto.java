package com.ragentek.mealsupplement.db.bean;

import com.ragentek.mealsupplement.json.Fees;

import java.util.List;

/**
 * Created by yingjing.liu on 2016/5/24.
 * 用于在导出考勤信息中的承载数据的对象 汇总
 */
public class KaoqinDto {
    //姓名
    private String name;
    //工号
    private String number ;

    private Integer shidao; //实到
    private Integer ztcd;  //迟到、早退
    private Integer weidaka; //未打卡
    private Integer gongchu ; //公出
    private Integer chucai; //出差

    private Integer shijia ; //事假
    private Integer bingjia;//病假
    private Integer nianjia ;//年假
    private Integer qitajia; //其他假
    private Integer jiaban;    //加班
    private Integer tiaoxiuz; //调休(正常)
    private Integer tiaoxiuf;  //调休(非正常)
    private Integer jbjieyu;    //加班结余

    private Integer zao;        // 早餐补贴
    private Integer zhong;      //中餐补贴
    private Integer wan;       //晚餐补贴
    private Integer xiao;       //夜宵补贴

    private Integer yingdao;//
    //下面的字段是用来携带 详情明细中的数据的
    private String startTime; //打卡开始时间
    private String endTime;     //打卡结束时间
    private String dayStr;  //打表日期
    private List<KaoqinDto> dtos; //携带考勤明细中的数据
    private String status;


    // 20160708 yingjing.liu  add  start 添加其他的几个假期
    private String hunjia;      //婚假
    private String sangjia;     //丧假
    private String chanjia;     //产假（陪产假）
    private String jiaotong;    //交通故障

    private Integer stat;   //
    public static final int STATUS_NORMAL = 0; //正常，参与考勤
    public static final int STATUS_NO_ATTENDANCE = 1; //不参与考勤
    public static final int STATUS_LEAVE = 2; //离职
    public static final int STATUS_MANAGER = 3; //高管

    // 20160708 yingjing.liu  add   end 添加其他的几个假期


    public KaoqinDto() {
    }

    public KaoqinDto(String name, String number, Integer shidao, Integer ztcd, Integer weidaka, Integer gongchu, Integer chucai, Integer shijia, Integer bingjia, Integer nianjia, Integer qitajia, Integer jiaban, Integer tiaoxiuz, Integer tiaoxiuf, Integer jbjieyu, Integer zao, Integer zhong, Integer wan, Integer xiao,Integer yingdao) {
        this.name = name;
        this.number = number;
        this.shidao = shidao;
        this.ztcd = ztcd;
        this.weidaka = weidaka;
        this.gongchu = gongchu;
        this.chucai = chucai;
        this.shijia = shijia;
        this.bingjia = bingjia;
        this.nianjia = nianjia;
        this.qitajia = qitajia;
        this.jiaban = jiaban;
        this.tiaoxiuz = tiaoxiuz;
        this.tiaoxiuf = tiaoxiuf;
        this.jbjieyu = jbjieyu;
        this.zao = zao;
        this.zhong = zhong;
        this.wan = wan;
        this.xiao = xiao;
        this.yingdao=yingdao;


    }



    public Integer getYingdao() {
        return yingdao;
    }

    public void setYingdao(Integer yingdao) {
        this.yingdao = yingdao;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "KaoqinDto{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", shidao=" + shidao +
                ", ztcd=" + ztcd +
                ", weidaka=" + weidaka +
                ", gongchu=" + gongchu +
                ", chucai=" + chucai +
                ", shijia=" + shijia +
                ", bingjia=" + bingjia +
                ", nianjia=" + nianjia +
                ", qitajia=" + qitajia +
                ", jiaban=" + jiaban +
                ", tiaoxiuz=" + tiaoxiuz +
                ", tiaoxiuf=" + tiaoxiuf +
                ", jbjieyu=" + jbjieyu +
                ", zao=" + zao +
                ", zhong=" + zhong +
                ", wan=" + wan +
                ", xiao=" + xiao +
                ", yingdao=" + yingdao +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", dayStr='" + dayStr + '\'' +
                ", dtos=" + dtos +
                ", status='" + status + '\'' +
                ", hunjia='" + hunjia + '\'' +
                ", sangjia='" + sangjia + '\'' +
                ", chanjia='" + chanjia + '\'' +
                ", jiaotong='" + jiaotong + '\'' +
                '}';
    }

    public void setJbjieyu(Integer jbjieyu) {
        this.jbjieyu = jbjieyu;
    }

    public String getHunjia() {
        return hunjia;
    }

    public Integer getStat() {
        return stat;
    }

    public void setStat(Integer stat) {
        this.stat = stat;
    }

    public void setHunjia(String hunjia) {
        this.hunjia = hunjia;
    }

    public String getSangjia() {
        return sangjia;
    }

    public void setSangjia(String sangjia) {
        this.sangjia = sangjia;
    }

    public String getChanjia() {
        return chanjia;
    }

    public void setChanjia(String chanjia) {
        this.chanjia = chanjia;
    }

    public String getJiaotong() {
        return jiaotong;
    }

    public void setJiaotong(String jiaotong) {
        this.jiaotong = jiaotong;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
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

    public List<KaoqinDto> getDtos() {
        return dtos;
    }

    public void setDtos(List<KaoqinDto> dtos) {
        this.dtos = dtos;
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

    public Integer getShidao() {
        return shidao;
    }

    public void setShidao(Integer shidao) {
        this.shidao = shidao;
    }

    public Integer getZtcd() {
        if(tiaoxiuf!=null&&tiaoxiuf!=0)
            ztcd+=tiaoxiuf;
        return ztcd;
    }

    public void setZtcd(Integer ztcd) {
        this.ztcd = ztcd;
    }

    public Integer getWeidaka() {
        return weidaka;
    }

    public void setWeidaka(Integer weidaka) {
        this.weidaka = weidaka;
    }

    public Integer getGongchu() {
        return gongchu;
    }

    public void setGongchu(Integer gongchu) {
        this.gongchu = gongchu;
    }

    public Integer getChucai() {
        return chucai;
    }

    public void setChucai(Integer chucai) {
        this.chucai = chucai;
    }

    public Integer getShijia() {
        if(computeJbjieyu() < 0) {
            shijia -= computeJbjieyu();
        }
        return shijia;
    }

    public void setShijia(Integer shijia) {
        this.shijia = shijia;
    }

    public Integer getBingjia() {
        return bingjia;
    }

    public void setBingjia(Integer bingjia) {
        this.bingjia = bingjia;
    }

    public Integer getNianjia() {
        return nianjia;
    }

    public void setNianjia(Integer nianjia) {
        this.nianjia = nianjia;
    }

    public Integer getQitajia() {
        return qitajia;
    }

    public void setQitajia(Integer qitajia) {
        this.qitajia = qitajia;
    }

    public Integer getJiaban() {
        return jiaban;
    }

    public void setJiaban(Integer jiaban) {
        this.jiaban = jiaban;
    }

    public Integer getTiaoxiuz() {
        return tiaoxiuz;
    }

    public void setTiaoxiuz(Integer tiaoxiuz) {
        this.tiaoxiuz = tiaoxiuz;
    }

    public Integer getTiaoxiuf() {
        return tiaoxiuf;
    }

    public void setTiaoxiuf(Integer tiaoxiuf) {
        this.tiaoxiuf = tiaoxiuf;
    }

    public Integer getJbjieyu() {
        return computeJbjieyu()<0?0:computeJbjieyu(); //modify 1110
    }

    private Integer computeJbjieyu() { //1110   加班结余   加班时间 - 调休正常-调休非正常
        //计算出加班结余
        Integer tiaoxiuHour= tiaoxiuz+tiaoxiuf; // 调休的总时间    bill_type 中值为0的
        // 加班时间为分钟 要转小时
        Integer jiabanHour =  jiaban /60;
        Integer jbJieyu = jiabanHour - tiaoxiuHour;
        return jbJieyu;
    }

    public Integer getZao() {
        return zao;
    }

    public void setZao(Integer zao) {
        this.zao = zao;
    }

    public Integer getZhong() {
        return zhong;
    }

    public void setZhong(Integer zhong) {
        this.zhong = zhong;
    }

    public Integer getWan() {
        return wan;
    }

    public void setWan(Integer wan) {
        this.wan = wan;
    }

    public Integer getXiao() {
        return xiao;
    }

    public void setXiao(Integer xiao) {
        this.xiao = xiao;
    }
}
