package com.example.mystep.callback;

import com.example.mystep.bean.UserInfo;

public interface mLoginCallback {
    void onLoginSuccess(UserInfo userInfo);
    void onLoginFailed(String msg);
}
