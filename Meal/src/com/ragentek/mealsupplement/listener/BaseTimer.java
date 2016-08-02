package com.ragentek.mealsupplement.listener;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zixiao.zhang on 2016/5/10.
 */
public abstract class BaseTimer {
    protected static final Logger logger = Logger.getLogger(BaseTimer.class);
    private static final long PERIOD = 86400000l; //24*60*60*1000
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            try {
                handle();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.toString(), e);
            }
        }
    };

    public final void startTimer() {
        if(timer == null) {
            timer = new Timer();
            timer.schedule(timerTask, getDelayMillisecond(), PERIOD);
        }
    }

    public final void stopTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    /**
     * 获取当前时间距离最近的晚上11点的时间间隔
     * @return
     */
    protected long getDelayMillisecond() {
        long delay = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getTriggerHour());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long curTime = System.currentTimeMillis();
        if(calendar.getTimeInMillis() > curTime) {
            delay = calendar.getTimeInMillis() - curTime;
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            delay = calendar.getTimeInMillis() - curTime;
        }
        return delay;
    }
    protected abstract int getTriggerHour(); //获得触发时间，24小时小时制，子类重新该方法设置触发时间

    protected abstract void handle() throws Exception; //实际操作

}
