package com.example.mystep.model;

import android.os.Looper;

import com.example.mystep.bean.ConfigBean;
import com.example.mystep.bean.CookieBean;
import com.example.mystep.util.XToastUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static com.example.mystep.model.InternetModel.getConfig;

public class DatabaseModel {
    public static List findAll(){
        List<CookieBean> contextSqliteBeans  = LitePal.findAll(CookieBean.class);
        return contextSqliteBeans;
    }

    public static void deleteAll(String userid){
        LitePal.deleteAll(CookieBean.class, "userid = ?" , userid);

    }
    public static void insertText(String phone, String userid,String token,String uuid){
        CookieBean contextSqliteBean = new CookieBean();
        contextSqliteBean.setPhone(phone);
        contextSqliteBean.setUserid(userid);
        contextSqliteBean.setCookie(token);
        contextSqliteBean.setMac(uuid);
        if (contextSqliteBean.save()){
            ConfigBean configBean = new ConfigBean();
            configBean.setPhone(phone);
            configBean.setUsername(userid);
            configBean.setWx(token);
            configBean.setMac(uuid);
            getConfig(configBean);
            Looper.prepare();
            XToastUtils.success("保存成功,请返回首页使用cookie");
            Looper.loop();

        }else {
            Looper.prepare();
            XToastUtils.error("保存失败或cookie已存在，请删除后重试");
            Looper.loop();
        }
    }

}
