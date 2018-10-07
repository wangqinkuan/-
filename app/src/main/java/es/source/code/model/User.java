package es.source.code.model;

import java.io.Serializable;
import java.util.regex.Pattern;

public class User implements Serializable{
    String Username;
    String Password;
    //新用户要注册
    Boolean OldUser;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Boolean getOldUser() {
        return OldUser;
    }

    public void setOldUser(Boolean oldUser) {
        OldUser = oldUser;
    }
}
