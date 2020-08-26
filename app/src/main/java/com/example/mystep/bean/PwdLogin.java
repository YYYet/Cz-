package com.example.mystep.bean;

public class PwdLogin {
    String password;
    String clientId ="8e844e28db7245eb81823132464835eb";
    Integer appType = 6;
    String loginName;
    Integer roleType = 0;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
