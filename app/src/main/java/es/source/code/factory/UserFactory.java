package es.source.code.factory;

import es.source.code.model.User;

public abstract class UserFactory {
    //创建User设置属性
    public static User createuser(String username, String password, Boolean olduser){
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setOldUser(olduser);
        return user;
    }
}
