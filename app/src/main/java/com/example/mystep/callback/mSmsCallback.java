package com.example.mystep.callback;

import com.example.mystep.bean.Message;

public interface mSmsCallback {
   void onVerificationCodeSuccess(Message message);
   void onFailed();
}
