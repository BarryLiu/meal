package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.db.ServiceConfig;
import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.service.UserService;

import javax.xml.ws.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zixiao.zhang on 2016/5/12.
 */
public class UserCache {
    private static UserCache instance;
    private UserCache(){}
    public static UserCache getInstance() {
        if(instance == null) {
            instance = new UserCache();
        }
        return instance;
    }

    private Map<String, User> caches = new HashMap<String, User>();
    private UserService userService = ServiceFactory.getService(ServiceConfig.SERVICE_USER);
    public User get(String number) {
        User user = null;
        if(!TextUtil.isNullOrEmpty(number)) {
            user = caches.get(number);
            if(user == null) {
                TUser tUser = userService.getByNumber(number);
                if(tUser != null) {
                    user = new User(tUser);
                    caches.put(number, user);
                }
            }
        }
        return user;
    }
    public void clear() {
        caches.clear();
    }
    public void update(User user) {
        if(user != null && user.getNumber() != null) {
            User preUser = caches.get(user.getNumber());
            if(preUser != null) {
                caches.remove(user.getNumber());
                caches.put(user.getNumber(), user);
            }
        }
    }
}
