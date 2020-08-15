package com.example.mystep.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class CookieBean extends LitePalSupport {
    private String mac;
    private String cookie;
    @Column(unique = true)
    private String userid;
    private String phone;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
